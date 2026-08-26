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
        final ArrayList<LevelSummary> levels = new ArrayList<>();
        for (final File file : this.baseDir.listFiles()) {
            if (!file.isDirectory()) continue;

            final String levelId = file.getName();

            final LevelData levelData = this.getDataTagFor(levelId);
            if (levelData != null) {
                final boolean requiresConversion = levelData.getVersion() != McRegionLevelStorage.MCREGION_VERSION_ID;
                String levelName = levelData.getLevelName();

                if (levelName == null || Mth.isEmpty(levelName)) {
                    levelName = levelId;
                }
                levels.add(new LevelSummary(levelId, levelName, levelData.getLastPlayed(), levelData.getSizeOnDisk(), requiresConversion));
            }
        }
        return levels;
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
    public boolean isConvertible(String levelId) {
        // check if there is old file format level data
        LevelData levelData = getDataTagFor(levelId);
        if (levelData == null || levelData.getVersion() != 0) {
            return false;
        }

        return true;
    }

    @Override
    public boolean requiresConversion(final String levelId) {
        final LevelData levelData = this.getDataTagFor(levelId);
        if (levelData == null || levelData.getVersion() != 0) {
            return false;
        }

        return true;
    }
    
    @Override
    public boolean convertLevel(final String levelId, final ProgressListener progress) {
        progress.progressStagePercentage(0);

        final ArrayList<ChunkFile> normalRegions = new ArrayList<>();
        final ArrayList<File> normalBaseFolders = new ArrayList<>();
        final ArrayList<ChunkFile> netherRegions = new ArrayList<>();
        final ArrayList<File> netherBaseFolders = new ArrayList<>();

        final File baseFolder = new File(this.baseDir, levelId);
        final File netherFolder = new File(baseFolder, "DIM-1");

        System.out.println("Scanning folders...");

        // find normal world
        this.addRegions(baseFolder, normalRegions, normalBaseFolders);

        // find hell world
        if (netherFolder.exists()) {
            this.addRegions(netherFolder, netherRegions, netherBaseFolders);
        }

        final int totalCount = normalRegions.size() + netherRegions.size() + normalBaseFolders.size() + netherBaseFolders.size();

        System.out.println("Total conversion count is " + totalCount);

        // convert normal world
        this.convertRegions(baseFolder, normalRegions, 0, totalCount, progress);
        // convert hell world
        this.convertRegions(netherFolder, netherRegions, normalRegions.size(), totalCount, progress);

        final LevelData levelData = this.getDataTagFor(levelId);
        levelData.setVersion(McRegionLevelStorage.MCREGION_VERSION_ID);

        LevelStorage levelStorage = this.selectLevel(levelId, false);
        levelStorage.saveLevelData(levelData);

        // erase old files
        this.eraseFolders(normalBaseFolders, normalRegions.size() + netherRegions.size(), totalCount, progress);
        if (netherFolder.exists()) {
            this.eraseFolders(netherBaseFolders, normalRegions.size() + netherRegions.size() + normalBaseFolders.size(), totalCount, progress);
        }
        return true;
    }
    
    private void addRegions(final File baseFolder, final ArrayList<ChunkFile> dest, final ArrayList<File> firstLevelFolders) {
        final FolderFilter folderFilter = new FolderFilter();
        final ChunkFilter chunkFilter = new ChunkFilter();

        for (final File folder1 : baseFolder.listFiles(folderFilter)) {
            // keep this for the clean-up process later on
            firstLevelFolders.add(folder1);

            final File[] folderLevel2 = folder1.listFiles(folderFilter);
            for (int length2 = folderLevel2.length, i2 = 0; i2 < length2; ++i2) {
                File folder2 = folderLevel2[i2];

                final File[] chunkFiles = folder2.listFiles(chunkFilter);

                for (int length3 = chunkFiles.length, i3 = 0; i3 < length3; ++i3) {
                    File chunk = chunkFiles[i3];
                    dest.add(new ChunkFile(chunk));
                }
            }
        }
    }
    
    private void convertRegions(final File baseFolder, final ArrayList<ChunkFile> chunkFiles, int currentCount, final int totalCount, final ProgressListener progress) {
        Collections.sort(chunkFiles);

        final byte[] buffer = new byte[4096];

        for (final ChunkFile chunkFile : chunkFiles) {

            //            Matcher matcher = ChunkFilter.chunkFilePattern.matcher(chunkFile.getName());
            //            if (!matcher.matches()) {
            //                continue;
            //            }
            //            int x = Integer.parseInt(matcher.group(1), 36);
            //            int z = Integer.parseInt(matcher.group(2), 36);

            final int x = chunkFile.getX();
            final int z = chunkFile.getZ();

            final RegionFile region = RegionFileCache.getRegionFile(baseFolder, x, z);
            if (!region.hasChunk(x & 31, z & 31)) {
                try {
                    FileInputStream fis = new FileInputStream(chunkFile.getFile());
                    final DataInputStream istream = new DataInputStream(new GZIPInputStream(fis));

                    final DataOutputStream out = region.getChunkDataOutputStream(x & 31, z & 31);

                    int length = 0;
                    while ((length = istream.read(buffer)) != -1) {
                        out.write(buffer, 0, length);
                    }

                    out.close();
                    istream.close();
                }
                catch (final IOException e) {
                    e.printStackTrace();
                }
            }

            currentCount++;
            int percent = (int) Math.round(100.0 * currentCount / totalCount);
            progress.progressStagePercentage(percent);
        }
        RegionFileCache.clear();
    }
    
    private void eraseFolders(final ArrayList<File> folders, int currentCount, final int totalCount, final ProgressListener progress) {
        for (final File folder : folders) {
            DirectoryLevelStorageSource.deleteRecursive(folder.listFiles());
            folder.delete();

            currentCount++;
            int percent = (int) Math.round(100.0 * currentCount / totalCount);
            progress.progressStagePercentage(percent);
        }
    }

    static class ChunkFile implements Comparable<ChunkFile>
    {
        private final File file;
        private final int x, z;

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

        //Returns a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
        public int compareTo(final ChunkFile rhs) {
            // sort chunk files so that they are placed according to their
            // region position
            final int rx = this.x >> 5;
            final int rhsrx = rhs.x >> 5;
            if (rx == rhsrx) {
                int rz = this.z >> 5;
                int rhsrz = rhs.z >> 5;
                return rz - rhsrz;
            }

            return rx - rhsrx;
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
        public static final Pattern chunkFilePattern = Pattern.compile("c\\.(-?[0-9a-z]+)\\.(-?[0-9a-z]+)\\.dat");

        private ChunkFilter() {
        }

        public boolean accept(final File file, final String string) {
            return ChunkFilter.chunkFilePattern.matcher(string).matches();
        }

    }

    static class FolderFilter implements FileFilter
    {
        public static final Pattern chunkFolderPattern = Pattern.compile("[0-9a-z]|([0-9a-z][0-9a-z])");

        private FolderFilter() {
        }

        public boolean accept(final File file) {
            return file.isDirectory() && FolderFilter.chunkFolderPattern.matcher(file.getName()).matches();
        }

    }
}
