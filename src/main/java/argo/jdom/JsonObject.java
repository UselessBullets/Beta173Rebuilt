// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

final class JsonObject extends JsonRootNode
{
    private final Map fields;
    
    JsonObject(final Map fields) {
        this.fields = new HashMap(fields);
    }
    
    @Override
    public Map getFields() {
        return new HashMap(this.fields);
    }
    
    @Override
    public JsonNodeType getType() {
        return JsonNodeType.OBJECT;
    }
    
    @Override
    public String getText() {
        throw new IllegalStateException("Attempt to get text on a JsonNode without text.");
    }
    
    @Override
    public List getElements() {
        throw new IllegalStateException("Attempt to get elements on a JsonNode without elements.");
    }
    
    @Override
    public boolean equals(final Object that) {
        return this == that || (that != null && this.getClass() == that.getClass() && this.fields.equals(((JsonObject)that).fields));
    }
    
    @Override
    public int hashCode() {
        return this.fields.hashCode();
    }
    
    @Override
    public String toString() {
        return "JsonObject fields:[" + this.fields + "]";
    }
}
