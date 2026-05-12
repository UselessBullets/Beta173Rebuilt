// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

final class JsonNumber extends JsonNode
{
    private static final Pattern a;
    private final String b;
    
    JsonNumber(final String string) {
        if (string == null) {
            throw new NullPointerException("Attempt to construct a JsonNumber with a null value.");
        }
        if (!JsonNumber.a.matcher(string).matches()) {
            throw new IllegalArgumentException("Attempt to construct a JsonNumber with a String [" + string + "] that does not match the JSON number specification.");
        }
        this.b = string;
    }
    
    @Override
    public JsonNodeType getType() {
        return JsonNodeType.NUMBER;
    }
    
    @Override
    public String getText() {
        return this.b;
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
    public boolean equals(final Object object) {
        return this == object || (object != null && this.getClass() == object.getClass() && this.b.equals(((JsonNumber)object).b));
    }
    
    @Override
    public int hashCode() {
        return this.b.hashCode();
    }
    
    @Override
    public String toString() {
        return "JsonNumberNode value:[" + this.b + "]";
    }
    
    static {
        a = Pattern.compile("(-?)(0|([1-9]([0-9]*)))(\\.[0-9]+)?((e|E)(\\+|-)?[0-9]+)?");
    }
}
