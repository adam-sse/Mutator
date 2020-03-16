package net.ssehub.mutator.visualization;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import net.ssehub.mutator.ast.Assignment;
import net.ssehub.mutator.ast.BinaryExpr;
import net.ssehub.mutator.ast.Block;
import net.ssehub.mutator.ast.Declaration;
import net.ssehub.mutator.ast.DeclarationStmt;
import net.ssehub.mutator.ast.DoWhileLoop;
import net.ssehub.mutator.ast.EmptyStmt;
import net.ssehub.mutator.ast.Expression;
import net.ssehub.mutator.ast.ExpressionStmt;
import net.ssehub.mutator.ast.File;
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

public class ControlFlowRenderer {

    private String dotExe;
    
    private StringBuilder dot;
    
    public ControlFlowRenderer(String dotExe) {
        this.dotExe = dotExe;
    }
    
    public void render(File file, java.io.File output) throws IOException {
        this.dot = new StringBuilder();
        
        preamble();
        
        Set<String> localFunctions = new HashSet<>();
        List<FunctionCallEdge> functionCalls = new LinkedList<>();
        
        for (Function func : file.functions) {
            localFunctions.add(func.name);
            function(func, localFunctions, functionCalls);
        }
        
     // write function calls
        for (FunctionCallEdge functionCall : functionCalls) {
            String attributes = "; headport=_; tailport=_; color=grey; style=dashed";
            
            this.dot.append("    ").append(functionCall.block).append(" -> ")
                    .append("\"" + functionCall.function + " Start\"")
                    .append(" [lhead=cluster_" + functionCall.function + attributes + "];\n");
//            this.dot.append("    ").append("\"" + functionCall.function + " End\"").append(" -> ")
//                    .append(functionCall.block)
//                    .append(" [ltail=cluster_" + functionCall.function + attributes + "];\n");
        }
        
        epilog();
        
//        System.out.println(this.dot);
        
        java.io.File tmp = java.io.File.createTempFile("mutator_", ".dot");
        tmp.deleteOnExit();
        try (FileWriter out = new FileWriter(tmp)) {
            out.write(this.dot.toString());
        }
        
        ProcessBuilder pb = new ProcessBuilder(dotExe,
                "-T" + output.getName().substring(output.getName().lastIndexOf('.') + 1),
                "-o", output.getAbsolutePath(), tmp.getAbsolutePath());
        pb.inheritIO();
        pb.redirectErrorStream(true);
        
        try {
            pb.start().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void preamble() {
        this.dot.append("digraph ast {\n")
            .append("    graph [fontname=\"Liberation Mono\"; fontsize=9];\n")
            .append("    node [fontname=\"Liberation Mono\"; fontsize=9; shape=\"rectangle\"; nojustify=\"true\"; margin=\"0.1\"; style=filled; fillcolor=white];\n")
            .append("    edge [fontname=\"Liberation Mono\"; fontsize=9; tailport=\"s\"; headport=\"n\"; arrowhead=\"vee\"; arrowsize=0.8];\n")
            .append("    compound=true;\n")
            .append("\n");
            
    }
    
    private void epilog() {
        this.dot.append("}\n");
    }
    
    private void function(Function func, Set<String> localFunctions, List<FunctionCallEdge> functionCalls) {
        String startNode = "\"" + func.name + " Start\"";
        String endNode = "\"" + func.name + " End\"";
        
        String header = func.type.getText() + " " + func.name;
        StringJoiner sj = new StringJoiner(", ", "(", ")");
        for (Declaration param : func.parameters) {
            sj.add(param.getText());
        }
        header += sj.toString();
        
        this.dot.append("    subgraph cluster_" + func.name + " {\n")
            .append("        label=\"" + header + "\";\n")
            .append("        style=filled;\n")
            .append("        bgcolor=whitesmoke;\n")
            .append("        " + startNode + " [shape=\"circle\"; label=\"\"; width=0.2; style=\"filled\"; fillcolor=\"black\"];\n")
            .append("        " + endNode + " [shape=\"circle\"; label=\"\"; width=0.2];\n");
        
        ControlBlockCollector collector = new ControlBlockCollector(func.name, endNode, localFunctions);
        func.body.accept(collector);
        functionCalls.addAll(collector.functionCalls);
        
        // write nodes
        for (ControlBlock block : collector.allBlocks) {
            this.dot.append("        ").append(block.name)
                    .append(" [label=\"").append(block.statements.toString()).append("\"];\n");
        }
        
        // write edges
        this.dot.append("        ").append(startNode).append(" -> ").append(collector.startNode).append(";\n");
        
        for (ControlBlock block : collector.allBlocks) {
            for (ControlEdge edge : block.outgoing) {
                this.dot.append("        ").append(block.name).append(" -> ").append(edge.target);
                
                if (edge.condition != null) {
                    this.dot.append(" [label=\"").append(edge.condition).append("\"]");
                }
                
                this.dot.append(";\n");
            }
        }
        
        this.dot.append("    }\n")
            .append("\n");
    }
 
    private static final class ControlEdge {
        
        private String target;
        
        private String condition;

        public ControlEdge(String target, String condition) {
            this.target = target;
            this.condition = condition;
        }
        
    }
    
    private static final class ControlBlock {
        
        private String name;
        
        private StringBuilder statements;
        
        private List<ControlEdge> outgoing;
        
        public ControlBlock(String name) {
            this.name = name;
            statements = new StringBuilder();
            outgoing = new LinkedList<>();
        }
        
        public void addLine(String line) {
            statements.append(line).append("\\l");
        }
        
        public void addNext(ControlBlock next, String condition) {
            this.outgoing.add(new ControlEdge(next.name, condition));
        }
        
        @Override
        public boolean equals(Object obj) {
            boolean equal = false;
            if (obj instanceof ControlBlock) {
                ControlBlock other = (ControlBlock) obj;
                equal = this.name.equals(other.name);
            }
            return equal;
        }
        
        @Override
        public int hashCode() {
            return name.hashCode();
        }
        
    }
    
    private static final class FunctionCallEdge {
        
        private String block;
        
        private String function;

        public FunctionCallEdge(String block, String function) {
            this.block = block;
            this.function = function;
        }
        
    }
    
    private static final class ControlBlockCollector implements IAstVisitor<Void> {

        private List<ControlBlock> allBlocks;
        
        private List<FunctionCallEdge> functionCalls;
        
        private Set<String> localFunctions;
        
        private String funcName;
        
        private String startNode;
        
        private String endNode;
        
        private int id = 0;
        
        private ControlBlock currentBlock;
        
        public ControlBlockCollector(String funcName, String endNode, Set<String> localFunctions) {
            this.funcName = funcName;
            this.endNode = endNode;
            this.localFunctions = localFunctions;
            allBlocks = new LinkedList<>();
            functionCalls = new LinkedList<>();
        }

        private ControlBlock createBlock() {
            String name = "\"" + funcName + " Block " + (++id) + "\"";
            ControlBlock cBlock = new ControlBlock(name);
            allBlocks.add(cBlock);
            return cBlock;
        }
        
        @Override
        public Void visitAssignment(Assignment stmt) {
            currentBlock.addLine(stmt.getText());
            
            stmt.variable.accept(this);
            stmt.value.accept(this);
            return null;
        }

        @Override
        public Void visitBinaryExpr(BinaryExpr expr) {
            expr.left.accept(this);
            expr.right.accept(this);
            return null;
        }

        @Override
        public Void visitBlock(Block stmt) {
            // for body Block of top-level function
            boolean isFirst = currentBlock == null;
            
            if (isFirst) {
                currentBlock = createBlock();
                this.startNode = currentBlock.name;
            }
            
            for (Statement st : stmt.statements) {
                st.accept(this);
            }
            
            if (isFirst && currentBlock != null) {
                currentBlock.outgoing.add(new ControlEdge(endNode, null));
            }
            
            return null;
        }

        @Override
        public Void visitDeclaration(Declaration decl) {
            decl.type.accept(this);
            return null;
        }

        @Override
        public Void visitDeclarationStmt(DeclarationStmt stmt) {
            // don't print this kind of statement
            stmt.decl.accept(this);
            return null;
        }

        @Override
        public Void visitDoWhileLoop(DoWhileLoop stmt) {
            ControlBlock prev = currentBlock;
            
            ControlBlock bodyStart = createBlock();
            currentBlock = bodyStart;
            
            stmt.body.accept(this);
            
            ControlBlock bodyEnd = currentBlock;
            
            bodyEnd.addLine(stmt.condition.getText() + "?");
            stmt.condition.accept(this);

            ControlBlock after = createBlock();
            
            prev.addNext(bodyStart, null);
            
            bodyEnd.addNext(bodyStart, "1");
            bodyEnd.addNext(after, "0");
            
            currentBlock = after;
            
            return null;
        }

        @Override
        public Void visitEmptyStmt(EmptyStmt stmt) {
            currentBlock.addLine(stmt.getText());
            return null;
        }

        @Override
        public Void visitExpressionStmt(ExpressionStmt stmt) {
            currentBlock.addLine(stmt.getText());
            stmt.expr.accept(this);
            return null;
        }

        @Override
        public Void visitFile(File file) {
            throw new IllegalArgumentException();
        }

        @Override
        public Void visitFunction(Function func) {
            throw new IllegalArgumentException();
        }

        @Override
        public Void visitFunctionCall(FunctionCall expr) {
            if (localFunctions.contains(expr.function)) {
                functionCalls.add(new FunctionCallEdge(currentBlock.name, expr.function));
            }
            
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
            ControlBlock prev = currentBlock;
            
            prev.addLine(stmt.condition.getText() + "?");
            stmt.condition.accept(this);
            
            ControlBlock thenStart = createBlock();
            currentBlock = thenStart;
            stmt.thenBlock.accept(this);
            ControlBlock thenEnd = currentBlock;

            ControlBlock elseStart = null;
            ControlBlock elseEnd = null;
            if (stmt.elseBlock != null) {
                elseStart = createBlock();
                currentBlock = elseStart;
                stmt.elseBlock.accept(this);
                elseEnd = currentBlock;
            }
            
            ControlBlock after = null;
            if (thenEnd != null || elseEnd != null || stmt.elseBlock == null) {
                
                // try to re-use trailing end blocks
                if (thenEnd != null && thenEnd.statements.length() == 0) {
                    after = thenEnd;
                } else if (elseEnd != null && elseEnd.statements.length() == 0) {
                    after = elseEnd;
                } else {
                    after = createBlock();
                }
            }
            
            prev.addNext(thenStart, "1");
            if (thenEnd != null && thenEnd != after) {
                thenEnd.addNext(after, null);
            }
            
            if (stmt.elseBlock != null) {
                prev.addNext(elseStart, "0");
                if (elseEnd != null && elseEnd != after) {
                    elseEnd.addNext(after, null);
                }
            } else if (after != null) {
                prev.addNext(after, "0");
            }
            
            currentBlock = after;
            
            return null;
        }

        @Override
        public Void visitLiteral(Literal expr) {
            return null;
        }

        @Override
        public Void visitReturn(Return stmt) {
            currentBlock.addLine(stmt.getText());
            if (stmt.value != null) {
                stmt.value.accept(this);
            }
            
            currentBlock.outgoing.add(new ControlEdge(endNode, null));
            
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
            ControlBlock prev = currentBlock;
            
            ControlBlock condBlock;
            if (prev.statements.length() == 0) {
                condBlock = prev;
            } else {
                condBlock = createBlock();
            }
            
            currentBlock = condBlock;
            
            stmt.condition.accept(this);
            condBlock.addLine(stmt.condition.getText() + "?");
            
            ControlBlock bodyStart = createBlock();
            currentBlock = bodyStart;
            stmt.body.accept(this);
            ControlBlock bodyEnd = currentBlock;
            
            stmt.condition.accept(this);

            ControlBlock after = createBlock();
            
            bodyEnd.addNext(condBlock, null);
            if (prev != condBlock) {
                prev.addNext(condBlock, null);
            }

            condBlock.addNext(bodyStart, "1");
            condBlock.addNext(after, "0");
            
            currentBlock = after;
            
            return null;
        }
        
    }
    
}
