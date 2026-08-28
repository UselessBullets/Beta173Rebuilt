// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.level;

import net.minecraft.Pos;
import util.ProgressListener;
import java.io.IOException;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import net.minecraft.world.level.Level;
import java.util.List;
import java.util.Map;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.chunk.LevelChunk;
import java.util.Set;
import net.minecraft.world.level.chunk.ChunkSource;

// Useless - Seems to be a near identical copy of the Server's ServerChunkCache baring a few extremely minor modifications
public class ServerChunkCache implements ChunkSource
{
    private static final int MAX_SAVES = 24;
    private Set<Integer> toDrop = new HashSet<>();
    private LevelChunk emptyChunk;
    private ChunkSource source;
    private ChunkStorage storage;
    private Map<Integer, LevelChunk> cache = new HashMap<>();
    private List<LevelChunk> loadedChunkList = new ArrayList<>();
    private Level level;
    
    public ServerChunkCache(final Level level, final ChunkStorage storage, final ChunkSource source) {
        this.emptyChunk = new EmptyLevelChunk(level, new byte[Level.CHUNK_TILE_COUNT], 0, 0);
        this.level = level;
        this.storage = storage;
        this.source = source;
    }
    
    public boolean hasChunk(final int x, final int z) {
        return this.cache.containsKey(ChunkPos.hashCode(x, z));
    }

    // Useless - In other version of this class, presumably here aswell given it has the toDrop Set
    public void drop(final int x, final int z) {
        final Pos spawnPos = this.level.getSharedSpawnPos();
        final int xd = x * 16 + 8 - spawnPos.x;
        final int zd = z * 16 + 8 - spawnPos.z;
        final int r = 128;
        if (xd < -r || xd > r || zd < -r || zd > r) {
            this.toDrop.add(ChunkPos.hashCode(x, z));
        }
    }

    public LevelChunk create(final int x, final int z) {
        final int hashCode = ChunkPos.hashCode(x, z);
        this.toDrop.remove(hashCode);
        LevelChunk chunk = this.cache.get(hashCode);
        if (chunk == null) {
            chunk = this.load(x, z);
            if (chunk == null) {
                if (this.source == null) chunk = this.emptyChunk;
                else chunk = this.source.getChunk(x, z);
            }

            this.cache.put(hashCode, chunk);
            this.loadedChunkList.add(chunk);

            if (chunk != null) {
                chunk.lightLava();
                chunk.load();
            }

            if (!chunk.terrainPopulated && this.hasChunk(x + 1, z + 1) && this.hasChunk(x, z + 1) && this.hasChunk(x + 1, z)) this.postProcess(this, x, z);
            if (this.hasChunk(x - 1, z) && !this.getChunk(x - 1, z).terrainPopulated && this.hasChunk(x - 1, z + 1) && this.hasChunk(x, z + 1) && this.hasChunk(x - 1, z)) this.postProcess(this, x - 1, z);
            if (this.hasChunk(x, z - 1) && !this.getChunk(x, z - 1).terrainPopulated && this.hasChunk(x + 1, z - 1) && this.hasChunk(x, z - 1) && this.hasChunk(x + 1, z)) this.postProcess(this, x, z - 1);
            if (this.hasChunk(x - 1, z - 1) && !this.getChunk(x - 1, z - 1).terrainPopulated && this.hasChunk(x - 1, z - 1) && this.hasChunk(x, z - 1) && this.hasChunk(x - 1, z)) this.postProcess(this, x - 1, z - 1);
        }
        return chunk;
    }
    
    public LevelChunk getChunk(final int x, final int z) {
        final LevelChunk lc = this.cache.get(ChunkPos.hashCode(x, z));
        if (lc != null) return lc;

        return this.create(x, z);

    }
    
    private LevelChunk load(final int x, final int z) {
        if (this.storage == null) return null;

        try {
            final LevelChunk levelChunk = this.storage.load(this.level, x, z);

            if (levelChunk != null) {
                levelChunk.lastSaveTime = this.level.getTime();
            }

            return levelChunk;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
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
        for (int i = 0; i < this.loadedChunkList.size(); ++i) {
            final LevelChunk chunk = this.loadedChunkList.get(i);
            if (force && !chunk.dontSave) this.saveEntities(chunk);

            if (chunk.shouldSave(force)) {
                this.save(chunk);
                chunk.unsaved = false;
                if (++saves == MAX_SAVES && !force) {
                    return false;
                }
            }
        }

        if (force) {
            if (this.storage == null) return true;
            this.storage.flush();
        }

        return true;
    }
    
    public boolean tick() {
        for (int i = 0; i < 100; ++i) {
            if (!this.toDrop.isEmpty()) {
                final Integer hash = this.toDrop.iterator().next();
                final LevelChunk chunk = this.cache.get(hash);
                chunk.unload();
                this.save(chunk);
                this.saveEntities(chunk);

                this.toDrop.remove(hash);
                this.cache.remove(hash);
                this.loadedChunkList.remove(chunk);
            }
        }
        if (this.storage != null) this.storage.tick();

        return this.source.tick();
    }
    
    public boolean shouldSave() {
        return true;
    }
    
    public String gatherStats() {
        return "ServerChunkCache: " + this.cache.size() + " Drop: " + this.toDrop.size();
    }
}
