package net.ssehub.mutator.ast.control_flow;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

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
import net.ssehub.mutator.ast.Literal;
import net.ssehub.mutator.ast.Return;
import net.ssehub.mutator.ast.Statement;
import net.ssehub.mutator.ast.Type;
import net.ssehub.mutator.ast.UnaryExpr;
import net.ssehub.mutator.ast.While;
import net.ssehub.mutator.ast.operations.IAstVisitor;

public class ControlFlowCreator {

    public List<ControlFlowFunction> createControlFlow(File ast) {
        List<ControlFlowFunction> result = new LinkedList<>();
        
        for (Function func : ast.functions) {
            result.add(createControlFlow(func));
        }
        
        return result;
    }
    
    public ControlFlowFunction createControlFlow(Function ast) {
        String header = ast.type.getText() + " " + ast.name;
        StringJoiner sj = new StringJoiner(", ", "(", ")");
        for (Declaration param : ast.parameters) {
            sj.add(param.getText());
        }
        header += sj.toString();
        
        ControlFlowFunction result = new ControlFlowFunction(ast.name, header);
        
        ControlFlowVisitor visitor = new ControlFlowVisitor(result);
        ast.body.accept(visitor);
        visitor.finish();

        removeEmptyBlocks(result);
        
        return result;
    }
    
    private static void removeEmptyBlocks(ControlFlowFunction func) {
        
        for (ControlFlowBlock block : new HashSet<>(func.getAllBlocks())) {
            
            if (block != func.getStartBlock() && block != func.getEndBlock()
                        && block.getSequence().isEmpty() && block.getOutFalse() == null) {
                
                for (ControlFlowBlock in : new HashSet<>(block.getIncoming())) {
                    if (in.getOutTrue() == block) {
                        in.setOutTrue(block.getOutTrue());
                    } else {
                        in.setOutFalse(block.getOutTrue());
                    }
                }
                
                func.removeBlock(block);
            }
            
        }
        
    }
    
    private static class ControlFlowVisitor implements IAstVisitor<Void> {

        private ControlFlowFunction func;
        
        private ControlFlowBlock currentBlock;
        
        public ControlFlowVisitor(ControlFlowFunction func) {
            this.func = func;
            this.currentBlock = func.createBlock();
            func.getStartBlock().setOutTrue(this.currentBlock);
        }
        
        public void finish() {
            if (currentBlock != null) {
                currentBlock.setOutTrue(func.getEndBlock());
            }
        }

        /**
         * If {@link #currentBlock} is <code>null</code>, this creates a dead block (no incoming) to ensure that
         * {@link #currentBlock} is not <code>null</code>.
         */
        private void requireCurrent() {
            if (currentBlock == null) {
                currentBlock = func.createBlock();
            }
        }
        
        @Override
        public Void visitBinaryExpr(BinaryExpr expr) {
            expr.left.accept(this);
            expr.right.accept(this);
            return null;
        }

        @Override
        public Void visitBlock(Block stmt) {
            for (Statement child : stmt.statements) {
                child.accept(this);
            }
            
            return null;
        }

        @Override
        public Void visitDeclaration(Declaration decl) {
            decl.type.accept(this);
            if (decl.initExpr != null) {
                decl.initExpr.accept(this);
            }
            return null;
        }

        @Override
        public Void visitDeclarationStmt(DeclarationStmt stmt) {
            requireCurrent();
            currentBlock.addStatement(stmt);
            
            stmt.decl.accept(this);
            
            return null;
        }

        @Override
        public Void visitDoWhileLoop(DoWhileLoop stmt) {
            requireCurrent();
            ControlFlowBlock prev = this.currentBlock;
            
            
            ControlFlowBlock bodyStart = func.createBlock();
            this.currentBlock = bodyStart;
            
            stmt.body.accept(this);
            
            ControlFlowBlock bodyEnd = this.currentBlock;
            
            ControlFlowBlock after = null;
            if (bodyEnd != null) {
                bodyEnd.setOutCondition(stmt.condition);
                this.currentBlock = bodyEnd;
                stmt.condition.accept(this);
                
                after = func.createBlock();
                
                bodyEnd.setOutTrue(bodyStart);
                bodyEnd.setOutFalse(after);
            }
            
            prev.setOutTrue(bodyStart);
            
            this.currentBlock = after;
            
            return null;
        }

        @Override
        public Void visitEmptyStmt(EmptyStmt stmt) {
            requireCurrent();
            currentBlock.addStatement(stmt);
            return null;
        }

        @Override
        public Void visitExpressionStmt(ExpressionStmt stmt) {
            requireCurrent();
            currentBlock.addStatement(stmt);
            
            stmt.expr.accept(this);
            return null;
        }

        @Override
        public Void visitFile(File file) {
            throw new IllegalArgumentException();
        }
        
        @Override
        public Void visitFor(For stmt) {
            requireCurrent();
            ControlFlowBlock prev = this.currentBlock;
            if (stmt.init != null) {
                stmt.init.accept(this);
                
                DeclarationStmt declStmt = new DeclarationStmt(stmt);
                declStmt.decl = stmt.init;
                prev.addStatement(declStmt);
            }
            
            ControlFlowBlock condBlock = null;
            if (stmt.condition != null) {
                condBlock = func.createBlock();
                condBlock.setOutCondition(stmt.condition);
                
                this.currentBlock = condBlock;
                stmt.condition.accept(this);
            }
            
            ControlFlowBlock bodyStart = func.createBlock();
            this.currentBlock = bodyStart;
            
            stmt.body.accept(this);
            
            ControlFlowBlock bodyEnd = this.currentBlock;
            
            if (bodyEnd != null && stmt.increment != null) {
                stmt.increment.accept(this);
                
                ExpressionStmt incrStmt = new ExpressionStmt(stmt);
                incrStmt.expr = stmt.increment;
                bodyEnd.addStatement(incrStmt);
            }
            
            ControlFlowBlock after = null;
            
            if (condBlock != null) {
                after = func.createBlock();
                
                prev.setOutTrue(condBlock);
                
                condBlock.setOutTrue(bodyStart);
                condBlock.setOutFalse(after);
                
                if (bodyEnd != null) {
                    bodyEnd.setOutTrue(condBlock);
                }
            } else {
                prev.setOutTrue(bodyStart);

                if (bodyEnd != null) {
                    bodyEnd.setOutTrue(bodyStart);
                }
            }
            
            this.currentBlock = after;
            
            return null;
        }

        @Override
        public Void visitFunction(Function func) {
            throw new IllegalArgumentException();
        }

        @Override
        public Void visitFunctionCall(FunctionCall expr) {
            requireCurrent();
            this.currentBlock.addCalledFunction(expr.function);
            
            for (Expression param : expr.params) {
                param.accept(this);
            }
            
            return null;
        }

        @Override
        public Void visitIdentifier(Identifier expr) {
            return null;
        }

        @Override
        public Void visitIf(If stmt) {
            requireCurrent();
            ControlFlowBlock prev = this.currentBlock;
            
            prev.setOutCondition(stmt.condition);
            stmt.condition.accept(this);
            
            ControlFlowBlock thenStart = func.createBlock();
            this.currentBlock = thenStart;
            
            stmt.thenBlock.accept(this);
            
            ControlFlowBlock thenEnd = this.currentBlock;
            
            boolean hasElse = stmt.elseBlock != null;
            
            ControlFlowBlock elseStart = null;
            ControlFlowBlock elseEnd = null;
            if (hasElse) {
                elseStart = func.createBlock();
                this.currentBlock = elseStart;
                
                stmt.elseBlock.accept(this);
                
                elseEnd = this.currentBlock;
            }
            
            ControlFlowBlock after = null;
            if (thenEnd != null || !hasElse || elseEnd != null) {
                after = func.createBlock();
            }
            
            prev.setOutTrue(thenStart);
            if (thenEnd != null) {
                thenEnd.setOutTrue(after);
            }
            
            if (hasElse) {
                prev.setOutFalse(elseStart);
                if (elseEnd != null) {
                    elseEnd.setOutTrue(after);
                }
            } else {
                prev.setOutFalse(after);
            }
            
            this.currentBlock = after;
            
            return null;
        }

        @Override
        public Void visitLiteral(Literal expr) {
            return null;
        }

        @Override
        public Void visitReturn(Return stmt) {
            requireCurrent();
            currentBlock.addStatement(stmt);
            
            if (stmt.value != null) {
                stmt.value.accept(this);
            }
            
            currentBlock.setOutTrue(func.getEndBlock());
            currentBlock = null;
            
            return null;
        }

        @Override
        public Void visitType(Type type) {
            return null;
        }

        @Override
        public Void visitUnaryExpr(UnaryExpr expr) {
            expr.expr.accept(this);
            return null;
        }

        @Override
        public Void visitWhile(While stmt) {
            requireCurrent();
            ControlFlowBlock prev = this.currentBlock;
            
            ControlFlowBlock cond = func.createBlock();
            
            cond.setOutCondition(stmt.condition);
            this.currentBlock = cond;
            stmt.condition.accept(this);
            
            ControlFlowBlock bodyStart = func.createBlock();
            this.currentBlock = bodyStart;
            
            stmt.body.accept(this);
            
            ControlFlowBlock bodyEnd = this.currentBlock;
            
            ControlFlowBlock after = func.createBlock();
            
            prev.setOutTrue(cond);
            
            cond.setOutTrue(bodyStart);
            cond.setOutFalse(after);
            
            if (bodyEnd != null) {
                bodyEnd.setOutTrue(cond);
            }
            
            this.currentBlock = after;
            
            return null;
        }
        
    }
    
}
