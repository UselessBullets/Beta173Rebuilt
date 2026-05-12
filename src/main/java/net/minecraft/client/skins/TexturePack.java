// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.skins;

import java.io.InputStream;
import net.minecraft.client.Minecraft;

public abstract class TexturePack
{
    public String name;
    public String desc1;
    public String desc2;
    public String id;
    
    public void select() {
    }
    
    public void deselect() {
    }
    
    public void load(final Minecraft minecraft) {
    }
    
    public void unload(final Minecraft minecraft) {
    }
    
    public void bindTexture(final Minecraft minecraft) {
    }
    
    public InputStream getResource(final String name) {
        return TexturePack.class.getResourceAsStream(name);
    }
}
