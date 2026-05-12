// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.tile.entity.RecordPlayerTileEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class RecordPlayerTile extends EntityTile
{
    protected RecordPlayerTile(final int id, final int tex) {
        super(id, tex, Material.wood);
    }
    
    @Override
    public int getTexture(final int face) {
        return this.tex + ((face == 1) ? 1 : 0);
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        if (level.getData(x, y, z) == 0) {
            return false;
        }
        this.dropRecording(level, x, y, z);
        return true;
    }
    
    public void setRecord(final Level level, final int x, final int y, final int z, final int record) {
        if (level.isClientSide) {
            return;
        }
        final RecordPlayerTileEntity recordPlayerTileEntity = (RecordPlayerTileEntity)level.getTileEntity(x, y, z);
        recordPlayerTileEntity.record = record;
        recordPlayerTileEntity.setChanged();
        level.setData(x, y, z, 1);
    }
    
    public void dropRecording(final Level level, final int x, final int y, final int z) {
        if (level.isClientSide) {
            return;
        }
        final RecordPlayerTileEntity recordPlayerTileEntity = (RecordPlayerTileEntity)level.getTileEntity(x, y, z);
        final int record = recordPlayerTileEntity.record;
        if (record == 0) {
            return;
        }
        level.levelEvent(1005, x, y, z, 0);
        level.playStreamingMusic(null, x, y, z);
        recordPlayerTileEntity.record = 0;
        recordPlayerTileEntity.setChanged();
        level.setData(x, y, z, 0);
        final int id = record;
        final float n = 0.7f;
        final ItemEntity e = new ItemEntity(level, x + (level.random.nextFloat() * n + (1.0f - n) * 0.5), y + (level.random.nextFloat() * n + (1.0f - n) * 0.2 + 0.6), z + (level.random.nextFloat() * n + (1.0f - n) * 0.5), new ItemInstance(id, 1, 0));
        e.throwTime = 10;
        level.addEntity(e);
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        this.dropRecording(level, x, y, z);
        super.onRemove(level, x, y, z);
    }
    
    @Override
    public void spawnResources(final Level level, final int x, final int y, final int z, final int data, final float odds) {
        if (level.isClientSide) {
            return;
        }
        super.spawnResources(level, x, y, z, data, odds);
    }
    
    @Override
    protected TileEntity newTileEntity() {
        return new RecordPlayerTileEntity();
    }
}
