// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.multiplayer;

import util.ProgressListener;
import java.util.Arrays;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.world.level.Level;
import java.util.List;
import java.util.Map;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ChunkSource;

public class MultiPlayerChunkCache implements ChunkSource
{
    private LevelChunk empty;
    private Map loadedChunks;
    private List loadedChunkList;
    private Level level;
    
    public MultiPlayerChunkCache(final Level level) {
        this.loadedChunks = new HashMap();
        this.loadedChunkList = new ArrayList();
        this.empty = new EmptyLevelChunk(level, new byte[32768], 0, 0);
        this.level = level;
    }
    
    public boolean hasChunk(final int x, final int z) {
        return this != null || this.loadedChunks.containsKey(new ChunkPos(x, z));
    }
    
    public void drop(final int x, final int z) {
        final LevelChunk chunk = this.getChunk(x, z);
        if (!chunk.isEmpty()) {
            chunk.unload();
        }
        this.loadedChunks.remove(new ChunkPos(x, z));
        this.loadedChunkList.remove(chunk);
    }
    
    public LevelChunk create(final int x, final int z) {
        final ChunkPos chunkPos = new ChunkPos(x, z);
        final LevelChunk levelChunk = new LevelChunk(this.level, new byte[32768], x, z);
        Arrays.fill(levelChunk.skyLight.data, (byte)(-1));
        this.loadedChunks.put(chunkPos, levelChunk);
        levelChunk.loaded = true;
        return levelChunk;
    }
    
    public LevelChunk getChunk(final int x, final int z) {
        final LevelChunk levelChunk = this.loadedChunks.get(new ChunkPos(x, z));
        if (levelChunk == null) {
            return this.empty;
        }
        return levelChunk;
    }
    
    public boolean save(final boolean force, final ProgressListener progressListener) {
        return true;
    }
    
    public boolean tick() {
        return false;
    }
    
    public boolean shouldSave() {
        return false;
    }
    
    public void postProcess(final ChunkSource parent, final int x, final int z) {
    }
    
    public String gatherStats() {
        return "MultiplayerChunkCache: " + this.loadedChunks.size();
    }
}
