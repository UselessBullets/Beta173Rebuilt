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
    private Sensitivity sensitivity;
    
    protected PressurePlateTile(final int id, final int tex, final Sensitivity sensitivity, final Material material) {
        super(id, tex, material);
        this.sensitivity = sensitivity;
        this.setTicking(true);

        final float o = 1 / 16.0f;
        this.setShape(o, 0.0f, o, 1.0f - o, 0.5f / 16.0f, 1.0f - o);
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
        boolean replace = false;

        if (!level.isSolidBlockingTile(x, y - 1, z)) replace = true;

        if (replace) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        }
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (level.isClientSide) return;
        if (level.getData(x, y, z) == 0) {
            return;
        }

        this.checkPressed(level, x, y, z);
    }
    
    @Override
    public void entityInside(final Level level, final int x, final int y, final int z, final Entity entity) {
        if (level.isClientSide) return;

        if (level.getData(x, y, z) == 1) {
            return;
        }

        this.checkPressed(level, x, y, z);
    }
    
    private void checkPressed(final Level level, final int x, final int y, final int z) {
        final boolean wasPressed = level.getData(x, y, z) == 1;
        boolean shouldBePressed = false;

        final float b = 2 / 16.0f;
        List<? extends Entity> list = null;

        if (this.sensitivity == Sensitivity.everything) list = level.getEntities(null, AABB.newTemp(x + b, y, z + b, x + 1 - b, y + 0.25, z + 1 - b));
        if (this.sensitivity == Sensitivity.mobs) list = level.getEntitiesOfClass(Mob.class, AABB.newTemp(x + b, y, z + b, x + 1 - b, y + 0.25, z + 1 - b));
        if (this.sensitivity == Sensitivity.players) list = level.getEntitiesOfClass(Player.class, AABB.newTemp(x + b, y, z + b, x + 1 - b, y + 0.25, z + 1 - b));

        if (!list.isEmpty()) {
            shouldBePressed = true;
        }

        if (shouldBePressed && !wasPressed) {
            level.setData(x, y, z, 1);
            level.updateNeighborsAt(x, y, z, this.id);
            level.updateNeighborsAt(x, y - 1, z, this.id);
            level.setTilesDirty(x, y, z, x, y, z);

            level.playLocalSound(x + 0.5, y + 0.1, z + 0.5, "random.click", 0.3f, 0.6f);
        }

        if (!shouldBePressed && wasPressed) {
            level.setData(x, y, z, 0);
            level.updateNeighborsAt(x, y, z, this.id);
            level.updateNeighborsAt(x, y - 1, z, this.id);
            level.setTilesDirty(x, y, z, x, y, z);

            level.playLocalSound(x + 0.5, y + 0.1, z + 0.5, "random.click", 0.3f, 0.5f);
        }

        if (shouldBePressed) {
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
        final boolean pressed = level.getData(x, y, z) == 1;
        final float o = 1 / 16.0f;
        if (pressed) {
            this.setShape(o, 0.0f, o, 1.0f - o, 0.5f / 16.0f, 1.0f - o);
        }
        else {
            this.setShape(o, 0.0f, o, 1.0f - o, 1 / 16.0f, 1.0f - o);
        }
    }
    
    @Override
    public boolean getSignal(final LevelSource level, final int x, final int y, final int z, final int dir) {
        return level.getData(x, y, z) > 0;
    }
    
    @Override
    public boolean getDirectSignal(final Level level, final int x, final int y, final int z, final int dir) {
        if (level.getData(x, y, z) == 0) return false;
        return dir == 1;
    }
    
    @Override
    public boolean isSignalSource() {
        return true;
    }
    
    @Override
    public void updateDefaultShape() {
        final float x = 8 / 16.0f;
        final float y = 2 / 16.0f;
        final float z = 8 / 16.0f;
        this.setShape(0.5f - x, 0.5f - y, 0.5f - z, 0.5f + x, 0.5f + y, 0.5f + z);
    }
    
    @Override
    public int getPistonPushReaction() {
        return Material.PUSH_DESTROY;
    }

    public enum Sensitivity
    {
        everything,
        mobs,
        players;
    }
}
