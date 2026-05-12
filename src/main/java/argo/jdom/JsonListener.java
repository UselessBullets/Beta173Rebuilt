// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

public interface JsonListener
{
    void startDocument();
    
    void endDocument();
    
    void startArray();
    
    void endArray();
    
    void startObject();
    
    void endObject();
    
    void startField(final String name);
    
    void endField();
    
    void stringValue(final String value);
    
    void numberValue(final String value);
    
    void trueValue();
    
    void falseValue();
    
    void nullValue();
}
