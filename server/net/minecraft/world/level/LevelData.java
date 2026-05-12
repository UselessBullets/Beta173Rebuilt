// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import java.util.List;
import com.mojang.nbt.CompoundTag;

public class LevelData
{
    private long seed;
    private int xSpawn;
    private int ySpawn;
    private int zSpawn;
    private long time;
    private long lastPlayed;
    private long sizeOnDisk;
    private CompoundTag loadedPlayerTag;
    private int dimension;
    private String levelName;
    private int version;
    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    
    public LevelData(final CompoundTag tag) {
        this.seed = tag.getLong("RandomSeed");
        this.xSpawn = tag.getInt("SpawnX");
        this.ySpawn = tag.getInt("SpawnY");
        this.zSpawn = tag.getInt("SpawnZ");
        this.time = tag.getLong("Time");
        this.lastPlayed = tag.getLong("LastPlayed");
        this.sizeOnDisk = tag.getLong("SizeOnDisk");
        this.levelName = tag.getString("LevelName");
        this.version = tag.getInt("version");
        this.rainTime = tag.getInt("rainTime");
        this.raining = tag.getBoolean("raining");
        this.thunderTime = tag.getInt("thunderTime");
        this.thundering = tag.getBoolean("thundering");
        if (tag.contains("Player")) {
            this.loadedPlayerTag = tag.getCompound("Player");
            this.dimension = this.loadedPlayerTag.getInt("Dimension");
        }
    }
    
    public LevelData(final long seed, final String levelName) {
        this.seed = seed;
        this.levelName = levelName;
    }
    
    public LevelData(final LevelData copy) {
        this.seed = copy.seed;
        this.xSpawn = copy.xSpawn;
        this.ySpawn = copy.ySpawn;
        this.zSpawn = copy.zSpawn;
        this.time = copy.time;
        this.lastPlayed = copy.lastPlayed;
        this.sizeOnDisk = copy.sizeOnDisk;
        this.loadedPlayerTag = copy.loadedPlayerTag;
        this.dimension = copy.dimension;
        this.levelName = copy.levelName;
        this.version = copy.version;
        this.rainTime = copy.rainTime;
        this.raining = copy.raining;
        this.thunderTime = copy.thunderTime;
        this.thundering = copy.thundering;
    }
    
    public CompoundTag createTag() {
        final CompoundTag tag = new CompoundTag();
        this.setTagData(tag, this.loadedPlayerTag);
        return tag;
    }
    
    public CompoundTag createTag(final List players) {
        final CompoundTag tag = new CompoundTag();
        Entity entity = null;
        CompoundTag compoundTag = null;
        if (players.size() > 0) {
            entity = players.get(0);
        }
        if (entity != null) {
            compoundTag = new CompoundTag();
            entity.saveWithoutId(compoundTag);
        }
        this.setTagData(tag, compoundTag);
        return tag;
    }
    
    private void setTagData(final CompoundTag tag, final CompoundTag playerTag) {
        tag.putLong("RandomSeed", this.seed);
        tag.putInt("SpawnX", this.xSpawn);
        tag.putInt("SpawnY", this.ySpawn);
        tag.putInt("SpawnZ", this.zSpawn);
        tag.putLong("Time", this.time);
        tag.putLong("SizeOnDisk", this.sizeOnDisk);
        tag.putLong("LastPlayed", System.currentTimeMillis());
        tag.putString("LevelName", this.levelName);
        tag.putInt("version", this.version);
        tag.putInt("rainTime", this.rainTime);
        tag.putBoolean("raining", this.raining);
        tag.putInt("thunderTime", this.thunderTime);
        tag.putBoolean("thundering", this.thundering);
        if (playerTag != null) {
            tag.putCompound("Player", playerTag);
        }
    }
    
    public long getSeed() {
        return this.seed;
    }
    
    public int getXSpawn() {
        return this.xSpawn;
    }
    
    public int getYSpawn() {
        return this.ySpawn;
    }
    
    public int getZSpawn() {
        return this.zSpawn;
    }
    
    public long getTime() {
        return this.time;
    }
    
    public long getSizeOnDisk() {
        return this.sizeOnDisk;
    }
    
    public int getDimension() {
        return this.dimension;
    }
    
    public void setTime(final long time) {
        this.time = time;
    }
    
    public void setSizeOnDisk(final long sizeOnDisk) {
        this.sizeOnDisk = sizeOnDisk;
    }
    
    public void setSpawn(final int xSpawn, final int ySpawn, final int zSpawn) {
        this.xSpawn = xSpawn;
        this.ySpawn = ySpawn;
        this.zSpawn = zSpawn;
    }
    
    public void setLevelName(final String levelName) {
        this.levelName = levelName;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public void setVersion(final int version) {
        this.version = version;
    }
    
    public boolean isThundering() {
        return this.thundering;
    }
    
    public void setThundering(final boolean thundering) {
        this.thundering = thundering;
    }
    
    public int getThunderTime() {
        return this.thunderTime;
    }
    
    public void setThunderTime(final int thunderTime) {
        this.thunderTime = thunderTime;
    }
    
    public boolean isRaining() {
        return this.raining;
    }
    
    public void setRaining(final boolean raining) {
        this.raining = raining;
    }
    
    public int getRainTime() {
        return this.rainTime;
    }
    
    public void setRainTime(final int rainTime) {
        this.rainTime = rainTime;
    }
}
