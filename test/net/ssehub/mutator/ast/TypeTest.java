package net.ssehub.mutator.ast;

import net.ssehub.mutator.ast.Type.Modifier;

public class TypeTest extends AbstractAstElementTest {

    @Override
    protected AstElement[] createElements() {
        Type t1 = new Type(null);
        {
            t1.modifier = null;
            t1.pointer = false;
            t1.type = BasicType.INT;
        }

        Type t2 = new Type(null);
        {
            t2.modifier = null;
            t2.pointer = false;
            t2.type = BasicType.INT;
        }

        Type t3 = new Type(null);
        {
            t3.modifier = Modifier.SIGNED;
            t3.pointer = false;
            t3.type = BasicType.INT;
        }

        Type t4 = new Type(null);
        {
            t4.modifier = null;
            t4.pointer = true;
            t4.type = BasicType.INT;
        }

        Type t5 = new Type(null);
        {
            t5.modifier = Modifier.UNSIGNED;
            t5.pointer = true;
            t5.type = BasicType.CHAR;
        }

        return new AstElement[] { t1, t2, t3, t4, t5 };
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
            return "int";

        case 1:
            return "int";

        case 2:
            return "signed int";

        case 3:
            return "int*";

        case 4:
            return "unsigned char*";
        }
        return null;
    }

    @Override
    protected String getPrettyString(int element) {
        return getLineString(element);
    }

}
