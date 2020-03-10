package net.ssehub.mutator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.ssehub.mutator.mutation.mutations.AllMutationTests;

@RunWith(Suite.class)
@SuiteClasses({
    AllMutationTests.class,
})
public class AllTests {

}
