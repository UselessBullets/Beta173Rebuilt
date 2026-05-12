// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.inventory;

import net.minecraft.world.item.ItemInstance;

public interface ContainerListener
{
    void slotChanged(final AbstractContainerMenu container, final int slotIndex, final ItemInstance item);
    
    void setContainerData(final AbstractContainerMenu container, final int id, final int value);
}
