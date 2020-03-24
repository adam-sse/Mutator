package net.ssehub.mutator.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.ssehub.mutator.BaseConfig;
import net.ssehub.mutator.mutation.IMutant;
import net.ssehub.mutator.mutation.fitness.Fitness;
import net.ssehub.mutator.mutation.fitness.FitnessComparatorFactory;
import net.ssehub.mutator.mutation.fitness.IFitnessComparator;

public class LinuxEvaluator extends Evaluator {
    
    private BaseConfig config;
    
    public LinuxEvaluator(BaseConfig config) {
        this.config = config;
    }
    
    @Override
    public TestResult test(IMutant mutant) {
        TestResult result;
        
        try {
            mutant.write(new File(config.getEvalDir(), config.getDropin()));
            boolean compilationSuccess = compile(config.getTestSrc(), getExe(config.getTestSrc()));
            if (compilationSuccess) {
                List<String> stdout = run(getExe(config.getTestSrc()));
                
                if (stdout.size() == 1 && stdout.get(0).equals("timeout")) {
                    result = TestResult.TIMEOUT;
                } else  if (stdout.size() >= 1 && stdout.get(stdout.size() - 1).equals("1")) {
                    result = TestResult.PASS;
                } else {
                    result = TestResult.TEST_FAILED;
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
    public Fitness measureFitness(IMutant mutant) {
        IFitnessComparator comparator = FitnessComparatorFactory.get();
        Fitness fitness = RUNTIME_ERROR;
        
        try {
            mutant.write(new File(config.getEvalDir(), config.getDropin()));
            boolean compilationSuccess = compile(config.getFitnessSrc(), getExe(config.getFitnessSrc()));
            if (compilationSuccess) {
                
                Fitness[] measures = new Fitness[config.getFitnessMeasures()];
                for (int i = 0; i < measures.length; i++) {
                    // sleep a bit so that we can cool down before performance measures take place
                    try {
                        Thread.sleep(config.getSleepBeforeFitness());
                    } catch (InterruptedException e) {
                    }
                    
                    List<String> stdout = run(getExe(config.getFitnessSrc()));
                    double[] values = new double[stdout.size()];
                    int j = 0;
                    for (String line : stdout) {
                        values[j++] = Double.parseDouble(line);
                    }
                    
                    measures[i] = new Fitness(values);
                }
                Arrays.sort(measures, comparator);
                
                fitness = measures[measures.length / 2];
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
    
    private List<String> run(File exe) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(exe.getPath());
        pb.redirectErrorStream(true);

        Process p = pb.start();
        
        List<String> lines = new LinkedList<>();
        
        Thread reader = new Thread(() -> {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    lines.add(line);
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
            return Arrays.asList("timeout");
        } else if (ret == 0) {
            return lines;
        } else {
            return Arrays.asList("error");
        }
    }
    
    private static File getExe(File src) {
        return new File(src.getParentFile(), src.getName() + "_exe");
    }

}
