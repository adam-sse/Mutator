package net.ssehub.mutator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import net.ssehub.mutator.ast.operations.AstPrettyPrinter;
import net.ssehub.mutator.evaluation.Evaluator;
import net.ssehub.mutator.evaluation.EvaluatorFactory;
import net.ssehub.mutator.evaluation.TestResult;
import net.ssehub.mutator.mutation.IMutant;
import net.ssehub.mutator.mutation.IMutator;
import net.ssehub.mutator.mutation.PseudoMutant;
import net.ssehub.mutator.mutation.fitness.Fitness;
import net.ssehub.mutator.mutation.fitness.FitnessComparatorFactory;
import net.ssehub.mutator.mutation.fitness.IFitnessComparator;
import net.ssehub.mutator.mutation.genetic.GeneticConfig;
import net.ssehub.mutator.mutation.genetic.GeneticMutator;
import net.ssehub.mutator.mutation.pattern_based.PatternBasedConfig;
import net.ssehub.mutator.mutation.pattern_based.PatternBasedMutator;
import net.ssehub.mutator.parsing.Converter;
import net.ssehub.mutator.parsing.SimpleCLexer;
import net.ssehub.mutator.parsing.SimpleCParser;
import net.ssehub.mutator.util.Logger;
import net.ssehub.mutator.visualization.ControlFlowRenderer;

public class Main {
    
    private static final Logger LOGGER = Logger.get(Main.class.getSimpleName());
    
    private static final int SUCCESS = 0;
    
    private static final int INVALID_USAGE = 1;
    
    private static final int ERROR = 2;
    
    private String argCommand;
    
    private File propertiesFile;
    
    private Properties properties;
    
    private File input;
    
    private File output;
    
    private String dotExe;
    
    public boolean parseOptions(String[] args) {
        LinkedList<String> arguments = new LinkedList<>(Arrays.asList(args));
        
        Map<String, String> cliConfig = new HashMap<>(args.length);
        
        while (!arguments.isEmpty() && arguments.peek().startsWith("-")) {
            String option = arguments.remove();
            switch (option) {
            case "-D": {
                if (arguments.isEmpty()) {
                    System.out.println("Missing KEY=VALUE for option -D");
                    return false;
                }
                String assignment = arguments.remove();
                int equalsIndex = assignment.indexOf('=');
                if (equalsIndex == -1) {
                    System.out.println("Expecting KEY=VALUE after option -D");
                    return false;
                }
                
                cliConfig.put(assignment.substring(0, equalsIndex), assignment.substring(equalsIndex + 1));
            } break;
            
            default:
                System.out.println("Unknown option: " + option);
                return false;
            }
        }
        
        if (arguments.isEmpty()) {
            System.out.println("Missing COMMAND");
            return false;
        }
        
        this.argCommand = arguments.remove();
        switch (this.argCommand) {
        
        case "run":
            if ((this.propertiesFile = getFileArgument(arguments, "CONFIGURATION", true)) == null) {
                return false;
            }
            if ((this.input = getFileArgument(arguments, "INPUT", true)) == null) {
                return false;
            }
            break;
            
        case "evaluate":
            if ((this.propertiesFile = getFileArgument(arguments, "CONFIGURATION", true)) == null) {
                return false;
            }
            if ((this.input = getFileArgument(arguments, "INPUT", true)) == null) {
                return false;
            }
            break;
            
        case "clean":
            if ((this.input = getFileArgument(arguments, "INPUT", true)) == null) {
                return false;
            }
            if ((this.output = getFileArgument(arguments, "OUTPUT", false)) == null) {
                return false;
            }
            break;
            
        case "render":
            if ((this.input = getFileArgument(arguments, "INPUT", true)) == null) {
                return false;
            }
            if ((this.output = getFileArgument(arguments, "OUTPUT", false)) == null) {
                return false;
            }
            if (!arguments.isEmpty()) {
                this.dotExe = arguments.remove();
            } else {
                this.dotExe = "dot";
            }
            
            if (!this.output.getName().endsWith(".svg") && !this.output.getName().endsWith(".png")
                    && !this.output.getName().endsWith(".pdf") && !this.output.getName().endsWith(".dot")) {
                System.out.println("OUTPUT must be either .svg, .png, .pdf, or .dot");
                this.output = null;
                return false;
            }
            
            break;
            
        case "help":
            break;
        
        default:
            System.out.println("Unknown command: " + this.argCommand);
            this.argCommand = null;
            return false;
        }
        
        if (!arguments.isEmpty()) {
            System.out.println("Too many arguments: " + arguments);
            return false;
        }
        
        if (this.propertiesFile != null) {
            this.properties = new Properties();
            try {
                this.properties.load(new FileReader(this.propertiesFile));
            } catch (IOException e) {
                System.out.println("Can't read " + this.propertiesFile + ": " + e.getMessage());
                this.propertiesFile = null;
                return false;
            }
            
            for (Map.Entry<String, String> entry : cliConfig.entrySet()) {
                this.properties.setProperty(entry.getKey(), entry.getValue());
            }
        }
        
        return true;
    }
    
    private String getArgument(LinkedList<String> arguments, String missingName) {
        if (arguments.isEmpty()) {
            System.out.println("Missing argument " + missingName);
            return null;
        }
        return arguments.remove();
    }
    
    private File getFileArgument(LinkedList<String> arguments, String missingName, boolean requireExisting) {
        String path = getArgument(arguments, missingName);
        
        File result = null;
        if (path != null) {
            result = new File(path);
            
            if (requireExisting && !result.isFile()) {
                if (!result.exists()) {
                    System.out.println(path + " doesn't exist");
                } else {
                    System.out.println(path + " is not a file");
                }
                result = null;
            }
        }
        
        return result;
    }
    
    public boolean execute() {
        boolean success;
        
        switch (this.argCommand) {
        case "run":
            success = run();
            break;
            
        case "evaluate":
            success = evaluate();
            break;
            
        case "clean":
            success = clean();
            break;
            
        case "render":
            success = render();
            break;
            
        case "help":
            success = help();
            break;
        
        default:
            // shouldn't happen, as parseOptions() already validated the input
            throw new IllegalStateException("Invalid argCommand: " + this.argCommand);
        }

        return success;
    }
    
    private boolean run() {
        try {
            String mutatorType = this.properties.getProperty("mutator").toLowerCase();
            
            int inputDotIndex = this.input.getName().lastIndexOf('.');
            String inputBase = this.input.getName().substring(0,
                    inputDotIndex > 0 ? inputDotIndex : this.input.getName().length());
            String inputSuffix = inputDotIndex > 0 ? this.input.getName().substring(inputDotIndex + 1) : "";
            
            // create an execution directory
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss", Locale.ROOT);
            File execDir = new File(inputBase + '_' + mutatorType + '_' + formatter.format(LocalDateTime.now()));
            LOGGER.println("Execution directory is at " + execDir.getAbsolutePath());
            if (execDir.exists()) {
                LOGGER.println(execDir + " already exists");
                return false;
            }
            execDir.mkdir();
            
            // create log file in execDir
            Logger.setFileOut(new File(execDir, "mutator.log"));
            
            // copy config to execDir
            Files.copy(this.propertiesFile.toPath(), new File(execDir, "config.properties").toPath());
            
            IMutator mutator;
            BaseConfig config;
            switch (mutatorType) {
            case "genetic":
                config = new GeneticConfig(this.properties);
                FitnessComparatorFactory.init(config);
                config.setExecDir(execDir);
                mutator = new GeneticMutator((GeneticConfig) config);
                break;
            case "patternbased":
                config = new PatternBasedConfig(this.properties);
                FitnessComparatorFactory.init(config);
                config.setExecDir(execDir);
                mutator = new PatternBasedMutator((PatternBasedConfig) config);
                break;
            default:
                LOGGER.println("Invalid mutator setting: " + mutatorType);
                return false;
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
            net.ssehub.mutator.ast.File file = parse(this.input);
            
            // write to execDir
            File inputOut = new File(execDir, "input." + inputSuffix);
            try (FileWriter out = new FileWriter(inputOut)) {
                out.write(file.accept(new AstPrettyPrinter(true)));
            }
            
            // 2) mutate file
            LOGGER.println("Mutating...");
            List<IMutant> mutants = mutator.run(file);
            
            // 3) print out
            // TODO: multi-objective fitness
            LOGGER.println();
            LOGGER.println("Writing " + mutants.size() + " mutants...");
            IFitnessComparator comparator = FitnessComparatorFactory.get();
            
            double bestFitness = mutants.size() > 0 ?
                    comparator.toSingleValue(mutator.getFitness(mutants.get(0).getId())) : 0.0;
            Double originalFitness = null;
            if (mutator.getUnmodifiedId() != null) {
                originalFitness = comparator.toSingleValue(mutator.getFitness(mutator.getUnmodifiedId()));
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
                
                Fitness fitness = mutator.getFitness(mutant.getId());
                double fd = comparator.toSingleValue(mutator.getFitness(mutant.getId()));
                LOGGER.printf("  %2d  | %10s | %7s | %5.1f%%",
                        i + 1,
                        mutant.getId(),
                        fitness,
                        fd / bestFitness * 100);
                if (originalFitness != null) {
                    LOGGER.printf(" | %6.1f%%\n", fd / originalFitness * 100);
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
            return false;
        }
        
        return true;
    }

    private boolean evaluate() {
        try {
            BaseConfig config = new BaseConfig(this.properties);
            FitnessComparatorFactory.init(config);
            
            // 1) parse file
            LOGGER.println("Parsing...");
            net.ssehub.mutator.ast.File file = parse(this.input);
            PseudoMutant mutant = new PseudoMutant(file);
            
            // 2) set up temporary evaluation directory
            File tmp = File.createTempFile("mutator_evaluation", null);
            tmp.delete();
            tmp.mkdir();
            config.setExecDir(tmp);
            tmp.deleteOnExit();
            
            config.getEvalDir().mkdir();
            File newTestSrc = new File(config.getEvalDir(), config.getTestSrc().getName());
            Files.copy(config.getTestSrc().toPath(), newTestSrc.toPath());
            config.setTestSrc(newTestSrc);
            File newFitnessSrc = new File(config.getEvalDir(), config.getFitnessSrc().getName());
            Files.copy(config.getFitnessSrc().toPath(), newFitnessSrc.toPath());
            config.setFitnessSrc(newFitnessSrc);
            
            // 3) evaluate
            LOGGER.println("Evaluating...");
            Evaluator evaluator = EvaluatorFactory.create(config);
            TestResult result = evaluator.test(mutant);
            LOGGER.println("Test result: " + result);
            if (result == TestResult.PASS) {
                Fitness fitness = evaluator.measureFitness(mutant);
                LOGGER.println("Fitness: " + fitness);
                LOGGER.println("  (= " + FitnessComparatorFactory.get().toSingleValue(fitness) + ")");
            }
            
            // 4) clean up
            Files.walkFileTree(tmp.toPath(), new SimpleFileVisitor<Path>() {
                
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
                
            });
        } catch (IOException e) {
            LOGGER.logException(e);
            return false;
        }
        
        return true;
    }

    private boolean clean() {
        try {
            // 1) parse file
            LOGGER.println("Parsing...");
            net.ssehub.mutator.ast.File file = parse(this.input);
            
            // 2) print out
            LOGGER.println("Writing...");
            
            try (FileWriter out = new FileWriter(this.output)) {
                out.write(file.accept(new AstPrettyPrinter(false)));
            }
            
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.logException(e);
            return false;
        }
        
        return false;
    }

    private boolean render() {
        try {
            // 1) parse file
            LOGGER.println("Parsing...");
            net.ssehub.mutator.ast.File file = parse(this.input);
            
            // 2) print out
            LOGGER.println("Rendering...");
            ControlFlowRenderer renderer = new ControlFlowRenderer(this.dotExe);
            renderer.render(file, this.output);
            
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.logException(e);
            return false;
        }
        
        return true;
    }

    private boolean help() {
        System.out.println("Usage: mutator [OPTION]... COMMAND [ARGUMENT...]");
        System.out.println();
        System.out.println("Execute mutator with a given command (task).");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -D KEY=VALUE");
        System.out.println("        Set the given configuration key to the given value.");
        System.out.println("        Overrides the supplied configuration file (if any).");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  run CONFIGURATION INPUT");
        System.out.println("        Run the main Mutator program based on the given");
        System.out.println("        configuration.");
        System.out.println("  evaluate CONFIGURATION INPUT");
        System.out.println("        Evaluates the fitness of the given input file");
        System.out.println("        according to the configuration.");
        System.out.println("  clean INPUT OUTPUT");
        System.out.println("        Pretty-print the given file.");
        System.out.println("  render INPUT OUTPUT [DOT]");
        System.out.println("        Renders the control-flow graph of the given source-");
        System.out.println("        code file. DOT is the graphviz dot command to use.");
        System.out.println("        OUTPUT must have a valid filename extension (svg,");
        System.out.println("        png, pdf, or dot).");
        System.out.println("  help");
        System.out.println("        Display this help message.");
        System.out.println();
        System.out.println("Exit status:");
        System.out.println("  " + SUCCESS);
        System.out.println("        Execution was successful.");
        System.out.println("  " + INVALID_USAGE);
        System.out.println("        Invalid usage (e.g. wrong command-line arguments).");
        System.out.println("  " + ERROR);
        System.out.println("        Error during execution.");
        
        return true;
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

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        
        if (!main.parseOptions(args)) {
            System.out.println();
            main.help();
            System.exit(INVALID_USAGE);
        }
        
        if (!main.execute()) {
            System.exit(ERROR);
        }
        
        System.exit(SUCCESS);
    }

}
