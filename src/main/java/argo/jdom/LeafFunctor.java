// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

abstract class LeafFunctor implements Functor
{
    public final Object applyTo(final Object jsonNode) {
        if (!this.matchesNode(jsonNode)) {
            throw JsonNodeDoesNotMatchChainedJsonNodeSelectorException.createJsonNodeDoesNotMatchJsonNodeSelectorException(this);
        }
        return this.typeSafeApplyTo(jsonNode);
    }
    
    protected abstract Object typeSafeApplyTo(final Object jsonNode);
}
