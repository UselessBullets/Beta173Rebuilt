// 
// Decompiled by Procyon v0.6.0
// 

package util;

// Useless - Limited information of internal class info, besides a couple constants from b1.2 leaks
public class LongHashMap<V>
{
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int MAXIMUM_CAPACITY = 1073741824;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private transient Entry<V>[] table = new Entry[DEFAULT_INITIAL_CAPACITY];
    private transient int size;
    private int threshold = 12;
    private final float loadFactor = DEFAULT_LOAD_FACTOR;
    private transient volatile int modCount;

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

    // Useless - Exists in b1.2 leak
    public int size() {
        return this.size;
    }

    // Useless - Exists in b1.2 leak
    public boolean isEmpty() {
        return this.size == 0;
    }

    public V get(final long key) {
        int hash = hash(key);

        for (Entry<V> entry = this.table[indexFor(hash, this.table.length)]; entry != null; entry = entry.next) {
            if (entry.key == key) {
                return entry.value;
            }
        }

        return null;
    }

    // Useless - Exists in b1.2 leak
    public boolean containsKey(int var1) {
        return this.getEntry(var1) != null;
    }

    // Useless - Exists in b1.2 leak
    final Entry<V> getEntry(int var1) {
        int hash = hash(var1);

        for (Entry<V> entry = this.table[indexFor(hash, this.table.length)]; entry != null; entry = entry.next) {
            if (entry.key == var1) {
                return entry;
            }
        }

        return null;
    }
    
    public void put(final long key, final V value) {
        final int hash = hash(key);
        final int index = indexFor(hash, this.table.length);

        for (Entry<V> entry = this.table[index]; entry != null; entry = entry.next) {
            if (entry.key == key) {
                entry.value = value;
            }
        }

        this.modCount++;
        this.addEntry(hash, key, value, index);
    }
    
    private void resize(final int newSize) {
        if (this.table.length == MAXIMUM_CAPACITY) {
            this.threshold = Integer.MAX_VALUE;
        } else {
            final Entry<V>[] newTable = new Entry[newSize];
            this.transfer(newTable);
            this.table = newTable;
            this.threshold = (int) (newSize * this.loadFactor);
        }
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
    
    public V remove(final long key) {
        final Entry<V> removed = this.removeEntryForKey(key);
        return (removed == null) ? null : removed.value;
    }
    
    final Entry<V> removeEntryForKey(final long key) {
        int hash = hash(key);
        final int index = indexFor(hash, this.table.length);
        Entry<V> current = this.table[index];
        Entry<V> last = current;

        while (current != null) {
            Entry<V> next = current.next;
            if (current.key == key) {
                this.modCount++;
                this.size--;
                if (last == current) {
                    this.table[index] = next;
                }
                else {
                    last.next = next;
                }
                return current;
            }
            last = current;
            current = next;
        }
        return current;
    }

    // Useless - Exists in b1.2 leak
    public void clear() {
        this.modCount++;
        Entry<V>[] var1 = this.table;

        for (int i = 0; i < var1.length; i++) {
            var1[i] = null;
        }

        this.size = 0;
    }

    // Useless - Exists in b1.2 leak
    public boolean containsValue(Object var1) {
        if (var1 == null) {
            return this.containsNullValue();
        }

        Entry<V>[] var2 = this.table;

        for (int i = 0; i < var2.length; i++) {
            for (Entry<V> entry = var2[i]; entry != null; entry = entry.next) {
                if (var1.equals(entry.value)) return true;
            }
        }

        return false;
    }

    // Useless - Exists in b1.2 leak
    private boolean containsNullValue() {
        Entry<V>[] var1 = this.table;

        for (int i = 0; i < var1.length; i++) {
            for (Entry<V> entry = var1[i]; entry != null; entry = entry.next) {
                if (entry.value == null) return true;
            }
        }

        return false;
    }
    
    private void addEntry(final int hash, final long key, final Object value, final int next) {
        this.table[next] = new Entry(hash, key, value, this.table[next]);
        if (this.size++ >= this.threshold) {
            this.resize(2 * this.table.length);
        }
    }

    static class Entry<V>
    {
        final long key;
        V value;
        Entry<V> next;
        final int hash;

        Entry(final int hash, final long key, final V value, final Entry<V> next) {
            this.value = value;
            this.next = next;
            this.key = key;
            this.hash = hash;
        }

        public final long getKey() {
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
            final Entry<?> entry = (Entry<?>)o;
            final Long k1 = this.getKey();
            final Long k2 = entry.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2))) {
                final Object v1 = this.getValue();
                final Object v2 = entry.getValue();
                return v1 == v2 || (v1 != null && v1.equals(v2));
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
