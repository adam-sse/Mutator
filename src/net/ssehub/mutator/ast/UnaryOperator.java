package net.ssehub.mutator.ast;

public enum UnaryOperator {

    PRE_INC("++", true, 10),

    POST_INC("++", false, 11),

    PRE_DEC("--", true, 10),

    POST_DEC("--", false, 11),

    NEGATION("!", true, 10),

    MINUS("-", true, 10),

    BIT_NEGATION("~", true, 10);

    public String str;

    public boolean prefix;

    /**
     * Used for printing parenthesis only. Higher precedence means evaluated first.
     * That means, an argument with lower or equal precedence needs parenthesis.
     */
    int precedence;

    private UnaryOperator(String str, boolean prefix, int precedence) {
        this.str = str;
        this.prefix = prefix;
        this.precedence = precedence;
    }

    public static UnaryOperator get(String op) {
        switch (op) {
        case "++":
            return PRE_INC;

        case "--":
            return PRE_DEC;

        case "!":
            return NEGATION;

        case "-":
            return MINUS;

        case "~":
            return BIT_NEGATION;

        default:
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return str;
    }

}
