// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.Level;

public class RedDustParticle extends Particle
{
    float oSize;
    
    public RedDustParticle(final Level level, final double x, final double y, final double z, final float rCol, final float gCol, final float bCol) {
        this(level, x, y, z, 1.0f, rCol, gCol, bCol);
    }
    
    public RedDustParticle(final Level level, final double x, final double y, final double z, final float scale, float rCol, final float gCol, final float bCol) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.xd *= 0.1f;
        this.yd *= 0.1f;
        this.zd *= 0.1f;

        if (rCol == 0.0f) {
            rCol = 1.0f;
        }

        final float br = (float)Math.random() * 0.4f + 0.6f;
        this.rCol = ((float)(Math.random() * 0.2f) + 0.8f) * rCol * br;
        this.gCol = ((float)(Math.random() * 0.2f) + 0.8f) * gCol * br;
        this.bCol = ((float)(Math.random() * 0.2f) + 0.8f) * bCol * br;
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
