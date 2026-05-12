// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import java.io.DataOutputStream;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.io.FileInputStream;
import java.util.Collections;

import util.ProgressListener;
import net.minecraft.world.level.LevelData;
import net.minecraft.world.level.LevelSummary;
import util.Mth;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class McRegionLevelStorageSource extends DirectoryLevelStorageSource
{
    public McRegionLevelStorageSource(final File dir) {
        super(dir);
    }
    
    @Override
    public String getName() {
        return "Scaevolus' McRegion";
    }
    
    @Override
    public List<LevelSummary> getLevelList() {
        final ArrayList<LevelSummary> list = new ArrayList<>();
        for (final File file : this.baseDir.listFiles()) {
            if (file.isDirectory()) {
                final String name = file.getName();
                final LevelData dataTag = this.getDataTagFor(name);
                if (dataTag != null) {
                    final boolean requiresConversion = dataTag.getVersion() != 19132;
                    String levelName = dataTag.getLevelName();
                    if (levelName == null || Mth.isNullOrEmpty(levelName)) {
                        levelName = name;
                    }
                    list.add(new LevelSummary(name, levelName, dataTag.getLastPlayed(), dataTag.getSizeOnDisk(), requiresConversion));
                }
            }
        }
        return list;
    }
    
    @Override
    public void clearAll() {
        RegionFileCache.clear();
    }
    
    @Override
    public LevelStorage selectLevel(final String levelId, final boolean createPlayerDir) {
        return new McRegionLevelStorage(this.baseDir, levelId, createPlayerDir);
    }
    
    @Override
    public boolean requiresConversion(final String levelId) {
        final LevelData dataTag = this.getDataTagFor(levelId);
        return dataTag != null && dataTag.getVersion() == 0;
    }
    
    @Override
    public boolean convertLevel(final String levelId, final ProgressListener progress) {
        progress.progressStagePercentage(0);
        final ArrayList<ChunkFile> list = new ArrayList<>();
        final ArrayList<File> list2 = new ArrayList<>();
        final ArrayList<ChunkFile> list3 = new ArrayList<>();
        final ArrayList<File> list4 = new ArrayList<>();
        final File baseFolder = new File(this.baseDir, levelId);
        final File file = new File(baseFolder, "DIM-1");
        System.out.println("Scanning folders...");
        this.addRegions(baseFolder, list, list2);
        if (file.exists()) {
            this.addRegions(file, list3, list4);
        }
        final int totalCount = list.size() + list3.size() + list2.size() + list4.size();
        System.out.println("Total conversion count is " + totalCount);
        this.convertRegions(baseFolder, list, 0, totalCount, progress);
        this.convertRegions(file, list3, list.size(), totalCount, progress);
        final LevelData dataTag = this.getDataTagFor(levelId);
        dataTag.setVersion(19132);
        this.selectLevel(levelId, false).saveLevelData(dataTag);
        this.eraseFolders(list2, list.size() + list3.size(), totalCount, progress);
        if (file.exists()) {
            this.eraseFolders(list4, list.size() + list3.size() + list2.size(), totalCount, progress);
        }
        return true;
    }
    
    private void addRegions(final File baseFolder, final ArrayList<ChunkFile> dest, final ArrayList<File> firstLevelFolders) {
        final FolderFilter folderFilter = new FolderFilter();
        final ChunkFilter filter = new ChunkFilter();
        for (final File e : baseFolder.listFiles(folderFilter)) {
            firstLevelFolders.add(e);
            final File[] listFiles2 = e.listFiles(folderFilter);
            for (int length2 = listFiles2.length, j = 0; j < length2; ++j) {
                final File[] listFiles3 = listFiles2[j].listFiles(filter);
                for (int length3 = listFiles3.length, k = 0; k < length3; ++k) {
                    dest.add(new ChunkFile(listFiles3[k]));
                }
            }
        }
    }
    
    private void convertRegions(final File baseFolder, final ArrayList<ChunkFile> chunkFiles, int currentCount, final int totalCount, final ProgressListener progress) {
        Collections.sort(chunkFiles);
        final byte[] array = new byte[4096];
        for (final ChunkFile chunkFile : chunkFiles) {
            final int x = chunkFile.getX();
            final int z = chunkFile.getZ();
            final RegionFile regionFile = RegionFileCache.getRegionFile(baseFolder, x, z);
            if (!regionFile.hasChunk(x & 0x1F, z & 0x1F)) {
                try {
                    final DataInputStream dataInputStream = new DataInputStream(new GZIPInputStream(new FileInputStream(chunkFile.getFile())));
                    final DataOutputStream chunkDataOutputStream = regionFile.getChunkDataOutputStream(x & 0x1F, z & 0x1F);
                    int read;
                    while ((read = dataInputStream.read(array)) != -1) {
                        chunkDataOutputStream.write(array, 0, read);
                    }
                    chunkDataOutputStream.close();
                    dataInputStream.close();
                }
                catch (final IOException ex) {
                    ex.printStackTrace();
                }
            }
            ++currentCount;
            progress.progressStagePercentage((int)Math.round(100.0 * currentCount / totalCount));
        }
        RegionFileCache.clear();
    }
    
    private void eraseFolders(final ArrayList<File> folders, int currentCount, final int totalCount, final ProgressListener progress) {
        for (final File file : folders) {
            DirectoryLevelStorageSource.deleteRecursive(file.listFiles());
            file.delete();
            ++currentCount;
            progress.progressStagePercentage((int)Math.round(100.0 * currentCount / totalCount));
        }
    }

    static class ChunkFile implements Comparable<ChunkFile>
    {
        private final File file;
        private final int x;
        private final int z;

        public ChunkFile(final File file) {
            this.file = file;
            final Matcher matcher = ChunkFilter.chunkFilePattern.matcher(file.getName());
            if (matcher.matches()) {
                this.x = Integer.parseInt(matcher.group(1), 36);
                this.z = Integer.parseInt(matcher.group(2), 36);
            }
            else {
                this.x = 0;
                this.z = 0;
            }
        }

        public int compareTo(final ChunkFile rhs) {
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

    static class ChunkFilter implements FilenameFilter
    {
        public static final Pattern chunkFilePattern;

        private ChunkFilter() {
        }

        public boolean accept(final File file, final String string) {
            return ChunkFilter.chunkFilePattern.matcher(string).matches();
        }

        static {
            chunkFilePattern = Pattern.compile("c\\.(-?[0-9a-z]+)\\.(-?[0-9a-z]+)\\.dat");
        }
    }

    static class FolderFilter implements FileFilter
    {
        public static final Pattern chunkFolderPattern;

        private FolderFilter() {
        }

        public boolean accept(final File file) {
            return file.isDirectory() && FolderFilter.chunkFolderPattern.matcher(file.getName()).matches();
        }

        static {
            chunkFolderPattern = Pattern.compile("[0-9a-z]|([0-9a-z][0-9a-z])");
        }
    }
}
