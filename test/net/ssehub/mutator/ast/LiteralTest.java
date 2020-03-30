package net.ssehub.mutator.ast;

public class LiteralTest extends AbstractAstElementTest {

    @Override
    protected AstElement[] createElements() {
        Literal l1 = new Literal(null);
        {
            l1.value = "15";
        }

        Literal l2 = new Literal(null);
        {
            l2.value = "15";
        }

        Literal l3 = new Literal(null);
        {
            l3.value = "3154";
        }

        return new AstElement[] { l1, l2, l3 };
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
            return "15";

        case 1:
            return "15";

        case 2:
            return "3154";
        }
        return null;
    }

    @Override
    protected String getPrettyString(int element) {
        return getLineString(element);
    }

}
