// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import java.io.File;
import java.util.regex.Pattern;
import java.io.FileFilter;

class McRegionLevelStorageSource_FolderFilter implements FileFilter
{
    public static final Pattern chunkFolderPattern;
    
    private McRegionLevelStorageSource_FolderFilter() {
    }
    
    public boolean accept(final File file) {
        return file.isDirectory() && McRegionLevelStorageSource_FolderFilter.chunkFolderPattern.matcher(file.getName()).matches();
    }
    
    static {
        chunkFolderPattern = Pattern.compile("[0-9a-z]|([0-9a-z][0-9a-z])");
    }
}
