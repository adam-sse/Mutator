package net.ssehub.mutator.mutation.pattern_based.patterns;

import java.util.HashMap;
import java.util.Map;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.BasicType;
import net.ssehub.mutator.ast.BinaryExpr;
import net.ssehub.mutator.ast.Declaration;
import net.ssehub.mutator.ast.Expression;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.Function;
import net.ssehub.mutator.ast.FunctionCall;
import net.ssehub.mutator.ast.FunctionDecl;
import net.ssehub.mutator.ast.Identifier;
import net.ssehub.mutator.ast.Literal;
import net.ssehub.mutator.ast.UnaryExpr;
import net.ssehub.mutator.ast.operations.FullVisitor;
import net.ssehub.mutator.ast.operations.IExpressionVisitor;
import net.ssehub.mutator.ast.operations.SingleOperationVisitor;
import net.ssehub.mutator.util.Logger;

public class TypeGuesser {
    
    private static final Logger LOGGER = Logger.get("TypeGuesser");

    public BasicType guessType(Expression expression) {
        Function func = getParentFunction(expression);
        
        File file = getParentFile(func);
        
        Map<String, BasicType> typeFunctions = new HashMap<>(file.functions.size());
        for (AstElement f : file.functions) {
            FunctionDecl decl;
            if (f instanceof Function) {
                decl = ((Function) f).header;
            } else {
                decl = (FunctionDecl) f;
            }
            typeFunctions.put(decl.name, decl.type.type);
        }
        
        DeclTypeBuilder typeBuilder = new DeclTypeBuilder();
        func.accept(new FullVisitor(typeBuilder));
        
        return expression.accept(new TypeEvaluator(typeBuilder.types, typeFunctions));
    }
    
    private Function getParentFunction(AstElement element) {
        if (element instanceof Function) {
            return (Function) element;
        } else {
            return getParentFunction(element.parent);
        }
    }
    
    private File getParentFile(AstElement element) {
        if (element instanceof File) {
            return (File) element;
        } else {
            return getParentFile(element.parent);
        }
    }
    
    private static class TypeEvaluator implements IExpressionVisitor<BasicType> {
        
        private Map<String, BasicType> varTypes;
        
        private Map<String, BasicType> funcTypes;
        
        public TypeEvaluator(Map<String, BasicType> varTypes, Map<String, BasicType> funcTypes) {
            this.varTypes = varTypes;
            this.funcTypes = funcTypes;
        }

        @Override
        public BasicType visitBinaryExpr(BinaryExpr expr) {
            BasicType result;
            
            switch (expr.operator) {
            case ADDITION:
            case SUBTRACTION:
            case MULTIPLICATION:
            case DIVISION:
            case MODULO:
            case BIT_AND:
            case BIT_OR:
            case BIT_XOR:
            {
                BasicType left = expr.left.accept(this);
                BasicType right = expr.right.accept(this);
                result = left.ordinal() > right.ordinal() ? left : right;
            } break;
            
            case CMP_EQUAL:
            case CMP_NOT_EQUAL:
            case CMP_GREATER:
            case CMP_GREATER_EQUAL:
            case CMP_LOWER:
            case CMP_LOWER_EQUAL:
            case AND:
            case OR:
                result = BasicType.INT;
                break;

            case ARRAY_ACCESS:
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
            case ASSIGNMENT:
            case ASSIGNMENT_PLUS:
            case ASSIGNMENT_MINUS:
            case ASSIGNMENT_MULT:
            case ASSIGNMENT_DIV:
            case ASSIGNMENT_MOD:
            case ASSIGNMENT_SHL:
            case ASSIGNMENT_SHR:
            case ASSIGNMENT_AND:
            case ASSIGNMENT_OR:
            case ASSIGNMENT_XOR:
                result = expr.left.accept(this);
                break;
                
            default: { // can't happen
                BasicType left = expr.left.accept(this);
                BasicType right = expr.right.accept(this);
                result = left.ordinal() > right.ordinal() ? left : right;
            }
            }
            
            return result;
        }

        @Override
        public BasicType visitFunctionCall(FunctionCall expr) {
            BasicType type = funcTypes.get(expr.function);
            if (type == null) {
                LOGGER.println("Warning: found function with unknown type: " + expr.function + "; assuming double");
                type = BasicType.DOUBLE;
            }
            return type;
        }

        @Override
        public BasicType visitIdentifier(Identifier expr) {
            BasicType type = varTypes.get(expr.identifier);
            if (type == null) {
                LOGGER.println("Warning: found variable with unknown type: " + expr.identifier + "; assuming int");
                type = BasicType.INT;
            }
            return type;
        }

        @Override
        public BasicType visitLiteral(Literal expr) {
            return expr.value.contains(".") ? BasicType.DOUBLE : BasicType.INT;
        }
        
        @Override
        public BasicType visitUnaryExpr(UnaryExpr expr) {
            BasicType result;
            
            switch (expr.operator) {
            case MINUS:
            case BIT_NEGATION:
            case POST_DEC:
            case POST_INC:
            case PRE_DEC:
            case PRE_INC:
                result = expr.expr.accept(this);
                break;
                
            case NEGATION:
                result = BasicType.INT;
                break;
                
            default: // can't happen
                result = expr.expr.accept(this);
            }
            
            return result;
        }

    }
    
    private static class DeclTypeBuilder extends SingleOperationVisitor<Void> {

        private Map<String, BasicType> types = new HashMap<>();
        
        @Override
        protected Void visit(AstElement element) {
            if (element instanceof Declaration) {
                Declaration decl = (Declaration) element;
                BasicType previous = types.get(decl.identifier);
                if (previous == null) {
                    types.put(decl.identifier, decl.type.type);
                } else if (previous != decl.type.type) {
                    LOGGER.println("Warning: conflicting types for variable " + decl.identifier + ": " 
                            + previous + " and " + decl.type.type);
                    
                    // use double over int
                    types.put(decl.identifier, previous.ordinal() > decl.type.type.ordinal() ? previous : decl.type.type);
                }
            }
            return null;
        }
        
    }
    
}
