// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

public abstract class AbstractContainerMenu
{
    public static final int CLICKED_OUTSIDE = -999;

    public static final int CONTAINER_ID_CARRIED = -1;
    public static final int CONTAINER_ID_INVENTORY = 0;
    public List<ItemInstance> lastSlots = new ArrayList<>();
    public List<Slot> slots = new ArrayList<>();
    public int containerId = 0;
    private short changeUid = 0;
    protected List<ContainerListener> containerListeners = new ArrayList<>();
    private Set<Player> unSynchedPlayers = new HashSet<>();
    
    protected void addSlot(final Slot slot) {
        slot.index = this.slots.size();
        this.slots.add(slot);
        this.lastSlots.add(null);
    }

    public void addSlotListener(final ContainerListener listener) {
        if (this.containerListeners.contains(listener)) throw new IllegalArgumentException("Listener already listening");

        this.containerListeners.add(listener);
        listener.refreshContainer(this, this.getItems());
        this.broadcastChanges();
    }

    public List<ItemInstance> getItems() {
        final ArrayList<ItemInstance> items = new ArrayList<>();
        for (int i = 0; i < this.slots.size(); ++i) {
            items.add(this.slots.get(i).getItem());
        }
        return items;
    }

    // Useless - In b1.2 and LCE leaks
    public void sendData(int id, int value) {
        for (int i = 0; i < this.containerListeners.size(); i++) {
            this.containerListeners.get(i).setContainerData(this, id, value);
        }
    }
    
    public void broadcastChanges() {
        for (int i = 0; i < this.slots.size(); ++i) {
            ItemInstance current = this.slots.get(i).getItem();
            ItemInstance expected = this.lastSlots.get(i);
            if (!ItemInstance.matches(expected, current)) {
                expected = current == null ? null : current.copy();
                this.lastSlots.set(i, expected);

                for (int j = 0; j < this.containerListeners.size(); ++j) {
                    this.containerListeners.get(j).slotChanged(this, i, expected);
                }
            }
        }
    }

    public Slot getSlotFor(final Container c, final int index) {
        for (int i = 0; i < this.slots.size(); ++i) {
            final Slot slot = this.slots.get(i);
            if (slot.isAt(c, index)) {
                return slot;
            }
        }
        return null;
    }

    public Slot getSlot(final int index) {
        return this.slots.get(index);
    }
    
    public ItemInstance quickMoveStack(final int slotIndex) {
        final Slot slot = this.slots.get(slotIndex);
        if (slot != null) {
            return slot.getItem();
        }
        return null;
    }
    
    public ItemInstance clicked(final int slotIndex, final int buttonNum, final boolean quickMove, final Player player) {
        ItemInstance clickedEntity = null;

        if (buttonNum == 0 || buttonNum == 1) {
            final Inventory inventory = player.inventory;
            if (slotIndex == CLICKED_OUTSIDE) {
                if (inventory.getCarried() != null)
                    if (slotIndex == CLICKED_OUTSIDE) {
                        if (buttonNum == 0) {
                            player.drop(inventory.getCarried());
                            inventory.setCarried(null);
                        }
                        if (buttonNum == 1) {
                            player.drop(inventory.getCarried().remove(1));
                            if (inventory.getCarried().count == 0) inventory.setCarried(null);
                        }
                    }
            }
            else if (quickMove) {
                final ItemInstance piiClicked = this.quickMoveStack(slotIndex);
                if (piiClicked != null) {
                    final int oldSize = piiClicked.count;

                    clickedEntity = piiClicked.copy();

                    final Slot slot = this.slots.get(slotIndex);
                    if (slot != null) {
                        if (slot.getItem() != null && slot.getItem().count < oldSize) {
                            this.clicked(slotIndex, buttonNum, quickMove, player);
                        }
                    }
                }
            }
            else {
                final Slot slot = this.slots.get(slotIndex);
                if (slot != null) {
                    slot.setChanged();
                    final ItemInstance clicked = slot.getItem();
                    final ItemInstance carried = inventory.getCarried();

                    if (clicked != null) {
                        clickedEntity = clicked.copy();
                    }

                    if (clicked == null) {
                        if (carried != null && slot.mayPlace(carried)) {
                            int c = buttonNum == 0 ? carried.count : 1;
                            if (c > slot.getMaxStackSize()) {
                                c = slot.getMaxStackSize();
                            }
                            slot.set(carried.remove(c));
                            if (carried.count == 0) {
                                inventory.setCarried(null);
                            }
                        }
                    }
                    else if (carried == null) {
                        // pick up to empty hand
                        int c = buttonNum == 0 ? clicked.count : (clicked.count + 1) / 2;
                        ItemInstance removed = slot.remove(c);

                        inventory.setCarried(removed);
                        if (clicked.count == 0) {
                            slot.set(null);
                        }
                        slot.onTake(inventory.getCarried());
                    }
                    else if (slot.mayPlace(carried)) {
                        // put down and/or pick up
                        if (clicked.id != carried.id || (clicked.isStackedByData() && clicked.getAuxValue() != carried.getAuxValue())) {
                            // no match, replace
                            if (carried.count <= slot.getMaxStackSize()) {
                                slot.set(carried);
                                inventory.setCarried(clicked);
                            }
                        }
                        else {
                            // match, attempt to fill slot
                            int c = (buttonNum == 0) ? carried.count : 1;
                            if (c > slot.getMaxStackSize() - clicked.count) {
                                c = slot.getMaxStackSize() - clicked.count;
                            }
                            if (c > carried.getMaxStackSize() - clicked.count) {
                                c = carried.getMaxStackSize() - clicked.count;
                            }
                            carried.remove(c);
                            if (carried.count == 0) {
                                inventory.setCarried(null);
                            }
                            clicked.count += c;
                        }
                    }
                    else {
                        // pick up to non-empty hand
                        if (clicked.id == carried.id && carried.getMaxStackSize() > 1 && (!clicked.isStackedByData() || clicked.getAuxValue() == carried.getAuxValue())) {
                            final int c = clicked.count;
                            if (c > 0 && c + carried.count <= carried.getMaxStackSize()) {
                                carried.count += c;
                                clicked.remove(c);
                                if (clicked.count == 0) slot.set(null);
                                slot.onTake(inventory.getCarried());
                            }
                        }
                    }
                }
            }
        }
        return clickedEntity;
    }
    
    public void removed(final Player player) {
        final Inventory inventory = player.inventory;
        if (inventory.getCarried() != null) {
            player.drop(inventory.getCarried());
            inventory.setCarried(null);
        }
    }
    
    public void slotsChanged(final Container container) {
        this.broadcastChanges();
    }

    public boolean isSynched(final Player player) {
        return !this.unSynchedPlayers.contains(player);
    }

    public void setSynched(final Player player, final boolean synched) {
        if (synched) {
            this.unSynchedPlayers.remove(player);
        }
        else {
            this.unSynchedPlayers.add(player);
        }
    }
    
    public void setItem(final int slot, final ItemInstance item) {
        this.getSlot(slot).set(item);
    }
    
    public void setAll(final ItemInstance[] items) {
        for (int i = 0; i < items.length; ++i) {
            this.getSlot(i).set(items[i]);
        }
    }
    
    public void setData(final int id, final int value) {
    }
    
    public short backup(final Inventory inventory) {
        this.changeUid++;
        return this.changeUid;
    }
    
    public void deleteBackup(final short uid) {
    }
    
    public void rollbackToBackup(final short uid) {
    }
    
    public abstract boolean stillValid(final Player player);
    
    protected void moveItemStackTo(final ItemInstance itemStack, final int startSlot, final int endSlot, final boolean backwards) {
        int destSlot = startSlot;
        if (backwards) {
            destSlot = endSlot - 1;
        }

        // find stackable slots first
        if (itemStack.isStackable()) {
            while (itemStack.count > 0 && ((!backwards && destSlot < endSlot) || (backwards && destSlot >= startSlot))) {
                final Slot slot = this.slots.get(destSlot);
                final ItemInstance target = slot.getItem();
                if (target != null && target.id == itemStack.id && (!itemStack.isStackedByData() || itemStack.getAuxValue() == target.getAuxValue())) {
                    final int totalStack = target.count + itemStack.count;
                    if (totalStack <= itemStack.getMaxStackSize()) {
                        itemStack.count = 0;
                        target.count = totalStack;
                        slot.setChanged();
                    }
                    else if (target.count < itemStack.getMaxStackSize()) {
                        itemStack.count -= itemStack.getMaxStackSize() - target.count;
                        target.count = itemStack.getMaxStackSize();
                        slot.setChanged();
                    }
                }

                if (backwards) {
                    destSlot--;
                }
                else {
                    destSlot++;
                }
            }
        }

        // find empty slot
        if (itemStack.count > 0) {
            if (backwards) {
                destSlot = endSlot - 1;
            }
            else {
                destSlot = startSlot;
            }

            while ((!backwards && destSlot < endSlot) || (backwards && destSlot >= startSlot)) {
                final Slot slot = this.slots.get(destSlot);
                ItemInstance target = slot.getItem();

                if (target == null) {
                    slot.set(itemStack.copy());
                    slot.setChanged();
                    itemStack.count = 0;
                    break;
                }

                if (backwards) {
                    --destSlot;
                }
                else {
                    ++destSlot;
                }
            }
        }
    }
}
