// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.inventory;

import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.Container;

class ArmorSlot extends Slot
{
    final /* synthetic */ int slotNum;
    final /* synthetic */ InventoryMenu im;
    
    ArmorSlot(final InventoryMenu im, final Container container, final int id, final int x, final int y, final int slotNum) {
        this.im = im;
        this.slotNum = slotNum;
        super(container, id, x, y);
    }
    
    @Override
    public int getMaxStackSize() {
        return 1;
    }
    
    @Override
    public boolean mayPlace(final ItemInstance item) {
        if (item.getItem() instanceof ArmorItem) {
            return ((ArmorItem)item.getItem()).slot == this.slotNum;
        }
        return item.getItem().id == Tile.pumpkin.id && this.slotNum == 0;
    }
}
