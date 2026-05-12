// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.level.tile.Tile;

public class ShovelItem extends DiggerItem
{
    private static Tile[] diggables;
    
    public ShovelItem(final int id, final Item_Tier tier) {
        super(id, 1, tier, ShovelItem.diggables);
    }
    
    @Override
    public boolean canDestroySpecial(final Tile tile) {
        return tile == Tile.topSnow || tile == Tile.snow;
    }
    
    static {
        ShovelItem.diggables = new Tile[] { Tile.grass, Tile.dirt, Tile.sand, Tile.gravel, Tile.topSnow, Tile.snow, Tile.clay, Tile.farmland };
    }
}
