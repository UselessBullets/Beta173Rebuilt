// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;
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
        final int data = level.getData(x, y, z);
        final float z2 = 0.0625f;
        this.setShape((1 + data * 2) / 16.0f, 0.0f, z2, 1.0f - z2, 0.5f, 1.0f - z2);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        final int data = level.getData(x, y, z);
        final float n = 0.0625f;
        return AABB.newTemp(x + (1 + data * 2) / 16.0f, y, z + n, x + 1 - n, y + 0.5f - n, z + 1 - n);
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if (face == 1) {
            return this.tex;
        }
        if (face == 0) {
            return this.tex + 3;
        }
        if (data > 0 && face == 4) {
            return this.tex + 2;
        }
        return this.tex + 1;
    }
    
    @Override
    public int getTexture(final int face) {
        if (face == 1) {
            return this.tex;
        }
        if (face == 0) {
            return this.tex + 3;
        }
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
        if (player.health < 20) {
            player.heal(3);
            final int data = level.getData(x, y, z) + 1;
            if (data >= 6) {
                level.setTile(x, y, z, 0);
            }
            else {
                level.setData(x, y, z, data);
                level.setTileDirty(x, y, z);
            }
        }
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return super.mayPlace(level, x, y, z) && this.canSurvive(level, x, y, z);
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
