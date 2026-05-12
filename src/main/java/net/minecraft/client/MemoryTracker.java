// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.util.ArrayList;
import java.nio.FloatBuffer;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import java.util.List;

public class MemoryTracker
{
    private static List lists;
    private static List textures;
    
    public static synchronized int genLists(final int count) {
        final int glGenLists = GL11.glGenLists(count);
        MemoryTracker.lists.add(glGenLists);
        MemoryTracker.lists.add(count);
        return glGenLists;
    }
    
    public static synchronized void genTextures(final IntBuffer ib) {
        GL11.glGenTextures(ib);
        for (int i = ib.position(); i < ib.limit(); ++i) {
            MemoryTracker.textures.add(ib.get(i));
        }
    }
    
    public static synchronized void releaseLists(final int id) {
        final int index = MemoryTracker.lists.indexOf(id);
        GL11.glDeleteLists((int)MemoryTracker.lists.get(index), (int)MemoryTracker.lists.get(index + 1));
        MemoryTracker.lists.remove(index);
        MemoryTracker.lists.remove(index);
    }
    
    public static synchronized void release() {
        for (int i = 0; i < MemoryTracker.lists.size(); i += 2) {
            GL11.glDeleteLists((int)MemoryTracker.lists.get(i), (int)MemoryTracker.lists.get(i + 1));
        }
        final IntBuffer intBuffer = createIntBuffer(MemoryTracker.textures.size());
        intBuffer.flip();
        GL11.glDeleteTextures(intBuffer);
        for (int j = 0; j < MemoryTracker.textures.size(); ++j) {
            intBuffer.put((int)MemoryTracker.textures.get(j));
        }
        intBuffer.flip();
        GL11.glDeleteTextures(intBuffer);
        MemoryTracker.lists.clear();
        MemoryTracker.textures.clear();
    }
    
    public static synchronized ByteBuffer createByteBuffer(final int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }
    
    public static IntBuffer createIntBuffer(final int size) {
        return createByteBuffer(size << 2).asIntBuffer();
    }
    
    public static FloatBuffer createFloatBuffer(final int size) {
        return createByteBuffer(size << 2).asFloatBuffer();
    }
    
    static {
        MemoryTracker.lists = new ArrayList();
        MemoryTracker.textures = new ArrayList();
    }
}
