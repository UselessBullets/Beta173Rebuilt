// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.level;

import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.server.MinecraftServer;

public class DerivedServerLevel extends ServerLevel
{
    public DerivedServerLevel(final MinecraftServer server, final LevelStorage levelStorage, final String levelName, final int dimension, final long seed, final ServerLevel wrapped) {
        super(server, levelStorage, levelName, dimension, seed);
        this.savedDataStorage = wrapped.savedDataStorage;
    }
}
