// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import util.Mth;
import java.util.Random;

public class LargeHellCaveFeature extends LargeFeature
{
    protected void addRoom(final int xOffs, final int zOffs, final byte[] blocks, final double xRoom, final double yRoom, final double zRoom) {
        this.addTunnel(xOffs, zOffs, blocks, xRoom, yRoom, zRoom, 1.0f + this.random.nextFloat() * 6.0f, 0.0f, 0.0f, -1, -1, 0.5);
    }
    
    protected void addTunnel(final int xOffs, final int zOffs, final byte[] blocks, double xCave, double yCave, double zCave, final float thickness, float yRot, float xRot, int step, int dist, final double yScale) {
        final double n = xOffs * 16 + 8;
        final double n2 = zOffs * 16 + 8;
        float n3 = 0.0f;
        float n4 = 0.0f;
        final Random random = new Random(this.random.nextLong());
        if (dist <= 0) {
            final int n5 = this.radius * 16 - 16;
            dist = n5 - random.nextInt(n5 / 4);
        }
        boolean b = false;
        if (step == -1) {
            step = dist / 2;
            b = true;
        }
        final int n6 = random.nextInt(dist / 2) + dist / 4;
        final boolean b2 = random.nextInt(6) == 0;
        while (step < dist) {
            final double n7 = 1.5 + Mth.sin(step * Mth.PI / dist) * thickness * 1.0f;
            final double n8 = n7 * yScale;
            final float cos = Mth.cos(xRot);
            final float sin = Mth.sin(xRot);
            xCave += Mth.cos(yRot) * cos;
            yCave += sin;
            zCave += Mth.sin(yRot) * cos;
            if (b2) {
                xRot *= 0.92f;
            }
            else {
                xRot *= 0.7f;
            }
            xRot += n4 * 0.1f;
            yRot += n3 * 0.1f;
            final float n9 = n4 * 0.9f;
            final float n10 = n3 * 0.75f;
            n4 = n9 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
            n3 = n10 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
            if (!b && step == n6 && thickness > 1.0f) {
                this.addTunnel(xOffs, zOffs, blocks, xCave, yCave, zCave, random.nextFloat() * 0.5f + 0.5f, yRot - Mth.HALF_PI, xRot / 3.0f, step, dist, 1.0);
                this.addTunnel(xOffs, zOffs, blocks, xCave, yCave, zCave, random.nextFloat() * 0.5f + 0.5f, yRot + Mth.HALF_PI, xRot / 3.0f, step, dist, 1.0);
                return;
            }
            if (b || random.nextInt(4) != 0) {
                final double n11 = xCave - n;
                final double n12 = zCave - n2;
                final double n13 = dist - step;
                final double n14 = thickness + 2.0f + 16.0f;
                if (n11 * n11 + n12 * n12 - n13 * n13 > n14 * n14) {
                    return;
                }
                if (xCave >= n - 16.0 - n7 * 2.0 && zCave >= n2 - 16.0 - n7 * 2.0 && xCave <= n + 16.0 + n7 * 2.0) {
                    if (zCave <= n2 + 16.0 + n7 * 2.0) {
                        int n15 = Mth.floor(xCave - n7) - xOffs * 16 - 1;
                        int n16 = Mth.floor(xCave + n7) - xOffs * 16 + 1;
                        int n17 = Mth.floor(yCave - n8) - 1;
                        int n18 = Mth.floor(yCave + n8) + 1;
                        int n19 = Mth.floor(zCave - n7) - zOffs * 16 - 1;
                        int n20 = Mth.floor(zCave + n7) - zOffs * 16 + 1;
                        if (n15 < 0) {
                            n15 = 0;
                        }
                        if (n16 > 16) {
                            n16 = 16;
                        }
                        if (n17 < 1) {
                            n17 = 1;
                        }
                        if (n18 > 120) {
                            n18 = 120;
                        }
                        if (n19 < 0) {
                            n19 = 0;
                        }
                        if (n20 > 16) {
                            n20 = 16;
                        }
                        int n21 = 0;
                        for (int n22 = n15; n21 == 0 && n22 < n16; ++n22) {
                            for (int n23 = n19; n21 == 0 && n23 < n20; ++n23) {
                                for (int n24 = n18 + 1; n21 == 0 && n24 >= n17 - 1; --n24) {
                                    final int n25 = (n22 * 16 + n23) * 128 + n24;
                                    if (n24 >= 0) {
                                        if (n24 < 128) {
                                            if (blocks[n25] == Tile.lava.id || blocks[n25] == Tile.calmLava.id) {
                                                n21 = 1;
                                            }
                                            if (n24 != n17 - 1 && n22 != n15 && n22 != n16 - 1 && n23 != n19 && n23 != n20 - 1) {
                                                n24 = n17;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (n21 == 0) {
                            for (int i = n15; i < n16; ++i) {
                                final double n26 = (i + xOffs * 16 + 0.5 - xCave) / n7;
                                for (int j = n19; j < n20; ++j) {
                                    final double n27 = (j + zOffs * 16 + 0.5 - zCave) / n7;
                                    int n28 = (i * 16 + j) * 128 + n18;
                                    for (int k = n18 - 1; k >= n17; --k) {
                                        final double n29 = (k + 0.5 - yCave) / n8;
                                        if (n29 > -0.7 && n26 * n26 + n29 * n29 + n27 * n27 < 1.0) {
                                            final byte b3 = blocks[n28];
                                            if (b3 == Tile.hellRock.id || b3 == Tile.dirt.id || b3 == Tile.grass.id) {
                                                blocks[n28] = 0;
                                            }
                                        }
                                        --n28;
                                    }
                                }
                            }
                            if (b) {
                                break;
                            }
                        }
                    }
                }
            }
            ++step;
        }
    }
    
    @Override
    protected void addFeature(final Level level, final int x, final int z, final int xOffs, final int zOffs, final byte[] blocks) {
        int nextInt = this.random.nextInt(this.random.nextInt(this.random.nextInt(10) + 1) + 1);
        if (this.random.nextInt(5) != 0) {
            nextInt = 0;
        }
        for (int i = 0; i < nextInt; ++i) {
            final double n = x * 16 + this.random.nextInt(16);
            final double n2 = this.random.nextInt(128);
            final double n3 = z * 16 + this.random.nextInt(16);
            int n4 = 1;
            if (this.random.nextInt(4) == 0) {
                this.addRoom(xOffs, zOffs, blocks, n, n2, n3);
                n4 += this.random.nextInt(4);
            }
            for (int j = 0; j < n4; ++j) {
                this.addTunnel(xOffs, zOffs, blocks, n, n2, n3, (this.random.nextFloat() * 2.0f + this.random.nextFloat()) * 2.0f, this.random.nextFloat() * Mth.PI * 2.0f, (this.random.nextFloat() - 0.5f) * 2.0f / 8.0f, 0, 0, 0.5);
            }
        }
    }
}
