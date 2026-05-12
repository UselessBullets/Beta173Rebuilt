// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import argo.format.CompactJsonFormatter;
import argo.format.JsonFormatter;

public final class JsonNodeDoesNotMatchPathElementsException extends JsonNodeDoesNotMatchJsonNodeSelectorException
{
    private static final JsonFormatter JSON_FORMATTER;
    
    static JsonNodeDoesNotMatchPathElementsException jsonNodeDoesNotMatchPathElementsException(final JsonNodeDoesNotMatchChainedJsonNodeSelectorException mz, final Object[] arr, final JsonRootNode qe) {
        return new JsonNodeDoesNotMatchPathElementsException(mz, arr, qe);
    }
    
    private JsonNodeDoesNotMatchPathElementsException(final JsonNodeDoesNotMatchChainedJsonNodeSelectorException mz, final Object[] arr, final JsonRootNode qe) {
        super(b(mz, arr, qe));
    }
    
    private static String b(final JsonNodeDoesNotMatchChainedJsonNodeSelectorException mz, final Object[] arr, final JsonRootNode qe) {
        return "Failed to find " + mz.failedNode.toString() + " at [" + JsonNodeDoesNotMatchChainedJsonNodeSelectorException.getShortFormFailPath(mz.failPath) + "] while resolving [" + a(arr) + "] in " + JsonNodeDoesNotMatchPathElementsException.JSON_FORMATTER.format(qe) + ".";
    }
    
    private static String a(final Object[] arr) {
        final StringBuilder sb = new StringBuilder();
        int n = 1;
        for (final Object o : arr) {
            if (n == 0) {
                sb.append(".");
            }
            n = 0;
            if (o instanceof String) {
                sb.append("\"").append(o).append("\"");
            }
            else {
                sb.append(o);
            }
        }
        return sb.toString();
    }
    
    static {
        JSON_FORMATTER = new CompactJsonFormatter();
    }
}
