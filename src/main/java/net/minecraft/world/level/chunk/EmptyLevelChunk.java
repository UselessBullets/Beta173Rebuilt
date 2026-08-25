// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.chunk;

import java.util.Arrays;
import java.util.Random;
import java.util.List;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;

public class EmptyLevelChunk extends LevelChunk
{
    public EmptyLevelChunk(final Level level, final int x, final int z) {
        super(level, x, z);
        this.dontSave = true;
    }
    
    public EmptyLevelChunk(final Level level, final byte[] blocks, final int x, final int z) {
        super(level, blocks, x, z);
        this.dontSave = true;
    }
    
    @Override
    public boolean isAt(final int x, final int z) {
        return x == this.x && z == this.z;
    }
    
    @Override
    public int getHeightmap(final int x, final int z) {
        return 0;
    }
    
    @Override
    public void recalcBlocksLights() {
    }
    
    @Override
    public void recalcHeightmapOnly() {
    }
    
    @Override
    public void recalcHeightmap() {
    }
    
    @Override
    public void lightLava() {
    }
    
    @Override
    public int getTile(final int x, final int y, final int z) {
        return 0;
    }
    
    @Override
    public boolean setTileAndData(final int x, final int y, final int z, final int tile, final int data) {
        return true;
    }
    
    @Override
    public boolean setTile(final int x, final int y, final int z, final int tile) {
        return true;
    }
    
    @Override
    public int getData(final int x, final int y, final int z) {
        return 0;
    }
    
    @Override
    public void setData(final int x, final int y, final int z, final int val) {
    }
    
    @Override
    public int getBrightness(final LightLayer layer, final int x, final int y, final int z) {
        return 0;
    }
    
    @Override
    public void setBrightness(final LightLayer layer, final int x, final int y, final int z, final int brightness) {
    }
    
    @Override
    public int getRawBrightness(final int x, final int y, final int z, final int skyDampen) {
        return 0;
    }
    
    @Override
    public void addEntity(final Entity e) {
    }
    
    @Override
    public void removeEntity(final Entity e) {
    }
    
    @Override
    public void removeEntity(final Entity e, final int yc) {
    }
    
    @Override
    public boolean isSkyLit(final int x, final int y, final int z) {
        return false;
    }

    @Override
    public void skyBrightnessChanged() {
    }

    @Override
    public TileEntity getTileEntity(final int x, final int y, final int z) {
        return null;
    }
    
    @Override
    public void addTileEntity(final TileEntity te) {
    }
    
    @Override
    public void setTileEntity(final int x, final int y, final int z, final TileEntity tileEntity) {
    }
    
    @Override
    public void removeTileEntity(final int x, final int y, final int z) {
    }
    
    @Override
    public void load() {
    }
    
    @Override
    public void unload() {
    }
    
    @Override
    public void markUnsaved() {
    }

    @Override
    public void getEntities(Entity except, AABB bb, List<Entity> es) {
    }

    @Override
    public <T extends Entity> void getEntitiesOfClass(Class<T> ec, AABB bb, List<Entity> es) {
    }

    @Override
    // Useless - in b1.2 and LCE leaks
    public int countEntities() {
        return 0;
    }
    
    @Override
    public boolean shouldSave(final boolean force) {
        return false;
    }
    
    @Override
    public int getBlocksAndData(final byte[] data, final int x0, final int y0, final int z0, final int x1, final int y1, final int z1, final int p) {
        int xs = x1 - x0;
        int ys = y1 - y0;
        int zs = z1 - z0;

        final int s = xs * ys * zs;
        final int len = s + s / 2 * 3;
        Arrays.fill(data, p, p + len, (byte) 0);
        return len;
    }

    @Override
    // Useless - in B1.2 and LCE leaks
    public int setBlocksAndData(byte[] data, int x0, int y0, int z0, int x1, int y1, int z1, int p) {
        int xs = x1 - x0;
        int ys = y1 - y0;
        int zs = z1 - z0;

        int s = xs * ys * zs;
        return s + s / 2 * 3;
    }
    
    @Override
    public Random getRandom(final long l) {
        return new Random(this.level.getSeed() + this.x * this.x * 4987142 + this.x * 5947611 + this.z * this.z * 4392871L + this.z * 389711 ^ l);
    }
    
    @Override
    public boolean isEmpty() {
        return true;
    }
}
