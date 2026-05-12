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

import argo.saj.InvalidSyntaxException;
import argo.saj.SajParser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Parses a JSON character stream into a <code>JsonRootNode</code> object.  Instances of this class can safely be shared
 * between threads.
 */
public final class JdomParser {

    /**
     * Parse the character stream from the specified <code>Reader</code> into a <code>JsonRootNode</code> object.
     *
     * @param reader the <code>Reader</code> to parse.
     * @return a <code>JsonRootNode</code> representing the JSON read from the specified <code>Reader</code>.
     * @throws IOException            bubbled up from exceptions thrown reading from the specified <code>Reader</code>.
     * @throws InvalidSyntaxException if the characters streamed from the specified <code>Reader</code> does not represent valid JSON.
     */
    public JsonRootNode parse(final Reader reader) throws IOException, InvalidSyntaxException {
        final JsonListenerToJdomAdapter jsonListenerToJdomAdapter = new JsonListenerToJdomAdapter();
        new SajParser().parse(reader, jsonListenerToJdomAdapter);
        return jsonListenerToJdomAdapter.getDocument();
    }

    /**
     * Parse the specified JSON <code>String</code> into a <code>JsonRootNode</code> object.
     *
     * @param json the <code>String</code> to parse.
     * @return a <code>JsonRootNode</code> representing the JSON read from the specified <code>String</code>.
     * @throws InvalidSyntaxException if the characters streamed from the specified <code>String</code> does not represent valid JSON.
     */
    public JsonRootNode parse(final String json) throws InvalidSyntaxException {
        final JsonRootNode result;
        try {
            result = parse(new StringReader(json));
        } catch (final IOException e) {
            throw new RuntimeException("Coding failure in Argo:  StringWriter gave an IOException", e);
        }
        return result;
    }
}
