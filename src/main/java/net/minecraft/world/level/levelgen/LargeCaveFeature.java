// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import util.Mth;
import java.util.Random;

public class LargeCaveFeature extends LargeFeature
{
    protected void addRoom(final int xOffs, final int zOffs, final byte[] blocks, final double xRoom, final double yRoom, final double zRoom) {
        this.addTunnel(xOffs, zOffs, blocks, xRoom, yRoom, zRoom, 1.0f + this.random.nextFloat() * 6.0f, 0.0f, 0.0f, -1, -1, 0.5);
    }
    
    protected void addTunnel(final int xOffs, final int zOffs, final byte[] blocks, double xCave, double yCave, double zCave, final float thickness, float yRot, float xRot, int step, int dist, final double yScale) {
        final double xMid = xOffs * 16 + 8;
        final double zMid = zOffs * 16 + 8;

        float yRota = 0.0f;
        float xRota = 0.0f;
        final Random random = new Random(this.random.nextLong());
        if (dist <= 0) {
            final int max = this.radius * 16 - 16;
            dist = max - random.nextInt(max / 4);
        }
        boolean singleStep = false;

        if (step == -1) {
            step = dist / 2;
            singleStep = true;
        }


        final int splitPoint = random.nextInt(dist / 2) + dist / 4;
        final boolean steep = random.nextInt(6) == 0;

        for (; step < dist; step++) {
            final double rad = 1.5 + Mth.sin(step * Mth.PI / dist) * thickness * 1.0f;
            final double yRad = rad * yScale;

            final float xc = Mth.cos(xRot);
            final float xs = Mth.sin(xRot);
            xCave += Mth.cos(yRot) * xc;
            yCave += xs;
            zCave += Mth.sin(yRot) * xc;

            if (steep) {
                xRot *= 0.92f;
            }
            else {
                xRot *= 0.7f;
            }
            xRot += xRota * 0.1f;
            yRot += yRota * 0.1f;

            xRota *= 0.9f;
            yRota *= 0.75f;
            xRota += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
            yRota += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;

            if (!singleStep && step == splitPoint && thickness > 1.0f) {
                this.addTunnel(xOffs, zOffs, blocks, xCave, yCave, zCave, random.nextFloat() * 0.5f + 0.5f, yRot - Mth.HALF_PI, xRot / 3.0f, step, dist, 1.0);
                this.addTunnel(xOffs, zOffs, blocks, xCave, yCave, zCave, random.nextFloat() * 0.5f + 0.5f, yRot + Mth.HALF_PI, xRot / 3.0f, step, dist, 1.0);
                return;
            }
            if (!singleStep && random.nextInt(4) == 0) continue;

            {
                final double xd = xCave - xMid;
                final double zd = zCave - zMid;
                final double remaining = dist - step;
                final double rr = thickness + 2.0f + 16.0f;
                if (xd * xd + zd * zd - remaining * remaining > rr * rr) {
                    return;
                }
            }

            if (xCave < xMid - 16.0 - rad * 2.0 || zCave < zMid - 16.0 - rad * 2.0 || xCave > xMid + 16.0 + rad * 2.0 || zCave > zMid + 16.0 + rad * 2.0) continue;

            int x0 = Mth.floor(xCave - rad) - xOffs * 16 - 1;
            int x1 = Mth.floor(xCave + rad) - xOffs * 16 + 1;

            int y0 = Mth.floor(yCave - yRad) - 1;
            int y1 = Mth.floor(yCave + yRad) + 1;

            int z0 = Mth.floor(zCave - rad) - zOffs * 16 - 1;
            int z1 = Mth.floor(zCave + rad) - zOffs * 16 + 1;

            if (x0 < 0) x0 = 0;
            if (x1 > 16) x1 = 16;

            if (y0 < 1) y0 = 1;
            if (y1 > Level.MAX_HEIGHT - 8) y1 = Level.MAX_HEIGHT - 8;

            if (z0 < 0) z0 = 0;
            if (z1 > 16) z1 = 16;

            boolean detectedWater = false;
            for (int xx = x0; !detectedWater && xx < x1; ++xx) {
                for (int zz = z0; !detectedWater && zz < z1; ++zz) {
                    for (int yy = y1 + 1; !detectedWater && yy >= y0 - 1; --yy) {
                        final int p = (xx * 16 + zz) * Level.MAX_HEIGHT + yy;
                        if (yy < 0 || yy >= Level.MAX_HEIGHT) continue;
                        if (blocks[p] == Tile.water.id || blocks[p] == Tile.calmWater.id) {
                            detectedWater = true;
                        }
                        if (yy != y0 - 1 && xx != x0 && xx != x1 - 1 && zz != z0 && zz != z1 - 1) {
                            yy = y0;
                        }
                    }
                }
            }
            if (detectedWater) continue;

            for (int xx = x0; xx < x1; ++xx) {
                final double xd = (xx + xOffs * 16 + 0.5 - xCave) / rad;
                for (int zz = z0; zz < z1; ++zz) {
                    final double zd = (zz + zOffs * 16 + 0.5 - zCave) / rad;
                    int p = (xx * 16 + zz) * Level.MAX_HEIGHT + y1;
                    boolean hasGrass = false;

                    if (xd * xd + zd * zd < 1.0) {
                        for (int yy = y1 - 1; yy >= y0; --yy) {
                            final double yd = (yy + 0.5 - yCave) / yRad;
                            if (yd > -0.7 && xd * xd + yd * yd + zd * zd < 1.0) {
                                int block = blocks[p];
                                if (block == Tile.grass.id) hasGrass = true;
                                if (block == Tile.rock.id || block == Tile.dirt.id || block == Tile.grass.id) {
                                    if (yy < 10) {
                                        blocks[p] = (byte) Tile.lava.id;
                                    } else {
                                        blocks[p] = 0;
                                        if (hasGrass && blocks[p - 1] == Tile.dirt.id) blocks[p - 1] = (byte) Tile.grass.id;
                                    }
                                }
                            }
                            p--;
                        }
                    }
                }
            }
            if (singleStep) break;
        }
    }
    
    @Override
    protected void addFeature(final Level level, final int x, final int z, final int xOffs, final int zOffs, final byte[] blocks) {
        int caves = this.random.nextInt(this.random.nextInt(this.random.nextInt(40) + 1) + 1);
        if (this.random.nextInt(15) != 0) caves = 0;

        for (int cave = 0; cave < caves; ++cave) {
            final double xCave = x * 16 + this.random.nextInt(16);
            final double yCave = this.random.nextInt(this.random.nextInt(120) + 8);
            final double zCave = z * 16 + this.random.nextInt(16);

            int tunnels = 1;
            if (this.random.nextInt(4) == 0) {
                this.addRoom(xOffs, zOffs, blocks, xCave, yCave, zCave);
                tunnels += this.random.nextInt(4);
            }

            for (int i = 0; i < tunnels; ++i) {
                float yRot = this.random.nextFloat() * Mth.PI * 2.0f;
                float xRot = (this.random.nextFloat() - 0.5f) * 2.0f / 8.0f;
                float thickness = this.random.nextFloat() * 2.0f + this.random.nextFloat();

                this.addTunnel(xOffs, zOffs, blocks, xCave, yCave, zCave, thickness, yRot, xRot, 0, 0, 1.0);
            }
        }
    }
}
