// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.Facing;
import net.minecraft.SharedConstants;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.Level;
import java.util.List;

public class NotGateTile extends TorchTile
{
    private static final int RECENT_TOGGLE_TIMER = SharedConstants.TICKS_PER_SECOND * 5;
    private static final int MAX_RECENT_TOGGLES = 8;
    private boolean on = false;
    private static List<Toggle> recentToggles = new ArrayList<>();
    
    @Override
    public int getTexture(final int face, final int data) {
        if (face == Facing.UP) return Tile.redStoneDust.getTexture(face, data);
        return super.getTexture(face, data);
    }
    
    private boolean isToggledTooFrequently(final Level level, final int x, final int y, final int z, final boolean add) {
        if (add) NotGateTile.recentToggles.add(new Toggle(x, y, z, level.getTime()));
        int count = 0;

        for (int i = 0; i < NotGateTile.recentToggles.size(); ++i) {
            final Toggle toggle = NotGateTile.recentToggles.get(i);
            if (toggle.x == x && toggle.y == y && toggle.z == z) {
                count++;
                if (count >= MAX_RECENT_TOGGLES) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected NotGateTile(final int id, final int tex, final boolean on) {
        super(id, tex);
        this.on = on;
        this.setTicking(true);
    }
    
    @Override
    public int getTickDelay() {
        return 2;
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        if (level.getData(x, y, z) == 0) super.onPlace(level, x, y, z);
        if (this.on) {
            level.updateNeighborsAt(x, y - 1, z, this.id);
            level.updateNeighborsAt(x, y + 1, z, this.id);
            level.updateNeighborsAt(x - 1, y, z, this.id);
            level.updateNeighborsAt(x + 1, y, z, this.id);
            level.updateNeighborsAt(x, y, z - 1, this.id);
            level.updateNeighborsAt(x, y, z + 1, this.id);
        }
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        if (this.on) {
            level.updateNeighborsAt(x, y - 1, z, this.id);
            level.updateNeighborsAt(x, y + 1, z, this.id);
            level.updateNeighborsAt(x - 1, y, z, this.id);
            level.updateNeighborsAt(x + 1, y, z, this.id);
            level.updateNeighborsAt(x, y, z - 1, this.id);
            level.updateNeighborsAt(x, y, z + 1, this.id);
        }
    }
    
    @Override
    public boolean getSignal(final LevelSource level, final int x, final int y, final int z, final int face) {
        if (!this.on) return false;

        final int dir = level.getData(x, y, z);

        if (dir == 5 && face == 1) return false;
        if (dir == 3 && face == 3) return false;
        if (dir == 4 && face == 2) return false;
        if (dir == 1 && face == 5) return false;
        if (dir == 2 && face == 4) return false;

        return true;
    }
    
    private boolean hasNeighborSignal(final Level level, final int x, final int y, final int z) {
        final int dir = level.getData(x, y, z);

        if (dir == 5 && level.getSignal(x, y - 1, z, 0)) return true;
        if (dir == 3 && level.getSignal(x, y, z - 1, 2)) return true;
        if (dir == 4 && level.getSignal(x, y, z + 1, 3)) return true;
        if (dir == 1 && level.getSignal(x - 1, y, z, 4)) return true;
        if (dir == 2 && level.getSignal(x + 1, y, z, 5)) return true;
        return false;
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        final boolean neighborSignal = this.hasNeighborSignal(level, x, y, z);

        while (!NotGateTile.recentToggles.isEmpty() && level.getTime() - NotGateTile.recentToggles.get(0).when > RECENT_TOGGLE_TIMER) {
            NotGateTile.recentToggles.remove(0);
        }

        if (this.on) {
            if (neighborSignal) {
                level.setTileAndData(x, y, z, Tile.notGate_off.id, level.getData(x, y, z));

                if (this.isToggledTooFrequently(level, x, y, z, true)) {
                    level.playLocalSound(x + 0.5f, y + 0.5f, z + 0.5f, "random.fizz", 0.5f, 2.6f + (level.random.nextFloat() - level.random.nextFloat()) * 0.8f);
                    for (int i = 0; i < 5; ++i) {
                        double xx = x + random.nextDouble() * 0.6 + 0.2;
                        double yy = y + random.nextDouble() * 0.6 + 0.2;
                        double zz = z + random.nextDouble() * 0.6 + 0.2;

                        level.addParticle("smoke", xx, yy, zz, 0.0, 0.0, 0.0);
                    }
                }
            }
        }
        else {
            if (!neighborSignal) {
                if (!this.isToggledTooFrequently(level, x, y, z, false)) {
                    level.setTileAndData(x, y, z, Tile.notGate_on.id, level.getData(x, y, z));
                }
            }
        }
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        super.neighborChanged(level, x, y, z, type);
        level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
    }
    
    @Override
    public boolean getDirectSignal(final Level level, final int x, final int y, final int z, final int face) {
        if (face == Facing.DOWN) {
            return this.getSignal(level, x, y, z, face);
        }
        return false;
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Tile.notGate_on.id;
    }
    
    @Override
    public boolean isSignalSource() {
        return true;
    }
    
    @Override
    public void animateTick(final Level level, final int xt, final int yt, final int zt, final Random random) {
        if (!this.on) return;
        final int dir = level.getData(xt, yt, zt);
        final double x = xt + 0.5f + (random.nextFloat() - 0.5f) * 0.2;
        final double y = yt + 0.7f + (random.nextFloat() - 0.5f) * 0.2;
        final double z = zt + 0.5f + (random.nextFloat() - 0.5f) * 0.2;
        final double h = 0.22f;
        final double r = 0.27f;
        if (dir == 1) {
            level.addParticle("reddust", x - r, y + h, z, 0.0, 0.0, 0.0);
        }
        else if (dir == 2) {
            level.addParticle("reddust", x + r, y + h, z, 0.0, 0.0, 0.0);
        }
        else if (dir == 3) {
            level.addParticle("reddust", x, y + h, z - r, 0.0, 0.0, 0.0);
        }
        else if (dir == 4) {
            level.addParticle("reddust", x, y + h, z + r, 0.0, 0.0, 0.0);
        }
        else {
            level.addParticle("reddust", x, y, z, 0.0, 0.0, 0.0);
        }
    }

    static class Toggle
    {
        int x, y, z;
        long when;

        public Toggle(final int x, final int y, final int z, final long when) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.when = when;
        }
    }
}
