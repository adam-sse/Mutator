package net.ssehub.mutator.ast;

public class BinaryExprTest extends AbstractAstElementTest {

    @Override
    protected AstElement[] createElements() {
        BinaryExpr e1 = new BinaryExpr(null);
        {
            e1.operator = BinaryOperator.ADDITION;

            Identifier i = new Identifier(e1);
            i.identifier = "A";
            e1.left = i;

            Literal l = new Literal(e1);
            l.value = "5";
            e1.right = l;
        }

        BinaryExpr e2 = new BinaryExpr(null);
        {
            e2.operator = BinaryOperator.ADDITION;

            Identifier i = new Identifier(e2);
            i.identifier = "A";
            e2.left = i;

            Literal l = new Literal(e2);
            l.value = "5";
            e2.right = l;
        }

        BinaryExpr e3 = new BinaryExpr(null);
        {
            e3.operator = BinaryOperator.MULTIPLICATION;

            Identifier i = new Identifier(e3);
            i.identifier = "A";
            e3.left = i;

            Literal l = new Literal(e3);
            l.value = "5";
            e3.right = l;
        }

        BinaryExpr e4 = new BinaryExpr(null);
        {
            e4.operator = BinaryOperator.ADDITION;

            Identifier i = new Identifier(e4);
            i.identifier = "B";
            e4.left = i;

            Literal l = new Literal(e4);
            l.value = "5";
            e4.right = l;
        }

        BinaryExpr e5 = new BinaryExpr(null);
        {
            e5.operator = BinaryOperator.ADDITION;

            Identifier i = new Identifier(e5);
            i.identifier = "A";
            e5.left = i;

            Literal l = new Literal(e5);
            l.value = "7";
            e5.right = l;
        }

        BinaryExpr e6 = new BinaryExpr(null);
        {
            e6.operator = BinaryOperator.MULTIPLICATION;

            e6.left = e1;
            e6.right = e2;
        }

        BinaryExpr e7 = new BinaryExpr(null);
        {
            e7.operator = BinaryOperator.ADDITION;

            Identifier i = new Identifier(e7);
            i.identifier = "A";
            e7.right = i;

            Literal l = new Literal(e7);
            l.value = "5";
            e7.left = l;
        }

        BinaryExpr e8 = new BinaryExpr(null);
        {
            e8.operator = BinaryOperator.ARRAY_ACCESS;

            Identifier i = new Identifier(e8);
            i.identifier = "A";
            e8.left = i;

            Literal l = new Literal(e8);
            l.value = "5";
            e8.right = l;
        }

        return new AstElement[] { e1, e2, e3, e4, e5, e6, e7, e8 };
    }

    @Override
    protected boolean equal(int element1, int elemen2) {
        if (element1 == elemen2) {
            return true;
        }
        if (element1 == 0 && elemen2 == 1 || element1 == 1 && elemen2 == 0) {
            return true;
        }
        return false;
    }

    @Override
    protected String getLineString(int element) {
        switch (element) {
        case 0:
            return "A + 5";

        case 1:
            return "A + 5";

        case 2:
            return "A * 5";

        case 3:
            return "B + 5";

        case 4:
            return "A + 7";

        case 5:
            return "(A + 5) * (A + 5)";

        case 6:
            return "5 + A";

        case 7:
            return "A[5]";
        }
        return null;
    }

    @Override
    protected String getPrettyString(int element) {
        return getLineString(element);
    }

}
