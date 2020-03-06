import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import mutation.mutations.DeleteStatementTest;
import mutation.mutations.ElementReplacerTest;
import mutation.mutations.OverrideWithLiteralTest;

@RunWith(Suite.class)
@SuiteClasses({
    DeleteStatementTest.class,
    ElementReplacerTest.class,
    OverrideWithLiteralTest.class,
})
public class AllTests {

}
