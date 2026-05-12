// 
// Decompiled by Procyon v0.6.0
// 

package argo.format;

final class JsonEscapedString
{
    private final String escapedString;
    
    JsonEscapedString(final String unescapedString) {
        this.escapedString = unescapedString.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b").replace("\f", "\\f").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
    
    @Override
    public String toString() {
        return this.escapedString;
    }
}
