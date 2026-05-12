// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import java.util.HashMap;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.util.Iterator;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.ref.Reference;
import java.io.File;
import java.util.Map;

public class RegionFileCache
{
    private static final Map defaultCache;
    
    private RegionFileCache() {
    }
    
    public static synchronized RegionFile getRegionFile(final File saveFile, final int chunkX, final int chunkZ) {
        final File parent = new File(saveFile, "region");
        final File path = new File(parent, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mcr");
        final Reference reference = RegionFileCache.defaultCache.get(path);
        if (reference != null) {
            final RegionFile regionFile = (RegionFile)reference.get();
            if (regionFile != null) {
                return regionFile;
            }
        }
        if (!parent.exists()) {
            parent.mkdirs();
        }
        if (RegionFileCache.defaultCache.size() >= 256) {
            clear();
        }
        final RegionFile referent = new RegionFile(path);
        RegionFileCache.defaultCache.put(path, new SoftReference(referent));
        return referent;
    }
    
    public static synchronized void clear() {
        for (final Reference reference : RegionFileCache.defaultCache.values()) {
            try {
                final RegionFile regionFile = (RegionFile)reference.get();
                if (regionFile == null) {
                    continue;
                }
                regionFile.close();
            }
            catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
        RegionFileCache.defaultCache.clear();
    }
    
    public static int getSizeDelta(final File saveFile, final int chunkX, final int chunkZ) {
        return getRegionFile(saveFile, chunkX, chunkZ).getSizeDelta();
    }
    
    public static DataInputStream getChunkDataInputStream(final File saveFile, final int chunkX, final int chunkZ) {
        return getRegionFile(saveFile, chunkX, chunkZ).getChunkDataInputStream(chunkX & 0x1F, chunkZ & 0x1F);
    }
    
    public static DataOutputStream getChunkDataOutputStream(final File saveFile, final int chunkX, final int chunkZ) {
        return getRegionFile(saveFile, chunkX, chunkZ).getChunkDataOutputStream(chunkX & 0x1F, chunkZ & 0x1F);
    }
    
    static {
        defaultCache = new HashMap();
    }
}
