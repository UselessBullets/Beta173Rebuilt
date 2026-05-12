// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.List;
import java.util.Map;

final class JsonConstants extends JsonNode
{
    static final JsonConstants NULL;
    static final JsonConstants TRUE;
    static final JsonConstants FALSE;
    private final JsonNodeType jsonNodeType;
    
    private JsonConstants(final JsonNodeType jsonNodeType) {
        this.jsonNodeType = jsonNodeType;
    }
    
    @Override
    public JsonNodeType getType() {
        return this.jsonNodeType;
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
    public List getElements() {
        throw new IllegalStateException("Attempt to get elements on a JsonNode without elements.");
    }
    
    static {
        NULL = new JsonConstants(JsonNodeType.NULL);
        TRUE = new JsonConstants(JsonNodeType.TRUE);
        FALSE = new JsonConstants(JsonNodeType.FALSE);
    }
}
