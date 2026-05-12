// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.Iterator;
import java.util.HashMap;

class JsonObjectNodeBuilder_Map extends HashMap
{
    final /* synthetic */ JsonObjectNodeBuilder builder;
    
    JsonObjectNodeBuilder_Map(final JsonObjectNodeBuilder builder) {
        this.builder = builder;
        for (final JsonFieldBuilder jsonFieldBuilder : this.builder.fieldBuilders) {
            this.put(jsonFieldBuilder.buildKey(), jsonFieldBuilder.buildValue());
        }
    }
}
