// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.Facing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class ButtonTile extends Tile
{
    protected ButtonTile(final int id, final int tex) {
        super(id, tex, Material.decoration);
        this.setTicking(true);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        return null;
    }
    
    @Override
    public int getTickDelay() {
        return 20;
    }
    
    @Override
    public boolean isSolidRender() {
        return false;
    }
    
    @Override
    public boolean isCubeShaped() {
        return false;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z, final int face) {
        if (face == Facing.NORTH && level.isSolidBlockingTile(x, y, z + 1)) return true;
        if (face == Facing.SOUTH && level.isSolidBlockingTile(x, y, z - 1)) return true;
        if (face == Facing.WEST && level.isSolidBlockingTile(x + 1, y, z)) return true;
        if (face == Facing.EAST && level.isSolidBlockingTile(x - 1, y, z)) return true;
        return false;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        if (level.isSolidBlockingTile(x - 1, y, z)) return true;
        if (level.isSolidBlockingTile(x + 1, y, z)) return true;
        if (level.isSolidBlockingTile(x, y, z - 1)) return true;
        if (level.isSolidBlockingTile(x, y, z + 1)) return true;
        return false;
    }
    
    @Override
    public void setPlacedOnFace(final Level level, final int x, final int y, final int z, final int face) {
        final int oldFlip = level.getData(x, y, z) & 0x8;

        int dir;
        if (face == Facing.NORTH && level.isSolidBlockingTile(x, y, z + 1)) dir = 4;
        else if (face == Facing.SOUTH && level.isSolidBlockingTile(x, y, z - 1)) dir = 3;
        else if (face == Facing.WEST && level.isSolidBlockingTile(x + 1, y, z)) dir = 2;
        else if (face == Facing.EAST && level.isSolidBlockingTile(x - 1, y, z)) dir = 1;
        else dir = this.findFace(level, x, y, z);

        level.setData(x, y, z, dir + oldFlip);
    }
    
    private int findFace(final Level level, final int x, final int y, final int z) {
        if (level.isSolidBlockingTile(x - 1, y, z)) return 1;
        if (level.isSolidBlockingTile(x + 1, y, z)) return 2;
        if (level.isSolidBlockingTile(x, y, z - 1)) return 3;
        if (level.isSolidBlockingTile(x, y, z + 1)) return 4;
        return 1;
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (this.checkCanSurvive(level, x, y, z)) {
            final int dir = level.getData(x, y, z) & 0x7;
            boolean replace = false;

            if (!level.isSolidBlockingTile(x - 1, y, z) && dir == 1) replace = true;
            if (!level.isSolidBlockingTile(x + 1, y, z) && dir == 2) replace = true;
            if (!level.isSolidBlockingTile(x, y, z - 1) && dir == 3) replace = true;
            if (!level.isSolidBlockingTile(x, y, z + 1) && dir == 4) replace = true;

            if (replace) {
                this.spawnResources(level, x, y, z, level.getData(x, y, z));
                level.setTile(x, y, z, 0);
            }
        }
    }
    
    private boolean checkCanSurvive(final Level level, final int x, final int y, final int z) {
        if (!this.mayPlace(level, x, y, z)) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
            return false;
        }
        return true;
    }
    
    @Override
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
        final int data = level.getData(x, y, z);
        final int dir = data & 0x7;
        final boolean pressed = (data & 0x8) > 0;

        final float h0 = 6 / 16.0f;
        final float h1 = 10 / 16.0f;
        final float r = 3 / 16.0f;
        float d = 2 / 16.0f;
        if (pressed) d = 1 / 16.0f;

        if (dir == 1) this.setShape(0.0f, h0, 0.5f - r, d, h1, 0.5f + r);
        else if (dir == 2) this.setShape(1.0f - d, h0, 0.5f - r, 1.0f, h1, 0.5f + r);
        else if (dir == 3) this.setShape(0.5f - r, h0, 0.0f, 0.5f + r, h1, d);
        else if (dir == 4) this.setShape(0.5f - r, h0, 1.0f - d, 0.5f + r, h1, 1.0f);
    }
    
    @Override
    public void attack(final Level level, final int x, final int y, final int z, final Player player) {
        this.use(level, x, y, z, player);
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        final int data = level.getData(x, y, z);
        final int dir = data & 0x7;
        final int open = 8 - (data & 0x8);
        if (open == 0) return true;

        level.setData(x, y, z, dir + open);
        level.setTilesDirty(x, y, z, x, y, z);

        level.playSound(x + 0.5, y + 0.5, z + 0.5, "random.click", 0.3f, 0.6f);

        level.updateNeighborsAt(x, y, z, this.id);
        if (dir == 1) level.updateNeighborsAt(x - 1, y, z, this.id);
        else if (dir == 2) level.updateNeighborsAt(x + 1, y, z, this.id);
        else if (dir == 3) level.updateNeighborsAt(x, y, z - 1, this.id);
        else if (dir == 4) level.updateNeighborsAt(x, y, z + 1, this.id);
        else level.updateNeighborsAt(x, y - 1, z, this.id);

        level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
        return true;
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        final int data = level.getData(x, y, z);
        if ((data & 0x8) > 0) {
            final int dir = data & 0x7;
            level.updateNeighborsAt(x, y, z, this.id);

            if (dir == 1) level.updateNeighborsAt(x - 1, y, z, this.id);
            else if (dir == 2) level.updateNeighborsAt(x + 1, y, z, this.id);
            else if (dir == 3) level.updateNeighborsAt(x, y, z - 1, this.id);
            else if (dir == 4) level.updateNeighborsAt(x, y, z + 1, this.id);
            else level.updateNeighborsAt(x, y - 1, z, this.id);
        }
        super.onRemove(level, x, y, z);
    }
    
    @Override
    public boolean getSignal(final LevelSource level, final int x, final int y, final int z, final int dir) {
        return (level.getData(x, y, z) & 0x8) > 0;
    }
    
    @Override
    public boolean getDirectSignal(final Level level, final int x, final int y, final int z, final int dir) {
        final int data = level.getData(x, y, z);
        if ((data & 0x8) == 0x0) return false;
        final int myDir = data & 0x7;

        if (myDir == 5 && dir == 1) return true;
        if (myDir == 4 && dir == 2) return true;
        if (myDir == 3 && dir == 3) return true;
        if (myDir == 2 && dir == 4) return true;
        if (myDir == 1 && dir == 5) return true;

        return false;
    }
    
    @Override
    public boolean isSignalSource() {
        return true;
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (level.isClientSide) return;
        final int data = level.getData(x, y, z);
        if ((data & 0x8) == 0x0) {
            return;
        }

        level.setData(x, y, z, data & 0x7);

        final int dir = data & 0x7;
        level.updateNeighborsAt(x, y, z, this.id);
        if (dir == 1) level.updateNeighborsAt(x - 1, y, z, this.id);
        else if (dir == 2) level.updateNeighborsAt(x + 1, y, z, this.id);
        else if (dir == 3) level.updateNeighborsAt(x, y, z - 1, this.id);
        else if (dir == 4) level.updateNeighborsAt(x, y, z + 1, this.id);
        else level.updateNeighborsAt(x, y - 1, z, this.id);

        level.playSound(x + 0.5, y + 0.5, z + 0.5, "random.click", 0.3f, 0.5f);
        level.setTilesDirty(x, y, z, x, y, z);
    }
    
    @Override
    public void updateDefaultShape() {
        final float x = 3 / 16.0f;
        final float y = 2 / 16.0f;
        final float z = 2 / 16.0f;
        this.setShape(0.5f - x, 0.5f - y, 0.5f - z, 0.5f + x, 0.5f + y, 0.5f + z);
    }
}
