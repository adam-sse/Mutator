package net.ssehub.mutator.ast.control_flow;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.mutator.ast.AstElement;
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
import net.ssehub.mutator.ast.FunctionDecl;
import net.ssehub.mutator.ast.Identifier;
import net.ssehub.mutator.ast.If;
import net.ssehub.mutator.ast.JumpStmt;
import net.ssehub.mutator.ast.Literal;
import net.ssehub.mutator.ast.Return;
import net.ssehub.mutator.ast.Statement;
import net.ssehub.mutator.ast.Type;
import net.ssehub.mutator.ast.UnaryExpr;
import net.ssehub.mutator.ast.While;
import net.ssehub.mutator.ast.operations.AstLinePrinter;
import net.ssehub.mutator.ast.operations.IAstVisitor;

public class ControlFlowCreator {

    public List<ControlFlowFunction> createControlFlow(File ast) {
        List<ControlFlowFunction> result = new LinkedList<>();
        
        for (AstElement func : ast.functions) {
            if (func instanceof Function) {
                result.add(createControlFlow((Function) func));
            }
        }
        
        return result;
    }
    
    public ControlFlowFunction createControlFlow(Function ast) {
        String header = ast.header.accept(new AstLinePrinter());
        
        ControlFlowFunction result = new ControlFlowFunction(ast.header.name, header);
        
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
        
        private Deque<ControlFlowBlock> loopNextIter;
        
        private Deque<ControlFlowBlock> loopAfter;
        
        public ControlFlowVisitor(ControlFlowFunction func) {
            this.func = func;
            this.currentBlock = func.createBlock();
            func.getStartBlock().setOutTrue(this.currentBlock);
            
            this.loopNextIter = new LinkedList<>();
            this.loopAfter = new LinkedList<>();
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
            
            ControlFlowBlock cond = func.createBlock();
            
            cond.setOutCondition(stmt.condition);
            this.currentBlock = cond;
            stmt.condition.accept(this);
            
            ControlFlowBlock bodyStart = func.createBlock();
            this.currentBlock = bodyStart;
            
            ControlFlowBlock after = func.createBlock();
            
            loopNextIter.push(cond);
            loopAfter.push(after);
            
            stmt.body.accept(this);
            
            loopNextIter.pop();
            loopAfter.pop();
            
            ControlFlowBlock bodyEnd = this.currentBlock;
            
            prev.setOutTrue(bodyStart);
            
            if (bodyEnd != null) {
                bodyEnd.setOutTrue(cond);
                
                cond.setOutTrue(bodyStart);
                cond.setOutFalse(after);
            } else if (cond.getIncoming().isEmpty()) {
                func.removeBlock(cond);
            }
            
            if (after.getIncoming().isEmpty()) {
                this.currentBlock = null;
            } else {
                this.currentBlock = after;
            }
            
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
            
            ControlFlowBlock incrBlock = null;
            if (stmt.increment != null) {
                incrBlock = func.createBlock();
                this.currentBlock = incrBlock;
                stmt.increment.accept(this);
                
                ExpressionStmt incrStmt = new ExpressionStmt(stmt);
                incrStmt.expr = stmt.increment;
                incrBlock.addStatement(incrStmt);
            }
            
            ControlFlowBlock after = func.createBlock();
            loopAfter.push(after);
            
            ControlFlowBlock bodyStart = func.createBlock();
            this.currentBlock = bodyStart;
            
            if (incrBlock != null) {
                loopNextIter.push(incrBlock);
            } else if (condBlock != null) {
                loopNextIter.push(condBlock);
            } else {
                loopNextIter.push(bodyStart);
            }
            
            stmt.body.accept(this);
            
            ControlFlowBlock bodyEnd = this.currentBlock;
            
            if (bodyEnd == null && incrBlock != null && incrBlock.getIncoming().isEmpty()) {
                func.removeBlock(incrBlock);
            }
            
            loopAfter.pop();
            loopNextIter.pop();
            
            if (condBlock != null) {
                
                prev.setOutTrue(condBlock);
                
                condBlock.setOutTrue(bodyStart);
                condBlock.setOutFalse(after);
                
                if (incrBlock != null) {
                    incrBlock.setOutTrue(condBlock);
                }
                
                if (bodyEnd != null) {
                    if (incrBlock != null) {
                        bodyEnd.setOutTrue(incrBlock);
                    } else {
                        bodyEnd.setOutTrue(condBlock);
                    }
                }
                
            } else {
                prev.setOutTrue(bodyStart);

                if (incrBlock != null) {
                    incrBlock.setOutTrue(bodyStart);
                }
                
                if (bodyEnd != null) {
                    if (incrBlock != null) {
                        bodyEnd.setOutTrue(incrBlock);
                    } else {
                        bodyEnd.setOutTrue(bodyStart);
                    }
                }
            }
            
            if (after.getIncoming().isEmpty()) {
                this.currentBlock = null;
            } else {
                this.currentBlock = after;
            }
            
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
        public Void visitFunctionDecl(FunctionDecl decl) {
            throw new IllegalArgumentException();
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
        public Void visitJumpStmt(JumpStmt stmt) {
            requireCurrent();
            this.currentBlock.addStatement(stmt);

            ControlFlowBlock next;
            if (stmt.type == net.ssehub.mutator.ast.JumpStmt.Type.CONTINUE) {
                next = loopNextIter.peek();
            } else if (stmt.type == net.ssehub.mutator.ast.JumpStmt.Type.BREAK) {
                next = loopAfter.peek();
            } else {
                throw new IllegalArgumentException(stmt.type.toString());
            }
            
            this.currentBlock.setOutTrue(next);
            this.currentBlock = null;
            
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
            
            ControlFlowBlock after = func.createBlock();
            
            loopNextIter.push(cond);
            loopAfter.push(after);
            
            stmt.body.accept(this);
            
            loopNextIter.pop();
            loopAfter.pop();
            
            ControlFlowBlock bodyEnd = this.currentBlock;
            
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
