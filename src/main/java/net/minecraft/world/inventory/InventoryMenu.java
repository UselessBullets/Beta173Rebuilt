// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.inventory;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.CraftingContainer;
import net.minecraft.world.level.tile.Tile;

public class InventoryMenu extends AbstractContainerMenu
{
    public static final int RESULT_SLOT = 0;
    public static final int CRAFT_SLOT_START = 1;
    public static final int CRAFT_SLOT_END = InventoryMenu.CRAFT_SLOT_START + 4;
    public static final int ARMOR_SLOT_START = InventoryMenu.CRAFT_SLOT_END;
    public static final int ARMOR_SLOT_END = InventoryMenu.ARMOR_SLOT_START + 4;
    public static final int INV_SLOT_START = InventoryMenu.ARMOR_SLOT_END;
    public static final int INV_SLOT_END = InventoryMenu.INV_SLOT_START + 9 * 3;
    public static final int USE_ROW_SLOT_START = InventoryMenu.INV_SLOT_END;
    public static final int USE_ROW_SLOT_END = InventoryMenu.USE_ROW_SLOT_START + 9;

    public CraftingContainer craftSlots = new CraftingContainer(this, 2, 2);
    public Container resultSlots = new ResultContainer();
    public boolean active = false;
    
    public InventoryMenu(final Inventory inventory) {
        this(inventory, true);
    }
    
    public InventoryMenu(final Inventory inventory, final boolean active) {
        this.active = active;
        this.addSlot(new ResultSlot(inventory.player, this.craftSlots, this.resultSlots, RESULT_SLOT, 144, 36));

        for (int y = 0; y < 2; ++y) {
            for (int x = 0; x < 2; ++x) {
                this.addSlot(new Slot(this.craftSlots, x + y * 2, 88 + x * 18, 26 + y * 18));
            }
        }

        for (int i = 0; i < 4; ++i) {
            final int slotNum = i;
            this.addSlot(new Slot(inventory, inventory.getContainerSize() - 1 - slotNum, 8, 8 + slotNum * 18) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(final ItemInstance item) {
                    if (item.getItem() instanceof ArmorItem) {
                        return ((ArmorItem)item.getItem()).slot == slotNum;
                    }
                    return item.getItem().id == Tile.pumpkin.id && slotNum == 0;
                }
            });
        }

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(inventory, x + (y + 1) * 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(inventory, x, 8 + x * 18, 142));
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
        ItemInstance clicked = null;
        final Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasItem()) {
            final ItemInstance stack = slot.getItem();
            clicked = stack.copy();

            if (slotIndex == RESULT_SLOT) {
                this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, true);
            }
            else if (slotIndex >= INV_SLOT_START && slotIndex < INV_SLOT_END) {
                this.moveItemStackTo(stack, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false);
            }
            else if (slotIndex >= USE_ROW_SLOT_START && slotIndex < USE_ROW_SLOT_END) {
                this.moveItemStackTo(stack, INV_SLOT_START, INV_SLOT_END, false);
            }
            else {
                this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, false);
            }

            if (stack.count == 0) {
                slot.set(null);
            }
            else {
                slot.setChanged();
            }

            if (stack.count == clicked.count) {
                return null;
            } else {
                slot.onTake(stack);
            }
        }
        return clicked;
    }
}
