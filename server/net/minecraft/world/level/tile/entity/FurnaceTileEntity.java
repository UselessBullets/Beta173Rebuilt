// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile.entity;

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
    private ItemInstance[] items;
    public int litTime;
    public int litDuration;
    public int tickCount;
    
    public FurnaceTileEntity() {
        this.items = new ItemInstance[3];
        this.litTime = 0;
        this.litDuration = 0;
        this.tickCount = 0;
    }
    
    public int getContainerSize() {
        return this.items.length;
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
            return itemInstance;
        }
        final ItemInstance remove = this.items[slot].remove(count);
        if (this.items[slot].count == 0) {
            this.items[slot] = null;
        }
        return remove;
    }
    
    public void setItem(final int slot, final ItemInstance item) {
        this.items[slot] = item;
        if (item != null && item.count > this.getMaxStackSize()) {
            item.count = this.getMaxStackSize();
        }
    }
    
    public String getName() {
        return "Furnace";
    }
    
    @Override
    public void load(final CompoundTag compoundTag) {
        super.load(compoundTag);
        final ListTag list = compoundTag.getList("Items");
        this.items = new ItemInstance[this.getContainerSize()];
        for (int i = 0; i < list.size(); ++i) {
            final CompoundTag itemTag = (CompoundTag)list.get(i);
            final byte byte1 = itemTag.getByte("Slot");
            if (byte1 >= 0 && byte1 < this.items.length) {
                this.items[byte1] = new ItemInstance(itemTag);
            }
        }
        this.litTime = compoundTag.getShort("BurnTime");
        this.tickCount = compoundTag.getShort("CookTime");
        this.litDuration = this.getBurnDuration(this.items[1]);
    }
    
    @Override
    public void save(final CompoundTag compoundTag) {
        super.save(compoundTag);
        compoundTag.putShort("BurnTime", (short)this.litTime);
        compoundTag.putShort("CookTime", (short)this.tickCount);
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
    
    public boolean isLit() {
        return this.litTime > 0;
    }
    
    @Override
    public void tick() {
        final boolean b = this.litTime > 0;
        boolean b2 = false;
        if (this.litTime > 0) {
            --this.litTime;
        }
        if (!this.level.isClientSide) {
            if (this.litTime == 0 && this.canBurn()) {
                final int burnDuration = this.getBurnDuration(this.items[1]);
                this.litTime = burnDuration;
                this.litDuration = burnDuration;
                if (this.litTime > 0) {
                    b2 = true;
                    if (this.items[1] != null) {
                        final ItemInstance itemInstance = this.items[1];
                        --itemInstance.count;
                        if (this.items[1].count == 0) {
                            this.items[1] = null;
                        }
                    }
                }
            }
            if (this.isLit() && this.canBurn()) {
                ++this.tickCount;
                if (this.tickCount == 200) {
                    this.tickCount = 0;
                    this.burn();
                    b2 = true;
                }
            }
            else {
                this.tickCount = 0;
            }
            if (b != this.litTime > 0) {
                b2 = true;
                FurnaceTile.setLit(this.litTime > 0, this.level, this.x, this.y, this.z);
            }
        }
        if (b2) {
            this.setChanged();
        }
    }
    
    private boolean canBurn() {
        if (this.items[0] == null) {
            return false;
        }
        final ItemInstance result = FurnaceRecipes.getInstance().getResult(this.items[0].getItem().id);
        return result != null && (this.items[2] == null || (this.items[2].sameItem(result) && ((this.items[2].count < this.getMaxStackSize() && this.items[2].count < this.items[2].getMaxStackSize()) || this.items[2].count < result.getMaxStackSize())));
    }
    
    public void burn() {
        if (!this.canBurn()) {
            return;
        }
        final ItemInstance result = FurnaceRecipes.getInstance().getResult(this.items[0].getItem().id);
        if (this.items[2] == null) {
            this.items[2] = result.copy();
        }
        else if (this.items[2].id == result.id) {
            final ItemInstance itemInstance = this.items[2];
            ++itemInstance.count;
        }
        final ItemInstance itemInstance2 = this.items[0];
        --itemInstance2.count;
        if (this.items[0].count <= 0) {
            this.items[0] = null;
        }
    }
    
    private int getBurnDuration(final ItemInstance itemInstance) {
        if (itemInstance == null) {
            return 0;
        }
        final int id = itemInstance.getItem().id;
        if (id < 256 && Tile.tiles[id].material == Material.wood) {
            return 300;
        }
        if (id == Item.stick.id) {
            return 100;
        }
        if (id == Item.coal.id) {
            return 1600;
        }
        if (id == Item.bucket_lava.id) {
            return 20000;
        }
        if (id == Tile.sapling.id) {
            return 100;
        }
        return 0;
    }
    
    public boolean stillValid(final Player player) {
        return this.level.getTileEntity(this.x, this.y, this.z) == this && player.distanceToSqr(this.x + 0.5, this.y + 0.5, this.z + 0.5) <= 64.0;
    }
}
