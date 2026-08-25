// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.chunk;

public class DataLayer
{
    public final byte[] data;
    
    public DataLayer(final int length) {
        this.data = new byte[length >> 1];
    }
    
    public DataLayer(final byte[] data) {
        this.data = data;
    }
    
    public int get(final int x, final int y, final int z) {
        final int pos = x << 11 | z << 7 | y;
        final int slot = pos >> 1;
        final int part = pos & 0x1;

        if (part == 0) {
            return this.data[slot] & 0xF;
        } else {
            return this.data[slot] >> 4 & 0xF;
        }
    }
    
    public void set(final int x, final int y, final int z, final int val) {
        final int pos = x << 11 | z << 7 | y;

        final int slot = pos >> 1;
        final int part = pos & 0x1;

        if (part == 0) {
            this.data[slot] = (byte)((this.data[slot] & 0xF0) | (val & 0xF));
        }
        else {
            this.data[slot] = (byte)((this.data[slot] & 0xF) | (val & 0xF) << 4);
        }
    }
    
    public boolean isValid() {
        return this.data != null;
    }

    // Useless - in b1.2 and LCE leaks
    public void setAll(int br) {
        byte val = (byte)(br & br << 4);
        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = val;
        }
    }
}
