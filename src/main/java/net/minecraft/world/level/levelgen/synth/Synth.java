// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.synth;

public abstract class Synth {
    // Useless - exists in b1.2 and LCE leaks
    public abstract double getValue(double x, double y);

    // Useless - exists in b1.2 and LCE leaks
    public double[] create(int width, int height) {
        double[] result = new double[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result[x + y * width] = this.getValue(x, y);
            }
        }

        return result;
    }
}
