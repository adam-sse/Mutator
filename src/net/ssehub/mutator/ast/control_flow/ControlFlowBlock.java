package net.ssehub.mutator.ast.control_flow;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ssehub.mutator.ast.Expression;
import net.ssehub.mutator.ast.Statement;

public class ControlFlowBlock {

    private String name;
    
    private List<Statement> sequence;
    
    private Expression outCondition;
    
    private ControlFlowBlock outTrue;
    
    private ControlFlowBlock outFalse;
    
    private Set<ControlFlowBlock> incoming;
    
    private Set<String> calledFunctions;
    
    ControlFlowBlock(String name) {
        this.name = name;
        this.sequence = new LinkedList<>();
        this.incoming = new HashSet<>();
        this.calledFunctions = new HashSet<>();
    }
    
    public String getName() {
        return name;
    }
    
    public void addStatement(Statement statement) {
        this.sequence.add(statement);
    }
    
    public List<Statement> getSequence() {
        return Collections.unmodifiableList(this.sequence);
    }
    
    public void setOutTrue(ControlFlowBlock other) {
        if (this.outTrue != null) {
            this.outTrue.incoming.remove(this);
        }
        
        this.outTrue = other;
        other.incoming.add(this);
    }
    
    public ControlFlowBlock getOutTrue() {
        return outTrue;
    }
    
    public void setOutFalse(ControlFlowBlock other) {
        if (this.outFalse != null) {
            this.outFalse.incoming.remove(this);
        }
        
        this.outFalse = other;
        other.incoming.add(this);
    }
    
    public ControlFlowBlock getOutFalse() {
        return outFalse;
    }
    
    public void setOutCondition(Expression outCondition) {
        this.outCondition = outCondition;
    }
    
    public Expression getOutCondition() {
        return outCondition;
    }
    
    public Set<ControlFlowBlock> getIncoming() {
        return Collections.unmodifiableSet(incoming);
    }
    
    void removeIncoming(ControlFlowBlock previousIn) {
        this.incoming.remove(previousIn);
    }
    
    public void addCalledFunction(String functionName) {
        this.calledFunctions.add(functionName);
    }
    
    public Set<String> getCalledFunctions() {
        return Collections.unmodifiableSet(calledFunctions);
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        if (obj instanceof ControlFlowBlock) {
            ControlFlowBlock other = (ControlFlowBlock) obj;
            equal = this.name.equals(other.name);
        }
        return equal;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
}
