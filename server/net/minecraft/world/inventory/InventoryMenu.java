// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.inventory;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.CraftingContainer;

public class InventoryMenu extends AbstractContainerMenu
{
    public CraftingContainer craftSlots;
    public Container resultSlots;
    public boolean active;
    
    public InventoryMenu(final Inventory inventory) {
        this(inventory, true);
    }
    
    public InventoryMenu(final Inventory inventory, final boolean active) {
        this.craftSlots = new CraftingContainer(this, 2, 2);
        this.resultSlots = new ResultContainer();
        this.active = false;
        this.active = active;
        this.addSlot(new ResultSlot(inventory.player, this.craftSlots, this.resultSlots, 0, 144, 36));
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                this.addSlot(new Slot(this.craftSlots, j + i * 2, 88 + j * 18, 26 + i * 18));
            }
        }
        for (int k = 0; k < 4; ++k) {
            this.addSlot(new ArmorSlot(this, inventory, inventory.getContainerSize() - 1 - k, 8, 8 + k * 18, k));
        }
        for (int l = 0; l < 3; ++l) {
            for (int n = 0; n < 9; ++n) {
                this.addSlot(new Slot(inventory, n + (l + 1) * 9, 8 + n * 18, 84 + l * 18));
            }
        }
        for (int slot = 0; slot < 9; ++slot) {
            this.addSlot(new Slot(inventory, slot, 8 + slot * 18, 142));
        }
        this.slotsChanged(this.craftSlots);
    }
    
    @Override
    public void slotsChanged(final Container container) {
        this.resultSlots.setItem(0, Recipes.getInstance().getItemFor(this.craftSlots));
    }
    
    @Override
    public void removed(final Player player) {
        super.removed(player);
        for (int i = 0; i < 4; ++i) {
            final ItemInstance item = this.craftSlots.getItem(i);
            if (item != null) {
                player.drop(item);
                this.craftSlots.setItem(i, null);
            }
        }
    }
    
    @Override
    public boolean stillValid(final Player player) {
        return true;
    }
    
    @Override
    public ItemInstance quickMoveStack(final int slotIndex) {
        ItemInstance copy = null;
        final Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            final ItemInstance item = slot.getItem();
            copy = item.copy();
            if (slotIndex == 0) {
                this.moveItemStackTo(item, 9, 45, true);
            }
            else if (slotIndex >= 9 && slotIndex < 36) {
                this.moveItemStackTo(item, 36, 45, false);
            }
            else if (slotIndex >= 36 && slotIndex < 45) {
                this.moveItemStackTo(item, 9, 36, false);
            }
            else {
                this.moveItemStackTo(item, 9, 45, false);
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
