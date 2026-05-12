// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;

public class GrassTile extends Tile
{
    protected GrassTile(final int id) {
        super(id, Material.grass);
        this.tex = 3;
        this.setTicking(true);
    }
    
    @Override
    public int getTexture(final LevelSource level, final int x, final int y, final int z, final int face) {
        if (face == 1) {
            return 0;
        }
        if (face == 0) {
            return 2;
        }
        final Material material = level.getMaterial(x, y + 1, z);
        if (material == Material.topSnow || material == Material.snow) {
            return 68;
        }
        return 3;
    }
    
    @Override
    public int getColor(final LevelSource level, final int x, final int y, final int z) {
        level.getBiomeSource().getBiomeBlock(x, z, 1, 1);
        return GrassColor.get(level.getBiomeSource().temperatures[0], level.getBiomeSource().downfalls[0]);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (level.isClientSide) {
            return;
        }
        if (level.getRawBrightness(x, y + 1, z) < 4 && Tile.lightBlock[level.getTile(x, y + 1, z)] > 2) {
            if (random.nextInt(4) != 0) {
                return;
            }
            level.setTile(x, y, z, Tile.dirt.id);
        }
        else if (level.getRawBrightness(x, y + 1, z) >= 9) {
            final int n = x + random.nextInt(3) - 1;
            final int n2 = y + random.nextInt(5) - 3;
            final int n3 = z + random.nextInt(3) - 1;
            final int tile = level.getTile(n, n2 + 1, n3);
            if (level.getTile(n, n2, n3) == Tile.dirt.id && level.getRawBrightness(n, n2 + 1, n3) >= 4 && Tile.lightBlock[tile] <= 2) {
                level.setTile(n, n2, n3, Tile.grass.id);
            }
        }
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Tile.dirt.getResource(0, random);
    }
}
