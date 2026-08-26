// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import util.ProgressListener;
import com.mojang.nbt.CompoundTag;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import com.mojang.nbt.NbtIo;
import java.io.FileInputStream;
import net.minecraft.world.level.LevelData;
import net.minecraft.world.level.LevelSummary;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class DirectoryLevelStorageSource implements LevelStorageSource
{
    protected final File baseDir;
    
    public DirectoryLevelStorageSource(final File dir) {
        if (!dir.exists()) dir.mkdirs();
        this.baseDir = dir;
    }
    
    public String getName() {
        return "Old Format";
    }
    
    public List<LevelSummary> getLevelList() {
        final ArrayList<LevelSummary> levels = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            final String levelId = "World" + (i + 1);

            final LevelData levelData = this.getDataTagFor(levelId);
            if (levelData != null) {
                levels.add(new LevelSummary(levelId, "", levelData.getLastPlayed(), levelData.getSizeOnDisk(), false));
            }
        }
        return levels;
    }
    
    public void clearAll() {
    }
    
    public LevelData getDataTagFor(final String levelId) {
        final File levelFolder = new File(this.baseDir, levelId);
        if (!levelFolder.exists()) return null;

        File dataFile = new File(levelFolder, "level.dat");
        if (dataFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(dataFile);
                CompoundTag root = NbtIo.readCompressed(fis);
                CompoundTag tag = root.getCompound("Data");
                return new LevelData(tag);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        dataFile = new File(levelFolder, "level.dat_old");
        if (dataFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(dataFile);
                CompoundTag root = NbtIo.readCompressed(fis);
                CompoundTag tag = root.getCompound("Data");
                return new LevelData(tag);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    // Useless - In LCE in a between methods that do exist here
    public boolean isNewLevelIdAcceptable(String levelId) {
        try {
            File levelFolder = new File(this.baseDir, levelId);
            if (levelFolder.exists()) {
                return false;
            }

            levelFolder.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public void renameLevel(final String levelId, final String newLevelName) {
        final File levelFolder = new File(this.baseDir, levelId);
        if (!levelFolder.exists()) return;

        final File dataFile = new File(levelFolder, "level.dat");
        if (dataFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(dataFile);
                final CompoundTag root = NbtIo.readCompressed(fis);
                CompoundTag tag = root.getCompound("Data");
                tag.putString("LevelName", newLevelName);

                FileOutputStream fos = new FileOutputStream(dataFile);
                NbtIo.writeCompressed(root, fos);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    // Useless - In LCE in a between methods that do exist here
    public boolean isConvertible(String levelId) {
        return false;
    }

    public void deleteLevel(final String levelId) {
        final File levelFolder = new File(this.baseDir, levelId);
        if (!levelFolder.exists()) return;

        deleteRecursive(levelFolder.listFiles());
        levelFolder.delete();
    }
    
    protected static void deleteRecursive(final File[] files) {
        for (int i = 0; i < files.length; ++i) {
            if (files[i].isDirectory()) {
                deleteRecursive(files[i].listFiles());
            }
            files[i].delete();
        }
    }
    
    public LevelStorage selectLevel(final String levelId, final boolean createPlayerDir) {
        return new DirectoryLevelStorage(this.baseDir, levelId, createPlayerDir);
    }
    
    public boolean requiresConversion(final String levelId) {
        return false;
    }
    
    public boolean convertLevel(final String levelId, final ProgressListener progress) {
        return false;
    }
}
