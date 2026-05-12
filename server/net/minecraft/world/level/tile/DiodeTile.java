// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.item.Item;
import util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelSource;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class DiodeTile extends Tile
{
    public static final double[] DELAY_RENDER_OFFSETS;
    private static final int[] DELAYS;
    private final boolean on;
    
    protected DiodeTile(final int id, final boolean on) {
        super(id, 6, Material.decoration);
        this.on = on;
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
    }
    
    @Override
    public boolean isCubeShaped() {
        return false;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return level.isSolidBlockingTile(x, y - 1, z) && super.mayPlace(level, x, y, z);
    }
    
    @Override
    public boolean canSurvive(final Level level, final int x, final int y, final int z) {
        return level.isSolidBlockingTile(x, y - 1, z) && super.canSurvive(level, x, y, z);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        final int data = level.getData(x, y, z);
        final boolean sourceSignal = this.getSourceSignal(level, x, y, z, data);
        if (this.on && !sourceSignal) {
            level.setTileAndData(x, y, z, Tile.diode_off.id, data);
        }
        else if (!this.on) {
            level.setTileAndData(x, y, z, Tile.diode_on.id, data);
            if (!sourceSignal) {
                level.addToTickNextTick(x, y, z, Tile.diode_on.id, DiodeTile.DELAYS[(data & 0xC) >> 2] * 2);
            }
        }
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if (face == 0) {
            if (this.on) {
                return 99;
            }
            return 115;
        }
        else {
            if (face != 1) {
                return 5;
            }
            if (this.on) {
                return 147;
            }
            return 131;
        }
    }
    
    @Override
    public int getTexture(final int face) {
        return this.getTexture(face, 0);
    }
    
    @Override
    public boolean getDirectSignal(final Level level, final int x, final int y, final int z, final int dir) {
        return this.getSignal(level, x, y, z, dir);
    }
    
    @Override
    public boolean getSignal(final LevelSource level, final int x, final int y, final int z, final int dir) {
        if (!this.on) {
            return false;
        }
        final int n = level.getData(x, y, z) & 0x3;
        return (n == 0 && dir == 3) || (n == 1 && dir == 4) || (n == 2 && dir == 2) || (n == 3 && dir == 5);
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (!this.canSurvive(level, x, y, z)) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
            return;
        }
        final int data = level.getData(x, y, z);
        final boolean sourceSignal = this.getSourceSignal(level, x, y, z, data);
        final int n = (data & 0xC) >> 2;
        if (this.on && !sourceSignal) {
            level.addToTickNextTick(x, y, z, this.id, DiodeTile.DELAYS[n] * 2);
        }
        else if (!this.on && sourceSignal) {
            level.addToTickNextTick(x, y, z, this.id, DiodeTile.DELAYS[n] * 2);
        }
    }
    
    private boolean getSourceSignal(final Level level, final int x, final int y, final int z, final int data) {
        switch (data & 0x3) {
            case 0: {
                return level.getSignal(x, y, z + 1, 3) || (level.getTile(x, y, z + 1) == Tile.redStoneDust.id && level.getData(x, y, z + 1) > 0);
            }
            case 2: {
                return level.getSignal(x, y, z - 1, 2) || (level.getTile(x, y, z - 1) == Tile.redStoneDust.id && level.getData(x, y, z - 1) > 0);
            }
            case 3: {
                return level.getSignal(x + 1, y, z, 5) || (level.getTile(x + 1, y, z) == Tile.redStoneDust.id && level.getData(x + 1, y, z) > 0);
            }
            case 1: {
                return level.getSignal(x - 1, y, z, 4) || (level.getTile(x - 1, y, z) == Tile.redStoneDust.id && level.getData(x - 1, y, z) > 0);
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        final int data = level.getData(x, y, z);
        level.setData(x, y, z, (((data & 0xC) >> 2) + 1 << 2 & 0xC) | (data & 0x3));
        return true;
    }
    
    @Override
    public boolean isSignalSource() {
        return false;
    }
    
    @Override
    public void setPlacedBy(final Level level, final int x, final int y, final int z, final Mob by) {
        final int n = ((Mth.floor(by.yRot * 4.0f / 360.0f + 0.5) & 0x3) + 2) % 4;
        level.setData(x, y, z, n);
        if (this.getSourceSignal(level, x, y, z, n)) {
            level.addToTickNextTick(x, y, z, this.id, 1);
        }
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        level.updateNeighborsAt(x + 1, y, z, this.id);
        level.updateNeighborsAt(x - 1, y, z, this.id);
        level.updateNeighborsAt(x, y, z + 1, this.id);
        level.updateNeighborsAt(x, y, z - 1, this.id);
        level.updateNeighborsAt(x, y - 1, z, this.id);
        level.updateNeighborsAt(x, y + 1, z, this.id);
    }
    
    @Override
    public boolean isSolidRender() {
        return false;
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Item.diode.id;
    }
    
    static {
        DELAY_RENDER_OFFSETS = new double[] { -0.0625, 0.0625, 0.1875, 0.3125 };
        DELAYS = new int[] { 1, 2, 3, 4 };
    }
}
