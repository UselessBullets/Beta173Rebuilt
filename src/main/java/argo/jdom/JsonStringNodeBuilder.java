// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

public final class JsonStringNodeBuilder implements JsonNodeBuilder
{
    private final String a;
    
    JsonStringNodeBuilder(final String string) {
        this.a = string;
    }
    
    public JsonStringNode build() {
        return JsonNodeFactories.aJsonString(this.a);
    }
}
