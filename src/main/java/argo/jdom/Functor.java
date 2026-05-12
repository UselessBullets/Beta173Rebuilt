// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

interface Functor
{
    boolean matchesNode(final Object jsonNode);
    
    Object applyTo(final Object jsonNode);
    
    String shortForm();
}
