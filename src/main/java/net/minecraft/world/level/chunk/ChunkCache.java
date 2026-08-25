// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.chunk;

import util.ProgressListener;
import java.io.IOException;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.ChunkStorage;

public class ChunkCache implements ChunkSource
{
    private static final int CHUNK_CACHE_WIDTH = 32;
    private LevelChunk emptyChunk;
    private ChunkSource source;
    private ChunkStorage storage;
    private LevelChunk[] chunks = new LevelChunk[CHUNK_CACHE_WIDTH * CHUNK_CACHE_WIDTH];
    private Level level;
    int xLast = -999999999;
    int zLast = -999999999;
    private LevelChunk last;
    private int xCenter;
    private int zCenter;
    private static final int MAX_SAVES = 2;

    // Useless - Constructor existed in b1.2 leaks
    public ChunkCache(Level var1, ChunkStorage var2, ChunkSource var3) {
        this.emptyChunk = new EmptyLevelChunk(var1, new byte[Level.CHUNK_TILE_COUNT], 0, 0);
        this.level = var1;
        this.storage = var2;
        this.source = var3;
    }
    
    public void centerOn(final int xCenter, final int zCenter) {
        this.xCenter = xCenter;
        this.zCenter = zCenter;
    }
    
    public boolean fits(final int x, final int z) {
        final int r = CHUNK_CACHE_WIDTH / 2 - 1;
        return x >= this.xCenter - r && z >= this.zCenter - r && x <= this.xCenter + r && z <= this.zCenter + r;
    }
    
    public boolean hasChunk(final int x, final int z) {
        if (!this.fits(x, z)) return false;

        if (x == this.xLast && z == this.zLast && this.last != null) return true;

        final int i = (x & (CHUNK_CACHE_WIDTH - 1)) + (z & (CHUNK_CACHE_WIDTH - 1)) * CHUNK_CACHE_WIDTH;
        return this.chunks[i] != null && (this.chunks[i] == this.emptyChunk || this.chunks[i].isAt(x, z));
    }
    
    public LevelChunk create(final int x, final int z) {
        return this.getChunk(x, z);
    }
    
    public LevelChunk getChunk(final int x, final int z) {
        if (x == this.xLast && z == this.zLast && this.last != null) return this.last;

        if (!this.level.isFindingSpawn && !this.fits(x, z)) return this.emptyChunk;

        final int i = (x & (CHUNK_CACHE_WIDTH - 1)) + (z & (CHUNK_CACHE_WIDTH - 1)) * CHUNK_CACHE_WIDTH;
        if (!this.hasChunk(x, z)) {
            if (this.chunks[i] != null) {
                this.chunks[i].unload();
                this.save(this.chunks[i]);
                this.saveEntities(this.chunks[i]);
            }

            LevelChunk chunk = this.load(x, z);
            if (chunk == null) {
                if (this.source == null) {
                    chunk = this.emptyChunk;
                }
                else {
                    chunk = this.source.getChunk(x, z);
                    chunk.attemptCompression();
                }
            }
            this.chunks[i] = chunk;
            this.chunks[i].lightLava();
            if (this.chunks[i] != null) {
                this.chunks[i].load();
            }

            if (!this.chunks[i].terrainPopulated && this.hasChunk(x + 1, z + 1) && this.hasChunk(x, z + 1) && this.hasChunk(x + 1, z)) this.postProcess(this, x, z);
            if (this.hasChunk(x - 1, z) && !this.getChunk(x - 1, z).terrainPopulated && this.hasChunk(x - 1, z + 1) && this.hasChunk(x, z + 1) && this.hasChunk(x - 1, z)) this.postProcess(this, x - 1, z);
            if (this.hasChunk(x, z - 1) && !this.getChunk(x, z - 1).terrainPopulated && this.hasChunk(x + 1, z - 1) && this.hasChunk(x, z - 1) && this.hasChunk(x + 1, z)) this.postProcess(this, x, z - 1);
            if (this.hasChunk(x - 1, z - 1) && !this.getChunk(x - 1, z - 1).terrainPopulated && this.hasChunk(x - 1, z - 1) && this.hasChunk(x, z - 1) && this.hasChunk(x - 1, z)) this.postProcess(this, x - 1, z - 1);
        }

        this.xLast = x;
        this.zLast = z;
        this.last = this.chunks[i];

        return this.chunks[i];
    }
    
    private LevelChunk load(final int x, final int z) {
        if (this.storage == null) return this.emptyChunk;

        try {
            final LevelChunk levelChunk = this.storage.load(this.level, x, z);
            if (levelChunk != null) {
                levelChunk.lastSaveTime = this.level.getTime();
            }
            return levelChunk;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return this.emptyChunk;
        }
    }
    
    private void saveEntities(final LevelChunk levelChunk) {
        if (this.storage == null) return;

        try {
            this.storage.saveEntities(this.level, levelChunk);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private void save(final LevelChunk levelChunk) {
        if (this.storage == null) return;

        try {
            levelChunk.lastSaveTime = this.level.getTime();
            this.storage.save(this.level, levelChunk);
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    public void postProcess(final ChunkSource parent, final int x, final int z) {
        final LevelChunk chunk = this.getChunk(x, z);
        if (!chunk.terrainPopulated) {
            chunk.terrainPopulated = true;
            if (this.source != null) {
                this.source.postProcess(parent, x, z);
                chunk.markUnsaved();
            }
        }
    }
    
    public boolean save(final boolean force, final ProgressListener progressListener) {
        int saves = 0;
        int count = 0;

        if (progressListener != null) {
            for (int i = 0; i < this.chunks.length; ++i) {
                if (this.chunks[i] != null && this.chunks[i].shouldSave(force)) {
                    count++;
                }
            }
        }
        int cc = 0;

        for (int i = 0; i < this.chunks.length; ++i) {
            if (this.chunks[i] != null) {
                if (force && !this.chunks[i].dontSave) {
                    this.saveEntities(this.chunks[i]);
                }
                if (this.chunks[i].shouldSave(force)) {
                    this.save(this.chunks[i]);
                    this.chunks[i].unsaved = false;
                    if (saves++ == MAX_SAVES && !force) {
                        return false;
                    }

                    if (progressListener != null) {
                        if (++cc % 10 == 0) {
                            progressListener.progressStagePercentage(cc * 100 / count);
                        }
                    }
                }
            }
        }

        if (force) {
            if (this.storage == null) {
                return true;
            }
            this.storage.flush();
        }
        return true;
    }
    
    public boolean tick() {
        if (this.storage != null) this.storage.tick();
        return this.source.tick();
    }
    
    public boolean shouldSave() {
        return true;
    }
    
    public String gatherStats() {
        return "ChunkCache: " + this.chunks.length;
    }
}
