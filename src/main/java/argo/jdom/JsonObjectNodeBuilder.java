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
import java.util.LinkedList;
import java.util.List;

import static argo.jdom.JsonFieldBuilder.aJsonFieldBuilder;

/**
 * Builder for <code>JsonRootNode</code>s representing JSON objects.
 */
public final class JsonObjectNodeBuilder implements JsonNodeBuilder<JsonRootNode> {

    private final List<JsonFieldBuilder> fieldBuilders = new LinkedList<JsonFieldBuilder>();

    JsonObjectNodeBuilder() {}

    /**
     * Adds a field to the object that will be built.
     *
     * @param name the name of the field
     * @param value a builder for the value of the field.
     * @return the modified object builder.
     */
    public JsonObjectNodeBuilder withField(final String name, final JsonNodeBuilder value) {
        return withField(JsonNodeBuilders.aStringBuilder(name), value);
    }

    /**
     * Adds a field to the object that will be built.
     *
     * @param name a builder for the name of the field
     * @param value a builder for the value of the field.
     * @return the modified object builder.
     */
    public JsonObjectNodeBuilder withField(final JsonNodeBuilder<JsonStringNode> name, final JsonNodeBuilder value) {
        return withFieldBuilder(aJsonFieldBuilder().withKey(name).withValue(value));
    }

    /**
     * Adds a field to the object that will be built.
     *
     * @param jsonFieldBuilder a builder for the field.
     * @return the modified object builder.
     */
    public JsonObjectNodeBuilder withFieldBuilder(final JsonFieldBuilder jsonFieldBuilder) {
        fieldBuilders.add(jsonFieldBuilder);
        return this;
    }

    public JsonRootNode build() {
        return JsonNodeFactories.aJsonObject(new HashMap<JsonStringNode, JsonNode>() {{
            for (JsonFieldBuilder fieldBuilder : fieldBuilders) {
                put(fieldBuilder.buildKey(), fieldBuilder.buildValue());
            }
        }});
    }
}
