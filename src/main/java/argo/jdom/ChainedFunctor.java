// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

final class ChainedFunctor implements Functor
{
    private final JsonNodeSelector parentJsonNodeSelector;
    private final JsonNodeSelector childJsonNodeSelector;
    
    ChainedFunctor(final JsonNodeSelector parentJsonNodeSelector, final JsonNodeSelector childJsonNodeSelector) {
        this.parentJsonNodeSelector = parentJsonNodeSelector;
        this.childJsonNodeSelector = childJsonNodeSelector;
    }
    
    public boolean matchesNode(final Object jsonNode) {
        return this.parentJsonNodeSelector.matches(jsonNode) && this.childJsonNodeSelector.matches(this.parentJsonNodeSelector.getValue(jsonNode));
    }
    
    public Object applyTo(final Object jsonNode) {
        Object value;
        try {
            value = this.parentJsonNodeSelector.getValue(jsonNode);
        }
        catch (final JsonNodeDoesNotMatchChainedJsonNodeSelectorException e) {
            throw JsonNodeDoesNotMatchChainedJsonNodeSelectorException.createUnchainedJsonNodeDoesNotMatchJsonNodeSelectorException(e, this.parentJsonNodeSelector);
        }
        Object value2;
        try {
            value2 = this.childJsonNodeSelector.getValue(value);
        }
        catch (final JsonNodeDoesNotMatchChainedJsonNodeSelectorException e2) {
            throw JsonNodeDoesNotMatchChainedJsonNodeSelectorException.createChainedJsonNodeDoesNotMatchJsonNodeSelectorException(e2, this.parentJsonNodeSelector);
        }
        return value2;
    }
    
    public String shortForm() {
        return this.childJsonNodeSelector.shortForm();
    }
    
    @Override
    public String toString() {
        return this.parentJsonNodeSelector.toString() + ", with " + this.childJsonNodeSelector.toString();
    }
}
