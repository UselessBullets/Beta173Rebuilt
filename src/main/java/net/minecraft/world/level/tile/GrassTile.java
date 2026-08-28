// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.Facing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;

public class GrassTile extends Tile
{
    public static final int MIN_BRIGHTNESS = 4;
    protected GrassTile(final int id) {
        super(id, Material.grass);
        this.tex = 3;
        this.setTicking(true);
    }
    
    @Override
    public int getTexture(final LevelSource level, final int x, final int y, final int z, final int face) {
        if (face == Facing.UP) return 0;
        if (face == Facing.DOWN) return 2;
        final Material above = level.getMaterial(x, y + 1, z);
        if (above == Material.topSnow || above == Material.snow) return 68;
        return 3;
    }
    
    @Override
    public int getColor(final LevelSource level, final int x, final int y, final int z) {
        level.getBiomeSource().getBiomeBlock(x, z, 1, 1);
        double temp = level.getBiomeSource().temperatures[0];
        double rain = level.getBiomeSource().downfalls[0];

        return GrassColor.get(temp, rain);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (level.isClientSide) return;

        if (level.getRawBrightness(x, y + 1, z) < MIN_BRIGHTNESS && Tile.lightBlock[level.getTile(x, y + 1, z)] > 2) {
            if (random.nextInt(4) == 0) {
                level.setTile(x, y, z, Tile.dirt.id);
            }
        }
        else if (level.getRawBrightness(x, y + 1, z) >= (Level.MAX_BRIGHTNESS - 6)) {
            final int xt = x + random.nextInt(3) - 1;
            final int yt = y + random.nextInt(5) - 3;
            final int zt = z + random.nextInt(3) - 1;
            final int above = level.getTile(xt, yt + 1, zt);
            if (level.getTile(xt, yt, zt) == Tile.dirt.id && level.getRawBrightness(xt, yt + 1, zt) >= MIN_BRIGHTNESS && Tile.lightBlock[above] <= 2) {
                level.setTile(xt, yt, zt, Tile.grass.id);
            }
        }
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Tile.dirt.getResource(0, random);
    }
}
