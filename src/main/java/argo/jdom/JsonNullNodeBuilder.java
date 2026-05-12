// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

final class JsonNullNodeBuilder implements JsonNodeBuilder
{
    public JsonNode build() {
        return JsonNodeFactories.aJsonNull();
    }
}
