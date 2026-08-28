// 
// Decompiled by Procyon v0.6.0
// 

package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class Tag
{
    public static final byte TAG_End = 0;
    public static final byte TAG_Byte = 1;
    public static final byte TAG_Short = 2;
    public static final byte TAG_Int = 3;
    public static final byte TAG_Long = 4;
    public static final byte TAG_Float = 5;
    public static final byte TAG_Double = 6;
    public static final byte TAG_Byte_Array = 7;
    public static final byte TAG_String = 8;
    public static final byte TAG_List = 9;
    public static final byte TAG_Compound = 10;
    private String name;
    
    public Tag() {
        this.name = null;
    }
    
    abstract void write(final DataOutput dos) throws IOException;
    
    abstract void load(final DataInput dis) throws IOException;
    
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
    
    public static Tag readNamedTag(final DataInput dis) throws IOException {
        final byte type = dis.readByte();
        if (type == TAG_End) return new EndTag();

        final Tag tag = newTag(type);
//        short length = dis.readShort();
//        byte[] bytes = new byte[length];
//        dis.readFully(bytes);

        tag.name = dis.readUTF();
        tag.load(dis);
        return tag;
    }
    
    public static void writeNamedTag(final Tag tag, final DataOutput dos) throws IOException {
        dos.writeByte(tag.getId());
        if (tag.getId() == TAG_End) return;

//        byte[] bytes = tag.getName().getBytes("UTF-8");
//        dos.writeShort(bytes.length);
//        dos.write(bytes);
        dos.writeUTF(tag.getName());
        tag.write(dos);
    }
    
    public static Tag newTag(final byte type) {
        switch (type) {
            case TAG_End: return new EndTag();
            case TAG_Byte: return new ByteTag();
            case TAG_Short: return new ShortTag();
            case TAG_Int: return new IntTag();
            case TAG_Long: return new LongTag();
            case TAG_Float: return new FloatTag();
            case TAG_Double: return new DoubleTag();
            case TAG_Byte_Array: return new ByteArrayTag();
            case TAG_String: return new StringTag();
            case TAG_List: return new ListTag<>();
            case TAG_Compound: return new CompoundTag();
            default: return null;
        }
    }
    
    public static String getTagName(final byte type) {
        switch (type) {
            case TAG_End: return "TAG_End";
            case TAG_Byte: return "TAG_Byte";
            case TAG_Short: return "TAG_Short";
            case TAG_Int: return "TAG_Int";
            case TAG_Long: return "TAG_Long";
            case TAG_Float: return "TAG_Float";
            case TAG_Double: return "TAG_Double";
            case TAG_Byte_Array: return "TAG_Byte_Array";
            case TAG_String: return "TAG_String";
            case TAG_List: return "TAG_List";
            case TAG_Compound: return "TAG_Compound";
            default: return "UNKNOWN";
        }
    }
}
