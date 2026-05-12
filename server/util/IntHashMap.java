// 
// Decompiled by Procyon v0.6.0
// 

package util;

public class IntHashMap
{
    private transient IntHashMap_Entry[] table;
    private transient int size;
    private int threshold;
    private final float loadFactor;
    private transient volatile int modCount;
    
    public IntHashMap() {
        this.loadFactor = 0.75f;
        this.threshold = 12;
        this.table = new IntHashMap_Entry[16];
    }
    
    private static int hash(int i) {
        i ^= (i >>> 20 ^ i >>> 12);
        return i ^ i >>> 7 ^ i >>> 4;
    }
    
    private static int indexFor(final int hash, final int length) {
        return hash & length - 1;
    }
    
    public Object get(final int key) {
        for (IntHashMap_Entry next = this.table[indexFor(hash(key), this.table.length)]; next != null; next = next.next) {
            if (next.key == key) {
                return next.value;
            }
        }
        return null;
    }
    
    public boolean containsKey(final int key) {
        return this.getEntry(key) != null;
    }
    
    final IntHashMap_Entry getEntry(final int key) {
        for (IntHashMap_Entry next = this.table[indexFor(hash(key), this.table.length)]; next != null; next = next.next) {
            if (next.key == key) {
                return next;
            }
        }
        return null;
    }
    
    public void put(final int key, final Object value) {
        final int hash = hash(key);
        final int index = indexFor(hash, this.table.length);
        for (IntHashMap_Entry next = this.table[index]; next != null; next = next.next) {
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
        final IntHashMap_Entry[] array = new IntHashMap_Entry[newSize];
        this.transfer(array);
        this.table = array;
        this.threshold = (int)(newSize * this.loadFactor);
    }
    
    private void transfer(final IntHashMap_Entry[] newTable) {
        final IntHashMap_Entry[] table = this.table;
        final int length = newTable.length;
        for (int i = 0; i < table.length; ++i) {
            IntHashMap_Entry intHashMap_Entry = table[i];
            if (intHashMap_Entry != null) {
                table[i] = null;
                do {
                    final IntHashMap_Entry next = intHashMap_Entry.next;
                    final int index = indexFor(intHashMap_Entry.hash, length);
                    intHashMap_Entry.next = newTable[index];
                    newTable[index] = intHashMap_Entry;
                    intHashMap_Entry = next;
                } while (intHashMap_Entry != null);
            }
        }
    }
    
    public Object remove(final int key) {
        final IntHashMap_Entry removeEntryForKey = this.removeEntryForKey(key);
        return (removeEntryForKey == null) ? null : removeEntryForKey.value;
    }
    
    final IntHashMap_Entry removeEntryForKey(final int key) {
        final int index = indexFor(hash(key), this.table.length);
        IntHashMap_Entry intHashMap_Entry2;
        IntHashMap_Entry next;
        for (IntHashMap_Entry intHashMap_Entry = intHashMap_Entry2 = this.table[index]; intHashMap_Entry2 != null; intHashMap_Entry2 = next) {
            next = intHashMap_Entry2.next;
            if (intHashMap_Entry2.key == key) {
                ++this.modCount;
                --this.size;
                if (intHashMap_Entry == intHashMap_Entry2) {
                    this.table[index] = next;
                }
                else {
                    intHashMap_Entry.next = next;
                }
                return intHashMap_Entry2;
            }
            intHashMap_Entry = intHashMap_Entry2;
        }
        return intHashMap_Entry2;
    }
    
    public void clear() {
        ++this.modCount;
        final IntHashMap_Entry[] table = this.table;
        for (int i = 0; i < table.length; ++i) {
            table[i] = null;
        }
        this.size = 0;
    }
    
    private void addEntry(final int hash, final int key, final Object value, final int next) {
        this.table[next] = new IntHashMap_Entry(hash, key, value, this.table[next]);
        if (this.size++ >= this.threshold) {
            this.resize(2 * this.table.length);
        }
    }
}
