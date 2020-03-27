package net.ssehub.mutator.mutation.pattern_based.patterns;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.Expression;
import net.ssehub.mutator.ast.Statement;

class Util {

    private Util() {
    }
    
    public static Statement findParentStatement(Expression expr) {
        AstElement parent = expr.parent;
        while (!(parent instanceof Statement)) {
            parent = parent.parent;
        }
        return (Statement) parent;
    }
    
    public static int depth(AstElement element) {
        int depth = 0;
        while (element != null) {
            depth++;
            element = element.parent;
        }
        return depth;
    }
    
    public static boolean sameElements(AstElement... elements) {
        for (int i = 1; i < elements.length; i++) {
            if (elements[i].id != elements[0].id) {
                return false;
            }
        }
        return true;
    }
    
    public static AstElement findCommonParent(AstElement... elements) {

        int minDepth = Integer.MAX_VALUE;
        for (AstElement element : elements) {
            int depth = depth(element);
            if (depth < minDepth) {
                minDepth = depth;
            }
        }
        
        for (int i = 0; i < elements.length; i++) {
            while (depth(elements[i]) > minDepth) {
                elements[i] = elements[i].parent;
            }
        }
        
        while (!sameElements(elements)) {
            for (int i = 0; i < elements.length; i++) {
                elements[i] = elements[i].parent;
            }
        }
        
        return elements[0];
    }
    
    
}
