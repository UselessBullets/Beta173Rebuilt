// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.tile.Tile;
import java.util.Random;
import net.minecraft.world.level.Level;

public class SpruceFeature extends Feature
{
    @Override
    public boolean place(final Level level, final Random random, final int x, final int y, final int z) {
        final int n = random.nextInt(4) + 6;
        final int n2 = 1 + random.nextInt(2);
        final int n3 = n - n2;
        final int n4 = 2 + random.nextInt(2);
        int n5 = 1;
        if (y < 1 || y + n + 1 > 128) {
            return false;
        }
        for (int y2 = y; y2 <= y + 1 + n && n5 != 0; ++y2) {
            int n6;
            if (y2 - y < n2) {
                n6 = 0;
            }
            else {
                n6 = n4;
            }
            for (int x2 = x - n6; x2 <= x + n6 && n5 != 0; ++x2) {
                for (int z2 = z - n6; z2 <= z + n6 && n5 != 0; ++z2) {
                    if (y2 >= 0 && y2 < 128) {
                        final int tile = level.getTile(x2, y2, z2);
                        if (tile != 0 && tile != Tile.leaves.id) {
                            n5 = 0;
                        }
                    }
                    else {
                        n5 = 0;
                    }
                }
            }
        }
        if (n5 == 0) {
            return false;
        }
        final int tile2 = level.getTile(x, y - 1, z);
        if ((tile2 != Tile.grass.id && tile2 != Tile.dirt.id) || y >= 128 - n - 1) {
            return false;
        }
        level.setTileNoUpdate(x, y - 1, z, Tile.dirt.id);
        int nextInt = random.nextInt(2);
        int n7 = 1;
        int n8 = 0;
        for (int i = 0; i <= n3; ++i) {
            final int n9 = y + n - i;
            for (int j = x - nextInt; j <= x + nextInt; ++j) {
                final int a = j - x;
                for (int k = z - nextInt; k <= z + nextInt; ++k) {
                    final int a2 = k - z;
                    if (Math.abs(a) != nextInt || Math.abs(a2) != nextInt || nextInt <= 0) {
                        if (!Tile.solid[level.getTile(j, n9, k)]) {
                            level.setTileAndDataNoUpdate(j, n9, k, Tile.leaves.id, 1);
                        }
                    }
                }
            }
            if (nextInt >= n7) {
                nextInt = n8;
                n8 = 1;
                if (++n7 > n4) {
                    n7 = n4;
                }
            }
            else {
                ++nextInt;
            }
        }
        for (int nextInt2 = random.nextInt(3), l = 0; l < n - nextInt2; ++l) {
            final int tile3 = level.getTile(x, y + l, z);
            if (tile3 == 0 || tile3 == Tile.leaves.id) {
                level.setTileAndDataNoUpdate(x, y + l, z, Tile.treeTrunk.id, 1);
            }
        }
        return true;
    }
}
