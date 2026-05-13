// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.level;

import util.ProgressListener;
import java.io.IOException;
import net.minecraft.Pos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.chunk.LevelChunk;
import java.util.Set;
import net.minecraft.world.level.chunk.ChunkSource;

public class ServerChunkCache implements ChunkSource
{
    private Set<Integer> toDrop;
    private LevelChunk emptyChunk;
    private ChunkSource source;
    private ChunkStorage storage;
    public boolean autoCreate;
    private Map<Integer, LevelChunk> cache;
    private List<LevelChunk> loadedChunkList;
    private ServerLevel level;
    
    public ServerChunkCache(final ServerLevel level, final ChunkStorage storage, final ChunkSource source) {
        this.toDrop = new HashSet<>();
        this.autoCreate = false;
        this.cache = new HashMap<>();
        this.loadedChunkList = new ArrayList<>();
        this.emptyChunk = new EmptyLevelChunk(level, new byte[32768], 0, 0);
        this.level = level;
        this.storage = storage;
        this.source = source;
    }
    
    public boolean hasChunk(final int x, final int z) {
        return this.cache.containsKey(ChunkPos.hashCode(x, z));
    }
    
    public void drop(final int x, final int z) {
        final Pos sharedSpawnPos = this.level.getSharedSpawnPos();
        final int n = x * 16 + 8 - sharedSpawnPos.x;
        final int n2 = z * 16 + 8 - sharedSpawnPos.z;
        final int n3 = 128;
        if (n < -n3 || n > n3 || n2 < -n3 || n2 > n3) {
            this.toDrop.add(ChunkPos.hashCode(x, z));
        }
    }
    
    public LevelChunk create(final int x, final int z) {
        final int hashCode = ChunkPos.hashCode(x, z);
        this.toDrop.remove(hashCode);
        LevelChunk levelChunk = this.cache.get(hashCode);
        if (levelChunk == null) {
            levelChunk = this.load(x, z);
            if (levelChunk == null) {
                if (this.source == null) {
                    levelChunk = this.emptyChunk;
                }
                else {
                    levelChunk = this.source.getChunk(x, z);
                }
            }
            this.cache.put(hashCode, levelChunk);
            this.loadedChunkList.add(levelChunk);
            if (levelChunk != null) {
                levelChunk.lightLava();
                levelChunk.load();
            }
            if (!levelChunk.terrainPopulated && this.hasChunk(x + 1, z + 1) && this.hasChunk(x, z + 1) && this.hasChunk(x + 1, z)) {
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
        return levelChunk;
    }
    
    public LevelChunk getChunk(final int x, final int z) {
        final LevelChunk levelChunk = this.cache.get(ChunkPos.hashCode(x, z));
        if (levelChunk != null) {
            return levelChunk;
        }
        if (this.level.isFindingSpawn || this.autoCreate) {
            return this.create(x, z);
        }
        return this.emptyChunk;
    }
    
    private LevelChunk load(final int x, final int z) {
        if (this.storage == null) {
            return null;
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
            return null;
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
        for (int i = 0; i < this.loadedChunkList.size(); ++i) {
            final LevelChunk levelChunk = this.loadedChunkList.get(i);
            if (force && !levelChunk.dontSave) {
                this.saveEntities(levelChunk);
            }
            if (levelChunk.shouldSave(force)) {
                this.save(levelChunk);
                levelChunk.unsaved = false;
                if (++n == 24 && !force) {
                    return false;
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
        if (!this.level.noSave) {
            for (int i = 0; i < 100; ++i) {
                if (!this.toDrop.isEmpty()) {
                    final Integer n = this.toDrop.iterator().next();
                    final LevelChunk levelChunk = this.cache.get(n);
                    levelChunk.unload();
                    this.save(levelChunk);
                    this.saveEntities(levelChunk);
                    this.toDrop.remove(n);
                    this.cache.remove(n);
                    this.loadedChunkList.remove(levelChunk);
                }
            }
            if (this.storage != null) {
                this.storage.tick();
            }
        }
        return this.source.tick();
    }

    @Override
    public boolean shouldSave() {
        return !this.level.noSave;
    }

    @Override
    public String gatherStats() {
        return "ServerChunkCache: " + this.cache.size() + " Drop: " + this.toDrop.size();
    }
}
