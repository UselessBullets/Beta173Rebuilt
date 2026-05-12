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
    protected float uo;
    protected float vo;
    protected int age;
    protected int lifetime;
    protected float size;
    protected float gravity;
    protected float rCol;
    protected float gCol;
    protected float bCol;
    public static double xOff;
    public static double yOff;
    public static double zOff;
    
    public Particle(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za) {
        super(level);
        this.age = 0;
        this.lifetime = 0;
        this.setSize(0.2f, 0.2f);
        this.heightOffset = this.bbHeight / 2.0f;
        this.setPos(x, y, z);
        final float rCol = 1.0f;
        this.bCol = rCol;
        this.gCol = rCol;
        this.rCol = rCol;
        this.xd = xa + (float)(Math.random() * 2.0 - 1.0) * 0.4f;
        this.yd = ya + (float)(Math.random() * 2.0 - 1.0) * 0.4f;
        this.zd = za + (float)(Math.random() * 2.0 - 1.0) * 0.4f;
        final float n = (float)(Math.random() + Math.random() + 1.0) * 0.15f;
        final float sqrt = Mth.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
        this.xd = this.xd / sqrt * n * 0.4000000059604645;
        this.yd = this.yd / sqrt * n * 0.4000000059604645 + 0.10000000149011612;
        this.zd = this.zd / sqrt * n * 0.4000000059604645;
        this.uo = this.random.nextFloat() * 3.0f;
        this.vo = this.random.nextFloat() * 3.0f;
        this.size = (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
        this.lifetime = (int)(4.0f / (this.random.nextFloat() * 0.9f + 0.1f));
        this.age = 0;
    }
    
    public Particle setPower(final float power) {
        this.xd *= power;
        this.yd = (this.yd - 0.10000000149011612) * power + 0.10000000149011612;
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
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        this.yd -= 0.04 * this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.9800000190734863;
        this.yd *= 0.9800000190734863;
        this.zd *= 0.9800000190734863;
        if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
        }
    }
    
    public void render(final Tesselator t, final float partialTick, final float xa, final float ya, final float za, final float xa2, final float za2) {
        final float n = this.tex % 16 / 16.0f;
        final float n2 = n + 0.0624375f;
        final float n3 = this.tex / 16 / 16.0f;
        final float n4 = n3 + 0.0624375f;
        final float n5 = 0.1f * this.size;
        final float n6 = (float)(this.xo + (this.x - this.xo) * partialTick - Particle.xOff);
        final float n7 = (float)(this.yo + (this.y - this.yo) * partialTick - Particle.yOff);
        final float n8 = (float)(this.zo + (this.z - this.zo) * partialTick - Particle.zOff);
        final float brightness = this.getBrightness(partialTick);
        t.color(this.rCol * brightness, this.gCol * brightness, this.bCol * brightness);
        t.vertexUV(n6 - xa * n5 - xa2 * n5, n7 - ya * n5, n8 - za * n5 - za2 * n5, n2, n4);
        t.vertexUV(n6 - xa * n5 + xa2 * n5, n7 + ya * n5, n8 - za * n5 + za2 * n5, n2, n3);
        t.vertexUV(n6 + xa * n5 + xa2 * n5, n7 + ya * n5, n8 + za * n5 + za2 * n5, n, n3);
        t.vertexUV(n6 + xa * n5 - xa2 * n5, n7 - ya * n5, n8 + za * n5 - za2 * n5, n, n4);
    }
    
    public int getParticleTexture() {
        return 0;
    }
    
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
    }
    
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
    }
}
