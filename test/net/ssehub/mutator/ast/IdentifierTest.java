package net.ssehub.mutator.ast;

public class IdentifierTest extends AbstractAstElementTest {

    @Override
    protected AstElement[] createElements() {
        Identifier i1 = new Identifier(null);
        {
            i1.identifier = "VAR_1";
        }

        Identifier i2 = new Identifier(null);
        {
            i2.identifier = "VAR_1";
        }

        Identifier i3 = new Identifier(null);
        {
            i3.identifier = "VAR_2";
        }

        return new AstElement[] { i1, i2, i3 };
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
            return "VAR_1";

        case 1:
            return "VAR_1";

        case 2:
            return "VAR_2";
        }
        return null;
    }

    @Override
    protected String getPrettyString(int element) {
        return getLineString(element);
    }

}
