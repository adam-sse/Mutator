package net.ssehub.mutator.mutation.genetic.mutations;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.Statement;

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
