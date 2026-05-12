// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.Map;

final class JsonNodeSelectors_FieldFunctor extends LeafFunctor
{
    final /* synthetic */ JsonStringNode a;
    
    JsonNodeSelectors_FieldFunctor(final JsonStringNode qa) {
        this.a = qa;
    }
    
    public boolean matchesNode(final Map jsonNode) {
        return jsonNode.containsKey(this.a);
    }
    
    public String shortForm() {
        return "\"" + this.a.getText() + "\"";
    }
    
    public JsonNode typeSafeApplyTo(final Map jsonNode) {
        return jsonNode.get(this.a);
    }
    
    @Override
    public String toString() {
        return "a field called [\"" + this.a.getText() + "\"]";
    }
}
