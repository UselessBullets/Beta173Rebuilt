// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

public class ArmorItem extends Item
{
    private static final int[] defensePerSlot;
    private static final int[] healthPerSlot;
    public final int tier;
    public final int slot;
    public final int defense;
    public final int materialIcon;
    
    public ArmorItem(final int id, final int tier, final int materialIcon, final int slot) {
        super(id);
        this.tier = tier;
        this.slot = slot;
        this.materialIcon = materialIcon;
        this.defense = ArmorItem.defensePerSlot[slot];
        this.setMaxDamage(ArmorItem.healthPerSlot[slot] * 3 << tier);
        this.maxStackSize = 1;
    }
    
    static {
        defensePerSlot = new int[] { 3, 8, 6, 3 };
        healthPerSlot = new int[] { 11, 16, 15, 13 };
    }
}
