// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.gui;

import net.minecraft.server.level.ServerPlayer;
import java.util.Vector;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Tickable;
import javax.swing.JList;

public class PlayerListComponent extends JList implements Tickable
{
    private MinecraftServer server;
    private int tick;
    
    public PlayerListComponent(final MinecraftServer server) {
        this.tick = 0;
        (this.server = server).addTickable(this);
    }
    
    public void tick() {
        if (this.tick++ % 20 == 0) {
            final Vector listData = new Vector();
            for (int i = 0; i < this.server.players.players.size(); ++i) {
                listData.add(((ServerPlayer)this.server.players.players.get(i)).name);
            }
            this.setListData(listData);
        }
    }
}
