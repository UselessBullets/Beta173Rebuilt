// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class JsonNodeDoesNotMatchChainedJsonNodeSelectorException extends JsonNodeDoesNotMatchJsonNodeSelectorException
{
    final Functor failedNode;
    final List failPath;
    
    static JsonNodeDoesNotMatchJsonNodeSelectorException createJsonNodeDoesNotMatchJsonNodeSelectorException(final Functor failedNode) {
        return new JsonNodeDoesNotMatchChainedJsonNodeSelectorException(failedNode, new LinkedList());
    }
    
    static JsonNodeDoesNotMatchJsonNodeSelectorException createChainedJsonNodeDoesNotMatchJsonNodeSelectorException(final JsonNodeDoesNotMatchChainedJsonNodeSelectorException e, final JsonNodeSelector parentJsonNodeSelector) {
        final LinkedList failPath = new LinkedList(e.failPath);
        failPath.add(parentJsonNodeSelector);
        return new JsonNodeDoesNotMatchChainedJsonNodeSelectorException(e.failedNode, failPath);
    }
    
    static JsonNodeDoesNotMatchJsonNodeSelectorException createUnchainedJsonNodeDoesNotMatchJsonNodeSelectorException(final JsonNodeDoesNotMatchChainedJsonNodeSelectorException e, final JsonNodeSelector parentJsonNodeSelector) {
        final LinkedList failPath = new LinkedList();
        failPath.add(parentJsonNodeSelector);
        return new JsonNodeDoesNotMatchChainedJsonNodeSelectorException(e.failedNode, failPath);
    }
    
    private JsonNodeDoesNotMatchChainedJsonNodeSelectorException(final Functor failedNode, final List failPath) {
        super("Failed to match any JSON node at [" + getShortFormFailPath(failPath) + "]");
        this.failedNode = failedNode;
        this.failPath = failPath;
    }
    
    static String getShortFormFailPath(final List failPath) {
        final StringBuilder sb = new StringBuilder();
        for (int i = failPath.size() - 1; i >= 0; --i) {
            sb.append(((JsonNodeSelector)failPath.get(i)).shortForm());
            if (i != 0) {
                sb.append(".");
            }
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "JsonNodeDoesNotMatchJsonNodeSelectorException{failedNode=" + this.failedNode + ", failPath=" + this.failPath + '}';
    }
}
