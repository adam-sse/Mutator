package net.ssehub.mutator.mutation.mutations;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    DeleteStatementTest.class,
    ElementReplacerTest.class,
    OverrideWithLiteralTest.class,
})
public class AllMutationTests {

}
