// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.Map;
import java.util.Arrays;

public final class JsonNodeFactories
{
    private JsonNodeFactories() {
    }
    
    public static JsonNode aJsonNull() {
        return JsonConstants.NULL;
    }
    
    public static JsonNode aJsonTrue() {
        return JsonConstants.TRUE;
    }
    
    public static JsonNode aJsonFalse() {
        return JsonConstants.FALSE;
    }
    
    public static JsonStringNode aJsonString(final String value) {
        return new JsonStringNode(value);
    }
    
    public static JsonNode aJsonNumber(final String value) {
        return new JsonNumber(value);
    }
    
    public static JsonRootNode aJsonArray(final Iterable elements) {
        return new JsonArray(elements);
    }
    
    public static JsonRootNode aJsonArray(final JsonNode... nodes) {
        return aJsonArray(Arrays.asList(nodes));
    }
    
    public static JsonRootNode aJsonObject(final Map fields) {
        return new JsonObject(fields);
    }
}
