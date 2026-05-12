// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.List;
import java.util.Map;

public final class JsonStringNode extends JsonNode implements Comparable
{
    private final String value;
    
    JsonStringNode(final String value) {
        if (value == null) {
            throw new NullPointerException("Attempt to construct a JsonString with a null value.");
        }
        this.value = value;
    }
    
    @Override
    public JsonNodeType getType() {
        return JsonNodeType.STRING;
    }
    
    @Override
    public String getText() {
        return this.value;
    }
    
    @Override
    public Map getFields() {
        throw new IllegalStateException("Attempt to get fields on a JsonNode without fields.");
    }
    
    @Override
    public List getElements() {
        throw new IllegalStateException("Attempt to get elements on a JsonNode without elements.");
    }
    
    @Override
    public boolean equals(final Object that) {
        return this == that || (that != null && this.getClass() == that.getClass() && this.value.equals(((JsonStringNode)that).value));
    }
    
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
    
    @Override
    public String toString() {
        return "JsonStringNode value:[" + this.value + "]";
    }
    
    public int compareTo(final JsonStringNode that) {
        return this.value.compareTo(that.value);
    }
}
