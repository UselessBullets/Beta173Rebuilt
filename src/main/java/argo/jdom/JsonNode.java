// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.List;
import java.util.Map;

public abstract class JsonNode
{
    JsonNode() {
    }
    
    public abstract JsonNodeType getType();
    
    public abstract String getText();
    
    public abstract Map getFields();
    
    public abstract List getElements();
    
    public final String getStringValue(final Object... pathElements) {
        return (String)this.wrapExceptionsFor(JsonNodeSelectors.aStringNode(pathElements), this, pathElements);
    }
    
    public final List getArrayNode(final Object... pathElements) {
        return (List)this.wrapExceptionsFor(JsonNodeSelectors.anArrayNode(pathElements), this, pathElements);
    }
    
    private Object wrapExceptionsFor(final JsonNodeSelector value, final JsonNode node, final Object[] pathElements) {
        try {
            return value.getValue(node);
        }
        catch (final JsonNodeDoesNotMatchChainedJsonNodeSelectorException mz) {
            throw JsonNodeDoesNotMatchPathElementsException.jsonNodeDoesNotMatchPathElementsException(mz, pathElements, JsonNodeFactories.aJsonArray(node));
        }
    }
}
