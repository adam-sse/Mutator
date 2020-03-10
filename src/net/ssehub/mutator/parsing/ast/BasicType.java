package net.ssehub.mutator.parsing.ast;

public enum BasicType {

    VOID("void"),
    
    CHAR("char"),
    SHORT("short"),
    INT("int"),
    LONG("long"),
    
    FLOAT("float"),
    DOUBLE("double"),
    LONG_DOUBLE("long double");
    
    public String str;
    
    private BasicType(String str) {
        this.str = str;
    }
    
    public static BasicType get(String str) {
        switch (str) {
        case "void": return VOID;
        
        case "char": return CHAR;
        case "short": return SHORT;
        case "int": return INT;
        case "long": return LONG;
        
        case "float": return FLOAT;
        case "double": return DOUBLE;
        case "longdouble": return LONG_DOUBLE;
        default: throw new IllegalArgumentException();
        }
    }
    
}
