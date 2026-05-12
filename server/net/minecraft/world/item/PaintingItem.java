// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Painting;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

public class PaintingItem extends Item
{
    public PaintingItem(final int id) {
        super(id);
    }
    
    @Override
    public boolean useOn(final ItemInstance itemInstance, final Player player, final Level level, final int x, final int y, final int z, final int face) {
        if (face == 0) {
            return false;
        }
        if (face == 1) {
            return false;
        }
        int dir = 0;
        if (face == 4) {
            dir = 1;
        }
        if (face == 3) {
            dir = 2;
        }
        if (face == 5) {
            dir = 3;
        }
        final Painting e = new Painting(level, x, y, z, dir);
        if (e.survives()) {
            if (!level.isClientSide) {
                level.addEntity(e);
            }
            --itemInstance.count;
        }
        return true;
    }
}
