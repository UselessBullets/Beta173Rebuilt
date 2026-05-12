// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class JsonArrayNodeBuilder implements JsonNodeBuilder
{
    private final List elementBuilders;
    
    JsonArrayNodeBuilder() {
        this.elementBuilders = new LinkedList();
    }
    
    public JsonArrayNodeBuilder withElement(final JsonNodeBuilder elementBuilder) {
        this.elementBuilders.add(elementBuilder);
        return this;
    }
    
    public JsonRootNode build() {
        final LinkedList elements = new LinkedList();
        final Iterator iterator = this.elementBuilders.iterator();
        while (iterator.hasNext()) {
            elements.add(((JsonNodeBuilder)iterator.next()).build());
        }
        return JsonNodeFactories.aJsonArray(elements);
    }
}
