// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.level.LightLayer;
import java.util.Random;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class TopSnowTile extends Tile
{
    protected TopSnowTile(final int id, final int tex) {
        super(id, tex, Material.topSnow);
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
        this.setTicking(true);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        if ((level.getData(x, y, z) & 0x7) >= 3) {
            return AABB.newTemp(x + this.xx0, y + this.yy0, z + this.zz0, x + this.xx1, y + 0.5f, z + this.zz1);
        }
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
    
    @Override
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 2 * (1 + (level.getData(x, y, z) & 0x7)) / 16.0f, 1.0f);
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        final int tile = level.getTile(x, y - 1, z);
        return tile != 0 && Tile.tiles[tile].isSolidRender() && level.getMaterial(x, y - 1, z).blocksMotion();
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        this.checkCanSurvive(level, x, y, z);
    }
    
    private boolean checkCanSurvive(final Level level, final int x, final int y, final int z) {
        if (!this.mayPlace(level, x, y, z)) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
            return false;
        }
        return true;
    }
    
    @Override
    public void playerDestroy(final Level level, final Player player, final int x, final int y, final int z, final int data) {
        final int id = Item.snowBall.id;
        final float n = 0.7f;
        final ItemEntity e = new ItemEntity(level, x + (level.random.nextFloat() * n + (1.0f - n) * 0.5), y + (level.random.nextFloat() * n + (1.0f - n) * 0.5), z + (level.random.nextFloat() * n + (1.0f - n) * 0.5), new ItemInstance(id, 1, 0));
        e.throwTime = 10;
        level.addEntity(e);
        level.setTile(x, y, z, 0);
        player.awardStat(Stats.blockMined[this.id], 1);
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Item.snowBall.id;
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 0;
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (level.getBrightness(LightLayer.Block, x, y, z) > 11) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        }
    }
    
    @Override
    public boolean shouldRenderFace(final LevelSource level, final int x, final int y, final int z, final int f) {
        return f == 1 || super.shouldRenderFace(level, x, y, z, f);
    }
}
