// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import java.util.List;
import net.minecraft.world.level.LevelData;
import net.minecraft.world.level.chunk.storage.McRegionChunkStorage;
import net.minecraft.world.level.dimension.HellDimension;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.dimension.Dimension;
import java.io.File;

public class McRegionLevelStorage extends DirectoryLevelStorage
{
    public McRegionLevelStorage(final File dir, final String levelId, final boolean createPlayerDir) {
        super(dir, levelId, createPlayerDir);
    }
    
    @Override
    public ChunkStorage createChunkStorage(final Dimension dimension) {
        final File folder = this.getFolder();
        if (dimension instanceof HellDimension) {
            final File saveFile = new File(folder, "DIM-1");
            saveFile.mkdirs();
            return new McRegionChunkStorage(saveFile);
        }
        return new McRegionChunkStorage(folder);
    }
    
    @Override
    public void saveLevelData(final LevelData levelData, final List players) {
        levelData.setVersion(19132);
        super.saveLevelData(levelData, players);
    }
    
    @Override
    public void closeAll() {
        RegionFileCache.clear();
    }
}
