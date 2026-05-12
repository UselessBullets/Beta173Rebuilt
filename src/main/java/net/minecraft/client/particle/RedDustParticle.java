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
        this.xd *= 0.10000000149011612;
        this.yd *= 0.10000000149011612;
        this.zd *= 0.10000000149011612;
        if (rCol == 0.0f) {
            rCol = 1.0f;
        }
        final float n = (float)Math.random() * 0.4f + 0.6f;
        this.rCol = ((float)(Math.random() * 0.20000000298023224) + 0.8f) * rCol * n;
        this.gCol = ((float)(Math.random() * 0.20000000298023224) + 0.8f) * gCol * n;
        this.bCol = ((float)(Math.random() * 0.20000000298023224) + 0.8f) * bCol * n;
        this.size *= 0.75f;
        this.size *= scale;
        this.oSize = this.size;
        this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.lifetime *= (int)scale;
        this.noPhysics = false;
    }
    
    @Override
    public void render(final Tesselator t, final float partialTick, final float xa, final float ya, final float za, final float xa2, final float za2) {
        float n = (this.age + partialTick) / this.lifetime * 32.0f;
        if (n < 0.0f) {
            n = 0.0f;
        }
        if (n > 1.0f) {
            n = 1.0f;
        }
        this.size = this.oSize * n;
        super.render(t, partialTick, xa, ya, za, xa2, za2);
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        this.tex = 7 - this.age * 8 / this.lifetime;
        this.move(this.xd, this.yd, this.zd);
        if (this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
        }
        this.xd *= 0.9599999785423279;
        this.yd *= 0.9599999785423279;
        this.zd *= 0.9599999785423279;
        if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
        }
    }
}
