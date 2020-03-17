package net.ssehub.mutator.mutation.pattern_based.patterns;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ssehub.mutator.ast.BinaryExpr;
import net.ssehub.mutator.ast.BinaryOperator;
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
import net.ssehub.mutator.ast.Statement;
import net.ssehub.mutator.ast.Type;
import net.ssehub.mutator.ast.UnaryExpr;
import net.ssehub.mutator.ast.UnaryOperator;
import net.ssehub.mutator.ast.While;
import net.ssehub.mutator.ast.operations.AstCloner;
import net.ssehub.mutator.ast.operations.FullVisitor;
import net.ssehub.mutator.ast.operations.IAstVisitor;
import net.ssehub.mutator.ast.operations.IdFinder;

public class LoopUnrolling implements IOpportunity {

    private long loopId;
    
    private String var;
    
    private int increment;
    
    public LoopUnrolling(long loopId, String var, int increment) {
        this.loopId = loopId;
        this.var = var;
        this.increment = increment;
    }
    
    @Override
    public int getMinParam() {
        return 1;
    }
    
    @Override
    public int getDefaultParam() {
        return 1;
    }
    
    @Override
    public int getMaxParam() {
        return 16; // TODO: calc this somehow
    }
    
    @Override
    public void apply(int param, File ast) {
        // no modification if param = 1
        if (param > 1) {
            For loop = (For) ast.accept(new IdFinder(this.loopId));
            if (loop != null) {
                
                Block newBody = new Block(loop);
                
                Statement oldBody = loop.body;
                newBody.statements.add(oldBody);
                oldBody.parent = newBody;
                
                for (int i = 1; i < param; i++) {
                    Statement clone = cloneBody(oldBody, i);
                    newBody.statements.add(clone);
                }
                
                loop.body = newBody;
                
                BinaryExpr newIncrementOp = new BinaryExpr(loop);
                
                Literal newIncrementStep = new Literal(newIncrementOp);
                newIncrementStep.value = Integer.toString(Math.abs(increment * param));
                
                Identifier newIncrementVar = new Identifier(newIncrementOp);
                newIncrementVar.identifier = var;
                
                if (increment >= 0) {
                    newIncrementOp.operator = BinaryOperator.ASSIGNMENT_PLUS;
                } else {
                    newIncrementOp.operator = BinaryOperator.ASSIGNMENT_MINUS;
                }
                newIncrementOp.left = newIncrementVar;
                newIncrementOp.right = newIncrementStep;
                
                loop.increment = newIncrementOp;
                
                // TODO: handle remainder
            }
        }
    }
    
    private Statement cloneBody(Statement body, int varAddition) {
        Statement clone = (Statement) body.accept(new AstCloner(body.parent, false));
        clone.accept(new FullVisitor(new LiteralReplacer(varAddition)));
        return clone;
    }
    
    private class LiteralReplacer implements IAstVisitor<Void> {

        private int varAddition;
        
        private Set<Long> converted;
        
        public LiteralReplacer(int varAddition) {
            this.varAddition = varAddition;
            this.converted = new HashSet<>();
        }
        
        private Expression checkAndConvert(Expression expr) {
            Expression result = expr;
            if (expr instanceof Identifier && !converted.contains(expr.id)
                    && ((Identifier) expr).identifier.equals(var)) {
                BinaryExpr addition = new BinaryExpr(expr.parent);
                
                Literal lit = new Literal(addition);
                lit.value = Integer.toString(Math.abs(varAddition));
                if (varAddition >= 0) {
                    addition.operator = BinaryOperator.ADDITION;
                } else {
                    addition.operator = BinaryOperator.SUBTRACTION;
                }
                
                addition.left = expr;
                addition.right = lit;
                
                expr.parent = addition;
                result = addition;
                
                converted.add(expr.id);
            }
            return result;
        }
        
        @Override
        public Void visitBinaryExpr(BinaryExpr expr) {
            expr.left = checkAndConvert(expr.left);
            expr.right = checkAndConvert(expr.right);
            return null;
        }

        @Override
        public Void visitBlock(Block stmt) {
            return null;
        }

        @Override
        public Void visitDeclaration(Declaration decl) {
            decl.initExpr = checkAndConvert(decl.initExpr);
            return null;
        }

        @Override
        public Void visitDeclarationStmt(DeclarationStmt stmt) {
            return null;
        }

        @Override
        public Void visitDoWhileLoop(DoWhileLoop stmt) {
            stmt.condition = checkAndConvert(stmt.condition);
            return null;
        }

        @Override
        public Void visitEmptyStmt(EmptyStmt stmt) {
            return null;
        }

        @Override
        public Void visitExpressionStmt(ExpressionStmt stmt) {
            stmt.expr = checkAndConvert(stmt.expr);
            return null;
        }

        @Override
        public Void visitFile(File file) {
            return null;
        }

        @Override
        public Void visitFor(For stmt) {
            if (stmt.condition != null) {
                stmt.condition = checkAndConvert(stmt.condition);
            }
            if (stmt.increment != null) {
                stmt.increment = checkAndConvert(stmt.increment);
            }
            return null;
        }

        @Override
        public Void visitFunction(Function func) {
            return null;
        }

        @Override
        public Void visitFunctionCall(FunctionCall expr) {
            for (int i = 0; i < expr.params.size(); i++) {
                expr.params.set(i, checkAndConvert(expr.params.get(i)));
            }
            return null;
        }

        @Override
        public Void visitIdentifier(Identifier expr) {
            return null;
        }

        @Override
        public Void visitIf(If stmt) {
            stmt.condition = checkAndConvert(stmt.condition);
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
            if (stmt.value != null) {
                stmt.value = checkAndConvert(stmt.value);
            }
            return null;
        }

        @Override
        public Void visitType(Type type) {
            return null;
        }

        @Override
        public Void visitUnaryExpr(UnaryExpr expr) {
            expr.expr = checkAndConvert(expr.expr);
            return null;
        }

        @Override
        public Void visitWhile(While stmt) {
            stmt.condition = checkAndConvert(stmt.condition);
            return null;
        }
        
    }
    
    @Override
    public String toString() {
        return "LoopUnrolling(loop=#" + loopId + "; var=" + var + "; increment=" + increment + ")";
    }

    public static List<LoopUnrolling> findOpportunities(File ast) {
        LoopUnrollingFinder finder = new LoopUnrollingFinder();
        ast.accept(new FullVisitor(finder));
        return finder.opportunities;
    }
    
    private static class LoopUnrollingFinder implements IAstVisitor<Void> {

        private List<LoopUnrolling> opportunities = new LinkedList<>();
        
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

//        private boolean isIntType(Type type) {
//            return !type.pointer && type.type == BasicType.INT && type.modifier == null;
//        }
//        
//        private boolean isVariable(Expression expr, String expectedName) {
//            return expr instanceof Identifier && ((Identifier) expr).identifier.equals(expectedName);
//        }
        
        private String getVariable(Expression expr) {
            if (expr instanceof Identifier) {
                return ((Identifier) expr).identifier;
            }
            return null;
        }
        
        private Integer getConstant(Expression expr) {
            Integer result = null;
            
            if (expr != null && expr instanceof Literal) {
                try {
                    result = Integer.parseInt(((Literal) expr).value);
                } catch (NumberFormatException e) {}
            }
            // TODO: unary minus, or more generally: any constant (evaluated)
            
            return result;
        }
        
        @Override
        public Void visitFor(For stmt) {
            // check init
//            String var = null;
//            Integer start = null;
//            if (stmt.init != null && isIntType(stmt.init.type)) {
//                var = stmt.init.identifier;
//                start = getConstant(stmt.init.initExpr);
//            }
            
            // check condition
//            Integer end = null;
//            if (var != null && stmt.condition != null) {
//                if (stmt.condition instanceof BinaryExpr) {
//                    BinaryExpr op = (BinaryExpr) stmt.condition;
//                    if (isVariable(op.left, var)) {
//                        if (op.operator == BinaryOperator.CMP_LOWER) {
//                                end = getConstant(op.right);
//                        }
//                        // TODO: other comparison operators
//                    }
//                }
//            }
            
            // check increment
            String var = null;
            Integer increment = null;
            if (stmt.increment != null) {
                if (stmt.increment instanceof UnaryExpr) {
                    UnaryExpr op = (UnaryExpr) stmt.increment;
                    
                    var = getVariable(op.expr);
                    if (var != null) {
                        if (op.operator == UnaryOperator.PRE_INC || op.operator == UnaryOperator.POST_INC) {
                            increment = 1;
                        } else if (op.operator == UnaryOperator.PRE_DEC || op.operator == UnaryOperator.POST_DEC) {
                            increment = -1;
                        }
                    }
                } else if (stmt.increment instanceof BinaryExpr) {
                    BinaryExpr op = (BinaryExpr) stmt.increment;

                    var = getVariable(op.left);
                    if (var != null) {
                        if (op.operator == BinaryOperator.ASSIGNMENT_PLUS) {
                            increment = getConstant(op.right);
                        } else if (op.operator == BinaryOperator.ASSIGNMENT_MINUS) {
                            increment = -1 * getConstant(op.right);
                        }
                    }
                }
            }
            
            if (var != null && increment != null) {
                opportunities.add(new LoopUnrolling(stmt.id, var, increment));
            }
            
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
