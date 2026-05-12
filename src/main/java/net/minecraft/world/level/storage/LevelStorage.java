// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import java.io.File;
import java.util.List;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.LevelData;

public interface LevelStorage
{
    LevelData prepareLevel();
    
    void checkSession();
    
    ChunkStorage createChunkStorage(final Dimension dimension);
    
    void saveLevelData(final LevelData levelData, final List players);
    
    void saveLevelData(final LevelData levelData);
    
    File getDataFile(final String id);
}
