// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

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
        final float ss = 0.5f;
        this.setShape(0.5f - ss, 0.0f, 0.5f - ss, 0.5f + ss, 0.25f, 0.5f + ss);
    }
    
    @Override
    protected boolean mayPlaceOn(final int tile) {
        return tile == Tile.farmland.id;
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        super.tick(level, x, y, z, random);
        if (level.getRawBrightness(x, y + 1, z) >= 9) {
            int age = level.getData(x, y, z);
            if (age < 7) {
                float growthSpeed = this.getGrowthSpeed(level, x, y, z);

                if (random.nextInt((int) (100.0f / growthSpeed)) == 0) {
                    age++;
                    level.setData(x, y, z, age);
                }
            }
        }
    }
    
    public void growCropsToMax(final Level level, final int x, final int y, final int z) {
        level.setData(x, y, z, 7);
    }
    
    private float getGrowthSpeed(final Level level, final int x, final int y, final int z) {
        float speed = 1.0f;

        final int n = level.getTile(x, y, z - 1);
        final int s = level.getTile(x, y, z + 1);
        final int w = level.getTile(x - 1, y, z);
        final int e = level.getTile(x + 1, y, z);

        final int d0 = level.getTile(x - 1, y, z - 1);
        final int d1 = level.getTile(x + 1, y, z - 1);
        final int d2 = level.getTile(x + 1, y, z + 1);
        final int d3 = level.getTile(x - 1, y, z + 1);

        final boolean horizontal = w == this.id || e == this.id;
        final boolean vertical = n == this.id || s == this.id;
        final boolean diagonal = d0 == this.id || d1 == this.id || d2 == this.id || d3 == this.id;

        for (int xx = x - 1; xx <= x + 1; ++xx) {
            for (int zz = z - 1; zz <= z + 1; ++zz) {
                final int t = level.getTile(xx, y - 1, zz);

                float tileSpeed = 0.0f;
                if (t == Tile.farmland.id) {
                    tileSpeed = 1.0f;
                    if (level.getData(xx, y - 1, zz) > 0) tileSpeed = 3.0f;
                }
                if (xx != x || zz != z) tileSpeed /= 4.0f;

                speed += tileSpeed;
            }
        }

        if (diagonal || (horizontal && vertical)) speed /= 2.0f;

        return speed;
    }
    
    @Override
    public int getTexture(final int face, int data) {
        if (data < 0) data = 7;
        return this.tex + data;
    }
    
    @Override
    public int getRenderShape() {
        return Tile.SHAPE_ROWS;
    }

    /**
     * Using this method instead of destroy() to determine if seeds should be
     * dropped
     */
    @Override
    public void spawnResources(final Level level, final int x, final int y, final int z, final int data, final float odds) {
        super.spawnResources(level, x, y, z, data, odds);

        if (level.isClientSide) {
            return;
        }

        for (int i = 0; i < 3; ++i) {
            if (level.random.nextInt(5 * 3) > data) continue;

            final float s = 0.7f;
            float xo = x + (level.random.nextFloat() * s + (1.0f - s) * 0.5f);
            float yo = y + (level.random.nextFloat() * s + (1.0f - s) * 0.5f);
            float zo = z + (level.random.nextFloat() * s + (1.0f - s) * 0.5f);
            final ItemEntity item = new ItemEntity(level, xo, yo, zo, new ItemInstance(Item.seeds));
            item.throwTime = 10;
            level.addEntity(item);
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
