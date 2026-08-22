// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class MemoryTracker
{
    private static List<Integer> lists = new ArrayList<>();
    private static List<Integer> textures = new ArrayList<>();
    
    public static synchronized int genLists(final int count) {
        final int glGenLists = glGenLists(count);
        lists.add(glGenLists);
        lists.add(count);
        return glGenLists;
    }
    
    public static synchronized void genTextures(final IntBuffer ib) {
        glGenTextures(ib);
        for (int i = ib.position(); i < ib.limit(); ++i) {
            textures.add(ib.get(i));
        }
    }
    
    public static synchronized void releaseLists(final int id) {
        final int index = lists.indexOf(id);
        glDeleteLists(lists.get(index), lists.get(index + 1));
        lists.remove(index);
        lists.remove(index);
    }
    
    public static synchronized void release() {
        for (int i = 0; i < lists.size(); i += 2) {
            glDeleteLists(lists.get(i), lists.get(i + 1));
        }

        final IntBuffer ib = createIntBuffer(textures.size());
        ib.flip();
        glDeleteTextures(ib);

        for (int j = 0; j < textures.size(); ++j) {
            ib.put(textures.get(j));
        }

        ib.flip();
        glDeleteTextures(ib);
        lists.clear();
        textures.clear();
    }
    
    public static synchronized ByteBuffer createByteBuffer(final int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }
    // Useless - exists in b1.2 leak
    public static ShortBuffer createShortBuffer(int size) {
        return createByteBuffer(size << 1).asShortBuffer();
    }
    // Useless - exists in b1.2 leak
    public static CharBuffer createCharBuffer(int size) {
        return createByteBuffer(size << 1).asCharBuffer();
    }

    public static IntBuffer createIntBuffer(int size) {
        return createByteBuffer(size << 2).asIntBuffer();
    }
    // Useless - exists in b1.2 leak
    public static LongBuffer createLongBuffer(int size) {
        return createByteBuffer(size << 3).asLongBuffer();
    }

    public static FloatBuffer createFloatBuffer(int size) {
        return createByteBuffer(size << 2).asFloatBuffer();
    }
    // Useless - exists in b1.2 leak
    public static DoubleBuffer createDoubleBuffer(int size) {
        return createByteBuffer(size << 3).asDoubleBuffer();
    }
}
