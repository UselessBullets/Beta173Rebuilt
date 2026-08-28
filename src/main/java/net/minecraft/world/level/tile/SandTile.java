// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.entity.item.FallingTile;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class SandTile extends Tile
{
    public static boolean instaFall = false;
    
    public SandTile(final int id, final int tex) {
        super(id, tex, Material.sand);
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        this.checkSlide(level, x, y, z);
    }
    
    private void checkSlide(final Level level, final int x, int y, final int z) {
        int x2 = x;
        int y2 = y;
        int z2 = z;
        if (isFree(level, x2, y2 - 1, z2) && y2 >= 0) {
            final int r = 32;

            if (SandTile.instaFall || !level.hasChunksAt(x - r, y - r, z - r, x + r, y + r, z + r)) {
                level.setTile(x, y, z, 0);
                while (isFree(level, x, y - 1, z) && y > 0) {
                    y--;
                }
                if (y > 0) {
                    level.setTile(x, y, z, this.id);
                }
            }
            else {
                FallingTile e = new FallingTile(level, x + 0.5f, y + 0.5f, z + 0.5f, this.id);
                level.addEntity(e);
            }
        }
    }
    
    @Override
    public int getTickDelay() {
        return 3;
    }
    
    public static boolean isFree(final Level level, final int x, final int y, final int z) {
        final int t = level.getTile(x, y, z);
        if (t == 0) return true;
        if (t == Tile.fire.id) return true;
        final Material material = Tile.tiles[t].material;
        if (material == Material.water) return true;
        if (material == Material.lava) return true;
        return false;
    }

}
