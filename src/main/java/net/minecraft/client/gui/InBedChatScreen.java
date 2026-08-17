// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.client.multiplayer.ClientConnection;
import net.minecraft.network.packet.PlayerCommandPacket;
import net.minecraft.client.multiplayer.MultiplayerLocalPlayer;
import net.minecraft.locale.language.Language;
import org.lwjgl.input.Keyboard;

public class InBedChatScreen extends ChatScreen
{
    private static final int WAKE_UP_BUTTON = 1;
    @Override
    public void init() {
        Keyboard.enableRepeatEvents(true);
        Language language = Language.getInstance();
        this.buttons.add(new Button(WAKE_UP_BUTTON, this.width / 2 - 100, this.height - 40, language.getElement("multiplayer.stopSleeping")));
    }
    
    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
        if (eventKey == Keyboard.KEY_ESCAPE) {
            this.sendWakeUp();
        }
        else if (eventKey == Keyboard.KEY_RETURN) {
            if (this.message.trim().length() > 0) {
                this.minecraft.player.chat(this.message.trim());
            }
            this.message = "";
        }
        else {
            super.keyPressed(eventCharacter, eventKey);
        }
    }
    
    @Override
    public void render(final int xm, final int ym, final float a) {
        super.render(xm, ym, a);
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (button.id == WAKE_UP_BUTTON) {
            this.sendWakeUp();
        }
        else {
            super.buttonClicked(button);
        }
    }
    
    private void sendWakeUp() {
        if (this.minecraft.player instanceof MultiplayerLocalPlayer) {
            ClientConnection connection = ((MultiplayerLocalPlayer)this.minecraft.player).connection;
            connection.send(new PlayerCommandPacket(this.minecraft.player, PlayerCommandPacket.STOP_SLEEPING));
        }
    }
}
