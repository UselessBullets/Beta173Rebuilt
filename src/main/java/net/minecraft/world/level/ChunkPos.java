// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

import net.minecraft.world.entity.Entity;

public class ChunkPos
{
    public final int x, z;
    
    public ChunkPos(final int x, final int z) {
        this.x = x;
        this.z = z;
    }
    
    public static int hashCode(final int x, final int z) {
        return ((x < 0) ? Integer.MIN_VALUE : 0) | (x & 0x7FFF) << 16 | ((z < 0) ? 0x8000 : 0) | (z & 0x7FFF);
    }
    
    @Override
    public int hashCode() {
        return hashCode(this.x, this.z);
    }
    
    @Override
    public boolean equals(final Object obj) {
        final ChunkPos chunkPos = (ChunkPos)obj;
        return chunkPos.x == this.x && chunkPos.z == this.z;
    }

    // Useless - In B1.2 and LCE leaks
    public double distanceToSqr(Entity e) {
        double xPos = this.x * 16 + 8;
        double zPos = this.z * 16 + 8;

        double xd = xPos - e.x;
        double zd = zPos - e.z;

        return xd * xd + zd * zd;
    }
}
