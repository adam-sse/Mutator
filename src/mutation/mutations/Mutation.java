package mutation.mutations;

import java.util.List;

import parsing.ast.AstElement;

public abstract class Mutation {

    protected List<String> diff;
    
    public abstract boolean apply(AstElement ast);
    
    /**
     * Creates a diff-like representation for the last {@link #apply(AstElement)} call.
     * 
     * @return The diff; a list of lines. May be <code>null</code> if {@link #apply(AstElement)} wasn't called yet or was not successful.
     */
    public List<String> getDiff() {
        return diff;
    }
    
    @Override
    public abstract String toString();
    
    @Override
    public abstract boolean equals(Object obj);
    
}
