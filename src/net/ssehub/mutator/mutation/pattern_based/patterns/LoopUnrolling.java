package net.ssehub.mutator.mutation.pattern_based.patterns;

import java.util.LinkedList;
import java.util.List;

import net.ssehub.mutator.ast.BasicType;
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
import net.ssehub.mutator.mutation.genetic.mutations.StatementInserter;

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
        return 8;
    }
    
    @Override
    public void apply(int param, File ast) {
        // no modification if param = 1
        if (param > 1) {
            For loop = (For) ast.accept(new IdFinder(this.loopId));
            if (loop != null) {
                // clone now, before, the original loop is modified 
                For remainderLoop = new AstCloner(loop.parent, false).visitFor(loop);
                
                // create a new body block with duplicated loop.body elements
                Block newBody = new Block(loop);
                
                Statement oldBody = loop.body;
                newBody.statements.add(oldBody);
                oldBody.parent = newBody;
                
                for (int i = 1; i < param; i++) {
                    // for each duplication, increase var by one (e.g. i + 1, i + 2, etc.)
                    insertIncrement(loop, newBody);
                    Statement clone = (Statement) oldBody.accept(new AstCloner(newBody, false));
                    newBody.statements.add(clone);
                }
                insertIncrement(loop, newBody);
                
                loop.body = newBody;
                
                // remove the increment, as we inserted the increment statements
                loop.increment = null;
                
                int newIncrement = increment * param;
                
                // decrease the bound
                // the remainder loop will take care of all remaining elements that don't fit the new increment
                decreaseBound(loop, newIncrement - 1);
                
                String newCountVar = null;
                if (loop.init != null) {
                    // introduce new temporary variable for remainder loop
                    // this keeps track of the main loop var, so the remainder loop can finish the job
                    newCountVar = "mutator_tmp_" + (int) (Math.random() * Integer.MAX_VALUE);
                    insertCountDeclaration(newCountVar, loop);
                    insertCountAssigment(newCountVar, newBody);
                }

                // set the correct values for the remainder loop
                // no init, as it uses the newCountVar temporary variable
                remainderLoop.init = null;
                
                if (newCountVar != null) {
                    // replace main loop var with newCountVar
                    remainderLoop.accept(new FullVisitor(new WithIdentifierReplacer(newCountVar)));
                }
                
                // insert the remainder loop after the main loop
                new StatementInserter().insert(loop, false, remainderLoop);
            }
        }
    }
    
    private void insertIncrement(For loop, Block block) {
        ExpressionStmt incStmt = new ExpressionStmt(block);
        
        Expression inc = (Expression) loop.increment.accept(new AstCloner(block, false));
        incStmt.expr = inc;
        
        block.statements.add(incStmt);
    }
    
    private void insertCountDeclaration(String countVarId, For loop) {
        DeclarationStmt stmt = new DeclarationStmt(loop.parent);
        
        Declaration countDecl = new Declaration(stmt);
        Type countType = new Type(countDecl);
        countType.type = BasicType.INT;
        countDecl.type = countType;
        countDecl.identifier = countVarId;
        
        Expression initValue = (Expression) loop.init.initExpr.accept(new AstCloner(countDecl, false));
        countDecl.initExpr = initValue;

        stmt.decl = countDecl;
        
        new StatementInserter().insert(loop, true, stmt);
    }
    
    private void insertCountAssigment(String countVarId, Block body) {
        ExpressionStmt stmt = new ExpressionStmt(body);
        
        BinaryExpr expr = new BinaryExpr(stmt);
        expr.operator = BinaryOperator.ASSIGNMENT;
        
        Identifier left = new Identifier(expr);
        left.identifier = countVarId;
        
        Identifier right = new Identifier(expr);
        right.identifier = var;
        
        expr.left = left;
        expr.right = right;
        
        stmt.expr = expr;
        
        new StatementInserter().insert(body.statements.get(body.statements.size() - 1), false, stmt);
    }
    
    private void decreaseBound(For loop, int bound) {
        BinaryExpr cond = (BinaryExpr) loop.condition;
        
        BinaryExpr subtract = new BinaryExpr(loop);
        
        Literal lit = new Literal(subtract);
        lit.value = Integer.toString(bound);
        
        subtract.operator = BinaryOperator.SUBTRACTION;
        
        subtract.left = cond.right;
        subtract.right = lit;
        cond.right.parent = subtract;
        
        cond.right = subtract;
    }
    
//    private void setNewIncrement(For loop, int increment) {
//        BinaryExpr newIncrementOp = new BinaryExpr(loop);
//        
//        Literal newIncrementStep = new Literal(newIncrementOp);
//        newIncrementStep.value = Integer.toString(Math.abs(increment));
//        
//        Identifier newIncrementVar = new Identifier(newIncrementOp);
//        newIncrementVar.identifier = var;
//        
//        if (increment >= 0) {
//            newIncrementOp.operator = BinaryOperator.ASSIGNMENT_PLUS;
//        } else {
//            newIncrementOp.operator = BinaryOperator.ASSIGNMENT_MINUS;
//        }
//        newIncrementOp.left = newIncrementVar;
//        newIncrementOp.right = newIncrementStep;
//        
//        loop.increment = newIncrementOp;
//    }
    
    private static abstract class AbstractIdentifierReplacer implements IAstVisitor<Void> {

        private String toReplace;
        
        public AbstractIdentifierReplacer(String toReplace) {
            this.toReplace = toReplace;
        }
        
        protected abstract Expression convert(Identifier identifier);
        
        private Expression checkAndConvert(Expression expr) {
            Expression result = expr;
            if (expr instanceof Identifier && ((Identifier) expr).identifier.equals(toReplace)) {
                result = convert((Identifier) expr);
                if (result == null) {
                    result = expr;
                }
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
    
//    private class WithLiteralReplacer extends AbstractIdentifierReplacer {
//
//        private int varAddition;
//        
//        private Set<Long> converted;
//        
//        public WithLiteralReplacer(int varAddition) {
//            super(LoopUnrolling.this.var);
//            this.varAddition = varAddition;
//            this.converted = new HashSet<>();
//        }
//        
//        @Override
//        protected Expression convert(Identifier identifier) {
//            if (!converted.contains(identifier.id)) {
//                BinaryExpr addition = new BinaryExpr(identifier.parent);
//                
//                Literal lit = new Literal(addition);
//                lit.value = Integer.toString(Math.abs(varAddition));
//                if (varAddition >= 0) {
//                    addition.operator = BinaryOperator.ADDITION;
//                } else {
//                    addition.operator = BinaryOperator.SUBTRACTION;
//                }
//                
//                addition.left = identifier;
//                addition.right = lit;
//                
//                identifier.parent = addition;
//                
//                converted.add(identifier.id);
//                
//                return addition;
//            }
//            return null;
//        }
//    }
    
    private class WithIdentifierReplacer extends AbstractIdentifierReplacer {

        private String newIdentifier;
        
        public WithIdentifierReplacer(String newIdentifier) {
            super(LoopUnrolling.this.var);
            this.newIdentifier = newIdentifier;
        }

        @Override
        protected Expression convert(Identifier identifier) {
            Identifier newIdentifier = new Identifier(identifier.parent);
            newIdentifier.identifier = this.newIdentifier;
            return newIdentifier;
        }
        
    }
    
    @Override
    public String toString() {
        return "LoopUnrolling(loop=#" + loopId + "; var=" + var + "; increment=" + increment + ")";
    }

    public static List<LoopUnrolling> findOpportunities(File ast) {
        LoopUnrollingFinder finder = new LoopUnrollingFinder();
        ast.accept(new FullVisitor(finder));
        
        // sort higher IDs first -> inner loops come before outer loops
        finder.opportunities.sort((o1, o2) -> Long.compare(o2.loopId, o1.loopId));
        
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

        private boolean isIntType(Type type) {
            return !type.pointer && type.type == BasicType.INT && type.modifier == null;
        }
        
        private boolean isVariable(Expression expr, String expectedName) {
            return expr instanceof Identifier && ((Identifier) expr).identifier.equals(expectedName);
        }
        
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
            String var = null;
            if (stmt.init != null) {
                if (!isIntType(stmt.init.type)) {
                    return null;
                }
                var = stmt.init.identifier;
            }
            
            // check condition
            boolean conditionFits = false;
            if (stmt.condition != null) {
                if (stmt.condition instanceof BinaryExpr) {
                    BinaryExpr op = (BinaryExpr) stmt.condition;
                    
                    if (var != null) {
                        if (!isVariable(op.left, var)) {
                            return null;
                        }
                    } else {
                        var = getVariable(op.left);
                    }
                    
                    if (var != null) {
                        if (op.operator == BinaryOperator.CMP_LOWER || op.operator == BinaryOperator.CMP_LOWER_EQUAL) {
                            conditionFits = true;
                        }
                    }
                }
            }
            
            // check increment
            Integer increment = null;
            if (conditionFits && stmt.increment != null) {
                if (stmt.increment instanceof UnaryExpr) {
                    UnaryExpr op = (UnaryExpr) stmt.increment;
                    if (op.operator == UnaryOperator.PRE_INC || op.operator == UnaryOperator.POST_INC) {
                        increment = 1;
                    } else if (op.operator == UnaryOperator.PRE_DEC || op.operator == UnaryOperator.POST_DEC) {
                        increment = -1;
                    }
                    
                    if (var != null) {
                        if (!isVariable(op.expr, var)) {
                            return null;
                        }
                    } else {
                        var = getVariable(op.expr);
                    }
                    
                } else if (stmt.increment instanceof BinaryExpr) {
                    BinaryExpr op = (BinaryExpr) stmt.increment;
                    if (op.operator == BinaryOperator.ASSIGNMENT_PLUS) {
                        increment = getConstant(op.right);
                    } else if (op.operator == BinaryOperator.ASSIGNMENT_MINUS) {
                        increment = -1 * getConstant(op.right);
                    }

                    if (var != null) {
                        if (!isVariable(op.left, var)) {
                            return null;
                        }
                    } else {
                        var = getVariable(op.left);
                    }
                }
            }
            
            if (var != null && conditionFits && increment != null) {
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
