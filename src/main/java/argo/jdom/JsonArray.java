// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

final class JsonArray extends JsonRootNode
{
    private final List elements;
    
    JsonArray(final Iterable elements) {
        this.elements = asList(elements);
    }
    
    @Override
    public JsonNodeType getType() {
        return JsonNodeType.ARRAY;
    }
    
    @Override
    public List getElements() {
        return new ArrayList(this.elements);
    }
    
    @Override
    public String getText() {
        throw new IllegalStateException("Attempt to get text on a JsonNode without text.");
    }
    
    @Override
    public Map getFields() {
        throw new IllegalStateException("Attempt to get fields on a JsonNode without fields.");
    }
    
    @Override
    public boolean equals(final Object that) {
        return this == that || (that != null && this.getClass() == that.getClass() && this.elements.equals(((JsonArray)that).elements));
    }
    
    @Override
    public int hashCode() {
        return this.elements.hashCode();
    }
    
    @Override
    public String toString() {
        return "JsonArray elements:[" + this.elements + "]";
    }
    
    private static List asList(final Iterable elements) {
        return new JsonArray_List(elements);
    }
}
