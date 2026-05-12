// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingTile;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class SandTile extends Tile
{
    public static boolean instaFall;
    
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
        final int n = y;
        if (isFree(level, x, n - 1, z) && n >= 0) {
            final int n2 = 32;
            if (SandTile.instaFall || !level.hasChunksAt(x - n2, y - n2, z - n2, x + n2, y + n2, z + n2)) {
                level.setTile(x, y, z, 0);
                while (isFree(level, x, y - 1, z) && y > 0) {
                    --y;
                }
                if (y > 0) {
                    level.setTile(x, y, z, this.id);
                }
            }
            else {
                level.addEntity(new FallingTile(level, x + 0.5f, y + 0.5f, z + 0.5f, this.id));
            }
        }
    }
    
    @Override
    public int getTickDelay() {
        return 3;
    }
    
    public static boolean isFree(final Level level, final int x, final int y, final int z) {
        final int tile = level.getTile(x, y, z);
        if (tile == 0) {
            return true;
        }
        if (tile == Tile.fire.id) {
            return true;
        }
        final Material material = Tile.tiles[tile].material;
        return material == Material.water || material == Material.lava;
    }
    
    static {
        SandTile.instaFall = false;
    }
}
