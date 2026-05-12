// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.chunk;

import util.ProgressListener;

public interface ChunkSource
{
    boolean hasChunk(final int x, final int z);
    
    LevelChunk getChunk(final int x, final int z);
    
    LevelChunk create(final int x, final int z);
    
    void postProcess(final ChunkSource parent, final int x, final int z);
    
    boolean save(final boolean force, final ProgressListener progressListener);
    
    boolean tick();
    
    boolean shouldSave();
}
