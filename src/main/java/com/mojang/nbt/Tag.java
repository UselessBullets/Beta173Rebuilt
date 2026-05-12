// 
// Decompiled by Procyon v0.6.0
// 

package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public abstract class Tag
{
    private String name;
    
    public Tag() {
        this.name = null;
    }
    
    abstract void write(final DataOutput dos);
    
    abstract void load(final DataInput dis);
    
    public abstract byte getId();
    
    public String getName() {
        if (this.name == null) {
            return "";
        }
        return this.name;
    }
    
    public Tag setName(final String name) {
        this.name = name;
        return this;
    }
    
    public static Tag readNamedTag(final DataInput dis) {
        final byte byte1 = dis.readByte();
        if (byte1 == 0) {
            return new EndTag();
        }
        final Tag tag = newTag(byte1);
        tag.name = dis.readUTF();
        tag.load(dis);
        return tag;
    }
    
    public static void writeNamedTag(final Tag tag, final DataOutput dos) {
        dos.writeByte(tag.getId());
        if (tag.getId() == 0) {
            return;
        }
        dos.writeUTF(tag.getName());
        tag.write(dos);
    }
    
    public static Tag newTag(final byte type) {
        switch (type) {
            case 0: {
                return new EndTag();
            }
            case 1: {
                return new ByteTag();
            }
            case 2: {
                return new ShortTag();
            }
            case 3: {
                return new IntTag();
            }
            case 4: {
                return new LongTag();
            }
            case 5: {
                return new FloatTag();
            }
            case 6: {
                return new DoubleTag();
            }
            case 7: {
                return new ByteArrayTag();
            }
            case 8: {
                return new StringTag();
            }
            case 9: {
                return new ListTag();
            }
            case 10: {
                return new CompoundTag();
            }
            default: {
                return null;
            }
        }
    }
    
    public static String getTagName(final byte type) {
        switch (type) {
            case 0: {
                return "TAG_End";
            }
            case 1: {
                return "TAG_Byte";
            }
            case 2: {
                return "TAG_Short";
            }
            case 3: {
                return "TAG_Int";
            }
            case 4: {
                return "TAG_Long";
            }
            case 5: {
                return "TAG_Float";
            }
            case 6: {
                return "TAG_Double";
            }
            case 7: {
                return "TAG_Byte_Array";
            }
            case 8: {
                return "TAG_String";
            }
            case 9: {
                return "TAG_List";
            }
            case 10: {
                return "TAG_Compound";
            }
            default: {
                return "UNKNOWN";
            }
        }
    }
}
