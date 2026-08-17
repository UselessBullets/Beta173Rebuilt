// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.Level;

public class ExplodeParticle extends Particle
{
    public ExplodeParticle(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za) {
        super(level, x, y, z, xa, ya, za);

        this.xd = xa + (float)(Math.random() * 2.0 - 1.0) * 0.05f;
        this.yd = ya + (float)(Math.random() * 2.0 - 1.0) * 0.05f;
        this.zd = za + (float)(Math.random() * 2.0 - 1.0) * 0.05f;

        this.rCol = this.gCol = this.bCol = this.random.nextFloat() * 0.3f + 0.7f;

        this.size = this.random.nextFloat() * this.random.nextFloat() * 6.0f + 1.0f;

        this.lifetime = (int)(16.0 / (this.random.nextFloat() * 0.8 + 0.2)) + 2;
    }
    
    @Override
    public void render(final Tesselator t, final float partialTick, final float xa, final float ya, final float za, final float xa2, final float za2) {
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
        this.xd *= 0.90f;
        this.yd *= 0.90f;
        this.zd *= 0.90f;

        if (this.onGround) {
            this.xd *= 0.7f;
            this.zd *= 0.7f;
        }
    }
}
