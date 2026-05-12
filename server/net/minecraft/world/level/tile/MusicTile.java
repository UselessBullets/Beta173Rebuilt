// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.tile.entity.MusicTileEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class MusicTile extends EntityTile
{
    public MusicTile(final int id) {
        super(id, 74, Material.wood);
    }
    
    @Override
    public int getTexture(final int face) {
        return this.tex;
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (type > 0 && Tile.tiles[type].isSignalSource()) {
            final boolean hasDirectSignal = level.hasDirectSignal(x, y, z);
            final MusicTileEntity musicTileEntity = (MusicTileEntity)level.getTileEntity(x, y, z);
            if (musicTileEntity.on != hasDirectSignal) {
                if (hasDirectSignal) {
                    musicTileEntity.playNote(level, x, y, z);
                }
                musicTileEntity.on = hasDirectSignal;
            }
        }
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        if (level.isClientSide) {
            return true;
        }
        final MusicTileEntity musicTileEntity = (MusicTileEntity)level.getTileEntity(x, y, z);
        musicTileEntity.tune();
        musicTileEntity.playNote(level, x, y, z);
        return true;
    }
    
    @Override
    public void attack(final Level level, final int x, final int y, final int z, final Player player) {
        if (level.isClientSide) {
            return;
        }
        ((MusicTileEntity)level.getTileEntity(x, y, z)).playNote(level, x, y, z);
    }
    
    @Override
    protected TileEntity newTileEntity() {
        return new MusicTileEntity();
    }
    
    @Override
    public void triggerEvent(final Level level, final int x, final int y, final int z, final int b0, final int b1) {
        final float pitch = (float)Math.pow(2.0, (b1 - 12) / 12.0);
        String str = "harp";
        if (b0 == 1) {
            str = "bd";
        }
        if (b0 == 2) {
            str = "snare";
        }
        if (b0 == 3) {
            str = "hat";
        }
        if (b0 == 4) {
            str = "bassattack";
        }
        level.playLocalSound(x + 0.5, y + 0.5, z + 0.5, "note." + str, 3.0f, pitch);
        level.addParticle("note", x + 0.5, y + 1.2, z + 0.5, b1 / 24.0, 0.0, 0.0);
    }
}
