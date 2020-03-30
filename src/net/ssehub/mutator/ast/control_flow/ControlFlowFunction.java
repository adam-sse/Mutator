package net.ssehub.mutator.ast.control_flow;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ControlFlowFunction {

    private String name;

    private String header;

    private ControlFlowBlock start;

    private ControlFlowBlock end;

    private Set<ControlFlowBlock> allBlocks;

    private int nextId;

    public ControlFlowFunction(String name, String header) {
        this.name = name;
        this.header = header;
        this.allBlocks = new HashSet<>();

        this.start = new ControlFlowBlock(this.name + " bStart");
        this.end = new ControlFlowBlock(this.name + " bEnd");

        this.allBlocks.add(this.start);
        this.allBlocks.add(this.end);
    }

    public String getName() {
        return this.name;
    }

    public String getHeader() {
        return this.header;
    }

    public ControlFlowBlock getStartBlock() {
        return this.start;
    }

    public ControlFlowBlock getEndBlock() {
        return this.end;
    }

    private String createNewName() {
        return this.name + " b" + (++this.nextId);
    }

    public ControlFlowBlock createBlock() {
        ControlFlowBlock block = new ControlFlowBlock(createNewName());
        this.allBlocks.add(block);
        return block;
    }

    public void removeBlock(ControlFlowBlock block) {
        this.allBlocks.remove(block);

        for (ControlFlowBlock other : this.allBlocks) {
            other.removeIncoming(block);
        }
    }

    /**
     * All blocks, including entry.
     */
    public Set<ControlFlowBlock> getAllBlocks() {
        return Collections.unmodifiableSet(this.allBlocks);
    }

}
