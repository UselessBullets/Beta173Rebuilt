// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.Map;

final class JsonNodeSelectors_ObjectFunctor extends LeafFunctor
{
    public boolean matchesNode(final JsonNode jsonNode) {
        return JsonNodeType.OBJECT == jsonNode.getType();
    }
    
    public String shortForm() {
        return "A short form object";
    }
    
    public Map typeSafeApplyTo(final JsonNode jsonNode) {
        return jsonNode.getFields();
    }
    
    @Override
    public String toString() {
        return "an object";
    }
}
