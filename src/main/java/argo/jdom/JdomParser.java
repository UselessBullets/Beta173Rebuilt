// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.io.IOException;
import java.io.StringReader;
import argo.saj.SajParser;
import java.io.Reader;

public final class JdomParser
{
    public JsonRootNode parse(final Reader reader) {
        final JsonListenerToJdomAdapter jsonListener = new JsonListenerToJdomAdapter();
        new SajParser().parse(reader, jsonListener);
        return jsonListener.getDocument();
    }
    
    public JsonRootNode parse(final String json) {
        JsonRootNode parse;
        try {
            parse = this.parse(new StringReader(json));
        }
        catch (final IOException cause) {
            throw new RuntimeException("Coding failure in Argo:  StringWriter gave an IOException", cause);
        }
        return parse;
    }
}
