// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import net.minecraft.world.level.tile.Tile;
import java.util.ArrayList;
import java.util.List;

public class User
{
    public static List allowedTiles;
    public String name;
    public String sessionId;
    public String mpPassword;
    
    public User(final String name, final String sessionId) {
        this.name = name;
        this.sessionId = sessionId;
    }
    
    static {
        (User.allowedTiles = new ArrayList()).add(Tile.rock);
        User.allowedTiles.add(Tile.stoneBrick);
        User.allowedTiles.add(Tile.redBrick);
        User.allowedTiles.add(Tile.dirt);
        User.allowedTiles.add(Tile.wood);
        User.allowedTiles.add(Tile.treeTrunk);
        User.allowedTiles.add(Tile.leaves);
        User.allowedTiles.add(Tile.torch);
        User.allowedTiles.add(Tile.stoneSlabHalf);
        User.allowedTiles.add(Tile.glass);
        User.allowedTiles.add(Tile.mossStone);
        User.allowedTiles.add(Tile.sapling);
        User.allowedTiles.add(Tile.flower);
        User.allowedTiles.add(Tile.rose);
        User.allowedTiles.add(Tile.mushroom1);
        User.allowedTiles.add(Tile.mushroom2);
        User.allowedTiles.add(Tile.sand);
        User.allowedTiles.add(Tile.gravel);
        User.allowedTiles.add(Tile.sponge);
        User.allowedTiles.add(Tile.cloth);
        User.allowedTiles.add(Tile.coalOre);
        User.allowedTiles.add(Tile.ironOre);
        User.allowedTiles.add(Tile.goldOre);
        User.allowedTiles.add(Tile.ironBlock);
        User.allowedTiles.add(Tile.goldBlock);
        User.allowedTiles.add(Tile.bookshelf);
        User.allowedTiles.add(Tile.tnt);
        User.allowedTiles.add(Tile.obsidian);
    }
}
