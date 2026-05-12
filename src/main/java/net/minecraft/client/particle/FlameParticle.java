// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.Level;

public class FlameParticle extends Particle
{
    private float oSize;
    
    public FlameParticle(final Level level, double x, double y, double z, final double xa, final double ya, final double za) {
        super(level, x, y, z, xa, ya, za);
        this.xd = this.xd * 0.009999999776482582 + xa;
        this.yd = this.yd * 0.009999999776482582 + ya;
        this.zd = this.zd * 0.009999999776482582 + za;
        x += (this.random.nextFloat() - this.random.nextFloat()) * 0.05f;
        y += (this.random.nextFloat() - this.random.nextFloat()) * 0.05f;
        z += (this.random.nextFloat() - this.random.nextFloat()) * 0.05f;
        this.oSize = this.size;
        final float rCol = 1.0f;
        this.bCol = rCol;
        this.gCol = rCol;
        this.rCol = rCol;
        this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
        this.noPhysics = true;
        this.tex = 48;
    }
    
    @Override
    public void render(final Tesselator t, final float partialTick, final float xa, final float ya, final float za, final float xa2, final float za2) {
        final float n = (this.age + partialTick) / this.lifetime;
        this.size = this.oSize * (1.0f - n * n * 0.5f);
        super.render(t, partialTick, xa, ya, za, xa2, za2);
    }
    
    @Override
    public float getBrightness(final float partialTick) {
        float n = (this.age + partialTick) / this.lifetime;
        if (n < 0.0f) {
            n = 0.0f;
        }
        if (n > 1.0f) {
            n = 1.0f;
        }
        return super.getBrightness(partialTick) * n + (1.0f - n);
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
        this.xd *= 0.9599999785423279;
        this.yd *= 0.9599999785423279;
        this.zd *= 0.9599999785423279;
        if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
        }
    }
}
