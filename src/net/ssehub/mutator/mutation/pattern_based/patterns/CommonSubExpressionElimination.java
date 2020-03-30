package net.ssehub.mutator.mutation.pattern_based.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.BasicType;
import net.ssehub.mutator.ast.BinaryExpr;
import net.ssehub.mutator.ast.BinaryOperator;
import net.ssehub.mutator.ast.Block;
import net.ssehub.mutator.ast.Declaration;
import net.ssehub.mutator.ast.DeclarationStmt;
import net.ssehub.mutator.ast.Expression;
import net.ssehub.mutator.ast.ExpressionStmt;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.For;
import net.ssehub.mutator.ast.FunctionCall;
import net.ssehub.mutator.ast.Identifier;
import net.ssehub.mutator.ast.Literal;
import net.ssehub.mutator.ast.Statement;
import net.ssehub.mutator.ast.Type;
import net.ssehub.mutator.ast.UnaryExpr;
import net.ssehub.mutator.ast.UnaryOperator;
import net.ssehub.mutator.ast.operations.AstCloner;
import net.ssehub.mutator.ast.operations.FullExpressionVisitor;
import net.ssehub.mutator.ast.operations.IExpressionVisitor;
import net.ssehub.mutator.ast.operations.IdFinder;
import net.ssehub.mutator.mutation.genetic.mutations.ElementReplacer;
import net.ssehub.mutator.mutation.genetic.mutations.StatementInserter;

public class CommonSubExpressionElimination implements IOpportunity {

    private long parentId;

    private Expression expression;

    private BasicType type;

    private CommonSubExpressionElimination(long parentId, Expression expression, BasicType type) {
        this.parentId = parentId;
        this.expression = expression;
        this.type = type;
    }

    @Override
    public int getMinParam() {
        return 0;
    }

    @Override
    public int getDefaultParam() {
        return 0;
    }

    @Override
    public int getMaxParam() {
        return 1;
    }

    @Override
    public void apply(int param, File ast) {
        // no modification if param == 0
        if (param == 1) {

            // 1) find all expressions & corresponding statements
            Block parent = (Block) ast.accept(new IdFinder(parentId));

            List<Expression> expressions = new ArrayList<>(100);
            List<Statement> statements = new ArrayList<>(100);

            EqualExpressionFinder finder = new EqualExpressionFinder(this.expression);
            parent.accept(new FullExpressionVisitor(finder));
            for (Expression expr : finder.found) {
                expressions.add(expr);

                Statement parentStatement = Util.findParentStatement(expr);
                while (parentStatement.parent.id != parentId) {
                    parentStatement = (Statement) parentStatement.parent;
                }
                statements.add(parentStatement);
            }

            if (expressions.size() < 2) {
                // due to previous modifications, there may be less than 2 expressions left now
                return;
            }

            // 2) find the first and last statements that uses the expression
            Statement first = null;
            Statement last = null;
            for (Statement st : parent.statements) {
                if (statements.stream().filter((statement) -> statement.id == st.id).findAny().isPresent()) {
                    if (first == null) {
                        first = st;
                    }
                    last = st;
                }
            }

            // 3) create a declaration and insert before first statement
            String tempVarName = "mutator_tmp_" + (int) (Math.random() * Integer.MAX_VALUE);

            DeclarationStmt declStmt = new DeclarationStmt(first.parent);
            Declaration decl = new Declaration(declStmt);
            Type type = new Type(decl);
            type.type = this.type;

            decl.type = type;
            decl.identifier = tempVarName;

            // special case: don't add the initExpr if the first occurrence is an assignment
            // to expr
            if (!isAssignmentToExpr(expressions.get(0), true)) {
                decl.initExpr = (Expression) expression.accept(new AstCloner(decl, false));
            }

            declStmt.decl = decl;

            StatementInserter inserter = new StatementInserter();
            inserter.insert(first, true, declStmt);

            // 4) replace all expression occurrences with the temporary variable
            boolean containedAssignment = false;
            for (Expression expr : expressions) {
                if (isAssignmentToExpr(expr, false)) {
                    containedAssignment = true;
                }

                ElementReplacer<Expression> replacer = new ElementReplacer<>();

                Identifier tempIdentifer = new Identifier(expr.parent);
                tempIdentifer.identifier = tempVarName;

                replacer.replace(expr, tempIdentifer);
            }

            // 5) special case: write a back-assignment (if at least one occurrence was an
            // assignment)
            // (e.g. useful for array accesses)
            if (containedAssignment) {
                ExpressionStmt backAssignmentStmt = new ExpressionStmt(last.parent);

                BinaryExpr backAssignment = new BinaryExpr(backAssignmentStmt);
                backAssignment.operator = BinaryOperator.ASSIGNMENT;
                backAssignmentStmt.expr = backAssignment;

                backAssignment.left = (Expression) expression.accept(new AstCloner(backAssignment, false));

                Identifier tempIdentifier = new Identifier(backAssignment);
                tempIdentifier.identifier = tempVarName;
                backAssignment.right = tempIdentifier;

                inserter.insert(last, false, backAssignmentStmt);
            }
        }
    }

    private static boolean isAssignmentToExpr(Expression child, boolean onlySimple) {
        boolean result = false;
        if (child.parent instanceof BinaryExpr) {
            BinaryExpr parent = (BinaryExpr) child.parent;
            if (onlySimple) {
                result = parent.operator == BinaryOperator.ASSIGNMENT && child == parent.left;
            } else {
                result = parent.operator.isAssignment() && child == parent.left;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "CommonSubExpressionElimination(parent=#" + parentId + ", expr=\'" + expression.getText() + "\', type="
                + type + ")";
    }

    public static List<CommonSubExpressionElimination> findOpportunities(File ast) {
        ExpressionCounter counter = new ExpressionCounter();
        ast.accept(new FullExpressionVisitor(counter));

        List<Map.Entry<Expression, Integer>> entries = new ArrayList<>(counter.count.entrySet());
        entries = entries.stream().filter((entry) -> entry.getValue() > 1)
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .sorted((e1, e2) -> Integer.compare(e2.getKey().accept(new ExpressionComplexityMeasuerer()),
                        e1.getKey().accept(new ExpressionComplexityMeasuerer())))
                .collect(Collectors.toList());

        List<CommonSubExpressionElimination> result = new ArrayList<>(entries.size());

        for (Map.Entry<Expression, Integer> entry : entries) {
            Expression expr = entry.getKey();

            List<Expression> expressions = counter.elements.get(expr);

            AstElement commonParent = Util.findCommonParent(expressions.toArray(new AstElement[0]));
            // we want a Block as the common parent, so that we can properly insert
            // statements before the first expr
            while (!(commonParent instanceof Block)) {
                commonParent = commonParent.parent;
            }

            BasicType type = new TypeGuesser().guessType(expr);

            result.add(new CommonSubExpressionElimination(commonParent.id,
                    (Expression) expr.accept(new AstCloner(null, true)), type));
        }

        return result;
    }

    private static class EqualExpressionFinder implements IExpressionVisitor<Void> {

        private Expression toFind;

        private List<Expression> found;

        public EqualExpressionFinder(Expression toFind) {
            this.toFind = toFind;
            this.found = new LinkedList<>();
        }

        private void check(Expression e) {
            if (toFind.equals(e)) {
                found.add(e);
            }
        }

        @Override
        public Void visitBinaryExpr(BinaryExpr expr) {
            check(expr);
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
        public Void visitLiteral(Literal expr) {
            check(expr);
            return null;
        }

        @Override
        public Void visitUnaryExpr(UnaryExpr expr) {
            check(expr);
            return null;
        }

    }

    private static class ExpressionCounter implements IExpressionVisitor<Void> {

        private SideEffectChecker checker = new SideEffectChecker();

        private Map<Expression, Integer> count = new HashMap<>(1024);

        private Map<Expression, List<Expression>> elements = new HashMap<>(1024);

        private void addAndIncrement(Expression expr) {
            // don't consider top-level expressions in for loops
            // (this commonly breaks loop-unrolling)
            if (expr.parent instanceof For) {
                return;
            }

            // don't consider expressions that have side-effects
            if (checker.hasSideEffect(expr)) {
                return;
            }

            Integer count = this.count.get(expr);
            if (count != null) {
                this.count.put(expr, count + 1);
                this.elements.get(expr).add(expr);
            } else {
                this.count.put(expr, 1);

                LinkedList<Expression> list = new LinkedList<>();
                list.add(expr);
                this.elements.put(expr, list);
            }
        }

        @Override
        public Void visitBinaryExpr(BinaryExpr expr) {
            addAndIncrement(expr);
            return null;
        }

        @Override
        public Void visitFunctionCall(FunctionCall expr) {
            addAndIncrement(expr);
            return null;
        }

        @Override
        public Void visitIdentifier(Identifier expr) {
            // don't add single identifiers
            return null;
        }

        @Override
        public Void visitLiteral(Literal expr) {
            // don't add single literals
            return null;
        }

        @Override
        public Void visitUnaryExpr(UnaryExpr expr) {
            // ignore -literal
            if ((expr.operator != UnaryOperator.MINUS || !(expr.expr instanceof Literal))) {
                addAndIncrement(expr);
            }

            return null;
        }

    }

    private static class SideEffectChecker implements IExpressionVisitor<Void> {

        private boolean hasSideEffect;

        private FullExpressionVisitor visitor = new FullExpressionVisitor(this);

        public boolean hasSideEffect(Expression expr) {
            this.hasSideEffect = false;

            expr.accept(visitor);

            return this.hasSideEffect;
        }

        @Override
        public Void visitBinaryExpr(BinaryExpr expr) {
            if (expr.operator.isAssignment()) {
                this.hasSideEffect = true;
            }
            return null;
        }

        @Override
        public Void visitFunctionCall(FunctionCall expr) {
            // we don't consider functions to have side-effects
            return null;
        }

        @Override
        public Void visitIdentifier(Identifier expr) {
            return null;
        }

        @Override
        public Void visitLiteral(Literal expr) {
            return null;
        }

        @Override
        public Void visitUnaryExpr(UnaryExpr expr) {
            switch (expr.operator) {
            case POST_DEC:
            case POST_INC:
            case PRE_DEC:
            case PRE_INC:
                this.hasSideEffect = true;
                break;

            default:
                // do nothing
            }

            return null;
        }

    }

    private static class ExpressionComplexityMeasuerer implements IExpressionVisitor<Integer> {

        @Override
        public Integer visitBinaryExpr(BinaryExpr expr) {
            return 1 + expr.left.accept(this) + expr.right.accept(this);
        }

        @Override
        public Integer visitFunctionCall(FunctionCall expr) {
            int complexity = 2;
            for (Expression param : expr.params) {
                complexity += param.accept(this);
            }
            return complexity;
        }

        @Override
        public Integer visitIdentifier(Identifier expr) {
            return 1;
        }

        @Override
        public Integer visitLiteral(Literal expr) {
            return 1;
        }

        @Override
        public Integer visitUnaryExpr(UnaryExpr expr) {
            return 1 + expr.expr.accept(this);
        }

    }

}
