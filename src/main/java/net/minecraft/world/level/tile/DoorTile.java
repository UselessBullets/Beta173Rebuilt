// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

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
    protected DoorTile(final int id, final Material material) {
        super(id, material);
        this.tex = 97;
        if (material == Material.metal) {
            ++this.tex;
        }
        final float n = 0.5f;
        this.setShape(0.5f - n, 0.0f, 0.5f - n, 0.5f + n, 1.0f, 0.5f + n);
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if (face == 0 || face == 1) {
            return this.tex;
        }
        final int dir = this.getDir(data);
        if ((dir == 0 || dir == 2) ^ face <= 3) {
            return this.tex;
        }
        final int n = dir / 2 + ((face & 0x1) ^ dir) + (data & 0x4) / 4;
        int n2 = this.tex - (data & 0x8) * 2;
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
        return 7;
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
    
    public void setShape(final int compositeData) {
        final float n = 0.1875f;
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f);
        if (compositeData == 0) {
            this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, n);
        }
        if (compositeData == 1) {
            this.setShape(1.0f - n, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        }
        if (compositeData == 2) {
            this.setShape(0.0f, 0.0f, 1.0f - n, 1.0f, 1.0f, 1.0f);
        }
        if (compositeData == 3) {
            this.setShape(0.0f, 0.0f, 0.0f, n, 1.0f, 1.0f);
        }
    }
    
    @Override
    public void attack(final Level level, final int x, final int y, final int z, final Player player) {
        this.use(level, x, y, z, player);
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        if (this.material == Material.metal) {
            return true;
        }
        final int data = level.getData(x, y, z);
        if ((data & 0x8) != 0x0) {
            if (level.getTile(x, y - 1, z) == this.id) {
                this.use(level, x, y - 1, z, player);
            }
            return true;
        }
        if (level.getTile(x, y + 1, z) == this.id) {
            level.setData(x, y + 1, z, (data ^ 0x4) + 8);
        }
        level.setData(x, y, z, data ^ 0x4);
        level.setTilesDirty(x, y - 1, z, x, y, z);
        level.levelEvent(player, 1003, x, y, z, 0);
        return true;
    }
    
    public void setOpen(final Level level, final int x, final int y, final int z, final boolean shouldOpen) {
        final int data = level.getData(x, y, z);
        if ((data & 0x8) != 0x0) {
            if (level.getTile(x, y - 1, z) == this.id) {
                this.setOpen(level, x, y - 1, z, shouldOpen);
            }
            return;
        }
        if ((level.getData(x, y, z) & 0x4) > 0 == shouldOpen) {
            return;
        }
        if (level.getTile(x, y + 1, z) == this.id) {
            level.setData(x, y + 1, z, (data ^ 0x4) + 8);
        }
        level.setData(x, y, z, data ^ 0x4);
        level.setTilesDirty(x, y - 1, z, x, y, z);
        level.levelEvent(null, 1003, x, y, z, 0);
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        final int data = level.getData(x, y, z);
        if ((data & 0x8) != 0x0) {
            if (level.getTile(x, y - 1, z) != this.id) {
                level.setTile(x, y, z, 0);
            }
            if (type > 0 && Tile.tiles[type].isSignalSource()) {
                this.neighborChanged(level, x, y - 1, z, type);
            }
        }
        else {
            boolean b = false;
            if (level.getTile(x, y + 1, z) != this.id) {
                level.setTile(x, y, z, 0);
                b = true;
            }
            if (!level.isSolidBlockingTile(x, y - 1, z)) {
                level.setTile(x, y, z, 0);
                b = true;
                if (level.getTile(x, y + 1, z) == this.id) {
                    level.setTile(x, y + 1, z, 0);
                }
            }
            if (b) {
                if (!level.isClientSide) {
                    this.spawnResources(level, x, y, z, data);
                }
            }
            else if (type > 0 && Tile.tiles[type].isSignalSource()) {
                this.setOpen(level, x, y, z, level.hasNeighborSignal(x, y, z) || level.hasNeighborSignal(x, y + 1, z));
            }
        }
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        if ((data & 0x8) != 0x0) {
            return 0;
        }
        if (this.material == Material.metal) {
            return Item.door_iron.id;
        }
        return Item.door_wood.id;
    }
    
    @Override
    public HitResult clip(final Level level, final int xt, final int yt, final int zt, final Vec3 a, final Vec3 b) {
        this.updateShape(level, xt, yt, zt);
        return super.clip(level, xt, yt, zt, a, b);
    }
    
    public int getDir(final int data) {
        if ((data & 0x4) == 0x0) {
            return data - 1 & 0x3;
        }
        return data & 0x3;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return y < 127 && level.isSolidBlockingTile(x, y - 1, z) && super.mayPlace(level, x, y, z) && super.mayPlace(level, x, y + 1, z);
    }
    
    public static boolean isOpen(final int data) {
        return (data & 0x4) != 0x0;
    }
    
    @Override
    public int getPistonPushReaction() {
        return 1;
    }
}
