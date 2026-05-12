// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

public final class JsonNodeBuilders
{
    private JsonNodeBuilders() {
    }
    
    public static JsonNodeBuilder aNullBuilder() {
        return new JsonNullNodeBuilder();
    }
    
    public static JsonNodeBuilder aTrueBuilder() {
        return new JsonTrueNodeBuilder();
    }
    
    public static JsonNodeBuilder aFalseBuilder() {
        return new JsonFalseNodeBuilder();
    }
    
    public static JsonNodeBuilder aNumberBuilder(final String value) {
        return new JsonNumberNodeBuilder(value);
    }
    
    public static JsonStringNodeBuilder aStringBuilder(final String value) {
        return new JsonStringNodeBuilder(value);
    }
    
    public static JsonObjectNodeBuilder anObjectBuilder() {
        return new JsonObjectNodeBuilder();
    }
    
    public static JsonArrayNodeBuilder anArrayBuilder() {
        return new JsonArrayNodeBuilder();
    }
}
