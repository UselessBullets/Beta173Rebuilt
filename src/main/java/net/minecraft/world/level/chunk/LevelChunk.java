// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.chunk;

import java.util.Random;

import net.minecraft.SharedConstants;
import net.minecraft.world.phys.AABB;

import net.minecraft.world.level.tile.EntityTile;
import net.minecraft.world.level.TilePos;
import net.minecraft.world.level.tile.entity.TileEntity;
import util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.tile.Tile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.level.Level;

public class LevelChunk
{
    // Useless - LCE has a note that this used to be static until 1.8.2, so was presumably just static here like so
    private static final int ENTITY_BLOCKS_LENGTH = Level.MAX_HEIGHT / 16;
    public static boolean touchedSky;
    public byte[] blocks;
    public boolean loaded;
    public Level level;
    public DataLayer data;
    public DataLayer skyLight;
    public DataLayer blockLight;
    public byte[] heightmap;
    public int minHeight;
    public final int x;
    public final int z;
    public Map<TilePos, TileEntity> tileEntities = new HashMap<>();
    public List<Entity>[] entityBlocks = new List[ENTITY_BLOCKS_LENGTH];
    public boolean terrainPopulated = false;
    public boolean unsaved = false;
    public boolean dontSave;
    public boolean lastSaveHadEntities = false;
    public long lastSaveTime = 0L;
    
    public LevelChunk(final Level level, final int x, final int z) {
        this.level = level;
        this.x = x;
        this.z = z;
        this.heightmap = new byte[16 * 16];
        for (int i = 0; i < this.entityBlocks.length; ++i) {
            this.entityBlocks[i] = new ArrayList<>();
        }
    }
    
    public LevelChunk(final Level level, final byte[] blocks, final int x, final int z) {
        this(level, x, z);
        this.blocks = blocks;
        this.data = new DataLayer(blocks.length);
        this.skyLight = new DataLayer(blocks.length);
        this.blockLight = new DataLayer(blocks.length);
    }
    
    public boolean isAt(final int x, final int z) {
        return x == this.x && z == this.z;
    }
    
    public int getHeightmap(final int x, final int z) {
        return this.heightmap[z << 4 | x] & 0xFF;
    }
    
    public void recalcBlocksLights() {
    }
    
    public void recalcHeightmapOnly() {
        int min = Level.MAX_HEIGHT - 1;
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                int y = Level.MAX_HEIGHT - 1;

                int p = x << 11 | z << 7;
                while (y > 0 && Tile.lightBlock[this.blocks[p + y - 1] & 0xFF] == 0) {
                    y--;
                }
                this.heightmap[z << 4 | x] = (byte)y;
                if (y < min) min = y;
            }
        }

        this.minHeight = min;
        this.unsaved = true;
    }
    
    public void recalcHeightmap() {
        int min = Level.MAX_HEIGHT - 1;
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                int y = Level.MAX_HEIGHT - 1;

                int p = (x << 11 | z << 7);
                while (y > 0 && Tile.lightBlock[this.blocks[p + y - 1] & 0xFF] == 0) {
                    --y;
                }
                this.heightmap[z << 4 | x] = (byte)y;
                if (y < min) min = y;

                if (!this.level.dimension.hasCeiling) {
                    int br = Level.MAX_BRIGHTNESS;
                    int yy = Level.MAX_HEIGHT - 1;
                    do {
                        br -= Tile.lightBlock[this.blocks[p + yy] & 0xFF];
                        if (br > 0) {
                            this.skyLight.set(x, yy, z, br);
                        }
                        yy--;
                    } while (yy > 0 && br > 0);
                }
            }
        }

        this.minHeight = min;

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                this.lightGaps(x, z);
            }
        }

        this.unsaved = true;
    }
    
    public void lightLava() {
        // Useless - LCE codebase claims that this function contained code which was fully commented out in the java codebase, presumably it was the code used in the b1.2 leak just commented out so below is the b1.2 lightLava code cleaned up
//        byte min = 32;
//
//        for (int x = 0; x < 16; x++) {
//            for (int z = 0; z < 16; z++) {
//                int p = x << 11 | z << 7;
//
//                for (int y = 0; y < Level.MAX_BUILD_HEIGHT; y++) {
//                    int emit = Tile.lightEmission[this.blocks[p + y]];
//                    if (emit > 0) {
//                        this.blockLight.set(x, y, z, emit);
//                    }
//                }
//
//                int br = Level.MAX_BRIGHTNESS;
//                int yy = min - 2;
//                while (yy < Level.MAX_BUILD_HEIGHT && br > 0) {
//                    yy++;
//                    byte tile = this.blocks[p + yy];
//
//                    int block = Tile.lightBlock[tile];
//                    int emission = Tile.lightEmission[tile];
//                    if (block == 0) block = 1;
//
//                    br -= block;
//                    if (emission > br) br = emission;
//                    if (br < 0) br = 0;
//                    this.blockLight.set(x, yy, z, br);
//                }
//            }
//        }
//
//        this.level.updateLight(LightLayer.Block, this.x * 16, min - 1, this.z * 16, this.x * 16 + 16, min + 1, this.z * 16 + 16);
//        this.unsaved = true;
    }
    
    private void lightGaps(final int x, final int z) {
        final int height = this.getHeightmap(x, z);
        final int xOffs = this.x * 16 + x;
        final int zOffs = this.z * 16 + z;

        this.lightGap(xOffs - 1, zOffs, height);
        this.lightGap(xOffs + 1, zOffs, height);
        this.lightGap(xOffs, zOffs - 1, height);
        this.lightGap(xOffs, zOffs + 1, height);
    }
    
    private void lightGap(final int x, final int z, final int source) {
        final int height = this.level.getHeightmap(x, z);

        if (height > source) {
            this.level.updateLight(LightLayer.Sky, x, source, z, x, height, z);
            this.unsaved = true;
        }
        else if (height < source) {
            this.level.updateLight(LightLayer.Sky, x, height, z, x, source, z);
            this.unsaved = true;
        }
    }
    
    private void recalcHeight(final int x, final int yStart, final int z) {
        int yOld = (this.heightmap[z << 4 | x] & 0xFF);
        int y = yOld;
        if (yStart > yOld) y = yStart;

        int p = x << 11 | z << 7;
        while (y > 0 && Tile.lightBlock[this.blocks[p + y - 1] & 0xFF] == 0) {
            y--;
        }
        if (y == yOld) return;

        this.level.lightColumnChanged(x, z, y, yOld);
        this.heightmap[z << 4 | x] = (byte)y;

        if (y < this.minHeight) {
            this.minHeight = y;
        }
        else {
            int min = Level.MAX_HEIGHT - 1;
            for (int _x = 0; _x < 16; ++_x) {
                for (int _z = 0; _z < 16; ++_z) {
                    if ((this.heightmap[_z << 4 | _x] & 0xFF) < min) min = (this.heightmap[_z << 4 | _x] & 0xFF);
                }
            }
            this.minHeight = min;
        }

        final int xOffs = this.x * 16 + x;
        final int zOffs = this.z * 16 + z;
        if (y < yOld) {
            for (int yy = y; yy < yOld; ++yy) {
                this.skyLight.set(x, yy, z, Level.MAX_BRIGHTNESS);
            }
        }
        else {
            this.level.updateLight(LightLayer.Sky, xOffs, yOld, zOffs, xOffs, y, zOffs);
            for (int l = yOld; l < y; ++l) {
                this.skyLight.set(x, l, z, 0);
            }
        }

        int br = Level.MAX_BRIGHTNESS;

        final int y1 = y;
        while (y > 0 && br > 0) {
            y--;
            int block = Tile.lightBlock[this.getTile(x, y, z)];
            if (block == 0) block = 1;
            br -= block;
            if (br < 0) br = 0;
            this.skyLight.set(x, y, z, br);
        }
        while (y > 0 && Tile.lightBlock[this.getTile(x, y - 1, z)] == 0) {
            --y;
        }
        if (y != y1) {
            this.level.updateLight(LightLayer.Sky, xOffs - 1, y, zOffs - 1, xOffs + 1, y1, zOffs + 1);
        }
        this.unsaved = true;
    }
    
    public int getTile(final int x, final int y, final int z) {
        return this.blocks[x << 11 | z << 7 | y] & 0xFF;
    }
    
    public boolean setTileAndData(final int x, final int y, final int z, final int _tile, final int _data) {
        final byte tile = (byte)_tile;

        int slot = z << 4 | x;
        final int oldHeight = this.heightmap[slot] & 0xFF;
        final int old = this.blocks[x << 11 | z << 7 | y] & 0xFF;
        final int oldData = this.data.get(x, y, z);
        if (old == _tile && oldData == _data) return false;

        final int xOffs = this.x * 16 + x;
        final int zOffs = this.z * 16 + z;
        this.blocks[x << 11 | z << 7 | y] = (byte)(tile & 0xFF);
        if (old != 0) {
            if (!this.level.isClientSide) {
                Tile.tiles[old].onRemove(this.level, xOffs, y, zOffs);
            }
        }
        this.data.set(x, y, z, _data);

        if (!this.level.dimension.hasCeiling) {
            if (Tile.lightBlock[tile & 0xFF] != 0) {
                if (y >= oldHeight) {
                    this.recalcHeight(x, y + 1, z);
                }
            }
            else {
                if (y == oldHeight - 1) {
                    this.recalcHeight(x, y, z);
                }
            }

            this.level.updateLight(LightLayer.Sky, xOffs, y, zOffs, xOffs, y, zOffs);
        }
        this.level.updateLight(LightLayer.Block, xOffs, y, zOffs, xOffs, y, zOffs);
        this.lightGaps(x, z);
        this.data.set(x, y, z, _data);
        if (_tile != 0) {
            Tile.tiles[_tile].onPlace(this.level, xOffs, y, zOffs);
        }
        return this.unsaved = true;
    }
    
    public boolean setTile(final int x, final int y, final int z, final int _tile) {
        final byte tile = (byte)_tile;

        int slot = z << 4 | x;
        final int oldHeight = this.heightmap[slot] & 0xFF;
        final int old = this.blocks[x << 11 | z << 7 | y] & 0xFF;
        if (old == _tile) return false;

        final int xOffs = this.x * 16 + x;
        final int zOffs = this.z * 16 + z;
        this.blocks[x << 11 | z << 7 | y] = (byte)(tile & 0xFF);
        if (old != 0) {
            Tile.tiles[old].onRemove(this.level, xOffs, y, zOffs);
        }
        this.data.set(x, y, z, 0);
        if (Tile.lightBlock[tile & 0xFF] != 0) {
            if (y >= oldHeight) {
                this.recalcHeight(x, y + 1, z);
            }
        }
        else if (y == oldHeight - 1) {
            this.recalcHeight(x, y, z);
        }

        this.level.updateLight(LightLayer.Sky, xOffs, y, zOffs, xOffs, y, zOffs);
        this.level.updateLight(LightLayer.Block, xOffs, y, zOffs, xOffs, y, zOffs);
        this.lightGaps(x, z);
        if (_tile != 0 && !this.level.isClientSide) {
            Tile.tiles[_tile].onPlace(this.level, xOffs, y, zOffs);
        }
        return this.unsaved = true;
    }
    
    public int getData(final int x, final int y, final int z) {
        return this.data.get(x, y, z);
    }
    
    public void setData(final int x, final int y, final int z, final int val) {
        this.unsaved = true;
        this.data.set(x, y, z, val);
    }
    
    public int getBrightness(final LightLayer layer, final int x, final int y, final int z) {
        if (layer == LightLayer.Sky) return this.skyLight.get(x, y, z);
        if (layer == LightLayer.Block) return this.blockLight.get(x, y, z);
        return 0;
    }
    
    public void setBrightness(final LightLayer layer, final int x, final int y, final int z, final int brightness) {
        this.unsaved = true;
        if (layer == LightLayer.Sky) this.skyLight.set(x, y, z, brightness);
        else if (layer == LightLayer.Block) this.blockLight.set(x, y, z, brightness);
    }
    
    public int getRawBrightness(final int x, final int y, final int z, final int skyDampen) {
        int light = this.skyLight.get(x, y, z);
        if (light > 0) LevelChunk.touchedSky = true;
        light -= skyDampen;
        final int block = this.blockLight.get(x, y, z);
        if (block > light) light = block;

        // Useless - Java code comment from LCE codebase, seems to possibly be for some sort of dynamic light test?
//        int xd = (Mth.absFloor(level.player.x - (this.x * 16 +x)));
//        int yd = (Mth.absFloor(level.player.y - (y)));
//        int zd = (Mth.absFloor(level.player.z - (this.z * 16 +z)));
//        int dd = xd + yd + zd;
//        if (dd < 15) {
//            int carried = 15 - dd;
//            if (carried < 0) carried = 0;
//            if (carried > 15) carried = 15;
//            if (carried > light) light = carried;
//        }

        return light;
    }
    
    public void addEntity(final Entity e) {
        this.lastSaveHadEntities = true;

        final int xc = Mth.floor(e.x / 16.0);
        final int zc = Mth.floor(e.z / 16.0);
        if (xc != this.x || zc != this.z) {
            System.out.println("Wrong location! " + e);
            Thread.dumpStack();
        }
        int yc = Mth.floor(e.y / 16.0);
        if (yc < 0) yc = 0;
        if (yc >= this.entityBlocks.length) yc = this.entityBlocks.length - 1;
        e.inChunk = true;
        e.xChunk = this.x;
        e.yChunk = yc;
        e.zChunk = this.z;
        this.entityBlocks[yc].add(e);
    }
    
    public void removeEntity(final Entity e) {
        this.removeEntity(e, e.yChunk);
    }
    
    public void removeEntity(final Entity e, int yc) {
        if (yc < 0) yc = 0;
        if (yc >= this.entityBlocks.length) yc = this.entityBlocks.length - 1;
        this.entityBlocks[yc].remove(e);
    }
    
    public boolean isSkyLit(final int x, final int y, final int z) {
        return y >= (this.heightmap[z << 4 | x] & 0xFF);
    }

    // Useless - in b1.2 and LCE leaks
    public void skyBrightnessChanged() {
        int x0 = this.x * 16;
        int y0 = this.minHeight - 16;
        int z0 = this.z * 16;
        int x1 = this.x * 16 + 16;
        int y1 = Level.MAX_HEIGHT - 1;
        int z1 = this.z * 16 + 16;

        this.level.setTilesDirty(x0, y0, z0, x1, y1, z1);
    }
    
    public TileEntity getTileEntity(final int x, final int y, final int z) {
        final TilePos pos = new TilePos(x, y, z);

        TileEntity tileEntity = this.tileEntities.get(pos);
        if (tileEntity == null) {
            final int t = this.getTile(x, y, z);
            if (!Tile.isEntityTile[t]) return null;

            EntityTile et = (EntityTile) Tile.tiles[t];
            et.onPlace(this.level, this.x * 16 + x, y, this.z * 16 + z);

            tileEntity = this.tileEntities.get(pos);
        }
        if (tileEntity != null && tileEntity.isRemoved()) {
            this.tileEntities.remove(pos);
            return null;
        }
        return tileEntity;
    }
    
    public void addTileEntity(final TileEntity te) {
        int xx = te.x - this.x * 16;
        int yy = te.y;
        int zz = te.z - this.z * 16;
        this.setTileEntity(xx, yy, zz, te);
        if (this.loaded) {
            this.level.tileEntityList.add(te);
        }
    }
    
    public void setTileEntity(final int x, final int y, final int z, final TileEntity tileEntity) {
        final TilePos pos = new TilePos(x, y, z);

        tileEntity.level = this.level;
        tileEntity.x = this.x * 16 + x;
        tileEntity.y = y;
        tileEntity.z = this.z * 16 + z;

        if (this.getTile(x, y, z) == 0 || !(Tile.tiles[this.getTile(x, y, z)] instanceof EntityTile)) {
            System.out.println("Attempted to place a tile entity where there was no entity tile!");
            return;
        }

        tileEntity.clearRemoved();

        this.tileEntities.put(pos, tileEntity);
    }
    
    public void removeTileEntity(final int x, final int y, final int z) {
        final TilePos pos = new TilePos(x, y, z);

        if (this.loaded) {
            final TileEntity te = this.tileEntities.remove(pos);
            if (te != null) {
                te.setRemoved();
            }
        }
    }
    
    public void load() {
        this.loaded = true;
        this.level.addAllPendingTileEntities(this.tileEntities.values());
        for (int i = 0; i < this.entityBlocks.length; ++i) {
            this.level.addEntities(this.entityBlocks[i]);
        }
    }
    
    public void unload() {
        this.loaded = false;
        for (TileEntity tileEntity : this.tileEntities.values()) {
            tileEntity.setRemoved();
        }

        for (int i = 0; i < this.entityBlocks.length; ++i) {
            this.level.removeEntities(this.entityBlocks[i]);
        }
    }
    
    public void markUnsaved() {
        this.unsaved = true;
    }
    
    public void getEntities(final Entity except, final AABB bb, final List<Entity> es) {
        int yc0 = Mth.floor((bb.y0 - 2.0) / 16.0);
        int yc1 = Mth.floor((bb.y1 + 2.0) / 16.0);
        if (yc0 < 0) yc0 = 0;
        if (yc1 >= this.entityBlocks.length) yc1 = this.entityBlocks.length - 1;

        for (int yc = yc0; yc <= yc1; ++yc) {
            final List<Entity> entities = this.entityBlocks[yc];
            for (int i = 0; i < entities.size(); ++i) {
                final Entity e = entities.get(i);
                if (e != except && e.bb.intersects(bb)) {
                    es.add(e);
                }
            }
        }
    }
    
    public <T extends Entity>  void getEntitiesOfClass(final Class<T> ec, final AABB bb, final List<T> es) {
        int yc0 = Mth.floor((bb.y0 - 2.0) / 16.0);
        int yc1 = Mth.floor((bb.y1 + 2.0) / 16.0);

        if (yc0 < 0) yc0 = 0;
        if (yc1 >= this.entityBlocks.length) yc1 = this.entityBlocks.length - 1;

        for (int yc = yc0; yc <= yc1; ++yc) {
            final List<Entity> entities = this.entityBlocks[yc];
            for (int i = 0; i < entities.size(); ++i) {
                final Entity e = entities.get(i);
                if (ec.isAssignableFrom(e.getClass()) && e.bb.intersects(bb)) {
                    es.add((T) e);
                }
            }
        }
    }

    // Useless - In b1.2 and LCE leaks
    public int countEntities() {
        int entityCount = 0;

        for (int yc = 0; yc < this.entityBlocks.length; yc++) {
            entityCount += this.entityBlocks[yc].size();
        }

        return entityCount;
    }
    
    public boolean shouldSave(final boolean force) {
        if (this.dontSave) return false;

        if (force) {
            if (this.lastSaveHadEntities && this.level.getTime() != this.lastSaveTime) return true;
        }
        else {
            if (this.lastSaveHadEntities && this.level.getTime() >= this.lastSaveTime + SharedConstants.TICKS_PER_SECOND * 30) return true;
        }

        return this.unsaved;
    }
    
    public int getBlocksAndData(final byte[] data, final int x0, final int y0, final int z0, final int x1, final int y1, final int z1, int p) {
        for (int x = x0; x < x1; ++x) {
            for (int z = z0; z < z1; ++z) {
                final int slot = x << 11 | z << 7 | y0;
                final int len = y1 - y0;
                System.arraycopy(this.blocks, slot, data, p, len);
                p += len;
            }
        }

        this.recalcHeightmapOnly();

        for (int x = x0; x < x1; ++x) {
            for (int z = z0; z < z1; ++z) {
                final int slot = (x << 11 | z << 7 | y0) >> 1;
                final int len = (y1 - y0) / 2;
                System.arraycopy(this.data.data, slot, data, p, len);
                p += len;
            }
        }

        for (int x = x0; x < x1; ++x) {
            for (int z = z0; z < z1; ++z) {
                final int slot = (x << 11 | z << 7 | y0) >> 1;
                final int len = (y1 - y0) / 2;
                System.arraycopy(this.blockLight.data, slot, data, p, len);
                p += len;
            }
        }

        for (int x = x0; x < x1; ++x) {
            for (int z = z0; z < z1; ++z) {
                final int slot = (x << 11 | z << 7 | y0) >> 1;
                final int len = (y1 - y0) / 2;
                System.arraycopy(this.skyLight.data, slot, data, p, len);
                p += len;
            }
        }
        return p;
    }
    
    public int setBlocksAndData(byte[] data, int x0, int y0, int z0, int x1, int y1, int z1, int p) {
        for (int x = x0; x < x1; x++) {
            for (int z = z0; z < z1; z++) {
                int slot = x << 11 | z << 7 | y0;
                int len = y1 - y0;
                System.arraycopy(data, p, this.blocks, slot, len);
                p += len;
            }
        }

        this.recalcHeightmapOnly();

        for (int x = x0; x < x1; x++) {
            for (int z = z0; z < z1; z++) {
                int slot = (x << 11 | z << 7 | y0) >> 1;
                int len = (y1 - y0) / 2;
                System.arraycopy(data, p, this.data.data, slot, len);
                p += len;
            }
        }

        for (int x = x0; x < x1; x++) {
            for (int z = z0; z < z1; z++) {
                int slot = (x << 11 | z << 7 | y0) >> 1;
                int len = (y1 - y0) / 2;
                System.arraycopy(data, p, this.blockLight.data, slot, len);
                p += len;
            }
        }

        for (int x = x0; x < x1; x++) {
            for (int z = z0; z < z1; z++) {
                int slot = (x << 11 | z << 7 | y0) >> 1;
                int len = (y1 - y0) / 2;
                System.arraycopy(data, p, this.skyLight.data, slot, len);
                p += len;
            }
        }

        return p;
    }
    
    public Random getRandom(final long l) {
        return new Random(this.level.getSeed() + this.x * this.x * 4987142 + this.x * 5947611 + this.z * this.z * 4392871L + this.z * 389711 ^ l);
    }
    
    public boolean isEmpty() {
        return false;
    }
    
    public void attemptCompression() {
        BlockReplacements.replace(this.blocks);
    }
}
