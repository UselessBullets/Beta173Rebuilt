// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import java.util.regex.Matcher;
import java.io.File;

class McRegionLevelStorageSource_ChunkFile implements Comparable
{
    private final File file;
    private final int x;
    private final int z;
    
    public McRegionLevelStorageSource_ChunkFile(final File file) {
        this.file = file;
        final Matcher matcher = McRegionLevelStorageSource_ChunkFilter.chunkFilePattern.matcher(file.getName());
        if (matcher.matches()) {
            this.x = Integer.parseInt(matcher.group(1), 36);
            this.z = Integer.parseInt(matcher.group(2), 36);
        }
        else {
            this.x = 0;
            this.z = 0;
        }
    }
    
    public int compareTo(final McRegionLevelStorageSource_ChunkFile rhs) {
        final int n = this.x >> 5;
        final int n2 = rhs.x >> 5;
        if (n == n2) {
            return (this.z >> 5) - (rhs.z >> 5);
        }
        return n - n2;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getZ() {
        return this.z;
    }
}
