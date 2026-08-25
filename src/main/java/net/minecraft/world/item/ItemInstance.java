// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.level.tile.Tile;

public final class ItemInstance
{
    public int count;
    public int popTime;
    public int id;
    private int auxValue;
    
    public ItemInstance(final Tile tile) {
        this(tile, 1);
    }
    
    public ItemInstance(final Tile tile, final int count) {
        this(tile.id, count, 0);
    }
    
    public ItemInstance(final Tile tile, final int count, final int auxValue) {
        this(tile.id, count, auxValue);
    }
    
    public ItemInstance(final Item item) {
        this(item.id, 1, 0);
    }
    
    public ItemInstance(final Item item, final int count) {
        this(item.id, count, 0);
    }
    
    public ItemInstance(final Item item, final int count, final int auxValue) {
        this(item.id, count, auxValue);
    }
    
    public ItemInstance(final int id, final int count, final int auxValue) {
        this.count = 0;
        this.id = id;
        this.count = count;
        this.auxValue = auxValue;
    }
    
    public ItemInstance(final CompoundTag itemTag) {
        this.count = 0;
        this.load(itemTag);
    }
    
    public ItemInstance remove(final int count) {
        this.count -= count;
        return new ItemInstance(this.id, count, this.auxValue);
    }
    
    public Item getItem() {
        return Item.items[this.id];
    }
    
    public int getIcon() {
        return this.getItem().getIcon(this);
    }
    
    public boolean useOn(final Player player, final Level level, final int x, final int y, final int z, final int face) {
        final boolean useOn = this.getItem().useOn(this, player, level, x, y, z, face);
        if (useOn) {
            player.awardStat(Stats.itemUsed[this.id], 1);
        }
        return useOn;
    }
    
    public float getDestroySpeed(final Tile tile) {
        return this.getItem().getDestroySpeed(this, tile);
    }
    
    public ItemInstance use(final Level level, final Player player) {
        return this.getItem().use(this, level, player);
    }
    
    public CompoundTag save(final CompoundTag compoundTag) {
        compoundTag.putShort("id", (short)this.id);
        compoundTag.putByte("Count", (byte)this.count);
        compoundTag.putShort("Damage", (short)this.auxValue);
        return compoundTag;
    }
    
    public void load(final CompoundTag compoundTag) {
        this.id = compoundTag.getShort("id");
        this.count = compoundTag.getByte("Count");
        this.auxValue = compoundTag.getShort("Damage");
    }
    
    public int getMaxStackSize() {
        return this.getItem().getMaxStackSize();
    }
    
    public boolean isStackable() {
        return this.getMaxStackSize() > 1 && (!this.isDamageableItem() || !this.isDamaged());
    }
    
    public boolean isDamageableItem() {
        return Item.items[this.id].getMaxDamage() > 0;
    }
    
    public boolean isStackedByData() {
        return Item.items[this.id].isStackedByData();
    }
    
    public boolean isDamaged() {
        return this.isDamageableItem() && this.auxValue > 0;
    }
    
    public int getDamageValue() {
        return this.auxValue;
    }
    
    public int getAuxValue() {
        return this.auxValue;
    }
    
    public void setAuxValue(final int value) {
        this.auxValue = value;
    }
    
    public int getMaxDamage() {
        return Item.items[this.id].getMaxDamage();
    }
    
    public void hurt(final int i, final Entity owner) {
        if (!this.isDamageableItem()) {
            return;
        }
        this.auxValue += i;
        if (this.auxValue > this.getMaxDamage()) {
            if (owner instanceof Player) {
                ((Player)owner).awardStat(Stats.itemBroke[this.id], 1);
            }
            --this.count;
            if (this.count < 0) {
                this.count = 0;
            }
            this.auxValue = 0;
        }
    }
    
    public void hurtEnemy(final Mob mob, final Player attacker) {
        if (Item.items[this.id].hurtEnemy(this, mob, attacker)) {
            attacker.awardStat(Stats.itemUsed[this.id], 1);
        }
    }
    
    public void mineBlock(final int tile, final int x, final int y, final int z, final Player owner) {
        if (Item.items[this.id].mineBlock(this, tile, x, y, z, owner)) {
            owner.awardStat(Stats.itemUsed[this.id], 1);
        }
    }
    
    public int getAttackDamage(final Entity entity) {
        return Item.items[this.id].getAttackDamage(entity);
    }
    
    public boolean canDestroySpecial(final Tile tile) {
        return Item.items[this.id].canDestroySpecial(tile);
    }
    
    public void snap(final Player player) {
    }
    
    public void interactEnemy(final Mob mob) {
        Item.items[this.id].interactEnemy(this, mob);
    }
    
    public ItemInstance copy() {
        return new ItemInstance(this.id, this.count, this.auxValue);
    }
    
    public static boolean matches(final ItemInstance a, final ItemInstance b) {
        return (a == null && b == null) || (a != null && b != null && a.matches(b));
    }
    
    private boolean matches(final ItemInstance b) {
        return this.count == b.count && this.id == b.id && this.auxValue == b.auxValue;
    }
    
    public boolean sameItem(final ItemInstance b) {
        return this.id == b.id && this.auxValue == b.auxValue;
    }
    
    public String getDescriptionId() {
        return Item.items[this.id].getDescriptionId(this);
    }
    
    public static ItemInstance clone(final ItemInstance item) {
        return (item == null) ? null : item.copy();
    }
    
    @Override
    public String toString() {
        return this.count + "x" + Item.items[this.id].getDescriptionId() + "@" + this.auxValue;
    }
    
    public void inventoryTick(final Level level, final Entity owner, final int slot, final boolean selected) {
        if (this.popTime > 0) {
            --this.popTime;
        }
        Item.items[this.id].inventoryTick(this, level, owner, slot, selected);
    }
    
    public void onCraftedBy(final Level level, final Player player) {
        player.awardStat(Stats.itemCrafted[this.id], this.count);
        Item.items[this.id].onCraftedBy(this, level, player);
    }
    
    public boolean equals(final ItemInstance ii) {
        return this.id == ii.id && this.count == ii.count && this.auxValue == ii.auxValue;
    }
}
