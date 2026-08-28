// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.Facing;
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
    public static final int MAX_HEIGHT = 6;
    public static final int HEIGHT_MASK = 7; // max 8 steps
    protected TopSnowTile(final int id, final int tex) {
        super(id, tex, Material.topSnow);
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
        this.setTicking(true);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        int height = level.getData(x, y, z) & HEIGHT_MASK;
        if (height >= (MAX_HEIGHT / 2)) {
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
        int data = level.getData(x, y, z);
        int height = data & HEIGHT_MASK;
        float o = 2 * (1 + height) / 16.0f;
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, o, 1.0f);
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        final int t = level.getTile(x, y - 1, z);
        if (t == 0 || !Tile.tiles[t].isSolidRender()) return false;
        return level.getMaterial(x, y - 1, z).blocksMotion();
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
        final int type = Item.snowBall.id;
        final float s = 0.7f;
        double xo = level.random.nextFloat() * s + (1.0f - s) * 0.5;
        double yo = level.random.nextFloat() * s + (1.0f - s) * 0.5;
        double zo = level.random.nextFloat() * s + (1.0f - s) * 0.5;
        final ItemEntity item = new ItemEntity(level, x + xo, y + yo, z + zo, new ItemInstance(type, 1, 0));
        item.throwTime = 10;
        level.addEntity(item);
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
        if (f == Facing.UP) return true;
        return super.shouldRenderFace(level, x, y, z, f);
    }
}
