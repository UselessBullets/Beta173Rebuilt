// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

public class LevelSummary implements Comparable<LevelSummary>
{
    private final String levelId;
    private final String levelName;
    private final long lastPlayed;
    private final long sizeOnDisk;
    private final boolean requiresConversion;
    
    public LevelSummary(final String levelId, final String levelName, final long lastPlayed, final long sizeOnDisk, final boolean requiresConversion) {
        this.levelId = levelId;
        this.levelName = levelName;
        this.lastPlayed = lastPlayed;
        this.sizeOnDisk = sizeOnDisk;
        this.requiresConversion = requiresConversion;
    }
    
    public String getLevelId() {
        return this.levelId;
    }
    
    public String getLevelName() {
        return this.levelName;
    }
    
    public long getSizeOnDisk() {
        return this.sizeOnDisk;
    }
    
    public boolean isRequiresConversion() {
        return this.requiresConversion;
    }
    
    public long getLastPlayed() {
        return this.lastPlayed;
    }
    
    public int compareTo(final LevelSummary rhs) {
        if (this.lastPlayed < rhs.lastPlayed) {
            return 1;
        }
        if (this.lastPlayed > rhs.lastPlayed) {
            return -1;
        }

        return this.levelId.compareTo(rhs.levelId);
    }
}
