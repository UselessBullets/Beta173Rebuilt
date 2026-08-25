// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;

public class PickaxeItem extends DiggerItem
{
    private static Tile[] diggables = new Tile[] {
            Tile.stoneBrick,
            Tile.stoneSlab,
            Tile.stoneSlabHalf,
            Tile.rock,
            Tile.sandStone,
            Tile.mossStone,
            Tile.ironOre,
            Tile.ironBlock,
            Tile.coalOre,
            Tile.goldBlock,
            Tile.goldOre,
            Tile.emeraldOre,
            Tile.emeraldBlock,
            Tile.ice,
            Tile.hellRock,
            Tile.lapisOre,
            Tile.lapisBlock };
    
    protected PickaxeItem(final int id, final Tier tier) {
        super(id, 2, tier, PickaxeItem.diggables);
    }
    
    @Override
    public boolean canDestroySpecial(final Tile tile) {
        if (tile == Tile.obsidian) return this.tier.getLevel() == 3;
        if (tile == Tile.emeraldBlock || tile == Tile.emeraldOre) return this.tier.getLevel() >= 2;
        if (tile == Tile.goldBlock || tile == Tile.goldOre) return this.tier.getLevel() >= 2;
        if (tile == Tile.ironBlock || tile == Tile.ironOre) return this.tier.getLevel() >= 1;
        if (tile == Tile.lapisBlock || tile == Tile.lapisOre) return this.tier.getLevel() >= 1;
        if (tile == Tile.redStoneOre || tile == Tile.redStoneOre_lit) return this.tier.getLevel() >= 2;
        if (tile.material == Material.stone) return true;
        if (tile.material == Material.metal) return true;
        return false;
    }

}
