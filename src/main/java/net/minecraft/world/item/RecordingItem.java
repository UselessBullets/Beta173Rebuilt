// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.level.tile.RecordPlayerTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

public class RecordingItem extends Item
{
    public final String recording;
    
    protected RecordingItem(final int id, final String recording) {
        super(id);
        this.recording = recording;
        this.maxStackSize = 1;
    }
    
    @Override
    public boolean useOn(final ItemInstance itemInstance, final Player player, final Level level, final int x, final int y, final int z, final int face) {
        if (level.getTile(x, y, z) != Tile.recordPlayer.id || level.getData(x, y, z) != 0) {
            return false;
        }
        if (level.isClientSide) {
            return true;
        }
        ((RecordPlayerTile)Tile.recordPlayer).setRecord(level, x, y, z, this.id);
        level.levelEvent(null, 1005, x, y, z, this.id);
        --itemInstance.count;
        return true;
    }
}
