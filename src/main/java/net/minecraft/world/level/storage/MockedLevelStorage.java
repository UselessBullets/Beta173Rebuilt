// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import java.io.File;
import java.util.List;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.LevelData;

public class MockedLevelStorage implements LevelStorage
{
    public LevelData prepareLevel() {
        return null;
    }
    
    public void checkSession() {
    }
    
    public ChunkStorage createChunkStorage(final Dimension dimension) {
        return null;
    }
    
    public void saveLevelData(final LevelData levelData, final List players) {
    }
    
    public void saveLevelData(final LevelData levelData) {
    }
    
    public File getDataFile(final String id) {
        return null;
    }
}
