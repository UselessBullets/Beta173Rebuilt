// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

public class TilePos
{
    public final int x, y, z;
    
    public TilePos(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof TilePos) {
            final TilePos tp = (TilePos)o;
            return tp.x == this.x && tp.y == this.y && tp.z == this.z;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.x * 8976890 + this.y * 981131 + this.z;
    }
}
