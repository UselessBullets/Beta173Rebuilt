// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.inventory;

import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.Container;
import net.minecraft.world.CraftingContainer;

public class CraftingMenu extends AbstractContainerMenu
{
    public CraftingContainer craftingSlots;
    public Container resultSlots;
    private Level level;
    private int x;
    private int y;
    private int z;
    
    public CraftingMenu(final Inventory inventory, final Level level, final int xt, final int yt, final int zt) {
        this.craftingSlots = new CraftingContainer(this, 3, 3);
        this.resultSlots = new ResultContainer();
        this.level = level;
        this.x = xt;
        this.y = yt;
        this.z = zt;
        this.addSlot(new ResultSlot(inventory.player, this.craftingSlots, this.resultSlots, 0, 124, 35));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.addSlot(new Slot(this.craftingSlots, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
            }
        }
        for (int slot = 0; slot < 9; ++slot) {
            this.addSlot(new Slot(inventory, slot, 8 + slot * 18, 142));
        }
        this.slotsChanged(this.craftingSlots);
    }
    
    @Override
    public void slotsChanged(final Container container) {
        this.resultSlots.setItem(0, Recipes.getInstance().getItemFor(this.craftingSlots));
    }
    
    @Override
    public void removed(final Player player) {
        super.removed(player);
        if (this.level.isClientSide) {
            return;
        }
        for (int i = 0; i < 9; ++i) {
            final ItemInstance item = this.craftingSlots.getItem(i);
            if (item != null) {
                player.drop(item);
            }
        }
    }
    
    @Override
    public boolean stillValid(final Player player) {
        return this.level.getTile(this.x, this.y, this.z) == Tile.workBench.id && player.distanceToSqr(this.x + 0.5, this.y + 0.5, this.z + 0.5) <= 64.0;
    }
    
    @Override
    public ItemInstance quickMoveStack(final int slotIndex) {
        ItemInstance copy = null;
        final Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            final ItemInstance item = slot.getItem();
            copy = item.copy();
            if (slotIndex == 0) {
                this.moveItemStackTo(item, 10, 46, true);
            }
            else if (slotIndex >= 10 && slotIndex < 37) {
                this.moveItemStackTo(item, 37, 46, false);
            }
            else if (slotIndex >= 37 && slotIndex < 46) {
                this.moveItemStackTo(item, 10, 37, false);
            }
            else {
                this.moveItemStackTo(item, 10, 46, false);
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
