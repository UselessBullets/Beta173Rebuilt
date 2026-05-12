// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

final class JsonFieldBuilder
{
    private JsonNodeBuilder key;
    private JsonNodeBuilder valueBuilder;
    
    private JsonFieldBuilder() {
    }
    
    static JsonFieldBuilder aJsonFieldBuilder() {
        return new JsonFieldBuilder();
    }
    
    JsonFieldBuilder withKey(final JsonNodeBuilder jsonStringNode) {
        this.key = jsonStringNode;
        return this;
    }
    
    JsonFieldBuilder withValue(final JsonNodeBuilder jsonNodeBuilder) {
        this.valueBuilder = jsonNodeBuilder;
        return this;
    }
    
    JsonStringNode buildKey() {
        return (JsonStringNode)this.key.build();
    }
    
    JsonNode buildValue() {
        return this.valueBuilder.build();
    }
}
