package net.ssehub.mutator.ast;

public class ExpressionStmtTest extends AbstractAstElementTest {

    @Override
    protected AstElement[] createElements() {
        ExpressionStmt s1 = new ExpressionStmt(null);
        {
            Identifier expr = new Identifier(s1);
            expr.identifier = "A";

            s1.expr = expr;
        }

        ExpressionStmt s2 = new ExpressionStmt(null);
        {
            Identifier expr = new Identifier(s2);
            expr.identifier = "A";

            s2.expr = expr;
        }

        ExpressionStmt s3 = new ExpressionStmt(null);
        {
            Literal expr = new Literal(s3);
            expr.value = "4";

            s3.expr = expr;
        }

        return new AstElement[] { s1, s2, s3 };
    }

    @Override
    protected boolean equal(int element1, int elemen2) {
        if (element1 == elemen2)
            return true;
        if (element1 == 0 && elemen2 == 1 || element1 == 1 && elemen2 == 0)
            return true;
        return false;
    }

    @Override
    protected String getLineString(int element) {
        switch (element) {
        case 0:
            return "A;";

        case 1:
            return "A;";

        case 2:
            return "4;";
        }
        return null;
    }

    @Override
    protected String getPrettyString(int element) {
        return getLineString(element) + "\n";
    }

}
