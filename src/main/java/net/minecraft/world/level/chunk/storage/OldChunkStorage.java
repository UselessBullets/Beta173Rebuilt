// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.chunk.storage;

import net.minecraft.world.entity.EntityIO;
import net.minecraft.world.level.chunk.DataLayer;
import java.util.Iterator;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.entity.Entity;
import com.mojang.nbt.ListTag;
import net.minecraft.world.level.LevelData;
import java.io.OutputStream;
import com.mojang.nbt.Tag;
import java.io.FileOutputStream;
import com.mojang.nbt.CompoundTag;
import java.io.InputStream;
import com.mojang.nbt.NbtIo;
import java.io.FileInputStream;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.Level;
import java.io.File;

public class OldChunkStorage implements ChunkStorage
{
    private File dir;
    private boolean create;
    
    public OldChunkStorage(final File dir, final boolean create) {
        this.dir = dir;
        this.create = create;
    }
    
    private File getFile(final int x, final int z) {
        final String string = "c." + Integer.toString(x, 36) + "." + Integer.toString(z, 36) + ".dat";
        final String string2 = Integer.toString(x & 0x3F, 36);
        final String string3 = Integer.toString(z & 0x3F, 36);
        final File parent = new File(this.dir, string2);
        if (!parent.exists()) {
            if (!this.create) {
                return null;
            }
            parent.mkdir();
        }
        final File parent2 = new File(parent, string3);
        if (!parent2.exists()) {
            if (!this.create) {
                return null;
            }
            parent2.mkdir();
        }
        final File file = new File(parent2, string);
        if (!file.exists() && !this.create) {
            return null;
        }
        return file;
    }
    
    public LevelChunk load(final Level level, final int x, final int z) {
        final File file = this.getFile(x, z);
        if (file != null && file.exists()) {
            try {
                final CompoundTag compressed = NbtIo.readCompressed(new FileInputStream(file));
                if (!compressed.contains("Level")) {
                    System.out.println("Chunk file at " + x + "," + z + " is missing level data, skipping");
                    return null;
                }
                if (!compressed.getCompound("Level").contains("Blocks")) {
                    System.out.println("Chunk file at " + x + "," + z + " is missing block data, skipping");
                    return null;
                }
                LevelChunk levelChunk = load(level, compressed.getCompound("Level"));
                if (!levelChunk.isAt(x, z)) {
                    System.out.println("Chunk file at " + x + "," + z + " is in the wrong location; relocating. (Expected " + x + ", " + z + ", got " + levelChunk.x + ", " + levelChunk.z + ")");
                    compressed.putInt("xPos", x);
                    compressed.putInt("zPos", z);
                    levelChunk = load(level, compressed.getCompound("Level"));
                }
                levelChunk.attemptCompression();
                return levelChunk;
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    
    public void save(final Level level, final LevelChunk levelChunk) {
        level.checkSession();
        final File file = this.getFile(levelChunk.x, levelChunk.z);
        if (file.exists()) {
            final LevelData levelData = level.getLevelData();
            levelData.setSizeOnDisk(levelData.getSizeOnDisk() - file.length());
        }
        try {
            final File file2 = new File(this.dir, "tmp_chunk.dat");
            final FileOutputStream out = new FileOutputStream(file2);
            final CompoundTag tag = new CompoundTag();
            final CompoundTag compoundTag = new CompoundTag();
            tag.put("Level", compoundTag);
            save(levelChunk, level, compoundTag);
            NbtIo.writeCompressed(tag, out);
            out.close();
            if (file.exists()) {
                file.delete();
            }
            file2.renameTo(file);
            final LevelData levelData2 = level.getLevelData();
            levelData2.setSizeOnDisk(levelData2.getSizeOnDisk() + file.length());
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void save(final LevelChunk lc, final Level level, final CompoundTag tag) {
        level.checkSession();
        tag.putInt("xPos", lc.x);
        tag.putInt("zPos", lc.z);
        tag.putLong("LastUpdate", level.getTime());
        tag.putByteArray("Blocks", lc.blocks);
        tag.putByteArray("Data", lc.data.data);
        tag.putByteArray("SkyLight", lc.skyLight.data);
        tag.putByteArray("BlockLight", lc.blockLight.data);
        tag.putByteArray("HeightMap", lc.heightmap);
        tag.putBoolean("TerrainPopulated", lc.terrainPopulated);
        lc.lastSaveHadEntities = false;
        final ListTag tag2 = new ListTag();
        for (int i = 0; i < lc.entityBlocks.length; ++i) {
            for (final Entity entity : lc.entityBlocks[i]) {
                lc.lastSaveHadEntities = true;
                final CompoundTag compoundTag = new CompoundTag();
                if (entity.save(compoundTag)) {
                    tag2.add(compoundTag);
                }
            }
        }
        tag.put("Entities", tag2);
        final ListTag tag3 = new ListTag();
        for (final TileEntity tileEntity : lc.tileEntities.values()) {
            final CompoundTag compoundTag2 = new CompoundTag();
            tileEntity.save(compoundTag2);
            tag3.add(compoundTag2);
        }
        tag.put("TileEntities", tag3);
    }
    
    public static LevelChunk load(final Level level, final CompoundTag tag) {
        final LevelChunk levelChunk = new LevelChunk(level, tag.getInt("xPos"), tag.getInt("zPos"));
        levelChunk.blocks = tag.getByteArray("Blocks");
        levelChunk.data = new DataLayer(tag.getByteArray("Data"));
        levelChunk.skyLight = new DataLayer(tag.getByteArray("SkyLight"));
        levelChunk.blockLight = new DataLayer(tag.getByteArray("BlockLight"));
        levelChunk.heightmap = tag.getByteArray("HeightMap");
        levelChunk.terrainPopulated = tag.getBoolean("TerrainPopulated");
        if (!levelChunk.data.isValid()) {
            levelChunk.data = new DataLayer(levelChunk.blocks.length);
        }
        if (levelChunk.heightmap == null || !levelChunk.skyLight.isValid()) {
            levelChunk.heightmap = new byte[256];
            levelChunk.skyLight = new DataLayer(levelChunk.blocks.length);
            levelChunk.recalcHeightmap();
        }
        if (!levelChunk.blockLight.isValid()) {
            levelChunk.blockLight = new DataLayer(levelChunk.blocks.length);
            levelChunk.recalcBlocksLights();
        }
        final ListTag list = tag.getList("Entities");
        if (list != null) {
            for (int i = 0; i < list.size(); ++i) {
                final Entity loadStatic = EntityIO.loadStatic((CompoundTag)list.get(i), level);
                levelChunk.lastSaveHadEntities = true;
                if (loadStatic != null) {
                    levelChunk.addEntity(loadStatic);
                }
            }
        }
        final ListTag list2 = tag.getList("TileEntities");
        if (list2 != null) {
            for (int j = 0; j < list2.size(); ++j) {
                final TileEntity loadStatic2 = TileEntity.loadStatic((CompoundTag)list2.get(j));
                if (loadStatic2 != null) {
                    levelChunk.addTileEntity(loadStatic2);
                }
            }
        }
        return levelChunk;
    }
    
    public void tick() {
    }
    
    public void flush() {
    }
    
    public void saveEntities(final Level level, final LevelChunk levelChunk) {
    }
}
