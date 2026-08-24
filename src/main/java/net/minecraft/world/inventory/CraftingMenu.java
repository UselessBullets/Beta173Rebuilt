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
    public static final int RESULT_SLOT = 0;
    public static final int CRAFT_SLOT_START = 1;
    public static final int CRAFT_SLOT_END = CraftingMenu.CRAFT_SLOT_START + 9;
    public static final int INV_SLOT_START = CraftingMenu.CRAFT_SLOT_END;
    public static final int INV_SLOT_END = CraftingMenu.INV_SLOT_START + 9 * 3;
    public static final int USE_ROW_SLOT_START = CraftingMenu.INV_SLOT_END;
    public static final int USE_ROW_SLOT_END = CraftingMenu.USE_ROW_SLOT_START + 9;
    public CraftingContainer craftingSlots = new CraftingContainer(this, 3, 3);
    public Container resultSlots = new ResultContainer();
    private Level level;
    private int x, y, z;
    
    public CraftingMenu(final Inventory inventory, final Level level, final int xt, final int yt, final int zt) {
        this.level = level;
        this.x = xt;
        this.y = yt;
        this.z = zt;
        this.addSlot(new ResultSlot(inventory.player, this.craftingSlots, this.resultSlots, RESULT_SLOT, 120 + 4, 31 + 4));

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                this.addSlot(new Slot(this.craftingSlots, x + y * 3, 30 + x * 18, 17 + y * 18));
            }
        }

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(inventory, x, 8 + x * 18, 142));
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
        if (this.level.isClientSide) return;

        for (int i = 0; i < 9; ++i) {
            final ItemInstance item = this.craftingSlots.getItem(i);
            if (item != null) {
                player.drop(item);
            }
        }
    }
    
    @Override
    public boolean stillValid(final Player player) {
        if (this.level.getTile(this.x, this.y, this.z) != Tile.workBench.id) return false;
        if (player.distanceToSqr(this.x + 0.5, this.y + 0.5, this.z + 0.5) <= 8 * 8) return true;
        return false;
    }
    
    @Override
    public ItemInstance quickMoveStack(final int slotIndex) {
        ItemInstance clicked = null;
        final Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            final ItemInstance item = slot.getItem();
            clicked = item.copy();

            if (slotIndex == RESULT_SLOT) {
                this.moveItemStackTo(item, INV_SLOT_START, USE_ROW_SLOT_END, true);
            }
            else if (slotIndex >= INV_SLOT_START && slotIndex < INV_SLOT_END) {
                this.moveItemStackTo(item, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false);
            }
            else if (slotIndex >= USE_ROW_SLOT_START && slotIndex < USE_ROW_SLOT_END) {
                this.moveItemStackTo(item, INV_SLOT_START, INV_SLOT_END, false);
            }
            else {
                this.moveItemStackTo(item, INV_SLOT_START, USE_ROW_SLOT_END, false);
            }

            if (item.count == 0) {
                slot.set(null);
            }
            else {
                slot.setChanged();
            }

            if (item.count == clicked.count) {
                return null;
            } else {
                slot.onTake(item);
            }
        }
        return clicked;
    }
}
