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
