// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.Arrays;

public final class JsonNodeSelectors
{
    private JsonNodeSelectors() {
    }
    
    public static JsonNodeSelector aStringNode(final Object... pathElements) {
        return chainOn(pathElements, new JsonNodeSelector(new JsonNodeSelectors_StringFunctor()));
    }
    
    public static JsonNodeSelector anArrayNode(final Object... pathElements) {
        return chainOn(pathElements, new JsonNodeSelector(new JsonNodeSelectors_ArrayFunctor()));
    }
    
    public static JsonNodeSelector anObjectNode(final Object... pathElements) {
        return chainOn(pathElements, new JsonNodeSelector(new JsonNodeSelectors_ObjectFunctor()));
    }
    
    public static JsonNodeSelector aField(final String fieldName) {
        return aField(JsonNodeFactories.aJsonString(fieldName));
    }
    
    public static JsonNodeSelector aField(final JsonStringNode fieldName) {
        return new JsonNodeSelector(new JsonNodeSelectors_FieldFunctor(fieldName));
    }
    
    public static JsonNodeSelector anObjectNodeWithField(final String fieldName) {
        return anObjectNode(new Object[0]).with(aField(fieldName));
    }
    
    public static JsonNodeSelector anElement(final int index) {
        return new JsonNodeSelector(new JsonNodeSelectors_ElementFunctor(index));
    }
    
    public static JsonNodeSelector anArrayNodeWithElement(final int index) {
        return anArrayNode(new Object[0]).with(anElement(index));
    }
    
    private static JsonNodeSelector chainOn(final Object[] pathElements, final JsonNodeSelector parentSelector) {
        JsonNodeSelector jsonNodeSelector = parentSelector;
        for (int i = pathElements.length - 1; i >= 0; --i) {
            if (pathElements[i] instanceof Integer) {
                jsonNodeSelector = chainedJsonNodeSelector(anArrayNodeWithElement((int)pathElements[i]), jsonNodeSelector);
            }
            else {
                if (!(pathElements[i] instanceof String)) {
                    throw new IllegalArgumentException("Element [" + pathElements[i] + "] of path elements" + " [" + Arrays.toString(pathElements) + "] was of illegal type [" + pathElements[i].getClass().getCanonicalName() + "]; only Integer and String are valid.");
                }
                jsonNodeSelector = chainedJsonNodeSelector(anObjectNodeWithField((String)pathElements[i]), jsonNodeSelector);
            }
        }
        return jsonNodeSelector;
    }
    
    private static JsonNodeSelector chainedJsonNodeSelector(final JsonNodeSelector parent, final JsonNodeSelector child) {
        return new JsonNodeSelector(new ChainedFunctor(parent, child));
    }
}
