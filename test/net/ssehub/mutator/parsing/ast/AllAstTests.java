package net.ssehub.mutator.parsing.ast;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    AssignmentTest.class,
    BinaryExprTest.class,
    DeclarationTest.class,
    IdentifierTest.class,
    LiteralTest.class,
    TypeTest.class,
    UnaryExprTest.class,
})
public class AllAstTests {

}
