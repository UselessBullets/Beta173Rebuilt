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
    public static final int INGREDIENT_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    public static final int INV_SLOT_START = FurnaceMenu.RESULT_SLOT + 1;
    public static final int INV_SLOT_END = FurnaceMenu.INV_SLOT_START + 9 * 3;
    public static final int USE_ROW_SLOT_START = FurnaceMenu.INV_SLOT_END;
    public static final int USE_ROW_SLOT_END = FurnaceMenu.USE_ROW_SLOT_START + 9;
    private FurnaceTileEntity furnace;
    private int tc;
    private int lt;
    private int ld;
    
    public FurnaceMenu(final Inventory inventory, final FurnaceTileEntity furnace) {
        this.tc = 0;
        this.lt = 0;
        this.ld = 0;

        this.furnace = furnace;

        this.addSlot(new Slot(furnace, INGREDIENT_SLOT, 52 + 4, 13 + 4));
        this.addSlot(new Slot(furnace, FUEL_SLOT, 52 + 4, 49 + 4));
        this.addSlot(new FurnaceResultSlot(inventory.player, furnace, RESULT_SLOT, 112 + 4, 31 + 4));

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(inventory, x, 8 + x * 18, 142));
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
        if (id == 0) this.furnace.tickCount = value;
        if (id == 1) this.furnace.litTime = value;
        if (id == 2) this.furnace.litDuration = value;
    }
    
    @Override
    public boolean stillValid(final Player player) {
        return this.furnace.stillValid(player);
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
