// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.level.tile.Tile;

public class ShovelItem extends DiggerItem
{
    private static Tile[] diggables = new Tile[] {
            Tile.grass,
            Tile.dirt,
            Tile.sand,
            Tile.gravel,
            Tile.topSnow,
            Tile.snow,
            Tile.clay,
            Tile.farmland };
    
    public ShovelItem(final int id, final Tier tier) {
        super(id, 1, tier, ShovelItem.diggables);
    }
    
    @Override
    public boolean canDestroySpecial(final Tile tile) {
        if (tile == Tile.topSnow) return true;
        if (tile == Tile.snow) return true;
        return false;
    }

}
