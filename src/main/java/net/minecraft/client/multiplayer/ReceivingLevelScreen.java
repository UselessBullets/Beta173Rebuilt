// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.multiplayer;

import net.minecraft.locale.language.Language;
import net.minecraft.client.gui.Button;
import net.minecraft.network.packet.KeepAlivePacket;
import net.minecraft.client.gui.Screen;

public class ReceivingLevelScreen extends Screen
{
    private ClientConnection connection;
    private int tickCount = 0;
    
    public ReceivingLevelScreen(final ClientConnection connection) {
        this.connection = connection;
    }
    
    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
    }
    
    @Override
    public void init() {
        this.buttons.clear();
    }
    
    @Override
    public void tick() {
        ++this.tickCount;
        if (this.tickCount % 20 == 0) {
            this.connection.send(new KeepAlivePacket());
        }
        if (this.connection != null) {
            this.connection.tick();
        }
    }
    
    @Override
    protected void buttonClicked(final Button button) {
    }
    
    @Override
    public void render(final int xm, final int ym, final float a) {
        this.renderDirtBackground(0);

        Language language = Language.getInstance();

        this.drawCenteredString(this.font, language.getElement("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 0xffffff);

        super.render(xm, ym, a);
    }
}
