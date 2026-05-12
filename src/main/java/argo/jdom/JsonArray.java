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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class JsonArray extends JsonRootNode {

    private final List<JsonNode> elements;

    JsonArray(final Iterable<JsonNode> elements) {
        this.elements = asList(elements);
    }

    public JsonNodeType getType() {
        return JsonNodeType.ARRAY;
    }

    public List<JsonNode> getElements() {
        return new ArrayList<JsonNode>(elements);
    }

    public boolean hasText() {
        return false;
    }

    public String getText() {
        throw new IllegalStateException("Attempt to get text on a JsonNode without text.");
    }

    public boolean hasFields() {
        return false;
    }

    public Map<JsonStringNode, JsonNode> getFields() {
        throw new IllegalStateException("Attempt to get fields on a JsonNode without fields.");
    }

    public boolean hasElements() {
        return true;
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        final JsonArray thatJsonArray = (JsonArray) that;
        return this.elements.equals(thatJsonArray.elements);
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("JsonArray elements:[")
                .append(elements)
                .append("]")
                .toString();
    }

    private static List<JsonNode> asList(final Iterable<JsonNode> elements) {
        return new ArrayList<JsonNode>(){{
            for (JsonNode element : elements) {
                this.add(element);
            }
        }};
    }
}
