// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import com.mojang.nbt.CompoundTag;
import java.util.List;
import com.mojang.nbt.NbtIo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelData;
import net.minecraft.world.level.chunk.storage.OldChunkStorage;
import net.minecraft.world.level.dimension.HellDimension;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.dimension.Dimension;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.logging.Logger;

public class DirectoryLevelStorage implements PlayerIO, LevelStorage
{
    private static final Logger logger = Logger.getLogger("Minecraft");
    private final File dir;
    private final File playerDir;
    private final File dataDir;
    private final long sessionId = System.currentTimeMillis();
    
    public DirectoryLevelStorage(final File dir, final String levelId, final boolean createPlayerDir) {
        this.dir = new File(dir, levelId);
        this.dir.mkdirs();
        this.playerDir = new File(this.dir, "players");
        this.dataDir = new File(this.dir, "data");
        this.dataDir.mkdirs();
        if (createPlayerDir) {
            this.playerDir.mkdirs();
        }
        this.initiateSession();
    }
    
    private void initiateSession() {
        try {
            File dataFile = new File(this.dir, "session.lock");
            FileOutputStream fos = new FileOutputStream(dataFile);
            final DataOutputStream dos = new DataOutputStream(fos);
            try {
                dos.writeLong(this.sessionId);
            }
            finally {
                dos.close();
            }
        }
        catch (final IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to check session lock, aborting");
        }
    }
    
    protected File getFolder() {
        return this.dir;
    }
    
    public void checkSession() {
        try {
            File dataFile = new File(this.dir, "session.lock");
            FileInputStream fis = new FileInputStream(dataFile);
            final DataInputStream dis = new DataInputStream(fis);
            try {
                if (dis.readLong() != this.sessionId) throw new LevelStorageException("The save is being accessed from another location, aborting");
            }
            finally {
                dis.close();
            }
        }
        catch (final IOException e) {
            throw new LevelStorageException("Failed to check session lock, aborting");
        }
    }
    
    public ChunkStorage createChunkStorage(final Dimension dimension) {
        if (dimension instanceof HellDimension) {
            final File dir2 = new File(this.dir, "DIM-1");
            dir2.mkdirs();
            return new OldChunkStorage(dir2, true);
        }
        return new OldChunkStorage(this.dir, true);
    }
    
    public LevelData prepareLevel() {
        File dataFile = new File(this.dir, "level.dat");

        if (dataFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(dataFile);
                CompoundTag root = NbtIo.readCompressed(fis);
                CompoundTag tag = root.getCompound("Data");
                return new LevelData(tag);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }

        dataFile = new File(this.dir, "level.dat_old");
        if (dataFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(dataFile);
                CompoundTag root = NbtIo.readCompressed(fis);
                CompoundTag tag = root.getCompound("Data");
                return new LevelData(tag);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
    
    public void saveLevelData(final LevelData levelData, final List<Player> players) {
        final CompoundTag dataTag = levelData.createTag(players);

        final CompoundTag root = new CompoundTag();
        root.put("Data", dataTag);

        try {
            final File newDataFile = new File(this.dir, "level.dat_new");
            final File oldDataFile = new File(this.dir, "level.dat_old");
            final File dataFile = new File(this.dir, "level.dat");

            FileOutputStream fos = new FileOutputStream(newDataFile);
            NbtIo.writeCompressed(root, fos);

            if (oldDataFile.exists()) oldDataFile.delete();

            dataFile.renameTo(oldDataFile);
            if (dataFile.exists()) dataFile.delete();

            newDataFile.renameTo(dataFile);
            if (newDataFile.exists()) newDataFile.delete();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void saveLevelData(final LevelData levelData) {
        final CompoundTag dataTag = levelData.createTag();

        final CompoundTag root = new CompoundTag();
        root.put("Data", dataTag);

        try {
            final File newDataFile = new File(this.dir, "level.dat_new");
            final File oldDataFile = new File(this.dir, "level.dat_old");
            final File dataFile = new File(this.dir, "level.dat");

            FileOutputStream fos = new FileOutputStream(newDataFile);
            NbtIo.writeCompressed(root, fos);

            if (oldDataFile.exists()) oldDataFile.delete();

            dataFile.renameTo(oldDataFile);
            if (dataFile.exists()) dataFile.delete();

            newDataFile.renameTo(dataFile);
            if (newDataFile.exists()) newDataFile.delete();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(final Player player) {
        try {
            final CompoundTag tag = new CompoundTag();
            player.saveWithoutId(tag);

            final File tempFile = new File(this.playerDir, "_tmp_.dat");
            final File realFile = new File(this.playerDir, player.name + ".dat");

            FileOutputStream fos = new FileOutputStream(tempFile);
            NbtIo.writeCompressed(tag, fos);

            if (realFile.exists()) realFile.delete();

            tempFile.renameTo(realFile);
        }
        catch (final Exception e) {
            DirectoryLevelStorage.logger.warning("Failed to save player data for " + player.name);
        }
    }

    @Override
    public void load(final Player player) {
        final CompoundTag tag = this.loadPlayerDataTag(player.name);
        if (tag != null) {
            player.load(tag);
        }
    }

    @Override
    public CompoundTag loadPlayerDataTag(final String userName) {
        try {
            final File realFile = new File(this.playerDir, userName + ".dat");
            if (realFile.exists()) {
                FileInputStream fis = new FileInputStream(realFile);
                return NbtIo.readCompressed(fis);
            }
        }
        catch (final Exception e) {
            DirectoryLevelStorage.logger.warning("Failed to load player data for " + userName);
        }
        return null;
    }

    @Override
    public PlayerIO getPlayerIO() {
        return this;
    }

    @Override
    public void closeAll() {

    }

    @Override
    public File getDataFile(final String id) {
        return new File(this.dataDir, id + ".dat");
    }

}
