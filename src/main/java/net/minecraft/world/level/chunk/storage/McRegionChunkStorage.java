// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.chunk.storage;

import net.minecraft.world.level.LevelData;
import java.io.DataOutputStream;

import com.mojang.nbt.CompoundTag;
import java.io.DataInputStream;

import com.mojang.nbt.NbtIo;
import net.minecraft.world.level.storage.RegionFileCache;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.Level;
import java.io.File;
import java.io.IOException;

public class McRegionChunkStorage implements ChunkStorage
{
    private final File saveFile;
    
    public McRegionChunkStorage(final File saveFile) {
        this.saveFile = saveFile;
    }
    
    public LevelChunk load(final Level level, final int x, final int z) throws IOException {
        final DataInputStream regionChunkInputStream = RegionFileCache.getChunkDataInputStream(this.saveFile, x, z);
        if (regionChunkInputStream == null) {
            return null;
        }

        final CompoundTag chunkData = NbtIo.read(regionChunkInputStream);
        if (!chunkData.contains("Level")) {
            System.out.println("Chunk file at " + x + "," + z + " is missing level data, skipping");
            return null;
        }
        if (!chunkData.getCompound("Level").contains("Blocks")) {
            System.out.println("Chunk file at " + x + "," + z + " is missing block data, skipping");
            return null;
        }
        LevelChunk levelChunk = OldChunkStorage.load(level, chunkData.getCompound("Level"));
        if (!levelChunk.isAt(x, z)) {
            System.out.println("Chunk file at " + x + "," + z + " is in the wrong location; relocating. (Expected " + x + ", " + z + ", got " + levelChunk.x + ", " + levelChunk.z + ")");
            chunkData.putInt("xPos", x);
            chunkData.putInt("zPos", z);
            levelChunk = OldChunkStorage.load(level, chunkData.getCompound("Level"));
        }
        levelChunk.attemptCompression();
        return levelChunk;
    }
    
    public void save(final Level level, final LevelChunk levelChunk) throws IOException {
        level.checkSession();

        try {
            final DataOutputStream output = RegionFileCache.getChunkDataOutputStream(this.saveFile, levelChunk.x, levelChunk.z);
            final CompoundTag tag = new CompoundTag();
            final CompoundTag levelData = new CompoundTag();

            tag.put("Level", levelData);
            OldChunkStorage.save(levelChunk, level, levelData);
            NbtIo.write(tag, output);
            output.close();

            final LevelData levelInfo = level.getLevelData();
            levelInfo.setSizeOnDisk(levelInfo.getSizeOnDisk() + RegionFileCache.getSizeDelta(this.saveFile, levelChunk.x, levelChunk.z));
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void saveEntities(final Level level, final LevelChunk levelChunk) throws IOException {
    }
    
    public void tick() {
    }
    
    public void flush() {
    }
}
