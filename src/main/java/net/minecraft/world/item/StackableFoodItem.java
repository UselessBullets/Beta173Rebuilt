// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

public class StackableFoodItem extends FoodItem
{
    public StackableFoodItem(final int id, final int nutrition, final boolean isMeat, final int maxStackSize) {
        super(id, nutrition, isMeat);
        this.maxStackSize = maxStackSize;
    }
}
