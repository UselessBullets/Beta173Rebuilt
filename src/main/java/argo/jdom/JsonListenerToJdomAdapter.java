// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.Stack;

final class JsonListenerToJdomAdapter implements JsonListener
{
    private final Stack stack;
    private JsonNodeBuilder root;
    
    JsonListenerToJdomAdapter() {
        this.stack = new Stack();
    }
    
    JsonRootNode getDocument() {
        return (JsonRootNode)this.root.build();
    }
    
    public void startDocument() {
    }
    
    public void endDocument() {
    }
    
    public void startArray() {
        final JsonArrayNodeBuilder anArrayBuilder = JsonNodeBuilders.anArrayBuilder();
        this.addRootNode(anArrayBuilder);
        this.stack.push(new JsonListenerToJdomAdapter_ArrayNodeContainer(this, anArrayBuilder));
    }
    
    public void endArray() {
        this.stack.pop();
    }
    
    public void startObject() {
        final JsonObjectNodeBuilder anObjectBuilder = JsonNodeBuilders.anObjectBuilder();
        this.addRootNode(anObjectBuilder);
        this.stack.push(new JsonListenerToJdomAdapter_ObjectNodeContainer(this, anObjectBuilder));
    }
    
    public void endObject() {
        this.stack.pop();
    }
    
    public void startField(final String name) {
        final JsonFieldBuilder withKey = JsonFieldBuilder.aJsonFieldBuilder().withKey(JsonNodeBuilders.aStringBuilder(name));
        this.stack.peek().addField(withKey);
        this.stack.push(new JsonListenerToJdomAdapter_FieldNodeContainer(this, withKey));
    }
    
    public void endField() {
        this.stack.pop();
    }
    
    public void numberValue(final String value) {
        this.addValue(JsonNodeBuilders.aNumberBuilder(value));
    }
    
    public void trueValue() {
        this.addValue(JsonNodeBuilders.aTrueBuilder());
    }
    
    public void stringValue(final String value) {
        this.addValue(JsonNodeBuilders.aStringBuilder(value));
    }
    
    public void falseValue() {
        this.addValue(JsonNodeBuilders.aFalseBuilder());
    }
    
    public void nullValue() {
        this.addValue(JsonNodeBuilders.aNullBuilder());
    }
    
    private void addRootNode(final JsonNodeBuilder rootNodeBuilder) {
        if (this.root == null) {
            this.root = rootNodeBuilder;
        }
        else {
            this.addValue(rootNodeBuilder);
        }
    }
    
    private void addValue(final JsonNodeBuilder nodeBuilder) {
        this.stack.peek().addNode(nodeBuilder);
    }
}
