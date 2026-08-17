// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.Level;

public class PortalParticle extends Particle
{
    private float oSize;
    private double xStart;
    private double yStart;
    private double zStart;
    
    public PortalParticle(final Level level, final double x, final double y, final double z, final double xd, final double yd, final double zd) {
        super(level, x, y, z, xd, yd, zd);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.xStart = this.x = x;
        this.yStart = this.y = y;
        this.zStart = this.z = z;

        final float br = this.random.nextFloat() * 0.6f + 0.4f;
        this.oSize = this.size = this.random.nextFloat() * 0.2f + 0.5f;
        this.rCol = this.gCol = this.bCol = 1.0f * br;
        this.gCol *= 0.3f;
        this.rCol *= 0.9f;

        // Default colour (0.9f, 0.3f, 1.0f)
        // 0xE64DFF

        this.lifetime = (int)(Math.random() * 10.0) + 40;
        this.noPhysics = true;
        this.tex = (int)(Math.random() * 8.0);
    }
    
    @Override
    public void render(final Tesselator t, final float a, final float xa, final float ya, final float za, final float xa2, final float za2) {
        float s = (this.age + a) / this.lifetime;
        s = 1 - s;
        s = s * s;
        s = 1 - s;
        this.size = this.oSize * s;
        super.render(t, a, xa, ya, za, xa2, za2);
    }
    
    @Override
    public float getBrightness(final float a) {
        final float br = super.getBrightness(a);
        float pos = this.age / (float)this.lifetime;
        pos = pos * pos;
        pos = pos * pos;
        return br * (1.0f - pos) + pos;
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        float pos = this.age / (float)this.lifetime;
        float a = pos;
        pos = -pos + pos * pos * 2.0f;
        pos = 1 - pos;

        this.x = this.xStart + this.xd * pos;
        this.y = this.yStart + this.yd * pos + (1.0f - a);
        this.z = this.zStart + this.zd * pos;

        if (this.age++ >= this.lifetime) this.remove();
    }
}
