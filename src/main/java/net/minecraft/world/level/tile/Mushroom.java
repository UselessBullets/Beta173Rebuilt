// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;
import net.minecraft.world.level.Level;

public class Mushroom extends Bush
{
    protected Mushroom(final int id, final int tex) {
        super(id, tex);
        final float n = 0.2f;
        this.setShape(0.5f - n, 0.0f, 0.5f - n, 0.5f + n, n * 2.0f, 0.5f + n);
        this.setTicking(true);
    }
    
    @Override
    public void tick(final Level level, int x, final int y, int z, final Random random) {
        if (random.nextInt(100) == 0) {
            final int x2 = x + random.nextInt(3) - 1;
            final int y2 = y + random.nextInt(2) - random.nextInt(2);
            final int z2 = z + random.nextInt(3) - 1;
            if (level.isEmptyTile(x2, y2, z2) && this.canSurvive(level, x2, y2, z2)) {
                x += random.nextInt(3) - 1;
                z += random.nextInt(3) - 1;
                if (level.isEmptyTile(x2, y2, z2) && this.canSurvive(level, x2, y2, z2)) {
                    level.setTile(x2, y2, z2, this.id);
                }
            }
        }
    }
    
    @Override
    protected boolean mayPlaceOn(final int tile) {
        return Tile.solid[tile];
    }
    
    @Override
    public boolean canSurvive(final Level level, final int x, final int y, final int z) {
        return y >= 0 && y < 128 && level.getDaytimeRawBrightness(x, y, z) < 13 && this.mayPlaceOn(level.getTile(x, y - 1, z));
    }
}
