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
    private int life;
    private int lifeTime;
    private Textures textures;
    
    public FootstepParticle(final Textures textures, final Level level, final double x, final double y, final double z) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.life = 0;
        this.lifeTime = 0;
        this.textures = textures;
        final double xd = 0.0;
        this.zd = xd;
        this.yd = xd;
        this.xd = xd;
        this.lifeTime = 200;
    }
    
    @Override
    public void render(final Tesselator t, final float partialTick, final float xa, final float ya, final float za, final float xa2, final float za2) {
        final float n = (this.life + partialTick) / this.lifeTime;
        float n2 = 2.0f - n * n * 2.0f;
        if (n2 > 1.0f) {
            n2 = 1.0f;
        }
        final float a = n2 * 0.2f;
        GL11.glDisable(GL_LIGHTING);
        final float n3 = 0.125f;
        final float n4 = (float)(this.x - FootstepParticle.xOff);
        final float n5 = (float)(this.y - FootstepParticle.yOff);
        final float n6 = (float)(this.z - FootstepParticle.zOff);
        final float brightness = this.level.getBrightness(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z));
        this.textures.bind(this.textures.loadTexture("/misc/footprint.png"));
        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(770, 771);
        t.begin();
        t.color(brightness, brightness, brightness, a);
        t.vertexUV(n4 - n3, n5, n6 + n3, 0.0, 1.0);
        t.vertexUV(n4 + n3, n5, n6 + n3, 1.0, 1.0);
        t.vertexUV(n4 + n3, n5, n6 - n3, 1.0, 0.0);
        t.vertexUV(n4 - n3, n5, n6 - n3, 0.0, 0.0);
        t.end();
        GL11.glDisable(3042);
        GL11.glDisable(GL_LIGHTING);
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
        return ParticleEngine.ENTITY_PARTICLE_TEXTURE;
    }
}
