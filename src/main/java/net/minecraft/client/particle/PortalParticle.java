// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.Level;

public class PortalParticle extends Particle
{
    private float oSize;
    private double xStart;
    private double yStart;
    private double zStart;
    
    public PortalParticle(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za) {
        super(level, x, y, z, xa, ya, za);
        this.xd = xa;
        this.yd = ya;
        this.zd = za;
        this.x = x;
        this.xStart = x;
        this.y = y;
        this.yStart = y;
        this.z = z;
        this.zStart = z;
        final float n = this.random.nextFloat() * 0.6f + 0.4f;
        final float n2 = this.random.nextFloat() * 0.2f + 0.5f;
        this.size = n2;
        this.oSize = n2;
        final float rCol = 1.0f * n;
        this.bCol = rCol;
        this.gCol = rCol;
        this.rCol = rCol;
        this.gCol *= 0.3f;
        this.rCol *= 0.9f;
        this.lifetime = (int)(Math.random() * 10.0) + 40;
        this.noPhysics = true;
        this.tex = (int)(Math.random() * 8.0);
    }
    
    @Override
    public void render(final Tesselator t, final float partialTick, final float xa, final float ya, final float za, final float xa2, final float za2) {
        final float n = 1.0f - (this.age + partialTick) / this.lifetime;
        this.size = this.oSize * (1.0f - n * n);
        super.render(t, partialTick, xa, ya, za, xa2, za2);
    }
    
    @Override
    public float getBrightness(final float partialTick) {
        final float brightness = super.getBrightness(partialTick);
        final float n = this.age / (float)this.lifetime;
        final float n2 = n * n;
        final float n3 = n2 * n2;
        return brightness * (1.0f - n3) + n3;
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        final float n2;
        final float n = n2 = this.age / (float)this.lifetime;
        final float n3 = 1.0f - (-n + n * n * 2.0f);
        this.x = this.xStart + this.xd * n3;
        this.y = this.yStart + this.yd * n3 + (1.0f - n2);
        this.z = this.zStart + this.zd * n3;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }
}
