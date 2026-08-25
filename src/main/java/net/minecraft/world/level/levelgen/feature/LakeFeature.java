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
        x -= 8;
        z -= 8;
        while (y > 0 && level.isEmptyTile(x, y, z)) {
            --y;
        }
        y -= 4;

        final boolean[] grid = new boolean[16 * 16 * 8];

        int spots = random.nextInt(4) + 4;
        for (int i = 0; i < spots; ++i) {
            final double xr = random.nextDouble() * 6.0 + 3.0;
            final double yr = random.nextDouble() * 4.0 + 2.0;
            final double zr = random.nextDouble() * 6.0 + 3.0;

            final double xp = random.nextDouble() * (16.0 - xr - 2.0) + 1.0 + xr / 2.0;
            final double yp = random.nextDouble() * (8.0 - yr - 4.0) + 2.0 + yr / 2.0;
            final double zp = random.nextDouble() * (16.0 - zr - 2.0) + 1.0 + zr / 2.0;

            for (int xx = 1; xx < 15; ++xx) {
                for (int zz = 1; zz < 15; ++zz) {
                    for (int yy = 1; yy < 7; ++yy) {
                        final double xd = (xx - xp) / (xr / 2.0);
                        final double yd = (yy - yp) / (yr / 2.0);
                        final double zd = (zz - zp) / (zr / 2.0);
                        double d = xd * xd + yd * yd + zd * zd;
                        if (d < 1.0) grid[(xx * 16 + zz) * 8 + yy] = true;
                    }
                }
            }
        }

        for (int xx = 0; xx < 16; ++xx) {
            for (int zz = 0; zz < 16; ++zz) {
                for (int yy = 0; yy < 8; ++yy) {
                    boolean check = !grid[(xx * 16 + zz) * 8 + yy] && (
                               (xx < 15 && grid[((xx + 1) * 16 + zz) * 8 + yy])
                            || (xx > 0 && grid[((xx - 1) * 16 + zz) * 8 + yy])
                            || (zz < 15 && grid[(xx * 16 + (zz + 1)) * 8 + yy])
                            || (zz > 0 && grid[(xx * 16 + (zz - 1)) * 8 + yy])
                            || (yy < 7 && grid[(xx * 16 + zz) * 8 + (yy + 1)])
                            || (yy > 0 && grid[(xx * 16 + zz) * 8 + (yy - 1)]));

                    if (check) {
                        final Material m = level.getMaterial(x + xx, y + yy, z + zz);
                        if (yy >= 4 && m.isLiquid()) return false;
                        if (yy < 4 && !m.isSolid() && level.getTile(x + xx, y + yy, z + zz) != this.tile) return false;
                    }
                }
            }
        }

        for (int xx = 0; xx < 16; ++xx) {
            for (int zz = 0; zz < 16; ++zz) {
                for (int yy = 0; yy < 8; ++yy) {
                    if (grid[(xx * 16 + zz) * 8 + yy]) {
                        level.setTileNoUpdate(x + xx, y + yy, z + zz, yy >= 4 ? 0 : this.tile);
                    }
                }
            }
        }

        for (int xx = 0; xx < 16; ++xx) {
            for (int zz = 0; zz < 16; ++zz) {
                for (int yy = 4; yy < 8; ++yy) {
                    if (grid[(xx * 16 + zz) * 8 + yy]) {
                        if (level.getTile(x + xx, y + yy - 1, z + zz) == Tile.dirt.id && level.getBrightness(LightLayer.Sky, x + xx, y + yy, z + zz) > 0) {
                            level.setTileNoUpdate(x + xx, y + yy - 1, z + zz, Tile.grass.id);
                        }
                    }
                }
            }
        }

        if (Tile.tiles[this.tile].material == Material.lava) {
            for (int xx = 0; xx < 16; ++xx) {
                for (int zz = 0; zz < 16; ++zz) {
                    for (int yy = 0; yy < 8; ++yy) {
                        boolean check = !grid[(xx * 16 + zz) * 8 + yy] && (
                                   (xx < 15 && grid[((xx + 1) * 16 + zz) * 8 + yy])
                                || (xx > 0 && grid[((xx - 1) * 16 + zz) * 8 + yy])
                                || (zz < 15 && grid[(xx * 16 + (zz + 1)) * 8 + yy])
                                || (zz > 0 && grid[(xx * 16 + (zz - 1)) * 8 + yy])
                                || (yy < 7 && grid[(xx * 16 + zz) * 8 + (yy + 1)])
                                || (yy > 0 && grid[(xx * 16 + zz) * 8 + (yy - 1)]));

                        if (check) {
                            if ((yy < 4 || random.nextInt(2) != 0) && level.getMaterial(x + xx, y + yy, z + zz).isSolid()) {
                                level.setTileNoUpdate(x + xx, y + yy, z + zz, Tile.rock.id);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
