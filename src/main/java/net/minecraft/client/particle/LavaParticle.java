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
        this.xd *= 0.800000011920929;
        this.yd *= 0.800000011920929;
        this.zd *= 0.800000011920929;
        this.yd = this.random.nextFloat() * 0.4f + 0.05f;
        final float rCol = 1.0f;
        this.bCol = rCol;
        this.gCol = rCol;
        this.rCol = rCol;
        this.size *= this.random.nextFloat() * 2.0f + 0.2f;
        this.oSize = this.size;
        this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.noPhysics = false;
        this.tex = 49;
    }
    
    @Override
    public float getBrightness(final float partialTick) {
        return 1.0f;
    }
    
    @Override
    public void render(final Tesselator t, final float partialTick, final float xa, final float ya, final float za, final float xa2, final float za2) {
        final float n = (this.age + partialTick) / this.lifetime;
        this.size = this.oSize * (1.0f - n * n);
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
        if (this.random.nextFloat() > this.age / (float)this.lifetime) {
            this.level.addParticle("smoke", this.x, this.y, this.z, this.xd, this.yd, this.zd);
        }
        this.yd -= 0.03;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.9990000128746033;
        this.yd *= 0.9990000128746033;
        this.zd *= 0.9990000128746033;
        if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
        }
    }
}
