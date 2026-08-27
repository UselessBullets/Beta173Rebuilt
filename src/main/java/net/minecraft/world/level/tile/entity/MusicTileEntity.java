// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile.entity;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.Level;
import com.mojang.nbt.CompoundTag;

public class MusicTileEntity extends TileEntity
{
    public byte note;
    public boolean on;
    
    public MusicTileEntity() {
        this.note = 0;

        this.on = false;
    }
    
    @Override
    public void save(final CompoundTag compoundTag) {
        super.save(compoundTag);
        compoundTag.putByte("note", this.note);
    }
    
    @Override
    public void load(final CompoundTag compoundTag) {
        super.load(compoundTag);
        this.note = compoundTag.getByte("note");
        if (this.note < 0) this.note = 0;
        if (this.note > 24) this.note = 24;
    }
    
    public void tune() {
        this.note = (byte)((this.note + 1) % 25);
        this.setChanged();
    }
    
    public void playNote(final Level level, final int x, final int y, final int z) {
        if (level.getMaterial(x, y + 1, z) != Material.air) return;

        final Material m = level.getMaterial(x, y - 1, z);
        int i = 0;
        if (m == Material.stone) i = 1;
        if (m == Material.sand) i = 2;
        if (m == Material.glass) i = 3;
        if (m == Material.wood) i = 4;

        level.tileEvent(x, y, z, i, this.note);
    }
}
