// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import java.util.HashMap;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.ref.Reference;
import java.io.File;
import java.util.Map;

public class RegionFileCache
{
    private static final int MAX_CACHE_SIZE = 256;
    private static final Map<File, Reference<RegionFile>> defaultCache = new HashMap<>();
    
    private RegionFileCache() {
    }
    
    public static synchronized RegionFile getRegionFile(final File saveFile, final int chunkX, final int chunkZ) {
        final File regionDir = new File(saveFile, "region");

        final File file = new File(regionDir, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mcr");
        final Reference<RegionFile> ref = RegionFileCache.defaultCache.get(file);
        if (ref != null) {
            final RegionFile regionFile = ref.get();
            if (regionFile != null) return regionFile;
        }

        if (!regionDir.exists()) {
            regionDir.mkdirs();
        }

        if (RegionFileCache.defaultCache.size() >= MAX_CACHE_SIZE) {
            clear();
        }

        final RegionFile reg = new RegionFile(file);
        RegionFileCache.defaultCache.put(file, new SoftReference<>(reg));
        return reg;
    }
    
    public static synchronized void clear() {
        for (final Reference<RegionFile> reference : RegionFileCache.defaultCache.values()) {
            try {
                final RegionFile regionFile = reference.get();
                if (regionFile != null) {
                    regionFile.close();
                }
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }

        RegionFileCache.defaultCache.clear();
    }
    
    public static int getSizeDelta(final File saveFile, final int chunkX, final int chunkZ) {
        RegionFile r = getRegionFile(saveFile, chunkX, chunkZ);
        return r.getSizeDelta();
    }
    
    public static DataInputStream getChunkDataInputStream(final File saveFile, final int chunkX, final int chunkZ) {
        RegionFile r = getRegionFile(saveFile, chunkX, chunkZ);
        return r.getChunkDataInputStream(chunkX & 0x1F, chunkZ & 0x1F);
    }
    
    public static DataOutputStream getChunkDataOutputStream(final File saveFile, final int chunkX, final int chunkZ) {
        RegionFile r = getRegionFile(saveFile, chunkX, chunkZ);
        return r.getChunkDataOutputStream(chunkX & 0x1F, chunkZ & 0x1F);
    }

}
