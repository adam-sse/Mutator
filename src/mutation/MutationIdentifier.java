package mutation;

import parsing.ast.Assignment;
import parsing.ast.AstElement;
import parsing.ast.BinaryExpr;
import parsing.ast.Block;
import parsing.ast.Declaration;
import parsing.ast.DeclarationStmt;
import parsing.ast.DoWhileLoop;
import parsing.ast.EmptyStmt;
import parsing.ast.ExpressionStmt;
import parsing.ast.File;
import parsing.ast.FullVisitor;
import parsing.ast.Function;
import parsing.ast.FunctionCall;
import parsing.ast.IAstVisitor;
import parsing.ast.Identifier;
import parsing.ast.If;
import parsing.ast.Literal;
import parsing.ast.Return;
import parsing.ast.Type;
import parsing.ast.UnaryExpr;
import parsing.ast.While;

public class MutationIdentifier {

    private long id;
    
    public MutationIdentifier(AstElement element) {
        this.id = element.id;
    }
    
    public MutationIdentifier(long id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        return "#" + id;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof MutationIdentifier) {
            MutationIdentifier other = (MutationIdentifier) obj;
            equal = this.id == other.id;
        }
        
        return equal;
    }
    
    public AstElement find(AstElement ast) {
        Finder finder = new Finder();
        ast.accept(new FullVisitor(finder));
        
        AstElement result = null;
        if (finder.match != null && !finder.multipleMatch) {
            result = finder.match;
        }
        
        return result;
    }
    
    private class Finder implements IAstVisitor {

        private AstElement match;
        
        private boolean multipleMatch;
        
        private void check(AstElement ast) {
            if (ast.id == id) {
                if (match != null) {
                    multipleMatch = true;
                }
                match = ast;
            }
        }
        
        @Override
        public void visitAssignment(Assignment stmt) {
            check(stmt);
        }

        @Override
        public void visitBinaryExpr(BinaryExpr expr) {
            check(expr);
        }

        @Override
        public void visitBlock(Block stmt) { 
            check(stmt);
        }

        @Override
        public void visitDeclaration(Declaration decl) {
            check(decl);
        }

        @Override
        public void visitDeclarationStmt(DeclarationStmt stmt) {
            check(stmt);
        }

        @Override
        public void visitDoWhileLoop(DoWhileLoop stmt) {
            check(stmt);
        }

        @Override
        public void visitEmptyStmt(EmptyStmt stmt) {
            check(stmt);
        }

        @Override
        public void visitExpressionStmt(ExpressionStmt stmt) {
            check(stmt);
        }

        @Override
        public void visitFile(File file) {
            check(file);
        }

        @Override
        public void visitFunction(Function func) {
            check(func);
        }

        @Override
        public void visitFunctionCall(FunctionCall expr) {
            check(expr);
        }

        @Override
        public void visitIdentifier(Identifier expr) {
            check(expr);
        }

        @Override
        public void visitIf(If stmt) {
            check(stmt);
        }

        @Override
        public void visitLiteral(Literal expr) {
            check(expr);
        }

        @Override
        public void visitReturn(Return stmt) {
            check(stmt);
        }

        @Override
        public void visitType(Type type) {
            check(type);
        }

        @Override
        public void visitUnaryExpr(UnaryExpr expr) {
            check(expr);
        }

        @Override
        public void visitWhile(While stmt) {
            check(stmt);
        }
        
    }
    
}
