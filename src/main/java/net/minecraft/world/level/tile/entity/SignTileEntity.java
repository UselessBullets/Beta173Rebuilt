// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile.entity;

import com.mojang.nbt.CompoundTag;

public class SignTileEntity extends TileEntity
{
    public String[] messages;
    public int selectedLine;
    private boolean isEditable;
    
    public SignTileEntity() {
        this.messages = new String[] { "", "", "", "" };
        this.selectedLine = -1;
        this.isEditable = true;
    }
    
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
        for (int i = 0; i < 4; ++i) {
            this.messages[i] = compoundTag.getString("Text" + (i + 1));
            if (this.messages[i].length() > 15) {
                this.messages[i] = this.messages[i].substring(0, 15);
            }
        }
    }
}
