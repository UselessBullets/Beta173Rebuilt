// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.network;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import net.minecraft.network.packet.LoginPacket;

class PendingConnection_VerifyUserThread extends Thread
{
    final /* synthetic */ LoginPacket lp;
    final /* synthetic */ PendingConnection pc;
    
    PendingConnection_VerifyUserThread(final PendingConnection pendingConnection, final LoginPacket loginPacket) {
        this.pc = pendingConnection;
        this.lp = loginPacket;
    }
    
    @Override
    public void run() {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL("http://www.minecraft.net/game/checkserver.jsp?user=" + URLEncoder.encode(this.lp.userName, "UTF-8") + "&serverId=" + URLEncoder.encode(this.pc.loginKey, "UTF-8")).openStream()));
            final String line = bufferedReader.readLine();
            bufferedReader.close();
            if (line.equals("YES")) {
                this.pc.acceptedLogin = this.lp;
            }
            else {
                this.pc.disconnect("Failed to verify username!");
            }
        }
        catch (final Exception obj) {
            this.pc.disconnect("Failed to verify username! [internal error " + obj + "]");
            obj.printStackTrace();
        }
    }
}
