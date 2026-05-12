// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import util.ProgressListener;
import net.minecraft.world.level.LevelData;
import java.util.List;

public interface LevelStorageSource
{
    String getName();
    
    LevelStorage selectLevel(final String levelId, final boolean createPlayerDir);
    
    List getLevelList();
    
    void clearAll();
    
    LevelData getDataTagFor(final String levelId);
    
    void deleteLevel(final String levelId);
    
    void renameLevel(final String levelId, final String newLevelName);
    
    boolean requiresConversion(final String levelId);
    
    boolean convertLevel(final String levelId, final ProgressListener progress);
}
