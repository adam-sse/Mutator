package net.ssehub.mutator.visualization;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.BinaryExpr;
import net.ssehub.mutator.ast.Block;
import net.ssehub.mutator.ast.Declaration;
import net.ssehub.mutator.ast.DeclarationStmt;
import net.ssehub.mutator.ast.DoWhileLoop;
import net.ssehub.mutator.ast.EmptyStmt;
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
import net.ssehub.mutator.ast.Type;
import net.ssehub.mutator.ast.UnaryExpr;
import net.ssehub.mutator.ast.While;
import net.ssehub.mutator.ast.operations.FullVisitor;
import net.ssehub.mutator.ast.operations.IAstVisitor;
import net.ssehub.mutator.ast.operations.SingleOperationVisitor;

public class AstRenderer extends AbstractDotRenderer {

    public AstRenderer(String dotExe) {
        super(dotExe);
    }

    public void render(File file, java.io.File output) throws IOException {
        StringBuilder dot = new StringBuilder();

        dot.append("digraph AST {\n").append("    graph [fontname=\"Liberation Mono\"; fontsize=9];\n").append(
                "    node [fontname=\"Liberation Mono\"; fontsize=9; shape=\"rectangle\"; nojustify=\"true\"; margin=\"0.1\"];\n")
                .append("    edge [fontname=\"Liberation Mono\"; fontsize=9; arrowhead=\"vee\"; arrowsize=0.8];\n")
                .append("    splines=polyline;").append("\n");

        RankCollector rankCollector = new RankCollector();
        file.accept(new FullVisitor(rankCollector));

        LabelCreator labelCreator = new LabelCreator();

        rankCollector.ranks.entrySet().stream().sorted((r1, r2) -> Integer.compare(r1.getKey(), r2.getKey()))
                .forEach((rank) -> {

                    dot.append("    subgraph \"rank_").append(rank.getKey()).append("\" {\n")
                            .append("        rank=same;\n");

                    for (AstElement node : rank.getValue()) {
                        dot.append("        \"").append(node.id).append("\" [label=\"")
                                .append(node.accept(labelCreator)).append("\"];\n");
                    }

                    dot.append("    }\n");

                    for (AstElement node : rank.getValue()) {
                        if (node.parent != null) {
                            dot.append("    \"").append(node.parent.id).append("\" -> \"").append(node.id)
                                    .append("\";\n");
                        }
                    }
                });

        dot.append("}\n");

        render(dot.toString(), "dot", output);
    }

    private static class RankCollector extends SingleOperationVisitor<Void> {

        private Map<Integer, List<AstElement>> ranks = new HashMap<>();

        private int getRank(AstElement element) {
            int rank = 1;
            while (element != null) {
                rank++;
                element = element.parent;
            }
            return rank;
        }

        @Override
        protected Void visit(AstElement element) {
            int rank = getRank(element);
            List<AstElement> list = ranks.get(rank);
            if (list == null) {
                list = new LinkedList<>();
                ranks.put(rank, list);
            }
            list.add(element);
            return null;
        }

    }

    private static class LabelCreator implements IAstVisitor<String> {

        @Override
        public String visitBinaryExpr(BinaryExpr expr) {
            return expr.operator.toString();
        }

        @Override
        public String visitBlock(Block stmt) {
            return "Block";
        }

        @Override
        public String visitDeclaration(Declaration decl) {
            return "Declaration\\n" + decl.identifier;
        }

        @Override
        public String visitDeclarationStmt(DeclarationStmt stmt) {
            return "DeclarationStmt";
        }

        @Override
        public String visitDoWhileLoop(DoWhileLoop stmt) {
            return "do-while";
        }

        @Override
        public String visitEmptyStmt(EmptyStmt stmt) {
            return ";";
        }

        @Override
        public String visitExpressionStmt(ExpressionStmt stmt) {
            return "ExpressionStmt";
        }

        @Override
        public String visitFile(File file) {
            return "File";
        }

        @Override
        public String visitFor(For stmt) {
            return "for";
        }

        @Override
        public String visitFunction(Function func) {
            return "Function";
        }

        @Override
        public String visitFunctionCall(FunctionCall expr) {
            return expr.function + "()";
        }

        @Override
        public String visitFunctionDecl(FunctionDecl decl) {
            return "FunctionDecl\\n" + decl.name;
        }

        @Override
        public String visitIdentifier(Identifier expr) {
            return expr.identifier;
        }

        @Override
        public String visitIf(If stmt) {
            return "if";
        }

        @Override
        public String visitJumpStmt(JumpStmt stmt) {
            return stmt.type.toString();
        }

        @Override
        public String visitLiteral(Literal expr) {
            return expr.value;
        }

        @Override
        public String visitReturn(Return stmt) {
            return "return";
        }

        @Override
        public String visitType(Type type) {
            return type.getText();
        }

        @Override
        public String visitUnaryExpr(UnaryExpr expr) {
            return expr.operator.toString();
        }

        @Override
        public String visitWhile(While stmt) {
            return "while";
        }

    }

}
