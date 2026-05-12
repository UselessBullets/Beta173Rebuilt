// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.tile.Tile;
import java.util.Random;
import net.minecraft.world.level.Level;

public class PineFeature extends Feature
{
    @Override
    public boolean place(final Level level, final Random random, final int x, final int y, final int z) {
        final int n = random.nextInt(5) + 7;
        final int n2 = n - random.nextInt(2) - 3;
        final int n3 = 1 + random.nextInt(n - n2 + 1);
        int n4 = 1;
        if (y < 1 || y + n + 1 > 128) {
            return false;
        }
        for (int y2 = y; y2 <= y + 1 + n && n4 != 0; ++y2) {
            int n5;
            if (y2 - y < n2) {
                n5 = 0;
            }
            else {
                n5 = n3;
            }
            for (int x2 = x - n5; x2 <= x + n5 && n4 != 0; ++x2) {
                for (int z2 = z - n5; z2 <= z + n5 && n4 != 0; ++z2) {
                    if (y2 >= 0 && y2 < 128) {
                        final int tile = level.getTile(x2, y2, z2);
                        if (tile != 0 && tile != Tile.leaves.id) {
                            n4 = 0;
                        }
                    }
                    else {
                        n4 = 0;
                    }
                }
            }
        }
        if (n4 == 0) {
            return false;
        }
        final int tile2 = level.getTile(x, y - 1, z);
        if ((tile2 != Tile.grass.id && tile2 != Tile.dirt.id) || y >= 128 - n - 1) {
            return false;
        }
        level.setTileNoUpdate(x, y - 1, z, Tile.dirt.id);
        int n6 = 0;
        for (int i = y + n; i >= y + n2; --i) {
            for (int j = x - n6; j <= x + n6; ++j) {
                final int a = j - x;
                for (int k = z - n6; k <= z + n6; ++k) {
                    final int a2 = k - z;
                    if (Math.abs(a) != n6 || Math.abs(a2) != n6 || n6 <= 0) {
                        if (!Tile.solid[level.getTile(j, i, k)]) {
                            level.setTileAndDataNoUpdate(j, i, k, Tile.leaves.id, 1);
                        }
                    }
                }
            }
            if (n6 >= 1 && i == y + n2 + 1) {
                --n6;
            }
            else if (n6 < n3) {
                ++n6;
            }
        }
        for (int l = 0; l < n - 1; ++l) {
            final int tile3 = level.getTile(x, y + l, z);
            if (tile3 == 0 || tile3 == Tile.leaves.id) {
                level.setTileAndDataNoUpdate(x, y + l, z, Tile.treeTrunk.id, 1);
            }
        }
        return true;
    }
}
