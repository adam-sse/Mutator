package net.ssehub.mutator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import net.ssehub.mutator.ast.operations.AstPrettyPrinter;
import net.ssehub.mutator.mutation.Mutant;
import net.ssehub.mutator.mutation.MutantList;
import net.ssehub.mutator.mutation.Mutator;
import net.ssehub.mutator.parsing.Converter;
import net.ssehub.mutator.parsing.grammar.SimpleCLexer;
import net.ssehub.mutator.parsing.grammar.SimpleCParser;
import net.ssehub.mutator.visualization.ControlFlowRenderer;

public class Main {
    
    private static int help() {
        System.out.println("Usage: <command> <command-specific arguments>*");
        System.out.println("Available commands:");
        System.out.println("  help                             : "
                + "Display this help message");
        System.out.println("  run <configuration> <input file> : "
                + "Run the main Mutator program with the given configuration");
        System.out.println("  clean <input file> <output file> : "
                + "Pretty-print the given file");
        System.out.println("  render <dot exe> <input file> <output file> : "
                + "Renders the control-flow graph of the given source code file");
        return 0;
    }
    
    private static int run(String configPath, String inputPath) {
        try {
            File configFile = new File(configPath);
            Properties props = new Properties();
            props.load(new FileReader(configFile));
            Configuration config = new Configuration(props);
            
            File input = new File(inputPath);
            
            // 1) parse file to mutate
            System.out.println("Parsing...");
            net.ssehub.mutator.ast.File file = parse(input);
            
            // 2) mutate file
            System.out.println("Mutating...");
            Mutator mutator = new Mutator(config);
            MutantList mutants = mutator.run(file);
            
            // 3) print out
            System.out.println();
            System.out.println("Writing " + mutants.getSize() + " mutants...");
            double bestFitness = mutants.getSize() > 0 ? mutator.getFitness(mutants.getMutant(0).getId()) : 0.0;
            double originalFitness = mutator.getFitness("G001_M001");
            System.out.println(" Rank |   Mutant   |  Best  | Original ");
            System.out.println("------+------------+--------+----------");
            for (int i = 0; i < mutants.getSize(); i++) {
                Mutant mutant = mutants.getMutant(i);
                
                System.out.printf(Locale.ROOT, "  %2d  | %10s | %5.1f%% | %6.1f%%\n",
                        i + 1,
                        mutant.getId(),
                        mutator.getFitness(mutant.getId()) / bestFitness * 100,
                        mutator.getFitness(mutant.getId()) / originalFitness * 100);
                
                int dotIndex = input.getName().lastIndexOf('.');
                String inputBase = input.getName().substring(0, dotIndex);
                String suffix = input.getName().substring(dotIndex + 1);
                File output = new File(input.getParentFile(), inputBase + "_" + (i + 1) + "_"
                        + mutant.getId() + "." + suffix);
                mutant.write(output);
            }
            
            System.out.println();
            System.out.println("Statistics:");
            mutator.printStatistics();
            
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace(System.out);
            return 2;
        }
        
        return 0;
    }
    
    private static int clean(String inputPath, String outpuPath) {
        try {
            File input = new File(inputPath);
            
            // 1) parse file
            System.out.println("Parsing...");
            net.ssehub.mutator.ast.File file = parse(input);
            
            // 2) print out
            System.out.println("Writing...");
            
            try (FileWriter out = new FileWriter(outpuPath)) {
                out.write(file.accept(new AstPrettyPrinter(false)));
            }
            
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace(System.out);
            return 2;
        }
        
        return 0;
    }
    
    private static int render(String dotExe, String inputPath, String outputPath) {
        try {
            if (!outputPath.endsWith(".png") && !outputPath.endsWith(".svg") && !outputPath.endsWith(".pdf")) {
                System.out.println("Output must be either .png, .svg or .pdf");
                return 2;
            }
            
            File input = new File(inputPath);
            File ouput = new File(outputPath);
            
            // 1) parse file
            System.out.println("Parsing...");
            net.ssehub.mutator.ast.File file = parse(input);
            
            // 2) print out
            System.out.println("Rendering...");
            ControlFlowRenderer renderer = new ControlFlowRenderer(dotExe);
            renderer.render(file, ouput);
            
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace(System.out);
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
