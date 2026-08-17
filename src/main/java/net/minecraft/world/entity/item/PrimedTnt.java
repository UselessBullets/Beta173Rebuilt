// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.item;

import com.mojang.nbt.CompoundTag;
import util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

public class PrimedTnt extends Entity
{
    public int life;
    
    public PrimedTnt(final Level level) {
        super(level);
        this.life = 0;
        this.blocksBuilding = true;
        this.setSize(0.98f, 0.98f);
        this.heightOffset = this.bbHeight / 2.0f;
    }
    
    public PrimedTnt(final Level level, final double xo, final double yo, final double zo) {
        this(level);
        this.setPos(xo, yo, zo);
        final float n = (float)(Math.random() * Math.PI * 2.0);
        this.xd = -Mth.sin(n * Mth.DEGRAD) * 0.02f;
        this.yd = 0.2f;
        this.zd = -Mth.cos(n * Mth.DEGRAD) * 0.02f;
        this.life = 80;
        this.xo = xo;
        this.yo = yo;
        this.zo = zo;
    }
    
    @Override
    protected void definedSynchedData() {
    }
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    public boolean isPickable() {
        return !this.removed;
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.yd -= 0.03999999910593033;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.98f;
        this.yd *= 0.98f;
        this.zd *= 0.98f;
        if (this.onGround) {
            this.xd *= 0.7f;
            this.zd *= 0.7f;
            this.yd *= -0.5;
        }
        if (this.life-- <= 0) {
            if (!this.level.isClientSide) {
                this.remove();
                this.explode();
            }
            else {
                this.remove();
            }
        }
        else {
            this.level.addParticle("smoke", this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
        }
    }
    
    private void explode() {
        this.level.explode(null, this.x, this.y, this.z, 4.0f);
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        compoundTag.putByte("Fuse", (byte)this.life);
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        this.life = compoundTag.getByte("Fuse");
    }
    
    @Override
    public float getShadowHeightOffs() {
        return 0.0f;
    }
}
