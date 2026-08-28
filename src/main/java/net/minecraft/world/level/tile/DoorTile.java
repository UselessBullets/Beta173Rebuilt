// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.Facing;
import net.minecraft.world.level.LevelEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.Item;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class DoorTile extends Tile
{
    public static final int UPPER_BIT = 0x8;
    public static final int OPEN_BIT = 0x4;
    public static final int DIRECTION_MASK = 0x3;

    protected DoorTile(final int id, final Material material) {
        super(id, material);
        this.tex = 97;

        if (material == Material.metal) {
            this.tex++;
        }

        final float r = 0.5f;
        final float h = 1.0f;
        this.setShape(0.5f - r, 0.0f, 0.5f - r, 0.5f + r, h, 0.5f + r);
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if (face == Facing.DOWN || face == Facing.UP) return this.tex;

        final int dir = this.getDir(data);
        if ((dir == 0 || dir == 2) ^ face <= 3) {
            return this.tex;
        }
        final int n = dir / 2 + ((face & 0x1) ^ dir) + (data & OPEN_BIT) / 4;
        int n2 = this.tex - (data & UPPER_BIT) * 2;
        if ((n & 0x1) != 0x0) {
            n2 = -n2;
        }

        return n2;
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
    public int getRenderShape() {
        return Tile.SHAPE_DOOR;
    }
    
    @Override
    public AABB getTileAABB(final Level level, final int x, final int y, final int z) {
        this.updateShape(level, x, y, z);
        return super.getTileAABB(level, x, y, z);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        this.updateShape(level, x, y, z);
        return super.getAABB(level, x, y, z);
    }
    
    @Override
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
        this.setShape(this.getDir(level.getData(x, y, z)));
    }
    
    public void setShape(final int dir) {
        final float r = 3 / 16.0f;
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f);
        if (dir == 0) this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, r);
        if (dir == 1) this.setShape(1.0f - r, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        if (dir == 2) this.setShape(0.0f, 0.0f, 1.0f - r, 1.0f, 1.0f, 1.0f);
        if (dir == 3) this.setShape(0.0f, 0.0f, 0.0f, r, 1.0f, 1.0f);
    }
    
    @Override
    public void attack(final Level level, final int x, final int y, final int z, final Player player) {
        this.use(level, x, y, z, player);
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        if (this.material == Material.metal) return true;

        final int data = level.getData(x, y, z);
        boolean isUpper = (data & UPPER_BIT) != 0x0;

        if (isUpper) {
            if (level.getTile(x, y - 1, z) == this.id) {
                this.use(level, x, y - 1, z, player);
            }
            return true;
        }

        if (level.getTile(x, y + 1, z) == this.id) {
            level.setData(x, y + 1, z, (data ^ OPEN_BIT) + 8);
        }
        level.setData(x, y, z, data ^ OPEN_BIT);
        level.setTilesDirty(x, y - 1, z, x, y, z);
        level.levelEvent(player, LevelEvent.SOUND_OPEN_DOOR, x, y, z, 0);
        return true;
    }
    
    public void setOpen(final Level level, final int x, final int y, final int z, final boolean shouldOpen) {
        final int data = level.getData(x, y, z);
        boolean isUpper = (data & UPPER_BIT) != 0x0;

        if (isUpper) {
            if (level.getTile(x, y - 1, z) == this.id) {
                this.setOpen(level, x, y - 1, z, shouldOpen);
            }
            return;
        }

        if ((level.getData(x, y, z) & OPEN_BIT) > 0 == shouldOpen) {
            return;
        }

        if (level.getTile(x, y + 1, z) == this.id) {
            level.setData(x, y + 1, z, (data ^ OPEN_BIT) + UPPER_BIT);
        }

        level.setData(x, y, z, data ^ OPEN_BIT);
        level.setTilesDirty(x, y - 1, z, x, y, z);
        level.levelEvent(null, LevelEvent.SOUND_OPEN_DOOR, x, y, z, 0);
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        final int data = level.getData(x, y, z);
        boolean isUpper = (data & UPPER_BIT) != 0x0;
        if (!isUpper) {
            boolean spawn = false;
            if (level.getTile(x, y + 1, z) != this.id) {
                level.setTile(x, y, z, 0);
                spawn = true;
            }
            if (!level.isSolidBlockingTile(x, y - 1, z)) {
                level.setTile(x, y, z, 0);
                spawn = true;
                if (level.getTile(x, y + 1, z) == this.id) {
                    level.setTile(x, y + 1, z, 0);
                }
            }
            if (spawn) {
                if (!level.isClientSide) {
                    this.spawnResources(level, x, y, z, data);
                }
            }
            else {
                if (type > 0 && Tile.tiles[type].isSignalSource()) {
                    boolean signal = level.hasNeighborSignal(x, y, z) || level.hasNeighborSignal(x, y + 1, z);
                    this.setOpen(level, x, y, z, signal);
                }
            }
        } else {
            if (level.getTile(x, y - 1, z) != this.id) {
                level.setTile(x, y, z, 0);
            }
            if (type > 0 && Tile.tiles[type].isSignalSource()) {
                this.neighborChanged(level, x, y - 1, z, type);
            }
        }
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        if ((data & UPPER_BIT) != 0x0) return 0;
        if (this.material == Material.metal) return Item.door_iron.id;
        return Item.door_wood.id;
    }
    
    @Override
    public HitResult clip(final Level level, final int xt, final int yt, final int zt, final Vec3 a, final Vec3 b) {
        this.updateShape(level, xt, yt, zt);
        return super.clip(level, xt, yt, zt, a, b);
    }
    
    public int getDir(final int data) {
        if ((data & OPEN_BIT) == 0x0) {
            return data - 1 & DIRECTION_MASK;
        }
        return data & DIRECTION_MASK;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        if (y >= Level.MAX_HEIGHT - 1) return false;

        return level.isSolidBlockingTile(x, y - 1, z) && super.mayPlace(level, x, y, z) && super.mayPlace(level, x, y + 1, z);
    }
    
    public static boolean isOpen(final int data) {
        return (data & OPEN_BIT) != 0x0;
    }
    
    @Override
    public int getPistonPushReaction() {
        return Material.PUSH_DESTROY;
    }
}
