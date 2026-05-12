// 
// Decompiled by Procyon v0.6.0
// 

package util;

public class LongHashMap
{
    private transient LongHashMap_Entry[] table;
    private transient int size;
    private int threshold;
    private final float loadFactor;
    private transient volatile int modCount;
    
    public LongHashMap() {
        this.loadFactor = 0.75f;
        this.threshold = 12;
        this.table = new LongHashMap_Entry[16];
    }
    
    private static int hash(final long i) {
        return hash((int)(i ^ i >>> 32));
    }
    
    private static int hash(int i) {
        i ^= (i >>> 20 ^ i >>> 12);
        return i ^ i >>> 7 ^ i >>> 4;
    }
    
    private static int indexFor(final int hash, final int length) {
        return hash & length - 1;
    }
    
    public Object get(final long key) {
        for (LongHashMap_Entry next = this.table[indexFor(hash(key), this.table.length)]; next != null; next = next.next) {
            if (next.key == key) {
                return next.value;
            }
        }
        return null;
    }
    
    public void put(final long key, final Object value) {
        final int hash = hash(key);
        final int index = indexFor(hash, this.table.length);
        for (LongHashMap_Entry next = this.table[index]; next != null; next = next.next) {
            if (next.key == key) {
                next.value = value;
            }
        }
        ++this.modCount;
        this.addEntry(hash, key, value, index);
    }
    
    private void resize(final int newSize) {
        if (this.table.length == 1073741824) {
            this.threshold = Integer.MAX_VALUE;
            return;
        }
        final LongHashMap_Entry[] array = new LongHashMap_Entry[newSize];
        this.transfer(array);
        this.table = array;
        this.threshold = (int)(newSize * this.loadFactor);
    }
    
    private void transfer(final LongHashMap_Entry[] newTable) {
        final LongHashMap_Entry[] table = this.table;
        final int length = newTable.length;
        for (int i = 0; i < table.length; ++i) {
            LongHashMap_Entry longHashMap_Entry = table[i];
            if (longHashMap_Entry != null) {
                table[i] = null;
                do {
                    final LongHashMap_Entry next = longHashMap_Entry.next;
                    final int index = indexFor(longHashMap_Entry.hash, length);
                    longHashMap_Entry.next = newTable[index];
                    newTable[index] = longHashMap_Entry;
                    longHashMap_Entry = next;
                } while (longHashMap_Entry != null);
            }
        }
    }
    
    public Object remove(final long key) {
        final LongHashMap_Entry removeEntryForKey = this.removeEntryForKey(key);
        return (removeEntryForKey == null) ? null : removeEntryForKey.value;
    }
    
    final LongHashMap_Entry removeEntryForKey(final long key) {
        final int index = indexFor(hash(key), this.table.length);
        LongHashMap_Entry longHashMap_Entry2;
        LongHashMap_Entry next;
        for (LongHashMap_Entry longHashMap_Entry = longHashMap_Entry2 = this.table[index]; longHashMap_Entry2 != null; longHashMap_Entry2 = next) {
            next = longHashMap_Entry2.next;
            if (longHashMap_Entry2.key == key) {
                ++this.modCount;
                --this.size;
                if (longHashMap_Entry == longHashMap_Entry2) {
                    this.table[index] = next;
                }
                else {
                    longHashMap_Entry.next = next;
                }
                return longHashMap_Entry2;
            }
            longHashMap_Entry = longHashMap_Entry2;
        }
        return longHashMap_Entry2;
    }
    
    private void addEntry(final int hash, final long key, final Object value, final int next) {
        this.table[next] = new LongHashMap_Entry(hash, key, value, this.table[next]);
        if (this.size++ >= this.threshold) {
            this.resize(2 * this.table.length);
        }
    }
}
