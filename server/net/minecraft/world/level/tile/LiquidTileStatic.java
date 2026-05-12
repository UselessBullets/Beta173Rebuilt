// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class LiquidTileStatic extends LiquidTile
{
    protected LiquidTileStatic(final int id, final Material material) {
        super(id, material);
        this.setTicking(false);
        if (material == Material.lava) {
            this.setTicking(true);
        }
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        super.neighborChanged(level, x, y, z, type);
        if (level.getTile(x, y, z) == this.id) {
            this.setDynamic(level, x, y, z);
        }
    }
    
    private void setDynamic(final Level level, final int x, final int y, final int z) {
        final int data = level.getData(x, y, z);
        level.noNeighborUpdate = true;
        level.setTileAndDataNoUpdate(x, y, z, this.id - 1, data);
        level.setTilesDirty(x, y, z, x, y, z);
        level.addToTickNextTick(x, y, z, this.id - 1, this.getTickDelay());
        level.noNeighborUpdate = false;
    }
    
    @Override
    public void tick(final Level level, int x, int y, int z, final Random random) {
        if (this.material == Material.lava) {
            for (int nextInt = random.nextInt(3), i = 0; i < nextInt; ++i) {
                x += random.nextInt(3) - 1;
                ++y;
                z += random.nextInt(3) - 1;
                final int tile = level.getTile(x, y, z);
                if (tile == 0) {
                    if (this.isFlammable(level, x - 1, y, z) || this.isFlammable(level, x + 1, y, z) || this.isFlammable(level, x, y, z - 1) || this.isFlammable(level, x, y, z + 1) || this.isFlammable(level, x, y - 1, z) || this.isFlammable(level, x, y + 1, z)) {
                        level.setTile(x, y, z, Tile.fire.id);
                        return;
                    }
                }
                else if (Tile.tiles[tile].material.blocksMotion()) {
                    return;
                }
            }
        }
    }
    
    private boolean isFlammable(final Level level, final int x, final int y, final int z) {
        return level.getMaterial(x, y, z).isFlammable();
    }
}
