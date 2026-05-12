// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.chunk.storage;

import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.Level;

import java.io.IOException;

public interface ChunkStorage
{
    LevelChunk load(final Level level, final int x, final int z) throws IOException;
    
    void save(final Level level, final LevelChunk levelChunk) throws IOException;
    
    void saveEntities(final Level level, final LevelChunk levelChunk) throws IOException;
    
    void tick();
    
    void flush();
}
