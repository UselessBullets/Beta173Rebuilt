// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

class JsonListenerToJdomAdapter_ObjectNodeContainer implements JsonListenerToJdomAdapter_NodeContainer
{
    final /* synthetic */ JsonObjectNodeBuilder a;
    final /* synthetic */ JsonListenerToJdomAdapter b;
    
    JsonListenerToJdomAdapter_ObjectNodeContainer(final JsonListenerToJdomAdapter lt, final JsonObjectNodeBuilder sx) {
        this.b = lt;
        this.a = sx;
    }
    
    public void addNode(final JsonNodeBuilder jsonNodeBuilder) {
        throw new RuntimeException("Coding failure in Argo:  Attempt to add a node to an object.");
    }
    
    public void addField(final JsonFieldBuilder jsonFieldBuilder) {
        this.a.withFieldBuilder(jsonFieldBuilder);
    }
}
