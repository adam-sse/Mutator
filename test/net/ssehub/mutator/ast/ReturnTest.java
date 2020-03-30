package net.ssehub.mutator.ast;

public class ReturnTest extends AbstractAstElementTest {

    @Override
    protected AstElement[] createElements() {
        Return s1 = new Return(null);
        {
            Identifier expr = new Identifier(s1);
            expr.identifier = "A";

            s1.value = expr;
        }

        Return s2 = new Return(null);
        {
            Identifier expr = new Identifier(s2);
            expr.identifier = "A";

            s2.value = expr;
        }

        Return s3 = new Return(null);
        {
            Literal expr = new Literal(s3);
            expr.value = "4";

            s3.value = expr;
        }

        Return s4 = new Return(null);
        Return s5 = new Return(null);

        return new AstElement[] { s1, s2, s3, s4, s5 };
    }

    @Override
    protected boolean equal(int element1, int elemen2) {
        if (element1 == elemen2) {
            return true;
        }
        if (element1 == 0 && elemen2 == 1 || element1 == 1 && elemen2 == 0) {
            return true;
        }
        if (element1 == 3 && elemen2 == 4 || element1 == 4 && elemen2 == 3) {
            return true;
        }
        return false;
    }

    @Override
    protected String getLineString(int element) {
        switch (element) {
        case 0:
            return "return A;";

        case 1:
            return "return A;";

        case 2:
            return "return 4;";

        case 3:
            return "return;";

        case 4:
            return "return;";
        }
        return null;
    }

    @Override
    protected String getPrettyString(int element) {
        return getLineString(element) + "\n";
    }

}
