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
        final int n = random.nextInt(3) + 4;
        int n2 = 1;
        if (y < 1 || y + n + 1 > 128) {
            return false;
        }
        for (int i = y; i <= y + 1 + n; ++i) {
            int n3 = 1;
            if (i == y) {
                n3 = 0;
            }
            if (i >= y + 1 + n - 2) {
                n3 = 2;
            }
            for (int x2 = x - n3; x2 <= x + n3 && n2 != 0; ++x2) {
                for (int z2 = z - n3; z2 <= z + n3 && n2 != 0; ++z2) {
                    if (i >= 0 && i < 128) {
                        final int tile = level.getTile(x2, i, z2);
                        if (tile != 0 && tile != Tile.leaves.id) {
                            n2 = 0;
                        }
                    }
                    else {
                        n2 = 0;
                    }
                }
            }
        }
        if (n2 == 0) {
            return false;
        }
        final int tile2 = level.getTile(x, y - 1, z);
        if ((tile2 != Tile.grass.id && tile2 != Tile.dirt.id) || y >= 128 - n - 1) {
            return false;
        }
        level.setTileNoUpdate(x, y - 1, z, Tile.dirt.id);
        for (int j = y - 3 + n; j <= y + n; ++j) {
            final int n4 = j - (y + n);
            for (int n5 = 1 - n4 / 2, k = x - n5; k <= x + n5; ++k) {
                final int a = k - x;
                for (int l = z - n5; l <= z + n5; ++l) {
                    final int a2 = l - z;
                    if (Math.abs(a) == n5 && Math.abs(a2) == n5) {
                        if (random.nextInt(2) == 0) {
                            continue;
                        }
                        if (n4 == 0) {
                            continue;
                        }
                    }
                    if (!Tile.solid[level.getTile(k, j, l)]) {
                        level.setTileNoUpdate(k, j, l, Tile.leaves.id);
                    }
                }
            }
        }
        for (int n6 = 0; n6 < n; ++n6) {
            final int tile3 = level.getTile(x, y + n6, z);
            if (tile3 == 0 || tile3 == Tile.leaves.id) {
                level.setTileNoUpdate(x, y + n6, z, Tile.treeTrunk.id);
            }
        }
        return true;
    }
}
