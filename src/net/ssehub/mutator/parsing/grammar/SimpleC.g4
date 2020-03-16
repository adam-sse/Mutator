grammar SimpleC;

file
    : function*
;

function
    : type=declType name=IDENTIFIER '(' params=declList ')' body=stmtCompound
    ;

declList
    : declaration? (',' declaration)*
    ;

declaration
    : declType name=IDENTIFIER ('=' init=expr)?
    ;

declType
    : modifier=('unsigned'|'signed')? type=BASIC_TYPE ptr='*'?
    ;
    
stmtCompound
    : '{' stmt* '}'
    ;
    
stmt
    : stmtEmpty
    | stmtCompound
    | stmtBranch
    | stmtLoop
    | stmtJump
    | stmtReturn
    | stmtDeclaration
    | stmtExpr
    ;

stmtEmpty: ';';
stmtDeclaration: declaration ';';
stmtReturn: 'return' expr? ';'; 
stmtExpr: expr ';';
stmtJump: type=('break'|'continue') ';';

stmtBranch: 'if' '(' condition=expr ')' thenBody=stmt ('else' elseBody=stmt)?;

stmtLoop
    : stmtWhileLoop
    | stmtDoWhileLoop
    | stmtForLoop
    ;

stmtWhileLoop: 'while' '(' condition=expr ')' body=stmt;
stmtDoWhileLoop: 'do' body=stmt 'while' '(' condition=expr ')' ';';
stmtForLoop: 'for' '(' init=declaration? ';' condition=expr? ';' increment=expr? ')' body=stmt;

expr
    : '(' nested=expr ')'
    | var=IDENTIFIER
    | lit=LITERAL
    | var=IDENTIFIER op='(' params=exprList? ')'
    | var=IDENTIFIER op='[' r=expr ']'
    | l=expr post_op=('++'|'--')
    | op=('++'|'--'|'-'|'~'|'!') r=expr
    | l=expr op=('*'|'/'|'%') r=expr
    | l=expr op=('+'|'-') r=expr
    | l=expr op=('<<'|'>>') r=expr
    | l=expr op=('<'|'<='|'>'|'>='|'=='|'!=') r=expr
    | l=expr op='&' r=expr
    | l=expr op='^' r=expr
    | l=expr op='|' r=expr
    | l=expr op='&&' r=expr
    | l=expr op='||' r=expr
    | l=expr op='=' r=expr
    ;
    
exprList: expr (',' expr)*;

BASIC_TYPE
    : 'void' 
    | 'char'
    | 'short'
    | 'int'
    | 'long'
    | 'float'
    | 'double'
    | 'long' 'double'
    ;

LITERAL
    : '0'
    | '0x'[0-9a-fA-F]+
    | ([1-9][0-9]*)
    ;

IDENTIFIER: [a-zA-Z_][a-zA-Z_0-9]*;
WHITESPACE: [ \t\r\n]+ -> skip;
LINE_COMMENT: '//' ~[\r\n]* -> skip;
BLOCK_COMMENT: '/*' .*? '*/' -> skip;
