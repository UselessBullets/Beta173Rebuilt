// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.Minecart;
import net.minecraft.world.level.material.Material;
import net.minecraft.locale.language.I18n;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import java.util.Random;

public class Item
{
    public static final int ITEM_NUM_COUNT = 32000;
    protected static Random random = new Random();
    private static final int MAX_STACK_SIZE = Container.LARGE_MAX_STACK_SIZE;
    public static Item[] items = new Item[ITEM_NUM_COUNT];
    public static Item shovel_iron = new ShovelItem(0, Tier.IRON).setIcon(2, 5).setDescriptionId("shovelIron");
    public static Item pickAxe_iron = new PickaxeItem(1, Tier.IRON).setIcon(2, 6).setDescriptionId("pickaxeIron");
    public static Item hatchet_iron = new HatchetItem(2, Tier.IRON).setIcon(2, 7).setDescriptionId("hatchetIron");
    public static Item flintAndSteel = new FlintAndSteelItem(3).setIcon(5, 0).setDescriptionId("flintAndSteel");
    public static Item apple = new FoodItem(4, 4, false).setIcon(10, 0).setDescriptionId("apple");
    public static Item bow = new BowItem(5).setIcon(5, 1).setDescriptionId("bow");
    public static Item arrow = new Item(6).setIcon(5, 2).setDescriptionId("arrow");
    public static Item coal = new CoalItem(7).setIcon(7, 0).setDescriptionId("coal");
    public static Item emerald = new Item(8).setIcon(7, 3).setDescriptionId("emerald");
    public static Item ironIngot = new Item(9).setIcon(7, 1).setDescriptionId("ingotIron");
    public static Item goldIngot = new Item(10).setIcon(7, 2).setDescriptionId("ingotGold");
    public static Item sword_iron = new WeaponItem(11, Tier.IRON).setIcon(2, 4).setDescriptionId("swordIron");
    public static Item sword_wood = new WeaponItem(12, Tier.WOOD).setIcon(0, 4).setDescriptionId("swordWood");
    public static Item shovel_wood = new ShovelItem(13, Tier.WOOD).setIcon(0, 5).setDescriptionId("shovelWood");
    public static Item pickAxe_wood = new PickaxeItem(14, Tier.WOOD).setIcon(0, 6).setDescriptionId("pickaxeWood");
    public static Item hatchet_wood = new HatchetItem(15, Tier.WOOD).setIcon(0, 7).setDescriptionId("hatchetWood");
    public static Item sword_stone = new WeaponItem(16, Tier.STONE).setIcon(1, 4).setDescriptionId("swordStone");
    public static Item shovel_stone = new ShovelItem(17, Tier.STONE).setIcon(1, 5).setDescriptionId("shovelStone");
    public static Item pickAxe_stone = new PickaxeItem(18, Tier.STONE).setIcon(1, 6).setDescriptionId("pickaxeStone");
    public static Item hatchet_stone = new HatchetItem(19, Tier.STONE).setIcon(1, 7).setDescriptionId("hatchetStone");
    public static Item sword_emerald = new WeaponItem(20, Tier.EMERALD).setIcon(3, 4).setDescriptionId("swordDiamond");
    public static Item shovel_emerald = new ShovelItem(21, Tier.EMERALD).setIcon(3, 5).setDescriptionId("shovelDiamond");
    public static Item pickAxe_emerald = new PickaxeItem(22, Tier.EMERALD).setIcon(3, 6).setDescriptionId("pickaxeDiamond");
    public static Item hatchet_emerald = new HatchetItem(23, Tier.EMERALD).setIcon(3, 7).setDescriptionId("hatchetDiamond");
    public static Item stick = new Item(24).setIcon(5, 3).handEquipped().setDescriptionId("stick");
    public static Item bowl = new Item(25).setIcon(7, 4).setDescriptionId("bowl");
    public static Item mushroomStew = new BowlFoodItem(26, 10).setIcon(8, 4).setDescriptionId("mushroomStew");
    public static Item sword_gold = new WeaponItem(27, Tier.GOLD).setIcon(4, 4).setDescriptionId("swordGold");
    public static Item shovel_gold = new ShovelItem(28, Tier.GOLD).setIcon(4, 5).setDescriptionId("shovelGold");
    public static Item pickAxe_gold = new PickaxeItem(29, Tier.GOLD).setIcon(4, 6).setDescriptionId("pickaxeGold");
    public static Item hatchet_gold = new HatchetItem(30, Tier.GOLD).setIcon(4, 7).setDescriptionId("hatchetGold");
    public static Item string = new Item(31).setIcon(8, 0).setDescriptionId("string");
    public static Item feather = new Item(32).setIcon(8, 1).setDescriptionId("feather");
    public static Item sulphur = new Item(33).setIcon(8, 2).setDescriptionId("sulphur");
    public static Item hoe_wood = new HoeItem(34, Tier.WOOD).setIcon(0, 8).setDescriptionId("hoeWood");
    public static Item hoe_stone = new HoeItem(35, Tier.STONE).setIcon(1, 8).setDescriptionId("hoeStone");
    public static Item hoe_iron = new HoeItem(36, Tier.IRON).setIcon(2, 8).setDescriptionId("hoeIron");
    public static Item hoe_emerald = new HoeItem(37, Tier.EMERALD).setIcon(3, 8).setDescriptionId("hoeDiamond");
    public static Item hoe_gold = new HoeItem(38, Tier.GOLD).setIcon(4, 8).setDescriptionId("hoeGold");
    public static Item seeds = new SeedItem(39, Tile.crops.id).setIcon(9, 0).setDescriptionId("seeds");
    public static Item wheat = new Item(40).setIcon(9, 1).setDescriptionId("wheat");
    public static Item bread = new FoodItem(41, 5, false).setIcon(9, 2).setDescriptionId("bread");
    public static Item helmet_cloth = new ArmorItem(42, 0, 0, ArmorItem.SLOT_HEAD).setIcon(0, 0).setDescriptionId("helmetCloth");
    public static Item chestplate_cloth = new ArmorItem(43, 0, 0, ArmorItem.SLOT_TORSO).setIcon(0, 1).setDescriptionId("chestplateCloth");
    public static Item leggings_cloth = new ArmorItem(44, 0, 0, ArmorItem.SLOT_LEGS).setIcon(0, 2).setDescriptionId("leggingsCloth");
    public static Item boots_cloth = new ArmorItem(45, 0, 0, ArmorItem.SLOT_FEET).setIcon(0, 3).setDescriptionId("bootsCloth");
    public static Item helmet_chain = new ArmorItem(46, 1, 1, ArmorItem.SLOT_HEAD).setIcon(1, 0).setDescriptionId("helmetChain");
    public static Item chestplate_chain = new ArmorItem(47, 1, 1, ArmorItem.SLOT_TORSO).setIcon(1, 1).setDescriptionId("chestplateChain");
    public static Item leggings_chain = new ArmorItem(48, 1, 1, ArmorItem.SLOT_LEGS).setIcon(1, 2).setDescriptionId("leggingsChain");
    public static Item boots_chain = new ArmorItem(49, 1, 1, ArmorItem.SLOT_FEET).setIcon(1, 3).setDescriptionId("bootsChain");
    public static Item helmet_iron = new ArmorItem(50, 2, 2, ArmorItem.SLOT_HEAD).setIcon(2, 0).setDescriptionId("helmetIron");
    public static Item chestplate_iron = new ArmorItem(51, 2, 2, ArmorItem.SLOT_TORSO).setIcon(2, 1).setDescriptionId("chestplateIron");
    public static Item leggings_iron = new ArmorItem(52, 2, 2, ArmorItem.SLOT_LEGS).setIcon(2, 2).setDescriptionId("leggingsIron");
    public static Item boots_iron = new ArmorItem(53, 2, 2, ArmorItem.SLOT_FEET).setIcon(2, 3).setDescriptionId("bootsIron");
    public static Item helmet_emerald = new ArmorItem(54, 3, 3, ArmorItem.SLOT_HEAD).setIcon(3, 0).setDescriptionId("helmetDiamond");
    public static Item chestplate_emerald = new ArmorItem(55, 3, 3, ArmorItem.SLOT_TORSO).setIcon(3, 1).setDescriptionId("chestplateDiamond");
    public static Item leggings_emerald = new ArmorItem(56, 3, 3, ArmorItem.SLOT_LEGS).setIcon(3, 2).setDescriptionId("leggingsDiamond");
    public static Item boots_emerald = new ArmorItem(57, 3, 3, ArmorItem.SLOT_FEET).setIcon(3, 3).setDescriptionId("bootsDiamond");
    public static Item helmet_gold = new ArmorItem(58, 1, 4, ArmorItem.SLOT_HEAD).setIcon(4, 0).setDescriptionId("helmetGold");
    public static Item chestplate_gold = new ArmorItem(59, 1, 4, ArmorItem.SLOT_TORSO).setIcon(4, 1).setDescriptionId("chestplateGold");
    public static Item leggings_gold = new ArmorItem(60, 1, 4, ArmorItem.SLOT_LEGS).setIcon(4, 2).setDescriptionId("leggingsGold");
    public static Item boots_gold = new ArmorItem(61, 1, 4, ArmorItem.SLOT_FEET).setIcon(4, 3).setDescriptionId("bootsGold");
    public static Item flint = new Item(62).setIcon(6, 0).setDescriptionId("flint");
    public static Item porkChop_raw = new FoodItem(63, 3, true).setIcon(7, 5).setDescriptionId("porkchopRaw");
    public static Item porkChop_cooked = new FoodItem(64, 8, true).setIcon(8, 5).setDescriptionId("porkchopCooked");
    public static Item painting = new PaintingItem(65).setIcon(10, 1).setDescriptionId("painting");
    public static Item apple_gold = new FoodItem(66, 42, false).setIcon(11, 0).setDescriptionId("appleGold");
    public static Item sign = new SignItem(67).setIcon(10, 2).setDescriptionId("sign");
    public static Item door_wood = new DoorItem(68, Material.wood).setIcon(11, 2).setDescriptionId("doorWood");
    public static Item bucket_empty = new BucketItem(69, 0).setIcon(10, 4).setDescriptionId("bucket");
    public static Item bucket_water = new BucketItem(70, Tile.water.id).setIcon(11, 4).setDescriptionId("bucketWater").setCraftingRemainingItem(Item.bucket_empty);
    public static Item bucket_lava = new BucketItem(71, Tile.lava.id).setIcon(12, 4).setDescriptionId("bucketLava").setCraftingRemainingItem(Item.bucket_empty);
    public static Item minecart = new MinecartItem(72, Minecart.RIDEABLE).setIcon(7, 8).setDescriptionId("minecart");
    public static Item saddle = new SaddleItem(73).setIcon(8, 6).setDescriptionId("saddle");
    public static Item door_iron = new DoorItem(74, Material.metal).setIcon(12, 2).setDescriptionId("doorIron");
    public static Item redStone = new RedStoneItem(75).setIcon(8, 3).setDescriptionId("redstone");
    public static Item snowBall = new SnowballItem(76).setIcon(14, 0).setDescriptionId("snowball");
    public static Item boat = new BoatItem(77).setIcon(8, 8).setDescriptionId("boat");
    public static Item leather = new Item(78).setIcon(7, 6).setDescriptionId("leather");
    public static Item milk = new BucketItem(79, -1).setIcon(13, 4).setDescriptionId("milk").setCraftingRemainingItem(Item.bucket_empty);
    public static Item brick = new Item(80).setIcon(6, 1).setDescriptionId("brick");
    public static Item clay = new Item(81).setIcon(9, 3).setDescriptionId("clay");
    public static Item reeds = new TilePlanterItem(82, Tile.reeds).setIcon(11, 1).setDescriptionId("reeds");
    public static Item paper = new Item(83).setIcon(10, 3).setDescriptionId("paper");
    public static Item book = new Item(84).setIcon(11, 3).setDescriptionId("book");
    public static Item slimeBall = new Item(85).setIcon(14, 1).setDescriptionId("slimeball");
    public static Item minecart_chest = new MinecartItem(86, Minecart.CHEST).setIcon(7, 9).setDescriptionId("minecartChest");
    public static Item minecart_furnace = new MinecartItem(87, Minecart.FURNACE).setIcon(7, 10).setDescriptionId("minecartFurnace");
    public static Item egg = new EggItem(88).setIcon(12, 0).setDescriptionId("egg");
    public static Item compass = new Item(89).setIcon(6, 3).setDescriptionId("compass");
    public static Item fishingRod = new FishingRodItem(90).setIcon(5, 4).setDescriptionId("fishingRod");
    public static Item clock = new Item(91).setIcon(6, 4).setDescriptionId("clock");
    public static Item yellowDust = new Item(92).setIcon(9, 4).setDescriptionId("yellowDust");
    public static Item fish_raw = new FoodItem(93, 2, false).setIcon(9, 5).setDescriptionId("fishRaw");
    public static Item fish_cooked = new FoodItem(94, 5, false).setIcon(10, 5).setDescriptionId("fishCooked");
    public static Item dye_powder = new DyePowderItem(95).setIcon(14, 4).setDescriptionId("dyePowder");
    public static Item bone = new Item(96).setIcon(12, 1).setDescriptionId("bone").handEquipped();
    public static Item sugar = new Item(97).setIcon(13, 0).setDescriptionId("sugar").handEquipped();
    public static Item cake = new TilePlanterItem(98, Tile.cake).setMaxStackSize(1).setIcon(13, 1).setDescriptionId("cake");
    public static Item bed = new BedItem(99).setMaxStackSize(1).setIcon(13, 2).setDescriptionId("bed");
    public static Item diode = new TilePlanterItem(100, Tile.diode_off).setIcon(6, 5).setDescriptionId("diode");
    public static Item cookie = new StackableFoodItem(101, 1, false, 8).setIcon(12, 5).setDescriptionId("cookie");
    public static MapItem map = (MapItem)new MapItem(102).setIcon(12, 3).setDescriptionId("map");
    public static ShearsItem shears = (ShearsItem)new ShearsItem(103).setIcon(13, 5).setDescriptionId("shears");
    public static Item record_01 = new RecordingItem(2000, "13").setIcon(0, 15).setDescriptionId("record");
    public static Item record_02 = new RecordingItem(2001, "cat").setIcon(1, 15).setDescriptionId("record");
    public final int id;
    protected int maxStackSize = Item.MAX_STACK_SIZE;
    private int maxDamage = 0;
    protected int icon;
    protected boolean handEquipped = false;
    protected boolean isStackedByData = false;
    private Item craftingRemainingItem = null;
    private String descriptionId;
    
    protected Item(final int id) {
        this.id = Tile.TILE_NUM_COUNT + id;
        if (Item.items[Tile.TILE_NUM_COUNT + id] != null) System.out.println("CONFLICT @ " + id);
        Item.items[Tile.TILE_NUM_COUNT + id] = this;
    }
    
    public Item setIcon(final int icon) {
        this.icon = icon;
        return this;
    }
    
    public Item setMaxStackSize(final int maxStackSize) {
        this.maxStackSize = maxStackSize;
        return this;
    }
    
    public Item setIcon(final int x, final int y) {
        this.icon = x + y * 16;
        return this;
    }
    
    public int getIcon(final int auxValue) {
        return this.icon;
    }
    
    public final int getIcon(final ItemInstance itemInstance) {
        return this.getIcon(itemInstance.getAuxValue());
    }
    
    public boolean useOn(final ItemInstance itemInstance, final Player player, final Level level, final int x, final int y, final int z, final int face) {
        return false;
    }
    
    public float getDestroySpeed(final ItemInstance itemInstance, final Tile tile) {
        return 1.0f;
    }
    
    public ItemInstance use(final ItemInstance itemInstance, final Level level, final Player player) {
        return itemInstance;
    }
    
    public int getMaxStackSize() {
        return this.maxStackSize;
    }
    
    public int getLevelDataForAuxValue(final int auxValue) {
        return 0;
    }
    
    public boolean isStackedByData() {
        return this.isStackedByData;
    }
    
    protected Item setStackedByData(final boolean isStackedByData) {
        this.isStackedByData = isStackedByData;
        return this;
    }
    
    public int getMaxDamage() {
        return this.maxDamage;
    }
    
    protected Item setMaxDamage(final int maxDamage) {
        this.maxDamage = maxDamage;
        return this;
    }
    
    public boolean canBeDepleted() {
        return this.maxDamage > 0 && !this.isStackedByData;
    }


    /**
     * Returns true when the item was used to deal more than default damage
     *
     * @param itemInstance
     * @param mob
     * @param attacker
     * @return
     */
    public boolean hurtEnemy(final ItemInstance itemInstance, final Mob mob, final Mob attacker) {
        return false;
    }

    /**
     * Returns true when the item was used to mine more efficiently
     *
     * @param itemInstance
     * @param tile
     * @param x
     * @param y
     * @param z
     * @param owner
     * @return
     */
    public boolean mineBlock(final ItemInstance itemInstance, final int tile, final int x, final int y, final int z, final Mob owner) {
        return false;
    }
    
    public int getAttackDamage(final Entity entity) {
        return 1;
    }
    
    public boolean canDestroySpecial(final Tile tile) {
        return false;
    }
    
    public void interactEnemy(final ItemInstance itemInstance, final Mob mob) {
    }
    
    public Item handEquipped() {
        this.handEquipped = true;
        return this;
    }
    
    public boolean isHandEquipped() {
        return this.handEquipped;
    }
    
    public boolean isMirroredArt() {
        return false;
    }
    
    public Item setDescriptionId(final String id) {
        this.descriptionId = "item." + id;
        return this;
    }
    
    public String getDescriptionId() {
        return this.descriptionId;
    }
    
    public String getDescriptionId(final ItemInstance itemInstance) {
        return this.descriptionId;
    }
    
    public Item setCraftingRemainingItem(final Item craftingRemainingItem) {
        if (this.maxStackSize > 1) {
            throw new IllegalArgumentException("Max stack size must be 1 for items with crafting results");
        }
        this.craftingRemainingItem = craftingRemainingItem;
        return this;
    }
    
    public Item getCraftingRemainingItem() {
        return this.craftingRemainingItem;
    }
    
    public boolean hasCraftingRemainingItem() {
        return this.craftingRemainingItem != null;
    }
    
    public String getName() {
        return I18n.get(this.getDescriptionId() + ".name");
    }
    
    public int getColor(final int auxData) {
        return 0xffffff;
    }
    
    public void inventoryTick(final ItemInstance itemInstance, final Level level, final Entity owner, final int slot, final boolean selected) {
    }
    
    public void onCraftedBy(final ItemInstance itemInstance, final Level level, final Player player) {
    }

    public boolean isComplex() {
        return false;
    }
    
    static {
        Stats.buildItemStats();
    }

    public enum Tier
    {
        WOOD(0, 59, 2.0f, 0),
        STONE(1, 131, 4.0f, 1),
        IRON(2, 250, 6.0f, 2),
        EMERALD(3, 1561, 8.0f, 3),
        GOLD(0, 32, 12.0f, 0);

        private final int level;
        private final int uses;
        private final float speed;
        private final int damage;

        Tier(final int level, final int uses, final float speed, final int damage) {
            this.level = level;
            this.uses = uses;
            this.speed = speed;
            this.damage = damage;
        }

        public int getUses() {
            return this.uses;
        }

        public float getSpeed() {
            return this.speed;
        }

        public int getAttackDamageBonus() {
            return this.damage;
        }

        public int getLevel() {
            return this.level;
        }
    }
}
