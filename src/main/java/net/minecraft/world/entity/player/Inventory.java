// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.player;

import net.minecraft.world.item.ArmorItem;
import com.mojang.nbt.Tag;
import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.Container;

public class Inventory implements Container
{
    public ItemInstance[] items;
    public ItemInstance[] armor;
    public int selected;
    public Player player;
    private ItemInstance carried;
    public boolean changed;
    
    public Inventory(final Player player) {
        this.items = new ItemInstance[36];
        this.armor = new ItemInstance[4];
        this.selected = 0;
        this.changed = false;
        this.player = player;
    }
    
    public ItemInstance getSelected() {
        if (this.selected < 9 && this.selected >= 0) {
            return this.items[this.selected];
        }
        return null;
    }
    
    private int getSlot(final int tileId) {
        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] != null && this.items[i].id == tileId) {
                return i;
            }
        }
        return -1;
    }
    
    private int getSlotWithRemainingSpace(final ItemInstance item) {
        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] != null && this.items[i].id == item.id && this.items[i].isStackable() && this.items[i].count < this.items[i].getMaxStackSize() && this.items[i].count < this.getMaxStackSize() && (!this.items[i].isStackedByData() || this.items[i].getAuxValue() == item.getAuxValue())) {
                return i;
            }
        }
        return -1;
    }
    
    private int getFreeSlot() {
        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] == null) {
                return i;
            }
        }
        return -1;
    }
    
    public void grabTexture(final int id, final boolean boolean2) {
        final int slot = this.getSlot(id);
        if (slot >= 0 && slot < 9) {
            this.selected = slot;
        }
    }
    
    public void swapPaint(int wheel) {
        if (wheel > 0) {
            wheel = 1;
        }
        if (wheel < 0) {
            wheel = -1;
        }
        this.selected -= wheel;
        while (this.selected < 0) {
            this.selected += 9;
        }
        while (this.selected >= 9) {
            this.selected -= 9;
        }
    }
    
    private int addResource(final ItemInstance itemInstance) {
        final int id = itemInstance.id;
        final int count = itemInstance.count;
        int n = this.getSlotWithRemainingSpace(itemInstance);
        if (n < 0) {
            n = this.getFreeSlot();
        }
        if (n < 0) {
            return count;
        }
        if (this.items[n] == null) {
            this.items[n] = new ItemInstance(id, 0, itemInstance.getAuxValue());
        }
        int n2 = count;
        if (n2 > this.items[n].getMaxStackSize() - this.items[n].count) {
            n2 = this.items[n].getMaxStackSize() - this.items[n].count;
        }
        if (n2 > this.getMaxStackSize() - this.items[n].count) {
            n2 = this.getMaxStackSize() - this.items[n].count;
        }
        if (n2 == 0) {
            return count;
        }
        final int n3 = count - n2;
        final ItemInstance itemInstance2 = this.items[n];
        itemInstance2.count += n2;
        this.items[n].popTime = 5;
        return n3;
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
        if (slot < 0) {
            return false;
        }
        final ItemInstance itemInstance = this.items[slot];
        if (--itemInstance.count <= 0) {
            this.items[slot] = null;
        }
        return true;
    }
    
    public boolean add(final ItemInstance item) {
        if (!item.isDamaged()) {
            int count;
            do {
                count = item.count;
                item.count = this.addResource(item);
            } while (item.count > 0 && item.count < count);
            return item.count < count;
        }
        final int freeSlot = this.getFreeSlot();
        if (freeSlot >= 0) {
            this.items[freeSlot] = ItemInstance.clone(item);
            this.items[freeSlot].popTime = 5;
            item.count = 0;
            return true;
        }
        return false;
    }
    
    public ItemInstance removeItem(int slot, final int count) {
        ItemInstance[] array = this.items;
        if (slot >= this.items.length) {
            array = this.armor;
            slot -= this.items.length;
        }
        if (array[slot] == null) {
            return null;
        }
        if (array[slot].count <= count) {
            final ItemInstance itemInstance = array[slot];
            array[slot] = null;
            return itemInstance;
        }
        final ItemInstance remove = array[slot].remove(count);
        if (array[slot].count == 0) {
            array[slot] = null;
        }
        return remove;
    }
    
    public void setItem(int slot, final ItemInstance item) {
        ItemInstance[] array = this.items;
        if (slot >= array.length) {
            slot -= array.length;
            array = this.armor;
        }
        array[slot] = item;
    }
    
    public float getDestroySpeed(final Tile tile) {
        float n = 1.0f;
        if (this.items[this.selected] != null) {
            n *= this.items[this.selected].getDestroySpeed(tile);
        }
        return n;
    }
    
    public ListTag save(final ListTag listTag) {
        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] != null) {
                final CompoundTag compoundTag = new CompoundTag();
                compoundTag.putByte("Slot", (byte)i);
                this.items[i].save(compoundTag);
                listTag.add(compoundTag);
            }
        }
        for (int j = 0; j < this.armor.length; ++j) {
            if (this.armor[j] != null) {
                final CompoundTag compoundTag2 = new CompoundTag();
                compoundTag2.putByte("Slot", (byte)(j + 100));
                this.armor[j].save(compoundTag2);
                listTag.add(compoundTag2);
            }
        }
        return listTag;
    }
    
    public void load(final ListTag inventoryList) {
        this.items = new ItemInstance[36];
        this.armor = new ItemInstance[4];
        for (int i = 0; i < inventoryList.size(); ++i) {
            final CompoundTag itemTag = (CompoundTag)inventoryList.get(i);
            final int n = itemTag.getByte("Slot") & 0xFF;
            final ItemInstance itemInstance = new ItemInstance(itemTag);
            if (itemInstance.getItem() != null) {
                if (n >= 0 && n < this.items.length) {
                    this.items[n] = itemInstance;
                }
                if (n >= 100 && n < this.armor.length + 100) {
                    this.armor[n - 100] = itemInstance;
                }
            }
        }
    }
    
    public int getContainerSize() {
        return this.items.length + 4;
    }
    
    public ItemInstance getItem(int slot) {
        ItemInstance[] array = this.items;
        if (slot >= array.length) {
            slot -= array.length;
            array = this.armor;
        }
        return array[slot];
    }
    
    public String getName() {
        return "Inventory";
    }
    
    public int getMaxStackSize() {
        return 64;
    }
    
    public int getAttackDamage(final Entity entity) {
        final ItemInstance item = this.getItem(this.selected);
        if (item != null) {
            return item.getAttackDamage(entity);
        }
        return 1;
    }
    
    public boolean canDestroy(final Tile tile) {
        if (tile.material.isDestroyedByHand()) {
            return true;
        }
        final ItemInstance item = this.getItem(this.selected);
        return item != null && item.canDestroySpecial(tile);
    }
    
    public ItemInstance getArmor(final int layer) {
        return this.armor[layer];
    }
    
    public int getArmorValue() {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        for (int i = 0; i < this.armor.length; ++i) {
            if (this.armor[i] != null && this.armor[i].getItem() instanceof ArmorItem) {
                final int maxDamage = this.armor[i].getMaxDamage();
                n2 += maxDamage - this.armor[i].getDamageValue();
                n3 += maxDamage;
                n += ((ArmorItem)this.armor[i].getItem()).defense;
            }
        }
        if (n3 == 0) {
            return 0;
        }
        return (n - 1) * n2 / n3 + 1;
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
    
    public void setCarried(final ItemInstance carried) {
        this.carried = carried;
        this.player.handleCollectItem(carried);
    }
    
    public ItemInstance getCarried() {
        return this.carried;
    }
    
    public boolean stillValid(final Player player) {
        return !this.player.removed && player.distanceToSqr(this.player) <= 64.0;
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
