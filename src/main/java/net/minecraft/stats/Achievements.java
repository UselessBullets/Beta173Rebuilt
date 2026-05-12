// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.item.Item;
import java.util.ArrayList;
import java.util.List;

public class Achievements
{
    protected static final int ACHIEVEMENT_OFFSET = 0x500000;
    public static final int ACHIEVEMENT_WIDTH_POSITION = 12;
    public static final int ACHIEVEMENT_HEIGHT_POSITION = 12;
    public static int xMin;
    public static int yMin;
    public static int xMax;
    public static int yMax;
    public static List<Achievement> achievements;
    public static Achievement openInventory;
    public static Achievement mineWood;
    public static Achievement buildWorkbench;
    public static Achievement buildPickaxe;
    public static Achievement buildFurnace;
    public static Achievement acquireIron;
    public static Achievement buildHoe;
    public static Achievement makeBread;
    public static Achievement bakeCake;
    public static Achievement buildBetterPickaxe;
    public static Achievement cookFish;
    public static Achievement onARail;
    public static Achievement buildSword;
    public static Achievement killEnemy;
    public static Achievement killCow;
    public static Achievement flyPig;
    
    public static void init() {
    }
    
    static {
        Achievements.achievements = new ArrayList<>();
        Achievements.openInventory = new Achievement(0, "openInventory", 0, 0, Item.book, null).setAwardLocallyOnly().postConstruct();
        Achievements.mineWood = new Achievement(1, "mineWood", 2, 1, Tile.treeTrunk, Achievements.openInventory).postConstruct();
        Achievements.buildWorkbench = new Achievement(2, "buildWorkBench", 4, -1, Tile.workBench, Achievements.mineWood).postConstruct();
        Achievements.buildPickaxe = new Achievement(3, "buildPickaxe", 4, 2, Item.pickAxe_wood, Achievements.buildWorkbench).postConstruct();
        Achievements.buildFurnace = new Achievement(4, "buildFurnace", 3, 4, Tile.furnace_lit, Achievements.buildPickaxe).postConstruct();
        Achievements.acquireIron = new Achievement(5, "acquireIron", 1, 4, Item.ironIngot, Achievements.buildFurnace).postConstruct();
        Achievements.buildHoe = new Achievement(6, "buildHoe", 2, -3, Item.hoe_wood, Achievements.buildWorkbench).postConstruct();
        Achievements.makeBread = new Achievement(7, "makeBread", -1, -3, Item.bread, Achievements.buildHoe).postConstruct();
        Achievements.bakeCake = new Achievement(8, "bakeCake", 0, -5, Item.cake, Achievements.buildHoe).postConstruct();
        Achievements.buildBetterPickaxe = new Achievement(9, "buildBetterPickaxe", 6, 2, Item.pickAxe_stone, Achievements.buildPickaxe).postConstruct();
        Achievements.cookFish = new Achievement(10, "cookFish", 2, 6, Item.fish_cooked, Achievements.buildFurnace).postConstruct();
        Achievements.onARail = new Achievement(11, "onARail", 2, 3, Tile.rail, Achievements.acquireIron).setGolden().postConstruct();
        Achievements.buildSword = new Achievement(12, "buildSword", 6, -1, Item.sword_wood, Achievements.buildWorkbench).postConstruct();
        Achievements.killEnemy = new Achievement(13, "killEnemy", 8, -1, Item.bone, Achievements.buildSword).postConstruct();
        Achievements.killCow = new Achievement(14, "killCow", 7, -3, Item.leather, Achievements.buildSword).postConstruct();
        Achievements.flyPig = new Achievement(15, "flyPig", 8, -4, Item.saddle, Achievements.killCow).setGolden().postConstruct();
        System.out.println(Achievements.achievements.size() + " achievements");
    }
}
