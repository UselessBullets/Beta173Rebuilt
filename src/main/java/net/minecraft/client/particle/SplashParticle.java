// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.world.level.Level;

public class SplashParticle extends WaterDropParticle
{
    public SplashParticle(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za) {
        super(level, x, y, z);
        this.gravity = 0.04f;
        ++this.tex;
        if (ya == 0.0 && (xa != 0.0 || za != 0.0)) {
            this.xd = xa;
            this.yd = ya + 0.1;
            this.zd = za;
        }
    }
}
