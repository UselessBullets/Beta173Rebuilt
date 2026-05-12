// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.level.LevelSource;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import java.util.Random;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class PressurePlateTile extends Tile
{
    private PressurePlateTile_Sensitivity sensitivity;
    
    protected PressurePlateTile(final int id, final int tex, final PressurePlateTile_Sensitivity sensitivity, final Material material) {
        super(id, tex, material);
        this.sensitivity = sensitivity;
        this.setTicking(true);
        final float n = 0.0625f;
        this.setShape(n, 0.0f, n, 1.0f - n, 0.03125f, 1.0f - n);
    }
    
    @Override
    public int getTickDelay() {
        return 20;
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        return null;
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
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return level.isSolidBlockingTile(x, y - 1, z);
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        boolean b = false;
        if (!level.isSolidBlockingTile(x, y - 1, z)) {
            b = true;
        }
        if (b) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        }
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (level.isClientSide) {
            return;
        }
        if (level.getData(x, y, z) == 0) {
            return;
        }
        this.checkPressed(level, x, y, z);
    }
    
    @Override
    public void entityInside(final Level level, final int x, final int y, final int z, final Entity entity) {
        if (level.isClientSide) {
            return;
        }
        if (level.getData(x, y, z) == 1) {
            return;
        }
        this.checkPressed(level, x, y, z);
    }
    
    private void checkPressed(final Level level, final int x, final int y, final int z) {
        final boolean b = level.getData(x, y, z) == 1;
        boolean b2 = false;
        final float n = 0.125f;
        List list = null;
        if (this.sensitivity == PressurePlateTile_Sensitivity.everything) {
            list = level.getEntities(null, AABB.newTemp(x + n, y, z + n, x + 1 - n, y + 0.25, z + 1 - n));
        }
        if (this.sensitivity == PressurePlateTile_Sensitivity.mobs) {
            list = level.getEntitiesOfClass(Mob.class, AABB.newTemp(x + n, y, z + n, x + 1 - n, y + 0.25, z + 1 - n));
        }
        if (this.sensitivity == PressurePlateTile_Sensitivity.players) {
            list = level.getEntitiesOfClass(Player.class, AABB.newTemp(x + n, y, z + n, x + 1 - n, y + 0.25, z + 1 - n));
        }
        if (list.size() > 0) {
            b2 = true;
        }
        if (b2 && !b) {
            level.setData(x, y, z, 1);
            level.updateNeighborsAt(x, y, z, this.id);
            level.updateNeighborsAt(x, y - 1, z, this.id);
            level.setTilesDirty(x, y, z, x, y, z);
            level.playLocalSound(x + 0.5, y + 0.1, z + 0.5, "random.click", 0.3f, 0.6f);
        }
        if (!b2 && b) {
            level.setData(x, y, z, 0);
            level.updateNeighborsAt(x, y, z, this.id);
            level.updateNeighborsAt(x, y - 1, z, this.id);
            level.setTilesDirty(x, y, z, x, y, z);
            level.playLocalSound(x + 0.5, y + 0.1, z + 0.5, "random.click", 0.3f, 0.5f);
        }
        if (b2) {
            level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
        }
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        if (level.getData(x, y, z) > 0) {
            level.updateNeighborsAt(x, y, z, this.id);
            level.updateNeighborsAt(x, y - 1, z, this.id);
        }
        super.onRemove(level, x, y, z);
    }
    
    @Override
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
        final boolean b = level.getData(x, y, z) == 1;
        final float n = 0.0625f;
        if (b) {
            this.setShape(n, 0.0f, n, 1.0f - n, 0.03125f, 1.0f - n);
        }
        else {
            this.setShape(n, 0.0f, n, 1.0f - n, 0.0625f, 1.0f - n);
        }
    }
    
    @Override
    public boolean getSignal(final LevelSource level, final int x, final int y, final int z, final int dir) {
        return level.getData(x, y, z) > 0;
    }
    
    @Override
    public boolean getDirectSignal(final Level level, final int x, final int y, final int z, final int dir) {
        return level.getData(x, y, z) != 0 && dir == 1;
    }
    
    @Override
    public boolean isSignalSource() {
        return true;
    }
    
    @Override
    public int getPistonPushReaction() {
        return 1;
    }
}
