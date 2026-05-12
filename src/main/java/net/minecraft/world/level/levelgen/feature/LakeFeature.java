// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.tile.Tile;
import java.util.Random;
import net.minecraft.world.level.Level;

public class LakeFeature extends Feature
{
    private int tile;
    
    public LakeFeature(final int tile) {
        this.tile = tile;
    }
    
    @Override
    public boolean place(final Level level, final Random random, int x, int y, int z) {
        for (x -= 8, z -= 8; y > 0 && level.isEmptyTile(x, y, z); --y) {}
        y -= 4;
        final boolean[] array = new boolean[2048];
        for (int n = random.nextInt(4) + 4, i = 0; i < n; ++i) {
            final double n2 = random.nextDouble() * 6.0 + 3.0;
            final double n3 = random.nextDouble() * 4.0 + 2.0;
            final double n4 = random.nextDouble() * 6.0 + 3.0;
            final double n5 = random.nextDouble() * (16.0 - n2 - 2.0) + 1.0 + n2 / 2.0;
            final double n6 = random.nextDouble() * (8.0 - n3 - 4.0) + 2.0 + n3 / 2.0;
            final double n7 = random.nextDouble() * (16.0 - n4 - 2.0) + 1.0 + n4 / 2.0;
            for (int j = 1; j < 15; ++j) {
                for (int k = 1; k < 15; ++k) {
                    for (int l = 1; l < 7; ++l) {
                        final double n8 = (j - n5) / (n2 / 2.0);
                        final double n9 = (l - n6) / (n3 / 2.0);
                        final double n10 = (k - n7) / (n4 / 2.0);
                        if (n8 * n8 + n9 * n9 + n10 * n10 < 1.0) {
                            array[(j * 16 + k) * 8 + l] = true;
                        }
                    }
                }
            }
        }
        for (int n11 = 0; n11 < 16; ++n11) {
            for (int n12 = 0; n12 < 16; ++n12) {
                for (int n13 = 0; n13 < 8; ++n13) {
                    if (!array[(n11 * 16 + n12) * 8 + n13] && ((n11 < 15 && array[((n11 + 1) * 16 + n12) * 8 + n13]) || (n11 > 0 && array[((n11 - 1) * 16 + n12) * 8 + n13]) || (n12 < 15 && array[(n11 * 16 + (n12 + 1)) * 8 + n13]) || (n12 > 0 && array[(n11 * 16 + (n12 - 1)) * 8 + n13]) || (n13 < 7 && array[(n11 * 16 + n12) * 8 + (n13 + 1)]) || (n13 > 0 && array[(n11 * 16 + n12) * 8 + (n13 - 1)]))) {
                        final Material material = level.getMaterial(x + n11, y + n13, z + n12);
                        if (n13 >= 4 && material.isLiquid()) {
                            return false;
                        }
                        if (n13 < 4 && !material.isSolid() && level.getTile(x + n11, y + n13, z + n12) != this.tile) {
                            return false;
                        }
                    }
                }
            }
        }
        for (int n14 = 0; n14 < 16; ++n14) {
            for (int n15 = 0; n15 < 16; ++n15) {
                for (int n16 = 0; n16 < 8; ++n16) {
                    if (array[(n14 * 16 + n15) * 8 + n16]) {
                        level.setTileNoUpdate(x + n14, y + n16, z + n15, (n16 >= 4) ? 0 : this.tile);
                    }
                }
            }
        }
        for (int n17 = 0; n17 < 16; ++n17) {
            for (int n18 = 0; n18 < 16; ++n18) {
                for (int n19 = 4; n19 < 8; ++n19) {
                    if (array[(n17 * 16 + n18) * 8 + n19] && level.getTile(x + n17, y + n19 - 1, z + n18) == Tile.dirt.id && level.getBrightness(LightLayer.Sky, x + n17, y + n19, z + n18) > 0) {
                        level.setTileNoUpdate(x + n17, y + n19 - 1, z + n18, Tile.grass.id);
                    }
                }
            }
        }
        if (Tile.tiles[this.tile].material == Material.lava) {
            for (int n20 = 0; n20 < 16; ++n20) {
                for (int n21 = 0; n21 < 16; ++n21) {
                    for (int n22 = 0; n22 < 8; ++n22) {
                        if (!array[(n20 * 16 + n21) * 8 + n22] && ((n20 < 15 && array[((n20 + 1) * 16 + n21) * 8 + n22]) || (n20 > 0 && array[((n20 - 1) * 16 + n21) * 8 + n22]) || (n21 < 15 && array[(n20 * 16 + (n21 + 1)) * 8 + n22]) || (n21 > 0 && array[(n20 * 16 + (n21 - 1)) * 8 + n22]) || (n22 < 7 && array[(n20 * 16 + n21) * 8 + (n22 + 1)]) || (n22 > 0 && array[(n20 * 16 + n21) * 8 + (n22 - 1)])) && (n22 < 4 || random.nextInt(2) != 0) && level.getMaterial(x + n20, y + n22, z + n21).isSolid()) {
                            level.setTileNoUpdate(x + n20, y + n22, z + n21, Tile.rock.id);
                        }
                    }
                }
            }
        }
        return true;
    }
}
