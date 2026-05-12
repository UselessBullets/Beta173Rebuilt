// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

class JsonListenerToJdomAdapter_FieldNodeContainer implements JsonListenerToJdomAdapter_NodeContainer
{
    final /* synthetic */ JsonFieldBuilder a;
    final /* synthetic */ JsonListenerToJdomAdapter b;
    
    JsonListenerToJdomAdapter_FieldNodeContainer(final JsonListenerToJdomAdapter lt, final JsonFieldBuilder pn) {
        this.b = lt;
        this.a = pn;
    }
    
    public void addNode(final JsonNodeBuilder jsonNodeBuilder) {
        this.a.withValue(jsonNodeBuilder);
    }
    
    public void addField(final JsonFieldBuilder jsonFieldBuilder) {
        throw new RuntimeException("Coding failure in Argo:  Attempt to add a field to a field.");
    }
}
