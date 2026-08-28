// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.level.LightLayer;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;

public class IceTile extends HalfTransparentTile
{
    public IceTile(final int id, final int tex) {
        super(id, tex, Material.ice, false);
        this.friction = 0.98f;
        this.setTicking(true);
    }
    
    @Override
    public int getRenderLayer() {
        return 1;
    }
    
    @Override
    public boolean shouldRenderFace(final LevelSource level, final int x, final int y, final int z, final int face) {
        return super.shouldRenderFace(level, x, y, z, 1 - face);
    }
    
    @Override
    public void playerDestroy(final Level level, final Player player, final int x, final int y, final int z, final int data) {
        super.playerDestroy(level, player, x, y, z, data);
        final Material below = level.getMaterial(x, y - 1, z);
        if (below.blocksMotion() || below.isLiquid()) {
            level.setTile(x, y, z, Tile.water.id);
        }
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 0;
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (level.getBrightness(LightLayer.Block, x, y, z) > 11 - Tile.lightBlock[this.id]) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, Tile.calmWater.id);
        }
    }
    
    @Override
    public int getPistonPushReaction() {
        return Material.PUSH_NORMAL;
    }
}
