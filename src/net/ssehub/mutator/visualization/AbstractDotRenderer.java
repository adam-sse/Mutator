package net.ssehub.mutator.visualization;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.mutator.util.Logger;

abstract class AbstractDotRenderer {

    private static final Logger LOGGER = Logger.get("AbstractDotRenderer");
    
    private String dotExe;
    
    protected AbstractDotRenderer(String dotExe) {
        this.dotExe = dotExe;
    }
    
    protected void render(String dotGraph, String engine, File output) throws IOException {
        if (output.getName().endsWith(".dot")) {
            try (FileWriter out = new FileWriter(output)) {
                out.write(dotGraph);
            }
            
        } else {
            java.io.File tmp = java.io.File.createTempFile("mutator_", ".dot");
            tmp.deleteOnExit();
            try (FileWriter out = new FileWriter(tmp)) {
                out.write(dotGraph);
            }
            
            List<String> command = new LinkedList<>();
            command.add(dotExe);
            command.add("-T" + output.getName().substring(output.getName().lastIndexOf('.') + 1));
            command.add("-K" + engine);
            command.add("-o");
            command.add(output.getAbsolutePath());
            command.add(tmp.getAbsolutePath());
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            pb.redirectErrorStream(true);
            
            try {
                pb.start().waitFor();
            } catch (InterruptedException e) {
                LOGGER.logException(e);
            }
        }
    }
    
}
