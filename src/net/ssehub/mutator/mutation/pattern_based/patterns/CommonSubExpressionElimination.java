package net.ssehub.mutator.mutation.pattern_based.patterns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.BasicType;
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
import net.ssehub.mutator.mutation.genetic.mutations.ElementReplacer;
import net.ssehub.mutator.mutation.genetic.mutations.StatementInserter;

public class CommonSubExpressionElimination implements IOpportunity {

    private long parentId;
    
    private List<Long> expressionIds;
    
    private String exprTxt;
    
    private CommonSubExpressionElimination(long parentId, List<Long> expressionIds, String exprTxt) {
        this.parentId = parentId;
        this.expressionIds = expressionIds;
        this.exprTxt = exprTxt;
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
            List<Expression> expressions = new ArrayList<>(expressionIds.size());
            List<Statement> statements = new ArrayList<>(expressionIds.size());
            for (long id : expressionIds) {
                Expression expr = (Expression) ast.accept(new IdFinder(id));
                expressions.add(expr);
                statements.add(findParentStatement(expr));
            }
            
            // 2) find the statement with the lowest id (~= first statement)
            Statement firstStatement = Collections.min(statements, (s1, s2) -> Long.compare(s1.id, s2.id));
            
            // 3) create a declaration and insert before first statement
            String tempVar = "mutator_tmp_" + (int) (Math.random() * Integer.MAX_VALUE);
            
            DeclarationStmt declStmt = new DeclarationStmt(firstStatement.parent);
            Declaration decl = new Declaration(declStmt);
            Type type = new Type(decl);
            // TODO: find out correct type
            type.type = BasicType.DOUBLE;
            
            decl.type = type;
            decl.identifier = tempVar;
            decl.initExpr = (Expression) expressions.get(0).accept(new AstCloner(decl, false));
            declStmt.decl = decl;
            
            StatementInserter inserter = new StatementInserter();
            inserter.insert(firstStatement, true, declStmt);
            
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
        return "CommonSubExpressionElimination(parent=#" + parentId + ", expr=\'" + exprTxt + "\', exprIds=" + sj + ")";
    }
    
    public static List<CommonSubExpressionElimination> findOpportunities(File ast) {
        
        ExpressionCounter counter = new ExpressionCounter();
        ast.accept(new FullVisitor(counter));
        
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
            
            List<Long> exprIds = new ArrayList<>(expressions.size());
            for (Expression expr : expressions) {
                exprIds.add(expr.id);
            }
            
            result.add(new CommonSubExpressionElimination(commonParent.id, exprIds, key.getText()));
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
    
    private static class ExpressionCounter implements IAstVisitor<Void> {

        private Map<Expression, Integer> count = new HashMap<>(1024);
        
        private Map<Expression, List<Expression>> elements = new HashMap<>(1024);
        
        private void addAndIncrement(Expression expr) {
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

        @Override
        public Void visitFor(For stmt) {
            return null;
        }

        @Override
        public Void visitFunction(Function func) {
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
        public Void visitIf(If stmt) {
            return null;
        }

        @Override
        public Void visitJumpStmt(JumpStmt stmt) {
            return null;
        }

        @Override
        public Void visitLiteral(Literal expr) {
            // don't add single literals
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
            // ignore -literal
            if (expr.operator != UnaryOperator.MINUS || !(expr.expr instanceof Literal)) {
                addAndIncrement(expr);
            }
            
            return null;
        }

        @Override
        public Void visitWhile(While stmt) {
            return null;
        }
        
    }
    
    private static class ExpressionComplexityMeasuerer implements IAstVisitor<Integer> {

        @Override
        public Integer visitBinaryExpr(BinaryExpr expr) {
            return 1 + expr.left.accept(this) + expr.right.accept(this);
        }

        @Override
        public Integer visitBlock(Block stmt) {
            return null;
        }

        @Override
        public Integer visitDeclaration(Declaration decl) {
            return null;
        }

        @Override
        public Integer visitDeclarationStmt(DeclarationStmt stmt) {
            return null;
        }

        @Override
        public Integer visitDoWhileLoop(DoWhileLoop stmt) {
            return null;
        }

        @Override
        public Integer visitEmptyStmt(EmptyStmt stmt) {
            return null;
        }

        @Override
        public Integer visitExpressionStmt(ExpressionStmt stmt) {
            return null;
        }

        @Override
        public Integer visitFile(File file) {
            return null;
        }

        @Override
        public Integer visitFor(For stmt) {
            return null;
        }

        @Override
        public Integer visitFunction(Function func) {
            return null;
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
        public Integer visitIf(If stmt) {
            return null;
        }

        @Override
        public Integer visitJumpStmt(JumpStmt stmt) {
            return null;
        }

        @Override
        public Integer visitLiteral(Literal expr) {
            return 1;
        }

        @Override
        public Integer visitReturn(Return stmt) {
            return null;
        }

        @Override
        public Integer visitType(Type type) {
            return null;
        }

        @Override
        public Integer visitUnaryExpr(UnaryExpr expr) {
            return 1 + expr.expr.accept(this);
        }

        @Override
        public Integer visitWhile(While stmt) {
            return null;
        }
        
    }

}
