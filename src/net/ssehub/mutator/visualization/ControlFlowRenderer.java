package net.ssehub.mutator.visualization;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import net.ssehub.mutator.parsing.ast.Assignment;
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
import net.ssehub.mutator.parsing.ast.Statement;
import net.ssehub.mutator.parsing.ast.Type;
import net.ssehub.mutator.parsing.ast.UnaryExpr;
import net.ssehub.mutator.parsing.ast.While;
import net.ssehub.mutator.parsing.ast.operations.IAstVisitor;

public class ControlFlowRenderer {

    private String dotExe;
    
    private StringBuilder dot;
    
    public ControlFlowRenderer(String dotExe) {
        this.dotExe = dotExe;
    }
    
    public void render(File file, java.io.File output) throws IOException {
        this.dot = new StringBuilder();
        
        preamble();
        
        for (Function func : file.functions) {
            function(func);
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
            .append("    node [fontname=\"Liberation Mono\"; fontsize=9; shape=\"rectangle\"; nojustify=\"true\"; margin=\"0.02\"];\n")
            .append("    edge [fontname=\"Liberation Mono\"; fontsize=9; tailport=\"s\"; headport=\"n\"; arrowhead=\"vee\"; arrowsize=0.8];\n")
            .append("    compound=true;\n")
            .append("\n");
            
    }
    
    private void epilog() {
        this.dot.append("}\n");
    }
    
    private void function(Function func) {
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
            .append("        " + startNode + " [shape=\"circle\"; label=\"\"; width=0.2; style=\"filled\"; fillcolor=\"black\"];\n")
            .append("        " + endNode + " [shape=\"circle\"; label=\"\"; width=0.2];\n");
        
        ControlBlockCollector collector = new ControlBlockCollector(func.name, endNode);
        func.body.accept(collector);
        
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
    
    private static class ControlBlockCollector implements IAstVisitor<Void> {

        private List<ControlBlock> allBlocks;
        
        private String funcName;
        
        private String startNode;
        
        private String endNode;
        
        private int id = 0;
        
        private ControlBlock currentBlock;
        
        public ControlBlockCollector(String funcName, String endNode) {
            this.funcName = funcName;
            this.endNode = endNode;
            allBlocks = new LinkedList<>();
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
            return null;
        }

        @Override
        public Void visitBinaryExpr(BinaryExpr expr) {
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
            return null;
        }

        @Override
        public Void visitDeclarationStmt(DeclarationStmt stmt) {
            currentBlock.addLine(stmt.getText());
            return null;
        }

        @Override
        public Void visitDoWhileLoop(DoWhileLoop stmt) {
            ControlBlock prev = currentBlock;
            
            ControlBlock bodyStart = createBlock();
            currentBlock = bodyStart;
            stmt.body.accept(this);
            ControlBlock bodyEnd = currentBlock;
            

            ControlBlock after = createBlock();
            
            prev.addNext(bodyStart, null);
            
            String condition = stmt.condition.getText();
            bodyEnd.addNext(bodyStart, condition);
            
            bodyEnd.addNext(after, "else");
            
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
            return null;
        }

        @Override
        public Void visitFile(File file) {
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
            ControlBlock prev = currentBlock;
            
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
            
            ControlBlock after = createBlock();
            
            prev.addNext(thenStart, stmt.condition.getText());
            if (thenEnd != null) {
                thenEnd.addNext(after, null);
            }
            
            if (stmt.elseBlock != null) {
                prev.addNext(elseStart, "else");
                if (elseEnd != null) {
                    elseEnd.addNext(after, null);
                }
            } else {
                prev.addNext(after, "else");
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
            return null;
        }

        @Override
        public Void visitWhile(While stmt) {
            ControlBlock prev = currentBlock;
            
            ControlBlock bodyStart = createBlock();
            currentBlock = bodyStart;
            stmt.body.accept(this);
            ControlBlock bodyEnd = currentBlock;
            

            ControlBlock after = createBlock();
            
            String condition = stmt.condition.getText();
            bodyEnd.addNext(bodyStart, condition);
            prev.addNext(bodyStart, condition);
            
            bodyEnd.addNext(after, "else");
            prev.addNext(after, "else");
            
            currentBlock = after;
            
            return null;
        }
        
        
        
    }
    
}
