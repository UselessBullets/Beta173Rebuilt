// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.Level;

public class SnowShovelParticle extends Particle
{
    float oSize;
    
    public SnowShovelParticle(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za) {
        this(level, x, y, z, xa, ya, za, 1.0f);
    }
    
    public SnowShovelParticle(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za, final float scale) {
        super(level, x, y, z, xa, ya, za);
        this.xd *= 0.1f;
        this.yd *= 0.1f;
        this.zd *= 0.1f;
        this.xd += xa;
        this.yd += ya;
        this.zd += za;

        this.rCol = this.gCol = this.bCol = 1.0f - (float)(Math.random() * 0.3f);
        this.size *= 0.75f;
        this.size *= scale;
        this.oSize = this.size;

        this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.lifetime *= (int)scale;
        this.noPhysics = false;
    }
    
    @Override
    public void render(final Tesselator t, final float a, final float xa, final float ya, final float za, final float xa2, final float za2) {
        float l = (this.age + a) / this.lifetime * 32.0f;
        if (l < 0.0f) l = 0.0f;
        if (l > 1.0f) l = 1.0f;

        this.size = this.oSize * l;
        super.render(t, a, xa, ya, za, xa2, za2);
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) this.remove();

        this.tex = 7 - this.age * 8 / this.lifetime;

        this.yd -= 0.03;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.99f;
        this.yd *= 0.99f;
        this.zd *= 0.99f;

        if (this.onGround) {
            this.xd *= 0.7f;
            this.zd *= 0.7f;
        }
    }
}
