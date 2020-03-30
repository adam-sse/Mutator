package net.ssehub.mutator.mutation;

import java.util.List;

import net.ssehub.mutator.ast.File;

public interface IMutator extends IFitnessStore {

    public List<IMutant> run(File ast);

    public void printStatistics();

    public String getUnmodifiedId();

}
