// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.Minecart;
import net.minecraft.world.level.tile.RailTile;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

public class MinecartItem extends Item
{
    public int type;
    
    public MinecartItem(final int id, final int type) {
        super(id);
        this.maxStackSize = 1;
        this.type = type;
    }
    
    @Override
    public boolean useOn(final ItemInstance itemInstance, final Player player, final Level level, final int x, final int y, final int z, final int face) {
        if (RailTile.isRail(level.getTile(x, y, z))) {
            if (!level.isClientSide) {
                level.addEntity(new Minecart(level, x + 0.5f, y + 0.5f, z + 0.5f, this.type));
            }
            --itemInstance.count;
            return true;
        }
        return false;
    }
}
