// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;
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
        return (face == 2 && level.isSolidBlockingTile(x, y, z + 1)) || (face == 3 && level.isSolidBlockingTile(x, y, z - 1)) || (face == 4 && level.isSolidBlockingTile(x + 1, y, z)) || (face == 5 && level.isSolidBlockingTile(x - 1, y, z));
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return level.isSolidBlockingTile(x - 1, y, z) || level.isSolidBlockingTile(x + 1, y, z) || level.isSolidBlockingTile(x, y, z - 1) || level.isSolidBlockingTile(x, y, z + 1);
    }
    
    @Override
    public void setPlacedOnFace(final Level level, final int x, final int y, final int z, final int face) {
        final int n = level.getData(x, y, z) & 0x8;
        int face2;
        if (face == 2 && level.isSolidBlockingTile(x, y, z + 1)) {
            face2 = 4;
        }
        else if (face == 3 && level.isSolidBlockingTile(x, y, z - 1)) {
            face2 = 3;
        }
        else if (face == 4 && level.isSolidBlockingTile(x + 1, y, z)) {
            face2 = 2;
        }
        else if (face == 5 && level.isSolidBlockingTile(x - 1, y, z)) {
            face2 = 1;
        }
        else {
            face2 = this.findFace(level, x, y, z);
        }
        level.setData(x, y, z, face2 + n);
    }
    
    private int findFace(final Level level, final int x, final int y, final int z) {
        if (level.isSolidBlockingTile(x - 1, y, z)) {
            return 1;
        }
        if (level.isSolidBlockingTile(x + 1, y, z)) {
            return 2;
        }
        if (level.isSolidBlockingTile(x, y, z - 1)) {
            return 3;
        }
        if (level.isSolidBlockingTile(x, y, z + 1)) {
            return 4;
        }
        return 1;
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (this.checkCanSurvive(level, x, y, z)) {
            final int n = level.getData(x, y, z) & 0x7;
            boolean b = false;
            if (!level.isSolidBlockingTile(x - 1, y, z) && n == 1) {
                b = true;
            }
            if (!level.isSolidBlockingTile(x + 1, y, z) && n == 2) {
                b = true;
            }
            if (!level.isSolidBlockingTile(x, y, z - 1) && n == 3) {
                b = true;
            }
            if (!level.isSolidBlockingTile(x, y, z + 1) && n == 4) {
                b = true;
            }
            if (b) {
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
        final int n = data & 0x7;
        final boolean b = (data & 0x8) > 0;
        final float n2 = 0.375f;
        final float n3 = 0.625f;
        final float n4 = 0.1875f;
        float n5 = 0.125f;
        if (b) {
            n5 = 0.0625f;
        }
        if (n == 1) {
            this.setShape(0.0f, n2, 0.5f - n4, n5, n3, 0.5f + n4);
        }
        else if (n == 2) {
            this.setShape(1.0f - n5, n2, 0.5f - n4, 1.0f, n3, 0.5f + n4);
        }
        else if (n == 3) {
            this.setShape(0.5f - n4, n2, 0.0f, 0.5f + n4, n3, n5);
        }
        else if (n == 4) {
            this.setShape(0.5f - n4, n2, 1.0f - n5, 0.5f + n4, n3, 1.0f);
        }
    }
    
    @Override
    public void attack(final Level level, final int x, final int y, final int z, final Player player) {
        this.use(level, x, y, z, player);
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        final int data = level.getData(x, y, z);
        final int n = data & 0x7;
        final int n2 = 8 - (data & 0x8);
        if (n2 == 0) {
            return true;
        }
        level.setData(x, y, z, n + n2);
        level.setTilesDirty(x, y, z, x, y, z);
        level.playLocalSound(x + 0.5, y + 0.5, z + 0.5, "random.click", 0.3f, 0.6f);
        level.updateNeighborsAt(x, y, z, this.id);
        if (n == 1) {
            level.updateNeighborsAt(x - 1, y, z, this.id);
        }
        else if (n == 2) {
            level.updateNeighborsAt(x + 1, y, z, this.id);
        }
        else if (n == 3) {
            level.updateNeighborsAt(x, y, z - 1, this.id);
        }
        else if (n == 4) {
            level.updateNeighborsAt(x, y, z + 1, this.id);
        }
        else {
            level.updateNeighborsAt(x, y - 1, z, this.id);
        }
        level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
        return true;
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        final int data = level.getData(x, y, z);
        if ((data & 0x8) > 0) {
            level.updateNeighborsAt(x, y, z, this.id);
            final int n = data & 0x7;
            if (n == 1) {
                level.updateNeighborsAt(x - 1, y, z, this.id);
            }
            else if (n == 2) {
                level.updateNeighborsAt(x + 1, y, z, this.id);
            }
            else if (n == 3) {
                level.updateNeighborsAt(x, y, z - 1, this.id);
            }
            else if (n == 4) {
                level.updateNeighborsAt(x, y, z + 1, this.id);
            }
            else {
                level.updateNeighborsAt(x, y - 1, z, this.id);
            }
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
        if ((data & 0x8) == 0x0) {
            return false;
        }
        final int n = data & 0x7;
        return (n == 5 && dir == 1) || (n == 4 && dir == 2) || (n == 3 && dir == 3) || (n == 2 && dir == 4) || (n == 1 && dir == 5);
    }
    
    @Override
    public boolean isSignalSource() {
        return true;
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
        level.setData(x, y, z, data & 0x7);
        level.updateNeighborsAt(x, y, z, this.id);
        final int n = data & 0x7;
        if (n == 1) {
            level.updateNeighborsAt(x - 1, y, z, this.id);
        }
        else if (n == 2) {
            level.updateNeighborsAt(x + 1, y, z, this.id);
        }
        else if (n == 3) {
            level.updateNeighborsAt(x, y, z - 1, this.id);
        }
        else if (n == 4) {
            level.updateNeighborsAt(x, y, z + 1, this.id);
        }
        else {
            level.updateNeighborsAt(x, y - 1, z, this.id);
        }
        level.playLocalSound(x + 0.5, y + 0.5, z + 0.5, "random.click", 0.3f, 0.5f);
        level.setTilesDirty(x, y, z, x, y, z);
    }
}
