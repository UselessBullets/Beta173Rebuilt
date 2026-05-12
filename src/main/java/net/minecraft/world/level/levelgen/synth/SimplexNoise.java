// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.synth;

import java.util.Random;

public class SimplexNoise
{
    private static int[][] grad3;
    private int[] p;
    public double xo;
    public double yo;
    public double zo;
    private static final double F2;
    private static final double G2;
    
    public SimplexNoise() {
        this(new Random());
    }
    
    public SimplexNoise(final Random random) {
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
    
    private static int fastFloor(final double x) {
        return (x > 0.0) ? ((int)x) : ((int)x - 1);
    }
    
    private static double dot(final int[] g, final double x, final double y) {
        return g[0] * x + g[1] * y;
    }
    
    public void add(final double[] buffer, final double x, final double y, final int xSize, final int ySize, final double xs, final double ys, final double pow) {
        int n = 0;
        for (int i = 0; i < xSize; ++i) {
            final double n2 = (x + i) * xs + this.xo;
            for (int j = 0; j < ySize; ++j) {
                final double n3 = (y + j) * ys + this.yo;
                final double n4 = (n2 + n3) * SimplexNoise.F2;
                final int fastFloor = fastFloor(n2 + n4);
                final int fastFloor2 = fastFloor(n3 + n4);
                final double n5 = (fastFloor + fastFloor2) * SimplexNoise.G2;
                final double n6 = fastFloor - n5;
                final double n7 = fastFloor2 - n5;
                final double x2 = n2 - n6;
                final double y2 = n3 - n7;
                int n8;
                int n9;
                if (x2 > y2) {
                    n8 = 1;
                    n9 = 0;
                }
                else {
                    n8 = 0;
                    n9 = 1;
                }
                final double x3 = x2 - n8 + SimplexNoise.G2;
                final double y3 = y2 - n9 + SimplexNoise.G2;
                final double x4 = x2 - 1.0 + 2.0 * SimplexNoise.G2;
                final double y4 = y2 - 1.0 + 2.0 * SimplexNoise.G2;
                final int n10 = fastFloor & 0xFF;
                final int n11 = fastFloor2 & 0xFF;
                final int n12 = this.p[n10 + this.p[n11]] % 12;
                final int n13 = this.p[n10 + n8 + this.p[n11 + n9]] % 12;
                final int n14 = this.p[n10 + 1 + this.p[n11 + 1]] % 12;
                final double n15 = 0.5 - x2 * x2 - y2 * y2;
                double n16;
                if (n15 < 0.0) {
                    n16 = 0.0;
                }
                else {
                    final double n17 = n15 * n15;
                    n16 = n17 * n17 * dot(SimplexNoise.grad3[n12], x2, y2);
                }
                final double n18 = 0.5 - x3 * x3 - y3 * y3;
                double n19;
                if (n18 < 0.0) {
                    n19 = 0.0;
                }
                else {
                    final double n20 = n18 * n18;
                    n19 = n20 * n20 * dot(SimplexNoise.grad3[n13], x3, y3);
                }
                final double n21 = 0.5 - x4 * x4 - y4 * y4;
                double n22;
                if (n21 < 0.0) {
                    n22 = 0.0;
                }
                else {
                    final double n23 = n21 * n21;
                    n22 = n23 * n23 * dot(SimplexNoise.grad3[n14], x4, y4);
                }
                final int n24 = n++;
                buffer[n24] += 70.0 * (n16 + n19 + n22) * pow;
            }
        }
    }
    
    static {
        SimplexNoise.grad3 = new int[][] { { 1, 1, 0 }, { -1, 1, 0 }, { 1, -1, 0 }, { -1, -1, 0 }, { 1, 0, 1 }, { -1, 0, 1 }, { 1, 0, -1 }, { -1, 0, -1 }, { 0, 1, 1 }, { 0, -1, 1 }, { 0, 1, -1 }, { 0, -1, -1 } };
        F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
        G2 = (3.0 - Math.sqrt(3.0)) / 6.0;
    }
}
