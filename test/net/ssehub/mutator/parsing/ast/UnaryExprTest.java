package net.ssehub.mutator.parsing.ast;

public class UnaryExprTest extends AbstractAstElementTest {

    @Override
    protected AstElement[] createElements() {
        UnaryExpr e1 = new UnaryExpr(null);
        {
            e1.operator = UnaryOperator.MINUS;
            
            Identifier i = new Identifier(e1);
            i.identifier = "A";
            
            e1.expr = i;
        }
        
        UnaryExpr e2 = new UnaryExpr(null);
        {
            e2.operator = UnaryOperator.MINUS;
            
            Identifier i = new Identifier(e2);
            i.identifier = "A";
            
            e2.expr = i;
        }
        
        UnaryExpr e3 = new UnaryExpr(null);
        {
            e3.operator = UnaryOperator.MINUS;
            
            Identifier i = new Identifier(e3);
            i.identifier = "B";
            
            e3.expr = i;
        }
        
        UnaryExpr e4 = new UnaryExpr(null);
        {
            e4.operator = UnaryOperator.POST_INC;
            
            Identifier i = new Identifier(e4);
            i.identifier = "A";
            
            e4.expr = i;
        }
        
        UnaryExpr e5 = new UnaryExpr(null);
        {
            e5.operator = UnaryOperator.MINUS;
            
            BinaryExpr nested = new BinaryExpr(e5);
            nested.operator = BinaryOperator.MULTIPLICATION;
            
            Identifier i = new Identifier(nested);
            i.identifier = "A";
            nested.left = i;
            
            Literal l = new Literal(nested);
            l.value = "5";
            nested.right = l;
            
            e5.expr = nested;
        }
        
        
        return new AstElement[] { e1, e2, e3, e4, e5 };
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
        case 0: return "-A";
        case 1: return "-A";
        case 2: return "-B";
        case 3: return "A++";
        case 4: return "-(A * 5)";
        }
        return null;
    }

    @Override
    protected String getPrettyString(int element) {
        return getLineString(element);
    }
    
    

}
