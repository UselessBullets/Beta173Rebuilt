// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

public final class JsonNodeSelector
{
    final Functor valueGetter;
    
    JsonNodeSelector(final Functor valueGetter) {
        this.valueGetter = valueGetter;
    }
    
    public boolean matches(final Object jsonNode) {
        return this.valueGetter.matchesNode(jsonNode);
    }
    
    public Object getValue(final Object argument) {
        return this.valueGetter.applyTo(argument);
    }
    
    public JsonNodeSelector with(final JsonNodeSelector childJsonNodeSelector) {
        return new JsonNodeSelector(new ChainedFunctor(this, childJsonNodeSelector));
    }
    
    String shortForm() {
        return this.valueGetter.shortForm();
    }
    
    @Override
    public String toString() {
        return this.valueGetter.toString();
    }
}
