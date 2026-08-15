// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import java.nio.ByteOrder;

import org.lwjgl.opengl.ARBBufferObject;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GLContext;
import net.minecraft.client.MemoryTracker;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Tesselator
{
    private static boolean TRIANGLE_MODE = true;
    private static boolean USE_VBO = false;
    private static final int MAX_MEMORY_USE = 16 * 1024 * 1024;
    private static final int MAX_FLOATS = MAX_MEMORY_USE / 4 / 2;
    private ByteBuffer buffer;
    private IntBuffer ib;
    private FloatBuffer fb;
    private int[] array;
    private int vertices;
    private double u;
    private double v;
    private int col;
    private boolean hasColor;
    private boolean hasTexture;
    private boolean hasNormal;
    private int p;
    private int count;
    private boolean noColor;
    private int mode;
    private double xo;
    private double yo;
    private double zo;
    private int normal;
    public static final Tesselator instance = new Tesselator(MAX_FLOATS);
    private boolean tesselating;
    private boolean vboMode;
    private IntBuffer vboIds;
    private int vboId;
    private int vboCounts;
    private int size;
    
    private Tesselator(final int size) {
        this.vertices = 0;
        this.hasColor = false;
        this.hasTexture = false;
        this.hasNormal = false;
        this.p = 0;
        this.count = 0;
        this.noColor = false;
        this.tesselating = false;
        this.vboMode = false;
        this.vboId = 0;
        this.vboCounts = 10;

        this.size = size;
        this.buffer = MemoryTracker.createByteBuffer(size * 4);
        this.ib = this.buffer.asIntBuffer();
        this.fb = this.buffer.asFloatBuffer();
        this.array = new int[size];
        this.vboMode = (Tesselator.USE_VBO && GLContext.getCapabilities().GL_ARB_vertex_buffer_object);

        if (this.vboMode) {
            this.vboIds = MemoryTracker.createIntBuffer(this.vboCounts);
            ARBVertexBufferObject.glGenBuffersARB(this.vboIds);
        }
    }
    
    public void end() {
        if (!this.tesselating) {
            throw new IllegalStateException("Not tesselating!");
        }
        this.tesselating = false;
        if (this.vertices > 0) {
            this.ib.clear();
            this.ib.put(this.array, 0, this.p);
            this.buffer.position(0);
            this.buffer.limit(this.p * 4);
            if (this.vboMode) {
                this.vboId = (this.vboId + 1) % this.vboCounts;
                ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, this.vboIds.get(this.vboId));
                ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, this.buffer, ARBBufferObject.GL_STREAM_DRAW_ARB);
            }
            if (this.hasTexture) {
                if (this.vboMode) {
                    glTexCoordPointer(2, GL_FLOAT, 32, 12L);
                }
                else {
                    this.fb.position(3);
                    glTexCoordPointer(2, 32, this.fb);
                }
                glEnableClientState(GL_TEXTURE_COORD_ARRAY);
            }
            if (this.hasColor) {
                if (this.vboMode) {
                    glColorPointer(4, GL_UNSIGNED_BYTE, 32, 20L);
                }
                else {
                    this.buffer.position(20);
                    glColorPointer(4, true, 32, this.buffer);
                }
                glEnableClientState(GL_COLOR_ARRAY);
            }
            if (this.hasNormal) {
                if (this.vboMode) {
                    glNormalPointer(GL_BYTE, 32, 24L);
                }
                else {
                    this.buffer.position(24);
                    glNormalPointer(32, this.buffer);
                }
                glEnableClientState(GL_NORMAL_ARRAY);
            }
            if (this.vboMode) {
                glVertexPointer(3, GL_FLOAT, 32, 0L);
            }
            else {
                this.fb.position(0);
                glVertexPointer(3, 32, this.fb);
            }
            glEnableClientState(GL_VERTEX_ARRAY);
            if (this.mode == GL_QUADS && Tesselator.TRIANGLE_MODE) {
                glDrawArrays(GL_TRIANGLES, 0, this.vertices);
            }
            else {
                glDrawArrays(this.mode, 0, this.vertices);
            }

            glDisableClientState(GL_VERTEX_ARRAY);
            if (this.hasTexture) glDisableClientState(GL_TEXTURE_COORD_ARRAY);
            if (this.hasColor) glDisableClientState(GL_COLOR_ARRAY);
            if (this.hasNormal) glDisableClientState(GL_NORMAL_ARRAY);
        }
        this.clear();
    }
    
    private void clear() {
        this.vertices = 0;
        this.buffer.clear();
        this.p = 0;
        this.count = 0;
    }
    
    public void begin() {
        this.begin(GL_QUADS);
    }
    
    public void begin(final int mode) {
        if (this.tesselating) {
            throw new IllegalStateException("Already tesselating!");
        }
        this.tesselating = true;
        this.clear();
        this.mode = mode;
        this.hasNormal = false;
        this.hasColor = false;
        this.hasTexture = false;
        this.noColor = false;
    }
    
    public void tex(final double u, final double v) {
        this.hasTexture = true;
        this.u = u;
        this.v = v;
    }
    
    public void color(final float r, final float g, final float b) {
        this.color((int)(r * 255.0f), (int)(g * 255.0f), (int)(b * 255.0f));
    }
    
    public void color(final float r, final float g, final float b, final float a) {
        this.color((int)(r * 255.0f), (int)(g * 255.0f), (int)(b * 255.0f), (int)(a * 255.0f));
    }
    
    public void color(final int r, final int g, final int b) {
        this.color(r, g, b, 255);
    }
    
    public void color(int r, int g, int b, int a) {
        if (this.noColor) {
            return;
        }
        if (r > 255) r = 255;
        if (g > 255) g = 255;
        if (b > 255) b = 255;
        if (a > 255) a = 255;
        if (r < 0) r = 0;
        if (g < 0) g = 0;
        if (b < 0) b = 0;
        if (a < 0) a = 0;

        this.hasColor = true;
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            this.col = (a << 24 | b << 16 | g << 8 | r);
        }
        else {
            this.col = (r << 24 | g << 16 | b << 8 | a);
        }
    }
    
    public void vertexUV(final double x, final double y, final double z, final double u, final double v) {
        this.tex(u, v);
        this.vertex(x, y, z);
    }
    
    public void vertex(final double x, final double y, final double z) {
        ++this.count;
        if (this.mode == GL_QUADS && Tesselator.TRIANGLE_MODE && this.count % 4 == 0) {
            for (int i = 0; i < 2; ++i) {
                final int n = 8 * (3 - i);
                if (this.hasTexture) {
                    this.array[this.p + 3] = this.array[this.p - n + 3];
                    this.array[this.p + 4] = this.array[this.p - n + 4];
                }
                if (this.hasColor) {
                    this.array[this.p + 5] = this.array[this.p - n + 5];
                }
                this.array[this.p + 0] = this.array[this.p - n + 0];
                this.array[this.p + 1] = this.array[this.p - n + 1];
                this.array[this.p + 2] = this.array[this.p - n + 2];
                ++this.vertices;
                this.p += 8;
            }
        }
        if (this.hasTexture) {
            this.array[this.p + 3] = Float.floatToRawIntBits((float)this.u);
            this.array[this.p + 4] = Float.floatToRawIntBits((float)this.v);
        }
        if (this.hasColor) {
            this.array[this.p + 5] = this.col;
        }
        if (this.hasNormal) {
            this.array[this.p + 6] = this.normal;
        }
        this.array[this.p + 0] = Float.floatToRawIntBits((float)(x + this.xo));
        this.array[this.p + 1] = Float.floatToRawIntBits((float)(y + this.yo));
        this.array[this.p + 2] = Float.floatToRawIntBits((float)(z + this.zo));
        this.p += 8;
        ++this.vertices;
        if (this.vertices % 4 == 0 && this.p >= this.size - 32) {
            this.end();
            this.tesselating = true;
        }
    }
    
    public void color(final int c) {
        this.color(c >> 16 & 0xFF, c >> 8 & 0xFF, c & 0xFF);
    }
    
    public void color(final int c, final int alpha) {
        this.color(c >> 16 & 0xFF, c >> 8 & 0xFF, c & 0xFF, alpha);
    }
    
    public void noColor() {
        this.noColor = true;
    }
    
    public void normal(final float x, final float y, final float z) {
        if (!this.tesselating) {
            System.out.println("But..");
        }
        this.hasNormal = true;
        this.normal = ((byte)(x * 128.0f) | (byte)(y * 127.0f) << 8 | (byte)(z * 127.0f) << 16);
    }
    
    public void offset(final double x, final double y, final double z) {
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }
    
    public void addOffset(final float x, final float y, final float z) {
        this.xo += x;
        this.yo += y;
        this.zo += z;
    }

}
