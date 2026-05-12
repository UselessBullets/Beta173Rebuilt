// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import java.io.ByteArrayOutputStream;

class RegionFile_ChunkBuffer extends ByteArrayOutputStream
{
    private int x;
    private int z;
    final /* synthetic */ RegionFile rf;
    
    public RegionFile_ChunkBuffer(final RegionFile regionFile, final int x, final int z) {
        this.rf = regionFile;
        super(8096);
        this.x = x;
        this.z = z;
    }
    
    @Override
    public void close() {
        this.rf.write(this.x, this.z, this.buf, this.count);
    }
}
