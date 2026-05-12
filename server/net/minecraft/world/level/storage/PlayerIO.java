// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import net.minecraft.world.entity.player.Player;

public interface PlayerIO
{
    void save(final Player player);
    
    void load(final Player player);
}
