// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.tile.entity.DispenserTileEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import java.util.Random;

public class DispenserTile extends EntityTile
{
    private Random random;
    
    protected DispenserTile(final int id) {
        super(id, Material.stone);
        this.random = new Random();
        this.tex = 45;
    }
    
    @Override
    public int getTickDelay() {
        return 4;
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Tile.dispenser.id;
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        super.onPlace(level, x, y, z);
        this.recalcLockDir(level, x, y, z);
    }
    
    private void recalcLockDir(final Level level, final int x, final int y, final int z) {
        if (level.isClientSide) {
            return;
        }
        final int tile = level.getTile(x, y, z - 1);
        final int tile2 = level.getTile(x, y, z + 1);
        final int tile3 = level.getTile(x - 1, y, z);
        final int tile4 = level.getTile(x + 1, y, z);
        int data = 3;
        if (Tile.solid[tile] && !Tile.solid[tile2]) {
            data = 3;
        }
        if (Tile.solid[tile2] && !Tile.solid[tile]) {
            data = 2;
        }
        if (Tile.solid[tile3] && !Tile.solid[tile4]) {
            data = 5;
        }
        if (Tile.solid[tile4] && !Tile.solid[tile3]) {
            data = 4;
        }
        level.setData(x, y, z, data);
    }
    
    @Override
    public int getTexture(final LevelSource level, final int x, final int y, final int z, final int face) {
        if (face == 1) {
            return this.tex + 17;
        }
        if (face == 0) {
            return this.tex + 17;
        }
        if (face != level.getData(x, y, z)) {
            return this.tex;
        }
        return this.tex + 1;
    }
    
    @Override
    public int getTexture(final int face) {
        if (face == 1) {
            return this.tex + 17;
        }
        if (face == 0) {
            return this.tex + 17;
        }
        if (face == 3) {
            return this.tex + 1;
        }
        return this.tex;
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        if (level.isClientSide) {
            return true;
        }
        player.openTrap((DispenserTileEntity)level.getTileEntity(x, y, z));
        return true;
    }
    
    private void fireArrow(final Level level, final int x, final int y, final int z, final Random random) {
        final int data = level.getData(x, y, z);
        int n = 0;
        int n2 = 0;
        if (data == 3) {
            n2 = 1;
        }
        else if (data == 2) {
            n2 = -1;
        }
        else if (data == 5) {
            n = 1;
        }
        else {
            n = -1;
        }
        final ItemInstance removeRandomItem = ((DispenserTileEntity)level.getTileEntity(x, y, z)).removeRandomItem();
        final double n3 = x + n * 0.6 + 0.5;
        final double y2 = y + 0.5;
        final double n4 = z + n2 * 0.6 + 0.5;
        if (removeRandomItem == null) {
            level.levelEvent(1001, x, y, z, 0);
        }
        else {
            if (removeRandomItem.id == Item.arrow.id) {
                final Arrow e = new Arrow(level, n3, y2, n4);
                e.shoot(n, 0.1f, n2, 1.1f, 6.0f);
                e.pickup = true;
                level.addEntity(e);
                level.levelEvent(1002, x, y, z, 0);
            }
            else if (removeRandomItem.id == Item.egg.id) {
                final ThrownEgg e2 = new ThrownEgg(level, n3, y2, n4);
                e2.shoot(n, 0.1f, n2, 1.1f, 6.0f);
                level.addEntity(e2);
                level.levelEvent(1002, x, y, z, 0);
            }
            else if (removeRandomItem.id == Item.snowBall.id) {
                final Snowball e3 = new Snowball(level, n3, y2, n4);
                e3.shoot(n, 0.1f, n2, 1.1f, 6.0f);
                level.addEntity(e3);
                level.levelEvent(1002, x, y, z, 0);
            }
            else {
                final ItemEntity e4 = new ItemEntity(level, n3, y2 - 0.3, n4, removeRandomItem);
                final double n5 = random.nextDouble() * 0.1 + 0.2;
                e4.xd = n * n5;
                e4.yd = 0.2f;
                e4.zd = n2 * n5;
                final ItemEntity itemEntity = e4;
                itemEntity.xd += random.nextGaussian() * 0.007499999832361937 * 6.0;
                final ItemEntity itemEntity2 = e4;
                itemEntity2.yd += random.nextGaussian() * 0.007499999832361937 * 6.0;
                final ItemEntity itemEntity3 = e4;
                itemEntity3.zd += random.nextGaussian() * 0.007499999832361937 * 6.0;
                level.addEntity(e4);
                level.levelEvent(1000, x, y, z, 0);
            }
            level.levelEvent(2000, x, y, z, n + 1 + (n2 + 1) * 3);
        }
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (type > 0 && Tile.tiles[type].isSignalSource() && (level.hasNeighborSignal(x, y, z) || level.hasNeighborSignal(x, y + 1, z))) {
            level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
        }
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (level.hasNeighborSignal(x, y, z) || level.hasNeighborSignal(x, y + 1, z)) {
            this.fireArrow(level, x, y, z, random);
        }
    }
    
    @Override
    protected TileEntity newTileEntity() {
        return new DispenserTileEntity();
    }
    
    @Override
    public void setPlacedBy(final Level level, final int x, final int y, final int z, final Mob by) {
        final int n = Mth.floor(by.yRot * 4.0f / 360.0f + 0.5) & 0x3;
        if (n == 0) {
            level.setData(x, y, z, 2);
        }
        if (n == 1) {
            level.setData(x, y, z, 5);
        }
        if (n == 2) {
            level.setData(x, y, z, 3);
        }
        if (n == 3) {
            level.setData(x, y, z, 4);
        }
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        final DispenserTileEntity dispenserTileEntity = (DispenserTileEntity)level.getTileEntity(x, y, z);
        for (int i = 0; i < dispenserTileEntity.getContainerSize(); ++i) {
            final ItemInstance item = dispenserTileEntity.getItem(i);
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
}
