// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

final class JsonNumberNodeBuilder implements JsonNodeBuilder
{
    private final JsonNode a;
    
    JsonNumberNodeBuilder(final String value) {
        this.a = JsonNodeFactories.aJsonNumber(value);
    }
    
    public JsonNode build() {
        return this.a;
    }
}
