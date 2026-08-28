// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import com.mojang.nbt.CompoundTag;
import net.minecraft.Facing;
import net.minecraft.world.level.LevelEvent;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemInstance;
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
        if (face == Facing.UP) {
            return this.tex + 1;
        }
        return this.tex;
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        if (level.getData(x, y, z) == 0) return false;
        this.dropRecording(level, x, y, z);
        return true;
    }
    
    public void setRecord(final Level level, final int x, final int y, final int z, final int record) {
        if (level.isClientSide) return;

        final Entity rte = (Entity)level.getTileEntity(x, y, z);
        rte.record = record;
        rte.setChanged();

        level.setData(x, y, z, 1);
    }
    
    public void dropRecording(final Level level, final int x, final int y, final int z) {
        if (level.isClientSide) return;

        final Entity rte = (Entity)level.getTileEntity(x, y, z);

        final int oldRecord = rte.record;
        if (oldRecord == 0) return;

        level.levelEvent(LevelEvent.SOUND_PLAY_RECORDING, x, y, z, 0);
        level.playStreamingMusic(null, x, y, z);
        rte.record = 0;
        rte.setChanged();
        level.setData(x, y, z, 0);

        final float s = 0.7f;
        double xo = x + (level.random.nextFloat() * s + (1.0f - s) * 0.5);
        double yo = y + (level.random.nextFloat() * s + (1.0f - s) * 0.2 + 0.6);
        double zo = z + (level.random.nextFloat() * s + (1.0f - s) * 0.5);
        final ItemEntity item = new ItemEntity(level, xo, yo, zo, new ItemInstance(oldRecord, 1, 0));
        item.throwTime = 10;
        level.addEntity(item);
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        this.dropRecording(level, x, y, z);
        super.onRemove(level, x, y, z);
    }
    
    @Override
    public void spawnResources(final Level level, final int x, final int y, final int z, final int data, final float odds) {
        if (level.isClientSide) return;
        super.spawnResources(level, x, y, z, data, odds);
    }
    
    @Override
    protected TileEntity newTileEntity() {
        return new Entity();
    }

    public static class Entity extends TileEntity
    {
        public int record;

        @Override
        public void load(final CompoundTag tag) {
            super.load(tag);
            this.record = tag.getInt("Record");
        }

        @Override
        public void save(final CompoundTag tag) {
            super.save(tag);
            if (this.record > 0) tag.putInt("Record", this.record);
        }
    }
}
