// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.item;

import net.minecraft.SharedConstants;
import net.minecraft.world.item.Item;
import net.minecraft.stats.Achievements;
import net.minecraft.world.entity.player.Player;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.material.Material;
import util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.Entity;

public class ItemEntity extends Entity
{
    private static final int LIFETIME = 5 * 60 * SharedConstants.TICKS_PER_SECOND; // Five miniutes.
    public ItemInstance item;
    private int tickCount;
    public int age = 0;
    public int throwTime;
    private int health = 5;
    public float bobOffs = (float)(Math.random() * Math.PI * 2.0);
    
    public ItemEntity(final Level level, final double x, final double y, final double z, final ItemInstance item) {
        super(level);

        this.setSize(0.25f, 0.25f);
        this.heightOffset = this.bbHeight / 2.0f;
        this.setPos(x, y, z);

        this.item = item;
        this.yRot = (float)(Math.random() * 360.0);

        this.xd = (float)(Math.random() * 0.2f - 0.1f);
        this.yd = 0.2f;
        this.zd = (float)(Math.random() * 0.2f - 0.1f);
    }
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    public ItemEntity(final Level level) {
        super(level);
        this.setSize(0.25f, 0.25f);
        this.heightOffset = this.bbHeight / 2.0f;
    }
    
    @Override
    protected void definedSynchedData() {
    }
    
    @Override
    public void tick() {
        super.tick();

        if (this.throwTime > 0) this.throwTime--;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        this.yd -= 0.04f;

        if (this.level.getMaterial(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z)) == Material.lava) {
            this.yd = 0.2f;
            this.xd = (this.random.nextFloat() - this.random.nextFloat()) * 0.2f;
            this.zd = (this.random.nextFloat() - this.random.nextFloat()) * 0.2f;
            this.level.playSound(this, "random.fizz", 0.4f, 2.0f + this.random.nextFloat() * 0.4f);
        }

        this.checkInTile(this.x, (this.bb.y0 + this.bb.y1) / 2.0, this.z);
        this.move(this.xd, this.yd, this.zd);

        float friction = 0.98f;
        if (this.onGround) {
            friction = 0.6f * 0.98f;
            final int t = this.level.getTile(Mth.floor(this.x), Mth.floor(this.bb.y0) - 1, Mth.floor(this.z));
            if (t > 0) {
                friction = Tile.tiles[t].friction * 0.98f;
            }
        }

        this.xd *= friction;
        this.yd *= 0.98f;
        this.zd *= friction;

        if (this.onGround) {
            this.yd *= -0.5;
        }

        this.tickCount++;
        this.age++;
        if (this.age >= LIFETIME) {
            this.remove();
        }
    }
    
    @Override
    public boolean updateInWaterState() {
        return this.level.checkAndHandleWater(this.bb, Material.water, this);
    }
    
    @Override
    protected void burn(final int dmg) {
        this.hurt(null, dmg);
    }
    
    @Override
    public boolean hurt(final Entity source, final int dmg) {
        this.markHurt();
        this.health -= dmg;
        if (this.health <= 0) {
            this.remove();
        }
        return false;
    }
    
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        compoundTag.putShort("Health", (byte)this.health);
        compoundTag.putShort("Age", (short)this.age);
        compoundTag.putCompound("Item", this.item.save(new CompoundTag()));
    }
    
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        this.health = (compoundTag.getShort("Health") & 0xFF);
        this.age = compoundTag.getShort("Age");
        this.item = new ItemInstance(compoundTag.getCompound("Item"));
    }
    
    @Override
    public void playerTouch(final Player player) {
        if (this.level.isClientSide) return;

        final int orgCount = this.item.count;
        if (this.throwTime == 0 && player.inventory.add(this.item)) {
            if (this.item.id == Tile.treeTrunk.id) player.awardStat(Achievements.mineWood);
            if (this.item.id == Item.leather.id) player.awardStat(Achievements.killCow);

            this.level.playSound(this, "random.pop", 0.2f, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7f + 1.0f) * 2.0f);
            player.take(this, orgCount);
            if (this.item.count <= 0) this.remove();
        }
    }
}
