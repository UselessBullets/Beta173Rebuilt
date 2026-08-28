// 
// Decompiled by Procyon v0.6.0
// 

package com.mojang.nbt;

import java.io.IOException;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.OutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;

public class NbtIo
{
    public static CompoundTag readCompressed(final InputStream in) throws IOException {
        final DataInputStream dis = new DataInputStream(new GZIPInputStream(in));
        try {
            return read(dis);
        }
        finally {
            dis.close();
        }
    }
    
    public static void writeCompressed(final CompoundTag tag, final OutputStream out) throws IOException {
        final DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(out));
        try {
            write(tag, dos);
        }
        finally {
            dos.close();
        }
    }
    
    public static CompoundTag read(final DataInput dis) throws IOException {
        final Tag tag = Tag.readNamedTag(dis);
        if (tag instanceof CompoundTag) return (CompoundTag) tag;
        throw new IOException("Root tag must be a named compound tag");
    }
    
    public static void write(final CompoundTag tag, final DataOutput dos) throws IOException {
        Tag.writeNamedTag(tag, dos);
    }
}
