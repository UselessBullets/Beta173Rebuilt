// 
// Decompiled by Procyon v0.6.0
// 

package util;

class LongHashMap_Entry
{
    final long key;
    Object value;
    LongHashMap_Entry next;
    final int hash;
    
    LongHashMap_Entry(final int hash, final long key, final Object value, final LongHashMap_Entry next) {
        this.value = value;
        this.next = next;
        this.key = key;
        this.hash = hash;
    }
    
    public final long getKey() {
        return this.key;
    }
    
    public final Object getValue() {
        return this.value;
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (!(o instanceof LongHashMap_Entry)) {
            return false;
        }
        final LongHashMap_Entry longHashMap_Entry = (LongHashMap_Entry)o;
        final Long value = this.getKey();
        final Long value2 = longHashMap_Entry.getKey();
        if (value == value2 || (value != null && value.equals(value2))) {
            final Object value3 = this.getValue();
            final Object value4 = longHashMap_Entry.getValue();
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
