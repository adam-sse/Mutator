package net.ssehub.mutator.parsing.ast;

public class DeclarationStmtTest extends AbstractAstElementTest {

    @Override
    protected AstElement[] createElements() {
        DeclarationStmt s1 = new DeclarationStmt(null);
        {
            Declaration decl = new Declaration(s1);
            decl.identifier = "A";
            
            Type t = new Type(decl);
            t.type = BasicType.INT;
            decl.type = t;
            
            s1.decl = decl;
        }
        
        DeclarationStmt s2 = new DeclarationStmt(null);
        {
            Declaration decl = new Declaration(s2);
            decl.identifier = "A";
            
            Type t = new Type(decl);
            t.type = BasicType.INT;
            decl.type = t;
            
            s2.decl = decl;
        }
        
        DeclarationStmt s3 = new DeclarationStmt(null);
        {
            Declaration decl = new Declaration(s3);
            decl.identifier = "other";
            
            Type t = new Type(decl);
            t.type = BasicType.INT;
            decl.type = t;
            
            s3.decl = decl;
        }
        
        DeclarationStmt s4 = new DeclarationStmt(null);
        {
            Declaration decl = new Declaration(s3);
            decl.identifier = "A";
            
            Type t = new Type(decl);
            t.type = BasicType.LONG_DOUBLE;
            t.pointer = true;
            decl.type = t;
            
            s4.decl = decl;
        }
        
        return new AstElement[] { s1, s2, s3, s4 };
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
        case 0: return "int A;";
        case 1: return "int A;";
        case 2: return "int other;";
        case 3: return "long double* A;";
        }
        return null;
    }

    @Override
    protected String getPrettyString(int element) {
        return getLineString(element) + "\n";
    }
    
    

}
