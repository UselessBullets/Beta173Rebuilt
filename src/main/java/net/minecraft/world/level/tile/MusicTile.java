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
            final boolean signal = level.hasDirectSignal(x, y, z);
            final MusicTileEntity mte = (MusicTileEntity)level.getTileEntity(x, y, z);
            if (mte.on != signal) {
                if (signal) {
                    mte.playNote(level, x, y, z);
                }
                mte.on = signal;
            }
        }
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        if (level.isClientSide) return true;

        final MusicTileEntity mte = (MusicTileEntity)level.getTileEntity(x, y, z);
        mte.tune();
        mte.playNote(level, x, y, z);
        return true;
    }
    
    @Override
    public void attack(final Level level, final int x, final int y, final int z, final Player player) {
        if (level.isClientSide) return;
        MusicTileEntity mte = (MusicTileEntity) level.getTileEntity(x, y, z);
        mte.playNote(level, x, y, z);
    }
    
    @Override
    protected TileEntity newTileEntity() {
        return new MusicTileEntity();
    }
    
    @Override
    public void triggerEvent(final Level level, final int x, final int y, final int z, final int i, final int note) {
        final float pitch = (float)Math.pow(2.0, (note - 12) / 12.0);

        String sound = "harp";
        if (i == 1) sound = "bd";
        if (i == 2) sound = "snare";
        if (i == 3) sound = "hat";
        if (i == 4) sound = "bassattack";

        level.playSound(x + 0.5, y + 0.5, z + 0.5, "note." + sound, 3.0f, pitch);
        level.addParticle("note", x + 0.5, y + 1.2, z + 0.5, note / 24.0, 0.0, 0.0);
    }
}
