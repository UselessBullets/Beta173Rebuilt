// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import java.io.DataOutputStream;
import java.util.Iterator;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.FileInputStream;
import java.util.List;
import java.util.Collections;
import java.io.FilenameFilter;
import java.io.FileFilter;
import java.util.ArrayList;
import util.ProgressListener;
import net.minecraft.world.level.LevelData;
import java.io.File;

public class McRegionLevelStorageSource extends DirectoryLevelStorageSource
{
    public McRegionLevelStorageSource(final File dir) {
        super(dir);
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
        final ArrayList list = new ArrayList();
        final ArrayList list2 = new ArrayList();
        final ArrayList list3 = new ArrayList();
        final ArrayList list4 = new ArrayList();
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
    
    private void addRegions(final File baseFolder, final ArrayList dest, final ArrayList firstLevelFolders) {
        final McRegionLevelStorageSource_FolderFilter mcRegionLevelStorageSource_FolderFilter = new McRegionLevelStorageSource_FolderFilter(null);
        final McRegionLevelStorageSource_ChunkFilter filter = new McRegionLevelStorageSource_ChunkFilter(null);
        for (final File e : baseFolder.listFiles(mcRegionLevelStorageSource_FolderFilter)) {
            firstLevelFolders.add(e);
            final File[] listFiles2 = e.listFiles(mcRegionLevelStorageSource_FolderFilter);
            for (int length2 = listFiles2.length, j = 0; j < length2; ++j) {
                final File[] listFiles3 = listFiles2[j].listFiles(filter);
                for (int length3 = listFiles3.length, k = 0; k < length3; ++k) {
                    dest.add(new McRegionLevelStorageSource_ChunkFile(listFiles3[k]));
                }
            }
        }
    }
    
    private void convertRegions(final File baseFolder, final ArrayList chunkFiles, int currentCount, final int totalCount, final ProgressListener progress) {
        Collections.sort((List<Comparable>)chunkFiles);
        final byte[] array = new byte[4096];
        for (final McRegionLevelStorageSource_ChunkFile mcRegionLevelStorageSource_ChunkFile : chunkFiles) {
            final int x = mcRegionLevelStorageSource_ChunkFile.getX();
            final int z = mcRegionLevelStorageSource_ChunkFile.getZ();
            final RegionFile regionFile = RegionFileCache.getRegionFile(baseFolder, x, z);
            if (!regionFile.hasChunk(x & 0x1F, z & 0x1F)) {
                try {
                    final DataInputStream dataInputStream = new DataInputStream(new GZIPInputStream(new FileInputStream(mcRegionLevelStorageSource_ChunkFile.getFile())));
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
    
    private void eraseFolders(final ArrayList folders, int currentCount, final int totalCount, final ProgressListener progress) {
        for (final File file : folders) {
            DirectoryLevelStorageSource.deleteRecursive(file.listFiles());
            file.delete();
            ++currentCount;
            progress.progressStagePercentage((int)Math.round(100.0 * currentCount / totalCount));
        }
    }
}
