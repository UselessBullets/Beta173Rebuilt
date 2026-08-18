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
    public String[] messages = new String[] { "", "", "", "" };
    public int selectedLine = -1;
    private boolean isEditable = true;
    
    @Override
    public void save(final CompoundTag compoundTag) {
        super.save(compoundTag);
        compoundTag.putString("Text1", this.messages[0]);
        compoundTag.putString("Text2", this.messages[1]);
        compoundTag.putString("Text3", this.messages[2]);
        compoundTag.putString("Text4", this.messages[3]);
    }
    
    @Override
    public void load(final CompoundTag compoundTag) {
        this.isEditable = false;
        super.load(compoundTag);
        for (int i = 0; i < MAX_SIGN_LINES; ++i) {
            this.messages[i] = compoundTag.getString("Text" + (i + 1));
            if (this.messages[i].length() > 15) {
                this.messages[i] = this.messages[i].substring(0, 15);
            }
        }
    }

    @Override
    public Packet getUpdatePacket() {
        final String[] lines = new String[MAX_SIGN_LINES];
        for (int i = 0; i < MAX_SIGN_LINES; ++i) {
            lines[i] = this.messages[i];
        }
        return new SignUpdatePacket(this.x, this.y, this.z, lines);
    }

    public boolean isEditable() {
        return this.isEditable;
    }

    public void setEditable(final boolean isEditable) {
        this.isEditable = isEditable;
    }
}
