package net.ssehub.mutator.mutation.pattern_based.patterns;

import net.ssehub.mutator.ast.File;

public interface IOpportunity {

    public void apply(int param, File ast);

    public int getMinParam();

    public int getDefaultParam();

    public int getMaxParam();

}
