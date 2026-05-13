// 
// Decompiled by Procyon v0.6.0
// 

package util;

public class IntHashMap<V>
{
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int MAXIMUM_CAPACITY = 1073741824;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private transient Entry<V>[] table;
    private transient int size;
    private int threshold;
    private final float loadFactor;
    private transient volatile int modCount;
    
    public IntHashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.threshold = 12;
        this.table = new Entry[DEFAULT_INITIAL_CAPACITY];
    }
    
    private static int hash(int i) {
        i ^= (i >>> 20 ^ i >>> 12);
        return i ^ i >>> 7 ^ i >>> 4;
    }
    
    private static int indexFor(final int hash, final int length) {
        return hash & length - 1;
    }
    
    public V get(final int key) {
        for (Entry<V> next = this.table[indexFor(hash(key), this.table.length)]; next != null; next = next.next) {
            if (next.key == key) {
                return next.value;
            }
        }
        return null;
    }

    public boolean containsKey(final int key) {
        return this.getEntry(key) != null;
    }

    final Entry<V> getEntry(final int key) {
        for (Entry<V> next = this.table[indexFor(hash(key), this.table.length)]; next != null; next = next.next) {
            if (next.key == key) {
                return next;
            }
        }
        return null;
    }
    
    public void put(final int key, final V value) {
        final int hash = hash(key);
        final int index = indexFor(hash, this.table.length);
        for (Entry<V> next = this.table[index]; next != null; next = next.next) {
            if (next.key == key) {
                next.value = value;
            }
        }
        ++this.modCount;
        this.addEntry(hash, key, value, index);
    }
    
    private void resize(final int newSize) {
        if (this.table.length == MAXIMUM_CAPACITY) {
            this.threshold = Integer.MAX_VALUE;
            return;
        }
        final Entry<V>[] array = new Entry[newSize];
        this.transfer(array);
        this.table = array;
        this.threshold = (int)(newSize * this.loadFactor);
    }
    
    private void transfer(final Entry<V>[] newTable) {
        final Entry<V>[] table = this.table;
        final int length = newTable.length;
        for (int i = 0; i < table.length; ++i) {
            Entry<V> entry = table[i];
            if (entry != null) {
                table[i] = null;
                do {
                    final Entry<V> next = entry.next;
                    final int index = indexFor(entry.hash, length);
                    entry.next = newTable[index];
                    newTable[index] = entry;
                    entry = next;
                } while (entry != null);
            }
        }
    }
    
    public Object remove(final int key) {
        final Entry<V> removeEntryForKey = this.removeEntryForKey(key);
        return (removeEntryForKey == null) ? null : removeEntryForKey.value;
    }
    
    final Entry<V> removeEntryForKey(final int key) {
        final int index = indexFor(hash(key), this.table.length);
        Entry<V> entry2;
        Entry<V> next;
        for (Entry<V> entry = entry2 = this.table[index]; entry2 != null; entry2 = next) {
            next = entry2.next;
            if (entry2.key == key) {
                ++this.modCount;
                --this.size;
                if (entry == entry2) {
                    this.table[index] = next;
                }
                else {
                    entry.next = next;
                }
                return entry2;
            }
            entry = entry2;
        }
        return entry2;
    }
    
    public void clear() {
        ++this.modCount;
        final Entry<V>[] table = this.table;
        for (int i = 0; i < table.length; ++i) {
            table[i] = null;
        }
        this.size = 0;
    }
    
    private void addEntry(final int hash, final int key, final V value, final int next) {
        this.table[next] = new Entry<>(hash, key, value, this.table[next]);
        if (this.size++ >= this.threshold) {
            this.resize(2 * this.table.length);
        }
    }

    static class Entry<V>
    {
        final int key;
        V value;
        Entry<V> next;
        final int hash;

        Entry(final int hash, final int key, final V value, final Entry<V> next) {
            this.value = value;
            this.next = next;
            this.key = key;
            this.hash = hash;
        }

        public final int getKey() {
            return this.key;
        }

        public final V getValue() {
            return this.value;
        }

        @Override
        public final boolean equals(final Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            final Entry entry = (Entry)o;
            final Integer value = this.getKey();
            final Integer value2 = entry.getKey();
            if (value == value2 || (value != null && value.equals(value2))) {
                final Object value3 = this.getValue();
                final Object value4 = entry.getValue();
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
}
