// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile.entity;

import net.minecraft.world.entity.player.Player;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.Container;

public class ChestTileEntity extends TileEntity implements Container
{
    private ItemInstance[] items = new ItemInstance[9 * 4];
    
    public int getContainerSize() {
        return 9 * 3;
    }
    
    public ItemInstance getItem(final int slot) {
        return this.items[slot];
    }
    
    public ItemInstance removeItem(final int slot, final int count) {
        if (this.items[slot] != null) {
            if (this.items[slot].count <= count) {
                final ItemInstance item = this.items[slot];
                this.items[slot] = null;
                this.setChanged();
                return item;
            } else {
                final ItemInstance i = this.items[slot].remove(count);
                if (this.items[slot].count == 0) this.items[slot] = null;
                this.setChanged();
                return i;
            }
        }
        return null;
    }
    
    public void setItem(final int slot, final ItemInstance item) {
        this.items[slot] = item;
        if (item != null && item.count > this.getMaxStackSize()) item.count = this.getMaxStackSize();
        this.setChanged();
    }
    
    public String getName() {
        return "Chest";
    }
    
    @Override
    public void load(final CompoundTag base) {
        super.load(base);
        final ListTag<CompoundTag> inventoryList = (ListTag<CompoundTag>) base.getList("Items");
        this.items = new ItemInstance[this.getContainerSize()];
        for (int i = 0; i < inventoryList.size(); ++i) {
            final CompoundTag tag = inventoryList.get(i);
            final int slot = tag.getByte("Slot") & 0xFF;
            if (slot >= 0 && slot < this.items.length) this.items[slot] = new ItemInstance(tag);
        }
    }
    
    @Override
    public void save(final CompoundTag base) {
        super.save(base);
        final ListTag<CompoundTag> listTag = new ListTag<>();

        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] != null) {
                final CompoundTag tag = new CompoundTag();
                tag.putByte("Slot", (byte)i);
                this.items[i].save(tag);
                listTag.add(tag);
            }
        }
        base.put("Items", listTag);
    }
    
    public int getMaxStackSize() {
        return Container.LARGE_MAX_STACK_SIZE;
    }
    
    public boolean stillValid(final Player player) {
        if (this.level.getTileEntity(this.x, this.y, this.z) != this) return false;
        if (player.distanceToSqr(this.x + 0.5, this.y + 0.5, this.z + 0.5) > 8 * 8) return false;
        return true;
    }
}
