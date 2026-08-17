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
    private int life = 0;
    private int lifeTime = 0;
    private float yOffs;
    
    public TakeAnimationParticle(final Level level, final Entity item, final Entity target, final float yOffs) {
        super(level, item.x, item.y, item.z, item.xd, item.yd, item.zd);
        this.item = item;

        this.target = target;
        this.lifeTime = 3;
        this.yOffs = yOffs;
    }
    
    @Override
    public void render(final Tesselator t, final float a, final float xa, final float ya, final float za, final float xa2, final float za2) {
        float time = (this.life + a) / this.lifeTime;
        time = time * time;

        final double xo = this.item.x;
        final double yo = this.item.y;
        final double zo = this.item.z;

        final double xt = this.target.xOld + (this.target.x - this.target.xOld) * a;
        final double yt = this.target.yOld + (this.target.y - this.target.yOld) * a + this.yOffs;
        final double zt = this.target.zOld + (this.target.z - this.target.zOld) * a;

        double xx = xo + (xt - xo) * time;
        double yy = yo + (yt - yo) * time;
        double zz = zo + (zt - zo) * time;

        final float br = this.level.getBrightness(Mth.floor(xx), Mth.floor(yy + this.heightOffset / 2.0f), Mth.floor(zz));
        xx -= TakeAnimationParticle.xOff;
        yy -= TakeAnimationParticle.yOff;
        zz -= TakeAnimationParticle.zOff;

        GL11.glColor4f(br, br, br, 1.0f);
        EntityRenderDispatcher.instance.render(this.item, (float)xx, (float)yy, (float)zz, this.item.yRot, a);
    }
    
    @Override
    public void tick() {
        ++this.life;
        if (this.life == this.lifeTime) this.remove();
    }
    
    @Override
    public int getParticleTexture() {
        return ParticleEngine.ENTITY_PARTICLE_TEXTURE;
    }
}
