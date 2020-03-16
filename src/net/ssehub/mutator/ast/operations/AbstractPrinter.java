package net.ssehub.mutator.ast.operations;

import java.util.StringJoiner;

import net.ssehub.mutator.ast.Assignment;
import net.ssehub.mutator.ast.BinaryExpr;
import net.ssehub.mutator.ast.BinaryOperator;
import net.ssehub.mutator.ast.Declaration;
import net.ssehub.mutator.ast.DeclarationStmt;
import net.ssehub.mutator.ast.EmptyStmt;
import net.ssehub.mutator.ast.Expression;
import net.ssehub.mutator.ast.ExpressionStmt;
import net.ssehub.mutator.ast.FunctionCall;
import net.ssehub.mutator.ast.Identifier;
import net.ssehub.mutator.ast.Literal;
import net.ssehub.mutator.ast.Return;
import net.ssehub.mutator.ast.Type;
import net.ssehub.mutator.ast.UnaryExpr;

abstract class AbstractPrinter implements IAstVisitor<String> {

    @Override
    public String visitAssignment(Assignment stmt) {
        return stmt.variable.accept(this) + " = " + stmt.value.accept(this) + ";";
    }
    
    @Override
    public String visitBinaryExpr(BinaryExpr expr) {
        StringBuilder sb = new StringBuilder();
        
        if (expr.left.getPrecedence() <= expr.getPrecedence()) {
            sb.append("(").append(expr.left.accept(this)).append(")");
        } else {
            sb.append(expr.left.accept(this));
        }
        
        if (expr.operator == BinaryOperator.ARRAY_ACCESS) {
            sb.append("[").append(expr.right.accept(this)).append("]");
            
        } else {
            

            sb.append(" ").append(expr.operator).append(" ");
            
            if (expr.right.getPrecedence() <= expr.getPrecedence()) {
                sb.append("(").append(expr.right.accept(this)).append(")");
            } else {
                sb.append(expr.right.accept(this));
            }
        }
        
        return sb.toString();
    }

    @Override
    public String visitDeclaration(Declaration decl) {
        StringBuilder sb = new StringBuilder();
        sb.append(decl.type.accept(this)).append(' ').append(decl.identifier);
        if (decl.initExpr != null) {
            sb.append(" = ").append(decl.initExpr.accept(this));
        }
        return sb.toString();
    }

    @Override
    public String visitDeclarationStmt(DeclarationStmt stmt) {
        return stmt.decl.accept(this) + ";";
    }

    @Override
    public String visitEmptyStmt(EmptyStmt stmt) {
        return ";";
    }

    @Override
    public String visitExpressionStmt(ExpressionStmt stmt) {
        return stmt.expr.accept(this) + ";";
    }

    @Override
    public String visitFunctionCall(FunctionCall expr) {
        StringJoiner sj = new StringJoiner(", ", expr.function + "(", ")");
        for (Expression param : expr.params) {
            sj.add(param.accept(this));
        }
        return sj.toString();
    }

    @Override
    public String visitIdentifier(Identifier expr) {
        return expr.identifier;
    }

    public String visitLiteral(Literal expr) {
        return expr.value;
    }

    @Override
    public String visitReturn(Return stmt) {
        return "return" + (stmt.value != null ? " " + stmt.value.accept(this) : "") + ";";
    }

    @Override
    public String visitType(Type type) {
        StringBuilder sb = new StringBuilder();
        
        if (type.modifier != null) {
            sb.append(type.modifier.name().toLowerCase()).append(" ");
        }
        
        sb.append(type.type.str);
        
        if (type.pointer) {
            sb.append("*");
        }
        
        return sb.toString();
    }

    @Override
    public String visitUnaryExpr(UnaryExpr expr) {
        StringBuilder sb = new StringBuilder();
        
        if (expr.operator.prefix) {
            sb.append(expr.operator);
        }
            
        if (expr.expr.getPrecedence() <= expr.getPrecedence()) {
            sb.append("(").append(expr.expr.accept(this)).append(")");
        } else {
            sb.append(expr.expr.accept(this));
        }

        if (!expr.operator.prefix) {
            sb.append(expr.operator);
        }
        
        return sb.toString();
    }

}
