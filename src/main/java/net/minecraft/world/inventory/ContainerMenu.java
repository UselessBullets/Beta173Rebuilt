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

        final int yo = (this.containerRows - 4) * 18;

        for (int y = 0; y < this.containerRows; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(container, x + y * 9, 8 + x * 18, 18 + y * 18));
            }
        }
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 103 + y * 18 + yo));
            }
        }
        for (int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(inventory, x, 8 + x * 18, 161 + yo));
        }
    }
    
    @Override
    public boolean stillValid(final Player player) {
        return this.container.stillValid(player);
    }
    
    @Override
    public ItemInstance quickMoveStack(final int slotIndex) {
        ItemInstance clicked = null;
        final Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            final ItemInstance stack = slot.getItem();
            clicked = stack.copy();

            if (slotIndex < this.containerRows * 9) {
                this.moveItemStackTo(stack, this.containerRows * 9, this.slots.size(), true);
            }
            else {
                this.moveItemStackTo(stack, 0, this.containerRows * 9, false);
            }
            if (stack.count == 0) {
                slot.set(null);
            }
            else {
                slot.setChanged();
            }
        }
        return clicked;
    }
}
