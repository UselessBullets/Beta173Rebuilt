// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.chunk.storage;

import net.minecraft.world.entity.EntityIO;
import net.minecraft.world.level.chunk.DataLayer;

import java.io.IOException;

import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.entity.Entity;
import com.mojang.nbt.ListTag;
import net.minecraft.world.level.LevelData;

import java.io.FileOutputStream;
import com.mojang.nbt.CompoundTag;
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
        String name = "c." + Integer.toString(x, 36) + "." + Integer.toString(z, 36) + ".dat";
        String path1 = Integer.toString(x & 0x3F, 36);
        String path2 = Integer.toString(z & 0x3F, 36);

        File file = new File(this.dir, path1);
        if (!file.exists()) {
            if (this.create) file.mkdir();
            else return null;
        }

        file = new File(file, path2);
        if (!file.exists()) {
            if (this.create) file.mkdir();
            else return null;
        }

        file = new File(file, name);
        if (!file.exists()) {
            if (!this.create) return null;
        }
        return file;
    }
    
    public LevelChunk load(final Level level, final int x, final int z) throws IOException {
        final File file = this.getFile(x, z);
        if (file != null && file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                final CompoundTag tag = NbtIo.readCompressed(fis);
                if (!tag.contains("Level")) {
                    System.out.println("Chunk file at " + x + "," + z + " is missing level data, skipping");
                    return null;
                }
                if (!tag.getCompound("Level").contains("Blocks")) {
                    System.out.println("Chunk file at " + x + "," + z + " is missing block data, skipping");
                    return null;
                }
                LevelChunk levelChunk = load(level, tag.getCompound("Level"));
                if (!levelChunk.isAt(x, z)) {
                    System.out.println("Chunk file at " + x + "," + z + " is in the wrong location; relocating. (Expected " + x + ", " + z + ", got " + levelChunk.x + ", " + levelChunk.z + ")");
                    tag.putInt("xPos", x);
                    tag.putInt("zPos", z);
                    levelChunk = load(level, tag.getCompound("Level"));
                }
                levelChunk.attemptCompression();
                return levelChunk;
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public void save(final Level level, final LevelChunk levelChunk) throws IOException {
        level.checkSession();
        final File file = this.getFile(levelChunk.x, levelChunk.z);
        if (file.exists()) {
            final LevelData levelData = level.getLevelData();
            levelData.setSizeOnDisk(levelData.getSizeOnDisk() - file.length());
        }

        try {
            final File tmpFile = new File(this.dir, "tmp_chunk.dat");

            final FileOutputStream fos = new FileOutputStream(tmpFile);
            final CompoundTag tag = new CompoundTag();
            final CompoundTag levelData = new CompoundTag();
            tag.put("Level", levelData);
            save(levelChunk, level, levelData);
            NbtIo.writeCompressed(tag, fos);
            fos.close();

            if (file.exists()) {
                file.delete();
            }
            tmpFile.renameTo(file);

            final LevelData levelInfo = level.getLevelData();
            levelInfo.setSizeOnDisk(levelInfo.getSizeOnDisk() + file.length());
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
        final ListTag<CompoundTag> entityTags = new ListTag<>();
        for (int i = 0; i < lc.entityBlocks.length; ++i) {
            for (final Entity entity : lc.entityBlocks[i]) {
                lc.lastSaveHadEntities = true;
                final CompoundTag eTag = new CompoundTag();
                if (entity.save(eTag)) {
                    entityTags.add(eTag);
                }
            }
        }
        tag.put("Entities", entityTags);

        final ListTag<CompoundTag> tileEntityTags = new ListTag<>();
        for (final TileEntity tileEntity : lc.tileEntities.values()) {
            final CompoundTag teTag = new CompoundTag();
            tileEntity.save(teTag);
            tileEntityTags.add(teTag);
        }
        tag.put("TileEntities", tileEntityTags);
    }
    
    public static LevelChunk load(final Level level, final CompoundTag tag) {
        int x = tag.getInt("xPos");
        int z = tag.getInt("zPos");

        final LevelChunk levelChunk = new LevelChunk(level, x, z);
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
            levelChunk.heightmap = new byte[16 * 16];
            levelChunk.skyLight = new DataLayer(levelChunk.blocks.length);
            levelChunk.recalcHeightmap();
        }

        if (!levelChunk.blockLight.isValid()) {
            levelChunk.blockLight = new DataLayer(levelChunk.blocks.length);
            levelChunk.recalcBlocksLights();
        }

        final ListTag<CompoundTag> entityTags = (ListTag<CompoundTag>) tag.getList("Entities");
        if (entityTags != null) {
            for (int i = 0; i < entityTags.size(); ++i) {
                CompoundTag eTag = entityTags.get(i);
                final Entity e = EntityIO.loadStatic(eTag, level);
                levelChunk.lastSaveHadEntities = true;
                if (e != null) {
                    levelChunk.addEntity(e);
                }
            }
        }

        final ListTag<CompoundTag> tileEntityTags = (ListTag<CompoundTag>) tag.getList("TileEntities");
        if (tileEntityTags != null) {
            for (int i = 0; i < tileEntityTags.size(); ++i) {
                CompoundTag teTag = tileEntityTags.get(i);
                final TileEntity te = TileEntity.loadStatic(teTag);
                if (te != null) {
                    levelChunk.addTileEntity(te);
                }
            }
        }
        return levelChunk;
    }
    
    public void tick() {
    }
    
    public void flush() {
    }
    
    public void saveEntities(final Level level, final LevelChunk levelChunk) throws IOException {
    }
}
