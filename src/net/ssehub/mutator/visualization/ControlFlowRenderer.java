package net.ssehub.mutator.visualization;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.Statement;
import net.ssehub.mutator.ast.control_flow.ControlFlowBlock;
import net.ssehub.mutator.ast.control_flow.ControlFlowCreator;
import net.ssehub.mutator.ast.control_flow.ControlFlowFunction;

public class ControlFlowRenderer {


    private String dotExe;
    
    private StringBuilder dot;
    
    public ControlFlowRenderer(String dotExe) {
        this.dotExe = dotExe;
    }
    
    public void render(File file, java.io.File output) throws IOException {
        this.dot = new StringBuilder();
        
        ControlFlowCreator creator = new ControlFlowCreator();
        List<ControlFlowFunction> functions = creator.createControlFlow(file);
        
        preamble();
        
        Map<String, ControlFlowFunction> localFunctions = new HashMap<>();
        for (ControlFlowFunction func : functions) {
            function(func);
            
            localFunctions.put(func.getName(), func);
        }
        
        // write function calls
        for (ControlFlowFunction func : functions) {
            for (ControlFlowBlock block : func.getAllBlocks()) {
                for (String funcName : block.getCalledFunctions()) {
                    ControlFlowFunction called = localFunctions.get(funcName);
                    String attributes = "headport=_; tailport=_; color=grey; style=dashed";
                    if (called != null && !called.equals(func)) {
                        this.dot.append("    ").append(escapeName(block.getName())).append(" -> ")
                            .append(escapeName(called.getStartBlock().getName()))
                            .append(" [" + attributes + "; lhead=" + getClusterName(called.getName()) + "];\n");
                        this.dot.append("    ").append(escapeName(called.getEndBlock().getName())).append(" -> ")
                            .append(escapeName(block.getName()))
                            .append(" [" + attributes + "; ltail=" + getClusterName(called.getName()) + "];\n");
                    } else  if (called != null) {
                        // recursive call
                        
                        this.dot.append("    ").append(escapeName(block.getName())).append(" -> ")
                            .append(escapeName(called.getStartBlock().getName()))
                            .append(" [" + attributes + "];\n");
                        this.dot.append("    ").append(escapeName(called.getEndBlock().getName())).append(" -> ")
                            .append(escapeName(block.getName()))
                            .append(" [" + attributes + "];\n");
                    }
                }
            }
        }
        
        epilog();
        
//        System.out.println(this.dot);
        
        java.io.File tmp = java.io.File.createTempFile("mutator_", ".dot");
        tmp.deleteOnExit();
        try (FileWriter out = new FileWriter(tmp)) {
            out.write(this.dot.toString());
        }
        
        ProcessBuilder pb = new ProcessBuilder(dotExe,
                "-T" + output.getName().substring(output.getName().lastIndexOf('.') + 1),
                "-o", output.getAbsolutePath(), tmp.getAbsolutePath());
        pb.inheritIO();
        pb.redirectErrorStream(true);
        
        try {
            pb.start().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void preamble() {
        this.dot.append("digraph ast {\n")
            .append("    graph [fontname=\"Liberation Mono\"; fontsize=9];\n")
            .append("    node [fontname=\"Liberation Mono\"; fontsize=9; shape=\"rectangle\"; nojustify=\"true\"; margin=\"0.1\"; style=filled; fillcolor=white];\n")
            .append("    edge [fontname=\"Liberation Mono\"; fontsize=9; tailport=\"s\"; headport=\"n\"; arrowhead=\"vee\"; arrowsize=0.8];\n")
            .append("    compound=true;\n")
            .append("\n");
            
    }
    
    private void epilog() {
        this.dot.append("}\n");
    }
    
    private void function(ControlFlowFunction func) {
          this.dot.append("    subgraph " + getClusterName(func.getName()) + " {\n")
              .append("        label=" + escapeName(func.getHeader()) + ";\n")
              .append("        style=filled;\n")
              .append("        bgcolor=whitesmoke;\n")
              .append("        " + escapeName(func.getStartBlock().getName())
                      + " [shape=\"circle\"; label=\"\"; width=0.2; style=\"filled\"; fillcolor=\"black\"];\n")
              .append("        " + escapeName(func.getEndBlock().getName())
                      + " [shape=\"circle\"; label=\"\"; width=0.2];\n");
          
            // write nodes
            for (ControlFlowBlock block : func.getAllBlocks()) {
                if (block != func.getStartBlock() && block != func.getEndBlock()) {
                    this.dot.append("        ").append(escapeName(block.getName()))
                            .append(" [label=\"").append(blockToString(block)).append("\"];\n");
                }
            }
            
            // write edges
            for (ControlFlowBlock block : func.getAllBlocks()) {
                boolean hasCondition = block.getOutCondition() != null;
                
                if (block.getOutTrue() != null) {
                    this.dot.append("        ").append(escapeName(block.getName()))
                            .append(" -> ").append(escapeName(block.getOutTrue().getName()));
                    
                    if (hasCondition) {
                        this.dot.append(" [label=\"1\"]");
                    }
                    this.dot.append(";\n");
                }
                

                if (block.getOutFalse() != null) {
                    this.dot.append("        ").append(escapeName(block.getName()))
                            .append(" -> ").append(escapeName(block.getOutFalse().getName()));
                    
                    if (hasCondition) {
                        this.dot.append(" [label=\"0\"]");
                    }
                    this.dot.append(";\n");
                }
            }
            
            this.dot.append("    }\n")
                .append("\n");
    }
    
    private static String blockToString(ControlFlowBlock block) {
        StringBuilder result = new StringBuilder();
        for (Statement stmt : block.getSequence()) {
            result.append(stmt.getText()).append("\\l");
        }
        
        if (block.getOutCondition() != null) {
            result.append(block.getOutCondition().getText()).append("?").append("\\l");
        }
        
        return result.toString();
    }
    
    private static String escapeName(String name) {
        return '\"' + name + '\"';
    }
    
    private static String getClusterName(String funcName) {
        return escapeName("cluster_" + funcName);
    }
    
}
