// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.player;

import net.minecraft.world.item.ArmorItem;
import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.Container;

public class Inventory implements Container
{
    public static final int POP_TIME_DURATION = 5;
    public static final int MAX_INVENTORY_STACK_SIZE = 64;

    private static final int INVENTORY_SIZE = 4 * 9;
    private static final int SELECTION_SIZE = 9;

    public ItemInstance[] items = new ItemInstance[INVENTORY_SIZE];
    public ItemInstance[] armor = new ItemInstance[4];
    public int selected = 0;
    public Player player;
    private ItemInstance carried;
    public boolean changed = false;
    
    public Inventory(final Player player) {
        this.player = player;
    }
    
    public ItemInstance getSelected() {
        if (this.selected < SELECTION_SIZE && this.selected >= 0) {
            return this.items[this.selected];
        }
        return null;
    }

    public static int getSelectionSize() {
        return SELECTION_SIZE;
    }
    
    private int getSlot(final int tileId) {
        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] != null && this.items[i].id == tileId) return i;
        }
        return -1;
    }
    
    private int getSlotWithRemainingSpace(final ItemInstance item) {
        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] != null && this.items[i].id == item.id && this.items[i].isStackable()
                    && this.items[i].count < this.items[i].getMaxStackSize() && this.items[i].count < this.getMaxStackSize()
                    && (!this.items[i].isStackedByData() || this.items[i].getAuxValue() == item.getAuxValue())) {
                return i;
            }
        }
        return -1;
    }
    
    private int getFreeSlot() {
        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] == null) return i;
        }
        return -1;
    }
    
    public void grabTexture(final int id, final boolean boolean2) {
        final int slot = this.getSlot(id);
        if (slot >= 0 && slot < SELECTION_SIZE) {
            this.selected = slot;
        }
    }
    
    public void swapPaint(int wheel) {
        if (wheel > 0) wheel = 1;
        if (wheel < 0) wheel = -1;

        this.selected -= wheel;
        while (this.selected < 0) this.selected += SELECTION_SIZE;
        while (this.selected >= 9) this.selected -= SELECTION_SIZE;
    }
    
    private int addResource(final ItemInstance itemInstance) {
        int type = itemInstance.id;
        int count = itemInstance.count;

        int slot = this.getSlotWithRemainingSpace(itemInstance);
        if (slot < 0) slot = this.getFreeSlot();
        if (slot < 0) return count;
        if (this.items[slot] == null) {
            this.items[slot] = new ItemInstance(type, 0, itemInstance.getAuxValue());
        }

        int toAdd = count;
        if (toAdd > this.items[slot].getMaxStackSize() - this.items[slot].count) {
            toAdd = this.items[slot].getMaxStackSize() - this.items[slot].count;
        }
        if (toAdd > this.getMaxStackSize() - this.items[slot].count) {
            toAdd = this.getMaxStackSize() - this.items[slot].count;
        }

        if (toAdd == 0) return count;

        count -= toAdd;
        this.items[slot].count += toAdd;
        this.items[slot].popTime = Inventory.POP_TIME_DURATION;

        return count;
    }
    
    public void tick() {
        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] != null) {
                this.items[i].inventoryTick(this.player.level, this.player, i, this.selected == i);
            }
        }
    }
    
    public boolean removeResource(final int type) {
        final int slot = this.getSlot(type);
        if (slot < 0) return false;
        if (--this.items[slot].count <= 0) this.items[slot] = null;

        return true;
    }

    // Useless - In b1.2 and LCE leaks
    public void swapSlots(int from, int to)
    {
        ItemInstance tmp = this.items[to];
        this.items[to] = this.items[from];
        this.items[from] = tmp;
    }
    
    public boolean add(final ItemInstance item) {
        if (!item.isDamaged()) {
            int lastSize;
            do {
                lastSize = item.count;
                item.count = this.addResource(item);
            } while (item.count > 0 && item.count < lastSize);
            return item.count < lastSize;
        }

        final int slot = this.getFreeSlot();
        if (slot >= 0) {
            this.items[slot] = ItemInstance.clone(item);
            this.items[slot].popTime = Inventory.POP_TIME_DURATION;
            item.count = 0;
            return true;
        }
        return false;
    }
    
    public ItemInstance removeItem(int slot, final int count) {
        ItemInstance[] pile = this.items;
        if (slot >= this.items.length) {
            pile = this.armor;
            slot -= this.items.length;
        }

        if (pile[slot] != null) {
            if (pile[slot].count <= count) {
                final ItemInstance item = pile[slot];
                pile[slot] = null;
                return item;
            } else {
                final ItemInstance i = pile[slot].remove(count);
                if (pile[slot].count == 0) pile[slot] = null;
                return i;
            }
        }
        return null;
    }
    
    public void setItem(int slot, final ItemInstance item) {
        ItemInstance[] pile = this.items;
        if (slot >= pile.length) {
            slot -= pile.length;
            pile = this.armor;
        }
        pile[slot] = item;
    }
    
    public float getDestroySpeed(final Tile tile) {
        float speed = 1.0f;
        if (this.items[this.selected] != null) speed *= this.items[this.selected].getDestroySpeed(tile);
        return speed;
    }
    
    public ListTag<CompoundTag> save(final ListTag<CompoundTag> listTag) {
        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] != null) {
                final CompoundTag tag = new CompoundTag();
                tag.putByte("Slot", (byte)i);
                this.items[i].save(tag);
                listTag.add(tag);
            }
        }
        for (int i = 0; i < this.armor.length; ++i) {
            if (this.armor[i] != null) {
                final CompoundTag tag = new CompoundTag();
                tag.putByte("Slot", (byte)(i + 100));
                this.armor[i].save(tag);
                listTag.add(tag);
            }
        }
        return listTag;
    }
    
    public void load(final ListTag<CompoundTag> inventoryList) {
        this.items = new ItemInstance[INVENTORY_SIZE];
        this.armor = new ItemInstance[4];
        for (int i = 0; i < inventoryList.size(); ++i) {
            final CompoundTag tag = inventoryList.get(i);
            final int slot = tag.getByte("Slot") & 0xFF;
            final ItemInstance item = new ItemInstance(tag);
            if (item.getItem() != null) {
                if (slot >= 0 && slot < this.items.length) this.items[slot] = item;
                if (slot >= 100 && slot < this.armor.length + 100) this.armor[slot - 100] = item;
            }
        }
    }
    
    public int getContainerSize() {
        return this.items.length + 4;
    }
    
    public ItemInstance getItem(int slot) {
        ItemInstance[] pile = this.items;
        if (slot >= pile.length) {
            slot -= pile.length;
            pile = this.armor;
        }
        return pile[slot];
    }
    
    public String getName() {
        return "Inventory";
    }
    
    public int getMaxStackSize() {
        return MAX_INVENTORY_STACK_SIZE;
    }
    
    public int getAttackDamage(final Entity entity) {
        final ItemInstance item = this.getItem(this.selected);
        if (item != null) return item.getAttackDamage(entity);
        return 1;
    }
    
    public boolean canDestroy(final Tile tile) {
        if (tile.material.isAlwaysDestroyable()) return true;

        final ItemInstance item = this.getItem(this.selected);
        if (item != null) return item.canDestroySpecial(tile);
        return false;
    }
    
    public ItemInstance getArmor(final int layer) {
        return this.armor[layer];
    }
    
    public int getArmorValue() {
        // Useless - these 3 variables don't have searchable names, names given are best guesses
        int totalDefense = 0;
        int currentSuitDamage = 0;
        int maxSuitDamage = 0;
        for (int i = 0; i < this.armor.length; ++i) {
            if (this.armor[i] != null && this.armor[i].getItem() instanceof ArmorItem) {
                final int maxDamage = this.armor[i].getMaxDamage();
                currentSuitDamage += maxDamage - this.armor[i].getDamageValue();
                maxSuitDamage += maxDamage;
                totalDefense += ((ArmorItem)this.armor[i].getItem()).defense;
            }
        }
        if (maxSuitDamage == 0) return 0;
        return (totalDefense - 1) * currentSuitDamage / maxSuitDamage + 1;
    }
    
    public void hurtArmor(final int dmg) {
        for (int i = 0; i < this.armor.length; ++i) {
            if (this.armor[i] != null && this.armor[i].getItem() instanceof ArmorItem) {
                this.armor[i].hurt(dmg, this.player);
                if (this.armor[i].count == 0) {
                    this.armor[i].snap(this.player);
                    this.armor[i] = null;
                }
            }
        }
    }
    
    public void dropAll() {
        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] != null) {
                this.player.drop(this.items[i], true);
                this.items[i] = null;
            }
        }
        for (int j = 0; j < this.armor.length; ++j) {
            if (this.armor[j] != null) {
                this.player.drop(this.armor[j], true);
                this.armor[j] = null;
            }
        }
    }
    
    public void setChanged() {
        this.changed = true;
    }

    // Useless - in b1.2 and LCE leaks
    public boolean isSame(Inventory copy) {
        for (int var2 = 0; var2 < this.items.length; var2++) {
            if (!this.isSame(copy.items[var2], this.items[var2])) return false;
        }

        for (int var3 = 0; var3 < this.armor.length; var3++) {
            if (!this.isSame(copy.armor[var3], this.armor[var3])) return false;
        }
        return true;
    }

    // Useless - in b1.2 and LCE leaks
    private boolean isSame(ItemInstance a, ItemInstance b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;

        return a.id == b.id && a.count == b.count && a.getAuxValue() == b.getAuxValue();
    }

    // Useless - in b1.2 and LCE leaks
    public Inventory copy() {
        Inventory copy = new Inventory(null);
        for (int i = 0; i < this.items.length; i++) {
            copy.items[i] = this.items[i] != null ? this.items[i].copy() : null;
        }

        for (int i = 0; i < this.armor.length; i++) {
            copy.armor[i] = this.armor[i] != null ? this.armor[i].copy() : null;
        }
        return copy;
    }
    
    public void setCarried(final ItemInstance carried) {
        this.carried = carried;
        this.player.handleCollectItem(carried);
    }
    
    public ItemInstance getCarried() {
        return this.carried;
    }
    
    public boolean stillValid(final Player player) {
        if (this.player.removed) return false;
        if (player.distanceToSqr(this.player) > 8 * 8) return false;
        return true;
    }
    
    public boolean contains(final ItemInstance itemInstance) {
        for (int i = 0; i < this.armor.length; ++i) {
            if (this.armor[i] != null && this.armor[i].equals(itemInstance)) {
                return true;
            }
        }

        for (int j = 0; j < this.items.length; ++j) {
            if (this.items[j] != null && this.items[j].equals(itemInstance)) {
                return true;
            }
        }
        return false;
    }
}
