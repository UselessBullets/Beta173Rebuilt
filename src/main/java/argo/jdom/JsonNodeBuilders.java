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

/**
 * Builders for <code>JsonNode</code>s.
 */
public final class JsonNodeBuilders {

    private JsonNodeBuilders() {}

    public static JsonNodeBuilder<JsonNode> aNullBuilder() {
        return new JsonNodeBuilder<JsonNode>() {
            public JsonNode build() {
                return JsonNodeFactories.aJsonNull();
            }
        };
    }

    public static JsonNodeBuilder<JsonNode> aTrueBuilder() {
        return new JsonNodeBuilder<JsonNode>() {
            public JsonNode build() {
                return JsonNodeFactories.aJsonTrue();
            }
        };
    }

    public static JsonNodeBuilder<JsonNode> aFalseBuilder() {
        return new JsonNodeBuilder<JsonNode>() {
            public JsonNode build() {
                return JsonNodeFactories.aJsonFalse();
            }
        };
    }

    /**
     * Builder for a JSON number.  This method will throw an illegal argument exception if the <code>String</code> specified does not conform to the JSON number specification.
     * @param value a String representing a JSON number.
     * @throws IllegalArgumentException if the given <code>String</code> does not conform to the JSON number specification.
     * @return a builder for a <code>JsonNode</code> representing the number given.
     */
    public static JsonNodeBuilder<JsonNode> aNumberBuilder(final String value) {
        return new JsonNumberNodeBuilder(value);
    }

    /**
     * Builder for a JSON string.
     * @param value a String to convert into a JSON string.
     * @return a builder for a <code>JsonNode</code> representing the string given.
     */
    public static JsonStringNodeBuilder aStringBuilder(final String value) {
        return new JsonStringNodeBuilder(value);
    }

    public static JsonObjectNodeBuilder anObjectBuilder() {
        return new JsonObjectNodeBuilder();
    }

    public static JsonArrayNodeBuilder anArrayBuilder() {
        return new JsonArrayNodeBuilder();
    }
}
