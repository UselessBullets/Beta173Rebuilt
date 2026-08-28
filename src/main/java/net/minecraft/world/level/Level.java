// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

import net.minecraft.SharedConstants;
import net.minecraft.client.level.ServerChunkCache;
import net.minecraft.world.Difficulty;
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
    public static final int MAX_TICK_TILES_PER_TICK = 1000;
    public static final int MAX_LEVEL_SIZE = 32000000;
    public static final short MIN_HEIGHT = 0;
    public static final short MAX_HEIGHT = 128;
    public static final short SEA_LEVEL = 63;

    public static final int CHUNK_TILE_COUNT = MAX_HEIGHT * 16 * 16;   // Useless - In LCE leak and are convient
    private static final int CHUNK_POLL_RANGE = 9;
    private static final int CHUNK_TILE_TICK_COUNT = 80;

    public boolean instaTick = false;
    public static final int MAX_BRIGHTNESS = 15;
    public static final int TICKS_PER_DAY = SharedConstants.TICKS_PER_SECOND * 60 * 20;
    private List<LightUpdate> lightUpdates = new ArrayList<>();
    public List<Entity> entities = new ArrayList<>();
    private List<Entity> entitiesToRemove = new ArrayList<>();
    private TreeSet<TickNextTickData> tickNextTickList = new TreeSet<>();
    private Set<TickNextTickData> tickNextTickSet = new HashSet<>();
    public List<TileEntity> tileEntityList = new ArrayList<>();
    private List<TileEntity> pendingTileEntities = new ArrayList<>();
    public List<Player> players = new ArrayList<>();
    public List<Entity> globalEntities = new ArrayList<>();
    private long cloudColor = 0x00ffffffL;
    public int skyDarken = 0;
    protected int randValue = new Random().nextInt();
    protected final int addend = 1013904223;
    protected float oRainLevel, rainLevel;
    protected float oThunderLevel, thunderLevel;
    protected int lightningTime = 0;
    public int lightningBoltTime = 0;
    public boolean noNeighborUpdate = false;
    private long sessionId = System.currentTimeMillis();
    protected int saveInterval = 40;
    public int difficulty;
    public Random random = new Random();
    public boolean isNew = false;
    public final Dimension dimension;
    protected List<LevelListener> listeners = new ArrayList<>();
    protected ChunkSource chunkSource;
    protected final LevelStorage levelStorage;
    protected LevelData levelData;
    public boolean isFindingSpawn;
    private boolean allPlayersSleeping;
    public SavedDataStorage savedDataStorage;
    private ArrayList<AABB> boxes = new ArrayList<>();
    private boolean updatingTileEntities;
    private int maxRecurse = 0;
    private boolean spawnEnemies = true;
    private boolean spawnFriendlies = true;
    static int maxLoop = 0;
    private Set<ChunkPos> chunksToPoll = new HashSet<>();
    private int delayUntilNextMoodSound = this.random.nextInt(SharedConstants.TICKS_PER_SECOND * 60 * 10);
    private List<Entity> es = new ArrayList<>();
    public boolean isClientSide = false;
    
    public BiomeSource getBiomeSource() {
        return this.dimension.biomeSource;
    }
    
    public Level(final LevelStorage levelStorage, final String name, final Dimension fixedDimension, final long seed) {
        this.levelStorage = levelStorage;
        this.levelData = new LevelData(seed, name);
        this.dimension = fixedDimension;
        this.savedDataStorage = new SavedDataStorage(levelStorage);

        this.dimension.init(this);
        this.chunkSource = this.createChunkSource();

        this.updateSkyBrightness();
        this.prepareWeather();
    }
    
    public Level(final Level level, final Dimension dimension) {
        this.sessionId = level.sessionId;
        this.levelStorage = level.levelStorage;
        this.levelData = new LevelData(level.levelData);
        this.savedDataStorage = new SavedDataStorage(this.levelStorage);

        this.dimension = dimension;
        this.dimension.init(this);
        this.chunkSource = this.createChunkSource();
        this.updateSkyBrightness();
        this.prepareWeather();
    }
    
    public Level(final LevelStorage levelStorage, final String levelName, final long seed) {
        this(levelStorage, levelName, seed, null);
    }
    
    public Level(final LevelStorage levelStorage, final String levelName, final long seed, final Dimension fixedDimension) {
        this.levelStorage = levelStorage;
        this.savedDataStorage = new SavedDataStorage(levelStorage);

        this.levelData = levelStorage.prepareLevel();
        this.isNew = this.levelData == null;

        if (fixedDimension != null) {
            this.dimension = fixedDimension;
        }
        else if (this.levelData != null && this.levelData.getDimension() == -1) {
            this.dimension = Dimension.getNew(-1);
        }
        else {
            this.dimension = Dimension.getNew(0);
        }

        boolean setInitialSpawn = false;
        if (this.levelData == null) {
            this.levelData = new LevelData(seed, levelName);
            setInitialSpawn = true;
        }
        else {
            this.levelData.setLevelName(levelName);
        }

        this.dimension.init(this);

        this.chunkSource = this.createChunkSource();

        if (setInitialSpawn) {
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
        int xSpawn = 0;
        int ySpawn = 64;
        int zSpawn = 0;
        while (!this.dimension.isValidSpawn(xSpawn, zSpawn)) {
            xSpawn += this.random.nextInt(64) - this.random.nextInt(64);
            zSpawn += this.random.nextInt(64) - this.random.nextInt(64);
        }
        this.levelData.setSpawn(xSpawn, ySpawn, zSpawn);
        this.isFindingSpawn = false;
    }
    
    public void validateSpawn() {
        if (this.levelData.getYSpawn() <= 0) {
            this.levelData.setYSpawn(64);
        }

        int xSpawn = this.levelData.getXSpawn();
        int zSpawn = this.levelData.getZSpawn();
        while (this.getTopTile(xSpawn, zSpawn) == 0) {
            xSpawn += this.random.nextInt(8) - this.random.nextInt(8);
            zSpawn += this.random.nextInt(8) - this.random.nextInt(8);
        }
        this.levelData.setXSpawn(xSpawn);
        this.levelData.setZSpawn(zSpawn);
    }
    
    public int getTopTile(final int x, final int z) {
        int y = SEA_LEVEL;
        while (!this.isEmptyTile(x, y + 1, z)) {
            y++;
        }
        return this.getTile(x, y, z);
    }
    
    public void clearLoadedPlayerData() {
    }
    
    public void loadPlayer(final Player player) {
        try {
            final CompoundTag playerTag = this.levelData.getLoadedPlayerTag();
            if (playerTag != null) {
                player.load(playerTag);
                this.levelData.setLoadedPlayerTag(null);
            }

            if (this.chunkSource instanceof ChunkCache) {
                ChunkCache cache = (ChunkCache) this.chunkSource;
                int xc = Mth.floor((float) (int) player.x) >> 4;
                int zc = Mth.floor((float) (int) player.z) >> 4;
                cache.centerOn(xc, zc);
            }
            this.addEntity(player);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void save(final boolean force, final ProgressListener progressListener) {
        if (!this.chunkSource.shouldSave()) return;

        if (progressListener != null) progressListener.progressStartNoAbort("Saving level");
        this.saveLevelData();
        if (progressListener != null) progressListener.progressStage("Saving chunks");

        this.chunkSource.save(force, progressListener);
    }
    
    private void saveLevelData() {
        this.checkSession();
        this.levelStorage.saveLevelData(this.levelData, this.players);
        this.savedDataStorage.save();
    }
    
    public boolean pauseSave(final int saveStep) {
        if (!this.chunkSource.shouldSave()) return true;
        if (saveStep == 0) this.saveLevelData();
        return this.chunkSource.save(false, null);
    }
    
    public int getTile(final int x, final int y, final int z) {
        if (x < -MAX_LEVEL_SIZE || z < -MAX_LEVEL_SIZE || x >= MAX_LEVEL_SIZE || z > MAX_LEVEL_SIZE) {
            return 0;
        }

        if (y < MIN_HEIGHT) return 0;
        if (y >= MAX_HEIGHT) return 0;
        return this.getChunk(x >> 4, z >> 4).getTile(x & 0xF, y, z & 0xF);
    }
    
    public boolean isEmptyTile(final int x, final int y, final int z) {
        return this.getTile(x, y, z) == 0;
    }
    
    public boolean hasChunkAt(final int x, final int y, final int z) {
        if (y < MIN_HEIGHT || y >= MAX_HEIGHT) return false;

        return this.hasChunk(x >> 4, z >> 4);
    }
    
    public boolean hasChunksAt(final int x, final int y, final int z, final int r) {
        return this.hasChunksAt(x - r, y - r, z - r, x + r, y + r, z + r);
    }
    
    public boolean hasChunksAt(int x0, int y0, int z0, int x1, int y1, int z1) {
        if (y1 < MIN_HEIGHT || y0 >= MAX_HEIGHT) return false;

        x0 >>= 4;
        y0 >>= 4;
        z0 >>= 4;
        x1 >>= 4;
        y1 >>= 4;
        z1 >>= 4;

        for (int x = x0; x <= x1; ++x) {
            for (int z = z0; z <= z1; ++z) {
                if (!this.hasChunk(x, z)) return false;
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
        if (x < -MAX_LEVEL_SIZE || z < -MAX_LEVEL_SIZE || x >= MAX_LEVEL_SIZE || z > MAX_LEVEL_SIZE) return false;
        if (y < MIN_HEIGHT || y >= MAX_HEIGHT) return false;

        LevelChunk c = this.getChunk(x >> 4, z >> 4);
        return c.setTileAndData(x & 0xF, y, z & 0xF, tile, data);
    }
    
    public boolean setTileNoUpdate(final int x, final int y, final int z, final int tile) {
        if (x < -MAX_LEVEL_SIZE || z < -MAX_LEVEL_SIZE || x >= MAX_LEVEL_SIZE || z > MAX_LEVEL_SIZE) return false;
        if (y < MIN_HEIGHT || y >= MAX_HEIGHT) return false;

        LevelChunk c = this.getChunk(x >> 4, z >> 4);
        return c.setTile(x & 0xF, y, z & 0xF, tile);
    }
    
    public Material getMaterial(final int x, final int y, final int z) {
        final int t = this.getTile(x, y, z);
        if (t == 0) return Material.air;
        return Tile.tiles[t].material;
    }
    
    public int getData(int x, final int y, int z) {
        if (x < -MAX_LEVEL_SIZE || z < -MAX_LEVEL_SIZE || x >= MAX_LEVEL_SIZE || z > MAX_LEVEL_SIZE) return 0;
        if (y < MIN_HEIGHT || y >= MAX_HEIGHT) return 0;

        final LevelChunk c = this.getChunk(x >> 4, z >> 4);
        x &= 0xF;
        z &= 0xF;
        return c.getData(x, y, z);
    }
    
    public void setData(final int x, final int y, final int z, final int data) {
        if (this.setDataNoUpdate(x, y, z, data)) {
            final int t = this.getTile(x, y, z);
            if (Tile.sendTileData[t & 0xFF]) {
                this.tileUpdated(x, y, z, t);
            }
            else {
                this.updateNeighborsAt(x, y, z, t);
            }
        }
    }
    
    public boolean setDataNoUpdate(int x, final int y, int z, final int data) {
        if (x < -MAX_LEVEL_SIZE || z < -MAX_LEVEL_SIZE || x >= MAX_LEVEL_SIZE || z > MAX_LEVEL_SIZE) return false;
        if (y < MIN_HEIGHT || y >= MAX_HEIGHT) return false;

        final LevelChunk c = this.getChunk(x >> 4, z >> 4);
        x &= 0xF;
        z &= 0xF;
        c.setData(x, y, z, data);
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
            this.listeners.get(i).tileChanged(x, y, z);
        }
    }
    
    protected void tileUpdated(final int x, final int y, final int z, final int tile) {
        this.sendTileUpdated(x, y, z);
        this.updateNeighborsAt(x, y, z, tile);
    }
    
    public void lightColumnChanged(final int x, final int z, int y0, int y1) {
        if (y0 > y1) {
            final int tmp = y1;
            y1 = y0;
            y0 = tmp;
        }
        this.setTilesDirty(x, y0, z, x, y1, z);
    }
    
    public void setTileDirty(final int x, final int y, final int z) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).setTilesDirty(x, y, z, x, y, z);
        }
    }
    
    public void setTilesDirty(final int x0, final int y0, final int z0, final int x1, final int y1, final int z1) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).setTilesDirty(x0, y0, z0, x1, y1, z1);
        }
    }

    // Useless - Exists in b1.2 and LCE leaks
    public void swap(int x1, int y1, int z1, int x2, int y2, int z2) {
        int t1 = this.getTile(x1, y1, z1);
        int d1 = this.getData(x1, y1, z1);
        int t2 = this.getTile(x2, y2, z2);
        int d2 = this.getData(x2, y2, z2);

        this.setTileAndDataNoUpdate(x1, y1, z1, t2, d2);
        this.setTileAndDataNoUpdate(x2, y2, z2, t1, d1);

        this.updateNeighborsAt(x1, y1, z1, t2);
        this.updateNeighborsAt(x2, y2, z2, t1);
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
        if (this.noNeighborUpdate || this.isClientSide) return;
        final Tile tile = Tile.tiles[this.getTile(x, y, z)];
        if (tile != null) tile.neighborChanged(this, x, y, z, type);
    }
    
    public boolean canSeeSky(final int x, final int y, final int z) {
        return this.getChunk(x >> 4, z >> 4).isSkyLit(x & 0xF, y, z & 0xF);
    }
    
    public int getDaytimeRawBrightness(final int x, int y, final int z) {
        if (y < MIN_HEIGHT) return 0;
        if (y >= MAX_HEIGHT) y = MAX_HEIGHT - 1;
        return this.getChunk(x >> 4, z >> 4).getRawBrightness(x & 0xF, y, z & 0xF, 0);
    }
    
    public int getRawBrightness(final int x, final int y, final int z) {
        return this.getRawBrightness(x, y, z, true);
    }
    
    public int getRawBrightness(int x, int y, int z, final boolean propagate) {
        if (x < -MAX_LEVEL_SIZE || z < -MAX_LEVEL_SIZE || x >= MAX_LEVEL_SIZE || z > MAX_LEVEL_SIZE) return MAX_BRIGHTNESS;

        if (propagate) {
            final int id = this.getTile(x, y, z);
            if (id == Tile.stoneSlabHalf.id || id == Tile.farmland.id || id == Tile.stairs_stone.id || id == Tile.stairs_wood.id) {
                int br = this.getRawBrightness(x, y + 1, z, false);
                final int br1 = this.getRawBrightness(x + 1, y, z, false);
                final int br2 = this.getRawBrightness(x - 1, y, z, false);
                final int br3 = this.getRawBrightness(x, y, z + 1, false);
                final int br4 = this.getRawBrightness(x, y, z - 1, false);
                if (br1 > br) br = br1;
                if (br2 > br) br = br2;
                if (br3 > br) br = br3;
                if (br4 > br) br = br4;
                return br;
            }
        }
        if (y < MIN_HEIGHT) return 0;
        if (y >= MAX_HEIGHT) y = MAX_HEIGHT - 1;

        final LevelChunk c = this.getChunk(x >> 4, z >> 4);
        x &= 0xF;
        z &= 0xF;
        return c.getRawBrightness(x, y, z, this.skyDarken);
    }
    
    public boolean isSkyLit(int x, final int y, int z) {
        if (x < -MAX_LEVEL_SIZE || z < -MAX_LEVEL_SIZE || x >= MAX_LEVEL_SIZE || z > MAX_LEVEL_SIZE) return false;

        if (y < MIN_HEIGHT) return false;
        if (y >= MAX_HEIGHT) return true;

        if (!this.hasChunk(x >> 4, z >> 4)) return false;

        final LevelChunk c = this.getChunk(x >> 4, z >> 4);
        x &= 0xF;
        z &= 0xF;
        return c.isSkyLit(x, y, z);
    }
    
    public int getHeightmap(final int x, final int z) {
        if (x < -MAX_LEVEL_SIZE || z < -MAX_LEVEL_SIZE || x >= MAX_LEVEL_SIZE || z > MAX_LEVEL_SIZE) return 0;

        if (!this.hasChunk(x >> 4, z >> 4)) return 0;

        LevelChunk c = this.getChunk(x >> 4, z >> 4);
        return c.getHeightmap(x & 0xF, z & 0xF);
    }
    
    public void updateLightIfOtherThan(final LightLayer layer, final int x, final int y, final int z, int expected) {
        if (this.dimension.hasCeiling && layer == LightLayer.Sky) return;

        if (!this.hasChunkAt(x, y, z)) return;

        if (layer == LightLayer.Sky) {
            if (this.isSkyLit(x, y, z)) expected = 15;
        }
        else if (layer == LightLayer.Block) {
            final int t = this.getTile(x, y, z);
            if (Tile.lightEmission[t] > expected) expected = Tile.lightEmission[t];
        }

        if (this.getBrightness(layer, x, y, z) != expected) {
            this.updateLight(layer, x, y, z, x, y, z);
        }
    }
    
    public int getBrightness(final LightLayer layer, final int x, int y, final int z) {
        if (y < MIN_HEIGHT) y = 0;
        if (y >= MAX_HEIGHT) y = MAX_HEIGHT - 1;

        if (y < MIN_HEIGHT || y >= MAX_HEIGHT || x < -MAX_LEVEL_SIZE || z < -MAX_LEVEL_SIZE || x >= MAX_LEVEL_SIZE || z > MAX_LEVEL_SIZE) return layer.surrounding;

        final int xc = x >> 4;
        final int zc = z >> 4;
        if (!this.hasChunk(xc, zc)) return 0;

        LevelChunk c = this.getChunk(xc, zc);
        return c.getBrightness(layer, x & 0xF, y, z & 0xF);
    }
    
    public void setBrightness(final LightLayer layer, final int x, final int y, final int z, final int brightness) {
        if (x < -MAX_LEVEL_SIZE || z < -MAX_LEVEL_SIZE || x >= MAX_LEVEL_SIZE || z > MAX_LEVEL_SIZE) return;
        if (y < MIN_HEIGHT || y >= MAX_HEIGHT) return;

        if (!this.hasChunk(x >> 4, z >> 4)) return;
        LevelChunk c = this.getChunk(x >> 4, z >> 4);
        c.setBrightness(layer, x & 0xF, y, z & 0xF, brightness);

        for (int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).tileChanged(x, y, z);
        }
    }
    
    public float getBrightness(final int x, final int y, final int z, final int emitt) {
        int n = this.getRawBrightness(x, y, z);
        if (n < emitt) n = emitt;
        return this.dimension.brightnessRamp[n];
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
        if (Double.isNaN(a.x) || Double.isNaN(a.y) || Double.isNaN(a.z)) return null;
        if (Double.isNaN(b.x) || Double.isNaN(b.y) || Double.isNaN(b.z)) return null;

        final int xTile1 = Mth.floor(b.x);
        final int yTile1 = Mth.floor(b.y);
        final int zTile1 = Mth.floor(b.z);

        int xTile0 = Mth.floor(a.x);
        int yTile0 = Mth.floor(a.y);
        int zTile0 = Mth.floor(a.z);

        {
            final int t = this.getTile(xTile0, yTile0, zTile0);
            final int data = this.getData(xTile0, yTile0, zTile0);
            final Tile tile = Tile.tiles[t];
            if (solidOnly && tile != null && tile.getAABB(this, xTile0, yTile0, zTile0) == null) {
                // No collision
            } else if (t > 0 && tile.mayPick(data, liquid)) {
                final HitResult r = tile.clip(this, xTile0, yTile0, zTile0, a, b);
                if (r != null) return r;
            }
        }

        int maxIterations = 200;
        while (maxIterations-- >= 0) {
            if (Double.isNaN(a.x) || Double.isNaN(a.y) || Double.isNaN(a.z)) return null;
            if (xTile0 == xTile1 && yTile0 == yTile1 && zTile0 == zTile1) return null;

            boolean xClipped = true;
            boolean yClipped = true;
            boolean zClipped = true;

            double x = 999.0;
            double y = 999.0;
            double z = 999.0;

            if (xTile1 > xTile0) x = xTile0 + 1.0;
            else if (xTile1 < xTile0) x = xTile0 + 0.0;
            else xClipped = false;

            if (yTile1 > yTile0) y = yTile0 + 1.0;
            else if (yTile1 < yTile0) y = yTile0 + 0.0;
            else yClipped = false;

            if (zTile1 > zTile0) z = zTile0 + 1.0;
            else if (zTile1 < zTile0) z = zTile0 + 0.0;
            else zClipped = false;

            double xDist = 999.0;
            double yDist = 999.0;
            double zDist = 999.0;

            final double xd = b.x - a.x;
            final double yd = b.y - a.y;
            final double zd = b.z - a.z;

            if (xClipped) xDist = (x - a.x) / xd;
            if (yClipped) yDist = (y - a.y) / yd;
            if (zClipped) zDist = (z - a.z) / zd;

            int face = 0;
            if (xDist < yDist && xDist < zDist) {
                if (xTile1 > xTile0) face = 4;
                else face = 5;

                a.x = x;
                a.y += yd * xDist;
                a.z += zd * xDist;
            }
            else if (yDist < zDist) {
                if (yTile1 > yTile0) face = 0;
                else face = 1;

                a.x += xd * yDist;
                a.y = y;
                a.z += zd * yDist;
            }
            else {
                if (zTile1 > zTile0) face = 2;
                else face = 3;

                a.x += xd * zDist;
                a.y += yd * zDist;
                a.z = z;
            }

            final Vec3 tPos = Vec3.newTemp(a.x, a.y, a.z);
            xTile0 = (int)(tPos.x = Mth.floor(a.x));
            if (face == 5) {
                xTile0--;
                tPos.x++;
            }
            yTile0 = (int)(tPos.y = Mth.floor(a.y));
            if (face == 1) {
                yTile0--;
                tPos.y++;
            }
            zTile0 = (int)(tPos.z = Mth.floor(a.z));
            if (face == 3) {
                zTile0--;
                tPos.z++;
            }

            final int t = this.getTile(xTile0, yTile0, zTile0);
            final int data = this.getData(xTile0, yTile0, zTile0);
            final Tile tile = Tile.tiles[t];
            if (solidOnly && tile != null && tile.getAABB(this, xTile0, yTile0, zTile0) == null) {
                // No collision
            } else if (t > 0 && tile.mayPick(data, liquid)) {
                final HitResult r = tile.clip(this, xTile0, yTile0, zTile0, a, b);
                if (r != null) return r;
            }
        }
        return null;
    }
    
    public void playSound(final Entity entity, final String name, final float volume, final float pitch) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).playSound(name, entity.x, entity.y - entity.heightOffset, entity.z, volume, pitch);
        }
    }
    
    public void playSound(final double x, final double y, final double z, final String name, final float volume, final float pitch) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).playSound(name, x, y, z, volume, pitch);
        }
    }
    
    public void playStreamingMusic(final String name, final int x, final int y, final int z) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).playStreamingMusic(name, x, y, z);
        }
    }
    
    public void addParticle(final String id, final double x, final double y, final double z, final double xd, final double yd, final double zd) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).addParticle(id, x, y, z, xd, yd, zd);
        }
    }
    
    public boolean addGlobalEntity(final Entity e) {
        this.globalEntities.add(e);
        return true;
    }
    
    public boolean addEntity(final Entity e) {
        final int xc = Mth.floor(e.x / 16.0);
        final int zc = Mth.floor(e.z / 16.0);

        boolean forced = false;
        if (e instanceof Player) {
            forced = true;
        }

        if (forced || this.hasChunk(xc, zc)) {
            if (e instanceof Player) {
                this.players.add((Player) e);
                this.updateSleepingPlayerList();
            }
            this.getChunk(xc, zc).addEntity(e);
            this.entities.add(e);
            this.entityAdded(e);
            return true;
        }
        return false;
    }
    
    protected void entityAdded(final Entity e) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).entityAdded(e);
        }
    }
    
    protected void entityRemoved(final Entity e) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).entityRemoved(e);
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

        final int xc = e.xChunk;
        final int zc = e.zChunk;
        if (e.inChunk && this.hasChunk(xc, zc)) {
            this.getChunk(xc, zc).removeEntity(e);
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
        final int x0 = Mth.floor(box.x0);
        final int x1 = Mth.floor(box.x1 + 1.0);
        final int y0 = Mth.floor(box.y0);
        final int y1 = Mth.floor(box.y1 + 1.0);
        final int z0 = Mth.floor(box.z0);
        final int z1 = Mth.floor(box.z1 + 1.0);

        for (int x = x0; x < x1; ++x) {
            for (int z = z0; z < z1; ++z) {
                if (this.hasChunkAt(x, 64, z)) {
                    for (int y = y0 - 1; y < y1; ++y) {
                        final Tile tile = Tile.tiles[this.getTile(x, y, z)];
                        if (tile != null) {
                            tile.addAABBs(this, x, y, z, box, this.boxes);
                        }
                    }
                }
            }
        }

        final double r = 0.25;
        final List<Entity> ee = this.getEntities(source, box.grow(r, r, r));
        for (int i = 0; i < ee.size(); ++i) {
            AABB collideBox = ee.get(i).getCollideBox();
            if (collideBox != null && collideBox.intersects(box)) {
                this.boxes.add(collideBox);
            }

            collideBox = source.getCollideAgainstBox(ee.get(i));
            if (collideBox != null && collideBox.intersects(box)) {
                this.boxes.add(collideBox);
            }
        }
        return this.boxes;
    }
    
    public int getSkyDarken(final float a) {
        float td = this.getTimeOfDay(a);

        float br = 1 - (Mth.cos(td * Mth.PI * 2) * 2 + 0.5f);
        if (br < 0) br = 0;
        if (br > 1) br = 1;

        br = 1 - br;

        br *= (float) (1 - this.getRainLevel(a) * 5 / 16.0);
        br *= (float) (1 - this.getThunderLevel(a) * 5 / 16.0);
        br = 1 - br;
        return (int)(br * 11);
    }
    
    public Vec3 getSkyColor(final Entity source, final float a) {
        float td = this.getTimeOfDay(a);

        float br = Mth.cos(td * Mth.PI * 2.0f) * 2.0f + 0.5f;
        if (br < 0.0f) br = 0.0f;
        if (br > 1.0f) br = 1.0f;

        final int xx = Mth.floor(source.x);
        final int zz = Mth.floor(source.z);
        Biome biome = this.getBiomeSource().getBiome(xx, zz);
        float temp = (float) this.getBiomeSource().getTemperature(xx, zz);
        final int skyColor = biome.getSkyColor(temp);

        float r = (skyColor >> 16 & 0xFF) / 255.0f;
        float g = (skyColor >> 8 & 0xFF) / 255.0f;
        float b = (skyColor & 0xFF) / 255.0f;
        r *= br;
        g *= br;
        b *= br;

        final float rainLevel = this.getRainLevel(a);
        if (rainLevel > 0.0f) {
            final float mid = (r * 0.3f + g * 0.59f + b * 0.11f) * 0.6f;

            final float ba = 1.0f - rainLevel * 0.75f;
            r = r * ba + mid * (1.0f - ba);
            g = g * ba + mid * (1.0f - ba);
            b = b * ba + mid * (1.0f - ba);
        }
        final float thunderLevel = this.getThunderLevel(a);
        if (thunderLevel > 0.0f) {
            final float mid = (r * 0.3f + g * 0.59f + b * 0.11f) * 0.2f;

            final float ba = 1.0f - thunderLevel * 0.75f;
            r = r * ba + mid * (1.0f - ba);
            g = g * ba + mid * (1.0f - ba);
            b = b * ba + mid * (1.0f - ba);
        }

        if (this.lightningBoltTime > 0) {
            float f = this.lightningBoltTime - a;
            if (f > 1.0f) f = 1.0f;
            f = f * 0.45f;
            r = r * (1.0f - f) + 0.8f * f;
            g = g * (1.0f - f) + 0.8f * f;
            b = b * (1.0f - f) + 1.0f * f;
        }

        return Vec3.newTemp(r, g, b);
    }
    
    public float getTimeOfDay(final float a) {
        return this.dimension.getTimeOfDay(this.levelData.getTime(), a);
    }

    // Useless - Exists in b1.2 and LCE leaks
    public float getSunAngle(float a) {
        float td = this.getTimeOfDay(a);
        return td * (float) Math.PI * 2.0F;
    }

    public Vec3 getCloudColor(final float a) {
        float td = this.getTimeOfDay(a);

        float br = Mth.cos(td * Mth.PI * 2.0f) * 2.0f + 0.5f;
        if (br < 0.0f) br = 0.0f;
        if (br > 1.0f) br = 1.0f;

        float r = (this.cloudColor >> 16 & 0xFFL) / 255.0f;
        float g = (this.cloudColor >> 8 & 0xFFL) / 255.0f;
        float b = (this.cloudColor & 0xFFL) / 255.0f;

        final float rainLevel = this.getRainLevel(a);
        if (rainLevel > 0.0f) {
            final float mid = (r * 0.3f + g * 0.59f + b * 0.11f) * 0.6f;

            final float ba = 1.0f - rainLevel * 0.95f;
            r = r * ba + mid * (1.0f - ba);
            g = g * ba + mid * (1.0f - ba);
            b = b * ba + mid * (1.0f - ba);
        }

        r *= (br * 0.9f + 0.1f);
        g *= (br * 0.9f + 0.1f);
        b *= (br * 0.85f + 0.15f);

        final float thunderLevel = this.getThunderLevel(a);
        if (thunderLevel > 0.0f) {
            final float mid = (r * 0.3f + g * 0.59f + b * 0.11f) * 0.2f;

            final float ba = 1.0f - thunderLevel * 0.95f;
            r = r * ba + mid * (1.0f - ba);
            g = g * ba + mid * (1.0f - ba);
            b = b * ba + mid * (1.0f - ba);
        }

        return Vec3.newTemp(r, g, b);
    }
    
    public Vec3 getFogColor(final float a) {
        float td = this.getTimeOfDay(a);
        return this.dimension.getFogColor(td, a);
    }
    
    public int getTopRainBlock(int x, int z) {
        final LevelChunk c = this.getChunkAt(x, z);

        int y = MAX_HEIGHT - 1;

        x &= 0xF;
        z &= 0xF;
        while (y > 0) {
            final int t = c.getTile(x, y, z);
            final Material m = t == 0 ? Material.air : Tile.tiles[t].material;
            if (!m.blocksMotion() && !m.isLiquid()) {
                y--;
            } else {
                return y + 1;
            }
        }
        return -1;
    }

    public int getTopSolidBlock(int x, int z) {
        final LevelChunk c = this.getChunkAt(x, z);

        int y = MAX_HEIGHT - 1;

        x &= 0xF;
        z &= 0xF;

        while (y > 0) {
            final int t = c.getTile(x, y, z);
            if (t == 0 || !Tile.tiles[t].material.blocksMotion()) {
                y--;
            } else {
                return y + 1;
            }
        }
        return -1;
    }
    
    public float getStarBrightness(final float a) {
        float td = this.getTimeOfDay(
                a);
        float br = 1.0f - (Mth.cos(td * Mth.PI * 2.0f) * 2.0f + 0.75f);
        if (br < 0.0f) br = 0.0f;
        if (br > 1.0f) br = 1.0f;

        return br * br * 0.5f;
    }
    
    public void addToTickNextTick(final int x, final int y, final int z, final int tileId, final int tickDelay) {
        final TickNextTickData td = new TickNextTickData(x, y, z, tileId);
        final int r = 8;
        if (this.instaTick) {
            if (this.hasChunksAt(td.x - r, td.y - r, td.z - r, td.x + r, td.y + r, td.z + r)) {
                final int id = this.getTile(td.x, td.y, td.z);
                if (id == td.tileId && id > 0) {
                    Tile.tiles[id].tick(this, td.x, td.y, td.z, this.random);
                }
            }
            return;
        }

        if (this.hasChunksAt(x - r, y - r, z - r, x + r, y + r, z + r)) {
            if (tileId > 0) {
                td.delay(tickDelay + this.levelData.getTime());
            }
            if (!this.tickNextTickSet.contains(td)) {
                this.tickNextTickSet.add(td);
                this.tickNextTickList.add(td);
            }
        }
    }
    
    public void tickEntities() {
        for (int i = 0; i < this.globalEntities.size(); ++i) {
            final Entity e = this.globalEntities.get(i);
            e.tick();
            if (e.removed) {
                this.globalEntities.remove(i--);
            }
        }

        this.entities.removeAll(this.entitiesToRemove);

        for (int i = 0; i < this.entitiesToRemove.size(); ++i) {
            final Entity e = this.entitiesToRemove.get(i);
            final int xc = e.xChunk;
            final int zc = e.zChunk;
            if (e.inChunk && this.hasChunk(xc, zc)) {
                this.getChunk(xc, zc).removeEntity(e);
            }
        }

        for (int i = 0; i < this.entitiesToRemove.size(); ++i) {
            this.entityRemoved(this.entitiesToRemove.get(i));
        }
        this.entitiesToRemove.clear();

        for (int i = 0; i < this.entities.size(); ++i) {
            final Entity e = this.entities.get(i);

            if (e.riding != null) {
                if (e.riding.removed || e.riding.rider != e) {
                    e.riding.rider = null;
                    e.riding = null;
                } else {
                    continue;
                }
            }

            if (!e.removed) {
                this.tick(e);
            }

            if (e.removed) {
                final int xc = e.xChunk;
                final int zc = e.zChunk;
                if (e.inChunk && this.hasChunk(xc, zc)) {
                    this.getChunk(xc, zc).removeEntity(e);
                }
                this.entities.remove(i--);
                this.entityRemoved(e);
            }
        }

        this.updatingTileEntities = true;
        final Iterator<TileEntity> iterator = this.tileEntityList.iterator();
        while (iterator.hasNext()) {
            final TileEntity te = iterator.next();
            if (!te.isRemoved()) {
                te.tick();
            }

            if (te.isRemoved()) {
                iterator.remove();
                final LevelChunk lc = this.getChunk(te.x >> 4, te.z >> 4);
                if (lc != null) lc.removeTileEntity(te.x & 0xF, te.y, te.z & 0xF);
            }
        }
        this.updatingTileEntities = false;

        if (!this.pendingTileEntities.isEmpty()) {
            for (final TileEntity e : this.pendingTileEntities) {
                if (!e.isRemoved()) {
                    if (!this.tileEntityList.contains(e)) {
                        this.tileEntityList.add(e);
                    }
                    final LevelChunk lc = this.getChunk(e.x >> 4, e.z >> 4);
                    if (lc != null) lc.setTileEntity(e.x & 0xF, e.y, e.z & 0xF, e);

                    this.sendTileUpdated(e.x, e.y, e.z);
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
        final int xc = Mth.floor(e.x);
        final int zc = Mth.floor(e.z);
        final int r = 32;
        if (actual && !this.hasChunksAt(xc - r, 0, zc - r, xc + r, 128, zc + r)) {
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

        // SANTITY!!
        if (Double.isNaN(e.x) || Double.isInfinite(e.x)) e.x = e.xOld;
        if (Double.isNaN(e.y) || Double.isInfinite(e.y)) e.y = e.yOld;
        if (Double.isNaN(e.z) || Double.isInfinite(e.z)) e.z = e.zOld;
        if (Double.isNaN(e.xRot) || Double.isInfinite(e.xRot)) e.xRot = e.xRotO;
        if (Double.isNaN(e.yRot) || Double.isInfinite(e.yRot)) e.yRot = e.yRotO;

        final int xcn = Mth.floor(e.x / 16.0);
        final int ycn = Mth.floor(e.y / 16.0);
        final int zcn = Mth.floor(e.z / 16.0);

        if (!e.inChunk || e.xChunk != xcn || e.yChunk != ycn || e.zChunk != zcn) {
            if (e.inChunk && this.hasChunk(e.xChunk, e.zChunk)) {
                this.getChunk(e.xChunk, e.zChunk).removeEntity(e, e.yChunk);
            }

            if (this.hasChunk(xcn, zcn)) {
                e.inChunk = true;
                this.getChunk(xcn, zcn).addEntity(e);
            }
            else {
                e.inChunk = false;
                // e.remove();
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
        final List<Entity> ents = this.getEntities(null, aabb);
        for (int i = 0; i < ents.size(); ++i) {
            final Entity e = ents.get(i);
            if (!e.removed && e.blocksBuilding) return false;
        }
        return true;
    }

    public boolean containsAnyBlocks(final AABB box) {
        int x0 = Mth.floor(box.x0);
        int x1 = Mth.floor(box.x1 + 1.0);
        int y0 = Mth.floor(box.y0);
        int y1 = Mth.floor(box.y1 + 1.0);
        int z0 = Mth.floor(box.z0);
        int z1 = Mth.floor(box.z1 + 1.0);

        if (box.x0 < 0.0) x0--;
        if (box.y0 < 0.0) y0--;
        if (box.z0 < 0.0) z0--;

        for (int x = x0; x < x1; ++x) {
            for (int y = y0; y < y1; ++y) {
                for (int z = z0; z < z1; ++z) {
                    if (Tile.tiles[this.getTile(x, y, z)] != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean containsAnyLiquid(final AABB box) {
        int x0 = Mth.floor(box.x0);
        int x1 = Mth.floor(box.x1 + 1.0);
        int y0 = Mth.floor(box.y0);
        int y1 = Mth.floor(box.y1 + 1.0);
        int z0 = Mth.floor(box.z0);
        int z1 = Mth.floor(box.z1 + 1.0);

        if (box.x0 < 0.0) x0--;
        if (box.y0 < 0.0) y0--;
        if (box.z0 < 0.0) z0--;

        for (int x = x0; x < x1; ++x) {
            for (int y = y0; y < y1; ++y) {
                for (int z = z0; z < z1; ++z) {
                    final Tile tile = Tile.tiles[this.getTile(x, y, z)];
                    if (tile != null && tile.material.isLiquid()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean containsFireTile(final AABB box) {
        final int x0 = Mth.floor(box.x0);
        final int x1 = Mth.floor(box.x1 + 1.0);
        final int y0 = Mth.floor(box.y0);
        final int y1 = Mth.floor(box.y1 + 1.0);
        final int z0 = Mth.floor(box.z0);
        final int z1 = Mth.floor(box.z1 + 1.0);

        if (this.hasChunksAt(x0, y0, z0, x1, y1, z1)) {
            for (int x = x0; x < x1; ++x) {
                for (int y = y0; y < y1; ++y) {
                    for (int z = z0; z < z1; ++z) {
                        final int t = this.getTile(x, y, z);
                        if (t == Tile.fire.id || t == Tile.lava.id || t == Tile.calmLava.id) return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean checkAndHandleWater(final AABB box, final Material material, final Entity e) {
        final int x0 = Mth.floor(box.x0);
        final int x1 = Mth.floor(box.x1 + 1.0);

        final int y0 = Mth.floor(box.y0);
        final int y1 = Mth.floor(box.y1 + 1.0);

        final int z0 = Mth.floor(box.z0);
        final int z1 = Mth.floor(box.z1 + 1.0);

        if (!this.hasChunksAt(x0, y0, z0, x1, y1, z1)) {
            return false;
        }

        boolean ok = false;
        Vec3 current = Vec3.newTemp(0.0, 0.0, 0.0);
        for (int x = x0; x < x1; ++x) {
            for (int y = y0; y < y1; ++y) {
                for (int z = z0; z < z1; ++z) {
                    final Tile tile = Tile.tiles[this.getTile(x, y, z)];
                    if (tile != null && tile.material == material) {
                        double yt0 = (y + 1 - LiquidTile.getHeight(this.getData(x, y, z)));
                        if (y1 >= yt0) {
                            ok = true;
                            tile.handleEntityInside(this, x, y, z, e, current);
                        }
                    }
                }
            }
        }
        if (current.length() > 0.0) {
            current = current.normalize();
            final double pow = 0.014;
            e.xd += current.x * pow;
            e.yd += current.y * pow;
            e.zd += current.z * pow;
        }
        return ok;
    }
    
    public boolean containsMaterial(final AABB box, final Material material) {
        final int x0 = Mth.floor(box.x0);
        final int x1 = Mth.floor(box.x1 + 1.0);
        final int y0 = Mth.floor(box.y0);
        final int y1 = Mth.floor(box.y1 + 1.0);
        final int z0 = Mth.floor(box.z0);
        final int z1 = Mth.floor(box.z1 + 1.0);

        for (int x = x0; x < x1; ++x) {
            for (int y = y0; y < y1; ++y) {
                for (int z = z0; z < z1; ++z) {
                    final Tile tile = Tile.tiles[this.getTile(x, y, z)];
                    if (tile != null && tile.material == material) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean containsLiquid(final AABB box, final Material material) {
        final int x0 = Mth.floor(box.x0);
        final int x1 = Mth.floor(box.x1 + 1.0);
        final int y0 = Mth.floor(box.y0);
        final int y1 = Mth.floor(box.y1 + 1.0);
        final int z0 = Mth.floor(box.z0);
        final int z1 = Mth.floor(box.z1 + 1.0);

        for (int x = x0; x < x1; ++x) {
            for (int y = y0; y < y1; ++y) {
                for (int z = z0; z < z1; ++z) {
                    final Tile tile = Tile.tiles[this.getTile(x, y, z)];
                    if (tile != null && tile.material == material) {
                        final int data = this.getData(x, y, z);
                        double yh1 = y + 1;
                        if (data < 8) {
                            yh1 = y + 1 - data / 8.0;
                        }
                        if (yh1 >= box.y0) {
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
        explosion.finalizeExplosion(true);
        return explosion;
    }
    
    public float getSeenPercent(final Vec3 center, final AABB bb) {
        final double xs = 1.0 / ((bb.x1 - bb.x0) * 2.0 + 1.0);
        final double ys = 1.0 / ((bb.y1 - bb.y0) * 2.0 + 1.0);
        final double zs = 1.0 / ((bb.z1 - bb.z0) * 2.0 + 1.0);
        int hits = 0;
        int count = 0;
        for (float xx = 0.0f; xx <= 1.0f; xx += (float)xs) {
            for (float yy = 0.0f; yy <= 1.0f; yy += (float)ys) {
                for (float zz = 0.0f; zz <= 1.0f; zz += (float)zs) {
                    double x = bb.x0 + (bb.x1 - bb.x0) * xx;
                    double y = bb.y0 + (bb.y1 - bb.y0) * yy;
                    double z = bb.z0 + (bb.z1 - bb.z0) * zz;
                    HitResult res = this.clip(Vec3.newTemp(x, y, z), center);
                    if (res == null) hits++;
                    count++;
                }
            }
        }
        return hits / (float)count;
    }
    
    public void extinguishFire(final Player player, int x, int y, int z, final int face) {
        if (face == 0) y--;
        if (face == 1) y++;
        if (face == 2) z--;
        if (face == 3) z++;
        if (face == 4) x--;
        if (face == 5) x++;

        if (this.getTile(x, y, z) == Tile.fire.id) {
            this.levelEvent(player, LevelEvent.SOUND_FIZZ, x, y, z, 0);
            this.setTile(x, y, z, 0);
        }
    }
    
    public Entity findSubclassOf(final Class<? extends Entity> entityClass) {
        return null;
    }
    
    public String gatherStats() {
        return "All: " + this.entities.size();
    }
    
    public String gatherChunkSourceStats() {
        return this.chunkSource.gatherStats();
    }
    
    public TileEntity getTileEntity(final int x, final int y, final int z) {
        final LevelChunk lc = this.getChunk(x >> 4, z >> 4);
        if (lc != null) return lc.getTileEntity(x & 0xF, y, z & 0xF);

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
                final LevelChunk lc = this.getChunk(x >> 4, z >> 4);
                if (lc != null) lc.setTileEntity(x & 0xF, y, z & 0xF, tileEntity);
            }
        }
    }
    
    public void removeTileEntity(final int x, final int y, final int z) {
        final TileEntity te = this.getTileEntity(x, y, z);
        if (te != null && this.updatingTileEntities) {
            te.setRemoved();
        }
        else {
            if (te != null) {
                this.tileEntityList.remove(te);
            }
            final LevelChunk lc = this.getChunk(x >> 4, z >> 4);
            if (lc != null) lc.removeTileEntity(x & 0xF, y, z & 0xF);
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
        if (this.maxRecurse >= 50) return false;
        this.maxRecurse++;
        try {
            int count = 500;
            while (!this.lightUpdates.isEmpty()) {
                if (--count <= 0) return true;
                this.lightUpdates.remove(this.lightUpdates.size() - 1).update(this);
            }
            return false;
        }
        finally {
            this.maxRecurse--;
        }
    }
    
    public void updateLight(final LightLayer layer, final int x0, final int y0, final int z0, final int x1, final int y1, final int z1) {
        this.updateLight(layer, x0, y0, z0, x1, y1, z1, true);
    }
    
    public void updateLight(final LightLayer layer, final int x0, final int y0, final int z0, final int x1, final int y1, final int z1, final boolean expand) {
        if (this.dimension.hasCeiling && layer == LightLayer.Sky) return;

        // Useless - TODO little to no info on internal method contents
        Level.maxLoop++;
        try {
            if (Level.maxLoop == 50) return;
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
            final int maxUpdates = 1000000;
            if (this.lightUpdates.size() > 1000000) {
                System.out.println("More than " + maxUpdates + " updates, aborting lighting updates");
                this.lightUpdates.clear();
            }
        }
        finally {
            Level.maxLoop--;
        }
    }
    
    public void updateSkyBrightness() {
        final int newDark = this.getSkyDarken(1.0f);
        if (newDark != this.skyDarken) {
            this.skyDarken = newDark;
        }
    }
    
    public void setSpawnSettings(final boolean spawnEnemies, final boolean spawnFriendlies) {
        this.spawnEnemies = spawnEnemies;
        this.spawnFriendlies = spawnFriendlies;
    }
    
    public void tick() {
        this.tickWeather();
        if (this.allPlayersAreSleeping()) {
            boolean somebodyWokeUp = false;
            if (this.spawnEnemies && this.difficulty >= Difficulty.EASY) {
                somebodyWokeUp = MobSpawner.attackSleepingPlayers(this, this.players);
            }

            if (!somebodyWokeUp) {
                final long newTime = this.levelData.getTime() + TICKS_PER_DAY;
                this.levelData.setTime(newTime - newTime % TICKS_PER_DAY);
                this.awakenAllPlayers();
            }
        }

        MobSpawner.tick(this, this.spawnEnemies, this.spawnFriendlies);
        this.chunkSource.tick();

        final int newDark = this.getSkyDarken(1.0f);
        if (newDark != this.skyDarken) {
            this.skyDarken = newDark;
            for (int i = 0; i < this.listeners.size(); ++i) {
                this.listeners.get(i).skyColorChanged();
            }
        }

        final long time = this.levelData.getTime() + 1L;
        if (time % this.saveInterval == 0L) {
            this.save(false, null);
        }
        this.levelData.setTime(time);

        this.tickPendingTicks(false);
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
        if (this.dimension.hasCeiling) return;

        if (this.lightningTime > 0) {
            this.lightningTime--;
        }

        int thunderTime = this.levelData.getThunderTime();
        if (thunderTime <= 0) {
            if (this.levelData.isThundering()) {
                this.levelData.setThunderTime(this.random.nextInt(SharedConstants.TICKS_PER_SECOND * 60 * 10) + SharedConstants.TICKS_PER_SECOND * 60 * 3);
            }
            else {
                this.levelData.setThunderTime(this.random.nextInt(TICKS_PER_DAY * 7) + TICKS_PER_DAY / 2);
            }
        }
        else {
            thunderTime--;
            this.levelData.setThunderTime(thunderTime);
            if (thunderTime <= 0) {
                this.levelData.setThundering(!this.levelData.isThundering());
            }
        }

        int rainTime = this.levelData.getRainTime();
        if (rainTime <= 0) {
            if (this.levelData.isRaining()) {
                this.levelData.setRainTime(this.random.nextInt(TICKS_PER_DAY / 2) + TICKS_PER_DAY / 2);
            }
            else {
                this.levelData.setRainTime(this.random.nextInt(TICKS_PER_DAY * 7) + TICKS_PER_DAY / 2);
            }
        }
        else {
            rainTime--;
            this.levelData.setRainTime(rainTime);
            if (rainTime <= 0) {
                this.levelData.setRaining(!this.levelData.isRaining());
            }
//            if(!this.levelData.isRaining()) {
//                this.levelData.setRaining(true);
//            }
        }

        this.oRainLevel = this.rainLevel;
        if (this.levelData.isRaining()) {
            this.rainLevel += (float)0.01;
        }
        else {
            this.rainLevel -= (float)0.01;
        }
        if (this.rainLevel < 0.0f) this.rainLevel = 0.0f;
        if (this.rainLevel > 1.0f) this.rainLevel = 1.0f;

        this.oThunderLevel = this.thunderLevel;
        if (this.levelData.isThundering()) {
            this.thunderLevel += (float)0.01;
        }
        else {
            this.thunderLevel -= (float)0.01;
        }
        if (this.thunderLevel < 0.0f) this.thunderLevel = 0.0f;
        if (this.thunderLevel > 1.0f) this.thunderLevel = 1.0f;
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
            final int xx = Mth.floor(player.x / 16.0);
            final int zz = Mth.floor(player.z / 16.0);

            int r = CHUNK_POLL_RANGE;
            for (int x = -r; x <= r; ++x) {
                for (int z = -r; z <= r; ++z) {
                    this.chunksToPoll.add(new ChunkPos(x + xx, z + zz));
                }
            }
        }

        if (this.delayUntilNextMoodSound > 0) this.delayUntilNextMoodSound--;

        for (final ChunkPos chunkPos : this.chunksToPoll) {
            final int xo = chunkPos.x * 16;
            final int zo = chunkPos.z * 16;
            final LevelChunk lc = this.getChunk(chunkPos.x, chunkPos.z);

            if (this.delayUntilNextMoodSound == 0) {
                this.randValue = this.randValue * 3 + this.addend;
                int val = this.randValue >> 2;
                int x = val & 0xF;
                int z = val >> 8 & 0xF;
                int y = val >> 16 & 0x7F;

                int id = lc.getTile(x, y, z);
                x += xo;
                z += zo;
                if (id == 0 && this.getDaytimeRawBrightness(x, y, z) <= this.random.nextInt(8) && this.getBrightness(LightLayer.Sky, x, y, z) <= 0) {
                    final Player player = this.getNearestPlayer(x + 0.5, y + 0.5, z + 0.5, 8.0);
                    if (player != null && player.distanceToSqr(x + 0.5, y + 0.5, z + 0.5) > 4.0) {
                        this.playSound(x + 0.5, y + 0.5, z + 0.5, "ambient.cave.cave", 0.7f, 0.8f + this.random.nextFloat() * 0.2f);
                        this.delayUntilNextMoodSound = this.random.nextInt(SharedConstants.TICKS_PER_SECOND * 60 * 10) + SharedConstants.TICKS_PER_SECOND * 60 * 5;
                    }
                }
            }

            int prob = 100000;
            if (this.random.nextInt(prob) == 0 && this.isRaining() && this.isThundering()) {
                this.randValue = this.randValue * 3 + this.addend;
                final int val = this.randValue >> 2;
                final int x = xo + (val & 0xF);
                final int z = zo + (val >> 8 & 0xF);
                final int y = this.getTopRainBlock(x, z);

                if (this.isRainingAt(x, y, z)) {
                    this.addGlobalEntity(new LightningBolt(this, x, y, z));
                    this.lightningTime = 2;
                }
            }

            if (this.random.nextInt(16) == 0) {
                this.randValue = this.randValue * 3 + this.addend;
                final int val = this.randValue >> 2;
                final int x = val & 0xF;
                final int z = val >> 8 & 0xF;
                final int yy = this.getTopRainBlock(x + xo, z + zo);
                boolean cold = this.getBiomeSource().getBiome(x + xo, z + zo).hasSnow() && yy >= MIN_HEIGHT && yy < MAX_HEIGHT && lc.getBrightness(LightLayer.Block, x, yy, z) < 10;
                if (cold) {
                    final int below = lc.getTile(x, yy - 1, z);
                    final int current = lc.getTile(x, yy, z);
                    boolean shouldSnow = current == 0 && Tile.topSnow.mayPlace(this, x + xo, yy, z + zo) && below != 0 && below != Tile.ice.id && Tile.tiles[below].material.blocksMotion();
                    if (this.isRaining() && shouldSnow) {
                        this.setTile(x + xo, yy, z + zo, Tile.topSnow.id);
                    }
                    if (below == Tile.calmWater.id && lc.getData(x, yy - 1, z) == 0) {
                        this.setTile(x + xo, yy - 1, z + zo, Tile.ice.id);
                    }
                }
            }

            for (int i = 0; i < CHUNK_TILE_TICK_COUNT; ++i) {
                this.randValue = this.randValue * 3 + this.addend;
                final int val = this.randValue >> 2;
                final int x = val & 0xF;
                final int z = val >> 8 & 0xF;
                final int y = val >> 16 & 0x7F;
                final int id = lc.blocks[x << 11 | z << 7 | y] & 0xFF;
                if (Tile.shouldTick[id]) {
                    Tile.tiles[id].tick(this, x + xo, y, z + zo, this.random);
                }
            }
        }
    }
    
    public boolean tickPendingTicks(final boolean force) {
        int count = this.tickNextTickList.size();
        if (count != this.tickNextTickSet.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        }
        if (count > Level.MAX_TICK_TILES_PER_TICK) count = MAX_TICK_TILES_PER_TICK;

        for (int i = 0; i < count; ++i) {
            final TickNextTickData td = this.tickNextTickList.first();
            if (!force && td.delay > this.levelData.getTime()) {
                break;
            }

            this.tickNextTickList.remove(td);
            this.tickNextTickSet.remove(td);
            final int r = 8;
            if (this.hasChunksAt(td.x - r, td.y - r, td.z - r, td.x + r, td.y + r, td.z + r)) {
                final int id = this.getTile(td.x, td.y, td.z);
                if (id == td.tileId && id > 0) {
                    Tile.tiles[id].tick(this, td.x, td.y, td.z, this.random);
                }
            }
        }
        return !this.tickNextTickList.isEmpty();
    }
    
    public void animateTick(final int xt, final int yt, final int zt) {
        final int ticksPerChunk = 16;
        final Random animateRandom = new Random();
        for (int i = 0; i < MAX_TICK_TILES_PER_TICK; i++) {
            final int x = xt + this.random.nextInt(ticksPerChunk) - this.random.nextInt(ticksPerChunk);
            final int y = yt + this.random.nextInt(ticksPerChunk) - this.random.nextInt(ticksPerChunk);
            final int z = zt + this.random.nextInt(ticksPerChunk) - this.random.nextInt(ticksPerChunk);
            final int t = this.getTile(x, y, z);
            if (t > 0) {
                Tile.tiles[t].animateTick(this, x, y, z, animateRandom);
            }
        }
    }
    
    public List<Entity> getEntities(final Entity except, final AABB bb) {
        this.es.clear();
        final int xc0 = Mth.floor((bb.x0 - 2.0) / 16.0);
        final int xc1 = Mth.floor((bb.x1 + 2.0) / 16.0);
        final int zc0 = Mth.floor((bb.z0 - 2.0) / 16.0);
        final int zc1 = Mth.floor((bb.z1 + 2.0) / 16.0);

        for (int xc = xc0; xc <= xc1; ++xc) {
            for (int zc = zc0; zc <= zc1; ++zc) {
                if (this.hasChunk(xc, zc)) {
                    this.getChunk(xc, zc).getEntities(except, bb, this.es);
                }
            }
        }
        return this.es;
    }
    
    public <T extends Entity> List<T> getEntitiesOfClass(final Class<T> baseClass, final AABB bb) {
        final int xc0 = Mth.floor((bb.x0 - 2.0) / 16.0);
        final int xc1 = Mth.floor((bb.x1 + 2.0) / 16.0);
        final int zc0 = Mth.floor((bb.z0 - 2.0) / 16.0);
        final int zc1 = Mth.floor((bb.z1 + 2.0) / 16.0);
        final List<T> es = new ArrayList<>();
        for (int xc = xc0; xc <= xc1; ++xc) {
            for (int zc = zc0; zc <= zc1; ++zc) {
                if (this.hasChunk(xc, zc)) {
                    this.getChunk(xc, zc).getEntitiesOfClass(baseClass, bb, es);
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
            this.listeners.get(i).tileEntityChanged(x, y, z, te);
        }
    }
    
    public int countInstanceOf(final Class<?> clas) {
        int count = 0;
        for (int i = 0; i < this.entities.size(); ++i) {
            if (clas.isAssignableFrom(this.entities.get(i).getClass())) count++;
        }
        return count;
    }
    
    public void addEntities(final List<Entity> list) {
        this.entities.addAll(list);
        for (int i = 0; i < list.size(); ++i) {
            this.entityAdded(list.get(i));
        }
    }
    
    public void removeEntities(final List<Entity> list) {
        this.entitiesToRemove.addAll(list);
    }
    
    public void prepare() {
        while (this.chunkSource.tick()) {}
    }
    
    public boolean mayPlace(final int tileId, final int x, final int y, final int z, final boolean ignoreEntities, final int face) {
        int targetType = this.getTile(x, y, z);
        Tile targetTile = Tile.tiles[targetType];

        final Tile tile = Tile.tiles[tileId];

        AABB aabb = tile.getAABB(this, x, y, z);
        if (ignoreEntities) aabb = null;
        if (aabb != null && !this.isUnobstructed(aabb)) return false;
        if (targetTile == Tile.water || targetTile == Tile.calmWater || targetTile == Tile.lava || targetTile == Tile.calmLava || targetTile == Tile.fire || targetTile == Tile.topSnow) targetTile = null;
        if (tileId > 0 && targetTile == null) {
            if (tile.mayPlace(this, x, y, z, face)) {
                return true;
            }
        }
        return false;
    }
    // Useless - Exists in b1.2 and LCE leaks
    public int getSeaLevel() {
        return SEA_LEVEL;
    }
    
    public Path findPath(final Entity from, final Entity to, final float maxDist) {
        final int x = Mth.floor(from.x);
        final int y = Mth.floor(from.y);
        final int z = Mth.floor(from.z);

        final int r = (int)(maxDist + 16.0f);
        int x1 = x - r;
        int y1 = y - r;
        int z1 = z - r;
        int x2 = x + r;
        int y2 = y + r;
        int z2 = z + r;
        Region region = new Region(this, x1, y1, z1, x2, y2, z2);
        Path path = new PathFinder(region).findPath(from, to, maxDist);
        return path;
    }
    
    public Path findPath(final Entity from, final int xBest, final int yBest, final int zBest, final float maxDist) {
        final int x = Mth.floor(from.x);
        final int y = Mth.floor(from.y);
        final int z = Mth.floor(from.z);

        final int r = (int)(maxDist + 8.0f);
        int x1 = x - r;
        int y1 = y - r;
        int z1 = z - r;
        int x2 = x + r;
        int y2 = y + r;
        int z2 = z + r;
        Region region = new Region(this, x1, y1, z1, x2, y2, z2);
        Path path = new PathFinder(region).findPath(from, xBest, yBest, zBest, maxDist);
        return path;
    }
    
    public boolean getDirectSignal(final int x, final int y, final int z, final int dir) {
        final int t = this.getTile(x, y, z);
        if (t == 0) return false;
        return Tile.tiles[t].getDirectSignal(this, x, y, z, dir);
    }
    
    public boolean hasDirectSignal(final int x, final int y, final int z) {
        if (this.getDirectSignal(x, y - 1, z, 0)) return true;
        if (this.getDirectSignal(x, y + 1, z, 1)) return true;
        if (this.getDirectSignal(x, y, z - 1, 2)) return true;
        if (this.getDirectSignal(x, y, z + 1, 3)) return true;
        if (this.getDirectSignal(x - 1, y, z, 4)) return true;
        if (this.getDirectSignal(x + 1, y, z, 5)) return true;
        return false;
    }
    
    public boolean getSignal(final int x, final int y, final int z, final int dir) {
        if (this.isSolidBlockingTile(x, y, z)) {
            return this.hasDirectSignal(x, y, z);
        }
        final int t = this.getTile(x, y, z);
        if (t == 0) return false;
        return Tile.tiles[t].getSignal(this, x, y, z, dir);
    }
    
    public boolean hasNeighborSignal(final int x, final int y, final int z) {
        if (this.getSignal(x, y - 1, z, 0)) return true;
        if (this.getSignal(x, y + 1, z, 1)) return true;
        if (this.getSignal(x, y, z - 1, 2)) return true;
        if (this.getSignal(x, y, z + 1, 3)) return true;
        if (this.getSignal(x - 1, y, z, 4)) return true;
        if (this.getSignal(x + 1, y, z, 5)) return true;
        return false;
    }
    
    public Player getNearestPlayer(final Entity source, final double maxDist) {
        return this.getNearestPlayer(source.x, source.y, source.z, maxDist);
    }
    
    public Player getNearestPlayer(final double x, final double y, final double z, final double maxDist) {
        double best = -1.0;
        Player result = null;
        for (int i = 0; i < this.players.size(); ++i) {
            final Player p = this.players.get(i);
            final double dist = p.distanceToSqr(x, y, z);
            if ((maxDist < 0.0 || dist < maxDist * maxDist) && (best == -1.0 || dist < best)) {
                best = dist;
                result = p;
            }
        }
        return result;
    }
    
    public Player getPlayerByName(final String name) {
        for (int i = 0; i < this.players.size(); ++i) {
            if (name.equals(this.players.get(i).name)) {
                return this.players.get(i);
            }
        }
        return null;
    }

    public byte[] getBlocksAndData(final int x, final int y, final int z, final int xs, final int ys, final int zs) {
        final byte[] result = new byte[xs * ys * zs * 5 / 2];
        final int xc0 = x >> 4;
        final int zc0 = z >> 4;
        final int xc1 = x + xs - 1 >> 4;
        final int zc1 = z + zs - 1 >> 4;

        int p = 0;

        int y0 = y;
        int y1 = y + ys;
        if (y0 < MIN_HEIGHT) y0 = MIN_HEIGHT;
        if (y1 > MAX_HEIGHT) y1 = MAX_HEIGHT;
        for (int xc = xc0; xc <= xc1; ++xc) {
            int x0 = x - xc * 16;
            int x1 = x + xs - xc * 16;
            if (x0 < 0) x0 = 0;
            if (x1 > 16) x1 = 16;
            for (int zc = zc0; zc <= zc1; ++zc) {
                int z0 = z - zc * 16;
                int z1 = z + zs - zc * 16;
                if (z0 < 0) z0 = 0;
                if (z1 > 16) z1 = 16;
                p = this.getChunk(xc, zc).getBlocksAndData(result, x0, y0, z0, x1, y1, z1, p);
            }
        }
        return result;
    }
    
    public void setBlocksAndData(final int x, final int y, final int z, final int xs, final int ys, final int za, final byte[] data) {
        final int xc0 = x >> 4;
        final int zc0 = z >> 4;
        final int xc1 = x + xs - 1 >> 4;
        final int zc1 = z + za - 1 >> 4;

        int p = 0;

        int y0 = y;
        int y1 = y + ys;
        if (y0 < MIN_HEIGHT) y0 = MIN_HEIGHT;
        if (y1 > MAX_HEIGHT) y1 = MAX_HEIGHT;
        for (int xc = xc0; xc <= xc1; ++xc) {
            int x0 = x - xc * 16;
            int x1 = x + xs - xc * 16;
            if (x0 < 0) x0 = 0;
            if (x1 > 16) x1 = 16;
            for (int zc = zc0; zc <= zc1; ++zc) {
                int z0 = z - zc * 16;
                int z1 = z + za - zc * 16;
                if (z0 < 0) z0 = 0;
                if (z1 > 16) z1 = 16;
                LevelChunk lc = this.getChunk(xc, zc);
                p = lc.setBlocksAndData(data, x0, y0, z0, x1, y1, z1, p);
                this.setTilesDirty(xc * 16 + x0, y0, zc * 16 + z0, xc * 16 + x1, y1, zc * 16 + z1);
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

    public void setTimeAndAdjustTileTicks(final long newTime) {
        final long delta = newTime - this.levelData.getTime();
        for (final TickNextTickData tickNextTickData : this.tickNextTickSet) {
            tickNextTickData.delay += delta;
        }
        this.setTime(newTime);
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
        for (Player player : this.players) {
            if (!player.isSleeping()) {
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
            for (Player player : this.players) {
                if (!player.isSleepingLongEnough()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public float getThunderLevel(final float a) {
        return (this.oThunderLevel + (this.thunderLevel - this.oThunderLevel) * a) * this.getRainLevel(a);
    }
    
    public float getRainLevel(final float a) {
        return this.oRainLevel + (this.rainLevel - this.oRainLevel) * a;
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
        if (this.getTopRainBlock(x, z) > y) {
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
            this.listeners.get(i).levelEvent(source, type, x, y, z, data);
        }
    }

}
