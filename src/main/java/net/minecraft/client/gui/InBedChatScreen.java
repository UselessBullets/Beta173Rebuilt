// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.network.packet.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.packet.PlayerCommandPacket;
import net.minecraft.client.multiplayer.MultiplayerLocalPlayer;
import net.minecraft.locale.language.Language;
import org.lwjgl.input.Keyboard;

public class InBedChatScreen extends ChatScreen
{
    @Override
    public void init() {
        Keyboard.enableRepeatEvents(true);
        this.buttons.add(new Button(1, this.width / 2 - 100, this.height - 40, Language.getInstance().getElement("multiplayer.stopSleeping")));
    }
    
    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    protected void keyPressed(final char ch, final int eventKey) {
        if (eventKey == 1) {
            this.sendWakeUp();
        }
        else if (eventKey == 28) {
            if (this.message.trim().length() > 0) {
                this.minecraft.player.chat(this.message.trim());
            }
            this.message = "";
        }
        else {
            super.keyPressed(ch, eventKey);
        }
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        super.render(xm, ym, partialTick);
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (button.id == 1) {
            this.sendWakeUp();
        }
        else {
            super.buttonClicked(button);
        }
    }
    
    private void sendWakeUp() {
        if (this.minecraft.player instanceof MultiplayerLocalPlayer) {
            ((MultiplayerLocalPlayer)this.minecraft.player).connection.send(new PlayerCommandPacket(this.minecraft.player, 3));
        }
    }
}
