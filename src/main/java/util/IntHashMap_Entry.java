// 
// Decompiled by Procyon v0.6.0
// 

package util;

class IntHashMap_Entry
{
    final int key;
    Object value;
    IntHashMap_Entry next;
    final int hash;
    
    IntHashMap_Entry(final int hash, final int key, final Object value, final IntHashMap_Entry next) {
        this.value = value;
        this.next = next;
        this.key = key;
        this.hash = hash;
    }
    
    public final int getKey() {
        return this.key;
    }
    
    public final Object getValue() {
        return this.value;
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (!(o instanceof IntHashMap_Entry)) {
            return false;
        }
        final IntHashMap_Entry intHashMap_Entry = (IntHashMap_Entry)o;
        final Integer value = this.getKey();
        final Integer value2 = intHashMap_Entry.getKey();
        if (value == value2 || (value != null && value.equals(value2))) {
            final Object value3 = this.getValue();
            final Object value4 = intHashMap_Entry.getValue();
            if (value3 == value4 || (value3 != null && value3.equals(value4))) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public final int hashCode() {
        return hash(this.key);
    }
    
    @Override
    public final String toString() {
        return this.getKey() + "=" + this.getValue();
    }
}
