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

    public static final int CLICK_PICKUP = 0;
    public static final int CLICK_QUICK_MOVE = 1;
    public static final int CLICK_SWAP = 2;
    public static final int CLICK_CLONE = 3;

    public static final int CONTAINER_ID_CARRIED = -1;
    public static final int CONTAINER_ID_INVENTORY = 0;
    public List<ItemInstance> lastSlots;
    public List<Slot> slots;
    public int containerId;
    private short changeUid;
    protected List<ContainerListener> containerListeners;
    private Set<Player> unSynchedPlayers;
    
    public AbstractContainerMenu() {
        this.lastSlots = new ArrayList<>();
        this.slots = new ArrayList<>();
        this.containerId = 0;
        this.changeUid = 0;
        this.containerListeners = new ArrayList<>();
        this.unSynchedPlayers = new HashSet<>();
    }
    
    protected void addSlot(final Slot slot) {
        slot.index = this.slots.size();
        this.slots.add(slot);
        this.lastSlots.add(null);
    }

    public void addSlotListener(final ContainerListener listener) {
        if (this.containerListeners.contains(listener)) {
            throw new IllegalArgumentException("Listener already listening");
        }
        this.containerListeners.add(listener);
        listener.refreshContainer(this, this.getItems());
        this.broadcastChanges();
    }

    public List<ItemInstance> getItems() {
        final ArrayList<ItemInstance> list = new ArrayList<>();
        for (int i = 0; i < this.slots.size(); ++i) {
            list.add(this.slots.get(i).getItem());
        }
        return list;
    }
    
    public void broadcastChanges() {
        for (int i = 0; i < this.slots.size(); ++i) {
            final ItemInstance item = this.slots.get(i).getItem();
            if (!ItemInstance.matches(this.lastSlots.get(i), item)) {
                final ItemInstance item2 = (item == null) ? null : item.copy();
                this.lastSlots.set(i, item2);
                for (int j = 0; j < this.containerListeners.size(); ++j) {
                    this.containerListeners.get(j).slotChanged(this, i, item2);
                }
            }
        }
    }

    public Slot getSlotFor(final Container container, final int index) {
        for (int i = 0; i < this.slots.size(); ++i) {
            final Slot slot = this.slots.get(i);
            if (slot.isAt(container, index)) {
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
    
    public ItemInstance clicked(final int slotIndex, final int buttonNum, final boolean clickType, final Player player) {
        ItemInstance itemInstance = null;
        if (buttonNum == 0 || buttonNum == 1) {
            final Inventory inventory = player.inventory;
            if (slotIndex == -999) {
                if (inventory.getCarried() != null && slotIndex == -999) {
                    if (buttonNum == 0) {
                        player.drop(inventory.getCarried());
                        inventory.setCarried(null);
                    }
                    if (buttonNum == 1) {
                        player.drop(inventory.getCarried().remove(1));
                        if (inventory.getCarried().count == 0) {
                            inventory.setCarried(null);
                        }
                    }
                }
            }
            else if (clickType) {
                final ItemInstance quickMoveStack = this.quickMoveStack(slotIndex);
                if (quickMoveStack != null) {
                    final int count = quickMoveStack.count;
                    itemInstance = quickMoveStack.copy();
                    final Slot slot = this.slots.get(slotIndex);
                    if (slot != null && slot.getItem() != null && slot.getItem().count < count) {
                        this.clicked(slotIndex, buttonNum, clickType, player);
                    }
                }
            }
            else {
                final Slot slot2 = this.slots.get(slotIndex);
                if (slot2 != null) {
                    slot2.setChanged();
                    final ItemInstance item = slot2.getItem();
                    final ItemInstance carried = inventory.getCarried();
                    if (item != null) {
                        itemInstance = item.copy();
                    }
                    if (item == null) {
                        if (carried != null && slot2.mayPlace(carried)) {
                            int maxStackSize = (buttonNum == 0) ? carried.count : 1;
                            if (maxStackSize > slot2.getMaxStackSize()) {
                                maxStackSize = slot2.getMaxStackSize();
                            }
                            slot2.set(carried.remove(maxStackSize));
                            if (carried.count == 0) {
                                inventory.setCarried(null);
                            }
                        }
                    }
                    else if (carried == null) {
                        inventory.setCarried(slot2.remove((buttonNum == 0) ? item.count : ((item.count + 1) / 2)));
                        if (item.count == 0) {
                            slot2.set(null);
                        }
                        slot2.onTake(inventory.getCarried());
                    }
                    else if (slot2.mayPlace(carried)) {
                        if (item.id != carried.id || (item.isStackedByData() && item.getAuxValue() != carried.getAuxValue())) {
                            if (carried.count <= slot2.getMaxStackSize()) {
                                final ItemInstance carried2 = item;
                                slot2.set(carried);
                                inventory.setCarried(carried2);
                            }
                        }
                        else {
                            int count2 = (buttonNum == 0) ? carried.count : 1;
                            if (count2 > slot2.getMaxStackSize() - item.count) {
                                count2 = slot2.getMaxStackSize() - item.count;
                            }
                            if (count2 > carried.getMaxStackSize() - item.count) {
                                count2 = carried.getMaxStackSize() - item.count;
                            }
                            carried.remove(count2);
                            if (carried.count == 0) {
                                inventory.setCarried(null);
                            }
                            final ItemInstance itemInstance2 = item;
                            itemInstance2.count += count2;
                        }
                    }
                    else if (item.id == carried.id && carried.getMaxStackSize() > 1 && (!item.isStackedByData() || item.getAuxValue() == carried.getAuxValue())) {
                        final int count3 = item.count;
                        if (count3 > 0 && count3 + carried.count <= carried.getMaxStackSize()) {
                            final ItemInstance itemInstance3 = carried;
                            itemInstance3.count += count3;
                            item.remove(count3);
                            if (item.count == 0) {
                                slot2.set(null);
                            }
                            slot2.onTake(inventory.getCarried());
                        }
                    }
                }
            }
        }
        return itemInstance;
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
        return (short)(++this.changeUid);
    }
    
    public void deleteBackup(final short uid) {
    }
    
    public void rollbackToBackup(final short uid) {
    }
    
    public abstract boolean stillValid(final Player player);
    
    protected void moveItemStackTo(final ItemInstance itemStack, final int startSlot, final int endSlot, final boolean backwards) {
        int n = startSlot;
        if (backwards) {
            n = endSlot - 1;
        }
        if (itemStack.isStackable()) {
            while (itemStack.count > 0 && ((!backwards && n < endSlot) || (backwards && n >= startSlot))) {
                final Slot slot = this.slots.get(n);
                final ItemInstance item = slot.getItem();
                if (item != null && item.id == itemStack.id && (!itemStack.isStackedByData() || itemStack.getAuxValue() == item.getAuxValue())) {
                    final int count = item.count + itemStack.count;
                    if (count <= itemStack.getMaxStackSize()) {
                        itemStack.count = 0;
                        item.count = count;
                        slot.setChanged();
                    }
                    else if (item.count < itemStack.getMaxStackSize()) {
                        itemStack.count -= itemStack.getMaxStackSize() - item.count;
                        item.count = itemStack.getMaxStackSize();
                        slot.setChanged();
                    }
                }
                if (backwards) {
                    --n;
                }
                else {
                    ++n;
                }
            }
        }
        if (itemStack.count > 0) {
            int n2;
            if (backwards) {
                n2 = endSlot - 1;
            }
            else {
                n2 = startSlot;
            }
            while ((!backwards && n2 < endSlot) || (backwards && n2 >= startSlot)) {
                final Slot slot2 = this.slots.get(n2);
                if (slot2.getItem() == null) {
                    slot2.set(itemStack.copy());
                    slot2.setChanged();
                    itemStack.count = 0;
                    break;
                }
                if (backwards) {
                    --n2;
                }
                else {
                    ++n2;
                }
            }
        }
    }
}
