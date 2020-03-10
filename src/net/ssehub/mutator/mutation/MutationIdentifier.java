package net.ssehub.mutator.mutation;

import net.ssehub.mutator.parsing.ast.Assignment;
import net.ssehub.mutator.parsing.ast.AstElement;
import net.ssehub.mutator.parsing.ast.BinaryExpr;
import net.ssehub.mutator.parsing.ast.Block;
import net.ssehub.mutator.parsing.ast.Declaration;
import net.ssehub.mutator.parsing.ast.DeclarationStmt;
import net.ssehub.mutator.parsing.ast.DoWhileLoop;
import net.ssehub.mutator.parsing.ast.EmptyStmt;
import net.ssehub.mutator.parsing.ast.ExpressionStmt;
import net.ssehub.mutator.parsing.ast.File;
import net.ssehub.mutator.parsing.ast.Function;
import net.ssehub.mutator.parsing.ast.FunctionCall;
import net.ssehub.mutator.parsing.ast.Identifier;
import net.ssehub.mutator.parsing.ast.If;
import net.ssehub.mutator.parsing.ast.Literal;
import net.ssehub.mutator.parsing.ast.Return;
import net.ssehub.mutator.parsing.ast.Type;
import net.ssehub.mutator.parsing.ast.UnaryExpr;
import net.ssehub.mutator.parsing.ast.While;
import net.ssehub.mutator.parsing.ast.operations.FullVisitor;
import net.ssehub.mutator.parsing.ast.operations.IAstVisitor;

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
    
    private class Finder implements IAstVisitor<Void> {

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
        public Void visitAssignment(Assignment stmt) {
            check(stmt);
            return null;
        }

        @Override
        public Void visitBinaryExpr(BinaryExpr expr) {
            check(expr);
            return null;
        }

        @Override
        public Void visitBlock(Block stmt) { 
            check(stmt);
            return null;
        }

        @Override
        public Void visitDeclaration(Declaration decl) {
            check(decl);
            return null;
        }

        @Override
        public Void visitDeclarationStmt(DeclarationStmt stmt) {
            check(stmt);
            return null;
        }

        @Override
        public Void visitDoWhileLoop(DoWhileLoop stmt) {
            check(stmt);
            return null;
        }

        @Override
        public Void visitEmptyStmt(EmptyStmt stmt) {
            check(stmt);
            return null;
        }

        @Override
        public Void visitExpressionStmt(ExpressionStmt stmt) {
            check(stmt);
            return null;
        }

        @Override
        public Void visitFile(File file) {
            check(file);
            return null;
        }

        @Override
        public Void visitFunction(Function func) {
            check(func);
            return null;
        }

        @Override
        public Void visitFunctionCall(FunctionCall expr) {
            check(expr);
            return null;
        }

        @Override
        public Void visitIdentifier(Identifier expr) {
            check(expr);
            return null;
        }

        @Override
        public Void visitIf(If stmt) {
            check(stmt);
            return null;
        }

        @Override
        public Void visitLiteral(Literal expr) {
            check(expr);
            return null;
        }

        @Override
        public Void visitReturn(Return stmt) {
            check(stmt);
            return null;
        }

        @Override
        public Void visitType(Type type) {
            check(type);
            return null;
        }

        @Override
        public Void visitUnaryExpr(UnaryExpr expr) {
            check(expr);
            return null;
        }

        @Override
        public Void visitWhile(While stmt) {
            check(stmt);
            return null;
        }
        
    }
    
}