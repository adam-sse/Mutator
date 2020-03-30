package net.ssehub.mutator.mutation;

import java.io.File;
import java.io.IOException;

public interface IMutant {

    public String getId();

    public void write(File destination) throws IOException;

}
