// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.monster;

import net.minecraft.SharedConstants;
import net.minecraft.world.item.Item;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.Entity;
import util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemInstance;

public class Skeleton extends Monster
{
    private static final ItemInstance bow = new ItemInstance(Item.bow, 1);
    
    public Skeleton(final Level level) {
        super(level);
        this.textureName = "/mob/skeleton.png";
    }
    
    @Override
    protected String getAmbientSound() {
        return "mob.skeleton";
    }
    
    @Override
    protected String getHurtSound() {
        return "mob.skeletonhurt";
    }
    
    @Override
    protected String getDeathSound() {
        return "mob.skeletonhurt";
    }
    
    @Override
    public void aiStep() {
        if (this.level.isDay()) {
            final float br = this.getBrightness(1.0f);
            if (br > 0.5f)
                if (this.level.canSeeSky(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z)) && this.random.nextFloat() * 30.0f < (br - 0.4f) * 2.0f) {
                    this.onFire = 8 * SharedConstants.TICKS_PER_SECOND;
                }
        }

        super.aiStep();
    }
    
    @Override
    protected void checkHurtTarget(final Entity target, final float distance) {
        if (distance < 10.0f) {
            final double xd = target.x - this.x;
            final double zd = target.z - this.z;
            if (this.attackTime == 0) {
                final Arrow arrow = new Arrow(this.level, this);

                arrow.y += 1.4f;
                final double yd = target.y + target.getHeadHeight() - 0.2f - arrow.y;

                float sd = Mth.sqrt(xd * xd + zd * zd);
                final float yo = sd * 0.2f;

                this.level.playSound(this, "random.bow", 1.0f, 1.0f / (this.random.nextFloat() * 0.4f + 0.8f));
                this.level.addEntity(arrow);

                arrow.shoot(xd, yd + yo, zd, 0.6f, 12.0f);
                this.attackTime = 30;
            }
            this.yRot = (float)(Math.atan2(zd, xd) * 180.0 / Math.PI) - 90.0f;
            this.holdGround = true;
        }
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
    }
    
    @Override
    protected int getDeathLoot() {
        return Item.arrow.id;
    }
    
    @Override
    protected void dropDeathLoot() {
        // drop some arrows
        int count = this.random.nextInt(3);
        for (int i = 0; i < count; ++i) {
            this.spawnAtLocation(Item.arrow.id, 1);
        }
        // and some bones
        count = this.random.nextInt(3);
        for (int i = 0; i < count; ++i) {
            this.spawnAtLocation(Item.bone.id, 1);
        }
    }
    
    @Override
    public ItemInstance getCarriedItem() {
        return Skeleton.bow;
    }

}
