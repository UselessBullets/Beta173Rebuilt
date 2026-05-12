// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    protected static Map statsById;
    public static List all;
    public static List generalStats;
    public static List blocksMinedStats;
    public static List itemsCraftedStats;
    public static Stat startGame;
    public static Stat createWorld;
    public static Stat loadWorld;
    public static Stat joinMultiplayer;
    public static Stat leaveGame;
    public static Stat playOneMinute;
    public static Stat walkOneCm;
    public static Stat swimOneCm;
    public static Stat fallOneCm;
    public static Stat climbOneCm;
    public static Stat flyOneCm;
    public static Stat diveOneCm;
    public static Stat minecartOneCm;
    public static Stat boatOneCm;
    public static Stat pigOneCm;
    public static Stat timesJumped;
    public static Stat itemsDropped;
    public static Stat damageDealt;
    public static Stat damageTaken;
    public static Stat deaths;
    public static Stat mobKills;
    public static Stat playerKills;
    public static Stat fishCaught;
    public static Stat[] blockMined;
    public static Stat[] itemCrafted;
    public static Stat[] itemUsed;
    public static Stat[] itemBroke;
    private static boolean blockStatsLoaded;
    private static boolean itemStatsLoaded;
    
    public static void init() {
    }
    
    public static void buildBlockStats() {
        Stats.itemUsed = getUsedStats(Stats.itemUsed, "stat.useItem", 16908288, 0, Tile.tiles.length);
        Stats.itemBroke = getBreakStats(Stats.itemBroke, "stat.breakItem", 16973824, 0, Tile.tiles.length);
        Stats.blockStatsLoaded = true;
        buildCraftableStats();
    }
    
    public static void buildItemStats() {
        Stats.itemUsed = getUsedStats(Stats.itemUsed, "stat.useItem", 16908288, Tile.tiles.length, 32000);
        Stats.itemBroke = getBreakStats(Stats.itemBroke, "stat.breakItem", 16973824, Tile.tiles.length, 32000);
        Stats.itemStatsLoaded = true;
        buildCraftableStats();
    }
    
    public static void buildCraftableStats() {
        if (!Stats.blockStatsLoaded || !Stats.itemStatsLoaded) {
            return;
        }
        final HashSet set = new HashSet();
        final Iterator iterator = Recipes.getInstance().getRecipes().iterator();
        while (iterator.hasNext()) {
            set.add(((Recipe)iterator.next()).getResultItem().id);
        }
        final Iterator iterator2 = FurnaceRecipes.getInstance().getRecipies().values().iterator();
        while (iterator2.hasNext()) {
            set.add(((ItemInstance)iterator2.next()).id);
        }
        Stats.itemCrafted = new Stat[32000];
        for (final Integer n : set) {
            if (Item.items[n] != null) {
                Stats.itemCrafted[n] = new ItemStat(16842752 + n, I18n.get("stat.craftItem", Item.items[n].getName()), n).postConstruct();
            }
        }
        remapIds(Stats.itemCrafted);
    }
    
    private static Stat[] getMinedStats(final String nameKey, final int idOff) {
        final Stat[] stats = new Stat[256];
        for (int i = 0; i < 256; ++i) {
            if (Tile.tiles[i] != null && Tile.tiles[i].isCollectStatistics()) {
                stats[i] = new ItemStat(idOff + i, I18n.get(nameKey, Tile.tiles[i].getName()), i).postConstruct();
                Stats.itemsCraftedStats.add(stats[i]);
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
                    Stats.blocksMinedStats.add(result[i]);
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
        Stats.itemsCraftedStats.remove(stats[id1]);
        Stats.generalStats.remove(stats[id1]);
        stats[id1] = stats[id2];
    }
    
    public static Stat getStat(final int statId) {
        return Stats.statsById.get(statId);
    }
    
    static {
        Stats.statsById = new HashMap();
        Stats.all = new ArrayList();
        Stats.generalStats = new ArrayList();
        Stats.blocksMinedStats = new ArrayList();
        Stats.itemsCraftedStats = new ArrayList();
        Stats.startGame = new GeneralStat(1000, I18n.get("stat.startGame")).setAwardLocallyOnly().postConstruct();
        Stats.createWorld = new GeneralStat(1001, I18n.get("stat.createWorld")).setAwardLocallyOnly().postConstruct();
        Stats.loadWorld = new GeneralStat(1002, I18n.get("stat.loadWorld")).setAwardLocallyOnly().postConstruct();
        Stats.joinMultiplayer = new GeneralStat(1003, I18n.get("stat.joinMultiplayer")).setAwardLocallyOnly().postConstruct();
        Stats.leaveGame = new GeneralStat(1004, I18n.get("stat.leaveGame")).setAwardLocallyOnly().postConstruct();
        Stats.playOneMinute = new GeneralStat(1100, I18n.get("stat.playOneMinute"), Stat.timeFormat).setAwardLocallyOnly().postConstruct();
        Stats.walkOneCm = new GeneralStat(2000, I18n.get("stat.walkOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
        Stats.swimOneCm = new GeneralStat(2001, I18n.get("stat.swimOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
        Stats.fallOneCm = new GeneralStat(2002, I18n.get("stat.fallOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
        Stats.climbOneCm = new GeneralStat(2003, I18n.get("stat.climbOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
        Stats.flyOneCm = new GeneralStat(2004, I18n.get("stat.flyOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
        Stats.diveOneCm = new GeneralStat(2005, I18n.get("stat.diveOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
        Stats.minecartOneCm = new GeneralStat(2006, I18n.get("stat.minecartOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
        Stats.boatOneCm = new GeneralStat(2007, I18n.get("stat.boatOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
        Stats.pigOneCm = new GeneralStat(2008, I18n.get("stat.pigOneCm"), Stat.distanceFormat).setAwardLocallyOnly().postConstruct();
        Stats.timesJumped = new GeneralStat(2010, I18n.get("stat.jump")).setAwardLocallyOnly().postConstruct();
        Stats.itemsDropped = new GeneralStat(2011, I18n.get("stat.drop")).setAwardLocallyOnly().postConstruct();
        Stats.damageDealt = new GeneralStat(2020, I18n.get("stat.damageDealt")).postConstruct();
        Stats.damageTaken = new GeneralStat(2021, I18n.get("stat.damageTaken")).postConstruct();
        Stats.deaths = new GeneralStat(2022, I18n.get("stat.deaths")).postConstruct();
        Stats.mobKills = new GeneralStat(2023, I18n.get("stat.mobKills")).postConstruct();
        Stats.playerKills = new GeneralStat(2024, I18n.get("stat.playerKills")).postConstruct();
        Stats.fishCaught = new GeneralStat(2025, I18n.get("stat.fishCaught")).postConstruct();
        Stats.blockMined = getMinedStats("stat.mineBlock", 16777216);
        Achievements.init();
        Stats.blockStatsLoaded = false;
        Stats.itemStatsLoaded = false;
    }
}
