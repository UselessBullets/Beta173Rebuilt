// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile.entity;

import com.mojang.nbt.CompoundTag;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.SignUpdatePacket;

public class SignTileEntity extends TileEntity
{
    public static final int MAX_SIGN_LINES = 4;
    public static final int MAX_LINE_LENGTH = 15;
    public String[] messages = new String[] { "", "", "", "" };
    public int selectedLine = -1;
    private boolean isEditable = true;
    
    @Override
    public void save(final CompoundTag tag) {
        super.save(tag);
        tag.putString("Text1", this.messages[0]);
        tag.putString("Text2", this.messages[1]);
        tag.putString("Text3", this.messages[2]);
        tag.putString("Text4", this.messages[3]);
    }
    
    @Override
    public void load(final CompoundTag tag) {
        this.isEditable = false;
        super.load(tag);
        for (int i = 0; i < MAX_SIGN_LINES; ++i) {
            this.messages[i] = tag.getString("Text" + (i + 1));
            if (this.messages[i].length() > MAX_LINE_LENGTH) this.messages[i] = this.messages[i].substring(0, MAX_LINE_LENGTH);
        }
    }

    @Override
    public Packet getUpdatePacket() {
        final String[] copy = new String[MAX_SIGN_LINES];
        for (int i = 0; i < MAX_SIGN_LINES; ++i) {
            copy[i] = this.messages[i];
        }
        return new SignUpdatePacket(this.x, this.y, this.z, copy);
    }

    public boolean isEditable() {
        return this.isEditable;
    }

    public void setEditable(final boolean isEditable) {
        this.isEditable = isEditable;
    }
}
