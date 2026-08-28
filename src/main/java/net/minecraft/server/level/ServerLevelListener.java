// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.level;

import net.minecraft.network.packet.LevelEventPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelListener;

public class ServerLevelListener implements LevelListener
{
    private MinecraftServer server;
    private ServerLevel level;
    
    public ServerLevelListener(final MinecraftServer server, final ServerLevel level) {
        this.server = server;
        this.level = level;
    }
    
    public void addParticle(final String name, final double x, final double y, final double z, final double xa, final double ya, final double za) {
    }
    
    public void entityAdded(final Entity entity) {
        this.server.getTracker(this.level.dimension.id).addEntity(entity);
    }
    
    public void entityRemoved(final Entity entity) {
        this.server.getTracker(this.level.dimension.id).removePlayer(entity);
    }
    
    public void playSound(final String name, final double x, final double y, final double z, final float volume, final float pitch) {
    }
    
    public void setTilesDirty(final int x0, final int y0, final int z0, final int x1, final int y1, final int z1) {
    }

    @Override
    // Useless - Exists here in b1.2 and LCE leaks
    public void allChanged() {

    }

    public void skyColorChanged() {
    }
    
    public void tileChanged(final int x, final int y, final int z) {
        this.server.players.isTrackingTile(x, y, z, this.level.dimension.id);
    }
    
    public void playStreamingMusic(final String name, final int x, final int y, final int z) {
    }
    
    public void tileEntityChanged(final int x, final int y, final int z, final TileEntity te) {
        this.server.players.isTrackingTileEntity(x, y, z, te);
    }
    
    public void levelEvent(final Player source, final int type, final int x, final int y, final int z, final int data) {
        this.server.players.broadcast(source, x, y, z, 64.0, this.level.dimension.id, new LevelEventPacket(type, x, y, z, data));
    }
}
