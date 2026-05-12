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
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.baseDir = dir;
    }
    
    public String getName() {
        return "Old Format";
    }
    
    public List<LevelSummary> getLevelList() {
        final ArrayList<LevelSummary> list = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            final String string = "World" + (i + 1);
            final LevelData dataTag = this.getDataTagFor(string);
            if (dataTag != null) {
                list.add(new LevelSummary(string, "", dataTag.getLastPlayed(), dataTag.getSizeOnDisk(), false));
            }
        }
        return list;
    }
    
    public void clearAll() {
    }
    
    public LevelData getDataTagFor(final String levelId) {
        final File file = new File(this.baseDir, levelId);
        if (!file.exists()) {
            return null;
        }
        final File file2 = new File(file, "level.dat");
        if (file2.exists()) {
            try {
                return new LevelData(NbtIo.readCompressed(new FileInputStream(file2)).getCompound("Data"));
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        final File file3 = new File(file, "level.dat_old");
        if (file3.exists()) {
            try {
                return new LevelData(NbtIo.readCompressed(new FileInputStream(file3)).getCompound("Data"));
            }
            catch (final Exception ex2) {
                ex2.printStackTrace();
            }
        }
        return null;
    }
    
    public void renameLevel(final String levelId, final String newLevelName) {
        final File parent = new File(this.baseDir, levelId);
        if (!parent.exists()) {
            return;
        }
        final File file = new File(parent, "level.dat");
        if (file.exists()) {
            try {
                final CompoundTag compressed = NbtIo.readCompressed(new FileInputStream(file));
                compressed.getCompound("Data").putString("LevelName", newLevelName);
                NbtIo.writeCompressed(compressed, new FileOutputStream(file));
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void deleteLevel(final String levelId) {
        final File file = new File(this.baseDir, levelId);
        if (!file.exists()) {
            return;
        }
        deleteRecursive(file.listFiles());
        file.delete();
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
