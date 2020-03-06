package util;

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
    
}
