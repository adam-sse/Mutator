package net.ssehub.mutator.mutation.mutations;

import net.ssehub.mutator.parsing.ast.AstElement;
import net.ssehub.mutator.parsing.ast.Statement;

class Util {

    private Util() {
    }

    public static String getParentStatementText(AstElement element) {
        if (element instanceof Statement) {
            return element.getText();
        } else {
            return getParentStatementText(element.parent);
        }
    }
    
}