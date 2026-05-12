/*
 * Copyright 2010 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.TreeSet;

/**
 * JsonFormat that formats JSON in a human-readable form.  Instances of this class can safely be shared between threads.
 */
public final class PrettyJsonFormatter implements JsonFormatter {

    public String format(final JsonRootNode jsonNode) {
        final StringWriter stringWriter = new StringWriter();
        try {
            format(jsonNode, stringWriter);
        } catch (final IOException e) {
            throw new RuntimeException("Coding failure in Argo:  StringWriter gave an IOException", e);
        }
        return stringWriter.toString();
    }

    public void format(final JsonRootNode jsonNode, final Writer writer) throws IOException {
        formatJsonNode(jsonNode, new PrintWriter(writer), 0);
    }

    private void formatJsonNode(final JsonNode jsonNode, final PrintWriter writer, final int indent) throws IOException {
        boolean first = true;
        switch (jsonNode.getType()) {
            case ARRAY:
                writer.append('[');
                for (final JsonNode node : jsonNode.getElements()) {
                    writer.println();
                    addTabs(writer, indent + 1);
                    if (!first) {
                        writer.append(", ");
                    }
                    first = false;
                    formatJsonNode(node, writer, indent + 1);
                }
                if (!jsonNode.getElements().isEmpty()) {
                    writer.println();
                    addTabs(writer, indent);
                }
                writer.append(']');
                break;
            case OBJECT:
                writer.append('{');
                for (final JsonStringNode field : new TreeSet<JsonStringNode>(jsonNode.getFields().keySet())) {
                    writer.println();
                    addTabs(writer, indent + 1);
                    if (!first) {
                        writer.append(", ");
                    }
                    first = false;
                    formatJsonNode(field, writer, indent + 1);
                    writer.append(": ");
                    formatJsonNode(jsonNode.getFields().get(field), writer, indent + 1);
                }
                if (!jsonNode.getFields().isEmpty()) {
                    writer.println();
                    addTabs(writer, indent);
                }
                writer.append('}');
                break;
            case STRING:
                writer.append('"')
                        .append(new JsonEscapedString(jsonNode.getText()).toString())
                        .append('"');
                break;
            case NUMBER:
                writer.append(jsonNode.getText());
                break;
            case FALSE:
                writer.append("false");
                break;
            case TRUE:
                writer.append("true");
                break;
            case NULL:
                writer.append("null");
                break;
            default:
                throw new RuntimeException("Coding failure in Argo:  Attempt to format a JsonNode of unknown type [" + jsonNode.getType() + "];");
        }
    }

    private void addTabs(final PrintWriter writer, final int tabs) {
        for (int i = 0; i < tabs; i++) {
            writer.write('\t');
        }
    }

}
