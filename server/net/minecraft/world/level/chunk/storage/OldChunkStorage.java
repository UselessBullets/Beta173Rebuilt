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
    private File a;
    private boolean b;
    
    public OldChunkStorage(final File file, final boolean boolean2) {
        this.a = file;
        this.b = boolean2;
    }
    
    private File a(final int integer1, final int integer2) {
        final String string = "c." + Integer.toString(integer1, 36) + "." + Integer.toString(integer2, 36) + ".dat";
        final String string2 = Integer.toString(integer1 & 0x3F, 36);
        final String string3 = Integer.toString(integer2 & 0x3F, 36);
        final File parent = new File(this.a, string2);
        if (!parent.exists()) {
            if (!this.b) {
                return null;
            }
            parent.mkdir();
        }
        final File parent2 = new File(parent, string3);
        if (!parent2.exists()) {
            if (!this.b) {
                return null;
            }
            parent2.mkdir();
        }
        final File file = new File(parent2, string);
        if (!file.exists() && !this.b) {
            return null;
        }
        return file;
    }
    
    public LevelChunk load(final Level level, final int x, final int z) {
        final File a = this.a(x, z);
        if (a != null && a.exists()) {
            try {
                final CompoundTag compressed = NbtIo.readCompressed(new FileInputStream(a));
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
        final File a = this.a(levelChunk.x, levelChunk.z);
        if (a.exists()) {
            final LevelData levelData = level.getLevelData();
            levelData.setSizeOnDisk(levelData.getSizeOnDisk() - a.length());
        }
        try {
            final File file = new File(this.a, "tmp_chunk.dat");
            final FileOutputStream out = new FileOutputStream(file);
            final CompoundTag tag = new CompoundTag();
            final CompoundTag compoundTag = new CompoundTag();
            tag.put("Level", compoundTag);
            save(levelChunk, level, compoundTag);
            NbtIo.writeCompressed(tag, out);
            out.close();
            if (a.exists()) {
                a.delete();
            }
            file.renameTo(a);
            final LevelData levelData2 = level.getLevelData();
            levelData2.setSizeOnDisk(levelData2.getSizeOnDisk() + a.length());
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void save(final LevelChunk hi, final Level dj, final CompoundTag iq) {
        dj.checkSession();
        iq.putInt("xPos", hi.x);
        iq.putInt("zPos", hi.z);
        iq.putLong("LastUpdate", dj.getTime());
        iq.putByteArray("Blocks", hi.blocks);
        iq.putByteArray("Data", hi.data.data);
        iq.putByteArray("SkyLight", hi.skyLight.data);
        iq.putByteArray("BlockLight", hi.blockLight.data);
        iq.putByteArray("HeightMap", hi.heightmap);
        iq.putBoolean("TerrainPopulated", hi.terrainPopulated);
        hi.lastSaveHadEntities = false;
        final ListTag tag = new ListTag();
        for (int i = 0; i < hi.entityBlocks.length; ++i) {
            for (final Entity entity : hi.entityBlocks[i]) {
                hi.lastSaveHadEntities = true;
                final CompoundTag compoundTag = new CompoundTag();
                if (entity.save(compoundTag)) {
                    tag.add(compoundTag);
                }
            }
        }
        iq.put("Entities", tag);
        final ListTag tag2 = new ListTag();
        for (final TileEntity tileEntity : hi.tileEntities.values()) {
            final CompoundTag compoundTag2 = new CompoundTag();
            tileEntity.save(compoundTag2);
            tag2.add(compoundTag2);
        }
        iq.put("TileEntities", tag2);
    }
    
    public static LevelChunk load(final Level dj, final CompoundTag iq) {
        final LevelChunk levelChunk = new LevelChunk(dj, iq.getInt("xPos"), iq.getInt("zPos"));
        levelChunk.blocks = iq.getByteArray("Blocks");
        levelChunk.data = new DataLayer(iq.getByteArray("Data"));
        levelChunk.skyLight = new DataLayer(iq.getByteArray("SkyLight"));
        levelChunk.blockLight = new DataLayer(iq.getByteArray("BlockLight"));
        levelChunk.heightmap = iq.getByteArray("HeightMap");
        levelChunk.terrainPopulated = iq.getBoolean("TerrainPopulated");
        if (!levelChunk.data.isValid()) {
            levelChunk.data = new DataLayer(levelChunk.blocks.length);
        }
        if (levelChunk.heightmap == null || !levelChunk.skyLight.isValid()) {
            levelChunk.heightmap = new byte[256];
            levelChunk.skyLight = new DataLayer(levelChunk.blocks.length);
            levelChunk.recalcHeightmapOnly();
        }
        if (!levelChunk.blockLight.isValid()) {
            levelChunk.blockLight = new DataLayer(levelChunk.blocks.length);
            levelChunk.recalcBlocksLights();
        }
        final ListTag list = iq.getList("Entities");
        if (list != null) {
            for (int i = 0; i < list.size(); ++i) {
                final Entity loadStatic = EntityIO.loadStatic((CompoundTag)list.get(i), dj);
                levelChunk.lastSaveHadEntities = true;
                if (loadStatic != null) {
                    levelChunk.addEntity(loadStatic);
                }
            }
        }
        final ListTag list2 = iq.getList("TileEntities");
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
