package net.ssehub.mutator.parsing.ast;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    AssignmentTest.class,
    BinaryExprTest.class,
    DeclarationStmtTest.class,
    DeclarationTest.class,
    EmptyStmtTest.class,
    ExpressionStmtTest.class,
    FunctionCallTest.class,
    IdentifierTest.class,
    LiteralTest.class,
    ReturnTest.class,
    TypeTest.class,
    UnaryExprTest.class,
})
public class AllAstTests {

}
