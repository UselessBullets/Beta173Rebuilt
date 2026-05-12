// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.Level;
import java.util.List;

public class NotGateTile extends TorchTile
{
    private boolean on;
    private static List<Toggle> recentToggles;
    
    @Override
    public int getTexture(final int face, final int data) {
        if (face == 1) {
            return Tile.redStoneDust.getTexture(face, data);
        }
        return super.getTexture(face, data);
    }
    
    private boolean isToggledTooFrequently(final Level level, final int x, final int y, final int z, final boolean add) {
        if (add) {
            NotGateTile.recentToggles.add(new Toggle(x, y, z, level.getTime()));
        }
        int n = 0;
        for (int i = 0; i < NotGateTile.recentToggles.size(); ++i) {
            final Toggle notGateTile_Toggle = NotGateTile.recentToggles.get(i);
            if (notGateTile_Toggle.x == x && notGateTile_Toggle.y == y && notGateTile_Toggle.z == z && ++n >= 8) {
                return true;
            }
        }
        return false;
    }
    
    protected NotGateTile(final int id, final int tex, final boolean on) {
        super(id, tex);
        this.on = false;
        this.on = on;
        this.setTicking(true);
    }
    
    @Override
    public int getTickDelay() {
        return 2;
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        if (level.getData(x, y, z) == 0) {
            super.onPlace(level, x, y, z);
        }
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
    public boolean getSignal(final LevelSource level, final int x, final int y, final int z, final int dir) {
        if (!this.on) {
            return false;
        }
        final int data = level.getData(x, y, z);
        return (data != 5 || dir != 1) && (data != 3 || dir != 3) && (data != 4 || dir != 2) && (data != 1 || dir != 5) && (data != 2 || dir != 4);
    }
    
    private boolean hasNeighborSignal(final Level level, final int x, final int y, final int z) {
        final int data = level.getData(x, y, z);
        return (data == 5 && level.getSignal(x, y - 1, z, 0)) || (data == 3 && level.getSignal(x, y, z - 1, 2)) || (data == 4 && level.getSignal(x, y, z + 1, 3)) || (data == 1 && level.getSignal(x - 1, y, z, 4)) || (data == 2 && level.getSignal(x + 1, y, z, 5));
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        final boolean hasNeighborSignal = this.hasNeighborSignal(level, x, y, z);
        while (NotGateTile.recentToggles.size() > 0 && level.getTime() - NotGateTile.recentToggles.get(0).when > 100L) {
            NotGateTile.recentToggles.remove(0);
        }
        if (this.on) {
            if (hasNeighborSignal) {
                level.setTileAndData(x, y, z, Tile.notGate_off.id, level.getData(x, y, z));
                if (this.isToggledTooFrequently(level, x, y, z, true)) {
                    level.playLocalSound(x + 0.5f, y + 0.5f, z + 0.5f, "random.fizz", 0.5f, 2.6f + (level.random.nextFloat() - level.random.nextFloat()) * 0.8f);
                    for (int i = 0; i < 5; ++i) {
                        level.addParticle("smoke", x + random.nextDouble() * 0.6 + 0.2, y + random.nextDouble() * 0.6 + 0.2, z + random.nextDouble() * 0.6 + 0.2, 0.0, 0.0, 0.0);
                    }
                }
            }
        }
        else if (!hasNeighborSignal && !this.isToggledTooFrequently(level, x, y, z, false)) {
            level.setTileAndData(x, y, z, Tile.notGate_on.id, level.getData(x, y, z));
        }
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        super.neighborChanged(level, x, y, z, type);
        level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
    }
    
    @Override
    public boolean getDirectSignal(final Level level, final int x, final int y, final int z, final int dir) {
        return dir == 0 && this.getSignal(level, x, y, z, dir);
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
    public void animateTick(final Level level, final int x, final int y, final int z, final Random random) {
        if (!this.on) {
            return;
        }
        final int data = level.getData(x, y, z);
        final double x2 = x + 0.5f + (random.nextFloat() - 0.5f) * 0.2;
        final double y2 = y + 0.7f + (random.nextFloat() - 0.5f) * 0.2;
        final double z2 = z + 0.5f + (random.nextFloat() - 0.5f) * 0.2;
        final double n = 0.2199999988079071;
        final double n2 = 0.27000001072883606;
        if (data == 1) {
            level.addParticle("reddust", x2 - n2, y2 + n, z2, 0.0, 0.0, 0.0);
        }
        else if (data == 2) {
            level.addParticle("reddust", x2 + n2, y2 + n, z2, 0.0, 0.0, 0.0);
        }
        else if (data == 3) {
            level.addParticle("reddust", x2, y2 + n, z2 - n2, 0.0, 0.0, 0.0);
        }
        else if (data == 4) {
            level.addParticle("reddust", x2, y2 + n, z2 + n2, 0.0, 0.0, 0.0);
        }
        else {
            level.addParticle("reddust", x2, y2, z2, 0.0, 0.0, 0.0);
        }
    }
    
    static {
        NotGateTile.recentToggles = new ArrayList<>();
    }

    static class Toggle
    {
        int x;
        int y;
        int z;
        long when;

        public Toggle(final int x, final int y, final int z, final long when) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.when = when;
        }
    }
}
