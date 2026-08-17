// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.entity.Entity;

public interface LevelListener
{
    int SOUND_CLICK = 1000;
    int SOUND_CLICK_FAIL = 1001;
    int SOUND_LAUNCH = 1002;
    int SOUND_OPEN_DOOR = 1003;
    int SOUND_FIZZ = 1004;
    int SOUND_PLAY_RECORDING = 1005;

    int SOUND_GHAST_WARNING = 1007;

    int PARTICLES_SHOOT = 2000;
    int PARTICLES_DESTROY_BLOCK = 2001;

    void tileChanged(final int x, final int y, final int z);
    
    void setTilesDirty(final int x0, final int y0, final int z0, final int x1, final int y1, final int z1);
    
    void playSound(final String name, final double x, final double y, final double z, final float volume, final float pitch);
    
    void addParticle(final String name, final double x, final double y, final double z, final double xa, final double ya, final double za);
    
    void entityAdded(final Entity entity);
    
    void entityRemoved(final Entity entity);
    
    void skyColorChanged();
    
    void playStreamingMusic(final String name, final int x, final int y, final int z);
    
    void tileEntityChanged(final int x, final int y, final int z, final TileEntity te);
    
    void levelEvent(final Player source, final int type, final int x, final int y, final int z, final int data);
}
