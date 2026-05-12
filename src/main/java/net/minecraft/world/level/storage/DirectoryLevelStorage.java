// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import com.mojang.nbt.Tag;
import com.mojang.nbt.CompoundTag;
import java.util.List;
import com.mojang.nbt.NbtIo;
import net.minecraft.world.level.LevelData;
import net.minecraft.world.level.chunk.storage.OldChunkStorage;
import net.minecraft.world.level.dimension.HellDimension;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.dimension.Dimension;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.logging.Logger;

public class DirectoryLevelStorage implements LevelStorage
{
    private static final Logger logger;
    private final File dir;
    private final File playerDir;
    private final File dataDir;
    private final long sessionId;
    
    public DirectoryLevelStorage(final File dir, final String levelId, final boolean createPlayerDir) {
        this.sessionId = System.currentTimeMillis();
        (this.dir = new File(dir, levelId)).mkdirs();
        this.playerDir = new File(this.dir, "players");
        (this.dataDir = new File(this.dir, "data")).mkdirs();
        if (createPlayerDir) {
            this.playerDir.mkdirs();
        }
        this.initiateSession();
    }
    
    private void initiateSession() {
        try {
            final DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(new File(this.dir, "session.lock")));
            try {
                dataOutputStream.writeLong(this.sessionId);
            }
            finally {
                dataOutputStream.close();
            }
        }
        catch (final IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to check session lock, aborting");
        }
    }
    
    protected File getFolder() {
        return this.dir;
    }
    
    public void checkSession() {
        try {
            final DataInputStream dataInputStream = new DataInputStream(new FileInputStream(new File(this.dir, "session.lock")));
            try {
                if (dataInputStream.readLong() != this.sessionId) {
                    throw new LevelStorageException("The save is being accessed from another location, aborting");
                }
            }
            finally {
                dataInputStream.close();
            }
        }
        catch (final IOException ex) {
            throw new LevelStorageException("Failed to check session lock, aborting");
        }
    }
    
    public ChunkStorage createChunkStorage(final Dimension dimension) {
        if (dimension instanceof HellDimension) {
            final File dir = new File(this.dir, "DIM-1");
            dir.mkdirs();
            return new OldChunkStorage(dir, true);
        }
        return new OldChunkStorage(this.dir, true);
    }
    
    public LevelData prepareLevel() {
        final File file = new File(this.dir, "level.dat");
        if (file.exists()) {
            try {
                return new LevelData(NbtIo.readCompressed(new FileInputStream(file)).getCompound("Data"));
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        final File file2 = new File(this.dir, "level.dat_old");
        if (file2.exists()) {
            try {
                return new LevelData(NbtIo.readCompressed(new FileInputStream(file2)).getCompound("Data"));
            }
            catch (final Exception ex2) {
                ex2.printStackTrace();
            }
        }
        return null;
    }
    
    public void saveLevelData(final LevelData levelData, final List players) {
        final CompoundTag tag = levelData.createTag(players);
        final CompoundTag tag2 = new CompoundTag();
        tag2.put("Data", tag);
        try {
            final File file = new File(this.dir, "level.dat_new");
            final File dest = new File(this.dir, "level.dat_old");
            final File dest2 = new File(this.dir, "level.dat");
            NbtIo.writeCompressed(tag2, new FileOutputStream(file));
            if (dest.exists()) {
                dest.delete();
            }
            dest2.renameTo(dest);
            if (dest2.exists()) {
                dest2.delete();
            }
            file.renameTo(dest2);
            if (file.exists()) {
                file.delete();
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void saveLevelData(final LevelData levelData) {
        final CompoundTag tag = levelData.createTag();
        final CompoundTag tag2 = new CompoundTag();
        tag2.put("Data", tag);
        try {
            final File file = new File(this.dir, "level.dat_new");
            final File dest = new File(this.dir, "level.dat_old");
            final File dest2 = new File(this.dir, "level.dat");
            NbtIo.writeCompressed(tag2, new FileOutputStream(file));
            if (dest.exists()) {
                dest.delete();
            }
            dest2.renameTo(dest);
            if (dest2.exists()) {
                dest2.delete();
            }
            file.renameTo(dest2);
            if (file.exists()) {
                file.delete();
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public File getDataFile(final String id) {
        return new File(this.dataDir, id + ".dat");
    }
    
    static {
        logger = Logger.getLogger("Minecraft");
    }
}
