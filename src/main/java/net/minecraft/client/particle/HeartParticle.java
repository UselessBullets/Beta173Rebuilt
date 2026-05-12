// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.Level;

public class HeartParticle extends Particle
{
    float oSize;
    
    public HeartParticle(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za) {
        this(level, x, y, z, xa, ya, za, 2.0f);
    }
    
    public HeartParticle(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za, final float scale) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.xd *= 0.009999999776482582;
        this.yd *= 0.009999999776482582;
        this.zd *= 0.009999999776482582;
        this.yd += 0.1;
        this.size *= 0.75f;
        this.size *= scale;
        this.oSize = this.size;
        this.lifetime = 16;
        this.noPhysics = false;
        this.tex = 80;
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
        this.move(this.xd, this.yd, this.zd);
        if (this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
        }
        this.xd *= 0.8600000143051147;
        this.yd *= 0.8600000143051147;
        this.zd *= 0.8600000143051147;
        if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
        }
    }
}
