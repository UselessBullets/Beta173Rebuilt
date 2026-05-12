// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.Map;
import java.util.LinkedList;
import java.util.List;

public final class JsonObjectNodeBuilder implements JsonNodeBuilder
{
    private final List fieldBuilders;
    
    JsonObjectNodeBuilder() {
        this.fieldBuilders = new LinkedList();
    }
    
    public JsonObjectNodeBuilder withFieldBuilder(final JsonFieldBuilder jsonFieldBuilder) {
        this.fieldBuilders.add(jsonFieldBuilder);
        return this;
    }
    
    public JsonRootNode build() {
        return JsonNodeFactories.aJsonObject(new JsonObjectNodeBuilder_Map(this));
    }
}
