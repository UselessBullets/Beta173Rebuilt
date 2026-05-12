// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

final class JsonNodeSelectors_StringFunctor extends LeafFunctor
{
    public boolean matchesNode(final JsonNode jsonNode) {
        return JsonNodeType.STRING == jsonNode.getType();
    }
    
    public String shortForm() {
        return "A short form string";
    }
    
    public String typeSafeApplyTo(final JsonNode jsonNode) {
        return jsonNode.getText();
    }
    
    @Override
    public String toString() {
        return "a value that is a string";
    }
}
