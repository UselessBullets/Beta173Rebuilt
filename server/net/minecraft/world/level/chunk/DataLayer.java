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
        final int n = x << 11 | z << 7 | y;
        final int n2 = n >> 1;
        if ((n & 0x1) == 0x0) {
            return this.data[n2] & 0xF;
        }
        return this.data[n2] >> 4 & 0xF;
    }
    
    public void set(final int x, final int y, final int z, final int val) {
        final int n = x << 11 | z << 7 | y;
        final int n2 = n >> 1;
        if ((n & 0x1) == 0x0) {
            this.data[n2] = (byte)((this.data[n2] & 0xF0) | (val & 0xF));
        }
        else {
            this.data[n2] = (byte)((this.data[n2] & 0xF) | (val & 0xF) << 4);
        }
    }
    
    public boolean isValid() {
        return this.data != null;
    }
}
