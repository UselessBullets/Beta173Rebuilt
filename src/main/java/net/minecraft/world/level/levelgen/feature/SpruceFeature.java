// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.tile.LeafTile;
import net.minecraft.world.level.tile.Tile;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.TreeTile;

public class SpruceFeature extends Feature
{
    @Override
    public boolean place(final Level level, final Random random, final int x, final int y, final int z) {
        // pines can be quite tall
        final int treeHeight = random.nextInt(4) + 6;
        final int trunkHeight = 1 + random.nextInt(2);
        final int topHeight = treeHeight - trunkHeight;
        final int leafRadius = 2 + random.nextInt(2);

        boolean free = true;
        // may not be outside of y boundaries
        if (y < 1 || y + treeHeight + 1 > Level.MAX_BUILD_HEIGHT) {
            return false;
        }

        // make sure there is enough space
        for (int yy = y; yy <= y + 1 + treeHeight && free; ++yy) {
            int r;
            if (yy - y < trunkHeight) {
                r = 0;
            }
            else {
                r = leafRadius;
            }
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

        // must stand on ground
        final int belowTile = level.getTile(x, y - 1, z);
        if ((belowTile != Tile.grass.id && belowTile != Tile.dirt.id) || y >= Level.MAX_BUILD_HEIGHT - treeHeight - 1) return false;

        level.setTileNoUpdate(x, y - 1, z, Tile.dirt.id);

        // place leaf top
        int currentRadius = random.nextInt(2);
        int maxRadius = 1;
        int minRadius = 0;
        for (int heightPos = 0; heightPos <= topHeight; ++heightPos) {
            final int yy = y + treeHeight - heightPos;
            for (int xx = x - currentRadius; xx <= x + currentRadius; ++xx) {
                final int xo = xx - x;
                for (int zz = z - currentRadius; zz <= z + currentRadius; ++zz) {
                    final int zo = zz - z;
                    if (Math.abs(xo) == currentRadius && Math.abs(zo) == currentRadius && currentRadius > 0) continue;
                    if (!Tile.solid[level.getTile(xx, yy, zz)]) level.setTileAndDataNoUpdate(xx, yy, zz, Tile.leaves.id, LeafTile.EVERGREEN_LEAF);
                }
            }

            if (currentRadius >= maxRadius) {
                currentRadius = minRadius;
                minRadius = 1;
                maxRadius++;
                if (maxRadius > leafRadius) {
                    maxRadius = leafRadius;
                }
            }
            else {
                currentRadius = currentRadius + 1;
            }
        }
        int topOffset = random.nextInt(3);
        for (int hh = 0; hh < treeHeight - topOffset; ++hh) {
            final int t = level.getTile(x, y + hh, z);
            if (t == 0 || t == Tile.leaves.id) level.setTileAndDataNoUpdate(x, y + hh, z, Tile.treeTrunk.id, TreeTile.DARK_TRUNK);
        }
        return true;
    }
}
