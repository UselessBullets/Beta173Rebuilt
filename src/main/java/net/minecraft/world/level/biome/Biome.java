// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.biome;

import net.minecraft.world.entity.Mob;
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
import net.minecraft.world.entity.monster.Spider;
import java.util.ArrayList;
import net.minecraft.world.level.tile.Tile;
import java.util.List;

public class Biome
{
    public static final Biome rainForest = new RainforestBiome().setColor(0x8fa36).setName("Rainforest").setLeafColor(0x1ff458);
    public static final Biome swampland = new SwampBiome().setColor(0x7f9b2).setName("Swampland").setLeafColor(0x8baf48);
    public static final Biome seasonalForest = new Biome().setColor(0x9be023).setName("Seasonal Forest");
    public static final Biome forest = new ForestBiome().setColor(0x56621).setName("Forest").setLeafColor(0x4eba31);
    public static final Biome savanna = new FlatBiome().setColor(0xd9e023).setName("Savanna");
    public static final Biome shrubland = new Biome().setColor(0xa1ad20).setName("Shrubland");
    public static final Biome taiga = new TaigaBiome().setColor(0x2eb153).setName("Taiga").setSnowCovered().setLeafColor(0x7bb731);
    public static final Biome desert = new FlatBiome().setColor(0xfa9418).setName("Desert").setNoRain();
    public static final Biome plains = new FlatBiome().setColor(0xffd910).setName("Plains");
    public static final Biome iceDesert = new FlatBiome().setColor(0xffed93).setName("Ice Desert").setSnowCovered().setNoRain().setLeafColor(0xc4d339);
    public static final Biome tunfra = new Biome().setColor(0x57ebf9).setName("Tundra").setSnowCovered().setLeafColor(0xc4d339);
    public static final Biome hell = new HellBiome().setColor(0xff0000).setName("Hell").setNoRain();
    public static final Biome sky = new SkyBiome().setColor(0x8080ff).setName("Sky").setNoRain();
    public String name;
    public int color;
    public byte topMaterial = (byte)Tile.grass.id;
    public byte material = (byte)Tile.dirt.id;
    public int leafColor = 0x4ee031;
    protected List<MobSpawnerData> enemies = new ArrayList<>();
    protected List<MobSpawnerData> friendlies = new ArrayList<>();
    protected List<MobSpawnerData> waterFriendlies = new ArrayList<>();
    private boolean snowCovered;
    private boolean hasRain = true;
    // Useless - Theoretical constant that likely existed, the biome map has a fixed precision that it samples temperature and downfall values at

    private static final int BIOME_MAP_RESOLUTION = 64;
    private static Biome[] map = new Biome[BIOME_MAP_RESOLUTION * BIOME_MAP_RESOLUTION];
    static {
        recalc();
    }

    protected Biome() {
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
        for (int t = 0; t < BIOME_MAP_RESOLUTION; ++t) {
            for (int d = 0; d < BIOME_MAP_RESOLUTION; ++d) {
                Biome.map[t + d * BIOME_MAP_RESOLUTION] = _getBiome(t / (BIOME_MAP_RESOLUTION - 1f), d / (BIOME_MAP_RESOLUTION - 1f));
            }
        }

        Biome.desert.topMaterial = Biome.desert.material = (byte)Tile.sand.id;
        Biome.iceDesert.topMaterial = Biome.iceDesert.material = (byte)Tile.sand.id;
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
        return Biome.map[(int)(temp * (BIOME_MAP_RESOLUTION - 1.0)) + (int)(downfall * (BIOME_MAP_RESOLUTION - 1.0)) * BIOME_MAP_RESOLUTION];
    }

    public static Biome _getBiome(final float temp, float downfall) {
        downfall *= temp;
        if (temp < 0.1f) return Biome.tunfra;
        if (downfall < 0.2f) {
            if (temp < 0.5f) return Biome.tunfra;
            if (temp < 0.95f) return Biome.savanna;
            return Biome.desert;
        }
        else {
            if (downfall > 0.5f && temp < 0.7f) return Biome.swampland;
            if (temp < 0.5f) return Biome.taiga;
            if (temp < 0.97f) {
                if (downfall < 0.35f) return Biome.shrubland;
                return Biome.forest;
            }
            else {
                if (downfall < 0.45f) return Biome.plains;
                if (downfall < 0.9f) return Biome.seasonalForest;
                return Biome.rainForest;
            }
        }
    }

    public int getSkyColor(float temp) {
        temp /= 3.0f;
        if (temp < -1.0f) temp = -1.0f;
        if (temp > 1.0f) temp = 1.0f;
        return Color.getHSBColor(224 / 360.0f - temp * 0.05f, 0.5f + temp * 0.1f, 1.0f).getRGB();
    }

    public List<MobSpawnerData> getMobs(final MobCategory category) {
        if (category == MobCategory.monster) return this.enemies;
        if (category == MobCategory.creature) return this.friendlies;
        if (category == MobCategory.waterCreature) return this.waterFriendlies;
        return null;
    }

    public boolean hasSnow() {
        return this.snowCovered;
    }

    public boolean hasRain() {
        return !this.snowCovered && this.hasRain;
    }

    public static class MobSpawnerData
    {
        public Class<? extends Mob> mobClass;
        public int probabilityWeight;

        public MobSpawnerData(final Class<? extends Mob> mobClass, final int probabilityWeight) {
            this.mobClass = mobClass;
            this.probabilityWeight = probabilityWeight;
        }
    }
}
