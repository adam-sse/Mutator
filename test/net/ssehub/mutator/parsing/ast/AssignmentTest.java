package net.ssehub.mutator.parsing.ast;

public class AssignmentTest extends AbstractAstElementTest {

    @Override
    protected AstElement[] createElements() {
        Assignment a1 = new Assignment(null);
        
        {
            Identifier var1 = new Identifier(a1);
            var1.identifier = "VAR_1";
            
            Literal val1 = new Literal(a1);
            val1.value = "5";
            
            a1.variable = var1;
            a1.value = val1;
        }
        
        Assignment a2 = new Assignment(null);
        {
            Identifier var2 = new Identifier(a2);
            var2.identifier = "VAR_2";
            
            Literal val2 = new Literal(a2);
            val2.value = "5";
            
            a2.variable = var2;
            a2.value = val2;
        }
        
        Assignment a3 = new Assignment(null);
        {
            Identifier var3 = new Identifier(a3);
            var3.identifier = "VAR_1";
            
            Literal val3 = new Literal(a3);
            val3.value = "5";
            
            a3.variable = var3;
            a3.value = val3;
        }
        
        Assignment a4 = new Assignment(null);
        {
            Identifier var4 = new Identifier(a4);
            var4.identifier = "VAR_1";
            
            Literal val4 = new Literal(a4);
            val4.value = "6";
            
            a4.variable = var4;
            a4.value = val4;
        }
        
        return new AstElement[] { a1, a2, a3, a4 };
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
        case 0: return "VAR_1 = 5;";
        case 1: return "VAR_2 = 5;";
        case 2: return "VAR_1 = 5;";
        case 3: return "VAR_1 = 6;";
        }
        return null;
    }

    @Override
    protected String getPrettyString(int element) {
        return getLineString(element) + "\n";
    }
    
    

}
