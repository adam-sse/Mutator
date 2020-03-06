package parsing.ast;

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
    
    OR("||", 1);
    
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
        }
        
        throw new IllegalArgumentException();
    }
    
    @Override
    public String toString() {
        return str;
    }
    
}
