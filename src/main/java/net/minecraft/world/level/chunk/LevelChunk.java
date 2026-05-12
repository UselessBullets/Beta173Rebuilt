// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.chunk;

import java.util.Random;
import net.minecraft.world.phys.AABB;
import java.util.Iterator;

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
    public Map<TilePos, TileEntity> tileEntities;
    public List<Entity>[] entityBlocks;
    public boolean terrainPopulated;
    public boolean unsaved;
    public boolean dontSave;
    public boolean lastSaveHadEntities;
    public long lastSaveTime;
    
    public LevelChunk(final Level level, final int x, final int z) {
        this.tileEntities = new HashMap<>();
        this.entityBlocks = new List[8];
        this.terrainPopulated = false;
        this.unsaved = false;
        this.lastSaveHadEntities = false;
        this.lastSaveTime = 0L;
        this.level = level;
        this.x = x;
        this.z = z;
        this.heightmap = new byte[256];
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
        int minHeight = 127;
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                int n = 127;
                for (int n2 = i << 11 | j << 7; n > 0 && Tile.lightBlock[this.blocks[n2 + n - 1] & 0xFF] == 0; --n) {}
                this.heightmap[j << 4 | i] = (byte)n;
                if (n < minHeight) {
                    minHeight = n;
                }
            }
        }
        this.minHeight = minHeight;
        this.unsaved = true;
    }
    
    public void recalcHeightmap() {
        int minHeight = 127;
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                int n;
                int n2;
                for (n = 127, n2 = (i << 11 | j << 7); n > 0 && Tile.lightBlock[this.blocks[n2 + n - 1] & 0xFF] == 0; --n) {}
                this.heightmap[j << 4 | i] = (byte)n;
                if (n < minHeight) {
                    minHeight = n;
                }
                if (!this.level.dimension.hasCeiling) {
                    int val = 15;
                    int y = 127;
                    do {
                        val -= Tile.lightBlock[this.blocks[n2 + y] & 0xFF];
                        if (val > 0) {
                            this.skyLight.set(i, y, j, val);
                        }
                    } while (--y > 0 && val > 0);
                }
            }
        }
        this.minHeight = minHeight;
        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                this.lightGaps(k, l);
            }
        }
        this.unsaved = true;
    }
    
    public void lightLava() {
    }
    
    private void lightGaps(final int x, final int z) {
        final int heightmap = this.getHeightmap(x, z);
        final int n = this.x * 16 + x;
        final int n2 = this.z * 16 + z;
        this.lightGap(n - 1, n2, heightmap);
        this.lightGap(n + 1, n2, heightmap);
        this.lightGap(n, n2 - 1, heightmap);
        this.lightGap(n, n2 + 1, heightmap);
    }
    
    private void lightGap(final int x, final int z, final int source) {
        final int heightmap = this.level.getHeightmap(x, z);
        if (heightmap > source) {
            this.level.updateLight(LightLayer.Sky, x, source, z, x, heightmap, z);
            this.unsaved = true;
        }
        else if (heightmap < source) {
            this.level.updateLight(LightLayer.Sky, x, heightmap, z, x, source, z);
            this.unsaved = true;
        }
    }
    
    private void recalcHeight(final int x, final int yStart, final int z) {
        int n2;
        final int n = n2 = (this.heightmap[z << 4 | x] & 0xFF);
        if (yStart > n) {
            n2 = yStart;
        }
        for (int n3 = x << 11 | z << 7; n2 > 0 && Tile.lightBlock[this.blocks[n3 + n2 - 1] & 0xFF] == 0; --n2) {}
        if (n2 == n) {
            return;
        }
        this.level.lightColumnChanged(x, z, n2, n);
        this.heightmap[z << 4 | x] = (byte)n2;
        if (n2 < this.minHeight) {
            this.minHeight = n2;
        }
        else {
            int minHeight = 127;
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if ((this.heightmap[j << 4 | i] & 0xFF) < minHeight) {
                        minHeight = (this.heightmap[j << 4 | i] & 0xFF);
                    }
                }
            }
            this.minHeight = minHeight;
        }
        final int n4 = this.x * 16 + x;
        final int n5 = this.z * 16 + z;
        if (n2 < n) {
            for (int k = n2; k < n; ++k) {
                this.skyLight.set(x, k, z, 15);
            }
        }
        else {
            this.level.updateLight(LightLayer.Sky, n4, n, n5, n4, n2, n5);
            for (int l = n; l < n2; ++l) {
                this.skyLight.set(x, l, z, 0);
            }
        }
        int val = 15;
        final int y1 = n2;
        while (n2 > 0 && val > 0) {
            --n2;
            int n6 = Tile.lightBlock[this.getTile(x, n2, z)];
            if (n6 == 0) {
                n6 = 1;
            }
            val -= n6;
            if (val < 0) {
                val = 0;
            }
            this.skyLight.set(x, n2, z, val);
        }
        while (n2 > 0 && Tile.lightBlock[this.getTile(x, n2 - 1, z)] == 0) {
            --n2;
        }
        if (n2 != y1) {
            this.level.updateLight(LightLayer.Sky, n4 - 1, n2, n5 - 1, n4 + 1, y1, n5 + 1);
        }
        this.unsaved = true;
    }
    
    public int getTile(final int x, final int y, final int z) {
        return this.blocks[x << 11 | z << 7 | y] & 0xFF;
    }
    
    public boolean setTileAndData(final int x, final int y, final int z, final int tile, final int data) {
        final byte b = (byte)tile;
        final int n = this.heightmap[z << 4 | x] & 0xFF;
        final int n2 = this.blocks[x << 11 | z << 7 | y] & 0xFF;
        if (n2 == tile && this.data.get(x, y, z) == data) {
            return false;
        }
        final int x2 = this.x * 16 + x;
        final int z2 = this.z * 16 + z;
        this.blocks[x << 11 | z << 7 | y] = (byte)(b & 0xFF);
        if (n2 != 0 && !this.level.isClientSide) {
            Tile.tiles[n2].onRemove(this.level, x2, y, z2);
        }
        this.data.set(x, y, z, data);
        if (!this.level.dimension.hasCeiling) {
            if (Tile.lightBlock[b & 0xFF] != 0) {
                if (y >= n) {
                    this.recalcHeight(x, y + 1, z);
                }
            }
            else if (y == n - 1) {
                this.recalcHeight(x, y, z);
            }
            this.level.updateLight(LightLayer.Sky, x2, y, z2, x2, y, z2);
        }
        this.level.updateLight(LightLayer.Block, x2, y, z2, x2, y, z2);
        this.lightGaps(x, z);
        this.data.set(x, y, z, data);
        if (tile != 0) {
            Tile.tiles[tile].onPlace(this.level, x2, y, z2);
        }
        return this.unsaved = true;
    }
    
    public boolean setTile(final int x, final int y, final int z, final int tile) {
        final byte b = (byte)tile;
        final int n = this.heightmap[z << 4 | x] & 0xFF;
        final int n2 = this.blocks[x << 11 | z << 7 | y] & 0xFF;
        if (n2 == tile) {
            return false;
        }
        final int x2 = this.x * 16 + x;
        final int z2 = this.z * 16 + z;
        this.blocks[x << 11 | z << 7 | y] = (byte)(b & 0xFF);
        if (n2 != 0) {
            Tile.tiles[n2].onRemove(this.level, x2, y, z2);
        }
        this.data.set(x, y, z, 0);
        if (Tile.lightBlock[b & 0xFF] != 0) {
            if (y >= n) {
                this.recalcHeight(x, y + 1, z);
            }
        }
        else if (y == n - 1) {
            this.recalcHeight(x, y, z);
        }
        this.level.updateLight(LightLayer.Sky, x2, y, z2, x2, y, z2);
        this.level.updateLight(LightLayer.Block, x2, y, z2, x2, y, z2);
        this.lightGaps(x, z);
        if (tile != 0 && !this.level.isClientSide) {
            Tile.tiles[tile].onPlace(this.level, x2, y, z2);
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
        if (layer == LightLayer.Sky) {
            return this.skyLight.get(x, y, z);
        }
        if (layer == LightLayer.Block) {
            return this.blockLight.get(x, y, z);
        }
        return 0;
    }
    
    public void setBrightness(final LightLayer layer, final int x, final int y, final int z, final int brightness) {
        this.unsaved = true;
        if (layer == LightLayer.Sky) {
            this.skyLight.set(x, y, z, brightness);
        }
        else {
            if (layer != LightLayer.Block) {
                return;
            }
            this.blockLight.set(x, y, z, brightness);
        }
    }
    
    public int getRawBrightness(final int x, final int y, final int z, final int skyDampen) {
        final int value = this.skyLight.get(x, y, z);
        if (value > 0) {
            LevelChunk.touchedSky = true;
        }
        int n = value - skyDampen;
        final int value2 = this.blockLight.get(x, y, z);
        if (value2 > n) {
            n = value2;
        }
        return n;
    }
    
    public void addEntity(final Entity e) {
        this.lastSaveHadEntities = true;
        final int floor = Mth.floor(e.x / 16.0);
        final int floor2 = Mth.floor(e.z / 16.0);
        if (floor != this.x || floor2 != this.z) {
            System.out.println("Wrong location! " + e);
            Thread.dumpStack();
        }
        int floor3 = Mth.floor(e.y / 16.0);
        if (floor3 < 0) {
            floor3 = 0;
        }
        if (floor3 >= this.entityBlocks.length) {
            floor3 = this.entityBlocks.length - 1;
        }
        e.inChunk = true;
        e.xChunk = this.x;
        e.yChunk = floor3;
        e.zChunk = this.z;
        this.entityBlocks[floor3].add(e);
    }
    
    public void removeEntity(final Entity e) {
        this.removeEntity(e, e.yChunk);
    }
    
    public void removeEntity(final Entity e, int yc) {
        if (yc < 0) {
            yc = 0;
        }
        if (yc >= this.entityBlocks.length) {
            yc = this.entityBlocks.length - 1;
        }
        this.entityBlocks[yc].remove(e);
    }
    
    public boolean isSkyLit(final int x, final int y, final int z) {
        return y >= (this.heightmap[z << 4 | x] & 0xFF);
    }
    
    public TileEntity getTileEntity(final int x, final int y, final int z) {
        final TilePos tilePos = new TilePos(x, y, z);
        TileEntity tileEntity = this.tileEntities.get(tilePos);
        if (tileEntity == null) {
            final int tile = this.getTile(x, y, z);
            if (!Tile.isEntityTile[tile]) {
                return null;
            }
            ((EntityTile)Tile.tiles[tile]).onPlace(this.level, this.x * 16 + x, y, this.z * 16 + z);
            tileEntity = this.tileEntities.get(tilePos);
        }
        if (tileEntity != null && tileEntity.isRemoved()) {
            this.tileEntities.remove(tilePos);
            return null;
        }
        return tileEntity;
    }
    
    public void addTileEntity(final TileEntity te) {
        this.setTileEntity(te.x - this.x * 16, te.y, te.z - this.z * 16, te);
        if (this.loaded) {
            this.level.tileEntityList.add(te);
        }
    }
    
    public void setTileEntity(final int x, final int y, final int z, final TileEntity tileEntity) {
        final TilePos tilePos = new TilePos(x, y, z);
        tileEntity.level = this.level;
        tileEntity.x = this.x * 16 + x;
        tileEntity.y = y;
        tileEntity.z = this.z * 16 + z;
        if (this.getTile(x, y, z) == 0 || !(Tile.tiles[this.getTile(x, y, z)] instanceof EntityTile)) {
            System.out.println("Attempted to place a tile entity where there was no entity tile!");
            return;
        }
        tileEntity.clearRemoved();
        this.tileEntities.put(tilePos, tileEntity);
    }
    
    public void removeTileEntity(final int x, final int y, final int z) {
        final TilePos tilePos = new TilePos(x, y, z);
        if (this.loaded) {
            final TileEntity tileEntity = this.tileEntities.remove(tilePos);
            if (tileEntity != null) {
                tileEntity.setRemoved();
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
        final Iterator iterator = this.tileEntities.values().iterator();
        while (iterator.hasNext()) {
            ((TileEntity)iterator.next()).setRemoved();
        }
        for (int i = 0; i < this.entityBlocks.length; ++i) {
            this.level.removeEntities(this.entityBlocks[i]);
        }
    }
    
    public void markUnsaved() {
        this.unsaved = true;
    }
    
    public void getEntities(final Entity except, final AABB bb, final List<Entity> es) {
        int floor = Mth.floor((bb.y0 - 2.0) / 16.0);
        int floor2 = Mth.floor((bb.y1 + 2.0) / 16.0);
        if (floor < 0) {
            floor = 0;
        }
        if (floor2 >= this.entityBlocks.length) {
            floor2 = this.entityBlocks.length - 1;
        }
        for (int i = floor; i <= floor2; ++i) {
            final List<Entity> list = this.entityBlocks[i];
            for (int j = 0; j < list.size(); ++j) {
                final Entity entity = list.get(j);
                if (entity != except && entity.bb.intersects(bb)) {
                    es.add(entity);
                }
            }
        }
    }
    
    public <T extends Entity>  void getEntitiesOfClass(final Class<T> ec, final AABB bb, final List<Entity> es) {
        int floor = Mth.floor((bb.y0 - 2.0) / 16.0);
        int floor2 = Mth.floor((bb.y1 + 2.0) / 16.0);
        if (floor < 0) {
            floor = 0;
        }
        if (floor2 >= this.entityBlocks.length) {
            floor2 = this.entityBlocks.length - 1;
        }
        for (int i = floor; i <= floor2; ++i) {
            final List<Entity> list = this.entityBlocks[i];
            for (int j = 0; j < list.size(); ++j) {
                final Entity entity = list.get(j);
                if (ec.isAssignableFrom(entity.getClass()) && entity.bb.intersects(bb)) {
                    es.add(entity);
                }
            }
        }
    }
    
    public boolean shouldSave(final boolean force) {
        if (this.dontSave) {
            return false;
        }
        if (force) {
            if (this.lastSaveHadEntities && this.level.getTime() != this.lastSaveTime) {
                return true;
            }
        }
        else if (this.lastSaveHadEntities && this.level.getTime() >= this.lastSaveTime + 600L) {
            return true;
        }
        return this.unsaved;
    }
    
    public int getBlocksAndData(final byte[] data, final int x0, final int y0, final int z0, final int x1, final int y1, final int z1, int p) {
        for (int i = x0; i < x1; ++i) {
            for (int j = z0; j < z1; ++j) {
                final int n = i << 11 | j << 7 | y0;
                final int n2 = y1 - y0;
                System.arraycopy(data, p, this.blocks, n, n2);
                p += n2;
            }
        }
        this.recalcHeightmapOnly();
        for (int k = x0; k < x1; ++k) {
            for (int l = z0; l < z1; ++l) {
                final int n3 = (k << 11 | l << 7 | y0) >> 1;
                final int n4 = (y1 - y0) / 2;
                System.arraycopy(data, p, this.data.data, n3, n4);
                p += n4;
            }
        }
        for (int n5 = x0; n5 < x1; ++n5) {
            for (int n6 = z0; n6 < z1; ++n6) {
                final int n7 = (n5 << 11 | n6 << 7 | y0) >> 1;
                final int n8 = (y1 - y0) / 2;
                System.arraycopy(data, p, this.blockLight.data, n7, n8);
                p += n8;
            }
        }
        for (int n9 = x0; n9 < x1; ++n9) {
            for (int n10 = z0; n10 < z1; ++n10) {
                final int n11 = (n9 << 11 | n10 << 7 | y0) >> 1;
                final int n12 = (y1 - y0) / 2;
                System.arraycopy(data, p, this.skyLight.data, n11, n12);
                p += n12;
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
