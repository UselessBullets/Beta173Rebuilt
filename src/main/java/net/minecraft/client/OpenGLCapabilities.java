// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import org.lwjgl.opengl.GLContext;

public class OpenGLCapabilities
{
    private static boolean USE_OCCLUSION_QUERY;
    
    public boolean hasOcclusionChecks() {
        return OpenGLCapabilities.USE_OCCLUSION_QUERY && GLContext.getCapabilities().GL_ARB_occlusion_query;
    }
    
    static {
        OpenGLCapabilities.USE_OCCLUSION_QUERY = true;
    }
}
