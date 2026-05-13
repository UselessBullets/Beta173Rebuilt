// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.network.packet.Packet;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ComplexItem extends Item
{
    protected ComplexItem(final int id) {
        super(id);
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    public Packet getUpdatePacket(final ItemInstance itemInstance, final Level level, final Player player) {
        return null;
    }
}
