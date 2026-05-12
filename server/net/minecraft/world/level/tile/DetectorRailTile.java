// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.item.Minecart;
import net.minecraft.world.level.LevelSource;
import java.util.Random;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class DetectorRailTile extends RailTile
{
    public DetectorRailTile(final int id, final int tex) {
        super(id, tex, true);
        this.setTicking(true);
    }
    
    @Override
    public int getTickDelay() {
        return 20;
    }
    
    @Override
    public boolean isSignalSource() {
        return true;
    }
    
    @Override
    public void entityInside(final Level level, final int x, final int y, final int z, final Entity entity) {
        if (level.isClientSide) {
            return;
        }
        final int data = level.getData(x, y, z);
        if ((data & 0x8) != 0x0) {
            return;
        }
        this.checkPressed(level, x, y, z, data);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (level.isClientSide) {
            return;
        }
        final int data = level.getData(x, y, z);
        if ((data & 0x8) == 0x0) {
            return;
        }
        this.checkPressed(level, x, y, z, data);
    }
    
    @Override
    public boolean getSignal(final LevelSource level, final int x, final int y, final int z, final int dir) {
        return (level.getData(x, y, z) & 0x8) != 0x0;
    }
    
    @Override
    public boolean getDirectSignal(final Level level, final int x, final int y, final int z, final int dir) {
        return (level.getData(x, y, z) & 0x8) != 0x0 && dir == 1;
    }
    
    private void checkPressed(final Level level, final int x, final int y, final int z, final int currentData) {
        final boolean b = (currentData & 0x8) != 0x0;
        boolean b2 = false;
        final float n = 0.125f;
        if (level.getEntitiesOfClass(Minecart.class, AABB.newTemp(x + n, y, z + n, x + 1 - n, y + 0.25, z + 1 - n)).size() > 0) {
            b2 = true;
        }
        if (b2 && !b) {
            level.setData(x, y, z, currentData | 0x8);
            level.updateNeighborsAt(x, y, z, this.id);
            level.updateNeighborsAt(x, y - 1, z, this.id);
            level.setTilesDirty(x, y, z, x, y, z);
        }
        if (!b2 && b) {
            level.setData(x, y, z, currentData & 0x7);
            level.updateNeighborsAt(x, y, z, this.id);
            level.updateNeighborsAt(x, y - 1, z, this.id);
            level.setTilesDirty(x, y, z, x, y, z);
        }
        if (b2) {
            level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
        }
    }
}
