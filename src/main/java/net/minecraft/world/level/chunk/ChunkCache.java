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
    private LevelChunk emptyChunk;
    private ChunkSource source;
    private ChunkStorage storage;
    private LevelChunk[] chunks;
    private Level level;
    int xLast;
    int zLast;
    private LevelChunk last;
    private int xCenter;
    private int zCenter;
    
    public void centerOn(final int xCenter, final int zCenter) {
        this.xCenter = xCenter;
        this.zCenter = zCenter;
    }
    
    public boolean fits(final int x, final int z) {
        final int n = 15;
        return x >= this.xCenter - n && z >= this.zCenter - n && x <= this.xCenter + n && z <= this.zCenter + n;
    }
    
    public boolean hasChunk(final int x, final int z) {
        if (!this.fits(x, z)) {
            return false;
        }
        if (x == this.xLast && z == this.zLast && this.last != null) {
            return true;
        }
        final int n = (x & 0x1F) + (z & 0x1F) * 32;
        return this.chunks[n] != null && (this.chunks[n] == this.emptyChunk || this.chunks[n].isAt(x, z));
    }
    
    public LevelChunk create(final int x, final int z) {
        return this.getChunk(x, z);
    }
    
    public LevelChunk getChunk(final int x, final int z) {
        if (x == this.xLast && z == this.zLast && this.last != null) {
            return this.last;
        }
        if (!this.level.isFindingSpawn && !this.fits(x, z)) {
            return this.emptyChunk;
        }
        final int n = (x & 0x1F) + (z & 0x1F) * 32;
        if (!this.hasChunk(x, z)) {
            if (this.chunks[n] != null) {
                this.chunks[n].unload();
                this.save(this.chunks[n]);
                this.saveEntities(this.chunks[n]);
            }
            LevelChunk levelChunk = this.load(x, z);
            if (levelChunk == null) {
                if (this.source == null) {
                    levelChunk = this.emptyChunk;
                }
                else {
                    levelChunk = this.source.getChunk(x, z);
                    levelChunk.attemptCompression();
                }
            }
            (this.chunks[n] = levelChunk).lightLava();
            if (this.chunks[n] != null) {
                this.chunks[n].load();
            }
            if (!this.chunks[n].terrainPopulated && this.hasChunk(x + 1, z + 1) && this.hasChunk(x, z + 1) && this.hasChunk(x + 1, z)) {
                this.postProcess(this, x, z);
            }
            if (this.hasChunk(x - 1, z) && !this.getChunk(x - 1, z).terrainPopulated && this.hasChunk(x - 1, z + 1) && this.hasChunk(x, z + 1) && this.hasChunk(x - 1, z)) {
                this.postProcess(this, x - 1, z);
            }
            if (this.hasChunk(x, z - 1) && !this.getChunk(x, z - 1).terrainPopulated && this.hasChunk(x + 1, z - 1) && this.hasChunk(x, z - 1) && this.hasChunk(x + 1, z)) {
                this.postProcess(this, x, z - 1);
            }
            if (this.hasChunk(x - 1, z - 1) && !this.getChunk(x - 1, z - 1).terrainPopulated && this.hasChunk(x - 1, z - 1) && this.hasChunk(x, z - 1) && this.hasChunk(x - 1, z)) {
                this.postProcess(this, x - 1, z - 1);
            }
        }
        this.xLast = x;
        this.zLast = z;
        this.last = this.chunks[n];
        return this.chunks[n];
    }
    
    private LevelChunk load(final int x, final int z) {
        if (this.storage == null) {
            return this.emptyChunk;
        }
        try {
            final LevelChunk load = this.storage.load(this.level, x, z);
            if (load != null) {
                load.lastSaveTime = this.level.getTime();
            }
            return load;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return this.emptyChunk;
        }
    }
    
    private void saveEntities(final LevelChunk levelChunk) {
        if (this.storage == null) {
            return;
        }
        try {
            this.storage.saveEntities(this.level, levelChunk);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void save(final LevelChunk levelChunk) {
        if (this.storage == null) {
            return;
        }
        try {
            levelChunk.lastSaveTime = this.level.getTime();
            this.storage.save(this.level, levelChunk);
        }
        catch (final IOException ex) {
            ex.printStackTrace();
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
        int n = 0;
        int n2 = 0;
        if (progressListener != null) {
            for (int i = 0; i < this.chunks.length; ++i) {
                if (this.chunks[i] != null && this.chunks[i].shouldSave(force)) {
                    ++n2;
                }
            }
        }
        int n3 = 0;
        for (int j = 0; j < this.chunks.length; ++j) {
            if (this.chunks[j] != null) {
                if (force && !this.chunks[j].dontSave) {
                    this.saveEntities(this.chunks[j]);
                }
                if (this.chunks[j].shouldSave(force)) {
                    this.save(this.chunks[j]);
                    this.chunks[j].unsaved = false;
                    if (++n == 2 && !force) {
                        return false;
                    }
                    if (progressListener != null && ++n3 % 10 == 0) {
                        progressListener.progressStagePercentage(n3 * 100 / n2);
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
        if (this.storage != null) {
            this.storage.tick();
        }
        return this.source.tick();
    }
    
    public boolean shouldSave() {
        return true;
    }
    
    public String gatherStats() {
        return "ChunkCache: " + this.chunks.length;
    }
}
