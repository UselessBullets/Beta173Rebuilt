// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.gui;

import java.util.Vector;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Tickable;
import javax.swing.JList;

public class PlayerListComponent extends JList<String> implements Tickable
{
    private MinecraftServer server;
    private int tickCount = 0;
    
    public PlayerListComponent(final MinecraftServer server) {
        this.server = server;
        server.addTickable(this);
    }
    
    public void tick() {
        if (this.tickCount++ % 20 == 0) {
            final Vector<String> names = new Vector<>();

            for (int i = 0; i < this.server.players.players.size(); ++i) {
                names.add(this.server.players.players.get(i).name);
            }

            this.setListData(names);
        }
    }
}
