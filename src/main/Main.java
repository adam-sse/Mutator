package main;

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

import mutation.Mutant;
import mutation.MutantList;
import mutation.Mutator;
import parsing.Converter;
import parsing.ast.AstElement;
import parsing.grammar.SimpleCLexer;
import parsing.grammar.SimpleCParser;

public class Main {
    
    private static int help() {
        System.out.println("Usage: <command> <command-specific arguments>*");
        System.out.println("Available commands:");
        System.out.println("  help                             : Display this help message");
        System.out.println("  run <configuration> <input file> : Run the main Mutator program with the given configuration");
        System.out.println("  clean <input file> <output file> : Pretty-print the given file");
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
            SimpleCLexer lexer = new SimpleCLexer(CharStreams.fromPath(input.toPath()));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            SimpleCParser parser = new SimpleCParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                        int charPositionInLine, String msg, RecognitionException e) {
                    throw new IllegalArgumentException("Parsing failed: in line " + line + ":" + charPositionInLine + " " + msg);
                }
            });
            
            Converter converter = new Converter();
            parsing.ast.File file = converter.convert(parser.file());
            
            // 2) mutate file
            System.out.println("Mutating...");
            Mutator mutator = new Mutator(config);
            MutantList mutants = mutator.run(file);
            
            // 3) print out
            System.out.println();
            System.out.println("Writing " + mutants.getSize() + " mutants...");
            double bestFitness = mutants.getSize() > 0 ? mutator.getFitness(mutants.getMutant(0).getId()) : 0.0;
            double originalFitness = mutator.getFitness("G001_M001");
            System.out.println(" Rank |  Mutant   |  Best  | Original ");
            System.out.println("------+-----------+--------+----------");
            for (int i = 0; i < mutants.getSize(); i++) {
                Mutant mutant = mutants.getMutant(i);
                
                System.out.printf(Locale.ROOT, "  %2d  | %s | %5.1f%% | %6.1f%%\n",
                        i + 1,
                        mutant.getId(),
                        mutator.getFitness(mutant.getId()) / bestFitness * 100,
                        mutator.getFitness(mutant.getId()) / originalFitness * 100);
                
                int dotIndex = input.getName().lastIndexOf('.');
                String inputBase = input.getName().substring(0, dotIndex);
                String suffix = input.getName().substring(dotIndex + 1);
                File output = new File(input.getParentFile(), inputBase + "_" + (i + 1) + "_" + mutant.getId() + "." + suffix);
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
            SimpleCLexer lexer = new SimpleCLexer(CharStreams.fromPath(input.toPath()));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            SimpleCParser parser = new SimpleCParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                        int charPositionInLine, String msg, RecognitionException e) {
                    throw new IllegalArgumentException("Parsing failed: in line " + line + ":" + charPositionInLine + " " + msg);
                }
            });
            
            Converter converter = new Converter();
            parsing.ast.File file = converter.convert(parser.file());
            
            // 2) print out
            System.out.println("Writing...");
            AstElement.PRINT_IDS = false;
            
            try (FileWriter out = new FileWriter(outpuPath)) {
                out.write(file.print(""));
            }
            
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
            
        default:
            help();
            break;
        }
        
        System.exit(result);
    }

}
