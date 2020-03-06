package parsing.ast;

import java.util.function.BiFunction;

public abstract class Statement extends AstElement {

    public Statement(AstElement parent) {
        super(parent);
    }
    
    @Override
    public abstract Statement cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct);

}
