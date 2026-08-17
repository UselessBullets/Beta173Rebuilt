// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.Level;

public class LavaParticle extends Particle
{
    private float oSize;
    
    public LavaParticle(final Level level, final double x, final double y, final double z) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.xd *= 0.8f;
        this.yd *= 0.8f;
        this.zd *= 0.8f;
        this.yd = this.random.nextFloat() * 0.4f + 0.05f;

        this.rCol = this.gCol = this.bCol = 1.0f;
        this.size *= this.random.nextFloat() * 2.0f + 0.2f;
        this.oSize = this.size;

        this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.noPhysics = false;
        this.tex = 49;
    }
    
    @Override
    public float getBrightness(final float a) {
        return 1.0f;
    }
    
    @Override
    public void render(final Tesselator t, final float a, final float xa, final float ya, final float za, final float xa2, final float za2) {
        final float s = (this.age + a) / this.lifetime;
        this.size = this.oSize * (1.0f - s * s);
        super.render(t, a, xa, ya, za, xa2, za2);
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) this.remove();
        float odds = this.age / (float)this.lifetime;
        if (this.random.nextFloat() > odds) this.level.addParticle("smoke", this.x, this.y, this.z, this.xd, this.yd, this.zd);

        this.yd -= 0.03;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.999f;
        this.yd *= 0.999f;
        this.zd *= 0.999f;

        if (this.onGround) {
            this.xd *= 0.7f;
            this.zd *= 0.7f;
        }
    }
}
