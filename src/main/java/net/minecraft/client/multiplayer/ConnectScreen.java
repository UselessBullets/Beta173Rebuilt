// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.multiplayer;

import net.minecraft.client.title.TitleScreen;
import net.minecraft.client.gui.Button;
import net.minecraft.locale.language.Language;
import net.minecraft.network.packet.PreLoginPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;

import java.net.ConnectException;
import java.net.UnknownHostException;

public class ConnectScreen extends Screen
{
    private ClientConnection connection;
    private boolean aborted = false;
    
    public ConnectScreen(final Minecraft minecraft, final String ip, final int port) {
        System.out.println("Connecting to " + ip + ", " + port);
        minecraft.setLevel(null);
        new Thread(() -> {
            try {
                this.connection = new ClientConnection(minecraft, ip, port);
                if (this.aborted) return;
                this.connection.send(new PreLoginPacket(minecraft.user.name));
            }
            catch (final UnknownHostException e) {
                if (this.aborted) return;
                minecraft.setScreen(new DisconnectedScreen("connect.failed", "disconnect.genericReason", "Unknown host '" + ip + "'"));
            }
            catch (final ConnectException e) {
                if (this.aborted) return;
                minecraft.setScreen(new DisconnectedScreen("connect.failed", "disconnect.genericReason", e.getMessage()));
            }
            catch (final Exception e) {
                if (this.aborted) return;
                e.printStackTrace();
                minecraft.setScreen(new DisconnectedScreen("connect.failed", "disconnect.genericReason", e.toString()));
            }
        }).start();
    }
    
    @Override
    public void tick() {
        if (this.connection != null) {
            this.connection.tick();
        }
    }
    
    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
    }
    
    @Override
    public void init() {
        final Language instance = Language.getInstance();

        this.buttons.clear();
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 24 * 5 + 12, instance.getElement("gui.cancel")));
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (button.id == 0) {
            this.aborted = true;
            if (this.connection != null) this.connection.close();
            this.minecraft.setScreen(new TitleScreen());
        }
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.renderBackground();
        final Language language = Language.getInstance();

        if (this.connection == null) {
            this.drawCenteredString(this.font, language.getElement("connect.connecting"), this.width / 2, this.height / 2 - 50, 0xffffff);
            this.drawCenteredString(this.font, "", this.width / 2, this.height / 2 - 10, 0xffffff);
        }
        else {
            this.drawCenteredString(this.font, language.getElement("connect.authorizing"), this.width / 2, this.height / 2 - 50, 0xffffff);
            this.drawCenteredString(this.font, this.connection.message, this.width / 2, this.height / 2 - 10, 0xffffff);
        }

        super.render(xm, ym, partialTick);
    }
}
