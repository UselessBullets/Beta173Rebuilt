// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.inventory;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;

public class ContainerMenu extends AbstractContainerMenu
{
    private Container container;
    private int containerRows;
    
    public ContainerMenu(final Container inventory, final Container container) {
        this.container = container;
        this.containerRows = container.getContainerSize() / 9;
        final int n = (this.containerRows - 4) * 18;
        for (int i = 0; i < this.containerRows; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(container, j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inventory, l + k * 9 + 9, 8 + l * 18, 103 + k * 18 + n));
            }
        }
        for (int slot = 0; slot < 9; ++slot) {
            this.addSlot(new Slot(inventory, slot, 8 + slot * 18, 161 + n));
        }
    }
    
    @Override
    public boolean stillValid(final Player player) {
        return this.container.stillValid(player);
    }
    
    @Override
    public ItemInstance quickMoveStack(final int slotIndex) {
        ItemInstance copy = null;
        final Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            final ItemInstance item = slot.getItem();
            copy = item.copy();
            if (slotIndex < this.containerRows * 9) {
                this.moveItemStackTo(item, this.containerRows * 9, this.slots.size(), true);
            }
            else {
                this.moveItemStackTo(item, 0, this.containerRows * 9, false);
            }
            if (item.count == 0) {
                slot.set(null);
            }
            else {
                slot.setChanged();
            }
        }
        return copy;
    }
}
