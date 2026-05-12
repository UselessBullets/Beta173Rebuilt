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
    private static final Map a;
    
    private RegionFileCache() {
    }
    
    public static synchronized RegionFile getRegionFile(final File file, final int integer2, final int integer3) {
        final File parent = new File(file, "region");
        final File path = new File(parent, "r." + (integer2 >> 5) + "." + (integer3 >> 5) + ".mcr");
        final Reference reference = RegionFileCache.a.get(path);
        if (reference != null) {
            final RegionFile regionFile = (RegionFile)reference.get();
            if (regionFile != null) {
                return regionFile;
            }
        }
        if (!parent.exists()) {
            parent.mkdirs();
        }
        if (RegionFileCache.a.size() >= 256) {
            clear();
        }
        final RegionFile referent = new RegionFile(path);
        RegionFileCache.a.put(path, new SoftReference(referent));
        return referent;
    }
    
    public static synchronized void clear() {
        for (final Reference reference : RegionFileCache.a.values()) {
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
        RegionFileCache.a.clear();
    }
    
    public static int getSizeDelta(final File file, final int integer2, final int integer3) {
        return getRegionFile(file, integer2, integer3).getSizeDelta();
    }
    
    public static DataInputStream getChunkDataInputStream(final File file, final int integer2, final int integer3) {
        return getRegionFile(file, integer2, integer3).getChunkDataInputStream(integer2 & 0x1F, integer3 & 0x1F);
    }
    
    public static DataOutputStream getChunkDataOutputStream(final File file, final int integer2, final int integer3) {
        return getRegionFile(file, integer2, integer3).getChunkDataOutputStream(integer2 & 0x1F, integer3 & 0x1F);
    }
    
    static {
        a = new HashMap();
    }
}
