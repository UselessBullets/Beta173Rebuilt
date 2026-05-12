// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.chunk.storage;

import net.minecraft.world.level.LevelData;
import java.io.DataOutputStream;
import java.io.DataOutput;
import com.mojang.nbt.Tag;
import com.mojang.nbt.CompoundTag;
import java.io.DataInputStream;
import java.io.DataInput;
import com.mojang.nbt.NbtIo;
import net.minecraft.world.level.storage.RegionFileCache;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.Level;
import java.io.File;

public class McRegionChunkStorage implements ChunkStorage
{
    private final File saveFile;
    
    public McRegionChunkStorage(final File saveFile) {
        this.saveFile = saveFile;
    }
    
    public LevelChunk load(final Level level, final int x, final int z) {
        final DataInputStream chunkDataInputStream = RegionFileCache.getChunkDataInputStream(this.saveFile, x, z);
        if (chunkDataInputStream == null) {
            return null;
        }
        final CompoundTag read = NbtIo.read(chunkDataInputStream);
        if (!read.contains("Level")) {
            System.out.println("Chunk file at " + x + "," + z + " is missing level data, skipping");
            return null;
        }
        if (!read.getCompound("Level").contains("Blocks")) {
            System.out.println("Chunk file at " + x + "," + z + " is missing block data, skipping");
            return null;
        }
        LevelChunk levelChunk = OldChunkStorage.load(level, read.getCompound("Level"));
        if (!levelChunk.isAt(x, z)) {
            System.out.println("Chunk file at " + x + "," + z + " is in the wrong location; relocating. (Expected " + x + ", " + z + ", got " + levelChunk.x + ", " + levelChunk.z + ")");
            read.putInt("xPos", x);
            read.putInt("zPos", z);
            levelChunk = OldChunkStorage.load(level, read.getCompound("Level"));
        }
        levelChunk.attemptCompression();
        return levelChunk;
    }
    
    public void save(final Level level, final LevelChunk levelChunk) {
        level.checkSession();
        try {
            final DataOutputStream chunkDataOutputStream = RegionFileCache.getChunkDataOutputStream(this.saveFile, levelChunk.x, levelChunk.z);
            final CompoundTag tag = new CompoundTag();
            final CompoundTag compoundTag = new CompoundTag();
            tag.put("Level", compoundTag);
            OldChunkStorage.save(levelChunk, level, compoundTag);
            NbtIo.write(tag, chunkDataOutputStream);
            chunkDataOutputStream.close();
            final LevelData levelData = level.getLevelData();
            levelData.setSizeOnDisk(levelData.getSizeOnDisk() + RegionFileCache.getSizeDelta(this.saveFile, levelChunk.x, levelChunk.z));
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void saveEntities(final Level level, final LevelChunk levelChunk) {
    }
    
    public void tick() {
    }
    
    public void flush() {
    }
}
