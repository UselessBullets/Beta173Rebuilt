// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import net.minecraft.world.level.LevelSummary;
import util.ProgressListener;
import net.minecraft.world.level.LevelData;
import java.util.List;

public interface LevelStorageSource
{
    String getName();
    
    LevelStorage selectLevel(final String levelId, final boolean createPlayerDir);
    
    List<LevelSummary> getLevelList();
    
    void clearAll();
    
    LevelData getDataTagFor(final String levelId);

    /**
     * Tests if a levelId can be used to store a level. For example, a levelId
     * can't be called COM1 on Windows systems, because that is a reserved file
     * handle.
     * <p>
     * Also, a new levelId may not overwrite an existing one.
     *
     * @param levelId
     * @return
     */
    // Useless - In LCE in a between methods that do exist here
    boolean isNewLevelIdAcceptable(String levelId);
    void deleteLevel(final String levelId);
    
    void renameLevel(final String levelId, final String newLevelName);

    // Useless - In LCE in a between methods that do exist here
    boolean isConvertible(final String levelId);
    boolean requiresConversion(final String levelId);
    
    boolean convertLevel(final String levelId, final ProgressListener progress);
}
