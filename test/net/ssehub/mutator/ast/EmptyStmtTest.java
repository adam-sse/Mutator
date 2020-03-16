package net.ssehub.mutator.ast;

public class EmptyStmtTest extends AbstractAstElementTest {

    @Override
    protected AstElement[] createElements() {
        EmptyStmt s1 = new EmptyStmt(null);
        EmptyStmt s2 = new EmptyStmt(null);
        
        return new AstElement[] { s1, s2 };
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
        case 0: return ";";
        case 1: return ";";
        }
        return null;
    }

    @Override
    protected String getPrettyString(int element) {
        return getLineString(element) + "\n";
    }
    
    

}
