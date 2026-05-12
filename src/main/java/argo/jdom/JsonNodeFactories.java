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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Factories for <code>JsonNode</code>s.
 */
public final class JsonNodeFactories {

    private JsonNodeFactories() {}

    public static JsonNode aJsonNull() {
        return JsonConstants.NULL;
    }

    public static JsonNode aJsonTrue() {
        return JsonConstants.TRUE;
    }

    public static JsonNode aJsonFalse() {
        return JsonConstants.FALSE;
    }

    public static JsonStringNode aJsonString(final String value) {
        return new JsonStringNode(value);
    }

    public static JsonNode aJsonNumber(final String value) {
        return new JsonNumberNode(value);
    }

    public static JsonNode aJsonNumber(final BigDecimal value) {
        return new JsonNumberNode(value.toString());
    }

    public static JsonNode aJsonNumber(final BigInteger value) {
        return new JsonNumberNode(value.toString());
    }

    public static JsonRootNode aJsonArray(final Iterable<JsonNode> elements) {
        return new JsonArray(elements);
    }

    public static JsonRootNode aJsonArray(final JsonNode... nodes) {
        return aJsonArray(Arrays.asList(nodes));
    }

    public static JsonRootNode aJsonObject(final Map<JsonStringNode, JsonNode> fields) {
        return new JsonObject(fields);
    }

    public static JsonRootNode aJsonObject(final JsonField... jsonField) {
        return aJsonObject(Arrays.asList(jsonField));
    }

    public static JsonRootNode aJsonObject(final Iterable<JsonField> jsonField) {
        return aJsonObject(new HashMap<JsonStringNode, JsonNode>() {{
            for (final JsonField field : jsonField) {
                put(field.getName(), field.getValue());
            }
        }});
    }

    public static JsonField aJsonField(final String name, final JsonNode value) {
        return new JsonField(aJsonString(name), value);
    }

    public static JsonNode aJsonBoolean(final boolean value) {
        return value ? aJsonTrue() : aJsonFalse();
    }
}
