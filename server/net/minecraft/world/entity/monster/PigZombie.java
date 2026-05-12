// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.monster;

import net.minecraft.world.item.Item;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemInstance;

public class PigZombie extends Zombie
{
    private int angryTime;
    private int playAngrySoundIn;
    private static final ItemInstance sword;
    
    public PigZombie(final Level level) {
        super(level);
        this.angryTime = 0;
        this.playAngrySoundIn = 0;
        this.textureName = "/mob/pigzombie.png";
        this.runSpeed = 0.5f;
        this.attackDamage = 5;
        this.fireImmune = true;
    }
    
    @Override
    public void tick() {
        this.runSpeed = ((this.attackTarget != null) ? 0.95f : 0.5f);
        if (this.playAngrySoundIn > 0 && --this.playAngrySoundIn == 0) {
            this.level.playSound(this, "mob.zombiepig.zpigangry", this.getSoundVolume() * 2.0f, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) * 1.8f);
        }
        super.tick();
    }
    
    @Override
    public boolean canSpawn() {
        return this.level.difficulty > 0 && this.level.isUnobstructed(this.bb) && this.level.getCubes(this, this.bb).size() == 0 && !this.level.containsAnyLiquid(this.bb);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putShort("Anger", (short)this.angryTime);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.angryTime = compoundTag.getShort("Anger");
    }
    
    @Override
    protected Entity findAttackTarget() {
        if (this.angryTime == 0) {
            return null;
        }
        return super.findAttackTarget();
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
    }
    
    @Override
    public boolean hurt(final Entity source, final int dmg) {
        if (source instanceof Player) {
            final List entities = this.level.getEntities(this, this.bb.grow(32.0, 32.0, 32.0));
            for (int i = 0; i < entities.size(); ++i) {
                final Entity entity = entities.get(i);
                if (entity instanceof PigZombie) {
                    ((PigZombie)entity).alert(source);
                }
            }
            this.alert(source);
        }
        return super.hurt(source, dmg);
    }
    
    private void alert(final Entity target) {
        this.attackTarget = target;
        this.angryTime = 400 + this.random.nextInt(400);
        this.playAngrySoundIn = this.random.nextInt(40);
    }
    
    @Override
    protected String getAmbientSound() {
        return "mob.zombiepig.zpig";
    }
    
    @Override
    protected String getHurtSound() {
        return "mob.zombiepig.zpighurt";
    }
    
    @Override
    protected String getDeathSound() {
        return "mob.zombiepig.zpigdeath";
    }
    
    @Override
    protected int getDeathLoot() {
        return Item.porkChop_cooked.id;
    }
    
    static {
        sword = new ItemInstance(Item.sword_gold, 1);
    }
}
