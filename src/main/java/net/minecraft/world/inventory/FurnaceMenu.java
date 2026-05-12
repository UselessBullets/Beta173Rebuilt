// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.inventory;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.tile.entity.FurnaceTileEntity;

public class FurnaceMenu extends AbstractContainerMenu
{
    private FurnaceTileEntity furnace;
    private int tc;
    private int lt;
    private int ld;
    
    public FurnaceMenu(final Inventory inventory, final FurnaceTileEntity furnace) {
        this.tc = 0;
        this.lt = 0;
        this.ld = 0;
        this.furnace = furnace;
        this.addSlot(new Slot(furnace, 0, 56, 17));
        this.addSlot(new Slot(furnace, 1, 56, 53));
        this.addSlot(new FurnaceResultSlot(inventory.player, furnace, 2, 116, 35));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }
    }
    
    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        for (int i = 0; i < this.containerListeners.size(); ++i) {
            final ContainerListener containerListener = this.containerListeners.get(i);
            if (this.tc != this.furnace.tickCount) {
                containerListener.setContainerData(this, 0, this.furnace.tickCount);
            }
            if (this.lt != this.furnace.litTime) {
                containerListener.setContainerData(this, 1, this.furnace.litTime);
            }
            if (this.ld != this.furnace.litDuration) {
                containerListener.setContainerData(this, 2, this.furnace.litDuration);
            }
        }
        this.tc = this.furnace.tickCount;
        this.lt = this.furnace.litTime;
        this.ld = this.furnace.litDuration;
    }
    
    @Override
    public void setData(final int id, final int value) {
        if (id == 0) {
            this.furnace.tickCount = value;
        }
        if (id == 1) {
            this.furnace.litTime = value;
        }
        if (id == 2) {
            this.furnace.litDuration = value;
        }
    }
    
    @Override
    public boolean stillValid(final Player player) {
        return this.furnace.stillValid(player);
    }
    
    @Override
    public ItemInstance quickMoveStack(final int slotIndex) {
        ItemInstance copy = null;
        final Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            final ItemInstance item = slot.getItem();
            copy = item.copy();
            if (slotIndex == 2) {
                this.moveItemStackTo(item, 3, 39, true);
            }
            else if (slotIndex >= 3 && slotIndex < 30) {
                this.moveItemStackTo(item, 30, 39, false);
            }
            else if (slotIndex >= 30 && slotIndex < 39) {
                this.moveItemStackTo(item, 3, 30, false);
            }
            else {
                this.moveItemStackTo(item, 3, 39, false);
            }
            if (item.count == 0) {
                slot.set(null);
            }
            else {
                slot.setChanged();
            }
            if (item.count == copy.count) {
                return null;
            }
            slot.onTake(item);
        }
        return copy;
    }
}
