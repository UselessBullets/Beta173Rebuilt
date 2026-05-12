// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import java.io.File;
import java.util.regex.Pattern;
import java.io.FilenameFilter;

class McRegionLevelStorageSource_ChunkFilter implements FilenameFilter
{
    public static final Pattern chunkFilePattern;
    
    private McRegionLevelStorageSource_ChunkFilter() {
    }
    
    public boolean accept(final File file, final String string) {
        return McRegionLevelStorageSource_ChunkFilter.chunkFilePattern.matcher(string).matches();
    }
    
    static {
        chunkFilePattern = Pattern.compile("c\\.(-?[0-9a-z]+)\\.(-?[0-9a-z]+)\\.dat");
    }
}
