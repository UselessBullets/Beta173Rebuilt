// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import java.util.Random;
import net.minecraft.world.level.Level;

public class CropTile extends Bush
{
    protected CropTile(final int id, final int tex) {
        super(id, tex);
        this.tex = tex;
        this.setTicking(true);
        final float n = 0.5f;
        this.setShape(0.5f - n, 0.0f, 0.5f - n, 0.5f + n, 0.25f, 0.5f + n);
    }
    
    @Override
    protected boolean mayPlaceOn(final int tile) {
        return tile == Tile.farmland.id;
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        super.tick(level, x, y, z, random);
        if (level.getRawBrightness(x, y + 1, z) >= 9) {
            int data = level.getData(x, y, z);
            if (data < 7 && random.nextInt((int)(100.0f / this.getGrowthSpeed(level, x, y, z))) == 0) {
                ++data;
                level.setData(x, y, z, data);
            }
        }
    }
    
    public void growCropsToMax(final Level level, final int x, final int y, final int z) {
        level.setData(x, y, z, 7);
    }
    
    private float getGrowthSpeed(final Level level, final int x, final int y, final int z) {
        float n = 1.0f;
        final int tile = level.getTile(x, y, z - 1);
        final int tile2 = level.getTile(x, y, z + 1);
        final int tile3 = level.getTile(x - 1, y, z);
        final int tile4 = level.getTile(x + 1, y, z);
        final int tile5 = level.getTile(x - 1, y, z - 1);
        final int tile6 = level.getTile(x + 1, y, z - 1);
        final int tile7 = level.getTile(x + 1, y, z + 1);
        final int tile8 = level.getTile(x - 1, y, z + 1);
        final boolean b = tile3 == this.id || tile4 == this.id;
        final boolean b2 = tile == this.id || tile2 == this.id;
        final boolean b3 = tile5 == this.id || tile6 == this.id || tile7 == this.id || tile8 == this.id;
        for (int i = x - 1; i <= x + 1; ++i) {
            for (int j = z - 1; j <= z + 1; ++j) {
                final int tile9 = level.getTile(i, y - 1, j);
                float n2 = 0.0f;
                if (tile9 == Tile.farmland.id) {
                    n2 = 1.0f;
                    if (level.getData(i, y - 1, j) > 0) {
                        n2 = 3.0f;
                    }
                }
                if (i != x || j != z) {
                    n2 /= 4.0f;
                }
                n += n2;
            }
        }
        if (b3 || (b && b2)) {
            n /= 2.0f;
        }
        return n;
    }
    
    @Override
    public int getTexture(final int face, int data) {
        if (data < 0) {
            data = 7;
        }
        return this.tex + data;
    }
    
    @Override
    public int getRenderShape() {
        return 6;
    }
    
    @Override
    public void spawnResources(final Level level, final int x, final int y, final int z, final int data, final float odds) {
        super.spawnResources(level, x, y, z, data, odds);
        if (level.isClientSide) {
            return;
        }
        for (int i = 0; i < 3; ++i) {
            if (level.random.nextInt(15) <= data) {
                final float n = 0.7f;
                final ItemEntity e = new ItemEntity(level, x + (level.random.nextFloat() * n + (1.0f - n) * 0.5f), y + (level.random.nextFloat() * n + (1.0f - n) * 0.5f), z + (level.random.nextFloat() * n + (1.0f - n) * 0.5f), new ItemInstance(Item.seeds));
                e.throwTime = 10;
                level.addEntity(e);
            }
        }
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        if (data == 7) {
            return Item.wheat.id;
        }
        return -1;
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 1;
    }
}
