// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import net.minecraft.world.phys.Vec3;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Lighting
{
    private static FloatBuffer lb;
    
    public static void turnOff() {
        glDisable(GL_LIGHTING);
        glDisable(GL_LIGHT0);
        glDisable(GL_LIGHT1);
        glDisable(GL_COLOR_MATERIAL);
    }
    
    public static void turnOn() {
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glEnable(GL_LIGHT1);
        glEnable(GL_COLOR_MATERIAL);
        glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
        final float a = 0.4f;
        final float d = 0.6f;
        final float s = 0.0f;

        Vec3 l = Vec3.newTemp(0.2, 1.0, -0.7).normalize();
        glLight(GL_LIGHT0, GL_POSITION, getBuffer(l.x, l.y, l.z, 0.0));
        glLight(GL_LIGHT0, GL_DIFFUSE, getBuffer(d, d, d, 1.0f));
        glLight(GL_LIGHT0, GL_AMBIENT, getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        glLight(GL_LIGHT0, GL_SPECULAR, getBuffer(s, s, s, 1.0f));

        l = Vec3.newTemp(-0.2, 1.0, 0.7).normalize();
        glLight(GL_LIGHT1, GL_POSITION, getBuffer(l.x, l.y, l.z, 0.0));
        glLight(GL_LIGHT1, GL_DIFFUSE, getBuffer(d, d, d, 1.0f));
        glLight(GL_LIGHT1, GL_AMBIENT, getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        glLight(GL_LIGHT1, GL_SPECULAR, getBuffer(s, s, s, 1.0f));

        glShadeModel(GL_FLAT);
        glLightModel(GL_LIGHT_MODEL_AMBIENT, getBuffer(a, a, a, 1.0f));
    }
    
    private static FloatBuffer getBuffer(final double a, final double b, final double c, final double d) {
        return getBuffer((float)a, (float)b, (float)c, (float)d);
    }
    
    private static FloatBuffer getBuffer(final float a, final float b, final float c, final float d) {
        Lighting.lb.clear();
        Lighting.lb.put(a).put(b).put(c).put(d);
        Lighting.lb.flip();
        return Lighting.lb;
    }
    
    static {
        Lighting.lb = MemoryTracker.createFloatBuffer(16);
    }
}
