// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.Facing;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.item.Minecart;
import net.minecraft.world.level.LevelSource;

import java.util.List;
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
        if ((data & RAIL_DATA_BIT) != 0x0) {
            return;
        }

        this.checkPressed(level, x, y, z, data);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (level.isClientSide) return;

        final int data = level.getData(x, y, z);
        if ((data & RAIL_DATA_BIT) == 0x0) {
            return;
        }

        this.checkPressed(level, x, y, z, data);
    }
    
    @Override
    public boolean getSignal(final LevelSource level, final int x, final int y, final int z, final int dir) {
        return (level.getData(x, y, z) & RAIL_DATA_BIT) != 0x0;
    }
    
    @Override
    public boolean getDirectSignal(final Level level, final int x, final int y, final int z, final int facing) {
        if ((level.getData(x, y, z) & RAIL_DATA_BIT) == 0x0) return false;
        return facing == Facing.UP;
    }
    
    private void checkPressed(final Level level, final int x, final int y, final int z, final int currentData) {
        final boolean wasPressed = (currentData & RAIL_DATA_BIT) != 0x0;
        boolean shouldBePressed = false;

        final float b = 2 / 16.0f;
        List<Minecart> entities = level.getEntitiesOfClass(Minecart.class, AABB.newTemp(x + b, y, z + b, x + 1 - b, y + 0.25, z + 1 - b));
        if (!entities.isEmpty()) {
            shouldBePressed = true;
        }

        if (shouldBePressed && !wasPressed) {
            level.setData(x, y, z, currentData | RAIL_DATA_BIT);
            level.updateNeighborsAt(x, y, z, this.id);
            level.updateNeighborsAt(x, y - 1, z, this.id);
            level.setTilesDirty(x, y, z, x, y, z);
        }
        if (!shouldBePressed && wasPressed) {
            level.setData(x, y, z, currentData & RAIL_DIRECTION_MASK);
            level.updateNeighborsAt(x, y, z, this.id);
            level.updateNeighborsAt(x, y - 1, z, this.id);
            level.setTilesDirty(x, y, z, x, y, z);
        }

        if (shouldBePressed) {
            level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
        }
    }
}
