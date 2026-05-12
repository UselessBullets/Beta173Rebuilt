// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

import net.minecraft.world.level.tile.Tile;

public class LightUpdate
{
    public final LightLayer layer;
    public int x0;
    public int y0;
    public int z0;
    public int x1;
    public int y1;
    public int z1;
    
    public LightUpdate(final LightLayer layer, final int x0, final int y0, final int z0, final int x1, final int y1, final int z1) {
        this.layer = layer;
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
    }
    
    public void update(final Level level) {
        if ((this.x1 - this.x0 + 1) * (this.y1 - this.y0 + 1) * (this.z1 - this.z0 + 1) > 32768) {
            System.out.println("Light too large, skipping!");
            return;
        }
        int n = 0;
        int n2 = 0;
        final boolean b = false;
        int n3 = 0;
        for (int i = this.x0; i <= this.x1; ++i) {
            for (int j = this.z0; j <= this.z1; ++j) {
                final int n4 = i >> 4;
                final int n5 = j >> 4;
                int hasChunks;
                if (b && n4 == n && n5 == n2) {
                    hasChunks = n3;
                }
                else {
                    hasChunks = (level.hasChunksAt(i, 0, j, 1) ? 1 : 0);
                    if (hasChunks != 0 && level.getChunk(i >> 4, j >> 4).isEmpty()) {
                        hasChunks = 0;
                    }
                    n3 = hasChunks;
                    n = n4;
                    n2 = n5;
                }
                if (hasChunks != 0) {
                    if (this.y0 < 0) {
                        this.y0 = 0;
                    }
                    if (this.y1 >= 128) {
                        this.y1 = 127;
                    }
                    for (int k = this.y0; k <= this.y1; ++k) {
                        final int brightness = level.getBrightness(this.layer, i, k, j);
                        final int tile = level.getTile(i, k, j);
                        int n6 = Tile.lightBlock[tile];
                        if (n6 == 0) {
                            n6 = 1;
                        }
                        int n7 = 0;
                        if (this.layer == LightLayer.Sky) {
                            if (level.isSkyLit(i, k, j)) {
                                n7 = 15;
                            }
                        }
                        else if (this.layer == LightLayer.Block) {
                            n7 = Tile.lightEmission[tile];
                        }
                        int brightness2;
                        if (n6 >= 15 && n7 == 0) {
                            brightness2 = 0;
                        }
                        else {
                            final int brightness3 = level.getBrightness(this.layer, i - 1, k, j);
                            final int brightness4 = level.getBrightness(this.layer, i + 1, k, j);
                            final int brightness5 = level.getBrightness(this.layer, i, k - 1, j);
                            final int brightness6 = level.getBrightness(this.layer, i, k + 1, j);
                            final int brightness7 = level.getBrightness(this.layer, i, k, j - 1);
                            final int brightness8 = level.getBrightness(this.layer, i, k, j + 1);
                            int n8 = brightness3;
                            if (brightness4 > n8) {
                                n8 = brightness4;
                            }
                            if (brightness5 > n8) {
                                n8 = brightness5;
                            }
                            if (brightness6 > n8) {
                                n8 = brightness6;
                            }
                            if (brightness7 > n8) {
                                n8 = brightness7;
                            }
                            if (brightness8 > n8) {
                                n8 = brightness8;
                            }
                            brightness2 = n8 - n6;
                            if (brightness2 < 0) {
                                brightness2 = 0;
                            }
                            if (n7 > brightness2) {
                                brightness2 = n7;
                            }
                        }
                        if (brightness != brightness2) {
                            level.setBrightness(this.layer, i, k, j, brightness2);
                            int n9 = brightness2 - 1;
                            if (n9 < 0) {
                                n9 = 0;
                            }
                            level.updateLightIfOtherThan(this.layer, i - 1, k, j, n9);
                            level.updateLightIfOtherThan(this.layer, i, k - 1, j, n9);
                            level.updateLightIfOtherThan(this.layer, i, k, j - 1, n9);
                            if (i + 1 >= this.x1) {
                                level.updateLightIfOtherThan(this.layer, i + 1, k, j, n9);
                            }
                            if (k + 1 >= this.y1) {
                                level.updateLightIfOtherThan(this.layer, i, k + 1, j, n9);
                            }
                            if (j + 1 >= this.z1) {
                                level.updateLightIfOtherThan(this.layer, i, k, j + 1, n9);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public boolean expandToContain(int x0, int y0, int z0, int x2, int y2, int z2) {
        if (x0 >= this.x0 && y0 >= this.y0 && z0 >= this.z0 && x2 <= this.x1 && y2 <= this.y1 && z2 <= this.z1) {
            return true;
        }
        final int n = 1;
        if (x0 >= this.x0 - n && y0 >= this.y0 - n && z0 >= this.z0 - n && x2 <= this.x1 + n && y2 <= this.y1 + n && z2 <= this.z1 + n) {
            final int n2 = this.x1 - this.x0;
            final int n3 = this.y1 - this.y0;
            final int n4 = this.z1 - this.z0;
            if (x0 > this.x0) {
                x0 = this.x0;
            }
            if (y0 > this.y0) {
                y0 = this.y0;
            }
            if (z0 > this.z0) {
                z0 = this.z0;
            }
            if (x2 < this.x1) {
                x2 = this.x1;
            }
            if (y2 < this.y1) {
                y2 = this.y1;
            }
            if (z2 < this.z1) {
                z2 = this.z1;
            }
            if ((x2 - x0) * (y2 - y0) * (z2 - z0) - n2 * n3 * n4 <= 2) {
                this.x0 = x0;
                this.y0 = y0;
                this.z0 = z0;
                this.x1 = x2;
                this.y1 = y2;
                this.z1 = z2;
                return true;
            }
        }
        return false;
    }
}
