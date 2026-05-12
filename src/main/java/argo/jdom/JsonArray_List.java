// 
// Decompiled by Procyon v0.6.0
// 

package argo.jdom;

import java.util.Iterator;
import java.util.ArrayList;

final class JsonArray_List extends ArrayList
{
    final /* synthetic */ Iterable it;
    
    JsonArray_List(final Iterable it) {
        this.it = it;
        final Iterator iterator = this.it.iterator();
        while (iterator.hasNext()) {
            this.add((JsonNode)iterator.next());
        }
    }
}
