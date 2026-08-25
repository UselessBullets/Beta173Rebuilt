// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.tile.Tile;
import java.util.Random;
import net.minecraft.world.level.Level;

public class SpringFeature extends Feature
{
    private int tile;
    
    public SpringFeature(final int tile) {
        this.tile = tile;
    }
    
    @Override
    public boolean place(final Level level, final Random random, final int x, final int y, final int z) {
        if (level.getTile(x, y + 1, z) != Tile.rock.id) return false;
        if (level.getTile(x, y - 1, z) != Tile.rock.id) return false;

        if (level.getTile(x, y, z) != 0 && level.getTile(x, y, z) != Tile.rock.id) return false;

        int rockCount = 0;
        if (level.getTile(x - 1, y, z) == Tile.rock.id) rockCount++;
        if (level.getTile(x + 1, y, z) == Tile.rock.id) rockCount++;
        if (level.getTile(x, y, z - 1) == Tile.rock.id) rockCount++;
        if (level.getTile(x, y, z + 1) == Tile.rock.id) rockCount++;

        int holeCount = 0;
        if (level.isEmptyTile(x - 1, y, z)) holeCount++;
        if (level.isEmptyTile(x + 1, y, z)) holeCount++;
        if (level.isEmptyTile(x, y, z - 1)) holeCount++;
        if (level.isEmptyTile(x, y, z + 1)) holeCount++;

        if (rockCount == 3 && holeCount == 1) {
            level.setTile(x, y, z, this.tile);
            level.instaTick = true;
            Tile.tiles[this.tile].tick(level, x, y, z, random);
            level.instaTick = false;
        }

        return true;
    }
}
