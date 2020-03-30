package net.ssehub.mutator.ast;

public class FunctionCallTest extends AbstractAstElementTest {

    @Override
    protected AstElement[] createElements() {
        FunctionCall c1 = new FunctionCall(null);
        {
            c1.function = "some_func";
        }

        FunctionCall c2 = new FunctionCall(null);
        {
            c2.function = "some_func";
        }

        FunctionCall c3 = new FunctionCall(null);
        {
            c3.function = "some_func";

            Literal lit = new Literal(c3);
            lit.value = "5";

            c3.params.add(lit);
        }

        FunctionCall c4 = new FunctionCall(null);
        {
            c4.function = "some_func";

            Literal lit = new Literal(c4);
            lit.value = "5";

            c4.params.add(lit);
        }

        FunctionCall c5 = new FunctionCall(null);
        {
            c5.function = "some_other_func";
        }

        FunctionCall c6 = new FunctionCall(null);
        {
            c6.function = "some_other_func";

            Literal lit = new Literal(c6);
            lit.value = "5";

            c6.params.add(lit);

            Identifier i = new Identifier(c6);
            i.identifier = "VAR";

            c6.params.add(i);
        }

        return new AstElement[] { c1, c2, c3, c4, c5, c6 };
    }

    @Override
    protected boolean equal(int element1, int elemen2) {
        if (element1 == elemen2) {
            return true;
        }
        if (element1 == 0 && elemen2 == 1 || element1 == 1 && elemen2 == 0) {
            return true;
        }
        if (element1 == 2 && elemen2 == 3 || element1 == 3 && elemen2 == 2) {
            return true;
        }
        return false;
    }

    @Override
    protected String getLineString(int element) {
        switch (element) {
        case 0:
            return "some_func()";

        case 1:
            return "some_func()";

        case 2:
            return "some_func(5)";

        case 3:
            return "some_func(5)";

        case 4:
            return "some_other_func()";

        case 5:
            return "some_other_func(5, VAR)";
        }
        return null;
    }

    @Override
    protected String getPrettyString(int element) {
        return getLineString(element);
    }

}
