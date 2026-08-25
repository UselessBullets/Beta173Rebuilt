// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.synth;

import java.util.Random;

// Useless - ImprovedNoise implementation as described here: https://mrl.cs.nyu.edu/~perlin/noise/
public class ImprovedNoise extends Synth
{
    private int[] p;
    public double xo, yo, zo;
    
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

        for (int i = 0; i < 256; ++i) {
            final int j = random.nextInt(256 - i) + i;
            final int tmp = this.p[i];
            this.p[i] = this.p[j];
            this.p[j] = tmp;

            this.p[i + 256] = this.p[i];
        }
    }
    
    public double noise(final double _x, final double _y, final double _z) {
        double x = _x + this.xo;
        double y = _y + this.yo;
        double z = _z + this.zo;

        int xf = (int)x;
        int yf = (int)y;
        int zf = (int)z;

        if (x < xf) --xf;
        if (y < yf) --yf;
        if (z < zf) --zf;

        int X = xf & 0xFF, // FIND UNIT CUBE THAT
            Y = yf & 0xFF, // CONTAINS POINT.
            Z = zf & 0xFF;

        x -= xf; // FIND RELATIVE X,Y,Z
        y -= yf; // OF POINT IN CUBE.
        z -= zf;

        final double u = x * x * x * (x * (x * 6.0 - 15.0) + 10.0); // COMPUTE FADE CURVES
        final double v = y * y * y * (y * (y * 6.0 - 15.0) + 10.0); // FOR EACH OF X,Y,Z.
        final double w = z * z * z * (z * (z * 6.0 - 15.0) + 10.0);

        int A = this.p[X] + Y, AA = this.p[A] + Z, AB = this.p[A + 1] + Z, // HASH COORDINATES OF
        B = this.p[X + 1] + Y, BA = this.p[B] + Z, BB = this.p[B + 1] + Z; // THE 8 CUBE CORNERS,

        return this.lerp(w, this.lerp(v, this.lerp(u, this.grad(this.p[AA], x,           y,       z      ),  // AND ADD
                                                      this.grad(this.p[BA], x - 1.0,     y,       z      )), // BLENDED
                                         this.lerp(u, this.grad(this.p[AB], x,           y - 1.0, z      ),  // RESULTS
                                                      this.grad(this.p[BB], x - 1.0,     y - 1.0, z      ))),// FROM  8
                            this.lerp(v, this.lerp(u, this.grad(this.p[AA + 1], x,       y,       z - 1.0),  // CORNERS
                                                      this.grad(this.p[BA + 1], x - 1.0, y,       z - 1.0)), // OF CUBE
                                         this.lerp(u, this.grad(this.p[AB + 1], x,       y - 1.0, z - 1.0),
                                                      this.grad(this.p[BB + 1], x - 1.0, y - 1.0, z - 1.0))));
    }
    
    public final double lerp(final double t, final double a, final double b) {
        return a + t * (b - a);
    }
    
    public final double grad2(final int hash, final double x, final double z) {
        int h = hash & 0xF;                                         // CONVERT LO 4 BITS OF HASH CODE
        double u = (1 - ((h & 0x8) >> 3)) * x,                      // INTO 12 GRADIENT DIRECTIONS.
               v = (h < 4) ? 0.0 : ((h == 12 || h == 14) ? x : z);

        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
    
    public final double grad(final int hash, final double x, final double y, final double z) {
        int h = hash & 0xF;                                         // CONVERT LO 4 BITS OF HASH CODE
        double u = (h < 8) ? x : y,                                 // INTO 12 GRADIENT DIRECTIONS.
               v = (h < 4) ? y : ((h == 12 || h == 14) ? x : z);

        return (((h & 0x1) == 0x0) ? u : (-u)) + (((h & 0x2) == 0x0) ? v : (-v));
    }

    @Override
    public double getValue(final double x, final double y) {
        return this.noise(x, y, 0.0);
    }

    // Useless - Exists in b1.2 and LCE leaks
    public double getValue(double x, double y, double z) {
        return this.noise(x, y, z);
    }
    
    public void add(final double[] buffer, final double _x, final double _y, final double _z, final int xSize, final int ySize, final int zSize, final double xs, final double ys, final double zs, final double pow) {
        if (ySize == 1) {
            int A = 0, AA = 0, B = 0, BA = 0;
            double vv0 = 0, vv2 = 0;
            int pp = 0;
            final double scale = 1.0 / pow;
            for (int xx = 0; xx < xSize; ++xx) {
                double x = (_x + xx) * xs + this.xo;
                int xf = (int)x;
                if (x < xf) xf--;
                final int X = xf & 0xFF;
                x -= xf;
                final double u = x * x * x * (x * (x * 6.0 - 15.0) + 10.0);

                for (int zz = 0; zz < zSize; ++zz) {
                    double z = (_z + zz) * zs + this.zo;
                    int zf = (int)z;
                    if (z < zf) zf--;
                    final int Z = zf & 0xFF;
                    z -= zf;
                    final double w = z * z * z * (z * (z * 6.0 - 15.0) + 10.0);

                    A = this.p[X] + 0;
                    AA = this.p[A] + Z;
                    B = this.p[X + 1] + 0;
                    BA = this.p[B] + Z;
                    vv0 = this.lerp(u, this.grad2(this.p[AA], x, z), this.grad(this.p[BA], x - 1.0, 0.0, z));
                    vv2 = this.lerp(u, this.grad(this.p[AA + 1], x, 0.0, z - 1.0), this.grad(this.p[BA + 1], x - 1.0, 0.0, z - 1.0));

                    double val = this.lerp(w, vv0, vv2);

                    buffer[pp++] += val * scale;
                }
            }
            return;
        }
        int pp = 0;
        double scale = 1.0 / pow;
        int yOld = -1;
        int A = 0, AA = 0, AB = 0, B = 0, BA = 0, BB = 0;
        double vv0 = 0, vv1 = 0, vv2 = 0, vv3 = 0;

        for (int xx = 0; xx < xSize; ++xx) {
            double x = (_x + xx) * xs + this.xo;
            int xf = (int)x;
            if (x < xf) xf--;
            final int X = xf & 0xFF;
            x -= xf;
            final double u = x * x * x * (x * (x * 6.0 - 15.0) + 10.0);

            for (int zz = 0; zz < zSize; ++zz) {
                double z = (_z + zz) * zs + this.zo;
                int zf = (int)z;
                if (z < zf) zf--;
                final int Z = zf & 0xFF;
                z -= zf;
                final double w = z * z * z * (z * (z * 6.0 - 15.0) + 10.0);

                for (int yy = 0; yy < ySize; ++yy) {
                    double y = (_y + yy) * ys + this.yo;
                    int yf = (int)y;
                    if (y < yf) yf--;
                    final int Y = yf & 0xFF;
                    y -= yf;
                    final double v = y * y * y * (y * (y * 6.0 - 15.0) + 10.0);

                    if (yy == 0 || Y != yOld) {
                        yOld = Y;
                        A = this.p[X] + Y;
                        AA = this.p[A] + Z;
                        AB = this.p[A + 1] + Z;
                        B = this.p[X + 1] + Y;
                        BA = this.p[B] + Z;
                        BB = this.p[B + 1] + Z;
                        vv0 = this.lerp(u, this.grad(this.p[AA], x, y, z), this.grad(this.p[BA], x - 1.0, y, z));
                        vv1 = this.lerp(u, this.grad(this.p[AB], x, y - 1.0, z), this.grad(this.p[BB], x - 1.0, y - 1.0, z));
                        vv2 = this.lerp(u, this.grad(this.p[AA + 1], x, y, z - 1.0), this.grad(this.p[BA + 1], x - 1.0, y, z - 1.0));
                        vv3 = this.lerp(u, this.grad(this.p[AB + 1], x, y - 1.0, z - 1.0), this.grad(this.p[BB + 1], x - 1.0, y - 1.0, z - 1.0));
                    }

                    double v0 = this.lerp(v, vv0, vv1);
                    double v1 = this.lerp(v, vv2, vv3);
                    final double val = this.lerp(w, v0, v1);

                    buffer[pp++] += val * scale;
                }
            }
        }
    }
}
