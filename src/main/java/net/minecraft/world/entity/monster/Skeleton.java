// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.monster;

import net.minecraft.world.item.Item;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.Entity;
import util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemInstance;

public class Skeleton extends Monster
{
    private static final ItemInstance bow;
    
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
            final float brightness = this.getBrightness(1.0f);
            if (brightness > 0.5f && this.level.canSeeSky(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z)) && this.random.nextFloat() * 30.0f < (brightness - 0.4f) * 2.0f) {
                this.onFire = 300;
            }
        }
        super.aiStep();
    }
    
    @Override
    protected void checkHurtTarget(final Entity target, final float distance) {
        if (distance < 10.0f) {
            final double n = target.x - this.x;
            final double n2 = target.z - this.z;
            if (this.attackTime == 0) {
                final Arrow arrow;
                final Arrow e = arrow = new Arrow(this.level, this);
                arrow.y += 1.399999976158142;
                final double n3 = target.y + target.getHeadHeight() - 0.20000000298023224 - e.y;
                final float n4 = Mth.sqrt(n * n + n2 * n2) * 0.2f;
                this.level.playSound(this, "random.bow", 1.0f, 1.0f / (this.random.nextFloat() * 0.4f + 0.8f));
                this.level.addEntity(e);
                e.shoot(n, n3 + n4, n2, 0.6f, 12.0f);
                this.attackTime = 30;
            }
            this.yRot = (float)(Math.atan2(n2, n) * 180.0 / Math.PI) - 90.0f;
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
        for (int nextInt = this.random.nextInt(3), i = 0; i < nextInt; ++i) {
            this.spawnAtLocation(Item.arrow.id, 1);
        }
        for (int nextInt2 = this.random.nextInt(3), j = 0; j < nextInt2; ++j) {
            this.spawnAtLocation(Item.bone.id, 1);
        }
    }
    
    @Override
    public ItemInstance getCarriedItem() {
        return Skeleton.bow;
    }
    
    static {
        bow = new ItemInstance(Item.bow, 1);
    }
}
