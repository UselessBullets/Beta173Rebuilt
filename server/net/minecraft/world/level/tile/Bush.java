// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.phys.AABB;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class Bush extends Tile
{
    protected Bush(final int id, final int tex) {
        super(id, Material.replaceable_plant);
        this.tex = tex;
        this.setTicking(true);
        final float n = 0.2f;
        this.setShape(0.5f - n, 0.0f, 0.5f - n, 0.5f + n, n * 3.0f, 0.5f + n);
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return super.mayPlace(level, x, y, z) && this.mayPlaceOn(level.getTile(x, y - 1, z));
    }
    
    protected boolean mayPlaceOn(final int tile) {
        return tile == Tile.grass.id || tile == Tile.dirt.id || tile == Tile.farmland.id;
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        super.neighborChanged(level, x, y, z, type);
        this.checkAlive(level, x, y, z);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        this.checkAlive(level, x, y, z);
    }
    
    protected final void checkAlive(final Level level, final int x, final int y, final int z) {
        if (!this.canSurvive(level, x, y, z)) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        }
    }
    
    @Override
    public boolean canSurvive(final Level level, final int x, final int y, final int z) {
        return (level.getDaytimeRawBrightness(x, y, z) >= 8 || level.canSeeSky(x, y, z)) && this.mayPlaceOn(level.getTile(x, y - 1, z));
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        return null;
    }
    
    @Override
    public boolean isSolidRender() {
        return false;
    }
    
    @Override
    public boolean isCubeShaped() {
        return false;
    }
}
