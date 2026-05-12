// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.multiplayer;

import net.minecraft.client.title.TitleScreen;
import net.minecraft.client.gui.Button;
import net.minecraft.locale.language.Language;
import net.minecraft.world.level.Level;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;

public class ConnectScreen extends Screen
{
    private ClientConnection connection;
    private boolean aborted;
    
    public ConnectScreen(final Minecraft minecraft, final String ip, final int port) {
        this.aborted = false;
        System.out.println("Connecting to " + ip + ", " + port);
        minecraft.setLevel(null);
        new ConnectScreen_Thread(this, minecraft, ip, port).start();
    }
    
    @Override
    public void tick() {
        if (this.connection != null) {
            this.connection.tick();
        }
    }
    
    @Override
    protected void keyPressed(final char ch, final int eventKey) {
    }
    
    @Override
    public void init() {
        final Language instance = Language.getInstance();
        this.buttons.clear();
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 120 + 12, instance.getElement("gui.cancel")));
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (button.id == 0) {
            this.aborted = true;
            if (this.connection != null) {
                this.connection.close();
            }
            this.minecraft.setScreen(new TitleScreen());
        }
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.renderBackground();
        final Language instance = Language.getInstance();
        if (this.connection == null) {
            this.drawCenteredString(this.font, instance.getElement("connect.connecting"), this.width / 2, this.height / 2 - 50, 16777215);
            this.drawCenteredString(this.font, "", this.width / 2, this.height / 2 - 10, 16777215);
        }
        else {
            this.drawCenteredString(this.font, instance.getElement("connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
            this.drawCenteredString(this.font, this.connection.message, this.width / 2, this.height / 2 - 10, 16777215);
        }
        super.render(xm, ym, partialTick);
    }
}
