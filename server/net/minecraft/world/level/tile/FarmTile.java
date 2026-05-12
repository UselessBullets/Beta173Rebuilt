// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.entity.Entity;
import java.util.Random;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class FarmTile extends Tile
{
    protected FarmTile(final int integer) {
        super(integer, Material.dirt);
        this.tex = 87;
        this.setTicking(true);
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 0.9375f, 1.0f);
        this.setLightBlock(255);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        return AABB.newTemp(x + 0, y + 0, z + 0, x + 1, y + 1, z + 1);
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
    public int getTexture(final int face, final int data) {
        if (face == 1 && data > 0) {
            return this.tex - 1;
        }
        if (face == 1) {
            return this.tex;
        }
        return 2;
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (random.nextInt(5) == 0) {
            if (this.isNearWater(level, x, y, z) || level.isRainingAt(x, y + 1, z)) {
                level.setData(x, y, z, 7);
            }
            else {
                final int data = level.getData(x, y, z);
                if (data > 0) {
                    level.setData(x, y, z, data - 1);
                }
                else if (!this.isUnderCrops(level, x, y, z)) {
                    level.setTile(x, y, z, Tile.dirt.id);
                }
            }
        }
    }
    
    @Override
    public void stepOn(final Level level, final int x, final int y, final int z, final Entity entity) {
        if (level.random.nextInt(4) == 0) {
            level.setTile(x, y, z, Tile.dirt.id);
        }
    }
    
    private boolean isUnderCrops(final Level level, final int x, final int y, final int z) {
        for (int n = 0, i = x - n; i <= x + n; ++i) {
            for (int j = z - n; j <= z + n; ++j) {
                if (level.getTile(i, y + 1, j) == Tile.crops.id) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isNearWater(final Level level, final int x, final int y, final int z) {
        for (int i = x - 4; i <= x + 4; ++i) {
            for (int j = y; j <= y + 1; ++j) {
                for (int k = z - 4; k <= z + 4; ++k) {
                    if (level.getMaterial(i, j, k) == Material.water) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        super.neighborChanged(level, x, y, z, type);
        if (level.getMaterial(x, y + 1, z).isSolid()) {
            level.setTile(x, y, z, Tile.dirt.id);
        }
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Tile.dirt.getResource(0, random);
    }
}
