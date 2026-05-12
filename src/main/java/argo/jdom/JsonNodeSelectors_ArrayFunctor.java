// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.List;

final class JsonNodeSelectors_ArrayFunctor extends LeafFunctor
{
    public boolean matchesNode(final JsonNode jsonNode) {
        return JsonNodeType.ARRAY == jsonNode.getType();
    }
    
    public String shortForm() {
        return "A short form array";
    }
    
    public List typeSafeApplyTo(final JsonNode jsonNode) {
        return jsonNode.getElements();
    }
    
    @Override
    public String toString() {
        return "an array";
    }
}
