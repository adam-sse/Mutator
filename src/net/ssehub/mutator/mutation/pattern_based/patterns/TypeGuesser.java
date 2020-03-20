package net.ssehub.mutator.mutation.pattern_based.patterns;

import java.util.HashMap;
import java.util.Map;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.BasicType;
import net.ssehub.mutator.ast.BinaryExpr;
import net.ssehub.mutator.ast.Block;
import net.ssehub.mutator.ast.Declaration;
import net.ssehub.mutator.ast.DeclarationStmt;
import net.ssehub.mutator.ast.DoWhileLoop;
import net.ssehub.mutator.ast.EmptyStmt;
import net.ssehub.mutator.ast.Expression;
import net.ssehub.mutator.ast.ExpressionStmt;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.For;
import net.ssehub.mutator.ast.Function;
import net.ssehub.mutator.ast.FunctionCall;
import net.ssehub.mutator.ast.Identifier;
import net.ssehub.mutator.ast.If;
import net.ssehub.mutator.ast.JumpStmt;
import net.ssehub.mutator.ast.Literal;
import net.ssehub.mutator.ast.Return;
import net.ssehub.mutator.ast.Type;
import net.ssehub.mutator.ast.UnaryExpr;
import net.ssehub.mutator.ast.While;
import net.ssehub.mutator.ast.operations.FullVisitor;
import net.ssehub.mutator.ast.operations.IAstVisitor;
import net.ssehub.mutator.util.Logger;

public class TypeGuesser {
    
    private static final Logger LOGGER = Logger.get("TypeGuesser");

    public BasicType guessType(Expression expression) {
        Function func = getParentFunction(expression);
        
        DeclTypeBuilder typeBuilder = new DeclTypeBuilder();
        func.accept(new FullVisitor(typeBuilder));
        
        return expression.accept(new TypeEvaluator(typeBuilder.types));
    }
    
    private Function getParentFunction(AstElement element) {
        if (element instanceof Function) {
            return (Function) element;
        } else {
            return getParentFunction(element.parent);
        }
    }
    
    private static class TypeEvaluator implements IAstVisitor<BasicType> {
        
        private Map<String, BasicType> varTypes;
        
        public TypeEvaluator(Map<String, BasicType> varTypes) {
            this.varTypes = varTypes;
        }

        @Override
        public BasicType visitBinaryExpr(BinaryExpr expr) {
            BasicType left = expr.left.accept(this);
            BasicType right = expr.right.accept(this);
            
            return left.ordinal() > right.ordinal() ? left : right;
        }

        @Override
        public BasicType visitBlock(Block stmt) {
            return null;
        }

        @Override
        public BasicType visitDeclaration(Declaration decl) {
            return null;
        }

        @Override
        public BasicType visitDeclarationStmt(DeclarationStmt stmt) {
            return null;
        }

        @Override
        public BasicType visitDoWhileLoop(DoWhileLoop stmt) {
            return null;
        }

        @Override
        public BasicType visitEmptyStmt(EmptyStmt stmt) {
            return null;
        }

        @Override
        public BasicType visitExpressionStmt(ExpressionStmt stmt) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public BasicType visitFile(File file) {
            return null;
        }

        @Override
        public BasicType visitFor(For stmt) {
            return null;
        }

        @Override
        public BasicType visitFunction(Function func) {
            return null;
        }

        @Override
        public BasicType visitFunctionCall(FunctionCall expr) {
            LOGGER.println("Warning: TODO: handle functions");
            // TODO: handle functions
            return BasicType.DOUBLE;
        }

        @Override
        public BasicType visitIdentifier(Identifier expr) {
            BasicType type = varTypes.get(expr.identifier);
            if (type == null) {
                LOGGER.println("Warning: found variable with unknown type: " + expr.identifier);
                type = BasicType.INT;
            }
            return type;
        }

        @Override
        public BasicType visitIf(If stmt) {
            return null;
        }

        @Override
        public BasicType visitJumpStmt(JumpStmt stmt) {
            return null;
        }

        @Override
        public BasicType visitLiteral(Literal expr) {
            return expr.value.contains(".") ? BasicType.DOUBLE : BasicType.INT;
        }

        @Override
        public BasicType visitReturn(Return stmt) {
            return null;
        }

        @Override
        public BasicType visitType(Type type) {
            return null;
        }

        @Override
        public BasicType visitUnaryExpr(UnaryExpr expr) {
            return expr.expr.accept(this);
        }

        @Override
        public BasicType visitWhile(While stmt) {
            return null;
        }
        
    }
    
    private static class DeclTypeBuilder implements IAstVisitor<Void> {

        private Map<String, BasicType> types = new HashMap<>();
        
        @Override
        public Void visitBinaryExpr(BinaryExpr expr) {
            return null;
        }

        @Override
        public Void visitBlock(Block stmt) {
            return null;
        }

        @Override
        public Void visitDeclaration(Declaration decl) {
            BasicType previous = types.get(decl.identifier);
            if (previous == null) {
                types.put(decl.identifier, decl.type.type);
            } else if (previous != decl.type.type) {
                LOGGER.println("Warning: conflicting types for variable " + decl.identifier + ": " 
                        + previous + " and " + decl.type.type);
                
                // use double over int
                types.put(decl.identifier, previous.ordinal() > decl.type.type.ordinal() ? previous : decl.type.type);
            }
            
            return null;
        }

        @Override
        public Void visitDeclarationStmt(DeclarationStmt stmt) {
            return null;
        }

        @Override
        public Void visitDoWhileLoop(DoWhileLoop stmt) {
            return null;
        }

        @Override
        public Void visitEmptyStmt(EmptyStmt stmt) {
            return null;
        }

        @Override
        public Void visitExpressionStmt(ExpressionStmt stmt) {
            return null;
        }

        @Override
        public Void visitFile(File file) {
            return null;
        }

        @Override
        public Void visitFor(For stmt) {
            return null;
        }

        @Override
        public Void visitFunction(Function func) {
            return null;
        }

        @Override
        public Void visitFunctionCall(FunctionCall expr) {
            return null;
        }

        @Override
        public Void visitIdentifier(Identifier expr) {
            return null;
        }

        @Override
        public Void visitIf(If stmt) {
            return null;
        }

        @Override
        public Void visitJumpStmt(JumpStmt stmt) {
            return null;
        }

        @Override
        public Void visitLiteral(Literal expr) {
            return null;
        }

        @Override
        public Void visitReturn(Return stmt) {
            return null;
        }

        @Override
        public Void visitType(Type type) {
            return null;
        }

        @Override
        public Void visitUnaryExpr(UnaryExpr expr) {
            return null;
        }

        @Override
        public Void visitWhile(While stmt) {
            return null;
        }
        
    }
    
}
