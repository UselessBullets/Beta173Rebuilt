// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile.entity;

import net.minecraft.world.entity.player.Player;
import com.mojang.nbt.Tag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.Container;

public class ChestTileEntity extends TileEntity implements Container
{
    private ItemInstance[] items;
    
    public ChestTileEntity() {
        this.items = new ItemInstance[36];
    }
    
    public int getContainerSize() {
        return 27;
    }
    
    public ItemInstance getItem(final int slot) {
        return this.items[slot];
    }
    
    public ItemInstance removeItem(final int slot, final int count) {
        if (this.items[slot] == null) {
            return null;
        }
        if (this.items[slot].count <= count) {
            final ItemInstance itemInstance = this.items[slot];
            this.items[slot] = null;
            this.setChanged();
            return itemInstance;
        }
        final ItemInstance remove = this.items[slot].remove(count);
        if (this.items[slot].count == 0) {
            this.items[slot] = null;
        }
        this.setChanged();
        return remove;
    }
    
    public void setItem(final int slot, final ItemInstance item) {
        this.items[slot] = item;
        if (item != null && item.count > this.getMaxStackSize()) {
            item.count = this.getMaxStackSize();
        }
        this.setChanged();
    }
    
    public String getName() {
        return "Chest";
    }
    
    @Override
    public void load(final CompoundTag compoundTag) {
        super.load(compoundTag);
        final ListTag list = compoundTag.getList("Items");
        this.items = new ItemInstance[this.getContainerSize()];
        for (int i = 0; i < list.size(); ++i) {
            final CompoundTag itemTag = (CompoundTag)list.get(i);
            final int n = itemTag.getByte("Slot") & 0xFF;
            if (n >= 0 && n < this.items.length) {
                this.items[n] = new ItemInstance(itemTag);
            }
        }
    }
    
    @Override
    public void save(final CompoundTag compoundTag) {
        super.save(compoundTag);
        final ListTag tag = new ListTag();
        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] != null) {
                final CompoundTag compoundTag2 = new CompoundTag();
                compoundTag2.putByte("Slot", (byte)i);
                this.items[i].save(compoundTag2);
                tag.add(compoundTag2);
            }
        }
        compoundTag.put("Items", tag);
    }
    
    public int getMaxStackSize() {
        return 64;
    }
    
    public boolean stillValid(final Player player) {
        return this.level.getTileEntity(this.x, this.y, this.z) == this && player.distanceToSqr(this.x + 0.5, this.y + 0.5, this.z + 0.5) <= 64.0;
    }
}
