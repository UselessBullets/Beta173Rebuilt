// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.multiplayer;

import java.net.ConnectException;
import java.net.UnknownHostException;
import net.minecraft.client.gui.Screen;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PreLoginPacket;
import net.minecraft.client.Minecraft;

class ConnectScreen_Thread extends Thread
{
    final /* synthetic */ Minecraft minecraft;
    final /* synthetic */ String ip;
    final /* synthetic */ int port;
    final /* synthetic */ ConnectScreen connectScreen;
    
    ConnectScreen_Thread(final ConnectScreen connectScreen, final Minecraft minecraft, final String ip, final int port) {
        this.connectScreen = connectScreen;
        this.minecraft = minecraft;
        this.ip = ip;
        this.port = port;
    }
    
    @Override
    public void run() {
        try {
            this.connectScreen.connection = new ClientConnection(this.minecraft, this.ip, this.port);
            if (this.connectScreen.aborted) {
                return;
            }
            this.connectScreen.connection.send(new PreLoginPacket(this.minecraft.user.name));
        }
        catch (final UnknownHostException ex) {
            if (this.connectScreen.aborted) {
                return;
            }
            this.minecraft.setScreen(new DisconnectedScreen("connect.failed", "disconnect.genericReason", new Object[] { "Unknown host '" + this.ip + "'" }));
        }
        catch (final ConnectException ex2) {
            if (this.connectScreen.aborted) {
                return;
            }
            this.minecraft.setScreen(new DisconnectedScreen("connect.failed", "disconnect.genericReason", new Object[] { ex2.getMessage() }));
        }
        catch (final Exception ex3) {
            if (this.connectScreen.aborted) {
                return;
            }
            ex3.printStackTrace();
            this.minecraft.setScreen(new DisconnectedScreen("connect.failed", "disconnect.genericReason", new Object[] { ex3.toString() }));
        }
    }
}
