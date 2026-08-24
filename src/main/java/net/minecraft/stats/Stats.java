// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.locale.language.I18n;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.crafting.FurnaceRecipes;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Recipes;
import java.util.HashSet;
import net.minecraft.world.level.tile.Tile;
import java.util.List;
import java.util.Map;

public class Stats
{
    private static final int BLOCKS_MINED_OFFSET = 0x1000000;
    private static final int ITEMS_CRAFTED_OFFSET = 0x1010000;
    private static final int ITEMS_USED_OFFSET = 0x1020000;
    private static final int ITEMS_BROKEN_OFFSET = 0x1030000;
    protected static Map<Integer, Stat> statsById = new HashMap<>();
    public static List<Stat> all = new ArrayList<>();
    public static List<Stat> generalStats = new ArrayList<>();
    public static List<ItemStat> itemStats = new ArrayList<>();
    public static List<ItemStat> blocksStats = new ArrayList<>();
    public static Stat startGame = new GeneralStat(1000, I18n.get("stat.startGame")).setAwardLocallyOnly().postConstruct();
    public static Stat createWorld = new GeneralStat(1001, I18n.get("stat.createWorld")).setAwardLocallyOnly().postConstruct();
    public static Stat loadWorld = new GeneralStat(1002, I18n.get("stat.loadWorld")).setAwardLocallyOnly().postConstruct();
    public static Stat joinMultiplayer = new GeneralStat(1003, I18n.get("stat.joinMultiplayer")).setAwardLocallyOnly().postConstruct();
    public static Stat leaveGame = new GeneralStat(1004, I18n.get("stat.leaveGame")).setAwardLocallyOnly().postConstruct();
    public static Stat playOneMinute = new GeneralStat(1100, I18n.get("stat.playOneMinute"), Stat.timeFormat).setAwardLocallyOnly().postConstruct();
    public static Stat walkOneCm = new GeneralStat(2000, I18n.get("stat.walkOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
    public static Stat swimOneCm = new GeneralStat(2001, I18n.get("stat.swimOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
    public static Stat fallOneCm = new GeneralStat(2002, I18n.get("stat.fallOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
    public static Stat climbOneCm = new GeneralStat(2003, I18n.get("stat.climbOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
    public static Stat flyOneCm = new GeneralStat(2004, I18n.get("stat.flyOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
    public static Stat diveOneCm = new GeneralStat(2005, I18n.get("stat.diveOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
    public static Stat minecartOneCm = new GeneralStat(2006, I18n.get("stat.minecartOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
    public static Stat boatOneCm = new GeneralStat(2007, I18n.get("stat.boatOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
    public static Stat pigOneCm = new GeneralStat(2008, I18n.get("stat.pigOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
    public static Stat timesJumped = new GeneralStat(2010, I18n.get("stat.jump")).setAwardLocallyOnly().postConstruct();
    public static Stat itemsDropped = new GeneralStat(2011, I18n.get("stat.drop")).setAwardLocallyOnly().postConstruct();
    public static Stat damageDealt = new GeneralStat(2020, I18n.get("stat.damageDealt")).postConstruct();
    public static Stat damageTaken = new GeneralStat(2021, I18n.get("stat.damageTaken")).postConstruct();
    public static Stat deaths = new GeneralStat(2022, I18n.get("stat.deaths")).postConstruct();
    public static Stat mobKills = new GeneralStat(2023, I18n.get("stat.mobKills")).postConstruct();
    public static Stat playerKills = new GeneralStat(2024, I18n.get("stat.playerKills")).postConstruct();
    public static Stat fishCaught = new GeneralStat(2025, I18n.get("stat.fishCaught")).postConstruct();

    static {
        Achievements.init();
    }
    public static Stat[] blockMined = getMinedStats("stat.mineBlock", BLOCKS_MINED_OFFSET);
    public static Stat[] itemCrafted;
    public static Stat[] itemUsed;
    public static Stat[] itemBroke;
    private static boolean blockStatsLoaded = false;

    private static boolean itemStatsLoaded = false;

    public static void init() {
    }

    public static void buildBlockStats() {
        Stats.itemUsed = getUsedStats(Stats.itemUsed, "stat.useItem", ITEMS_USED_OFFSET, 0, Tile.tiles.length);
        Stats.itemBroke = getBreakStats(Stats.itemBroke, "stat.breakItem", ITEMS_BROKEN_OFFSET, 0, Tile.tiles.length);
        Stats.blockStatsLoaded = true;
        buildCraftableStats();
    }

    public static void buildItemStats() {
        Stats.itemUsed = getUsedStats(Stats.itemUsed, "stat.useItem", ITEMS_USED_OFFSET, Tile.tiles.length, 32000);
        Stats.itemBroke = getBreakStats(Stats.itemBroke, "stat.breakItem", ITEMS_BROKEN_OFFSET, Tile.tiles.length, 32000);
        Stats.itemStatsLoaded = true;
        buildCraftableStats();
    }

    public static void buildCraftableStats() {
        if (!Stats.blockStatsLoaded || !Stats.itemStatsLoaded) {
            return;
        }

        final HashSet<Integer> set = new HashSet<>();
        for (Recipe recipe : Recipes.getInstance().getRecipes()) {
            set.add(recipe.getResultItem().id);
        }

        for (ItemInstance itemInstance : FurnaceRecipes.getInstance().getRecipes().values()) {
            set.add(itemInstance.id);
        }

        Stats.itemCrafted = new Stat[32000];
        for (final Integer n : set) {
            if (Item.items[n] != null) {
                Stats.itemCrafted[n] = new ItemStat(ITEMS_CRAFTED_OFFSET + n, I18n.get("stat.craftItem", Item.items[n].getName()), n).postConstruct();
            }
        }
        remapIds(Stats.itemCrafted);
    }

    private static Stat[] getMinedStats(final String nameKey, final int idOff) {
        final Stat[] stats = new Stat[256];
        for (int i = 0; i < 256; ++i) {
            if (Tile.tiles[i] != null && Tile.tiles[i].isCollectStatistics()) {
                stats[i] = new ItemStat(idOff + i, I18n.get(nameKey, Tile.tiles[i].getName()), i).postConstruct();
                Stats.blocksStats.add((ItemStat) stats[i]);
            }
        }
        remapIds(stats);
        return stats;
    }

    private static Stat[] getUsedStats(Stat[] result, final String nameKey, final int idOff, final int start, final int end) {
        if (result == null) {
            result = new Stat[32000];
        }
        for (int i = start; i < end; ++i) {
            if (Item.items[i] != null) {
                result[i] = new ItemStat(idOff + i, I18n.get(nameKey, Item.items[i].getName()), i).postConstruct();
                if (i >= Tile.tiles.length) {
                    Stats.itemStats.add((ItemStat) result[i]);
                }
            }
        }
        remapIds(result);
        return result;
    }

    private static Stat[] getBreakStats(Stat[] result, final String nameKey, final int idOff, final int start, final int end) {
        if (result == null) {
            result = new Stat[32000];
        }
        for (int i = start; i < end; ++i) {
            if (Item.items[i] != null && Item.items[i].canBeDepleted()) {
                result[i] = new ItemStat(idOff + i, I18n.get(nameKey, Item.items[i].getName()), i).postConstruct();
            }
        }
        remapIds(result);
        return result;
    }

    private static void remapIds(final Stat[] stats) {
        remapId(stats, Tile.calmWater.id, Tile.water.id);
        remapId(stats, Tile.calmLava.id, Tile.calmLava.id);
        remapId(stats, Tile.litPumpkin.id, Tile.pumpkin.id);
        remapId(stats, Tile.furnace_lit.id, Tile.furnace.id);
        remapId(stats, Tile.redStoneOre_lit.id, Tile.redStoneOre.id);
        remapId(stats, Tile.diode_on.id, Tile.diode_off.id);
        remapId(stats, Tile.notGate_on.id, Tile.notGate_off.id);
        remapId(stats, Tile.mushroom2.id, Tile.mushroom1.id);
        remapId(stats, Tile.stoneSlab.id, Tile.stoneSlabHalf.id);
        remapId(stats, Tile.grass.id, Tile.dirt.id);
        remapId(stats, Tile.farmland.id, Tile.dirt.id);
    }

    private static void remapId(final Stat[] stats, final int id1, final int id2) {
        if (stats[id1] != null && stats[id2] == null) {
            stats[id2] = stats[id1];
            return;
        }
        Stats.all.remove(stats[id1]);
        Stats.blocksStats.remove(stats[id1]);
        Stats.generalStats.remove(stats[id1]);
        stats[id1] = stats[id2];
    }

    public static Stat getStat(final int statId) {
        return Stats.statsById.get(statId);
    }
}
