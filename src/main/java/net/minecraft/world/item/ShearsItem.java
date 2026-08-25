// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.Mob;

public class ShearsItem extends Item
{
    public ShearsItem(final int id) {
        super(id);
        this.setMaxStackSize(1);
        this.setMaxDamage(238);
    }
    
    @Override
    public boolean mineBlock(final ItemInstance itemInstance, final int tile, final int x, final int y, final int z, final Mob owner) {
        if (tile == Tile.leaves.id || tile == Tile.web.id) {
            itemInstance.hurt(1, owner);
        }
        return super.mineBlock(itemInstance, tile, x, y, z, owner);
    }
    
    @Override
    public boolean canDestroySpecial(final Tile tile) {
        return tile.id == Tile.web.id;
    }
    
    @Override
    public float getDestroySpeed(final ItemInstance itemInstance, final Tile tile) {
        if (tile.id == Tile.web.id || tile.id == Tile.leaves.id) {
            return 15.0f;
        }
        if (tile.id == Tile.cloth.id) {
            return 5.0f;
        }
        return super.getDestroySpeed(itemInstance, tile);
    }
}
