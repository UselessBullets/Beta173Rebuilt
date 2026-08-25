// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.level.tile.Tile;

public class HatchetItem extends DiggerItem
{
    private static Tile[] diggables = new Tile[] {
            Tile.wood,
            Tile.bookshelf,
            Tile.treeTrunk,
            Tile.chest
    };
    
    protected HatchetItem(final int id, final Tier tier) {
        super(id, 3, tier, HatchetItem.diggables);
    }

}
