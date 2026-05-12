// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;
import java.nio.FloatBuffer;

public class Lighting
{
    private static FloatBuffer lb;
    
    public static void turnOff() {
        GL11.glDisable(2896);
        GL11.glDisable(16384);
        GL11.glDisable(16385);
        GL11.glDisable(2903);
    }
    
    public static void turnOn() {
        GL11.glEnable(2896);
        GL11.glEnable(16384);
        GL11.glEnable(16385);
        GL11.glEnable(2903);
        GL11.glColorMaterial(1032, 5634);
        final float n = 0.4f;
        final float n2 = 0.6f;
        final float n3 = 0.0f;
        final Vec3 normalize = Vec3.newTemp(0.20000000298023224, 1.0, -0.699999988079071).normalize();
        GL11.glLight(16384, 4611, getBuffer(normalize.x, normalize.y, normalize.z, 0.0));
        GL11.glLight(16384, 4609, getBuffer(n2, n2, n2, 1.0f));
        GL11.glLight(16384, 4608, getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GL11.glLight(16384, 4610, getBuffer(n3, n3, n3, 1.0f));
        final Vec3 normalize2 = Vec3.newTemp(-0.20000000298023224, 1.0, 0.699999988079071).normalize();
        GL11.glLight(16385, 4611, getBuffer(normalize2.x, normalize2.y, normalize2.z, 0.0));
        GL11.glLight(16385, 4609, getBuffer(n2, n2, n2, 1.0f));
        GL11.glLight(16385, 4608, getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GL11.glLight(16385, 4610, getBuffer(n3, n3, n3, 1.0f));
        GL11.glShadeModel(7424);
        GL11.glLightModel(2899, getBuffer(n, n, n, 1.0f));
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
