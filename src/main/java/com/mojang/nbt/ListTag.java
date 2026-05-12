// 
// Decompiled by Procyon v0.6.0
// 

package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListTag extends Tag
{
    private List<Tag> list;
    private byte type;
    
    public ListTag() {
        this.list = new ArrayList();
    }
    
    @Override
    void write(final DataOutput dos) throws IOException {
        if (this.list.size() > 0) {
            this.type = this.list.get(0).getId();
        }
        else {
            this.type = 1;
        }
        dos.writeByte(this.type);
        dos.writeInt(this.list.size());
        for (int i = 0; i < this.list.size(); ++i) {
            ((Tag)this.list.get(i)).write(dos);
        }
    }
    
    @Override
    void load(final DataInput dis) throws IOException {
        this.type = dis.readByte();
        final int int1 = dis.readInt();
        this.list = new ArrayList();
        for (int i = 0; i < int1; ++i) {
            final Tag tag = Tag.newTag(this.type);
            tag.load(dis);
            this.list.add(tag);
        }
    }
    
    @Override
    public byte getId() {
        return TAG_List;
    }
    
    @Override
    public String toString() {
        return "" + this.list.size() + " entries of type " + Tag.getTagName(this.type);
    }
    
    public void add(final Tag tag) {
        this.type = tag.getId();
        this.list.add(tag);
    }
    
    public Tag get(final int index) {
        return this.list.get(index);
    }
    
    public int size() {
        return this.list.size();
    }
}
