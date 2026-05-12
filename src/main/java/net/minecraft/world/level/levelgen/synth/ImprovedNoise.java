// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.synth;

import java.util.Random;

public class ImprovedNoise extends Synth
{
    private int[] p;
    public double xo;
    public double yo;
    public double zo;
    
    public ImprovedNoise() {
        this(new Random());
    }
    
    public ImprovedNoise(final Random random) {
        this.p = new int[512];
        this.xo = random.nextDouble() * 256.0;
        this.yo = random.nextDouble() * 256.0;
        this.zo = random.nextDouble() * 256.0;
        for (int i = 0; i < 256; ++i) {
            this.p[i] = i;
        }
        for (int j = 0; j < 256; ++j) {
            final int n = random.nextInt(256 - j) + j;
            final int n2 = this.p[j];
            this.p[j] = this.p[n];
            this.p[n] = n2;
            this.p[j + 256] = this.p[j];
        }
    }
    
    public double noise(final double x, final double y, final double z) {
        final double n = x + this.xo;
        final double n2 = y + this.yo;
        final double n3 = z + this.zo;
        int n4 = (int)n;
        int n5 = (int)n2;
        int n6 = (int)n3;
        if (n < n4) {
            --n4;
        }
        if (n2 < n5) {
            --n5;
        }
        if (n3 < n6) {
            --n6;
        }
        final int n7 = n4 & 0xFF;
        final int n8 = n5 & 0xFF;
        final int n9 = n6 & 0xFF;
        final double n10 = n - n4;
        final double n11 = n2 - n5;
        final double n12 = n3 - n6;
        final double n13 = n10 * n10 * n10 * (n10 * (n10 * 6.0 - 15.0) + 10.0);
        final double n14 = n11 * n11 * n11 * (n11 * (n11 * 6.0 - 15.0) + 10.0);
        final double t = n12 * n12 * n12 * (n12 * (n12 * 6.0 - 15.0) + 10.0);
        final int n15 = this.p[n7] + n8;
        final int n16 = this.p[n15] + n9;
        final int n17 = this.p[n15 + 1] + n9;
        final int n18 = this.p[n7 + 1] + n8;
        final int n19 = this.p[n18] + n9;
        final int n20 = this.p[n18 + 1] + n9;
        return this.lerp(t, this.lerp(n14, this.lerp(n13, this.grad(this.p[n16], n10, n11, n12), this.grad(this.p[n19], n10 - 1.0, n11, n12)), this.lerp(n13, this.grad(this.p[n17], n10, n11 - 1.0, n12), this.grad(this.p[n20], n10 - 1.0, n11 - 1.0, n12))), this.lerp(n14, this.lerp(n13, this.grad(this.p[n16 + 1], n10, n11, n12 - 1.0), this.grad(this.p[n19 + 1], n10 - 1.0, n11, n12 - 1.0)), this.lerp(n13, this.grad(this.p[n17 + 1], n10, n11 - 1.0, n12 - 1.0), this.grad(this.p[n20 + 1], n10 - 1.0, n11 - 1.0, n12 - 1.0))));
    }
    
    public final double lerp(final double t, final double a, final double b) {
        return a + t * (b - a);
    }
    
    public final double grad2(final int hash, final double x, final double z) {
        final int n = hash & 0xF;
        final double n2 = (1 - ((n & 0x8) >> 3)) * x;
        final double n3 = (n < 4) ? 0.0 : ((n == 12 || n == 14) ? x : z);
        return (((n & 0x1) == 0x0) ? n2 : (-n2)) + (((n & 0x2) == 0x0) ? n3 : (-n3));
    }
    
    public final double grad(final int hash, final double x, final double y, final double z) {
        final int n = hash & 0xF;
        final double n2 = (n < 8) ? x : y;
        final double n3 = (n < 4) ? y : ((n == 12 || n == 14) ? x : z);
        return (((n & 0x1) == 0x0) ? n2 : (-n2)) + (((n & 0x2) == 0x0) ? n3 : (-n3));
    }
    
    public double getValue(final double x, final double y) {
        return this.noise(x, y, 0.0);
    }
    
    public void add(final double[] buffer, final double x, final double y, final double z, final int xSize, final int ySize, final int zSize, final double xs, final double ys, final double zs, final double pow) {
        if (ySize == 1) {
            int n = 0;
            final double n2 = 1.0 / pow;
            for (int i = 0; i < xSize; ++i) {
                final double n3 = (x + i) * xs + this.xo;
                int n4 = (int)n3;
                if (n3 < n4) {
                    --n4;
                }
                final int n5 = n4 & 0xFF;
                final double n6 = n3 - n4;
                final double n7 = n6 * n6 * n6 * (n6 * (n6 * 6.0 - 15.0) + 10.0);
                for (int j = 0; j < zSize; ++j) {
                    final double n8 = (z + j) * zs + this.zo;
                    int n9 = (int)n8;
                    if (n8 < n9) {
                        --n9;
                    }
                    final int n10 = n9 & 0xFF;
                    final double n11 = n8 - n9;
                    final double t = n11 * n11 * n11 * (n11 * (n11 * 6.0 - 15.0) + 10.0);
                    final int n12 = this.p[this.p[n5] + 0] + n10;
                    final int n13 = this.p[this.p[n5 + 1] + 0] + n10;
                    final double lerp = this.lerp(t, this.lerp(n7, this.grad2(this.p[n12], n6, n11), this.grad(this.p[n13], n6 - 1.0, 0.0, n11)), this.lerp(n7, this.grad(this.p[n12 + 1], n6, 0.0, n11 - 1.0), this.grad(this.p[n13 + 1], n6 - 1.0, 0.0, n11 - 1.0)));
                    final int n14 = n++;
                    buffer[n14] += lerp * n2;
                }
            }
            return;
        }
        int n15 = 0;
        final double n16 = 1.0 / pow;
        int n17 = -1;
        double lerp2 = 0.0;
        double lerp3 = 0.0;
        double lerp4 = 0.0;
        double lerp5 = 0.0;
        for (int k = 0; k < xSize; ++k) {
            final double n18 = (x + k) * xs + this.xo;
            int n19 = (int)n18;
            if (n18 < n19) {
                --n19;
            }
            final int n20 = n19 & 0xFF;
            final double n21 = n18 - n19;
            final double n22 = n21 * n21 * n21 * (n21 * (n21 * 6.0 - 15.0) + 10.0);
            for (int l = 0; l < zSize; ++l) {
                final double n23 = (z + l) * zs + this.zo;
                int n24 = (int)n23;
                if (n23 < n24) {
                    --n24;
                }
                final int n25 = n24 & 0xFF;
                final double n26 = n23 - n24;
                final double t2 = n26 * n26 * n26 * (n26 * (n26 * 6.0 - 15.0) + 10.0);
                for (int n27 = 0; n27 < ySize; ++n27) {
                    final double n28 = (y + n27) * ys + this.yo;
                    int n29 = (int)n28;
                    if (n28 < n29) {
                        --n29;
                    }
                    final int n30 = n29 & 0xFF;
                    final double n31 = n28 - n29;
                    final double n32 = n31 * n31 * n31 * (n31 * (n31 * 6.0 - 15.0) + 10.0);
                    if (n27 == 0 || n30 != n17) {
                        n17 = n30;
                        final int n33 = this.p[n20] + n30;
                        final int n34 = this.p[n33] + n25;
                        final int n35 = this.p[n33 + 1] + n25;
                        final int n36 = this.p[n20 + 1] + n30;
                        final int n37 = this.p[n36] + n25;
                        final int n38 = this.p[n36 + 1] + n25;
                        lerp2 = this.lerp(n22, this.grad(this.p[n34], n21, n31, n26), this.grad(this.p[n37], n21 - 1.0, n31, n26));
                        lerp3 = this.lerp(n22, this.grad(this.p[n35], n21, n31 - 1.0, n26), this.grad(this.p[n38], n21 - 1.0, n31 - 1.0, n26));
                        lerp4 = this.lerp(n22, this.grad(this.p[n34 + 1], n21, n31, n26 - 1.0), this.grad(this.p[n37 + 1], n21 - 1.0, n31, n26 - 1.0));
                        lerp5 = this.lerp(n22, this.grad(this.p[n35 + 1], n21, n31 - 1.0, n26 - 1.0), this.grad(this.p[n38 + 1], n21 - 1.0, n31 - 1.0, n26 - 1.0));
                    }
                    final double lerp6 = this.lerp(t2, this.lerp(n32, lerp2, lerp3), this.lerp(n32, lerp4, lerp5));
                    final int n39 = n15++;
                    buffer[n39] += lerp6 * n16;
                }
            }
        }
    }
}
