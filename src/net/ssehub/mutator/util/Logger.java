package net.ssehub.mutator.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Logger {

    private static final Logger LOGGER = Logger.get("Logger");
    
    private static final int COMPONENT_WIDTH = 21;
    
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private static BufferedWriter fileOut;
    
    private static List<String> fileOutBuffer = new LinkedList<>();
    
    private String component;
    
    private StringBuilder buffer;
    
    private Logger(String component) {
        this.component = Util.fillWithSpaces(component, COMPONENT_WIDTH);
        this.buffer = new StringBuilder(2048);
    }
    
    public static Logger get(String component) {
        return new Logger(component);
    }
    
    public static synchronized void setFileOut(File file) throws IOException {
        fileOut = new BufferedWriter(new FileWriter(file));
        for (String bufferedLine : fileOutBuffer) {
            fileOut.write(bufferedLine);
            fileOut.write('\n');
        }
        fileOut.flush();
        fileOutBuffer = null;
    }
    
    private static synchronized void printlnImpl(String component, String line) {
        String message = "[" + TIME_FORMAT.format(LocalDateTime.now()) + "] [" + component + "]  " + line;
        System.out.println(message);
        
        if (fileOut != null) {
            try {
                fileOut.write(message);
                fileOut.write('\n');
                fileOut.flush();
            } catch (IOException e) {
                LOGGER.logException(e);
            }
        } else {
            fileOutBuffer.add(message);
        }
    }
    
    private void checkWrite() {
        int eolIndex;
        while ((eolIndex = buffer.indexOf("\n")) != -1) {
            String line = buffer.substring(0, eolIndex);
            printlnImpl(component, line);
            buffer.delete(0, eolIndex + 1);
        }
    }
    
    public void print(String message) {
        this.buffer.append(message);
        checkWrite();
    }
    
    public void println(String message) {
        this.buffer.append(message).append('\n');
        checkWrite();
    }
    
    public void println() {
        this.buffer.append('\n');
        checkWrite();
    }
    
    public void printf(String message, Object... args) {
        this.buffer.append(String.format(Locale.ROOT, message, args));
        checkWrite();
    }
    
    public void logException(Throwable exc) {
        synchronized (Logger.class) {
            println(exc.toString());
            for (StackTraceElement element : exc.getStackTrace()) {
                println("    at " + element.toString());
            }
            
            for (Throwable supressed : exc.getSuppressed()) {
                print("Suppressed: ");
                logException(supressed);
            }
            
            if (exc.getCause() != null) {
                print("Caused by: ");
                logException(exc.getCause());
            }
            
        }
    }
    
}
