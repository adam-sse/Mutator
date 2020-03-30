package net.ssehub.mutator.mutation.genetic.mutations;

import java.util.List;
import java.util.Random;

import net.ssehub.mutator.ast.File;

public class MutationFactory {

    private List<String> allowedMutations;

    public MutationFactory(List<String> allowedMutations) {
        this.allowedMutations = allowedMutations;
    }

    public Mutation createRandomMutation(File ast, Random random) {
        Mutation result;
        do {
            result = createMutation(allowedMutations.get(random.nextInt(allowedMutations.size())), ast, random);
        } while (result == null);
        return result;
    }

    private Mutation createMutation(String name, File ast, Random random) {
        try {
            Class<?> cls = Class.forName("net.ssehub.mutator.mutation.genetic.mutations." + name);
            return (Mutation) cls.getMethod("find", File.class, Random.class).invoke(null, ast, random);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Invalid mutation name: " + name);
        }
    }

}
