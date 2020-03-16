package net.ssehub.mutator.ast;

public class DeclarationTest extends AbstractAstElementTest {

    @Override
    protected AstElement[] createElements() {
        Declaration d1 = new Declaration(null);
        {
            d1.identifier = "VAR_1";
            
            Type t = new Type(d1);
            t.modifier = null;
            t.pointer = false;
            t.type = BasicType.INT;
            
            d1.type = t;
        }
        
        Declaration d2 = new Declaration(null);
        {
            d2.identifier = "VAR_2";
            
            Type t = new Type(d2);
            t.modifier = null;
            t.pointer = false;
            t.type = BasicType.INT;
            
            d2.type = t;
        }
        
        Declaration d3 = new Declaration(null);
        {
            d3.identifier = "VAR_1";
            
            Type t = new Type(d3);
            t.modifier = null;
            t.pointer = false;
            t.type = BasicType.INT;
            
            d3.type = t;
        }
        
        Declaration d4 = new Declaration(null);
        {
            d4.identifier = "VAR_2";
            
            Type t = new Type(d4);
            t.modifier = null;
            t.pointer = false;
            t.type = BasicType.DOUBLE;
            
            d4.type = t;
        }
        
        
        return new AstElement[] { d1, d2, d3, d4 };
    }

    @Override
    protected boolean equal(int element1, int elemen2) {
        if (element1 == elemen2) {
            return true;
        }
        if (element1 == 0 && elemen2 == 2 || element1 == 2 && elemen2 == 0) {
            return true;
        }
        return false;
    }

    @Override
    protected String getLineString(int element) {
        switch (element) {
        case 0: return "int VAR_1";
        case 1: return "int VAR_2";
        case 2: return "int VAR_1";
        case 3: return "double VAR_2";
        }
        return null;
    }

    @Override
    protected String getPrettyString(int element) {
        return getLineString(element);
    }
    
    

}
