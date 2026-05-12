// 
// Decompiled by Procyon v0.6.0
// 

package com.mojang.nbt;

import java.util.Collection;
import java.io.DataInput;
import java.util.Iterator;
import java.io.DataOutput;
import java.util.HashMap;
import java.util.Map;

public class CompoundTag extends Tag
{
    private Map tags;
    
    public CompoundTag() {
        this.tags = new HashMap();
    }
    
    @Override
    void write(final DataOutput dos) {
        final Iterator iterator = this.tags.values().iterator();
        while (iterator.hasNext()) {
            Tag.writeNamedTag((Tag)iterator.next(), dos);
        }
        dos.writeByte(0);
    }
    
    @Override
    void load(final DataInput dis) {
        this.tags.clear();
        Tag namedTag;
        while ((namedTag = Tag.readNamedTag(dis)).getId() != 0) {
            this.tags.put(namedTag.getName(), namedTag);
        }
    }
    
    public Collection getAllTags() {
        return this.tags.values();
    }
    
    @Override
    public byte getId() {
        return 10;
    }
    
    public void put(final String name, final Tag tag) {
        this.tags.put(name, tag.setName(name));
    }
    
    public void putByte(final String name, final byte value) {
        this.tags.put(name, new ByteTag(value).setName(name));
    }
    
    public void putShort(final String name, final short value) {
        this.tags.put(name, new ShortTag(value).setName(name));
    }
    
    public void putInt(final String name, final int value) {
        this.tags.put(name, new IntTag(value).setName(name));
    }
    
    public void putLong(final String name, final long value) {
        this.tags.put(name, new LongTag(value).setName(name));
    }
    
    public void putFloat(final String name, final float value) {
        this.tags.put(name, new FloatTag(value).setName(name));
    }
    
    public void putDouble(final String name, final double value) {
        this.tags.put(name, new DoubleTag(value).setName(name));
    }
    
    public void putString(final String name, final String value) {
        this.tags.put(name, new StringTag(value).setName(name));
    }
    
    public void putByteArray(final String name, final byte[] value) {
        this.tags.put(name, new ByteArrayTag(value).setName(name));
    }
    
    public void putCompound(final String name, final CompoundTag value) {
        this.tags.put(name, value.setName(name));
    }
    
    public void putBoolean(final String name, final boolean value) {
        this.putByte(name, (byte)(value ? 1 : 0));
    }
    
    public boolean contains(final String name) {
        return this.tags.containsKey(name);
    }
    
    public byte getByte(final String name) {
        if (!this.tags.containsKey(name)) {
            return 0;
        }
        return this.tags.get(name).data;
    }
    
    public short getShort(final String name) {
        if (!this.tags.containsKey(name)) {
            return 0;
        }
        return this.tags.get(name).data;
    }
    
    public int getInt(final String name) {
        if (!this.tags.containsKey(name)) {
            return 0;
        }
        return this.tags.get(name).data;
    }
    
    public long getLong(final String name) {
        if (!this.tags.containsKey(name)) {
            return 0L;
        }
        return this.tags.get(name).data;
    }
    
    public float getFloat(final String name) {
        if (!this.tags.containsKey(name)) {
            return 0.0f;
        }
        return this.tags.get(name).data;
    }
    
    public double getDouble(final String name) {
        if (!this.tags.containsKey(name)) {
            return 0.0;
        }
        return this.tags.get(name).data;
    }
    
    public String getString(final String name) {
        if (!this.tags.containsKey(name)) {
            return "";
        }
        return this.tags.get(name).data;
    }
    
    public byte[] getByteArray(final String name) {
        if (!this.tags.containsKey(name)) {
            return new byte[0];
        }
        return this.tags.get(name).data;
    }
    
    public CompoundTag getCompound(final String name) {
        if (!this.tags.containsKey(name)) {
            return new CompoundTag();
        }
        return this.tags.get(name);
    }
    
    public ListTag getList(final String name) {
        if (!this.tags.containsKey(name)) {
            return new ListTag();
        }
        return this.tags.get(name);
    }
    
    public boolean getBoolean(final String name) {
        return this.getByte(name) != 0;
    }
    
    @Override
    public String toString() {
        return "" + this.tags.size() + " entries";
    }
}
