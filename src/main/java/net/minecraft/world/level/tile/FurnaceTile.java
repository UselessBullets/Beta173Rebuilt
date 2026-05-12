// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemInstance;
import util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.tile.entity.FurnaceTileEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import java.util.Random;

public class FurnaceTile extends EntityTile
{
    private Random random;
    private final boolean lit;
    private static boolean noDrop;
    
    protected FurnaceTile(final int id, final boolean lit) {
        super(id, Material.stone);
        this.random = new Random();
        this.lit = lit;
        this.tex = 45;
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Tile.furnace.id;
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        super.onPlace(level, x, y, z);
        this.recalcLookDir(level, x, y, z);
    }
    
    private void recalcLookDir(final Level level, final int x, final int y, final int z) {
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
        if (this.lit) {
            return this.tex + 16;
        }
        return this.tex - 1;
    }
    
    @Override
    public void animateTick(final Level level, final int x, final int y, final int z, final Random random) {
        if (!this.lit) {
            return;
        }
        final int data = level.getData(x, y, z);
        final float n = x + 0.5f;
        final float n2 = y + 0.0f + random.nextFloat() * 6.0f / 16.0f;
        final float n3 = z + 0.5f;
        final float n4 = 0.52f;
        final float n5 = random.nextFloat() * 0.6f - 0.3f;
        if (data == 4) {
            level.addParticle("smoke", n - n4, n2, n3 + n5, 0.0, 0.0, 0.0);
            level.addParticle("flame", n - n4, n2, n3 + n5, 0.0, 0.0, 0.0);
        }
        else if (data == 5) {
            level.addParticle("smoke", n + n4, n2, n3 + n5, 0.0, 0.0, 0.0);
            level.addParticle("flame", n + n4, n2, n3 + n5, 0.0, 0.0, 0.0);
        }
        else if (data == 2) {
            level.addParticle("smoke", n + n5, n2, n3 - n4, 0.0, 0.0, 0.0);
            level.addParticle("flame", n + n5, n2, n3 - n4, 0.0, 0.0, 0.0);
        }
        else if (data == 3) {
            level.addParticle("smoke", n + n5, n2, n3 + n4, 0.0, 0.0, 0.0);
            level.addParticle("flame", n + n5, n2, n3 + n4, 0.0, 0.0, 0.0);
        }
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
            return this.tex - 1;
        }
        return this.tex;
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        if (level.isClientSide) {
            return true;
        }
        player.openFurnace((FurnaceTileEntity)level.getTileEntity(x, y, z));
        return true;
    }
    
    public static void setLit(final boolean lit, final Level level, final int x, final int y, final int z) {
        final int data = level.getData(x, y, z);
        final TileEntity tileEntity = level.getTileEntity(x, y, z);
        FurnaceTile.noDrop = true;
        if (lit) {
            level.setTile(x, y, z, Tile.furnace_lit.id);
        }
        else {
            level.setTile(x, y, z, Tile.furnace.id);
        }
        FurnaceTile.noDrop = false;
        level.setData(x, y, z, data);
        tileEntity.clearRemoved();
        level.setTileEntity(x, y, z, tileEntity);
    }
    
    @Override
    protected TileEntity newTileEntity() {
        return new FurnaceTileEntity();
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
        if (!FurnaceTile.noDrop) {
            final FurnaceTileEntity furnaceTileEntity = (FurnaceTileEntity)level.getTileEntity(x, y, z);
            for (int i = 0; i < furnaceTileEntity.getContainerSize(); ++i) {
                final ItemInstance item = furnaceTileEntity.getItem(i);
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
        }
        super.onRemove(level, x, y, z);
    }
    
    static {
        FurnaceTile.noDrop = false;
    }
}
