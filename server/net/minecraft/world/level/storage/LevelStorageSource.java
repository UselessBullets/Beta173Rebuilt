// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import util.ProgressListener;

public interface LevelStorageSource
{
    boolean requiresConversion(final String levelId);
    
    boolean convertLevel(final String levelId, final ProgressListener progress);
}
