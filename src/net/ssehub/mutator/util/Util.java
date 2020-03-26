package net.ssehub.mutator.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class Util {

    private Util() {
    }
    
    public static <T> int findIndex(List<T> list, T exactObj) {
        int i = 0;
        for (T element : list) {
            if (element == exactObj) {
                return i; 
            }
            i++;
        }
        return -1;
    }
    
    public static void deleteDirecotry(File dir) throws IOException {
        Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
            
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
    }
    
    public static String fillWithSpaces(String str, int width) {
        String result;
        int numSpaces = width - str.length();
        if (numSpaces > 0) {
            result = " ".repeat(numSpaces / 2) + str + " ".repeat((numSpaces + 1) / 2);
        } else {
            result = str;
        }
        return result;
    }
    
}
