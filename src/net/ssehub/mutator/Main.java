package net.ssehub.mutator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import net.ssehub.mutator.ast.operations.AstPrettyPrinter;
import net.ssehub.mutator.mutation.IMutant;
import net.ssehub.mutator.mutation.IMutator;
import net.ssehub.mutator.mutation.genetic.GeneticConfig;
import net.ssehub.mutator.mutation.genetic.GeneticMutator;
import net.ssehub.mutator.mutation.pattern_based.PatternBasedConfig;
import net.ssehub.mutator.mutation.pattern_based.PatternBasedMutator;
import net.ssehub.mutator.parsing.Converter;
import net.ssehub.mutator.parsing.grammar.SimpleCLexer;
import net.ssehub.mutator.parsing.grammar.SimpleCParser;
import net.ssehub.mutator.util.Logger;
import net.ssehub.mutator.visualization.ControlFlowRenderer;

public class Main {
    
    private static final Logger LOGGER = Logger.get(Main.class.getSimpleName());
    
    private static int help() {
        LOGGER.println("Usage: <command> <command-specific arguments>*");
        LOGGER.println("Available commands:");
        LOGGER.println("  help                             : "
                + "Display this help message");
        LOGGER.println("  run <configuration> <input file> : "
                + "Run the main Mutator program with the given configuration");
        LOGGER.println("  clean <input file> <output file> : "
                + "Pretty-print the given file");
        LOGGER.println("  render <dot exe> <input file> <output file> : "
                + "Renders the control-flow graph of the given source code file");
        return 0;
    }
    
    private static int run(String configPath, String inputPath) {
        try {
            File input = new File(inputPath);
            
            File configFile = new File(configPath);
            Properties props = new Properties();
            props.load(new FileReader(configFile));
            
            String mutatorType = props.getProperty("mutator").toLowerCase();
            
            int inputDotIndex = input.getName().lastIndexOf('.');
            String inputBase = input.getName().substring(0,
                    inputDotIndex > 0 ? inputDotIndex : input.getName().length());
            String inputSuffix = inputDotIndex > 0 ? input.getName().substring(inputDotIndex + 1) : "";
            
            // create an execution directory
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss", Locale.ROOT);
            File execDir = new File(inputBase + '_' + formatter.format(LocalDateTime.now()) + '_' + mutatorType);
            LOGGER.println("Execution directory is at " + execDir.getAbsolutePath());
            if (execDir.exists()) {
                LOGGER.println(execDir + " already exists");
                return 2;
            }
            execDir.mkdir();
            
            // create log file in execDir
            Logger.setFileOut(new File(execDir, "mutator.log"));
            
            // copy config to execDir
            Files.copy(configFile.toPath(), new File(execDir, "config.properties").toPath());
            
            IMutator mutator;
            BaseConfig config;
            switch (mutatorType) {
            case "genetic":
                config = new GeneticConfig(props);
                config.setExecDir(execDir);
                mutator = new GeneticMutator((GeneticConfig) config);
                break;
            case "patternbased":
                config = new PatternBasedConfig(props);
                config.setExecDir(execDir);
                mutator = new PatternBasedMutator((PatternBasedConfig) config);
                break;
            default:
                LOGGER.println("Invalid mutator setting: " + mutatorType);
                return 2;
            }
            
            // copy the evaluation resources to the exec directory
            config.getEvalDir().mkdir();
            File newTestSrc = new File(config.getEvalDir(), config.getTestSrc().getName());
            Files.copy(config.getTestSrc().toPath(), newTestSrc.toPath());
            config.setTestSrc(newTestSrc);
            File newFitnessSrc = new File(config.getEvalDir(), config.getFitnessSrc().getName());
            Files.copy(config.getFitnessSrc().toPath(), newFitnessSrc.toPath());
            config.setFitnessSrc(newFitnessSrc);
            
            // 1) parse file to mutate
            LOGGER.println("Parsing...");
            net.ssehub.mutator.ast.File file = parse(input);
            
            // write to execDir
            File inputOut = new File(execDir, "input." + inputSuffix);
            try (FileWriter out = new FileWriter(inputOut)) {
                out.write(file.accept(new AstPrettyPrinter(true)));
            }
            
            // 2) mutate file
            LOGGER.println("Mutating...");
            List<IMutant> mutants = mutator.run(file);
            
            // 3) print out
            LOGGER.println();
            LOGGER.println("Writing " + mutants.size() + " mutants...");
            double bestFitness = mutants.size() > 0 ? mutator.getFitness(mutants.get(0).getId()) : 0.0;
            Double originalFitness = null;
            if (mutator.getUnmodifiedId() != null) {
                originalFitness = mutator.getFitness(mutator.getUnmodifiedId());
            }
            
            LOGGER.print(" Rank |   Mutant   | Fitness |  Best  ");
            if (originalFitness != null) {
                LOGGER.println("| Original ");
            } else {
                LOGGER.println();
            }
            LOGGER.print("------+------------+---------+--------");
            if (originalFitness != null) {
                LOGGER.println("+----------");
            } else {
                LOGGER.println();
            }
            
            for (int i = 0; i < mutants.size(); i++) {
                IMutant mutant = mutants.get(i);
                
                double fitness = mutator.getFitness(mutant.getId());
                LOGGER.printf("  %2d  | %10s | %7.3f | %5.1f%%",
                        i + 1,
                        mutant.getId(),
                        fitness,
                        fitness / bestFitness * 100);
                if (originalFitness != null) {
                    LOGGER.printf(" | %6.1f%%\n", fitness / originalFitness * 100);
                } else {
                    LOGGER.println();
                }
                
                File output = new File(execDir, "result_" + (i + 1) + "_"
                        + mutant.getId() + "." + inputSuffix);
                mutant.write(output);
            }
            
            LOGGER.println();
            LOGGER.println("Statistics:");
            mutator.printStatistics();
            
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.logException(e);
            return 2;
        }
        
        return 0;
    }
    
    private static int clean(String inputPath, String outpuPath) {
        try {
            File input = new File(inputPath);
            
            // 1) parse file
            LOGGER.println("Parsing...");
            net.ssehub.mutator.ast.File file = parse(input);
            
            // 2) print out
            LOGGER.println("Writing...");
            
            try (FileWriter out = new FileWriter(outpuPath)) {
                out.write(file.accept(new AstPrettyPrinter(false)));
            }
            
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.logException(e);
            return 2;
        }
        
        return 0;
    }
    
    private static int render(String dotExe, String inputPath, String outputPath) {
        try {
            if (!outputPath.endsWith(".png") && !outputPath.endsWith(".svg")
                    && !outputPath.endsWith(".pdf") && !outputPath.endsWith(".dot")) {
                LOGGER.println("Output must be either .png, .svg, .pdf, or .dot");
                return 2;
            }
            
            File input = new File(inputPath);
            File ouput = new File(outputPath);
            
            // 1) parse file
            LOGGER.println("Parsing...");
            net.ssehub.mutator.ast.File file = parse(input);
            
            // 2) print out
            LOGGER.println("Rendering...");
            ControlFlowRenderer renderer = new ControlFlowRenderer(dotExe);
            renderer.render(file, ouput);
            
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.logException(e);
            return 2;
        }
        
        return 0;
    }
    
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            help();
            System.exit(1);
        }
        
        int result = 1;
        switch (args[0]) {
        case "help":
            result = help();
            break;
            
        case "run":
            if (args.length != 3) {
                help();
            } else {
                result = run(args[1], args[2]);
            }
            break;
            
        case "clean":
            if (args.length != 3) {
                help();
            } else {
                result = clean(args[1], args[2]);
            }
            break;
        case "render":
            if (args.length != 4) {
                help();
            } else {
                result = render(args[1], args[2], args[3]);
            }
            break;
            
        default:
            help();
            break;
        }
        
        System.exit(result);
    }
    
    private static net.ssehub.mutator.ast.File parse(File input) throws IOException {
        SimpleCLexer lexer = new SimpleCLexer(CharStreams.fromPath(input.toPath()));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SimpleCParser parser = new SimpleCParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                    int charPositionInLine, String msg, RecognitionException exc) {
                throw new IllegalArgumentException("Parsing failed: in line " + line + ":" + charPositionInLine
                        + " " + msg);
            }
        });
        
        Converter converter = new Converter();
        net.ssehub.mutator.ast.File file = converter.convert(parser.file());
        return file;
    }

}
