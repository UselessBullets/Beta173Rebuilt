// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.Pos;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.level.tile.LiquidTile;
import java.util.Iterator;
import net.minecraft.world.level.tile.entity.TileEntity;
import java.util.Collection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.chunk.LevelChunk;
import util.ProgressListener;
import net.minecraft.world.entity.Entity;
import util.Mth;
import net.minecraft.world.level.chunk.ChunkCache;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import net.minecraft.world.level.biome.BiomeSource;
import java.util.ArrayList;
import net.minecraft.world.level.saveddata.SavedDataStorage;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.Dimension;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;

public class Level implements LevelSource
{
    public boolean instaTick;
    private List<LightUpdate> lightUpdates;
    public List<Entity> entities;
    private List<Entity> entitiesToRemove;
    private TreeSet<TickNextTickData> tickNextTickList;
    private Set<TickNextTickData> tickNextTickSet;
    public List<TileEntity> tileEntityList;
    private List<TileEntity> pendingTileEntities;
    public List<Player> players;
    public List<Entity> globalEntities;
    private long cloudColor;
    public int skyDarken;
    protected int randValue;
    protected final int addend = 1013904223;
    protected float oRainLevel;
    protected float rainLevel;
    protected float oThunderLevel;
    protected float thunderLevel;
    protected int lightningTime;
    public int lightningBoltTime;
    public boolean noNeighborUpdate;
    private long sessionId;
    protected int saveInterval;
    public int difficulty;
    public Random random;
    public boolean isNew;
    public final Dimension dimension;
    protected List<LevelListener> listeners;
    protected ChunkSource chunkSource;
    protected final LevelStorage levelStorage;
    protected LevelData levelData;
    public boolean isFindingSpawn;
    private boolean allPlayersSleeping;
    public SavedDataStorage savedDataStorage;
    private ArrayList<AABB> boxes;
    private boolean updatingTileEntities;
    private int maxRecurse;
    private boolean spawnEnemies;
    private boolean spawnFriendlies;
    static int maxLoop;
    private Set<ChunkPos> chunksToPoll;
    private int delayUntilNextMoodSound;
    private List<Entity> es;
    public boolean isClientSide;
    
    public BiomeSource getBiomeSource() {
        return this.dimension.biomeSource;
    }
    
    public Level(final LevelStorage levelStorage, final String name, final Dimension fixedDimension, final long seed) {
        this.instaTick = false;
        this.lightUpdates = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.entitiesToRemove = new ArrayList<>();
        this.tickNextTickList = new TreeSet<>();
        this.tickNextTickSet = new HashSet<>();
        this.tileEntityList = new ArrayList<>();
        this.pendingTileEntities = new ArrayList<>();
        this.players = new ArrayList<>();
        this.globalEntities = new ArrayList<>();
        this.cloudColor = 16777215L;
        this.skyDarken = 0;
        this.randValue = new Random().nextInt();
        this.lightningTime = 0;
        this.lightningBoltTime = 0;
        this.noNeighborUpdate = false;
        this.sessionId = System.currentTimeMillis();
        this.saveInterval = 40;
        this.random = new Random();
        this.isNew = false;
        this.listeners = new ArrayList<>();
        this.boxes = new ArrayList<>();
        this.maxRecurse = 0;
        this.spawnEnemies = true;
        this.spawnFriendlies = true;
        this.chunksToPoll = new HashSet<>();
        this.delayUntilNextMoodSound = this.random.nextInt(12000);
        this.es = new ArrayList<>();
        this.isClientSide = false;
        this.levelStorage = levelStorage;
        this.levelData = new LevelData(seed, name);
        this.dimension = fixedDimension;
        this.savedDataStorage = new SavedDataStorage(levelStorage);
        fixedDimension.init(this);
        this.chunkSource = this.createChunkSource();
        this.updateSkyBrightness();
        this.prepareWeather();
    }
    
    public Level(final Level level, final Dimension dimension) {
        this.instaTick = false;
        this.lightUpdates = new ArrayList();
        this.entities = new ArrayList();
        this.entitiesToRemove = new ArrayList();
        this.tickNextTickList = new TreeSet();
        this.tickNextTickSet = new HashSet();
        this.tileEntityList = new ArrayList();
        this.pendingTileEntities = new ArrayList();
        this.players = new ArrayList();
        this.globalEntities = new ArrayList();
        this.cloudColor = 16777215L;
        this.skyDarken = 0;
        this.randValue = new Random().nextInt();
        this.lightningTime = 0;
        this.lightningBoltTime = 0;
        this.noNeighborUpdate = false;
        this.sessionId = System.currentTimeMillis();
        this.saveInterval = 40;
        this.random = new Random();
        this.isNew = false;
        this.listeners = new ArrayList();
        this.boxes = new ArrayList();
        this.maxRecurse = 0;
        this.spawnEnemies = true;
        this.spawnFriendlies = true;
        this.chunksToPoll = new HashSet();
        this.delayUntilNextMoodSound = this.random.nextInt(12000);
        this.es = new ArrayList();
        this.isClientSide = false;
        this.sessionId = level.sessionId;
        this.levelStorage = level.levelStorage;
        this.levelData = new LevelData(level.levelData);
        this.savedDataStorage = new SavedDataStorage(this.levelStorage);
        (this.dimension = dimension).init(this);
        this.chunkSource = this.createChunkSource();
        this.updateSkyBrightness();
        this.prepareWeather();
    }
    
    public Level(final LevelStorage levelStorage, final String levelName, final long seed) {
        this(levelStorage, levelName, seed, null);
    }
    
    public Level(final LevelStorage levelStorage, final String levelName, final long seed, final Dimension fixedDimension) {
        this.instaTick = false;
        this.lightUpdates = new ArrayList();
        this.entities = new ArrayList();
        this.entitiesToRemove = new ArrayList();
        this.tickNextTickList = new TreeSet();
        this.tickNextTickSet = new HashSet();
        this.tileEntityList = new ArrayList();
        this.pendingTileEntities = new ArrayList();
        this.players = new ArrayList();
        this.globalEntities = new ArrayList();
        this.cloudColor = 16777215L;
        this.skyDarken = 0;
        this.randValue = new Random().nextInt();
        this.lightningTime = 0;
        this.lightningBoltTime = 0;
        this.noNeighborUpdate = false;
        this.sessionId = System.currentTimeMillis();
        this.saveInterval = 40;
        this.random = new Random();
        this.isNew = false;
        this.listeners = new ArrayList();
        this.boxes = new ArrayList();
        this.maxRecurse = 0;
        this.spawnEnemies = true;
        this.spawnFriendlies = true;
        this.chunksToPoll = new HashSet();
        this.delayUntilNextMoodSound = this.random.nextInt(12000);
        this.es = new ArrayList();
        this.isClientSide = false;
        this.levelStorage = levelStorage;
        this.savedDataStorage = new SavedDataStorage(levelStorage);
        this.levelData = levelStorage.prepareLevel();
        this.isNew = (this.levelData == null);
        if (fixedDimension != null) {
            this.dimension = fixedDimension;
        }
        else if (this.levelData != null && this.levelData.getDimension() == -1) {
            this.dimension = Dimension.getNew(-1);
        }
        else {
            this.dimension = Dimension.getNew(0);
        }
        boolean b = false;
        if (this.levelData == null) {
            this.levelData = new LevelData(seed, levelName);
            b = true;
        }
        else {
            this.levelData.setLevelName(levelName);
        }
        this.dimension.init(this);
        this.chunkSource = this.createChunkSource();
        if (b) {
            this.setInitialSpawn();
        }
        this.updateSkyBrightness();
        this.prepareWeather();
    }
    
    protected ChunkSource createChunkSource() {
        return new ServerChunkCache(this, this.levelStorage.createChunkStorage(this.dimension), this.dimension.createRandomLevelSource());
    }
    
    protected void setInitialSpawn() {
        this.isFindingSpawn = true;
        int n = 0;
        final int ySpawn = 64;
        int n2;
        for (n2 = 0; !this.dimension.isValidSpawn(n, n2); n += this.random.nextInt(64) - this.random.nextInt(64), n2 += this.random.nextInt(64) - this.random.nextInt(64)) {}
        this.levelData.setSpawn(n, ySpawn, n2);
        this.isFindingSpawn = false;
    }
    
    public void validateSpawn() {
        if (this.levelData.getYSpawn() <= 0) {
            this.levelData.setYSpawn(64);
        }
        int xSpawn;
        int zSpawn;
        for (xSpawn = this.levelData.getXSpawn(), zSpawn = this.levelData.getZSpawn(); this.getTopTile(xSpawn, zSpawn) == 0; xSpawn += this.random.nextInt(8) - this.random.nextInt(8), zSpawn += this.random.nextInt(8) - this.random.nextInt(8)) {}
        this.levelData.setXSpawn(xSpawn);
        this.levelData.setZSpawn(zSpawn);
    }
    
    public int getTopTile(final int x, final int z) {
        int y;
        for (y = 63; !this.isEmptyTile(x, y + 1, z); ++y) {}
        return this.getTile(x, y, z);
    }
    
    public void clearLoadedPlayerData() {
    }
    
    public void loadPlayer(final Player player) {
        try {
            final CompoundTag loadedPlayerTag = this.levelData.getLoadedPlayerTag();
            if (loadedPlayerTag != null) {
                player.load(loadedPlayerTag);
                this.levelData.setLoadedPlayerTag(null);
            }
            if (this.chunkSource instanceof ChunkCache) {
                ((ChunkCache)this.chunkSource).centerOn(Mth.floor((float)(int)player.x) >> 4, Mth.floor((float)(int)player.z) >> 4);
            }
            this.addEntity(player);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void save(final boolean force, final ProgressListener progressListener) {
        if (!this.chunkSource.shouldSave()) {
            return;
        }
        if (progressListener != null) {
            progressListener.progressStartNoAbort("Saving level");
        }
        this.saveLevelData();
        if (progressListener != null) {
            progressListener.progressStage("Saving chunks");
        }
        this.chunkSource.save(force, progressListener);
    }
    
    private void saveLevelData() {
        this.checkSession();
        this.levelStorage.saveLevelData(this.levelData, this.players);
        this.savedDataStorage.save();
    }
    
    public boolean pauseSave(final int saveStep) {
        if (!this.chunkSource.shouldSave()) {
            return true;
        }
        if (saveStep == 0) {
            this.saveLevelData();
        }
        return this.chunkSource.save(false, null);
    }
    
    public int getTile(final int x, final int y, final int z) {
        if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return 0;
        }
        if (y < 0) {
            return 0;
        }
        if (y >= 128) {
            return 0;
        }
        return this.getChunk(x >> 4, z >> 4).getTile(x & 0xF, y, z & 0xF);
    }
    
    public boolean isEmptyTile(final int x, final int y, final int z) {
        return this.getTile(x, y, z) == 0;
    }
    
    public boolean hasChunkAt(final int x, final int y, final int z) {
        return y >= 0 && y < 128 && this.hasChunk(x >> 4, z >> 4);
    }
    
    public boolean hasChunksAt(final int x, final int y, final int z, final int r) {
        return this.hasChunksAt(x - r, y - r, z - r, x + r, y + r, z + r);
    }
    
    public boolean hasChunksAt(int x0, int y0, int z0, int x1, int y1, int z1) {
        if (y1 < 0 || y0 >= 128) {
            return false;
        }
        x0 >>= 4;
        y0 >>= 4;
        z0 >>= 4;
        x1 >>= 4;
        y1 >>= 4;
        z1 >>= 4;
        for (int i = x0; i <= x1; ++i) {
            for (int j = z0; j <= z1; ++j) {
                if (!this.hasChunk(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean hasChunk(final int x, final int z) {
        return this.chunkSource.hasChunk(x, z);
    }
    
    public LevelChunk getChunkAt(final int x, final int z) {
        return this.getChunk(x >> 4, z >> 4);
    }
    
    public LevelChunk getChunk(final int x, final int z) {
        return this.chunkSource.getChunk(x, z);
    }
    
    public boolean setTileAndDataNoUpdate(final int x, final int y, final int z, final int tile, final int data) {
        return x >= -32000000 && z >= -32000000 && x < 32000000 && z <= 32000000 && y >= 0 && y < 128 && this.getChunk(x >> 4, z >> 4).setTileAndData(x & 0xF, y, z & 0xF, tile, data);
    }
    
    public boolean setTileNoUpdate(final int x, final int y, final int z, final int tile) {
        return x >= -32000000 && z >= -32000000 && x < 32000000 && z <= 32000000 && y >= 0 && y < 128 && this.getChunk(x >> 4, z >> 4).setTile(x & 0xF, y, z & 0xF, tile);
    }
    
    public Material getMaterial(final int x, final int y, final int z) {
        final int tile = this.getTile(x, y, z);
        if (tile == 0) {
            return Material.air;
        }
        return Tile.tiles[tile].material;
    }
    
    public int getData(int x, final int y, int z) {
        if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return 0;
        }
        if (y < 0) {
            return 0;
        }
        if (y >= 128) {
            return 0;
        }
        final LevelChunk chunk = this.getChunk(x >> 4, z >> 4);
        x &= 0xF;
        z &= 0xF;
        return chunk.getData(x, y, z);
    }
    
    public void setData(final int x, final int y, final int z, final int data) {
        if (this.setDataNoUpdate(x, y, z, data)) {
            final int tile = this.getTile(x, y, z);
            if (Tile.sendTileData[tile & 0xFF]) {
                this.tileUpdated(x, y, z, tile);
            }
            else {
                this.updateNeighborsAt(x, y, z, tile);
            }
        }
    }
    
    public boolean setDataNoUpdate(int x, final int y, int z, final int data) {
        if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return false;
        }
        if (y < 0) {
            return false;
        }
        if (y >= 128) {
            return false;
        }
        final LevelChunk chunk = this.getChunk(x >> 4, z >> 4);
        x &= 0xF;
        z &= 0xF;
        chunk.setData(x, y, z, data);
        return true;
    }
    
    public boolean setTile(final int x, final int y, final int z, final int tile) {
        if (this.setTileNoUpdate(x, y, z, tile)) {
            this.tileUpdated(x, y, z, tile);
            return true;
        }
        return false;
    }
    
    public boolean setTileAndData(final int x, final int y, final int z, final int tile, final int data) {
        if (this.setTileAndDataNoUpdate(x, y, z, tile, data)) {
            this.tileUpdated(x, y, z, tile);
            return true;
        }
        return false;
    }
    
    public void sendTileUpdated(final int x, final int y, final int z) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            ((LevelListener)this.listeners.get(i)).tileChanged(x, y, z);
        }
    }
    
    protected void tileUpdated(final int x, final int y, final int z, final int tile) {
        this.sendTileUpdated(x, y, z);
        this.updateNeighborsAt(x, y, z, tile);
    }
    
    public void lightColumnChanged(final int x, final int z, int y0, int y1) {
        if (y0 > y1) {
            final int n = y1;
            y1 = y0;
            y0 = n;
        }
        this.setTilesDirty(x, y0, z, x, y1, z);
    }
    
    public void setTileDirty(final int x, final int y, final int z) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            ((LevelListener)this.listeners.get(i)).setTilesDirty(x, y, z, x, y, z);
        }
    }
    
    public void setTilesDirty(final int x0, final int y0, final int z0, final int x1, final int y1, final int z1) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            ((LevelListener)this.listeners.get(i)).setTilesDirty(x0, y0, z0, x1, y1, z1);
        }
    }
    
    public void updateNeighborsAt(final int x, final int y, final int z, final int tile) {
        this.neighborChanged(x - 1, y, z, tile);
        this.neighborChanged(x + 1, y, z, tile);
        this.neighborChanged(x, y - 1, z, tile);
        this.neighborChanged(x, y + 1, z, tile);
        this.neighborChanged(x, y, z - 1, tile);
        this.neighborChanged(x, y, z + 1, tile);
    }
    
    private void neighborChanged(final int x, final int y, final int z, final int type) {
        if (this.noNeighborUpdate || this.isClientSide) {
            return;
        }
        final Tile tile = Tile.tiles[this.getTile(x, y, z)];
        if (tile != null) {
            tile.neighborChanged(this, x, y, z, type);
        }
    }
    
    public boolean canSeeSky(final int x, final int y, final int z) {
        return this.getChunk(x >> 4, z >> 4).isSkyLit(x & 0xF, y, z & 0xF);
    }
    
    public int getDaytimeRawBrightness(final int x, int y, final int z) {
        if (y < 0) {
            return 0;
        }
        if (y >= 128) {
            y = 127;
        }
        return this.getChunk(x >> 4, z >> 4).getRawBrightness(x & 0xF, y, z & 0xF, 0);
    }
    
    public int getRawBrightness(final int x, final int y, final int z) {
        return this.getRawBrightness(x, y, z, true);
    }
    
    public int getRawBrightness(int x, int y, int z, final boolean propagate) {
        if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return 15;
        }
        if (propagate) {
            final int tile = this.getTile(x, y, z);
            if (tile == Tile.stoneSlabHalf.id || tile == Tile.farmland.id || tile == Tile.stairs_stone.id || tile == Tile.stairs_wood.id) {
                int rawBrightness = this.getRawBrightness(x, y + 1, z, false);
                final int rawBrightness2 = this.getRawBrightness(x + 1, y, z, false);
                final int rawBrightness3 = this.getRawBrightness(x - 1, y, z, false);
                final int rawBrightness4 = this.getRawBrightness(x, y, z + 1, false);
                final int rawBrightness5 = this.getRawBrightness(x, y, z - 1, false);
                if (rawBrightness2 > rawBrightness) {
                    rawBrightness = rawBrightness2;
                }
                if (rawBrightness3 > rawBrightness) {
                    rawBrightness = rawBrightness3;
                }
                if (rawBrightness4 > rawBrightness) {
                    rawBrightness = rawBrightness4;
                }
                if (rawBrightness5 > rawBrightness) {
                    rawBrightness = rawBrightness5;
                }
                return rawBrightness;
            }
        }
        if (y < 0) {
            return 0;
        }
        if (y >= 128) {
            y = 127;
        }
        final LevelChunk chunk = this.getChunk(x >> 4, z >> 4);
        x &= 0xF;
        z &= 0xF;
        return chunk.getRawBrightness(x, y, z, this.skyDarken);
    }
    
    public boolean isSkyLit(int x, final int y, int z) {
        if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return false;
        }
        if (y < 0) {
            return false;
        }
        if (y >= 128) {
            return true;
        }
        if (!this.hasChunk(x >> 4, z >> 4)) {
            return false;
        }
        final LevelChunk chunk = this.getChunk(x >> 4, z >> 4);
        x &= 0xF;
        z &= 0xF;
        return chunk.isSkyLit(x, y, z);
    }
    
    public int getHeightmap(final int x, final int z) {
        if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return 0;
        }
        if (!this.hasChunk(x >> 4, z >> 4)) {
            return 0;
        }
        return this.getChunk(x >> 4, z >> 4).getHeightmap(x & 0xF, z & 0xF);
    }
    
    public void updateLightIfOtherThan(final LightLayer layer, final int x, final int y, final int z, int expected) {
        if (this.dimension.hasCeiling && layer == LightLayer.Sky) {
            return;
        }
        if (!this.hasChunkAt(x, y, z)) {
            return;
        }
        if (layer == LightLayer.Sky) {
            if (this.isSkyLit(x, y, z)) {
                expected = 15;
            }
        }
        else if (layer == LightLayer.Block) {
            final int tile = this.getTile(x, y, z);
            if (Tile.lightEmission[tile] > expected) {
                expected = Tile.lightEmission[tile];
            }
        }
        if (this.getBrightness(layer, x, y, z) != expected) {
            this.updateLight(layer, x, y, z, x, y, z);
        }
    }
    
    public int getBrightness(final LightLayer layer, final int x, int y, final int z) {
        if (y < 0) {
            y = 0;
        }
        if (y >= 128) {
            y = 127;
        }
        if (y < 0 || y >= 128 || x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return layer.surrounding;
        }
        final int n = x >> 4;
        final int n2 = z >> 4;
        if (!this.hasChunk(n, n2)) {
            return 0;
        }
        return this.getChunk(n, n2).getBrightness(layer, x & 0xF, y, z & 0xF);
    }
    
    public void setBrightness(final LightLayer layer, final int x, final int y, final int z, final int brightness) {
        if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return;
        }
        if (y < 0) {
            return;
        }
        if (y >= 128) {
            return;
        }
        if (!this.hasChunk(x >> 4, z >> 4)) {
            return;
        }
        this.getChunk(x >> 4, z >> 4).setBrightness(layer, x & 0xF, y, z & 0xF, brightness);
        for (int i = 0; i < this.listeners.size(); ++i) {
            ((LevelListener)this.listeners.get(i)).tileChanged(x, y, z);
        }
    }
    
    public float getBrightness(final int x, final int y, final int z, final int emitt) {
        int rawBrightness = this.getRawBrightness(x, y, z);
        if (rawBrightness < emitt) {
            rawBrightness = emitt;
        }
        return this.dimension.brightnessRamp[rawBrightness];
    }
    
    public float getBrightness(final int x, final int y, final int z) {
        return this.dimension.brightnessRamp[this.getRawBrightness(x, y, z)];
    }
    
    public boolean isDay() {
        return this.skyDarken < 4;
    }
    
    public HitResult clip(final Vec3 a, final Vec3 b) {
        return this.clip(a, b, false, false);
    }
    
    public HitResult clip(final Vec3 a, final Vec3 b, final boolean liquid) {
        return this.clip(a, b, liquid, false);
    }
    
    public HitResult clip(final Vec3 a, final Vec3 b, final boolean liquid, final boolean solidOnly) {
        if (Double.isNaN(a.x) || Double.isNaN(a.y) || Double.isNaN(a.z)) {
            return null;
        }
        if (Double.isNaN(b.x) || Double.isNaN(b.y) || Double.isNaN(b.z)) {
            return null;
        }
        final int floor = Mth.floor(b.x);
        final int floor2 = Mth.floor(b.y);
        final int floor3 = Mth.floor(b.z);
        int floor4 = Mth.floor(a.x);
        int floor5 = Mth.floor(a.y);
        int floor6 = Mth.floor(a.z);
        final int tile = this.getTile(floor4, floor5, floor6);
        final int data = this.getData(floor4, floor5, floor6);
        final Tile tile2 = Tile.tiles[tile];
        if (!solidOnly || tile2 == null || tile2.getAABB(this, floor4, floor5, floor6) != null) {
            if (tile > 0 && tile2.mayPick(data, liquid)) {
                final HitResult clip = tile2.clip(this, floor4, floor5, floor6, a, b);
                if (clip != null) {
                    return clip;
                }
            }
        }
        int n = 200;
        while (n-- >= 0) {
            if (Double.isNaN(a.x) || Double.isNaN(a.y) || Double.isNaN(a.z)) {
                return null;
            }
            if (floor4 == floor && floor5 == floor2 && floor6 == floor3) {
                return null;
            }
            boolean b2 = true;
            boolean b3 = true;
            boolean b4 = true;
            double x = 999.0;
            double y = 999.0;
            double z = 999.0;
            if (floor > floor4) {
                x = floor4 + 1.0;
            }
            else if (floor < floor4) {
                x = floor4 + 0.0;
            }
            else {
                b2 = false;
            }
            if (floor2 > floor5) {
                y = floor5 + 1.0;
            }
            else if (floor2 < floor5) {
                y = floor5 + 0.0;
            }
            else {
                b3 = false;
            }
            if (floor3 > floor6) {
                z = floor6 + 1.0;
            }
            else if (floor3 < floor6) {
                z = floor6 + 0.0;
            }
            else {
                b4 = false;
            }
            double n2 = 999.0;
            double n3 = 999.0;
            double n4 = 999.0;
            final double n5 = b.x - a.x;
            final double n6 = b.y - a.y;
            final double n7 = b.z - a.z;
            if (b2) {
                n2 = (x - a.x) / n5;
            }
            if (b3) {
                n3 = (y - a.y) / n6;
            }
            if (b4) {
                n4 = (z - a.z) / n7;
            }
            int n8;
            if (n2 < n3 && n2 < n4) {
                if (floor > floor4) {
                    n8 = 4;
                }
                else {
                    n8 = 5;
                }
                a.x = x;
                a.y += n6 * n2;
                a.z += n7 * n2;
            }
            else if (n3 < n4) {
                if (floor2 > floor5) {
                    n8 = 0;
                }
                else {
                    n8 = 1;
                }
                a.x += n5 * n3;
                a.y = y;
                a.z += n7 * n3;
            }
            else {
                if (floor3 > floor6) {
                    n8 = 2;
                }
                else {
                    n8 = 3;
                }
                a.x += n5 * n4;
                a.y += n6 * n4;
                a.z = z;
            }
            final Vec3 temp;
            final Vec3 vec3 = temp = Vec3.newTemp(a.x, a.y, a.z);
            final double x2 = Mth.floor(a.x);
            temp.x = x2;
            floor4 = (int)x2;
            if (n8 == 5) {
                --floor4;
                final Vec3 vec4 = vec3;
                ++vec4.x;
            }
            final Vec3 vec5 = vec3;
            final double y2 = Mth.floor(a.y);
            vec5.y = y2;
            floor5 = (int)y2;
            if (n8 == 1) {
                --floor5;
                final Vec3 vec6 = vec3;
                ++vec6.y;
            }
            final Vec3 vec7 = vec3;
            final double z2 = Mth.floor(a.z);
            vec7.z = z2;
            floor6 = (int)z2;
            if (n8 == 3) {
                --floor6;
                final Vec3 vec8 = vec3;
                ++vec8.z;
            }
            final int tile3 = this.getTile(floor4, floor5, floor6);
            final int data2 = this.getData(floor4, floor5, floor6);
            final Tile tile4 = Tile.tiles[tile3];
            if (solidOnly && tile4 != null && tile4.getAABB(this, floor4, floor5, floor6) == null) {
                continue;
            }
            if (tile3 <= 0 || !tile4.mayPick(data2, liquid)) {
                continue;
            }
            final HitResult clip2 = tile4.clip(this, floor4, floor5, floor6, a, b);
            if (clip2 != null) {
                return clip2;
            }
        }
        return null;
    }
    
    public void playSound(final Entity entity, final String name, final float volume, final float pitch) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            ((LevelListener)this.listeners.get(i)).playSound(name, entity.x, entity.y - entity.heightOffset, entity.z, volume, pitch);
        }
    }
    
    public void playLocalSound(final double x, final double y, final double z, final String name, final float volume, final float pitch) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            ((LevelListener)this.listeners.get(i)).playSound(name, x, y, z, volume, pitch);
        }
    }
    
    public void playStreamingMusic(final String name, final int x, final int y, final int z) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            ((LevelListener)this.listeners.get(i)).playStreamingMusic(name, x, y, z);
        }
    }
    
    public void addParticle(final String id, final double x, final double y, final double z, final double xd, final double yd, final double zd) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            ((LevelListener)this.listeners.get(i)).addParticle(id, x, y, z, xd, yd, zd);
        }
    }
    
    public boolean addGlobalEntity(final Entity e) {
        this.globalEntities.add(e);
        return true;
    }
    
    public boolean addEntity(final Entity e) {
        final int floor = Mth.floor(e.x / 16.0);
        final int floor2 = Mth.floor(e.z / 16.0);
        boolean b = false;
        if (e instanceof Player) {
            b = true;
        }
        if (b || this.hasChunk(floor, floor2)) {
            if (e instanceof Player) {
                this.players.add((Player) e);
                this.updateSleepingPlayerList();
            }
            this.getChunk(floor, floor2).addEntity(e);
            this.entities.add(e);
            this.entityAdded(e);
            return true;
        }
        return false;
    }
    
    protected void entityAdded(final Entity e) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            ((LevelListener)this.listeners.get(i)).entityAdded(e);
        }
    }
    
    protected void entityRemoved(final Entity e) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            ((LevelListener)this.listeners.get(i)).entityRemoved(e);
        }
    }
    
    public void removeEntity(final Entity e) {
        if (e.rider != null) {
            e.rider.ride(null);
        }
        if (e.riding != null) {
            e.ride(null);
        }
        e.remove();
        if (e instanceof Player) {
            this.players.remove(e);
            this.updateSleepingPlayerList();
        }
    }

    public void removeEntityImmediately(final Entity e) {
        e.remove();
        if (e instanceof Player) {
            this.players.remove(e);
            this.updateSleepingPlayerList();
        }
        final int xChunk = e.xChunk;
        final int zChunk = e.zChunk;
        if (e.inChunk && this.hasChunk(xChunk, zChunk)) {
            this.getChunk(xChunk, zChunk).removeEntity(e);
        }
        this.entities.remove(e);
        this.entityRemoved(e);
    }
    
    public void addListener(final LevelListener listener) {
        this.listeners.add(listener);
    }
    
    public void removeListener(final LevelListener listener) {
        this.listeners.remove(listener);
    }
    
    public List<AABB> getCubes(final Entity source, final AABB box) {
        this.boxes.clear();
        final int floor = Mth.floor(box.x0);
        final int floor2 = Mth.floor(box.x1 + 1.0);
        final int floor3 = Mth.floor(box.y0);
        final int floor4 = Mth.floor(box.y1 + 1.0);
        final int floor5 = Mth.floor(box.z0);
        final int floor6 = Mth.floor(box.z1 + 1.0);
        for (int i = floor; i < floor2; ++i) {
            for (int j = floor5; j < floor6; ++j) {
                if (this.hasChunkAt(i, 64, j)) {
                    for (int k = floor3 - 1; k < floor4; ++k) {
                        final Tile tile = Tile.tiles[this.getTile(i, k, j)];
                        if (tile != null) {
                            tile.addAABBs(this, i, k, j, box, this.boxes);
                        }
                    }
                }
            }
        }
        final double n = 0.25;
        final List<Entity> entities = this.getEntities(source, box.grow(n, n, n));
        for (int l = 0; l < entities.size(); ++l) {
            final AABB collideBox = entities.get(l).getCollideBox();
            if (collideBox != null && collideBox.intersects(box)) {
                this.boxes.add(collideBox);
            }
            final AABB collideAgainstBox = source.getCollideAgainstBox(entities.get(l));
            if (collideAgainstBox != null && collideAgainstBox.intersects(box)) {
                this.boxes.add(collideAgainstBox);
            }
        }
        return this.boxes;
    }
    
    public int getSkyDarken(final float partialTick) {
        float n = 1.0f - (Mth.cos(this.getTimeOfDay(partialTick) * 3.1415927f * 2.0f) * 2.0f + 0.5f);
        if (n < 0.0f) {
            n = 0.0f;
        }
        if (n > 1.0f) {
            n = 1.0f;
        }
        return (int)((1.0f - (float)((float)((1.0f - n) * (1.0 - this.getRainLevel(partialTick) * 5.0f / 16.0)) * (1.0 - this.getThunderLevel(partialTick) * 5.0f / 16.0))) * 11.0f);
    }
    
    public Vec3 getSkyColor(final Entity source, final float partialTick) {
        float n = Mth.cos(this.getTimeOfDay(partialTick) * 3.1415927f * 2.0f) * 2.0f + 0.5f;
        if (n < 0.0f) {
            n = 0.0f;
        }
        if (n > 1.0f) {
            n = 1.0f;
        }
        final int floor = Mth.floor(source.x);
        final int floor2 = Mth.floor(source.z);
        final int skyColor = this.getBiomeSource().getBiome(floor, floor2).getSkyColor((float)this.getBiomeSource().getTemperature(floor, floor2));
        final float n2 = (skyColor >> 16 & 0xFF) / 255.0f;
        final float n3 = (skyColor >> 8 & 0xFF) / 255.0f;
        final float n4 = (skyColor & 0xFF) / 255.0f;
        float n5 = n2 * n;
        float n6 = n3 * n;
        float n7 = n4 * n;
        final float rainLevel = this.getRainLevel(partialTick);
        if (rainLevel > 0.0f) {
            final float n8 = (n5 * 0.3f + n6 * 0.59f + n7 * 0.11f) * 0.6f;
            final float n9 = 1.0f - rainLevel * 0.75f;
            n5 = n5 * n9 + n8 * (1.0f - n9);
            n6 = n6 * n9 + n8 * (1.0f - n9);
            n7 = n7 * n9 + n8 * (1.0f - n9);
        }
        final float thunderLevel = this.getThunderLevel(partialTick);
        if (thunderLevel > 0.0f) {
            final float n10 = (n5 * 0.3f + n6 * 0.59f + n7 * 0.11f) * 0.2f;
            final float n11 = 1.0f - thunderLevel * 0.75f;
            n5 = n5 * n11 + n10 * (1.0f - n11);
            n6 = n6 * n11 + n10 * (1.0f - n11);
            n7 = n7 * n11 + n10 * (1.0f - n11);
        }
        if (this.lightningBoltTime > 0) {
            float n12 = this.lightningBoltTime - partialTick;
            if (n12 > 1.0f) {
                n12 = 1.0f;
            }
            final float n13 = n12 * 0.45f;
            n5 = n5 * (1.0f - n13) + 0.8f * n13;
            n6 = n6 * (1.0f - n13) + 0.8f * n13;
            n7 = n7 * (1.0f - n13) + 1.0f * n13;
        }
        return Vec3.newTemp(n5, n6, n7);
    }
    
    public float getTimeOfDay(final float partialTick) {
        return this.dimension.getTimeOfDay(this.levelData.getTime(), partialTick);
    }
    
    public Vec3 getCloudColor(final float partialTick) {
        float n = Mth.cos(this.getTimeOfDay(partialTick) * 3.1415927f * 2.0f) * 2.0f + 0.5f;
        if (n < 0.0f) {
            n = 0.0f;
        }
        if (n > 1.0f) {
            n = 1.0f;
        }
        float n2 = (this.cloudColor >> 16 & 0xFFL) / 255.0f;
        float n3 = (this.cloudColor >> 8 & 0xFFL) / 255.0f;
        float n4 = (this.cloudColor & 0xFFL) / 255.0f;
        final float rainLevel = this.getRainLevel(partialTick);
        if (rainLevel > 0.0f) {
            final float n5 = (n2 * 0.3f + n3 * 0.59f + n4 * 0.11f) * 0.6f;
            final float n6 = 1.0f - rainLevel * 0.95f;
            n2 = n2 * n6 + n5 * (1.0f - n6);
            n3 = n3 * n6 + n5 * (1.0f - n6);
            n4 = n4 * n6 + n5 * (1.0f - n6);
        }
        float n7 = n2 * (n * 0.9f + 0.1f);
        float n8 = n3 * (n * 0.9f + 0.1f);
        float n9 = n4 * (n * 0.85f + 0.15f);
        final float thunderLevel = this.getThunderLevel(partialTick);
        if (thunderLevel > 0.0f) {
            final float n10 = (n7 * 0.3f + n8 * 0.59f + n9 * 0.11f) * 0.2f;
            final float n11 = 1.0f - thunderLevel * 0.95f;
            n7 = n7 * n11 + n10 * (1.0f - n11);
            n8 = n8 * n11 + n10 * (1.0f - n11);
            n9 = n9 * n11 + n10 * (1.0f - n11);
        }
        return Vec3.newTemp(n7, n8, n9);
    }
    
    public Vec3 getFogColor(final float partialTick) {
        return this.dimension.getFogColor(this.getTimeOfDay(partialTick), partialTick);
    }
    
    public int getTopSolidBlock(int x, int z) {
        final LevelChunk chunk = this.getChunkAt(x, z);
        int i = 127;
        x &= 0xF;
        z &= 0xF;
        while (i > 0) {
            final int tile = chunk.getTile(x, i, z);
            final Material material = (tile == 0) ? Material.air : Tile.tiles[tile].material;
            if (material.blocksMotion() || material.isLiquid()) {
                return i + 1;
            }
            --i;
        }
        return -1;
    }

    public int f(int x, int z) { // TODO find proper name
        final LevelChunk chunk = this.getChunkAt(x, z);
        int i = 127;
        x &= 0xF;
        z &= 0xF;
        while (i > 0) {
            final int tile = chunk.getTile(x, i, z);
            if (tile != 0 && Tile.tiles[tile].material.blocksMotion()) {
                return i + 1;
            }
            --i;
        }
        return -1;
    }
    
    public float getStarBrightness(final float partialTick) {
        float n = 1.0f - (Mth.cos(this.getTimeOfDay(partialTick) * 3.1415927f * 2.0f) * 2.0f + 0.75f);
        if (n < 0.0f) {
            n = 0.0f;
        }
        if (n > 1.0f) {
            n = 1.0f;
        }
        return n * n * 0.5f;
    }
    
    public void addToTickNextTick(final int x, final int y, final int z, final int tileId, final int tickDelay) {
        final TickNextTickData e = new TickNextTickData(x, y, z, tileId);
        final int n = 8;
        if (this.instaTick) {
            if (this.hasChunksAt(e.x - n, e.y - n, e.z - n, e.x + n, e.y + n, e.z + n)) {
                final int tile = this.getTile(e.x, e.y, e.z);
                if (tile == e.tileId && tile > 0) {
                    Tile.tiles[tile].tick(this, e.x, e.y, e.z, this.random);
                }
            }
            return;
        }
        if (this.hasChunksAt(x - n, y - n, z - n, x + n, y + n, z + n)) {
            if (tileId > 0) {
                e.delay(tickDelay + this.levelData.getTime());
            }
            if (!this.tickNextTickSet.contains(e)) {
                this.tickNextTickSet.add(e);
                this.tickNextTickList.add(e);
            }
        }
    }
    
    public void tickEntities() {
        for (int i = 0; i < this.globalEntities.size(); ++i) {
            final Entity entity = this.globalEntities.get(i);
            entity.tick();
            if (entity.removed) {
                this.globalEntities.remove(i--);
            }
        }
        this.entities.removeAll(this.entitiesToRemove);
        for (int j = 0; j < this.entitiesToRemove.size(); ++j) {
            final Entity e = this.entitiesToRemove.get(j);
            final int xChunk = e.xChunk;
            final int zChunk = e.zChunk;
            if (e.inChunk && this.hasChunk(xChunk, zChunk)) {
                this.getChunk(xChunk, zChunk).removeEntity(e);
            }
        }
        for (int k = 0; k < this.entitiesToRemove.size(); ++k) {
            this.entityRemoved((Entity)this.entitiesToRemove.get(k));
        }
        this.entitiesToRemove.clear();
        for (int l = 0; l < this.entities.size(); ++l) {
            final Entity e2 = this.entities.get(l);
            if (e2.riding != null) {
                if (!e2.riding.removed && e2.riding.rider == e2) {
                    continue;
                }
                e2.riding.rider = null;
                e2.riding = null;
            }
            if (!e2.removed) {
                this.tick(e2);
            }
            if (e2.removed) {
                final int xChunk2 = e2.xChunk;
                final int zChunk2 = e2.zChunk;
                if (e2.inChunk && this.hasChunk(xChunk2, zChunk2)) {
                    this.getChunk(xChunk2, zChunk2).removeEntity(e2);
                }
                this.entities.remove(l--);
                this.entityRemoved(e2);
            }
        }
        this.updatingTileEntities = true;
        final Iterator iterator = this.tileEntityList.iterator();
        while (iterator.hasNext()) {
            final TileEntity tileEntity = (TileEntity)iterator.next();
            if (!tileEntity.isRemoved()) {
                tileEntity.tick();
            }
            if (tileEntity.isRemoved()) {
                iterator.remove();
                final LevelChunk chunk = this.getChunk(tileEntity.x >> 4, tileEntity.z >> 4);
                if (chunk == null) {
                    continue;
                }
                chunk.removeTileEntity(tileEntity.x & 0xF, tileEntity.y, tileEntity.z & 0xF);
            }
        }
        this.updatingTileEntities = false;
        if (!this.pendingTileEntities.isEmpty()) {
            for (final TileEntity tileEntity2 : this.pendingTileEntities) {
                if (!tileEntity2.isRemoved()) {
                    if (!this.tileEntityList.contains(tileEntity2)) {
                        this.tileEntityList.add(tileEntity2);
                    }
                    final LevelChunk chunk2 = this.getChunk(tileEntity2.x >> 4, tileEntity2.z >> 4);
                    if (chunk2 != null) {
                        chunk2.setTileEntity(tileEntity2.x & 0xF, tileEntity2.y, tileEntity2.z & 0xF, tileEntity2);
                    }
                    this.sendTileUpdated(tileEntity2.x, tileEntity2.y, tileEntity2.z);
                }
            }
            this.pendingTileEntities.clear();
        }
    }
    
    public void addAllPendingTileEntities(final Collection entities) {
        if (this.updatingTileEntities) {
            this.pendingTileEntities.addAll(entities);
        }
        else {
            this.tileEntityList.addAll(entities);
        }
    }
    
    public void tick(final Entity e) {
        this.tick(e, true);
    }
    
    public void tick(final Entity e, final boolean actual) {
        final int floor = Mth.floor(e.x);
        final int floor2 = Mth.floor(e.z);
        final int n = 32;
        if (actual && !this.hasChunksAt(floor - n, 0, floor2 - n, floor + n, 128, floor2 + n)) {
            return;
        }
        e.xOld = e.x;
        e.yOld = e.y;
        e.zOld = e.z;
        e.yRotO = e.yRot;
        e.xRotO = e.xRot;
        if (actual && e.inChunk) {
            if (e.riding != null) {
                e.rideTick();
            }
            else {
                e.tick();
            }
        }
        if (Double.isNaN(e.x) || Double.isInfinite(e.x)) {
            e.x = e.xOld;
        }
        if (Double.isNaN(e.y) || Double.isInfinite(e.y)) {
            e.y = e.yOld;
        }
        if (Double.isNaN(e.z) || Double.isInfinite(e.z)) {
            e.z = e.zOld;
        }
        if (Double.isNaN(e.xRot) || Double.isInfinite(e.xRot)) {
            e.xRot = e.xRotO;
        }
        if (Double.isNaN(e.yRot) || Double.isInfinite(e.yRot)) {
            e.yRot = e.yRotO;
        }
        final int floor3 = Mth.floor(e.x / 16.0);
        final int floor4 = Mth.floor(e.y / 16.0);
        final int floor5 = Mth.floor(e.z / 16.0);
        if (!e.inChunk || e.xChunk != floor3 || e.yChunk != floor4 || e.zChunk != floor5) {
            if (e.inChunk && this.hasChunk(e.xChunk, e.zChunk)) {
                this.getChunk(e.xChunk, e.zChunk).removeEntity(e, e.yChunk);
            }
            if (this.hasChunk(floor3, floor5)) {
                e.inChunk = true;
                this.getChunk(floor3, floor5).addEntity(e);
            }
            else {
                e.inChunk = false;
            }
        }
        if (actual && e.inChunk && e.rider != null) {
            if (e.rider.removed || e.rider.riding != e) {
                e.rider.riding = null;
                e.rider = null;
            }
            else {
                this.tick(e.rider);
            }
        }
    }
    
    public boolean isUnobstructed(final AABB aabb) {
        final List<Entity> entities = this.getEntities(null, aabb);
        for (int i = 0; i < entities.size(); ++i) {
            final Entity entity = entities.get(i);
            if (!entity.removed && entity.blocksBuilding) {
                return false;
            }
        }
        return true;
    }

    public boolean containsAnyBlocks(final AABB box) {
        int floor = Mth.floor(box.x0);
        final int floor2 = Mth.floor(box.x1 + 1.0);
        int floor3 = Mth.floor(box.y0);
        final int floor4 = Mth.floor(box.y1 + 1.0);
        int floor5 = Mth.floor(box.z0);
        final int floor6 = Mth.floor(box.z1 + 1.0);
        if (box.x0 < 0.0) {
            --floor;
        }
        if (box.y0 < 0.0) {
            --floor3;
        }
        if (box.z0 < 0.0) {
            --floor5;
        }
        for (int i = floor; i < floor2; ++i) {
            for (int j = floor3; j < floor4; ++j) {
                for (int k = floor5; k < floor6; ++k) {
                    if (Tile.tiles[this.getTile(i, j, k)] != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean containsAnyLiquid(final AABB box) {
        int floor = Mth.floor(box.x0);
        final int floor2 = Mth.floor(box.x1 + 1.0);
        int floor3 = Mth.floor(box.y0);
        final int floor4 = Mth.floor(box.y1 + 1.0);
        int floor5 = Mth.floor(box.z0);
        final int floor6 = Mth.floor(box.z1 + 1.0);
        if (box.x0 < 0.0) {
            --floor;
        }
        if (box.y0 < 0.0) {
            --floor3;
        }
        if (box.z0 < 0.0) {
            --floor5;
        }
        for (int i = floor; i < floor2; ++i) {
            for (int j = floor3; j < floor4; ++j) {
                for (int k = floor5; k < floor6; ++k) {
                    final Tile tile = Tile.tiles[this.getTile(i, j, k)];
                    if (tile != null && tile.material.isLiquid()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean containsFireTile(final AABB box) {
        final int floor = Mth.floor(box.x0);
        final int floor2 = Mth.floor(box.x1 + 1.0);
        final int floor3 = Mth.floor(box.y0);
        final int floor4 = Mth.floor(box.y1 + 1.0);
        final int floor5 = Mth.floor(box.z0);
        final int floor6 = Mth.floor(box.z1 + 1.0);
        if (this.hasChunksAt(floor, floor3, floor5, floor2, floor4, floor6)) {
            for (int i = floor; i < floor2; ++i) {
                for (int j = floor3; j < floor4; ++j) {
                    for (int k = floor5; k < floor6; ++k) {
                        final int tile = this.getTile(i, j, k);
                        if (tile == Tile.fire.id || tile == Tile.lava.id || tile == Tile.calmLava.id) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public boolean checkAndHandleWater(final AABB box, final Material material, final Entity e) {
        final int floor = Mth.floor(box.x0);
        final int floor2 = Mth.floor(box.x1 + 1.0);
        final int floor3 = Mth.floor(box.y0);
        final int floor4 = Mth.floor(box.y1 + 1.0);
        final int floor5 = Mth.floor(box.z0);
        final int floor6 = Mth.floor(box.z1 + 1.0);
        if (!this.hasChunksAt(floor, floor3, floor5, floor2, floor4, floor6)) {
            return false;
        }
        boolean b = false;
        final Vec3 temp = Vec3.newTemp(0.0, 0.0, 0.0);
        for (int i = floor; i < floor2; ++i) {
            for (int j = floor3; j < floor4; ++j) {
                for (int k = floor5; k < floor6; ++k) {
                    final Tile tile = Tile.tiles[this.getTile(i, j, k)];
                    if (tile != null && tile.material == material && floor4 >= (double)(j + 1 - LiquidTile.getHeight(this.getData(i, j, k)))) {
                        b = true;
                        tile.handleEntityInside(this, i, j, k, e, temp);
                    }
                }
            }
        }
        if (temp.length() > 0.0) {
            final Vec3 normalize = temp.normalize();
            final double n = 0.014;
            e.xd += normalize.x * n;
            e.yd += normalize.y * n;
            e.zd += normalize.z * n;
        }
        return b;
    }
    
    public boolean containsMaterial(final AABB box, final Material material) {
        final int floor = Mth.floor(box.x0);
        final int floor2 = Mth.floor(box.x1 + 1.0);
        final int floor3 = Mth.floor(box.y0);
        final int floor4 = Mth.floor(box.y1 + 1.0);
        final int floor5 = Mth.floor(box.z0);
        final int floor6 = Mth.floor(box.z1 + 1.0);
        for (int i = floor; i < floor2; ++i) {
            for (int j = floor3; j < floor4; ++j) {
                for (int k = floor5; k < floor6; ++k) {
                    final Tile tile = Tile.tiles[this.getTile(i, j, k)];
                    if (tile != null && tile.material == material) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean containsLiquid(final AABB box, final Material material) {
        final int floor = Mth.floor(box.x0);
        final int floor2 = Mth.floor(box.x1 + 1.0);
        final int floor3 = Mth.floor(box.y0);
        final int floor4 = Mth.floor(box.y1 + 1.0);
        final int floor5 = Mth.floor(box.z0);
        final int floor6 = Mth.floor(box.z1 + 1.0);
        for (int i = floor; i < floor2; ++i) {
            for (int j = floor3; j < floor4; ++j) {
                for (int k = floor5; k < floor6; ++k) {
                    final Tile tile = Tile.tiles[this.getTile(i, j, k)];
                    if (tile != null && tile.material == material) {
                        final int data = this.getData(i, j, k);
                        double n = j + 1;
                        if (data < 8) {
                            n = j + 1 - data / 8.0;
                        }
                        if (n >= box.y0) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public Explosion explode(final Entity source, final double x, final double y, final double z, final float r) {
        return this.explode(source, x, y, z, r, false);
    }
    
    public Explosion explode(final Entity source, final double x, final double y, final double z, final float r, final boolean fire) {
        final Explosion explosion = new Explosion(this, source, x, y, z, r);
        explosion.fire = fire;
        explosion.explode();
        explosion.addParticles(true);
        return explosion;
    }
    
    public float getSeenPercent(final Vec3 center, final AABB bb) {
        final double n = 1.0 / ((bb.x1 - bb.x0) * 2.0 + 1.0);
        final double n2 = 1.0 / ((bb.y1 - bb.y0) * 2.0 + 1.0);
        final double n3 = 1.0 / ((bb.z1 - bb.z0) * 2.0 + 1.0);
        int n4 = 0;
        int n5 = 0;
        for (float n6 = 0.0f; n6 <= 1.0f; n6 += (float)n) {
            for (float n7 = 0.0f; n7 <= 1.0f; n7 += (float)n2) {
                for (float n8 = 0.0f; n8 <= 1.0f; n8 += (float)n3) {
                    if (this.clip(Vec3.newTemp(bb.x0 + (bb.x1 - bb.x0) * n6, bb.y0 + (bb.y1 - bb.y0) * n7, bb.z0 + (bb.z1 - bb.z0) * n8), center) == null) {
                        ++n4;
                    }
                    ++n5;
                }
            }
        }
        return n4 / (float)n5;
    }
    
    public void extinguishFire(final Player player, int x, int y, int z, final int face) {
        if (face == 0) {
            --y;
        }
        if (face == 1) {
            ++y;
        }
        if (face == 2) {
            --z;
        }
        if (face == 3) {
            ++z;
        }
        if (face == 4) {
            --x;
        }
        if (face == 5) {
            ++x;
        }
        if (this.getTile(x, y, z) == Tile.fire.id) {
            this.levelEvent(player, 1004, x, y, z, 0);
            this.setTile(x, y, z, 0);
        }
    }
    
    public Entity findSubclassOf(final Class clazz) {
        return null;
    }
    
    public String gatherStats() {
        return "All: " + this.entities.size();
    }
    
    public String gatherChunkSourceStats() {
        return this.chunkSource.gatherStats();
    }
    
    public TileEntity getTileEntity(final int x, final int y, final int z) {
        final LevelChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getTileEntity(x & 0xF, y, z & 0xF);
        }
        return null;
    }
    
    public void setTileEntity(final int x, final int y, final int z, final TileEntity tileEntity) {
        if (!tileEntity.isRemoved()) {
            if (this.updatingTileEntities) {
                tileEntity.x = x;
                tileEntity.y = y;
                tileEntity.z = z;
                this.pendingTileEntities.add(tileEntity);
            }
            else {
                this.tileEntityList.add(tileEntity);
                final LevelChunk chunk = this.getChunk(x >> 4, z >> 4);
                if (chunk != null) {
                    chunk.setTileEntity(x & 0xF, y, z & 0xF, tileEntity);
                }
            }
        }
    }
    
    public void removeTileEntity(final int x, final int y, final int z) {
        final TileEntity tileEntity = this.getTileEntity(x, y, z);
        if (tileEntity != null && this.updatingTileEntities) {
            tileEntity.setRemoved();
        }
        else {
            if (tileEntity != null) {
                this.tileEntityList.remove(tileEntity);
            }
            final LevelChunk chunk = this.getChunk(x >> 4, z >> 4);
            if (chunk != null) {
                chunk.removeTileEntity(x & 0xF, y, z & 0xF);
            }
        }
    }
    
    public boolean isSolidTile(final int x, final int y, final int z) {
        final Tile tile = Tile.tiles[this.getTile(x, y, z)];
        return tile != null && tile.isSolidRender();
    }
    
    public boolean isSolidBlockingTile(final int x, final int y, final int z) {
        final Tile tile = Tile.tiles[this.getTile(x, y, z)];
        return tile != null && tile.material.isSolidBlocking() && tile.isCubeShaped();
    }
    
    public void forceSave(final ProgressListener listener) {
        this.save(true, listener);
    }
    
    public boolean updateLights() {
        if (this.maxRecurse >= 50) {
            return false;
        }
        ++this.maxRecurse;
        try {
            int n = 500;
            while (this.lightUpdates.size() > 0) {
                if (--n <= 0) {
                    return true;
                }
                this.lightUpdates.remove(this.lightUpdates.size() - 1).update(this);
            }
            return false;
        }
        finally {
            --this.maxRecurse;
        }
    }
    
    public void updateLight(final LightLayer layer, final int x0, final int y0, final int z0, final int x1, final int y1, final int z1) {
        this.updateLight(layer, x0, y0, z0, x1, y1, z1, true);
    }
    
    public void updateLight(final LightLayer layer, final int x0, final int y0, final int z0, final int x1, final int y1, final int z1, final boolean expand) {
        if (this.dimension.hasCeiling && layer == LightLayer.Sky) {
            return;
        }
        ++Level.maxLoop;
        try {
            if (Level.maxLoop == 50) {
                return;
            }
            final int n = (x1 + x0) / 2;
            final int n2 = (z1 + z0) / 2;
            if (!this.hasChunkAt(n, 64, n2)) {
                return;
            }
            if (this.getChunkAt(n, n2).isEmpty()) {
                return;
            }
            final int size = this.lightUpdates.size();
            if (expand) {
                int n3 = 5;
                if (n3 > size) {
                    n3 = size;
                }
                for (int i = 0; i < n3; ++i) {
                    final LightUpdate lightUpdate = this.lightUpdates.get(this.lightUpdates.size() - i - 1);
                    if (lightUpdate.layer == layer && lightUpdate.expandToContain(x0, y0, z0, x1, y1, z1)) {
                        return;
                    }
                }
            }
            this.lightUpdates.add(new LightUpdate(layer, x0, y0, z0, x1, y1, z1));
            final int j = 1000000;
            if (this.lightUpdates.size() > 1000000) {
                System.out.println("More than " + j + " updates, aborting lighting updates");
                this.lightUpdates.clear();
            }
        }
        finally {
            --Level.maxLoop;
        }
    }
    
    public void updateSkyBrightness() {
        final int skyDarken = this.getSkyDarken(1.0f);
        if (skyDarken != this.skyDarken) {
            this.skyDarken = skyDarken;
        }
    }
    
    public void setSpawnSettings(final boolean spawnEnemies, final boolean spawnFriendlies) {
        this.spawnEnemies = spawnEnemies;
        this.spawnFriendlies = spawnFriendlies;
    }
    
    public void tick() {
        this.tickWeather();
        if (this.allPlayersAreSleeping()) {
            boolean attackSleepingPlayers = false;
            if (this.spawnEnemies && this.difficulty >= 1) {
                attackSleepingPlayers = MobSpawner.attackSleepingPlayers(this, this.players);
            }
            if (!attackSleepingPlayers) {
                final long n = this.levelData.getTime() + 24000L;
                this.levelData.setTime(n - n % 24000L);
                this.awakenAllPlayers();
            }
        }
        MobSpawner.tick(this, this.spawnEnemies, this.spawnFriendlies);
        this.chunkSource.tick();
        final int skyDarken = this.getSkyDarken(1.0f);
        if (skyDarken != this.skyDarken) {
            this.skyDarken = skyDarken;
            for (int i = 0; i < this.listeners.size(); ++i) {
                ((LevelListener)this.listeners.get(i)).skyColorChanged();
            }
        }
        final long time = this.levelData.getTime() + 1L;
        if (time % this.saveInterval == 0L) {
            this.save(false, null);
        }
        this.levelData.setTime(time);
        this.tickPendingTiles(false);
        this.tickTiles();
    }
    
    private void prepareWeather() {
        if (this.levelData.isRaining()) {
            this.rainLevel = 1.0f;
            if (this.levelData.isThundering()) {
                this.thunderLevel = 1.0f;
            }
        }
    }
    
    protected void tickWeather() {
        if (this.dimension.hasCeiling) {
            return;
        }
        if (this.lightningTime > 0) {
            --this.lightningTime;
        }
        int thunderTime = this.levelData.getThunderTime();
        if (thunderTime <= 0) {
            if (this.levelData.isThundering()) {
                this.levelData.setThunderTime(this.random.nextInt(12000) + 3600);
            }
            else {
                this.levelData.setThunderTime(this.random.nextInt(168000) + 12000);
            }
        }
        else {
            --thunderTime;
            this.levelData.setThunderTime(thunderTime);
            if (thunderTime <= 0) {
                this.levelData.setThundering(!this.levelData.isThundering());
            }
        }
        int rainTime = this.levelData.getRainTime();
        if (rainTime <= 0) {
            if (this.levelData.isRaining()) {
                this.levelData.setRainTime(this.random.nextInt(12000) + 12000);
            }
            else {
                this.levelData.setRainTime(this.random.nextInt(168000) + 12000);
            }
        }
        else {
            --rainTime;
            this.levelData.setRainTime(rainTime);
            if (rainTime <= 0) {
                this.levelData.setRaining(!this.levelData.isRaining());
            }
        }
        this.oRainLevel = this.rainLevel;
        if (this.levelData.isRaining()) {
            this.rainLevel += (float)0.01;
        }
        else {
            this.rainLevel -= (float)0.01;
        }
        if (this.rainLevel < 0.0f) {
            this.rainLevel = 0.0f;
        }
        if (this.rainLevel > 1.0f) {
            this.rainLevel = 1.0f;
        }
        this.oThunderLevel = this.thunderLevel;
        if (this.levelData.isThundering()) {
            this.thunderLevel += (float)0.01;
        }
        else {
            this.thunderLevel -= (float)0.01;
        }
        if (this.thunderLevel < 0.0f) {
            this.thunderLevel = 0.0f;
        }
        if (this.thunderLevel > 1.0f) {
            this.thunderLevel = 1.0f;
        }
    }
    
    private void stopWeather() {
        this.levelData.setRainTime(0);
        this.levelData.setRaining(false);
        this.levelData.setThunderTime(0);
        this.levelData.setThundering(false);
    }
    
    protected void tickTiles() {
        this.chunksToPoll.clear();
        for (int i = 0; i < this.players.size(); ++i) {
            final Player player = this.players.get(i);
            final int floor = Mth.floor(player.x / 16.0);
            final int floor2 = Mth.floor(player.z / 16.0);
            for (int n = 9, j = -n; j <= n; ++j) {
                for (int k = -n; k <= n; ++k) {
                    this.chunksToPoll.add(new ChunkPos(j + floor, k + floor2));
                }
            }
        }
        if (this.delayUntilNextMoodSound > 0) {
            --this.delayUntilNextMoodSound;
        }
        for (final ChunkPos chunkPos : this.chunksToPoll) {
            final int n2 = chunkPos.x * 16;
            final int n3 = chunkPos.z * 16;
            final LevelChunk chunk = this.getChunk(chunkPos.x, chunkPos.z);
            if (this.delayUntilNextMoodSound == 0) {
                this.randValue = this.randValue * 3 + 1013904223;
                final int n4 = this.randValue >> 2;
                final int x = n4 & 0xF;
                final int z = n4 >> 8 & 0xF;
                final int y = n4 >> 16 & 0x7F;
                final int tile = chunk.getTile(x, y, z);
                final int n5 = x + n2;
                final int n6 = z + n3;
                if (tile == 0 && this.getDaytimeRawBrightness(n5, y, n6) <= this.random.nextInt(8) && this.getBrightness(LightLayer.Sky, n5, y, n6) <= 0) {
                    final Player nearestPlayer = this.getNearestPlayer(n5 + 0.5, y + 0.5, n6 + 0.5, 8.0);
                    if (nearestPlayer != null && nearestPlayer.distanceToSqr(n5 + 0.5, y + 0.5, n6 + 0.5) > 4.0) {
                        this.playLocalSound(n5 + 0.5, y + 0.5, n6 + 0.5, "ambient.cave.cave", 0.7f, 0.8f + this.random.nextFloat() * 0.2f);
                        this.delayUntilNextMoodSound = this.random.nextInt(12000) + 6000;
                    }
                }
            }
            if (this.random.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
                this.randValue = this.randValue * 3 + 1013904223;
                final int n7 = this.randValue >> 2;
                final int n8 = n2 + (n7 & 0xF);
                final int n9 = n3 + (n7 >> 8 & 0xF);
                final int topSolidBlock = this.getTopSolidBlock(n8, n9);
                if (this.isRainingAt(n8, topSolidBlock, n9)) {
                    this.addGlobalEntity(new LightningBolt(this, n8, topSolidBlock, n9));
                    this.lightningTime = 2;
                }
            }
            if (this.random.nextInt(16) == 0) {
                this.randValue = this.randValue * 3 + 1013904223;
                final int n10 = this.randValue >> 2;
                final int n11 = n10 & 0xF;
                final int n12 = n10 >> 8 & 0xF;
                final int topSolidBlock2 = this.getTopSolidBlock(n11 + n2, n12 + n3);
                if (this.getBiomeSource().getBiome(n11 + n2, n12 + n3).hasSnow() && topSolidBlock2 >= 0 && topSolidBlock2 < 128 && chunk.getBrightness(LightLayer.Block, n11, topSolidBlock2, n12) < 10) {
                    final int tile2 = chunk.getTile(n11, topSolidBlock2 - 1, n12);
                    final int tile3 = chunk.getTile(n11, topSolidBlock2, n12);
                    if (this.isRaining() && tile3 == 0 && Tile.topSnow.mayPlace(this, n11 + n2, topSolidBlock2, n12 + n3) && tile2 != 0 && tile2 != Tile.ice.id && Tile.tiles[tile2].material.blocksMotion()) {
                        this.setTile(n11 + n2, topSolidBlock2, n12 + n3, Tile.topSnow.id);
                    }
                    if (tile2 == Tile.calmWater.id && chunk.getData(n11, topSolidBlock2 - 1, n12) == 0) {
                        this.setTile(n11 + n2, topSolidBlock2 - 1, n12 + n3, Tile.ice.id);
                    }
                }
            }
            for (int l = 0; l < 80; ++l) {
                this.randValue = this.randValue * 3 + 1013904223;
                final int n13 = this.randValue >> 2;
                final int n14 = n13 & 0xF;
                final int n15 = n13 >> 8 & 0xF;
                final int y2 = n13 >> 16 & 0x7F;
                final int n16 = chunk.blocks[n14 << 11 | n15 << 7 | y2] & 0xFF;
                if (Tile.shouldTick[n16]) {
                    Tile.tiles[n16].tick(this, n14 + n2, y2, n15 + n3, this.random);
                }
            }
        }
    }
    
    public boolean tickPendingTiles(final boolean force) {
        int size = this.tickNextTickList.size();
        if (size != this.tickNextTickSet.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        }
        if (size > 1000) {
            size = 1000;
        }
        for (int i = 0; i < size; ++i) {
            final TickNextTickData o = this.tickNextTickList.first();
            if (!force && o.delay > this.levelData.getTime()) {
                break;
            }
            this.tickNextTickList.remove(o);
            this.tickNextTickSet.remove(o);
            final int n = 8;
            if (this.hasChunksAt(o.x - n, o.y - n, o.z - n, o.x + n, o.y + n, o.z + n)) {
                final int tile = this.getTile(o.x, o.y, o.z);
                if (tile == o.tileId && tile > 0) {
                    Tile.tiles[tile].tick(this, o.x, o.y, o.z, this.random);
                }
            }
        }
        return this.tickNextTickList.size() != 0;
    }
    
    public void animateTick(final int x, final int y, final int z) {
        final int n = 16;
        final Random random = new Random();
        for (int i = 0; i < 1000; ++i) {
            final int n2 = x + this.random.nextInt(n) - this.random.nextInt(n);
            final int n3 = y + this.random.nextInt(n) - this.random.nextInt(n);
            final int n4 = z + this.random.nextInt(n) - this.random.nextInt(n);
            final int tile = this.getTile(n2, n3, n4);
            if (tile > 0) {
                Tile.tiles[tile].animateTick(this, n2, n3, n4, random);
            }
        }
    }
    
    public List<Entity> getEntities(final Entity except, final AABB bb) {
        this.es.clear();
        final int floor = Mth.floor((bb.x0 - 2.0) / 16.0);
        final int floor2 = Mth.floor((bb.x1 + 2.0) / 16.0);
        final int floor3 = Mth.floor((bb.z0 - 2.0) / 16.0);
        final int floor4 = Mth.floor((bb.z1 + 2.0) / 16.0);
        for (int i = floor; i <= floor2; ++i) {
            for (int j = floor3; j <= floor4; ++j) {
                if (this.hasChunk(i, j)) {
                    this.getChunk(i, j).getEntities(except, bb, this.es);
                }
            }
        }
        return this.es;
    }
    
    public <T extends Entity> List<T> getEntitiesOfClass(final Class<T> baseClass, final AABB bb) {
        final int floor = Mth.floor((bb.x0 - 2.0) / 16.0);
        final int floor2 = Mth.floor((bb.x1 + 2.0) / 16.0);
        final int floor3 = Mth.floor((bb.z0 - 2.0) / 16.0);
        final int floor4 = Mth.floor((bb.z1 + 2.0) / 16.0);
        final ArrayList es = new ArrayList<>();
        for (int i = floor; i <= floor2; ++i) {
            for (int j = floor3; j <= floor4; ++j) {
                if (this.hasChunk(i, j)) {
                    this.getChunk(i, j).getEntitiesOfClass(baseClass, bb, es);
                }
            }
        }
        return es;
    }
    
    public List<Entity> getAllEntities() {
        return this.entities;
    }
    
    public void tileEntityChanged(final int x, final int y, final int z, final TileEntity te) {
        if (this.hasChunkAt(x, y, z)) {
            this.getChunkAt(x, z).markUnsaved();
        }
        for (int i = 0; i < this.listeners.size(); ++i) {
            ((LevelListener)this.listeners.get(i)).tileEntityChanged(x, y, z, te);
        }
    }
    
    public int countInstanceOf(final Class clas) {
        int n = 0;
        for (int i = 0; i < this.entities.size(); ++i) {
            if (clas.isAssignableFrom(((Entity)this.entities.get(i)).getClass())) {
                ++n;
            }
        }
        return n;
    }
    
    public void addEntities(final List list) {
        this.entities.addAll(list);
        for (int i = 0; i < list.size(); ++i) {
            this.entityAdded((Entity)list.get(i));
        }
    }
    
    public void removeEntities(final List list) {
        this.entitiesToRemove.addAll(list);
    }
    
    public void prepare() {
        while (this.chunkSource.tick()) {}
    }
    
    public boolean mayPlace(final int tileId, final int x, final int y, final int z, final boolean ignoreEntities, final int face) {
        Tile tile = Tile.tiles[this.getTile(x, y, z)];
        final Tile tile2 = Tile.tiles[tileId];
        AABB aabb = tile2.getAABB(this, x, y, z);
        if (ignoreEntities) {
            aabb = null;
        }
        if (aabb != null && !this.isUnobstructed(aabb)) {
            return false;
        }
        if (tile == Tile.water || tile == Tile.calmWater || tile == Tile.lava || tile == Tile.calmLava || tile == Tile.fire || tile == Tile.topSnow) {
            tile = null;
        }
        return tileId > 0 && tile == null && tile2.mayPlace(this, x, y, z, face);
    }
    
    public Path findPath(final Entity from, final Entity to, final float maxDist) {
        final int floor = Mth.floor(from.x);
        final int floor2 = Mth.floor(from.y);
        final int floor3 = Mth.floor(from.z);
        final int n = (int)(maxDist + 16.0f);
        return new PathFinder(new Region(this, floor - n, floor2 - n, floor3 - n, floor + n, floor2 + n, floor3 + n)).findPath(from, to, maxDist);
    }
    
    public Path findPath(final Entity from, final int xBest, final int yBest, final int zBest, final float maxDist) {
        final int floor = Mth.floor(from.x);
        final int floor2 = Mth.floor(from.y);
        final int floor3 = Mth.floor(from.z);
        final int n = (int)(maxDist + 8.0f);
        return new PathFinder(new Region(this, floor - n, floor2 - n, floor3 - n, floor + n, floor2 + n, floor3 + n)).findPath(from, xBest, yBest, zBest, maxDist);
    }
    
    public boolean getDirectSignal(final int x, final int y, final int z, final int dir) {
        final int tile = this.getTile(x, y, z);
        return tile != 0 && Tile.tiles[tile].getDirectSignal(this, x, y, z, dir);
    }
    
    public boolean hasDirectSignal(final int x, final int y, final int z) {
        return this.getDirectSignal(x, y - 1, z, 0) || this.getDirectSignal(x, y + 1, z, 1) || this.getDirectSignal(x, y, z - 1, 2) || this.getDirectSignal(x, y, z + 1, 3) || this.getDirectSignal(x - 1, y, z, 4) || this.getDirectSignal(x + 1, y, z, 5);
    }
    
    public boolean getSignal(final int x, final int y, final int z, final int dir) {
        if (this.isSolidBlockingTile(x, y, z)) {
            return this.hasDirectSignal(x, y, z);
        }
        final int tile = this.getTile(x, y, z);
        return tile != 0 && Tile.tiles[tile].getSignal(this, x, y, z, dir);
    }
    
    public boolean hasNeighborSignal(final int x, final int y, final int z) {
        return this.getSignal(x, y - 1, z, 0) || this.getSignal(x, y + 1, z, 1) || this.getSignal(x, y, z - 1, 2) || this.getSignal(x, y, z + 1, 3) || this.getSignal(x - 1, y, z, 4) || this.getSignal(x + 1, y, z, 5);
    }
    
    public Player getNearestPlayer(final Entity source, final double maxDist) {
        return this.getNearestPlayer(source.x, source.y, source.z, maxDist);
    }
    
    public Player getNearestPlayer(final double x, final double y, final double z, final double maxDist) {
        double n = -1.0;
        Player player = null;
        for (int i = 0; i < this.players.size(); ++i) {
            final Player player2 = this.players.get(i);
            final double distanceToSqr = player2.distanceToSqr(x, y, z);
            if ((maxDist < 0.0 || distanceToSqr < maxDist * maxDist) && (n == -1.0 || distanceToSqr < n)) {
                n = distanceToSqr;
                player = player2;
            }
        }
        return player;
    }
    
    public Player getPlayerByName(final String name) {
        for (int i = 0; i < this.players.size(); ++i) {
            if (name.equals(((Player)this.players.get(i)).name)) {
                return (Player)this.players.get(i);
            }
        }
        return null;
    }

    public byte[] getBlocksAndData(final int x, final int y, final int z, final int xs, final int ys, final int zs) {
        final byte[] data = new byte[xs * ys * zs * 5 / 2];
        final int n = x >> 4;
        final int n2 = z >> 4;
        final int n3 = x + xs - 1 >> 4;
        final int n4 = z + zs - 1 >> 4;
        int blocksAndData = 0;
        int y2 = y;
        int y3 = y + ys;
        if (y2 < 0) {
            y2 = 0;
        }
        if (y3 > 128) {
            y3 = 128;
        }
        for (int i = n; i <= n3; ++i) {
            int x2 = x - i * 16;
            int x3 = x + xs - i * 16;
            if (x2 < 0) {
                x2 = 0;
            }
            if (x3 > 16) {
                x3 = 16;
            }
            for (int j = n2; j <= n4; ++j) {
                int z2 = z - j * 16;
                int z3 = z + zs - j * 16;
                if (z2 < 0) {
                    z2 = 0;
                }
                if (z3 > 16) {
                    z3 = 16;
                }
                blocksAndData = this.getChunk(i, j).getBlocksAndData(data, x2, y2, z2, x3, y3, z3, blocksAndData);
            }
        }
        return data;
    }
    
    public void setBlocksAndData(final int x, final int y, final int z, final int xs, final int ys, final int za, final byte[] data) {
        final int n = x >> 4;
        final int n2 = z >> 4;
        final int n3 = x + xs - 1 >> 4;
        final int n4 = z + za - 1 >> 4;
        int blocksAndData = 0;
        int n5 = y;
        int n6 = y + ys;
        if (n5 < 0) {
            n5 = 0;
        }
        if (n6 > 128) {
            n6 = 128;
        }
        for (int i = n; i <= n3; ++i) {
            int x2 = x - i * 16;
            int x3 = x + xs - i * 16;
            if (x2 < 0) {
                x2 = 0;
            }
            if (x3 > 16) {
                x3 = 16;
            }
            for (int j = n2; j <= n4; ++j) {
                int z2 = z - j * 16;
                int z3 = z + za - j * 16;
                if (z2 < 0) {
                    z2 = 0;
                }
                if (z3 > 16) {
                    z3 = 16;
                }
                blocksAndData = this.getChunk(i, j).getBlocksAndData(data, x2, n5, z2, x3, n6, z3, blocksAndData);
                this.setTilesDirty(i * 16 + x2, n5, j * 16 + z2, i * 16 + x3, n6, j * 16 + z3);
            }
        }
    }
    
    public void disconnect() {
    }
    
    public void checkSession() {
        this.levelStorage.checkSession();
    }
    
    public void setTime(final long time) {
        this.levelData.setTime(time);
    }

    public void b(final long long1) { // TODO figure out what the right name for this is
        final long n = long1 - this.levelData.getTime();
        for (final TickNextTickData tickNextTickData : this.tickNextTickSet) {
            tickNextTickData.delay += n;
        }
        this.setTime(long1);
    }
    
    public long getSeed() {
        return this.levelData.getSeed();
    }
    
    public long getTime() {
        return this.levelData.getTime();
    }
    
    public Pos getSharedSpawnPos() {
        return new Pos(this.levelData.getXSpawn(), this.levelData.getYSpawn(), this.levelData.getZSpawn());
    }
    
    public void setSpawnPos(final Pos spawnPos) {
        this.levelData.setSpawn(spawnPos.x, spawnPos.y, spawnPos.z);
    }
    
    public void ensureAdded(final Entity entity) {
        final int floor = Mth.floor(entity.x / 16.0);
        final int floor2 = Mth.floor(entity.z / 16.0);
        for (int n = 2, i = floor - n; i <= floor + n; ++i) {
            for (int j = floor2 - n; j <= floor2 + n; ++j) {
                this.getChunk(i, j);
            }
        }
        if (!this.entities.contains(entity)) {
            this.entities.add(entity);
        }
    }
    
    public boolean mayInteract(final Player player, final int xt, final int yt, final int zt) {
        return true;
    }
    
    public void broadcastEntityEvent(final Entity e, final byte event) {
    }
    
    public void removeAllPendingEntityRemovals() {
        this.entities.removeAll(this.entitiesToRemove);
        for (int i = 0; i < this.entitiesToRemove.size(); ++i) {
            final Entity e = this.entitiesToRemove.get(i);
            final int xChunk = e.xChunk;
            final int zChunk = e.zChunk;
            if (e.inChunk && this.hasChunk(xChunk, zChunk)) {
                this.getChunk(xChunk, zChunk).removeEntity(e);
            }
        }
        for (int j = 0; j < this.entitiesToRemove.size(); ++j) {
            this.entityRemoved((Entity)this.entitiesToRemove.get(j));
        }
        this.entitiesToRemove.clear();
        for (int k = 0; k < this.entities.size(); ++k) {
            final Entity entity = this.entities.get(k);
            if (entity.riding != null) {
                if (!entity.riding.removed && entity.riding.rider == entity) {
                    continue;
                }
                entity.riding.rider = null;
                entity.riding = null;
            }
            if (entity.removed) {
                final int xChunk2 = entity.xChunk;
                final int zChunk2 = entity.zChunk;
                if (entity.inChunk && this.hasChunk(xChunk2, zChunk2)) {
                    this.getChunk(xChunk2, zChunk2).removeEntity(entity);
                }
                this.entities.remove(k--);
                this.entityRemoved(entity);
            }
        }
    }
    
    public ChunkSource getChunkSource() {
        return this.chunkSource;
    }
    
    public void tileEvent(final int x, final int y, final int z, final int b0, final int b1) {
        final int tile = this.getTile(x, y, z);
        if (tile > 0) {
            Tile.tiles[tile].triggerEvent(this, x, y, z, b0, b1);
        }
    }

    public LevelStorage getLevelStorage() {
        return this.levelStorage;
    }

    public LevelData getLevelData() {
        return this.levelData;
    }
    
    public void updateSleepingPlayerList() {
        this.allPlayersSleeping = !this.players.isEmpty();
        final Iterator iterator = this.players.iterator();
        while (iterator.hasNext()) {
            if (!((Player)iterator.next()).isSleeping()) {
                this.allPlayersSleeping = false;
                break;
            }
        }
    }
    
    protected void awakenAllPlayers() {
        this.allPlayersSleeping = false;
        for (final Player player : this.players) {
            if (player.isSleeping()) {
                player.stopSleepInBed(false, false, true);
            }
        }
        this.stopWeather();
    }
    
    public boolean allPlayersAreSleeping() {
        if (this.allPlayersSleeping && !this.isClientSide) {
            final Iterator iterator = this.players.iterator();
            while (iterator.hasNext()) {
                if (!((Player)iterator.next()).isSleepingLongEnough()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public float getThunderLevel(final float partialTick) {
        return (this.oThunderLevel + (this.thunderLevel - this.oThunderLevel) * partialTick) * this.getRainLevel(partialTick);
    }
    
    public float getRainLevel(final float partialTick) {
        return this.oRainLevel + (this.rainLevel - this.oRainLevel) * partialTick;
    }
    
    public void setRainLevel(final float rainLevel) {
        this.oRainLevel = rainLevel;
        this.rainLevel = rainLevel;
    }
    
    public boolean isThundering() {
        return this.getThunderLevel(1.0f) > 0.9;
    }
    
    public boolean isRaining() {
        return this.getRainLevel(1.0f) > 0.2;
    }
    
    public boolean isRainingAt(final int x, final int y, final int z) {
        if (!this.isRaining()) {
            return false;
        }
        if (!this.canSeeSky(x, y, z)) {
            return false;
        }
        if (this.getTopSolidBlock(x, z) > y) {
            return false;
        }
        final Biome biome = this.getBiomeSource().getBiome(x, z);
        return !biome.hasSnow() && biome.hasRain();
    }
    
    public void setSavedData(final String id, final SavedData data) {
        this.savedDataStorage.set(id, data);
    }
    
    public SavedData getSavedData(final Class clazz, final String id) {
        return this.savedDataStorage.get(clazz, id);
    }
    
    public int getFreeAuxValueFor(final String id) {
        return this.savedDataStorage.getFreeAuxValueFor(id);
    }
    
    public void levelEvent(final int type, final int x, final int y, final int z, final int data) {
        this.levelEvent(null, type, x, y, z, data);
    }
    
    public void levelEvent(final Player source, final int type, final int x, final int y, final int z, final int data) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            ((LevelListener)this.listeners.get(i)).levelEvent(source, type, x, y, z, data);
        }
    }
    
    static {
        Level.maxLoop = 0;
    }
}
