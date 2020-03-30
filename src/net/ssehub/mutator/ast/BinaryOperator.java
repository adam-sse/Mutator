package net.ssehub.mutator.ast;

public enum BinaryOperator {

    ARRAY_ACCESS("[]", 12, false),

    MULTIPLICATION("*", 9, false), DIVISION("/", 9, false), MODULO("%", 9, false),

    ADDITION("+", 8, false), SUBTRACTION("-", 8, false),

    SHIFT_LEFT("<<", 7, false), SHIFT_RIGHT(">>", 7, false),

    CMP_LOWER("<", 6, false), CMP_LOWER_EQUAL("<=", 6, false), CMP_GREATER(">", 6, false),
    CMP_GREATER_EQUAL(">=", 6, false), CMP_EQUAL("==", 6, false), CMP_NOT_EQUAL("!=", 6, false),

    BIT_AND("&", 5, false),

    BIT_XOR("^", 4, false),

    BIT_OR("|", 3, false),

    AND("&&", 2, false),

    OR("||", 1, false),

    ASSIGNMENT("=", 0, true), ASSIGNMENT_PLUS("+=", 0, true), ASSIGNMENT_MINUS("-=", 0, true),
    ASSIGNMENT_MULT("*=", 0, true), ASSIGNMENT_DIV("/=", 0, true), ASSIGNMENT_MOD("%=", 0, true),
    ASSIGNMENT_SHL("<<=", 0, true), ASSIGNMENT_SHR(">>=", 0, true), ASSIGNMENT_AND("&=", 0, true),
    ASSIGNMENT_XOR("^=", 0, true), ASSIGNMENT_OR("|=", 0, true);

    public String str;

    /**
     * Used for printing parenthesis only. Higher precedence means evaluated first.
     * That means, an argument with lower or equal precedence needs parenthesis.
     */
    int precedence;

    private boolean isAssignment;

    private BinaryOperator(String str, int precedence, boolean isAssignment) {
        this.str = str;
        this.precedence = precedence;
        this.isAssignment = isAssignment;
    }

    public boolean isAssignment() {
        return this.isAssignment;
    }

    public static BinaryOperator get(String op) {
        switch (op) {
        case "*":
            return MULTIPLICATION;

        case "/":
            return DIVISION;

        case "%":
            return MODULO;

        case "+":
            return ADDITION;

        case "-":
            return SUBTRACTION;

        case "<<":
            return SHIFT_LEFT;

        case ">>":
            return SHIFT_RIGHT;

        case "<":
            return CMP_LOWER;

        case "<=":
            return CMP_LOWER_EQUAL;

        case ">":
            return CMP_GREATER;

        case ">=":
            return CMP_GREATER_EQUAL;

        case "==":
            return CMP_EQUAL;

        case "!=":
            return CMP_NOT_EQUAL;

        case "&":
            return BIT_AND;

        case "^":
            return BIT_XOR;

        case "|":
            return BIT_OR;

        case "&&":
            return AND;

        case "||":
            return OR;

        case "=":
            return ASSIGNMENT;

        case "+=":
            return ASSIGNMENT_PLUS;

        case "-=":
            return ASSIGNMENT_MINUS;

        case "*=":
            return ASSIGNMENT_MULT;

        case "/=":
            return ASSIGNMENT_DIV;

        case "%=":
            return ASSIGNMENT_MOD;

        case "<<=":
            return ASSIGNMENT_SHL;

        case ">>=":
            return ASSIGNMENT_SHR;

        case "&=":
            return ASSIGNMENT_AND;

        case "^=":
            return ASSIGNMENT_XOR;

        case "|=":
            return ASSIGNMENT_OR;

        default:
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return str;
    }

}
