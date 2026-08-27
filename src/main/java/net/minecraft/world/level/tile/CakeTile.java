// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.Facing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;

public class CakeTile extends Tile
{
    protected CakeTile(final int id, final int tex) {
        super(id, tex, Material.cake);
        this.setTicking(true);
    }
    
    @Override
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
        final int d = level.getData(x, y, z);
        float r = 1 / 16.0f;
        float r2 = (1 + d * 2) / 16.0f;
        float h = 8 / 16.0f;
        this.setShape(r2, 0.0f, r, 1.0f - r, h, 1.0f - r);
    }
    
    @Override
    public void updateDefaultShape() {
        float r = 1 / 16.0f;
        float h = 0.5f;
        this.setShape(r, 0.0f, r, 1.0f - r, h, 1.0f - r);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        final int d = level.getData(x, y, z);
        float r = 1 / 16.0f;
        float r2 = (1 + d * 2) / 16.0f;
        float h = 8 / 16.0f;
        return AABB.newTemp(x + r2, y, z + r, x + 1 - r, y + h - r, z + 1 - r);
    }
    
    @Override
    public AABB getTileAABB(final Level level, final int x, final int y, final int z) {
        final int d = level.getData(x, y, z);
        float r = 0.0625f;
        float r2 = (1 + d * 2) / 16.0f;
        float h = 8 / 16.0f;
        return AABB.newTemp(x + r2, y, z + r, x + 1 - r, y + h, z + 1 - r);
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if (face == Facing.UP) return this.tex;
        if (face == Facing.DOWN) return this.tex + 3;
        if (data > 0 && face == Facing.WEST) return this.tex + 2;
        return this.tex + 1;
    }
    
    @Override
    public int getTexture(final int face) {
        if (face == Facing.UP) return this.tex;
        if (face == Facing.DOWN) return this.tex + 3;
        return this.tex + 1;
    }
    
    @Override
    public boolean isCubeShaped() {
        return false;
    }
    
    @Override
    public boolean isSolidRender() {
        return false;
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        this.eat(level, x, y, z, player);
        return true;
    }
    
    @Override
    public void attack(final Level level, final int x, final int y, final int z, final Player player) {
        this.eat(level, x, y, z, player);
    }
    
    private void eat(final Level level, final int x, final int y, final int z, final Player player) {
        if (player.health < Player.MAX_HEALTH) {
            player.heal(3);

            final int d = level.getData(x, y, z) + 1;
            if (d >= 6) {
                level.setTile(x, y, z, 0);
            }
            else {
                level.setData(x, y, z, d);
                level.setTileDirty(x, y, z);
            }
        }
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        if (!super.mayPlace(level, x, y, z)) return false;

        return this.canSurvive(level, x, y, z);
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (!this.canSurvive(level, x, y, z)) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        }
    }
    
    @Override
    public boolean canSurvive(final Level level, final int x, final int y, final int z) {
        return level.getMaterial(x, y - 1, z).isSolid();
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 0;
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return 0;
    }
}
