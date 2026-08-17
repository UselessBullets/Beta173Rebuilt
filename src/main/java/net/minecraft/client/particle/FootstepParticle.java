// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import util.Mth;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.Level;
import net.minecraft.client.renderer.Textures;

import static org.lwjgl.opengl.GL11.*;

public class FootstepParticle extends Particle
{
    private int life = 0;
    private int lifeTime = 0;
    private Textures textures;
    
    public FootstepParticle(final Textures textures, final Level level, final double x, final double y, final double z) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.textures = textures;
        this.xd = this.yd = this.zd = 0.0;
        this.lifeTime = 200;
    }
    
    @Override
    public void render(final Tesselator t, final float a, final float xa, final float ya, final float za, final float xa2, final float za2) {
        float time = (this.life + a) / this.lifeTime;
        time = time * time;

        float alpha = 2.0f - time * 2.0f;
        if (alpha > 1.0f) alpha = 1.0f;
        alpha = alpha * 0.2f;

        GL11.glDisable(GL_LIGHTING);
        final float r = 2 / 16.0f;

        final float xx = (float)(this.x - FootstepParticle.xOff);
        final float yy = (float)(this.y - FootstepParticle.yOff);
        final float zz = (float)(this.z - FootstepParticle.zOff);

        final float br = this.level.getBrightness(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z));

        this.textures.bind(this.textures.loadTexture("/misc/footprint.png"));
        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        t.begin();
        t.color(br, br, br, alpha);
        t.vertexUV(xx - r, yy, zz + r, 0.0, 1.0);
        t.vertexUV(xx + r, yy, zz + r, 1.0, 1.0);
        t.vertexUV(xx + r, yy, zz - r, 1.0, 0.0);
        t.vertexUV(xx - r, yy, zz - r, 0.0, 0.0);
        t.end();

        GL11.glDisable(GL_BLEND);
        GL11.glEnable(GL_LIGHTING);
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
