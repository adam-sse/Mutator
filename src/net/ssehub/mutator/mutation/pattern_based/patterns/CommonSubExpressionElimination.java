package net.ssehub.mutator.mutation.pattern_based.patterns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.BasicType;
import net.ssehub.mutator.ast.BinaryExpr;
import net.ssehub.mutator.ast.BinaryOperator;
import net.ssehub.mutator.ast.Block;
import net.ssehub.mutator.ast.Declaration;
import net.ssehub.mutator.ast.DeclarationStmt;
import net.ssehub.mutator.ast.Expression;
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
    
    private List<Long> expressionIds;
    
    private String exprTxt;
    
    private BasicType type;
    
    private CommonSubExpressionElimination(long parentId, List<Long> expressionIds, String exprTxt, BasicType type) {
        this.parentId = parentId;
        this.expressionIds = expressionIds;
        this.exprTxt = exprTxt;
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
            
            List<Expression> expressions = new ArrayList<>(expressionIds.size());
            List<Statement> statements = new ArrayList<>(expressionIds.size());
            for (long id : expressionIds) {
                Expression expr = (Expression) ast.accept(new IdFinder(id));
                if (expr != null) {
                    expressions.add(expr);
                    
                    Statement parentStatement = findParentStatement(expr);
                    while (parentStatement.parent.id != parentId) {
                        parentStatement = (Statement) parentStatement.parent;
                    }
                    statements.add(parentStatement);
                }
            }
            
            if (expressions.size() < 2) {
                // due to previous modifications, a few expressions may be lost
                return;
            }
            
            // 2) find the first statement that uses the expression
            Statement reference = statements.get(0);
            for (Statement st : parent.statements) {
                if (statements.contains(st)) {
                    reference = st;
                    break;
                }
            }
            
            // 3) create a declaration and insert before first statement
            String tempVar = "mutator_tmp_" + (int) (Math.random() * Integer.MAX_VALUE);
            
            DeclarationStmt declStmt = new DeclarationStmt(reference.parent);
            Declaration decl = new Declaration(declStmt);
            Type type = new Type(decl);
            type.type = this.type;
            
            decl.type = type;
            decl.identifier = tempVar;
            decl.initExpr = (Expression) expressions.get(0).accept(new AstCloner(decl, false));
            declStmt.decl = decl;
            
            StatementInserter inserter = new StatementInserter();
            inserter.insert(reference, true, declStmt);
            
            // 4) replace all expression occurrences with the temporary variable 
            for (Expression expr : expressions) {
                ElementReplacer<Expression> replacer = new ElementReplacer<>();
                
                Identifier tempIdentifer = new Identifier(expr.parent);
                tempIdentifer.identifier = tempVar;
                
                replacer.replace(expr, tempIdentifer);
            }
        }
    }
    
    private static Statement findParentStatement(Expression expr) {
        AstElement parent = expr.parent;
        while (!(parent instanceof Statement)) {
            parent = parent.parent;
        }
        return (Statement) parent;
    }
    
    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", #", "[#", "]");
        for (Long id : expressionIds) {
            sj.add(Long.toString(id));
        }
        return "CommonSubExpressionElimination(parent=#" + parentId + ", expr=\'" + exprTxt
                + "\', type=" + type + " exprIds=" + sj + ")";
    }
    
    public static List<CommonSubExpressionElimination> findOpportunities(File ast) {
        
        ExpressionCounter counter = new ExpressionCounter();
        ast.accept(new FullExpressionVisitor(counter));
        
        List<Map.Entry<Expression, Integer>> entries = new ArrayList<>(counter.count.entrySet());
        entries = entries.stream()
                .filter((entry) -> entry.getValue() > 1)
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .sorted((e1, e2) -> Integer.compare(
                        e2.getKey().accept(new ExpressionComplexityMeasuerer()),
                        e1.getKey().accept(new ExpressionComplexityMeasuerer())))
                .collect(Collectors.toList());
        
        List<CommonSubExpressionElimination> result = new ArrayList<>(entries.size());
        
        for (Map.Entry<Expression, Integer> entry : entries) {
            Expression key = entry.getKey();
            
            List<Expression> expressions = counter.elements.get(key);
            
            AstElement commonParent = findCommonParent(expressions.toArray(new AstElement[0]));
            // we want a Block as the common parent, so that we can properly insert statements before the first expr
            while (!(commonParent instanceof Block)) {
                commonParent = commonParent.parent;
            }
            
            List<Long> exprIds = new ArrayList<>(expressions.size());
            for (Expression expr : expressions) {
                exprIds.add(expr.id);
            }
            
            BasicType type = new TypeGuesser().guessType(key);
            
            result.add(new CommonSubExpressionElimination(commonParent.id, exprIds, key.getText(), type));
        }
        
        return result;
    }
    
    private static int depth(AstElement element) {
        int depth = 0;
        while (element != null) {
            depth++;
            element = element.parent;
        }
        return depth;
    }
    
    private static boolean sameElements(AstElement... elements) {
        for (int i = 1; i < elements.length; i++) {
            if (elements[i].id != elements[0].id) {
                return false;
            }
        }
        return true;
    }
    
    private static AstElement findCommonParent(AstElement... elements) {

        int minDepth = Integer.MAX_VALUE;
        for (AstElement element : elements) {
            int depth = depth(element);
            if (depth < minDepth) {
                minDepth = depth;
            }
        }
        
        for (int i = 0; i < elements.length; i++) {
            while (depth(elements[i]) > minDepth) {
                elements[i] = elements[i].parent;
            }
        }
        
        while (!sameElements(elements)) {
            for (int i = 0; i < elements.length; i++) {
                elements[i] = elements[i].parent;
            }
        }
        
        return elements[0];
    }
    
    private static class ExpressionCounter implements IExpressionVisitor<Void> {

        private static final Set<BinaryOperator> ASSIGNMENTS = new HashSet<>(Arrays.asList(
                BinaryOperator.ASSIGNMENT, BinaryOperator.ASSIGNMENT_AND, BinaryOperator.ASSIGNMENT_DIV,
                BinaryOperator.ASSIGNMENT_MINUS, BinaryOperator.ASSIGNMENT_MOD, BinaryOperator.ASSIGNMENT_MULT,
                BinaryOperator.ASSIGNMENT_OR, BinaryOperator.ASSIGNMENT_PLUS, BinaryOperator.ASSIGNMENT_SHL,
                BinaryOperator.ASSIGNMENT_SHR, BinaryOperator.ASSIGNMENT_XOR));
        
        private static final Set<UnaryOperator> INC_OR_DEC = new HashSet<>(Arrays.asList(
                UnaryOperator.POST_DEC, UnaryOperator.POST_INC, UnaryOperator.PRE_DEC, UnaryOperator.PRE_INC));
        
        private Map<Expression, Integer> count = new HashMap<>(1024);
        
        private Map<Expression, List<Expression>> elements = new HashMap<>(1024);
        
        private void addAndIncrement(Expression expr) {
            // don't consider top-level expressions in for loops
            // (this commonly breaks loop-unrolling)
            if (expr.parent instanceof For) {
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
            // don't count assignments
            if (!ASSIGNMENTS.contains(expr.operator)) {
                addAndIncrement(expr);
            }
            
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
            // ignore -literal, don't allow ++ or --
            if ((expr.operator != UnaryOperator.MINUS || !(expr.expr instanceof Literal))
                    && !INC_OR_DEC.contains(expr.operator)) {
                addAndIncrement(expr);
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
