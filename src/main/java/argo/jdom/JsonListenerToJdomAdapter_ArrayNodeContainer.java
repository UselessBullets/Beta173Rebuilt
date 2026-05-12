// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

class JsonListenerToJdomAdapter_ArrayNodeContainer implements JsonListenerToJdomAdapter_NodeContainer
{
    final /* synthetic */ JsonArrayNodeBuilder builder;
    final /* synthetic */ JsonListenerToJdomAdapter adapter;
    
    JsonListenerToJdomAdapter_ArrayNodeContainer(final JsonListenerToJdomAdapter adapter, final JsonArrayNodeBuilder builder) {
        this.adapter = adapter;
        this.builder = builder;
    }
    
    public void addNode(final JsonNodeBuilder jsonNodeBuilder) {
        this.builder.withElement(jsonNodeBuilder);
    }
    
    public void addField(final JsonFieldBuilder jsonFieldBuilder) {
        throw new RuntimeException("Coding failure in Argo:  Attempt to add a field to an array.");
    }
}
