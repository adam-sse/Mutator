package net.ssehub.mutator.ast;

public enum BinaryOperator {

    ARRAY_ACCESS("[]", 12),
    
    MULTIPLICATION("*", 9),
    DIVISION("/", 9),
    MODULO("%", 9),
    
    ADDITION("+", 8),
    SUBTRACTION("-", 8),

    SHIFT_LEFT("<<", 7),
    SHIFT_RIGHT(">>", 7),
    
    CMP_LOWER("<", 6),
    CMP_LOWER_EQUAL("<=", 6),
    CMP_GREATER(">", 6),
    CMP_GREATER_EQUAL(">=", 6),
    CMP_EQUAL("==", 6),
    CMP_NOT_EQUAL("!=", 6),
    
    BIT_AND("&", 5),
    
    BIT_XOR("^", 4),
    
    BIT_OR("|", 3),
    
    AND("&&", 2),
    
    OR("||", 1),
    
    ASSIGNMENT("=", 0),
    ASSIGNMENT_PLUS("+=", 0),
    ASSIGNMENT_MINUS("-=", 0),
    ASSIGNMENT_MULT("*=", 0),
    ASSIGNMENT_DIV("/=", 0),
    ASSIGNMENT_MOD("%=", 0),
    ASSIGNMENT_SHL("<<=", 0),
    ASSIGNMENT_SHR(">>=", 0),
    ASSIGNMENT_AND("&=", 0),
    ASSIGNMENT_XOR("^=", 0),
    ASSIGNMENT_OR("|=", 0);
    
    public String str;
    
    /**
     * Used for printing parenthesis only. Higher precedence means evaluated first. That means,
     * an argument with lower or equal precedence needs parenthesis.
     */
    int precedence;
    
    private BinaryOperator(String str, int precedence) {
        this.str = str;
        this.precedence = precedence;
    }
    
    public static BinaryOperator get(String op) {
        switch (op) {
        case "*": return MULTIPLICATION;
        case "/": return DIVISION;
        case "%": return MODULO;
        
        case "+": return ADDITION;
        case "-": return SUBTRACTION;
        
        case "<<": return SHIFT_LEFT;
        case ">>": return SHIFT_RIGHT;
        
        case "<": return CMP_LOWER;
        case "<=": return CMP_LOWER_EQUAL;
        case ">": return CMP_GREATER;
        case ">=": return CMP_GREATER_EQUAL;
        case "==": return CMP_EQUAL;
        case "!=": return CMP_NOT_EQUAL;
        
        case "&": return BIT_AND;
        
        case "^": return BIT_XOR;
        
        case "|": return BIT_OR;
        
        case "&&": return AND;
        
        case "||": return OR;
        
        case "=": return ASSIGNMENT;
        case "+=": return ASSIGNMENT_PLUS;
        case "-=": return ASSIGNMENT_MINUS;
        case "*=": return ASSIGNMENT_MULT;
        case "/=": return ASSIGNMENT_DIV;
        case "%=": return ASSIGNMENT_MOD;
        case "<<=": return ASSIGNMENT_SHL;
        case ">>=": return ASSIGNMENT_SHR;
        case "&=": return ASSIGNMENT_AND;
        case "^=": return ASSIGNMENT_XOR;
        case "|=": return ASSIGNMENT_OR;
        
        default: throw new IllegalArgumentException();
        }
    }
    
    @Override
    public String toString() {
        return str;
    }
    
}
