// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.lwjgl.opengl.GL11;
import util.Mth;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

public class TakeAnimationParticle extends Particle
{
    private Entity item;
    private Entity target;
    private int life;
    private int lifeTime;
    private float yOffs;
    
    public TakeAnimationParticle(final Level level, final Entity item, final Entity target, final float yOffs) {
        super(level, item.x, item.y, item.z, item.xd, item.yd, item.zd);
        this.life = 0;
        this.lifeTime = 0;
        this.item = item;
        this.target = target;
        this.lifeTime = 3;
        this.yOffs = yOffs;
    }
    
    @Override
    public void render(final Tesselator t, final float partialTick, final float xa, final float ya, final float za, final float xa2, final float za2) {
        final float n = (this.life + partialTick) / this.lifeTime;
        final float n2 = n * n;
        final double x = this.item.x;
        final double y = this.item.y;
        final double z = this.item.z;
        final double n3 = this.target.xOld + (this.target.x - this.target.xOld) * partialTick;
        final double n4 = this.target.yOld + (this.target.y - this.target.yOld) * partialTick + this.yOffs;
        final double n5 = this.target.zOld + (this.target.z - this.target.zOld) * partialTick;
        final double v = x + (n3 - x) * n2;
        final double n6 = y + (n4 - y) * n2;
        final double v2 = z + (n5 - z) * n2;
        final float brightness = this.level.getBrightness(Mth.floor(v), Mth.floor(n6 + this.heightOffset / 2.0f), Mth.floor(v2));
        final double n7 = v - TakeAnimationParticle.xOff;
        final double n8 = n6 - TakeAnimationParticle.yOff;
        final double n9 = v2 - TakeAnimationParticle.zOff;
        GL11.glColor4f(brightness, brightness, brightness, 1.0f);
        EntityRenderDispatcher.instance.render(this.item, (float)n7, (float)n8, (float)n9, this.item.yRot, partialTick);
    }
    
    @Override
    public void tick() {
        ++this.life;
        if (this.life == this.lifeTime) {
            this.remove();
        }
    }
    
    @Override
    public int getParticleTexture() {
        return 3;
    }
}
