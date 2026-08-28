// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.Direction;
import net.minecraft.Facing;
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
    public static final int DIRECTION_MASK = 0x3; // Useless - possibly a forward port, DiodeTile starts extending DirectionalTile in the future and this mask is part of DirectionalTile
    public static final int DELAY_MASK = 0xc;
    public static final int DELAY_SHIFT = 2;
    public static final double[] DELAY_RENDER_OFFSETS = new double[] { -0.0625, 0.0625, 0.1875, 0.3125 };
    private static final int[] DELAYS = new int[] { 1, 2, 3, 4 };
    private final boolean on;
    
    protected DiodeTile(final int id, final boolean on) {
        super(id, 6, Material.decoration);
        this.on = on;
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 2.0f / 16.0f, 1.0f);
    }
    
    @Override
    public boolean isCubeShaped() {
        return false;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        if (!level.isSolidBlockingTile(x, y - 1, z)) {
            return false;
        }
        return super.mayPlace(level, x, y, z);
    }
    
    @Override
    public boolean canSurvive(final Level level, final int x, final int y, final int z) {
        if (!level.isSolidBlockingTile(x, y - 1, z)) {
            return false;
        }
        return super.canSurvive(level, x, y, z);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        final int data = level.getData(x, y, z);
        final boolean sourceOn = this.getSourceSignal(level, x, y, z, data);
        if (this.on && !sourceOn) {
            level.setTileAndData(x, y, z, Tile.diode_off.id, data);
        }
        else if (!this.on) {
            // when off-diodes are ticked, they always turn on for one tick and
            // then off again if necessary
            level.setTileAndData(x, y, z, Tile.diode_on.id, data);
            if (!sourceOn) {
                int delay = (data & DELAY_MASK) >> DELAY_SHIFT;
                level.addToTickNextTick(x, y, z, Tile.diode_on.id, DiodeTile.DELAYS[delay] * 2);
            }
        }
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        // down is used by the torch tesselator
        if (face == Facing.DOWN) {
            if (this.on) {
                return 99;
            }
            return 115;
        }
        if (face == Facing.UP) {
            if (this.on) {
                return 147;
            }
            return 131;
        }
        // edge of stone half-step
        return 5;
    }
    
    @Override
    public boolean shouldRenderFace(final LevelSource level, final int x, final int y, final int z, final int f) {
        if (f == Facing.DOWN || f == Facing.UP) {
            // up and down is a special case handled by the shape renderer
            return false;
        }
        return true;
    }
    
    @Override
    public int getRenderShape() {
        return Tile.SHAPE_DIODE;
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
    public boolean getSignal(final LevelSource level, final int x, final int y, final int z, final int facing) {
        if (!this.on) {
            return false;
        }

        final int dir = level.getData(x, y, z) & 0x3;

        if (dir == Direction.SOUTH && facing == Facing.SOUTH) return true;
        if (dir == Direction.WEST && facing == Facing.WEST) return true;
        if (dir == Direction.NORTH && facing == Facing.NORTH) return true;
        if (dir == Direction.EAST && facing == Facing.EAST) return true;

        return false;
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (!this.canSurvive(level, x, y, z)) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
            return;
        }

        final int data = level.getData(x, y, z);

        final boolean sourceOn = this.getSourceSignal(level, x, y, z, data);
        final int delay = (data & DELAY_MASK) >> DELAY_SHIFT;
        if (this.on && !sourceOn || !this.on && sourceOn) {
            level.addToTickNextTick(x, y, z, this.id, DiodeTile.DELAYS[delay] * 2);
        }
    }
    
    private boolean getSourceSignal(final Level level, final int x, final int y, final int z, final int data) {
        int dir = data & DIRECTION_MASK;
        switch (dir) {
            case Direction.SOUTH:
                return level.getSignal(x, y, z + 1, 3) || (level.getTile(x, y, z + 1) == Tile.redStoneDust.id && level.getData(x, y, z + 1) > 0);
            case Direction.NORTH:
                return level.getSignal(x, y, z - 1, 2) || (level.getTile(x, y, z - 1) == Tile.redStoneDust.id && level.getData(x, y, z - 1) > 0);
            case Direction.EAST:
                return level.getSignal(x + 1, y, z, 5) || (level.getTile(x + 1, y, z) == Tile.redStoneDust.id && level.getData(x + 1, y, z) > 0);
            case Direction.WEST:
                return level.getSignal(x - 1, y, z, 4) || (level.getTile(x - 1, y, z) == Tile.redStoneDust.id && level.getData(x - 1, y, z) > 0);
        }
        return false;
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        final int data = level.getData(x, y, z);
        int delay = (data & DELAY_MASK) >> DELAY_SHIFT;
        delay = ((delay + 1) << DELAY_SHIFT) & DELAY_MASK;

        level.setData(x, y, z, delay | (data & DIRECTION_MASK));
        return true;
    }
    
    @Override
    public boolean isSignalSource() {
        return false;
    }
    
    @Override
    public void setPlacedBy(final Level level, final int x, final int y, final int z, final Mob by) {
        final int dir = ((Mth.floor(by.yRot * 4.0f / 360.0f + 0.5) & 0x3) + 2) % 4;
        level.setData(x, y, z, dir);

        boolean sourceOn = this.getSourceSignal(level, x, y, z, dir);
        if (sourceOn) {
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
    
    @Override
    public void animateTick(final Level level, final int xt, final int yt, final int zt, final Random random) {
        if (!this.on) return;
        final int data = level.getData(xt, yt, zt);
        int dir = data & 0x3;

        final double x = xt + 0.5f + (random.nextFloat() - 0.5f) * 0.2;
        final double y = yt + 0.4f + (random.nextFloat() - 0.5f) * 0.2;
        final double z = zt + 0.5f + (random.nextFloat() - 0.5f) * 0.2;

        double xo = 0.0;
        double zo = 0.0;

        if (random.nextInt(2) == 0) {
            // spawn on receiver
            switch (dir) {
                case 0:
                    zo = -5.0f / 16.0f;
                    break;
                case 2:
                    zo = 5.0f / 16.0f;
                    break;
                case 3:
                    xo = -5.0f / 16.0f;
                    break;
                case 1:
                    xo = 5.0f / 16.0f;
                    break;
            }
        } else {
            // spawn on transmitter
            final int delay = (data & DELAY_MASK) >> DELAY_SHIFT;
            switch (dir) {
                case 0:
                    zo = DiodeTile.DELAY_RENDER_OFFSETS[delay];
                    break;
                case 2:
                    zo = -DiodeTile.DELAY_RENDER_OFFSETS[delay];
                    break;
                case 3:
                    xo = DiodeTile.DELAY_RENDER_OFFSETS[delay];
                    break;
                case 1:
                    xo = -DiodeTile.DELAY_RENDER_OFFSETS[delay];
                    break;
            }
        }
        level.addParticle("reddust", x + xo, y, z + zo, 0.0, 0.0, 0.0);
    }

}
