/*
 * Copyright 2010 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class JsonObject extends JsonRootNode {

    private final Map<JsonStringNode, JsonNode> fields;

    JsonObject(final Map<JsonStringNode, JsonNode> fields) {
        this.fields = new HashMap<JsonStringNode, JsonNode>(fields);
    }

    public Map<JsonStringNode, JsonNode> getFields() {
        return new HashMap<JsonStringNode, JsonNode>(fields);
    }

    public JsonNodeType getType() {
        return JsonNodeType.OBJECT;
    }

    public boolean hasText() {
        return false;
    }

    public String getText() {
        throw new IllegalStateException("Attempt to get text on a JsonNode without text.");
    }

    public boolean hasFields() {
        return true;
    }

    public boolean hasElements() {
        return false;
    }

    public List<JsonNode> getElements() {
        throw new IllegalStateException("Attempt to get elements on a JsonNode without elements.");
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        final JsonObject thatJsonObject = (JsonObject) that;
        return this.fields.equals(thatJsonObject.fields);
    }

    @Override
    public int hashCode() {
        return fields.hashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("JsonObject fields:[")
                .append(fields)
                .append("]")
                .toString();
    }
}
