// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.tile.Tile;
import java.util.Random;
import net.minecraft.world.level.Level;

public class TreeFeature extends Feature
{
    @Override
    public boolean place(final Level level, final Random random, final int x, final int y, final int z) {
        final int treeHeight = random.nextInt(3) + 4;

        boolean free = true;
        if (y < 1 || y + treeHeight + 1 > Level.MAX_BUILD_HEIGHT) return false;

        for (int yy = y; yy <= y + 1 + treeHeight; ++yy) {
            int r = 1;
            if (yy == y) r = 0;
            if (yy >= y + 1 + treeHeight - 2) r = 2;

            for (int xx = x - r; xx <= x + r && free; ++xx) {
                for (int zz = z - r; zz <= z + r && free; ++zz) {
                    if (yy >= 0 && yy < Level.MAX_BUILD_HEIGHT) {
                        final int tt = level.getTile(xx, yy, zz);
                        if (tt != 0 && tt != Tile.leaves.id) free = false;
                    }
                    else {
                        free = false;
                    }
                }
            }
        }

        if (!free) return false;

        final int belowTile = level.getTile(x, y - 1, z);
        if ((belowTile != Tile.grass.id && belowTile != Tile.dirt.id) || y >= Level.MAX_BUILD_HEIGHT - treeHeight - 1) return false;

        level.setTileNoUpdate(x, y - 1, z, Tile.dirt.id);

        int grassHeight = 3;
        int extraWidth = 0;
        for (int yy = y - grassHeight + treeHeight; yy <= y + treeHeight; ++yy) {
            final int yo = yy - (y + treeHeight);
            final int offs = extraWidth + 1 - yo / 2;
            for (int xx = x - offs; xx <= x + offs; ++xx) {
                final int xo = xx - x;
                for (int zz = z - offs; zz <= z + offs; ++zz) {
                    final int zo = zz - z;
                    if (Math.abs(xo) == offs && Math.abs(zo) == offs && (random.nextInt(2) == 0 || yo == 0)) continue;
                    if (!Tile.solid[level.getTile(xx, yy, zz)]) level.setTileNoUpdate(xx, yy, zz, Tile.leaves.id);
                }
            }
        }

        for (int hh = 0; hh < treeHeight; ++hh) {
            final int t = level.getTile(x, y + hh, z);
            if (t == 0 || t == Tile.leaves.id) level.setTileNoUpdate(x, y + hh, z, Tile.treeTrunk.id);
        }
        
        return true;
    }
}
