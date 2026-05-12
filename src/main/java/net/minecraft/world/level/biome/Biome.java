// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.biome;

import net.minecraft.world.entity.MobCategory;
import java.awt.Color;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.BasicTree;
import net.minecraft.world.level.levelgen.feature.Feature;
import java.util.Random;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.MobSpawnerData;
import net.minecraft.world.entity.monster.Spider;
import java.util.ArrayList;
import net.minecraft.world.level.tile.Tile;
import java.util.List;

public class Biome
{
    public static final Biome rainForest;
    public static final Biome swampland;
    public static final Biome seasonalForest;
    public static final Biome forest;
    public static final Biome savanna;
    public static final Biome shrubland;
    public static final Biome taiga;
    public static final Biome desert;
    public static final Biome plains;
    public static final Biome iceDesert;
    public static final Biome tunfra;
    public static final Biome hell;
    public static final Biome sky;
    public String name;
    public int color;
    public byte topMaterial;
    public byte material;
    public int leafColor;
    protected List enemies;
    protected List friendlies;
    protected List waterFriendlies;
    private boolean snowCovered;
    private boolean hasRain;
    private static Biome[] map;
    
    protected Biome() {
        this.topMaterial = (byte)Tile.grass.id;
        this.material = (byte)Tile.dirt.id;
        this.leafColor = 5169201;
        this.enemies = new ArrayList();
        this.friendlies = new ArrayList();
        this.waterFriendlies = new ArrayList();
        this.hasRain = true;
        this.enemies.add(new MobSpawnerData(Spider.class, 10));
        this.enemies.add(new MobSpawnerData(Zombie.class, 10));
        this.enemies.add(new MobSpawnerData(Skeleton.class, 10));
        this.enemies.add(new MobSpawnerData(Creeper.class, 10));
        this.enemies.add(new MobSpawnerData(Slime.class, 10));
        this.friendlies.add(new MobSpawnerData(Sheep.class, 12));
        this.friendlies.add(new MobSpawnerData(Pig.class, 10));
        this.friendlies.add(new MobSpawnerData(Chicken.class, 10));
        this.friendlies.add(new MobSpawnerData(Cow.class, 8));
        this.waterFriendlies.add(new MobSpawnerData(Squid.class, 10));
    }
    
    private Biome setNoRain() {
        this.hasRain = false;
        return this;
    }
    
    public static void recalc() {
        for (int i = 0; i < 64; ++i) {
            for (int j = 0; j < 64; ++j) {
                Biome.map[i + j * 64] = _getBiome(i / 63.0f, j / 63.0f);
            }
        }
        final Biome desert = Biome.desert;
        final Biome desert2 = Biome.desert;
        final byte b = (byte)Tile.sand.id;
        desert2.material = b;
        desert.topMaterial = b;
        final Biome iceDesert = Biome.iceDesert;
        final Biome iceDesert2 = Biome.iceDesert;
        final byte b2 = (byte)Tile.sand.id;
        iceDesert2.material = b2;
        iceDesert.topMaterial = b2;
    }
    
    public Feature getTreeFeature(final Random random) {
        if (random.nextInt(10) == 0) {
            return new BasicTree();
        }
        return new TreeFeature();
    }
    
    protected Biome setSnowCovered() {
        this.snowCovered = true;
        return this;
    }
    
    protected Biome setName(final String name) {
        this.name = name;
        return this;
    }
    
    protected Biome setLeafColor(final int leafColor) {
        this.leafColor = leafColor;
        return this;
    }
    
    protected Biome setColor(final int color) {
        this.color = color;
        return this;
    }
    
    public static Biome getBiome(final double temp, final double downfall) {
        return Biome.map[(int)(temp * 63.0) + (int)(downfall * 63.0) * 64];
    }
    
    public static Biome _getBiome(final float temp, float downfall) {
        downfall *= temp;
        if (temp < 0.1f) {
            return Biome.tunfra;
        }
        if (downfall < 0.2f) {
            if (temp < 0.5f) {
                return Biome.tunfra;
            }
            if (temp < 0.95f) {
                return Biome.savanna;
            }
            return Biome.desert;
        }
        else {
            if (downfall > 0.5f && temp < 0.7f) {
                return Biome.swampland;
            }
            if (temp < 0.5f) {
                return Biome.taiga;
            }
            if (temp < 0.97f) {
                if (downfall < 0.35f) {
                    return Biome.shrubland;
                }
                return Biome.forest;
            }
            else {
                if (downfall < 0.45f) {
                    return Biome.plains;
                }
                if (downfall < 0.9f) {
                    return Biome.seasonalForest;
                }
                return Biome.rainForest;
            }
        }
    }
    
    public int getSkyColor(float temp) {
        temp /= 3.0f;
        if (temp < -1.0f) {
            temp = -1.0f;
        }
        if (temp > 1.0f) {
            temp = 1.0f;
        }
        return Color.getHSBColor(0.62222224f - temp * 0.05f, 0.5f + temp * 0.1f, 1.0f).getRGB();
    }
    
    public List getMobs(final MobCategory category) {
        if (category == MobCategory.monster) {
            return this.enemies;
        }
        if (category == MobCategory.creature) {
            return this.friendlies;
        }
        if (category == MobCategory.waterCreature) {
            return this.waterFriendlies;
        }
        return null;
    }
    
    public boolean hasSnow() {
        return this.snowCovered;
    }
    
    public boolean hasRain() {
        return !this.snowCovered && this.hasRain;
    }
    
    static {
        rainForest = new RainforestBiome().setColor(588342).setName("Rainforest").setLeafColor(2094168);
        swampland = new SwampBiome().setColor(522674).setName("Swampland").setLeafColor(9154376);
        seasonalForest = new Biome().setColor(10215459).setName("Seasonal Forest");
        forest = new ForestBiome().setColor(353825).setName("Forest").setLeafColor(5159473);
        savanna = new FlatBiome().setColor(14278691).setName("Savanna");
        shrubland = new Biome().setColor(10595616).setName("Shrubland");
        taiga = new TaigaBiome().setColor(3060051).setName("Taiga").setSnowCovered().setLeafColor(8107825);
        desert = new FlatBiome().setColor(16421912).setName("Desert").setNoRain();
        plains = new FlatBiome().setColor(16767248).setName("Plains");
        iceDesert = new FlatBiome().setColor(16772499).setName("Ice Desert").setSnowCovered().setNoRain().setLeafColor(12899129);
        tunfra = new Biome().setColor(5762041).setName("Tundra").setSnowCovered().setLeafColor(12899129);
        hell = new HellBiome().setColor(16711680).setName("Hell").setNoRain();
        sky = new SkyBiome().setColor(8421631).setName("Sky").setNoRain();
        Biome.map = new Biome[4096];
        recalc();
    }
}
