package net.ssehub.mutator.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.ssehub.mutator.BaseConfiguration;
import net.ssehub.mutator.mutation.IMutant;

public class LinuxEvaluator extends Evaluator {
    
    private BaseConfiguration config;
    
    public LinuxEvaluator(BaseConfiguration config) {
        this.config = config;
    }
    
    @Override
    public TestResult test(IMutant mutant) {
        TestResult result;
        
        try {
            mutant.write(config.getDropin());
            boolean compilationSuccess = compile(config.getTestSrc(), getExe(config.getTestSrc()));
            if (compilationSuccess) {
                String stdout = run(getExe(config.getTestSrc()));
                
                switch (stdout) {
                case "1":
                    result = TestResult.PASS;
                    break;
                case "timeout":
                    result = TestResult.TIMEOUT;
                    break;
                default:
                    result = TestResult.TEST_FAILED;
                    break;
                }
                
            } else {
                result = TestResult.COMPILATION_FAILED;
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            result = TestResult.ERROR;
        }
        
        return result;
    }

    @Override
    public double measureFitness(IMutant mutant) {
        double fitness = RUNTIME_ERROR;
        
        try {
            mutant.write(config.getDropin());
            boolean compilationSuccess = compile(config.getFitnessSrc(), getExe(config.getFitnessSrc()));
            if (compilationSuccess) {
                
                double[] measures = new double[config.getFitnessMeasures()];
                for (int i = 0; i < measures.length; i++) {
                    // sleep a bit so that we can cool down before performance measures take place
                    try {
                        Thread.sleep(config.getSleepBeforeFitness());
                    } catch (InterruptedException e) {
                    }
                    
                    String stdout = run(getExe(config.getFitnessSrc()));
                    measures[i] = Double.parseDouble(stdout);
                }
                Arrays.sort(measures);
                
                if (measures.length % 2 == 1) {
                    fitness = measures[measures.length / 2];
                } else {
                    fitness = (measures[measures.length / 2] + measures[measures.length / 2 + 1]) / 2;
                }
                
            }
            
        } catch (NumberFormatException e) {
            // ignore
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return fitness;
    }
    
    private boolean compile(File src, File target) throws IOException {
        List<String> command = new LinkedList<>();
        command.addAll(config.getCompilerArgs());
        command.add("-o");
        command.add(target.getPath());
        command.add(src.getPath());
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        pb.redirectOutput(new File("/dev/null"));
        
        Process p = pb.start();
        
        int ret = -1;
        try {
            ret = p.waitFor();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
        
        return ret == 0;
    }
    
    private String run(File exe) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(exe.getPath());
        pb.redirectErrorStream(true);

        Process p = pb.start();
        
        class ResultHolder {
            private String result = "";
        }
        ResultHolder holder = new ResultHolder();
        
        Thread reader = new Thread(() -> {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    holder.result = line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        reader.start();
        
        
        int ret = -1;
        boolean timeout = false;
        try {
            boolean exited = p.waitFor(config.getTimeout(), TimeUnit.MILLISECONDS);
            if (exited) {
                ret = p.exitValue();
            } else {
                timeout = true;
                p.destroyForcibly();
            }
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
        
        try {
            reader.join();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
        
        if (timeout) {
            return "timeout";
        } else if (ret == 0) {
            return holder.result;
        } else {
            return "error";
        }
    }
    
    private static File getExe(File src) {
        return new File(src.getParentFile(), src.getName() + "_exe");
    }

}
