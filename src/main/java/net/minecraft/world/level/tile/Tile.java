// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.item.TileItem;
import net.minecraft.world.item.PistonTileItem;
import net.minecraft.world.item.LeavesTileItem;
import net.minecraft.world.item.SaplingTileItem;
import net.minecraft.world.item.StoneSlabTileItem;
import net.minecraft.world.item.TreeTileItem;
import net.minecraft.world.item.ClothTileItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.tile.entity.SignTileEntity;
import net.minecraft.locale.language.I18n;
import net.minecraft.world.entity.Mob;
import net.minecraft.stats.Stats;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.player.Player;
import java.util.Random;
import java.util.ArrayList;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;

public class Tile
{
    public static final int TILE_NUM_COUNT = 256;
    public static final int TILE_NUM_MASK = 0xff; // 256 - 1
    public static final int TILE_NUM_SHIFT = 8; // 256 is 8 bits
    private static final String TILE_DESCRIPTION_PREFIX = "tile.";
    public static final float INDESTRUCTIBLE_DESTROY_TIME = -1.0f;
    public static final SoundType SOUND_NORMAL = new SoundType("stone", 1.0f, 1.0f);
    public static final SoundType SOUND_WOOD = new SoundType("wood", 1.0f, 1.0f);
    public static final SoundType SOUND_GRAVEL = new SoundType("gravel", 1.0f, 1.0f);
    public static final SoundType SOUND_GRASS = new SoundType("grass", 1.0f, 1.0f);
    public static final SoundType SOUND_STONE = new SoundType("stone", 1.0f, 1.0f);
    public static final SoundType SOUND_METAL = new SoundType("stone", 1.0f, 1.5f);
    public static final SoundType SOUND_GLASS = new SoundType("stone", 1.0f, 1.0f) {
        @Override
        public String getBreakSound() {
            return "random.glass";
        }
    };
    public static final SoundType SOUND_CLOTH = new SoundType("cloth", 1.0f, 1.0f);
    public static final SoundType SOUND_SAND = new SoundType("sand", 1.0f, 1.0f) {
        @Override
        public String getBreakSound() {
            return "step.gravel";
        }
    };

    public static final int SHAPE_INVISIBLE = -1;
    public static final int SHAPE_BLOCK = 0;
    public static final int SHAPE_CROSS_TEXTURE = 1;
    public static final int SHAPE_TORCH = 2;
    public static final int SHAPE_FIRE = 3;
    public static final int SHAPE_WATER = 4;
    public static final int SHAPE_RED_DUST = 5;
    public static final int SHAPE_ROWS = 6;
    public static final int SHAPE_DOOR = 7;
    public static final int SHAPE_LADDER = 8;
    public static final int SHAPE_RAIL = 9;
    public static final int SHAPE_STAIRS = 10;
    public static final int SHAPE_FENCE = 11;
    public static final int SHAPE_LEVER = 12;
    public static final int SHAPE_CACTUS = 13;
    public static final int SHAPE_BED = 14;
    public static final int SHAPE_DIODE = 15;
    public static final int SHAPE_PISTON_BASE = 16;
    public static final int SHAPE_PISTON_EXTENSION = 17;

    public static final Tile[] tiles = new Tile[TILE_NUM_COUNT];
    public static final boolean[] shouldTick = new boolean[TILE_NUM_COUNT];
    public static final boolean[] solid = new boolean[TILE_NUM_COUNT];
    public static final boolean[] isEntityTile = new boolean[TILE_NUM_COUNT];
    public static final int[] lightBlock = new int[TILE_NUM_COUNT];
    public static final boolean[] transculent = new boolean[TILE_NUM_COUNT];
    public static final int[] lightEmission = new int[TILE_NUM_COUNT];
    public static final boolean[] sendTileData = new boolean[TILE_NUM_COUNT];

    public static final Tile rock = new StoneTile(1, 1).setDestroyTime(1.5f).setExplodeable(10.0f).setSoundType(Tile.SOUND_STONE).setDescriptionId("stone");
    public static final GrassTile grass = (GrassTile)new GrassTile(2).setDestroyTime(0.6f).setSoundType(Tile.SOUND_GRASS).setDescriptionId("grass");
    public static final Tile dirt = new DirtTile(3, 2).setDestroyTime(0.5f).setSoundType(Tile.SOUND_GRAVEL).setDescriptionId("dirt");
    public static final Tile stoneBrick = new Tile(4, 16, Material.stone).setDestroyTime(2.0f).setExplodeable(10.0f).setSoundType(Tile.SOUND_STONE).setDescriptionId("stonebrick");
    public static final Tile wood = new Tile(5, 4, Material.wood).setDestroyTime(2.0f).setExplodeable(5.0f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("wood").sendTileData();
    public static final Tile sapling = new Sapling(6, 15).setDestroyTime(0.0f).setSoundType(Tile.SOUND_GRASS).setDescriptionId("sapling").sendTileData();
    public static final Tile unbreakable = new Tile(7, 17, Material.stone).setIndestructible().setExplodeable(6000000.0f).setSoundType(Tile.SOUND_STONE).setDescriptionId("bedrock").setNotCollectStatistics();
    public static final Tile water = new LiquidTileDynamic(8, Material.water).setDestroyTime(100.0f).setLightBlock(3).setDescriptionId("water").setNotCollectStatistics().sendTileData();
    public static final Tile calmWater = new LiquidTileStatic(9, Material.water).setDestroyTime(100.0f).setLightBlock(3).setDescriptionId("water").setNotCollectStatistics().sendTileData();
    public static final Tile lava = new LiquidTileDynamic(10, Material.lava).setDestroyTime(0.0f).setLightEmission(1.0f).setLightBlock(255).setDescriptionId("lava").setNotCollectStatistics().sendTileData();
    public static final Tile calmLava = new LiquidTileStatic(11, Material.lava).setDestroyTime(100.0f).setLightEmission(1.0f).setLightBlock(255).setDescriptionId("lava").setNotCollectStatistics().sendTileData();
    public static final Tile sand = new SandTile(12, 18).setDestroyTime(0.5f).setSoundType(Tile.SOUND_SAND).setDescriptionId("sand");
    public static final Tile gravel = new GravelTile(13, 19).setDestroyTime(0.6f).setSoundType(Tile.SOUND_GRAVEL).setDescriptionId("gravel");
    public static final Tile goldOre = new OreTile(14, 32).setDestroyTime(3.0f).setExplodeable(5.0f).setSoundType(Tile.SOUND_STONE).setDescriptionId("oreGold");
    public static final Tile ironOre = new OreTile(15, 33).setDestroyTime(3.0f).setExplodeable(5.0f).setSoundType(Tile.SOUND_STONE).setDescriptionId("oreIron");
    public static final Tile coalOre = new OreTile(16, 34).setDestroyTime(3.0f).setExplodeable(5.0f).setSoundType(Tile.SOUND_STONE).setDescriptionId("oreCoal");
    public static final Tile treeTrunk = new TreeTile(17).setDestroyTime(2.0f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("log").sendTileData();
    public static final LeafTile leaves = (LeafTile)new LeafTile(18, 52).setDestroyTime(0.2f).setLightBlock(1).setSoundType(Tile.SOUND_GRASS).setDescriptionId("leaves").setNotCollectStatistics().sendTileData();
    public static final Tile sponge = new Sponge(19).setDestroyTime(0.6f).setSoundType(Tile.SOUND_GRASS).setDescriptionId("sponge");
    public static final Tile glass = new GlassTile(20, 49, Material.glass, false).setDestroyTime(0.3f).setSoundType(Tile.SOUND_GLASS).setDescriptionId("glass");
    public static final Tile lapisOre = new OreTile(21, 160).setDestroyTime(3.0f).setExplodeable(5.0f).setSoundType(Tile.SOUND_STONE).setDescriptionId("oreLapis");
    public static final Tile lapisBlock = new Tile(22, 144, Material.stone).setDestroyTime(3.0f).setExplodeable(5.0f).setSoundType(Tile.SOUND_STONE).setDescriptionId("blockLapis");
    public static final Tile dispenser = new DispenserTile(23).setDestroyTime(3.5f).setSoundType(Tile.SOUND_STONE).setDescriptionId("dispenser").sendTileData();
    public static final Tile sandStone = new SandStoneTile(24).setSoundType(Tile.SOUND_STONE).setDestroyTime(0.8f).setDescriptionId("sandStone");
    public static final Tile musicBlock = new MusicTile(25).setDestroyTime(0.8f).setDescriptionId("musicBlock").sendTileData();
    public static final Tile bed = new BedTile(26).setDestroyTime(0.2f).setDescriptionId("bed").setNotCollectStatistics().sendTileData();
    public static final Tile goldenRail = new RailTile(27, 179, true).setDestroyTime(0.7f).setSoundType(Tile.SOUND_METAL).setDescriptionId("goldenRail").sendTileData();
    public static final Tile detectorRail = new DetectorRailTile(28, 195).setDestroyTime(0.7f).setSoundType(Tile.SOUND_METAL).setDescriptionId("detectorRail").sendTileData();
    public static final Tile pistonStickyBase = new PistonBaseTile(29, 106, true).setDescriptionId("pistonStickyBase").sendTileData();
    public static final Tile web = new WebTile(30, 11).setLightBlock(1).setDestroyTime(4.0f).setDescriptionId("web");
    public static final TallGrass tallgrass = (TallGrass)new TallGrass(31, 39).setDestroyTime(0.0f).setSoundType(Tile.SOUND_GRASS).setDescriptionId("tallgrass");
    public static final DeadBushTile deadBush = (DeadBushTile)new DeadBushTile(32, 55).setDestroyTime(0.0f).setSoundType(Tile.SOUND_GRASS).setDescriptionId("deadbush");
    public static final Tile pistonBase = new PistonBaseTile(33, 107, false).setDescriptionId("pistonBase").sendTileData();
    public static final PistonExtensionTile pistonExtension = (PistonExtensionTile)new PistonExtensionTile(34, 107).sendTileData();
    public static final Tile cloth = new ClothTile().setDestroyTime(0.8f).setSoundType(Tile.SOUND_CLOTH).setDescriptionId("cloth").sendTileData();
    public static final PistonMovingPiece pistonMovingPiece = new PistonMovingPiece(36);
    public static final Bush flower = (Bush)new Bush(37, 13).setDestroyTime(0.0f).setSoundType(Tile.SOUND_GRASS).setDescriptionId("flower");
    public static final Bush rose = (Bush)new Bush(38, 12).setDestroyTime(0.0f).setSoundType(Tile.SOUND_GRASS).setDescriptionId("rose");
    public static final Bush mushroom1 = (Bush)new Mushroom(39, 29).setDestroyTime(0.0f).setSoundType(Tile.SOUND_GRASS).setLightEmission(0.125f).setDescriptionId("mushroom");
    public static final Bush mushroom2 = (Bush)new Mushroom(40, 28).setDestroyTime(0.0f).setSoundType(Tile.SOUND_GRASS).setDescriptionId("mushroom");
    public static final Tile goldBlock = new MetalTile(41, 23).setDestroyTime(3.0f).setExplodeable(10.0f).setSoundType(Tile.SOUND_METAL).setDescriptionId("blockGold");
    public static final Tile ironBlock = new MetalTile(42, 22).setDestroyTime(5.0f).setExplodeable(10.0f).setSoundType(Tile.SOUND_METAL).setDescriptionId("blockIron");
    public static final Tile stoneSlab = new StoneSlabTile(43, true).setDestroyTime(2.0f).setExplodeable(10.0f).setSoundType(Tile.SOUND_STONE).setDescriptionId("stoneSlab");
    public static final Tile stoneSlabHalf = new StoneSlabTile(44, false).setDestroyTime(2.0f).setExplodeable(10.0f).setSoundType(Tile.SOUND_STONE).setDescriptionId("stoneSlab");
    public static final Tile redBrick = new Tile(45, 7, Material.stone).setDestroyTime(2.0f).setExplodeable(10.0f).setSoundType(Tile.SOUND_STONE).setDescriptionId("brick");
    public static final Tile tnt = new TntTile(46, 8).setDestroyTime(0.0f).setSoundType(Tile.SOUND_GRASS).setDescriptionId("tnt");
    public static final Tile bookshelf = new BookshelfTile(47, 35).setDestroyTime(1.5f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("bookshelf");
    public static final Tile mossStone = new Tile(48, 36, Material.stone).setDestroyTime(2.0f).setExplodeable(10.0f).setSoundType(Tile.SOUND_STONE).setDescriptionId("stoneMoss");
    public static final Tile obsidian = new ObsidianTile(49, 37).setDestroyTime(10.0f).setExplodeable(2000.0f).setSoundType(Tile.SOUND_STONE).setDescriptionId("obsidian");
    public static final Tile torch = new TorchTile(50, 80).setDestroyTime(0.0f).setLightEmission(0.9375f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("torch").sendTileData();
    public static final FireTile fire = (FireTile)new FireTile(51, 31).setDestroyTime(0.0f).setLightEmission(1.0f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("fire").setNotCollectStatistics().sendTileData();
    public static final Tile mobSpawner = new MobSpawnerTile(52, 65).setDestroyTime(5.0f).setSoundType(Tile.SOUND_METAL).setDescriptionId("mobSpawner").setNotCollectStatistics();
    public static final Tile stairs_wood = new StairTile(53, Tile.wood).setDescriptionId("stairsWood").sendTileData();
    public static final Tile chest = new ChestTile(54).setDestroyTime(2.5f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("chest").sendTileData();
    public static final Tile redStoneDust = new RedStoneDustTile(55, 164).setDestroyTime(0.0f).setSoundType(Tile.SOUND_NORMAL).setDescriptionId("redstoneDust").setNotCollectStatistics().sendTileData();
    public static final Tile emeraldOre = new OreTile(56, 50).setDestroyTime(3.0f).setExplodeable(5.0f).setSoundType(Tile.SOUND_STONE).setDescriptionId("oreDiamond");
    public static final Tile emeraldBlock = new MetalTile(57, 24).setDestroyTime(5.0f).setExplodeable(10.0f).setSoundType(Tile.SOUND_METAL).setDescriptionId("blockDiamond");
    public static final Tile workBench = new WorkbenchTile(58).setDestroyTime(2.5f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("workbench");
    public static final Tile crops = new CropTile(59, 88).setDestroyTime(0.0f).setSoundType(Tile.SOUND_GRASS).setDescriptionId("crops").setNotCollectStatistics().sendTileData();
    public static final Tile farmland = new FarmTile(60).setDestroyTime(0.6f).setSoundType(Tile.SOUND_GRAVEL).setDescriptionId("farmland");
    public static final Tile furnace = new FurnaceTile(61, false).setDestroyTime(3.5f).setSoundType(Tile.SOUND_STONE).setDescriptionId("furnace").sendTileData();
    public static final Tile furnace_lit = new FurnaceTile(62, true).setDestroyTime(3.5f).setSoundType(Tile.SOUND_STONE).setLightEmission(0.875f).setDescriptionId("furnace").sendTileData();
    public static final Tile sign = new SignTile(63, SignTileEntity.class, true).setDestroyTime(1.0f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("sign").setNotCollectStatistics().sendTileData();
    public static final Tile door_wood = new DoorTile(64, Material.wood).setDestroyTime(3.0f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("doorWood").setNotCollectStatistics().sendTileData();
    public static final Tile ladder = new LadderTile(65, 83).setDestroyTime(0.4f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("ladder").sendTileData();
    public static final Tile rail = new RailTile(66, 128, false).setDestroyTime(0.7f).setSoundType(Tile.SOUND_METAL).setDescriptionId("rail").sendTileData();
    public static final Tile stairs_stone = new StairTile(67, Tile.stoneBrick).setDescriptionId("stairsStone").sendTileData();
    public static final Tile wallSign = new SignTile(68, SignTileEntity.class, false).setDestroyTime(1.0f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("sign").setNotCollectStatistics().sendTileData();
    public static final Tile lever = new LeverTile(69, 96).setDestroyTime(0.5f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("lever").sendTileData();
    public static final Tile pressurePlate_stone = new PressurePlateTile(70, Tile.rock.tex, PressurePlateTile.Sensitivity.mobs, Material.stone).setDestroyTime(0.5f).setSoundType(Tile.SOUND_STONE).setDescriptionId("pressurePlate").sendTileData();
    public static final Tile door_iron = new DoorTile(71, Material.metal).setDestroyTime(5.0f).setSoundType(Tile.SOUND_METAL).setDescriptionId("doorIron").setNotCollectStatistics().sendTileData();
    public static final Tile pressurePlate_wood = new PressurePlateTile(72, Tile.wood.tex, PressurePlateTile.Sensitivity.everything, Material.wood).setDestroyTime(0.5f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("pressurePlate").sendTileData();
    public static final Tile redStoneOre = new RedStoneOreTile(73, 51, false).setDestroyTime(3.0f).setExplodeable(5.0f).setSoundType(Tile.SOUND_STONE).setDescriptionId("oreRedstone").sendTileData();
    public static final Tile redStoneOre_lit = new RedStoneOreTile(74, 51, true).setLightEmission(0.625f).setDestroyTime(3.0f).setExplodeable(5.0f).setSoundType(Tile.SOUND_STONE).setDescriptionId("oreRedstone").sendTileData();
    public static final Tile notGate_off = new NotGateTile(75, 115, false).setDestroyTime(0.0f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("notGate").sendTileData();
    public static final Tile notGate_on = new NotGateTile(76, 99, true).setDestroyTime(0.0f).setLightEmission(0.5f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("notGate").sendTileData();
    public static final Tile button = new ButtonTile(77, Tile.rock.tex).setDestroyTime(0.5f).setSoundType(Tile.SOUND_STONE).setDescriptionId("button").sendTileData();
    public static final Tile topSnow = new TopSnowTile(78, 66).setDestroyTime(0.1f).setSoundType(Tile.SOUND_CLOTH).setDescriptionId("snow");
    public static final Tile ice = new IceTile(79, 67).setDestroyTime(0.5f).setLightBlock(3).setSoundType(Tile.SOUND_GLASS).setDescriptionId("ice");
    public static final Tile snow = new SnowTile(80, 66).setDestroyTime(0.2f).setSoundType(Tile.SOUND_CLOTH).setDescriptionId("snow");
    public static final Tile cactus = new CactusTile(81, 70).setDestroyTime(0.4f).setSoundType(Tile.SOUND_CLOTH).setDescriptionId("cactus");
    public static final Tile clay = new ClayTile(82, 72).setDestroyTime(0.6f).setSoundType(Tile.SOUND_GRAVEL).setDescriptionId("clay");
    public static final Tile reeds = new ReedTile(83, 73).setDestroyTime(0.0f).setSoundType(Tile.SOUND_GRASS).setDescriptionId("reeds").setNotCollectStatistics();
    public static final Tile recordPlayer = new RecordPlayerTile(84, 74).setDestroyTime(2.0f).setExplodeable(10.0f).setSoundType(Tile.SOUND_STONE).setDescriptionId("jukebox").sendTileData();
    public static final Tile fence = new FenceTile(85, 4).setDestroyTime(2.0f).setExplodeable(5.0f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("fence").sendTileData();
    public static final Tile pumpkin = new PumpkinTile(86, 102, false).setDestroyTime(1.0f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("pumpkin").sendTileData();
    public static final Tile hellRock = new HellStoneTile(87, 103).setDestroyTime(0.4f).setSoundType(Tile.SOUND_STONE).setDescriptionId("hellrock");
    public static final Tile hellSand = new HellSandTile(88, 104).setDestroyTime(0.5f).setSoundType(Tile.SOUND_SAND).setDescriptionId("hellsand");
    public static final Tile lightGem = new LightGemTile(89, 105, Material.stone).setDestroyTime(0.3f).setSoundType(Tile.SOUND_GLASS).setLightEmission(1.0f).setDescriptionId("lightgem");
    public static final PortalTile portalTile = (PortalTile)new PortalTile(90, 14).setDestroyTime(-1.0f).setSoundType(Tile.SOUND_GLASS).setLightEmission(0.75f).setDescriptionId("portal");
    public static final Tile litPumpkin = new PumpkinTile(91, 102, true).setDestroyTime(1.0f).setSoundType(Tile.SOUND_WOOD).setLightEmission(1.0f).setDescriptionId("litpumpkin").sendTileData();
    public static final Tile cake = new CakeTile(92, 121).setDestroyTime(0.5f).setSoundType(Tile.SOUND_CLOTH).setDescriptionId("cake").setNotCollectStatistics().sendTileData();
    public static final Tile diode_off = new DiodeTile(93, false).setDestroyTime(0.0f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("diode").setNotCollectStatistics().sendTileData();
    public static final Tile diode_on = new DiodeTile(94, true).setDestroyTime(0.0f).setLightEmission(0.625f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("diode").setNotCollectStatistics().sendTileData();
    public static final Tile aprilFoolsJoke = new LockedChestTile(95).setDestroyTime(0.0f).setLightEmission(1.0f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("lockedchest").setTicking(true).sendTileData();
    public static final Tile trapdoor = new TrapDoorTile(96, Material.wood).setDestroyTime(3.0f).setSoundType(Tile.SOUND_WOOD).setDescriptionId("trapdoor").setNotCollectStatistics().sendTileData();
    public int tex;
    public final int id;
    protected float destroySpeed;
    protected float explosionResistance;
    protected boolean isInventoryItem;
    protected boolean collectStatistics;
    public double xx0;
    public double yy0;
    public double zz0;
    public double xx1;
    public double yy1;
    public double zz1;
    public SoundType soundType;
    public float gravity;
    public final Material material;
    public float friction;
    private String descriptionId;
    
    protected Tile(final int id, final Material material) {
        this.isInventoryItem = true;
        this.collectStatistics = true;
        this.soundType = Tile.SOUND_NORMAL;
        this.gravity = 1.0f;
        this.friction = 0.6f;
        if (Tile.tiles[id] != null) {
            throw new IllegalArgumentException("Slot " + id + " is already occupied by " + Tile.tiles[id] + " when adding " + this);
        }
        this.material = material;
        Tile.tiles[id] = this;
        this.id = id;
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        Tile.solid[id] = this.isSolidRender();
        Tile.lightBlock[id] = (this.isSolidRender() ? 255 : 0);
        Tile.transculent[id] = !material.blocksLight();
        Tile.isEntityTile[id] = false;
    }
    
    protected Tile sendTileData() {
        Tile.sendTileData[this.id] = true;
        return this;
    }
    
    protected void init() {
    }
    
    protected Tile(final int id, final int tex, final Material material) {
        this(id, material);
        this.tex = tex;
    }
    
    protected Tile setSoundType(final SoundType soundType) {
        this.soundType = soundType;
        return this;
    }
    
    protected Tile setLightBlock(final int i) {
        Tile.lightBlock[this.id] = i;
        return this;
    }
    
    protected Tile setLightEmission(final float f) {
        Tile.lightEmission[this.id] = (int)(15.0f * f);
        return this;
    }
    
    protected Tile setExplodeable(final float explosionResistance) {
        this.explosionResistance = explosionResistance * 3.0f;
        return this;
    }
    
    public boolean isCubeShaped() {
        return true;
    }
    
    public int getRenderShape() {
        return 0;
    }
    
    protected Tile setDestroyTime(final float destroySpeed) {
        this.destroySpeed = destroySpeed;
        if (this.explosionResistance < destroySpeed * 5.0f) {
            this.explosionResistance = destroySpeed * 5.0f;
        }
        return this;
    }
    
    protected Tile setIndestructible() {
        this.setDestroyTime(-1.0f);
        return this;
    }
    
    public float getDestroySpeed() {
        return this.destroySpeed;
    }
    
    protected Tile setTicking(final boolean tick) {
        Tile.shouldTick[this.id] = tick;
        return this;
    }
    
    public void setShape(final float x0, final float y0, final float z0, final float x1, final float y1, final float z1) {
        this.xx0 = x0;
        this.yy0 = y0;
        this.zz0 = z0;
        this.xx1 = x1;
        this.yy1 = y1;
        this.zz1 = z1;
    }
    
    public float getBrightness(final LevelSource level, final int x, final int y, final int z) {
        return level.getBrightness(x, y, z, Tile.lightEmission[this.id]);
    }
    
    public boolean isFaceVisible(final LevelSource level, final int x, final int y, final int z, final int f) {
        return (f == 0 && this.yy0 > 0.0) || (f == 1 && this.yy1 < 1.0) || (f == 2 && this.zz0 > 0.0) || (f == 3 && this.zz1 < 1.0) || (f == 4 && this.xx0 > 0.0) || (f == 5 && this.xx1 < 1.0) || !level.isSolidTile(x, y, z);
    }
    
    public boolean isSolidFace(final LevelSource level, final int x, final int y, final int z, final int face) {
        return level.getMaterial(x, y, z).isSolid();
    }
    
    public int getTexture(final LevelSource level, final int x, final int y, final int z, final int face) {
        return this.getTexture(face, level.getData(x, y, z));
    }
    
    public int getTexture(final int face, final int data) {
        return this.getTexture(face);
    }
    
    public int getTexture(final int face) {
        return this.tex;
    }
    
    public AABB getTileAABB(final Level level, final int x, final int y, final int z) {
        return AABB.newTemp(x + this.xx0, y + this.yy0, z + this.zz0, x + this.xx1, y + this.yy1, z + this.zz1);
    }
    
    public void addAABBs(final Level level, final int x, final int y, final int z, final AABB box, final ArrayList boxes) {
        final AABB aabb = this.getAABB(level, x, y, z);
        if (aabb != null && box.intersects(aabb)) {
            boxes.add(aabb);
        }
    }
    
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        return AABB.newTemp(x + this.xx0, y + this.yy0, z + this.zz0, x + this.xx1, y + this.yy1, z + this.zz1);
    }
    
    public boolean isSolidRender() {
        return true;
    }
    
    public boolean mayPick(final int data, final boolean liquid) {
        return this.mayPick();
    }
    
    public boolean mayPick() {
        return true;
    }
    
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
    }
    
    public void animateTick(final Level level, final int x, final int y, final int z, final Random random) {
    }
    
    public void destroy(final Level level, final int x, final int y, final int z, final int data) {
    }
    
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
    }
    
    public int getTickDelay() {
        return 10;
    }
    
    public void onPlace(final Level level, final int x, final int y, final int z) {
    }
    
    public void onRemove(final Level level, final int x, final int y, final int z) {
    }
    
    public int getResourceCount(final Random random) {
        return 1;
    }
    
    public int getResource(final int data, final Random random) {
        return this.id;
    }
    
    public float getDestroyProgress(final Player player) {
        if (this.destroySpeed < 0.0f) {
            return 0.0f;
        }
        if (!player.canDestroy(this)) {
            return 1.0f / this.destroySpeed / 100.0f;
        }
        return player.getDestroySpeed(this) / this.destroySpeed / 30.0f;
    }
    
    public final void spawnResources(final Level level, final int x, final int y, final int z, final int data) {
        this.spawnResources(level, x, y, z, data, 1.0f);
    }
    
    public void spawnResources(final Level level, final int x, final int y, final int z, final int data, final float odds) {
        if (level.isClientSide) {
            return;
        }
        for (int resourceCount = this.getResourceCount(level.random), i = 0; i < resourceCount; ++i) {
            if (level.random.nextFloat() <= odds) {
                final int resource = this.getResource(data, level.random);
                if (resource > 0) {
                    this.popResource(level, x, y, z, new ItemInstance(resource, 1, this.getSpawnResourcesAuxValue(data)));
                }
            }
        }
    }
    
    protected void popResource(final Level level, final int x, final int y, final int z, final ItemInstance itemInstance) {
        if (level.isClientSide) {
            return;
        }
        final float n = 0.7f;
        final ItemEntity e = new ItemEntity(level, x + (level.random.nextFloat() * n + (1.0f - n) * 0.5), y + (level.random.nextFloat() * n + (1.0f - n) * 0.5), z + (level.random.nextFloat() * n + (1.0f - n) * 0.5), itemInstance);
        e.throwTime = 10;
        level.addEntity(e);
    }
    
    protected int getSpawnResourcesAuxValue(final int data) {
        return 0;
    }
    
    public float getExplosionResistance(final Entity source) {
        return this.explosionResistance / 5.0f;
    }
    
    public HitResult clip(final Level level, final int xt, final int yt, final int zt, Vec3 a, Vec3 b) {
        this.updateShape(level, xt, yt, zt);
        a = a.add(-xt, -yt, -zt);
        b = b.add(-xt, -yt, -zt);
        Vec3 clipX = a.clipX(b, this.xx0);
        Vec3 clipX2 = a.clipX(b, this.xx1);
        Vec3 clipY = a.clipY(b, this.yy0);
        Vec3 clipY2 = a.clipY(b, this.yy1);
        Vec3 clipZ = a.clipZ(b, this.zz0);
        Vec3 clipZ2 = a.clipZ(b, this.zz1);
        if (!this.containsX(clipX)) {
            clipX = null;
        }
        if (!this.containsX(clipX2)) {
            clipX2 = null;
        }
        if (!this.containsY(clipY)) {
            clipY = null;
        }
        if (!this.containsY(clipY2)) {
            clipY2 = null;
        }
        if (!this.containsZ(clipZ)) {
            clipZ = null;
        }
        if (!this.containsZ(clipZ2)) {
            clipZ2 = null;
        }
        Vec3 vec3 = null;
        if (clipX != null && (vec3 == null || a.distanceTo(clipX) < a.distanceTo(vec3))) {
            vec3 = clipX;
        }
        if (clipX2 != null && (vec3 == null || a.distanceTo(clipX2) < a.distanceTo(vec3))) {
            vec3 = clipX2;
        }
        if (clipY != null && (vec3 == null || a.distanceTo(clipY) < a.distanceTo(vec3))) {
            vec3 = clipY;
        }
        if (clipY2 != null && (vec3 == null || a.distanceTo(clipY2) < a.distanceTo(vec3))) {
            vec3 = clipY2;
        }
        if (clipZ != null && (vec3 == null || a.distanceTo(clipZ) < a.distanceTo(vec3))) {
            vec3 = clipZ;
        }
        if (clipZ2 != null && (vec3 == null || a.distanceTo(clipZ2) < a.distanceTo(vec3))) {
            vec3 = clipZ2;
        }
        if (vec3 == null) {
            return null;
        }
        int f = -1;
        if (vec3 == clipX) {
            f = 4;
        }
        if (vec3 == clipX2) {
            f = 5;
        }
        if (vec3 == clipY) {
            f = 0;
        }
        if (vec3 == clipY2) {
            f = 1;
        }
        if (vec3 == clipZ) {
            f = 2;
        }
        if (vec3 == clipZ2) {
            f = 3;
        }
        return new HitResult(xt, yt, zt, f, vec3.add(xt, yt, zt));
    }
    
    private boolean containsX(final Vec3 v) {
        return v != null && v.y >= this.yy0 && v.y <= this.yy1 && v.z >= this.zz0 && v.z <= this.zz1;
    }
    
    private boolean containsY(final Vec3 v) {
        return v != null && v.x >= this.xx0 && v.x <= this.xx1 && v.z >= this.zz0 && v.z <= this.zz1;
    }
    
    private boolean containsZ(final Vec3 v) {
        return v != null && v.x >= this.xx0 && v.x <= this.xx1 && v.y >= this.yy0 && v.y <= this.yy1;
    }
    
    public void wasExploded(final Level level, final int x, final int y, final int z) {
    }
    
    public int getRenderLayer() {
        return 0;
    }
    
    public boolean mayPlace(final Level level, final int x, final int y, final int z, final int face) {
        return this.mayPlace(level, x, y, z);
    }
    
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        final int tile = level.getTile(x, y, z);
        return tile == 0 || Tile.tiles[tile].material.isReplaceable();
    }
    
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        return false;
    }
    
    public void stepOn(final Level level, final int x, final int y, final int z, final Entity entity) {
    }
    
    public void setPlacedOnFace(final Level level, final int x, final int y, final int z, final int face) {
    }
    
    public void attack(final Level level, final int x, final int y, final int z, final Player player) {
    }
    
    public void handleEntityInside(final Level level, final int x, final int y, final int z, final Entity e, final Vec3 current) {
    }
    
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
    }
    
    public int getColor(final int auxData) {
        return 16777215;
    }
    
    public int getColor(final LevelSource level, final int x, final int y, final int z) {
        return 16777215;
    }
    
    public boolean getSignal(final LevelSource level, final int x, final int y, final int z, final int dir) {
        return false;
    }
    
    public boolean isSignalSource() {
        return false;
    }
    
    public void entityInside(final Level level, final int x, final int y, final int z, final Entity entity) {
    }
    
    public boolean getDirectSignal(final Level level, final int x, final int y, final int z, final int dir) {
        return false;
    }
    
    public void updateDefaultShape() {
    }
    
    public void playerDestroy(final Level level, final Player player, final int x, final int y, final int z, final int data) {
        player.awardStat(Stats.blockMined[this.id], 1);
        this.spawnResources(level, x, y, z, data);
    }
    
    public boolean canSurvive(final Level level, final int x, final int y, final int z) {
        return true;
    }
    
    public void setPlacedBy(final Level level, final int x, final int y, final int z, final Mob by) {
    }
    
    public Tile setDescriptionId(final String id) {
        this.descriptionId = TILE_DESCRIPTION_PREFIX + id;
        return this;
    }
    
    public String getName() {
        return I18n.get(this.getDescriptionId() + ".name");
    }
    
    public String getDescriptionId() {
        return this.descriptionId;
    }
    
    public void triggerEvent(final Level level, final int x, final int y, final int z, final int b0, final int b1) {
    }
    
    public boolean isCollectStatistics() {
        return this.collectStatistics;
    }
    
    protected Tile setNotCollectStatistics() {
        this.collectStatistics = false;
        return this;
    }
    
    public int getPistonPushReaction() {
        return this.material.getPushReaction();
    }
    
    static {
        Item.items[Tile.cloth.id] = new ClothTileItem(Tile.cloth.id - Tile.TILE_NUM_COUNT).setDescriptionId("cloth");
        Item.items[Tile.treeTrunk.id] = new TreeTileItem(Tile.treeTrunk.id - Tile.TILE_NUM_COUNT).setDescriptionId("log");
        Item.items[Tile.stoneSlabHalf.id] = new StoneSlabTileItem(Tile.stoneSlabHalf.id - Tile.TILE_NUM_COUNT).setDescriptionId("stoneSlab");
        Item.items[Tile.sapling.id] = new SaplingTileItem(Tile.sapling.id - Tile.TILE_NUM_COUNT).setDescriptionId("sapling");
        Item.items[Tile.leaves.id] = new LeavesTileItem(Tile.leaves.id - Tile.TILE_NUM_COUNT).setDescriptionId("leaves");
        Item.items[Tile.pistonBase.id] = new PistonTileItem(Tile.pistonBase.id - Tile.TILE_NUM_COUNT);
        Item.items[Tile.pistonStickyBase.id] = new PistonTileItem(Tile.pistonStickyBase.id - Tile.TILE_NUM_COUNT);
        for (int i = 0; i < Tile.TILE_NUM_COUNT; ++i) {
            if (Tile.tiles[i] != null && Item.items[i] == null) {
                Item.items[i] = new TileItem(i - Tile.TILE_NUM_COUNT);
                Tile.tiles[i].init();
            }
        }
        Tile.transculent[0] = true;
        Stats.buildBlockStats();
    }

    public static class SoundType
    {
        public final String name;
        public final float volume;
        public final float pitch;

        public SoundType(final String name, final float volume, final float pitch) {
            this.name = name;
            this.volume = volume;
            this.pitch = pitch;
        }

        public float getVolume() {
            return this.volume;
        }

        public float getPitch() {
            return this.pitch;
        }

        public String getBreakSound() {
            return "step." + this.name;
        }

        public String getStepSound() {
            return "step." + this.name;
        }
    }
}
