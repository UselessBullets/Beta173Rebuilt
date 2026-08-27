// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile.entity;

import net.minecraft.SharedConstants;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.item.crafting.FurnaceRecipes;
import net.minecraft.world.level.tile.FurnaceTile;
import com.mojang.nbt.Tag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.Container;

public class FurnaceTileEntity extends TileEntity implements Container
{
    private static final int BURN_INTERVAL = SharedConstants.TICKS_PER_SECOND * 10;
    private static final int INPUT_SLOT = 0;
    private static final int FUEL_SLOT = 1;
    private static final int RESULT_SLOT = 2;
    private ItemInstance[] items = new ItemInstance[3];
    public int litTime = 0;
    public int litDuration = 0;
    public int tickCount = 0;
    
    public int getContainerSize() {
        return this.items.length;
    }
    
    public ItemInstance getItem(final int slot) {
        return this.items[slot];
    }
    
    public ItemInstance removeItem(final int slot, final int count) {
        if (this.items[slot] != null) {
            if (this.items[slot].count <= count) {
                final ItemInstance item = this.items[slot];
                this.items[slot] = null;
                return item;
            } else {
                final ItemInstance i = this.items[slot].remove(count);
                if (this.items[slot].count == 0) this.items[slot] = null;
                return i;
            }
        }
        return null;
    }
    
    public void setItem(final int slot, final ItemInstance item) {
        this.items[slot] = item;
        if (item != null && item.count > this.getMaxStackSize()) item.count = this.getMaxStackSize();
    }
    
    public String getName() {
        return "Furnace";
    }
    
    @Override
    public void load(final CompoundTag compoundTag) {
        super.load(compoundTag);
        final ListTag<CompoundTag> inventoryList = (ListTag<CompoundTag>) compoundTag.getList("Items");
        this.items = new ItemInstance[this.getContainerSize()];
        for (int i = 0; i < inventoryList.size(); ++i) {
            final CompoundTag tag = inventoryList.get(i);
            final byte slot = tag.getByte("Slot");
            if (slot >= 0 && slot < this.items.length) this.items[slot] = new ItemInstance(tag);
        }

        this.litTime = compoundTag.getShort("BurnTime");
        this.tickCount = compoundTag.getShort("CookTime");
        this.litDuration = this.getBurnDuration(this.items[FUEL_SLOT]);
    }
    
    @Override
    public void save(final CompoundTag compoundTag) {
        super.save(compoundTag);
        compoundTag.putShort("BurnTime", (short)this.litTime);
        compoundTag.putShort("CookTime", (short)this.tickCount);
        final ListTag<CompoundTag> listTag = new ListTag<>();

        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] != null) {
                final CompoundTag tag = new CompoundTag();
                tag.putByte("Slot", (byte)i);
                this.items[i].save(tag);
                listTag.add(tag);
            }
        }
        compoundTag.put("Items", listTag);
    }
    
    public int getMaxStackSize() {
        return Container.LARGE_MAX_STACK_SIZE;
    }
    
    public int getBurnProgress(final int max) {
        return this.tickCount * max / BURN_INTERVAL;
    }
    
    public int getLitProgress(final int max) {
        if (this.litDuration == 0) this.litDuration = BURN_INTERVAL;
        return this.litTime * max / this.litDuration;
    }
    
    public boolean isLit() {
        return this.litTime > 0;
    }
    
    @Override
    public void tick() {
        final boolean wasLit = this.litTime > 0;
        boolean changed = false;
        if (this.litTime > 0) {
            --this.litTime;
        }

        if (!this.level.isClientSide) {
            if (this.litTime == 0 && this.canBurn()) {
                this.litDuration = this.litTime = this.getBurnDuration(this.items[FUEL_SLOT]);
                if (this.litTime > 0) {
                    changed = true;
                    if (this.items[FUEL_SLOT] != null) {
                        this.items[FUEL_SLOT].count--;
                        if (this.items[FUEL_SLOT].count == 0) {
                            this.items[FUEL_SLOT] = null;
                        }
                    }
                }
            }
            if (this.isLit() && this.canBurn()) {
                this.tickCount++;
                if (this.tickCount == BURN_INTERVAL) {
                    this.tickCount = 0;
                    this.burn();
                    changed = true;
                }
            }
            else {
                this.tickCount = 0;
            }

            if (wasLit != this.litTime > 0) {
                changed = true;
                FurnaceTile.setLit(this.litTime > 0, this.level, this.x, this.y, this.z);
            }
        }

        if (changed) this.setChanged();
    }
    
    private boolean canBurn() {
        if (this.items[INPUT_SLOT] == null) return false;
        final ItemInstance burnResult = FurnaceRecipes.getInstance().getResult(this.items[INPUT_SLOT].getItem().id);
        if (burnResult == null) return false;
        if (this.items[RESULT_SLOT] == null) return true;
        if (!this.items[RESULT_SLOT].sameItem(burnResult)) return false;
        if (this.items[RESULT_SLOT].count < this.getMaxStackSize() && this.items[RESULT_SLOT].count < this.items[RESULT_SLOT].getMaxStackSize()) return true;
        if (this.items[RESULT_SLOT].count < burnResult.getMaxStackSize()) return true;
        return false;
    }
    
    public void burn() {
        if (!this.canBurn()) return;

        final ItemInstance result = FurnaceRecipes.getInstance().getResult(this.items[INPUT_SLOT].getItem().id);
        if (this.items[RESULT_SLOT] == null) this.items[RESULT_SLOT] = result.copy();
        else if (this.items[RESULT_SLOT].id == result.id) this.items[RESULT_SLOT].count++;

        this.items[INPUT_SLOT].count--;
        if (this.items[INPUT_SLOT].count <= 0) this.items[INPUT_SLOT] = null;
    }
    
    private int getBurnDuration(final ItemInstance itemInstance) {
        if (itemInstance == null) return 0;
        final int id = itemInstance.getItem().id;
        if (id < 256 && Tile.tiles[id].material == Material.wood) return BURN_INTERVAL * 3 / 2;
        if (id == Item.stick.id) return BURN_INTERVAL / 2;
        if (id == Item.coal.id) return BURN_INTERVAL * 8;
        if (id == Item.bucket_lava.id) return BURN_INTERVAL * 100;
        if (id == Tile.sapling.id) return BURN_INTERVAL / 2;
        return 0;
    }
    
    public boolean stillValid(final Player player) {
        if (this.level.getTileEntity(this.x, this.y, this.z) != this) return false;
        if (player.distanceToSqr(this.x + 0.5, this.y + 0.5, this.z + 0.5) > 8 * 8) return false;
        return true;
    }
}
