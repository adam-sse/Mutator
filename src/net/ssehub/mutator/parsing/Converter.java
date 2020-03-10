package net.ssehub.mutator.parsing;

import java.util.Deque;
import java.util.LinkedList;

import net.ssehub.mutator.parsing.ast.Assignment;
import net.ssehub.mutator.parsing.ast.AstElement;
import net.ssehub.mutator.parsing.ast.BasicType;
import net.ssehub.mutator.parsing.ast.BinaryExpr;
import net.ssehub.mutator.parsing.ast.BinaryOperator;
import net.ssehub.mutator.parsing.ast.Block;
import net.ssehub.mutator.parsing.ast.Declaration;
import net.ssehub.mutator.parsing.ast.DeclarationStmt;
import net.ssehub.mutator.parsing.ast.DoWhileLoop;
import net.ssehub.mutator.parsing.ast.EmptyStmt;
import net.ssehub.mutator.parsing.ast.Expression;
import net.ssehub.mutator.parsing.ast.ExpressionStmt;
import net.ssehub.mutator.parsing.ast.File;
import net.ssehub.mutator.parsing.ast.Function;
import net.ssehub.mutator.parsing.ast.FunctionCall;
import net.ssehub.mutator.parsing.ast.Identifier;
import net.ssehub.mutator.parsing.ast.If;
import net.ssehub.mutator.parsing.ast.Literal;
import net.ssehub.mutator.parsing.ast.Loop;
import net.ssehub.mutator.parsing.ast.Return;
import net.ssehub.mutator.parsing.ast.Statement;
import net.ssehub.mutator.parsing.ast.Type;
import net.ssehub.mutator.parsing.ast.Type.Modifier;
import net.ssehub.mutator.parsing.ast.UnaryExpr;
import net.ssehub.mutator.parsing.ast.UnaryOperator;
import net.ssehub.mutator.parsing.ast.While;
import net.ssehub.mutator.parsing.grammar.SimpleCParser.DeclTypeContext;
import net.ssehub.mutator.parsing.grammar.SimpleCParser.DeclarationContext;
import net.ssehub.mutator.parsing.grammar.SimpleCParser.ExprContext;
import net.ssehub.mutator.parsing.grammar.SimpleCParser.FileContext;
import net.ssehub.mutator.parsing.grammar.SimpleCParser.FunctionContext;
import net.ssehub.mutator.parsing.grammar.SimpleCParser.StmtAssignmentContext;
import net.ssehub.mutator.parsing.grammar.SimpleCParser.StmtBranchContext;
import net.ssehub.mutator.parsing.grammar.SimpleCParser.StmtCompoundContext;
import net.ssehub.mutator.parsing.grammar.SimpleCParser.StmtContext;
import net.ssehub.mutator.parsing.grammar.SimpleCParser.StmtDeclarationContext;
import net.ssehub.mutator.parsing.grammar.SimpleCParser.StmtDoWhileLoopContext;
import net.ssehub.mutator.parsing.grammar.SimpleCParser.StmtEmptyContext;
import net.ssehub.mutator.parsing.grammar.SimpleCParser.StmtExprContext;
import net.ssehub.mutator.parsing.grammar.SimpleCParser.StmtLoopContext;
import net.ssehub.mutator.parsing.grammar.SimpleCParser.StmtReturnContext;
import net.ssehub.mutator.parsing.grammar.SimpleCParser.StmtWhileLoopContext;

public class Converter {

    private Deque<AstElement> parents;
    
    public File convert(FileContext tree) {
        parents = new LinkedList<>();
        parents.push(null);
        return convertFile(tree);
    }
    
    private File convertFile(FileContext tree) {
        File f = new File(parents.peek());
        f.initLocation(tree.start, tree.stop);
        parents.push(f);

        for (FunctionContext child : tree.function()) {
            f.functions.add(convertFunction(child));
        }
        
        parents.pop();
        return f;
    }
    
    private Function convertFunction(FunctionContext tree) {
        Function f = new Function(parents.peek());
        f.initLocation(tree.start, tree.stop);
        parents.push(f);
        
        f.name = tree.name.getText();
        f.type = convertType(tree.type);
        
        for (DeclarationContext declTree : tree.declList().declaration()) {
            f.parameters.add(convertDeclaration(declTree));
        }
        
        f.body = convertCompoundStatement(tree.body);

        parents.pop();
        return f;
    }

    private Statement convertStatement(StmtContext tree) {
        Statement result = null;
        
        if (tree.stmtAssignment() != null) {
            result = convertAssignment(tree.stmtAssignment());
        } else if (tree.stmtBranch() != null) {
            result = convertBranchStatement(tree.stmtBranch());
        } else if (tree.stmtLoop() != null) {
            result = convertLoopStatement(tree.stmtLoop());
        } else if (tree.stmtDeclaration() != null) {
            result = convertDeclaration(tree.stmtDeclaration());
        } else if (tree.stmtExpr() != null) {
            result = convertExpressionStatement(tree.stmtExpr());
        } else if (tree.stmtReturn() != null) {
            result = convertReturnStatement(tree.stmtReturn());
        } else if (tree.stmtCompound() != null) {
            result = convertCompoundStatement(tree.stmtCompound());
        } else if (tree.stmtEmpty() != null) {
            result = convertEmptyStatement(tree.stmtEmpty());
        } else {
            if (tree.children.size() >= 1) {
                throw new IllegalArgumentException(tree.getChild(0).getClass().getName());
            } else {
                throw new IllegalArgumentException(tree.getClass().getName());
            }
        }
        
        return result;
    }
    
    private Assignment convertAssignment(StmtAssignmentContext tree) {
        Assignment stmt = new Assignment(parents.peek());
        stmt.initLocation(tree.start, tree.stop);
        parents.push(stmt);
        
        stmt.variable = convertExpression(tree.var);
        stmt.value = convertExpression(tree.value);
        
        parents.pop();
        return stmt;
    }
    
    private If convertBranchStatement(StmtBranchContext tree) {
        If stmt = new If(parents.peek());
        stmt.initLocation(tree.start, tree.stop);
        parents.push(stmt);
        
        stmt.condition = convertExpression(tree.condition);
        stmt.thenBlock = convertStatement(tree.thenBody);
        if (tree.elseBody != null) {
            stmt.elseBlock = convertStatement(tree.elseBody);
        }
        
        parents.pop();
        return stmt;
    }
    
    private Loop convertLoopStatement(StmtLoopContext tree) {
        Loop result;
        
        if (tree.stmtWhileLoop() != null) {
            result = convertWhileLoop(tree.stmtWhileLoop());
        } else if (tree.stmtDoWhileLoop() != null) {
            result = convertDoWhileLoop(tree.stmtDoWhileLoop());
        } else {
            if (tree.children.size() >= 1) {
                throw new IllegalArgumentException(tree.getChild(0).getClass().getName());
            } else {
                throw new IllegalArgumentException(tree.getClass().getName());
            }
        }
        
        return result;
    }
    
    private While convertWhileLoop(StmtWhileLoopContext tree) {
        While loop = new While(parents.peek());
        loop.initLocation(tree.start, tree.stop);
        parents.push(loop);
        
        loop.condition = convertExpression(tree.condition);
        loop.body = convertStatement(tree.body);
        
        parents.pop();
        return loop;
    }
    
    private DoWhileLoop convertDoWhileLoop(StmtDoWhileLoopContext tree) {
        DoWhileLoop loop = new DoWhileLoop(parents.peek());
        loop.initLocation(tree.start, tree.stop);
        parents.push(loop);
        
        loop.condition = convertExpression(tree.condition);
        loop.body = convertStatement(tree.body);
        
        parents.pop();
        return loop;
    }
    
    private DeclarationStmt convertDeclaration(StmtDeclarationContext tree) {
        DeclarationStmt stmt = new DeclarationStmt(parents.peek());
        stmt.initLocation(tree.start, tree.stop);
        parents.push(stmt);
        
        stmt.decl = convertDeclaration(tree.declaration());
        
        parents.pop();
        return stmt;
    }

    private ExpressionStmt convertExpressionStatement(StmtExprContext tree) {
        ExpressionStmt stmt = new ExpressionStmt(parents.peek());
        stmt.initLocation(tree.start, tree.stop);
        parents.push(stmt);
        
        stmt.expr = convertExpression(tree.expr());
        
        parents.pop();
        return stmt;
    }
    
    private Return convertReturnStatement(StmtReturnContext tree) {
        Return stmt = new Return(parents.peek());
        stmt.initLocation(tree.start, tree.stop);
        parents.push(stmt);
        
        if (tree.expr() != null) {
            stmt.value = convertExpression(tree.expr());
        }
        
        parents.pop();
        return stmt;
    }
    
    private Block convertCompoundStatement(StmtCompoundContext tree) {
        Block block = new Block(parents.peek());
        block.initLocation(tree.start, tree.stop);
        parents.push(block);
        
        for (StmtContext child : tree.stmt()) {
            block.statements.add(convertStatement(child));
        }
        
        parents.pop();
        return block;
    }
    
    private EmptyStmt convertEmptyStatement(StmtEmptyContext tree) {
        EmptyStmt stmt = new EmptyStmt(parents.peek());
        stmt.initLocation(tree.start, tree.stop);
        return stmt;
    }
    
    private Expression convertExpression(ExprContext tree) {
        Expression result;
        
        if (tree.op != null && tree.op.getText().equals("[")) {
            // array access
            BinaryExpr expr = new BinaryExpr(parents.peek());
            expr.initLocation(tree.start, tree.stop);
            parents.push(expr);
            
            Identifier var = new Identifier(parents.peek());
            var.initLocation(tree.var, tree.var);
            var.identifier = tree.var.getText();
            
            expr.left = var;
            expr.operator = BinaryOperator.ARRAY_ACCESS;
            expr.right = convertExpression(tree.r);
            
            parents.pop();
            result = expr;
            
        } else if (tree.op != null && tree.op.getText().equals("(")) {
            // function call
            FunctionCall call = new FunctionCall(parents.peek());
            call.initLocation(tree.start, tree.stop);
            parents.push(call);
            
            call.function = tree.var.getText();
            if (tree.params != null) {
                for (ExprContext expr : tree.params.expr()) {
                    call.params.add(convertExpression(expr));
                }
            }
            
            parents.pop();
            result = call;
            
        } else if (tree.lit != null) {
            // literal value
            Literal expr = new Literal(parents.peek());
            expr.initLocation(tree.start, tree.stop);
            expr.value = tree.lit.getText();
            
            result = expr;
            
        } else if (tree.var != null) {
            // single identifier
            Identifier expr = new Identifier(parents.peek());
            expr.initLocation(tree.start, tree.stop);
            expr.identifier = tree.var.getText();
            
            result = expr;

        } else if (tree.post_op != null) {
            // post ++ or --
            UnaryExpr expr = new UnaryExpr(parents.peek());
            expr.initLocation(tree.start, tree.stop);
            parents.push(expr);
            
            expr.operator = (tree.post_op.getText().equals("++") ? UnaryOperator.POST_INC : UnaryOperator.POST_DEC);
            expr.expr = convertExpression(tree.l);
            
            parents.pop();
            result = expr;
            
        } else if (tree.op != null && tree.l != null && tree.r != null) {
            // binary operator
            BinaryExpr expr = new BinaryExpr(parents.peek());
            expr.initLocation(tree.start, tree.stop);
            parents.push(expr);
            
            expr.left = convertExpression(tree.l);
            expr.operator = BinaryOperator.get(tree.op.getText());
            expr.right = convertExpression(tree.r);
            
            parents.pop();
            result = expr;
            
        } else if (tree.op != null && tree.l == null && tree.r != null) {
            // unary operator (prefix)
            UnaryExpr expr = new UnaryExpr(parents.peek());
            expr.initLocation(tree.start, tree.stop);
            parents.push(expr);
            
            expr.operator = UnaryOperator.get(tree.op.getText());
            expr.expr = convertExpression(tree.r);
            
            parents.pop();
            result = expr;
            
        } else if (tree.nested != null) {
            // nested ( ) 
            result = convertExpression(tree.nested);
            
        } else {
            throw new IllegalArgumentException();
        }
        
        return result;
    }
    
    private Declaration convertDeclaration(DeclarationContext tree) {
        Declaration decl = new Declaration(parents.peek());
        decl.initLocation(tree.start, tree.stop);
        parents.push(decl);
        
        decl.identifier = tree.name.getText();
        decl.type = convertType(tree.declType());
        
        parents.pop();
        return decl;
    }
    
    private Type convertType(DeclTypeContext tree) {
        Type type = new Type(parents.peek());
        type.initLocation(tree.start, tree.stop);
        parents.push(type);
        
        type.type = BasicType.get(tree.type.getText());
        type.pointer = tree.ptr != null;
        
        if (tree.modifier != null) {
            if (tree.modifier.getText().equals("unsigned")) {
                type.modifier = Modifier.UNSIGNED;
            } else if (tree.modifier.getText().equals("signed")) {
                type.modifier = Modifier.SIGNED;
            } else {
                throw new IllegalArgumentException();
            }
        }

        parents.pop();
        return type;
    }
    
}