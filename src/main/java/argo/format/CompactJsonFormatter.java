// 
// Decompiled by Procyon v0.6.0
// 

package argo.format;

import java.util.Iterator;
import argo.jdom.JsonStringNode;
import java.util.Collection;
import java.util.TreeSet;
import argo.jdom.JsonNode;
import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import argo.jdom.JsonRootNode;

public final class CompactJsonFormatter implements JsonFormatter
{
    public String format(final JsonRootNode jsonRootNode) {
        final StringWriter write = new StringWriter();
        try {
            this.format(jsonRootNode, write);
        }
        catch (final IOException cause) {
            throw new RuntimeException("Coding failure in Argo:  StringWriter gave an IOException", cause);
        }
        return write.toString();
    }
    
    public void format(final JsonRootNode jsonNode, final Writer write) {
        this.formatJsonNode(jsonNode, write);
    }
    
    private void formatJsonNode(final JsonNode jsonNode, final Writer writer) {
        int n = 1;
        switch (JsonNodeType_Obfuscation.arr[jsonNode.getType().ordinal()]) {
            case 1: {
                writer.append('[');
                for (final JsonNode jsonNode2 : jsonNode.getElements()) {
                    if (n == 0) {
                        writer.append(',');
                    }
                    n = 0;
                    this.formatJsonNode(jsonNode2, writer);
                }
                writer.append(']');
                break;
            }
            case 2: {
                writer.append('{');
                for (final JsonStringNode jsonNode3 : new TreeSet(jsonNode.getFields().keySet())) {
                    if (n == 0) {
                        writer.append(',');
                    }
                    n = 0;
                    this.formatJsonNode(jsonNode3, writer);
                    writer.append(':');
                    this.formatJsonNode((JsonNode)jsonNode.getFields().get(jsonNode3), writer);
                }
                writer.append('}');
                break;
            }
            case 3: {
                writer.append('\"').append((CharSequence)new JsonEscapedString(jsonNode.getText()).toString()).append('\"');
                break;
            }
            case 4: {
                writer.append((CharSequence)jsonNode.getText());
                break;
            }
            case 5: {
                writer.append((CharSequence)"false");
                break;
            }
            case 6: {
                writer.append((CharSequence)"true");
                break;
            }
            case 7: {
                writer.append((CharSequence)"null");
                break;
            }
            default: {
                throw new RuntimeException("Coding failure in Argo:  Attempt to format a JsonNode of unknown type [" + jsonNode.getType() + "];");
            }
        }
    }
}
