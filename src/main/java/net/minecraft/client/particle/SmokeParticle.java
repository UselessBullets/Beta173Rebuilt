// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.Level;

public class SmokeParticle extends Particle
{
    float oSize;
    
    public SmokeParticle(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za) {
        this(level, x, y, z, xa, ya, za, 1.0f);
    }
    
    public SmokeParticle(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za, final float scale) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.xd *= 0.1f;
        this.yd *= 0.1f;
        this.zd *= 0.1f;
        this.xd += xa;
        this.yd += ya;
        this.zd += za;

        this.rCol = this.gCol = this.bCol = (float)(Math.random() * 0.3f);

        this.size *= 0.75f;
        this.size *= scale;
        this.oSize = this.size;

        this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.lifetime *= (int)scale;
        this.noPhysics = false;
    }
    
    @Override
    public void render(final Tesselator t, final float partialTick, final float xa, final float ya, final float za, final float xa2, final float za2) {
        float l = (this.age + partialTick) / this.lifetime * 32.0f;
        if (l < 0.0f) l = 0.0f;
        if (l > 1.0f) l = 1.0f;

        this.size = this.oSize * l;
        super.render(t, partialTick, xa, ya, za, xa2, za2);
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) this.remove();

        this.tex = 7 - this.age * 8 / this.lifetime;

        this.yd += 0.004;
        this.move(this.xd, this.yd, this.zd);
        if (this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
        }
        this.xd *= 0.96f;
        this.yd *= 0.96f;
        this.zd *= 0.96f;

        if (this.onGround) {
            this.xd *= 0.7f;
            this.zd *= 0.7f;
        }
    }
}
