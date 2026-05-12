// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.List;

final class JsonNodeSelectors_ElementFunctor extends LeafFunctor
{
    final /* synthetic */ int a;
    
    JsonNodeSelectors_ElementFunctor(final int integer) {
        this.a = integer;
    }
    
    public boolean matchesNode(final List jsonNode) {
        return jsonNode.size() > this.a;
    }
    
    public String shortForm() {
        return Integer.toString(this.a);
    }
    
    public JsonNode typeSafeApplyTo(final List jsonNode) {
        return jsonNode.get(this.a);
    }
    
    @Override
    public String toString() {
        return "an element at index [" + this.a + "]";
    }
}
