// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

import net.minecraft.world.level.tile.Tile;

public class LightUpdate
{
    public final LightLayer layer;
    public int b;
    public int c;
    public int d;
    public int e;
    public int f;
    public int g;
    
    public LightUpdate(final LightLayer co, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7) {
        this.layer = co;
        this.b = integer2;
        this.c = integer3;
        this.d = integer4;
        this.e = integer5;
        this.f = integer6;
        this.g = integer7;
    }
    
    public void update(final Level dj) {
        if ((this.e - this.b + 1) * (this.f - this.c + 1) * (this.g - this.d + 1) > 32768) {
            System.out.println("Light too large, skipping!");
            return;
        }
        int n = 0;
        int n2 = 0;
        final boolean b = false;
        int n3 = 0;
        for (int i = this.b; i <= this.e; ++i) {
            for (int j = this.d; j <= this.g; ++j) {
                final int n4 = i >> 4;
                final int n5 = j >> 4;
                int hasChunks;
                if (b && n4 == n && n5 == n2) {
                    hasChunks = n3;
                }
                else {
                    hasChunks = (dj.hasChunksAt(i, 0, j, 1) ? 1 : 0);
                    if (hasChunks != 0 && dj.getChunk(i >> 4, j >> 4).isEmpty()) {
                        hasChunks = 0;
                    }
                    n3 = hasChunks;
                    n = n4;
                    n2 = n5;
                }
                if (hasChunks != 0) {
                    if (this.c < 0) {
                        this.c = 0;
                    }
                    if (this.f >= 128) {
                        this.f = 127;
                    }
                    for (int k = this.c; k <= this.f; ++k) {
                        final int brightness = dj.getBrightness(this.layer, i, k, j);
                        final int tile = dj.getTile(i, k, j);
                        int n6 = Tile.lightBlock[tile];
                        if (n6 == 0) {
                            n6 = 1;
                        }
                        int n7 = 0;
                        if (this.layer == LightLayer.Sky) {
                            if (dj.isSkyLit(i, k, j)) {
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
                            final int brightness3 = dj.getBrightness(this.layer, i - 1, k, j);
                            final int brightness4 = dj.getBrightness(this.layer, i + 1, k, j);
                            final int brightness5 = dj.getBrightness(this.layer, i, k - 1, j);
                            final int brightness6 = dj.getBrightness(this.layer, i, k + 1, j);
                            final int brightness7 = dj.getBrightness(this.layer, i, k, j - 1);
                            final int brightness8 = dj.getBrightness(this.layer, i, k, j + 1);
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
                            dj.setBrightness(this.layer, i, k, j, brightness2);
                            int n9 = brightness2 - 1;
                            if (n9 < 0) {
                                n9 = 0;
                            }
                            dj.updateLightIfOtherThan(this.layer, i - 1, k, j, n9);
                            dj.updateLightIfOtherThan(this.layer, i, k - 1, j, n9);
                            dj.updateLightIfOtherThan(this.layer, i, k, j - 1, n9);
                            if (i + 1 >= this.e) {
                                dj.updateLightIfOtherThan(this.layer, i + 1, k, j, n9);
                            }
                            if (k + 1 >= this.f) {
                                dj.updateLightIfOtherThan(this.layer, i, k + 1, j, n9);
                            }
                            if (j + 1 >= this.g) {
                                dj.updateLightIfOtherThan(this.layer, i, k, j + 1, n9);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public boolean expandToContain(int integer1, int integer2, int integer3, int integer4, int integer5, int integer6) {
        if (integer1 >= this.b && integer2 >= this.c && integer3 >= this.d && integer4 <= this.e && integer5 <= this.f && integer6 <= this.g) {
            return true;
        }
        final int n = 1;
        if (integer1 >= this.b - n && integer2 >= this.c - n && integer3 >= this.d - n && integer4 <= this.e + n && integer5 <= this.f + n && integer6 <= this.g + n) {
            final int n2 = this.e - this.b;
            final int n3 = this.f - this.c;
            final int n4 = this.g - this.d;
            if (integer1 > this.b) {
                integer1 = this.b;
            }
            if (integer2 > this.c) {
                integer2 = this.c;
            }
            if (integer3 > this.d) {
                integer3 = this.d;
            }
            if (integer4 < this.e) {
                integer4 = this.e;
            }
            if (integer5 < this.f) {
                integer5 = this.f;
            }
            if (integer6 < this.g) {
                integer6 = this.g;
            }
            if ((integer4 - integer1) * (integer5 - integer2) * (integer6 - integer3) - n2 * n3 * n4 <= 2) {
                this.b = integer1;
                this.c = integer2;
                this.d = integer3;
                this.e = integer4;
                this.f = integer5;
                this.g = integer6;
                return true;
            }
        }
        return false;
    }
}
