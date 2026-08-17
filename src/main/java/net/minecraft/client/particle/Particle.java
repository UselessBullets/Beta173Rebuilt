// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import com.mojang.nbt.CompoundTag;
import net.minecraft.client.renderer.Tesselator;
import util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

public class Particle extends Entity
{
    protected int tex;
    protected float uo, vo;
    protected int age = 0;
    protected int lifetime = 0;
    protected float size;
    protected float gravity;
    protected float rCol, gCol, bCol;
    public static double xOff, yOff, zOff;
    
    public Particle(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za) {
        super(level);
        this.setSize(0.2f, 0.2f);
        this.heightOffset = this.bbHeight / 2.0f;
        this.setPos(x, y, z);
        this.rCol = this.gCol = this.bCol = 1.0f;

        this.xd = xa + (float)(Math.random() * 2.0 - 1.0) * 0.4f;
        this.yd = ya + (float)(Math.random() * 2.0 - 1.0) * 0.4f;
        this.zd = za + (float)(Math.random() * 2.0 - 1.0) * 0.4f;
        final float speed = (float)(Math.random() + Math.random() + 1.0) * 0.15f;

        final float dd = Mth.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
        this.xd = this.xd / dd * speed * 0.4f;
        this.yd = this.yd / dd * speed * 0.4f + 0.1f;
        this.zd = this.zd / dd * speed * 0.4f;

        this.uo = this.random.nextFloat() * 3.0f;
        this.vo = this.random.nextFloat() * 3.0f;

        this.size = (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;

        this.lifetime = (int)(4.0f / (this.random.nextFloat() * 0.9f + 0.1f));
    }
    
    public Particle setPower(final float power) {
        this.xd *= power;
        this.yd = (this.yd - 0.1f) * power + 0.1f;
        this.zd *= power;
        return this;
    }
    
    public Particle scale(final float scale) {
        this.setSize(0.2f * scale, 0.2f * scale);
        this.size *= scale;
        return this;
    }
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    protected void definedSynchedData() {
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) this.remove();

        this.yd -= 0.04 * this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.98f;
        this.yd *= 0.98f;
        this.zd *= 0.98f;

        if (this.onGround) {
            this.xd *= 0.7f;
            this.zd *= 0.7f;
        }
    }
    
    public void render(final Tesselator t, final float partialTick, final float xa, final float ya, final float za, final float xa2, final float za2) {
        final float u0 = this.tex % 16 / 16.0f;
        final float u1 = u0 + 0.999f / 16.0f;
        final float v0 = this.tex / 16 / 16.0f;
        final float v1 = v0 + 0.999f / 16.0f;
        final float r = 0.1f * this.size;

        final float x = (float)(this.xo + (this.x - this.xo) * partialTick - Particle.xOff);
        final float y = (float)(this.yo + (this.y - this.yo) * partialTick - Particle.yOff);
        final float z = (float)(this.zo + (this.z - this.zo) * partialTick - Particle.zOff);

        final float br = this.getBrightness(partialTick);

        t.color(this.rCol * br, this.gCol * br, this.bCol * br);
        t.vertexUV(x - xa * r - xa2 * r, y - ya * r, z - za * r - za2 * r, u1, v1);
        t.vertexUV(x - xa * r + xa2 * r, y + ya * r, z - za * r + za2 * r, u1, v0);
        t.vertexUV(x + xa * r + xa2 * r, y + ya * r, z + za * r + za2 * r, u0, v0);
        t.vertexUV(x + xa * r - xa2 * r, y - ya * r, z + za * r - za2 * r, u0, v1);
    }
    
    public int getParticleTexture() {
        return ParticleEngine.MISC_TEXTURE;
    }
    
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
    }
    
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
    }
}
