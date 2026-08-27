// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.Facing;
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
        if (face == Facing.UP) return this.tex - 1;
        if (face == Facing.DOWN) return this.tex - 1;

        final int n = level.getTile(x, y, z - 1); // face = 2
        final int s = level.getTile(x, y, z + 1); // face = 3
        final int w = level.getTile(x - 1, y, z); // face = 4
        final int e = level.getTile(x + 1, y, z); // face = 5

        // Long!
        int lockDir = 4;
        if (n == this.id || s == this.id) {
            if (face == Facing.NORTH || face == Facing.SOUTH) return this.tex;

            final int w2 = level.getTile(x - 1, y, n == this.id ? z - 1 : z + 1);
            final int e2 = level.getTile(x + 1, y, n == this.id ? z - 1 : z + 1);

            lockDir = 5;

            int otherDir = 0;
            if (n == this.id) otherDir = -1;
            if (face == Facing.WEST) otherDir = -1 - otherDir;

            if ((Tile.solid[w] || Tile.solid[w2]) && !Tile.solid[e] && !Tile.solid[e2]) lockDir = 5;
            if ((Tile.solid[e] || Tile.solid[e2]) && !Tile.solid[w] && !Tile.solid[w2]) lockDir = 4;
            return (face == lockDir ? this.tex + 16 : this.tex + 32) + otherDir;
        }
        else if (w == this.id || e == this.id) {
            if (face == Facing.WEST || face == Facing.EAST) return this.tex;

            final int n2 = level.getTile(w == this.id ? x - 1 : x + 1, y, z - 1);
            final int s2 = level.getTile(w == this.id ? x - 1 : x + 1, y, z + 1);

            lockDir = 3;

            int otherDir = 0;
            if (w == this.id) otherDir = -1;
            if (face == Facing.SOUTH) otherDir = -1 - otherDir;

            if ((Tile.solid[n] || Tile.solid[n2]) && !Tile.solid[s] && !Tile.solid[s2]) lockDir = 3;
            if ((Tile.solid[s] || Tile.solid[s2]) && !Tile.solid[n] && !Tile.solid[n2]) lockDir = 2;
            return (face == lockDir ? (this.tex + 16) : (this.tex + 32)) + otherDir;
        } else {
            lockDir = 3;
            if (Tile.solid[n] && !Tile.solid[s]) lockDir = 3;
            if (Tile.solid[s] && !Tile.solid[n]) lockDir = 2;
            if (Tile.solid[w] && !Tile.solid[e]) lockDir = 5;
            if (Tile.solid[e] && !Tile.solid[w]) lockDir = 4;
            return (face == lockDir) ? (this.tex + 1) : this.tex;
        }
    }
    
    @Override
    public int getTexture(final int face) {
        if (face == Facing.UP) return this.tex - 1;
        if (face == Facing.DOWN) return this.tex - 1;
        if (face == Facing.SOUTH) return this.tex + 1;
        return this.tex;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        int chestCount = 0;

        if (level.getTile(x - 1, y, z) == this.id) ++chestCount;
        if (level.getTile(x + 1, y, z) == this.id) ++chestCount;
        if (level.getTile(x, y, z - 1) == this.id) ++chestCount;
        if (level.getTile(x, y, z + 1) == this.id) ++chestCount;

        if (chestCount > 1) return false;

        if (this.isFullChest(level, x - 1, y, z)) return false;
        if (this.isFullChest(level, x + 1, y, z)) return false;
        if (this.isFullChest(level, x, y, z - 1)) return false;
        if (this.isFullChest(level, x, y, z + 1)) return false;
        return true;
    }
    
    private boolean isFullChest(final Level level, final int x, final int y, final int z) {
        if (level.getTile(x, y, z) != this.id) return false;
        if (level.getTile(x - 1, y, z) == this.id) return true;
        if (level.getTile(x + 1, y, z) == this.id) return true;
        if (level.getTile(x, y, z - 1) == this.id) return true;
        if (level.getTile(x, y, z + 1) == this.id) return true;
        return false;
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        Container container = (ChestTileEntity)level.getTileEntity(x, y, z);
        for (int i = 0; i < container.getContainerSize(); ++i) {
            final ItemInstance item = container.getItem(i);
            if (item != null) {
                final float xo = this.random.nextFloat() * 0.8f + 0.1f;
                final float yo = this.random.nextFloat() * 0.8f + 0.1f;
                final float zo = this.random.nextFloat() * 0.8f + 0.1f;

                while (item.count > 0) {
                    int count = this.random.nextInt(21) + 10;
                    if (count > item.count) count = item.count;
                    item.count -= count;

                    ItemInstance newItem = new ItemInstance(item.id, count, item.getAuxValue());
                    final ItemEntity itemEntity = new ItemEntity(level, x + xo, y + yo, z + zo, newItem);
                    final float pow = 0.05f;
                    itemEntity.xd = (float)this.random.nextGaussian() * pow;
                    itemEntity.yd = (float)this.random.nextGaussian() * pow + 0.2f;
                    itemEntity.zd = (float)this.random.nextGaussian() * pow;
                    level.addEntity(itemEntity);
                }
            }
        }

        super.onRemove(level, x, y, z);
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        Container container = (ChestTileEntity) level.getTileEntity(x, y, z);

        if (level.isSolidBlockingTile(x, y + 1, z)) return true;

        if (level.getTile(x - 1, y, z) == this.id && level.isSolidBlockingTile(x - 1, y + 1, z)) return true;
        if (level.getTile(x + 1, y, z) == this.id && level.isSolidBlockingTile(x + 1, y + 1, z)) return true;
        if (level.getTile(x, y, z - 1) == this.id && level.isSolidBlockingTile(x, y + 1, z - 1)) return true;
        if (level.getTile(x, y, z + 1) == this.id && level.isSolidBlockingTile(x, y + 1, z + 1)) return true;

        if (level.getTile(x - 1, y, z) == this.id) container = new CompoundContainer("Large chest", (ChestTileEntity) level.getTileEntity(x - 1, y, z), container);
        if (level.getTile(x + 1, y, z) == this.id) container = new CompoundContainer("Large chest", container, (ChestTileEntity) level.getTileEntity(x + 1, y, z));
        if (level.getTile(x, y, z - 1) == this.id) container = new CompoundContainer("Large chest", (ChestTileEntity) level.getTileEntity(x, y, z - 1), container);
        if (level.getTile(x, y, z + 1) == this.id) container = new CompoundContainer("Large chest", container, (ChestTileEntity) level.getTileEntity(x, y, z + 1));

        if (level.isClientSide) return true;

        player.openContainer(container);
        
        return true;
    }
    
    @Override
    protected TileEntity newTileEntity() {
        return new ChestTileEntity();
    }
}
