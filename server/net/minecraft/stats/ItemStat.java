// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

public class ItemStat extends Stat
{
    private final int itemId;
    
    public ItemStat(final int id, final String name, final int itemId) {
        super(id, name);
        this.itemId = itemId;
    }
}
