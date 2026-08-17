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
        this.xd = this.xd * 0.01f + xa;
        this.yd = this.yd * 0.01f + ya;
        this.zd = this.zd * 0.01f + za;
        x += (this.random.nextFloat() - this.random.nextFloat()) * 0.05f;
        y += (this.random.nextFloat() - this.random.nextFloat()) * 0.05f;
        z += (this.random.nextFloat() - this.random.nextFloat()) * 0.05f;

        this.oSize = this.size;
        this.rCol = this.gCol = this.bCol = 1.0f;

        this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
        this.noPhysics = true;
        this.tex = 48;
    }
    
    @Override
    public void render(final Tesselator t, final float partialTick, final float xa, final float ya, final float za, final float xa2, final float za2) {
        final float s = (this.age + partialTick) / this.lifetime;
        this.size = this.oSize * (1.0f - s * s * 0.5f);
        super.render(t, partialTick, xa, ya, za, xa2, za2);
    }
    
    @Override
    public float getBrightness(final float partialTick) {
        float l = (this.age + partialTick) / this.lifetime;
        if (l < 0.0f) l = 0.0f;
        if (l > 1.0f) l = 1.0f;
        float br = super.getBrightness(partialTick);

        return br * l + (1.0f - l);
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) this.remove();

        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.96f;
        this.yd *= 0.96f;
        this.zd *= 0.96f;

        if (this.onGround) {
            this.xd *= 0.7f;
            this.zd *= 0.7f;
        }
    }
}
