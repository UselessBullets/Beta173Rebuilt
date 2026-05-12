// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.Container;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.tile.entity.ChestTileEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import java.util.Random;

public class ChestTile extends EntityTile
{
    private Random random;
    
    protected ChestTile(final int id) {
        super(id, Material.wood);
        this.random = new Random();
        this.tex = 26;
    }
    
    @Override
    public int getTexture(final LevelSource level, final int x, final int y, final int z, final int face) {
        if (face == 1) {
            return this.tex - 1;
        }
        if (face == 0) {
            return this.tex - 1;
        }
        final int tile = level.getTile(x, y, z - 1);
        final int tile2 = level.getTile(x, y, z + 1);
        final int tile3 = level.getTile(x - 1, y, z);
        final int tile4 = level.getTile(x + 1, y, z);
        if (tile == this.id || tile2 == this.id) {
            if (face == 2 || face == 3) {
                return this.tex;
            }
            int n = 0;
            if (tile == this.id) {
                n = -1;
            }
            final int tile5 = level.getTile(x - 1, y, (tile == this.id) ? (z - 1) : (z + 1));
            final int tile6 = level.getTile(x + 1, y, (tile == this.id) ? (z - 1) : (z + 1));
            if (face == 4) {
                n = -1 - n;
            }
            int n2 = 5;
            if ((Tile.solid[tile3] || Tile.solid[tile5]) && !Tile.solid[tile4] && !Tile.solid[tile6]) {
                n2 = 5;
            }
            if ((Tile.solid[tile4] || Tile.solid[tile6]) && !Tile.solid[tile3] && !Tile.solid[tile5]) {
                n2 = 4;
            }
            return ((face == n2) ? (this.tex + 16) : (this.tex + 32)) + n;
        }
        else {
            if (tile3 != this.id && tile4 != this.id) {
                int n3 = 3;
                if (Tile.solid[tile] && !Tile.solid[tile2]) {
                    n3 = 3;
                }
                if (Tile.solid[tile2] && !Tile.solid[tile]) {
                    n3 = 2;
                }
                if (Tile.solid[tile3] && !Tile.solid[tile4]) {
                    n3 = 5;
                }
                if (Tile.solid[tile4] && !Tile.solid[tile3]) {
                    n3 = 4;
                }
                return (face == n3) ? (this.tex + 1) : this.tex;
            }
            if (face == 4 || face == 5) {
                return this.tex;
            }
            int n4 = 0;
            if (tile3 == this.id) {
                n4 = -1;
            }
            final int tile7 = level.getTile((tile3 == this.id) ? (x - 1) : (x + 1), y, z - 1);
            final int tile8 = level.getTile((tile3 == this.id) ? (x - 1) : (x + 1), y, z + 1);
            if (face == 3) {
                n4 = -1 - n4;
            }
            int n5 = 3;
            if ((Tile.solid[tile] || Tile.solid[tile7]) && !Tile.solid[tile2] && !Tile.solid[tile8]) {
                n5 = 3;
            }
            if ((Tile.solid[tile2] || Tile.solid[tile8]) && !Tile.solid[tile] && !Tile.solid[tile7]) {
                n5 = 2;
            }
            return ((face == n5) ? (this.tex + 16) : (this.tex + 32)) + n4;
        }
    }
    
    @Override
    public int getTexture(final int face) {
        if (face == 1) {
            return this.tex - 1;
        }
        if (face == 0) {
            return this.tex - 1;
        }
        if (face == 3) {
            return this.tex + 1;
        }
        return this.tex;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        int n = 0;
        if (level.getTile(x - 1, y, z) == this.id) {
            ++n;
        }
        if (level.getTile(x + 1, y, z) == this.id) {
            ++n;
        }
        if (level.getTile(x, y, z - 1) == this.id) {
            ++n;
        }
        if (level.getTile(x, y, z + 1) == this.id) {
            ++n;
        }
        return n <= 1 && !this.isFullChest(level, x - 1, y, z) && !this.isFullChest(level, x + 1, y, z) && !this.isFullChest(level, x, y, z - 1) && !this.isFullChest(level, x, y, z + 1);
    }
    
    private boolean isFullChest(final Level level, final int x, final int y, final int z) {
        return level.getTile(x, y, z) == this.id && (level.getTile(x - 1, y, z) == this.id || level.getTile(x + 1, y, z) == this.id || level.getTile(x, y, z - 1) == this.id || level.getTile(x, y, z + 1) == this.id);
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        final ChestTileEntity chestTileEntity = (ChestTileEntity)level.getTileEntity(x, y, z);
        for (int i = 0; i < chestTileEntity.getContainerSize(); ++i) {
            final ItemInstance item = chestTileEntity.getItem(i);
            if (item != null) {
                final float n = this.random.nextFloat() * 0.8f + 0.1f;
                final float n2 = this.random.nextFloat() * 0.8f + 0.1f;
                final float n3 = this.random.nextFloat() * 0.8f + 0.1f;
                while (item.count > 0) {
                    int count = this.random.nextInt(21) + 10;
                    if (count > item.count) {
                        count = item.count;
                    }
                    final ItemInstance itemInstance = item;
                    itemInstance.count -= count;
                    final ItemEntity e = new ItemEntity(level, x + n, y + n2, z + n3, new ItemInstance(item.id, count, item.getAuxValue()));
                    final float n4 = 0.05f;
                    e.xd = (float)this.random.nextGaussian() * n4;
                    e.yd = (float)this.random.nextGaussian() * n4 + 0.2f;
                    e.zd = (float)this.random.nextGaussian() * n4;
                    level.addEntity(e);
                }
            }
        }
        super.onRemove(level, x, y, z);
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        Object container = level.getTileEntity(x, y, z);
        if (level.isSolidBlockingTile(x, y + 1, z)) {
            return true;
        }
        if (level.getTile(x - 1, y, z) == this.id && level.isSolidBlockingTile(x - 1, y + 1, z)) {
            return true;
        }
        if (level.getTile(x + 1, y, z) == this.id && level.isSolidBlockingTile(x + 1, y + 1, z)) {
            return true;
        }
        if (level.getTile(x, y, z - 1) == this.id && level.isSolidBlockingTile(x, y + 1, z - 1)) {
            return true;
        }
        if (level.getTile(x, y, z + 1) == this.id && level.isSolidBlockingTile(x, y + 1, z + 1)) {
            return true;
        }
        if (level.getTile(x - 1, y, z) == this.id) {
            container = new CompoundContainer("Large chest", (Container)level.getTileEntity(x - 1, y, z), (Container)container);
        }
        if (level.getTile(x + 1, y, z) == this.id) {
            container = new CompoundContainer("Large chest", (Container)container, (Container)level.getTileEntity(x + 1, y, z));
        }
        if (level.getTile(x, y, z - 1) == this.id) {
            container = new CompoundContainer("Large chest", (Container)level.getTileEntity(x, y, z - 1), (Container)container);
        }
        if (level.getTile(x, y, z + 1) == this.id) {
            container = new CompoundContainer("Large chest", (Container)container, (Container)level.getTileEntity(x, y, z + 1));
        }
        if (level.isClientSide) {
            return true;
        }
        player.openContainer((Container)container);
        return true;
    }
    
    @Override
    protected TileEntity newTileEntity() {
        return new ChestTileEntity();
    }
}
