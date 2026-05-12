// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import org.lwjgl.input.Keyboard;

public class Minecraft_DescFormatter implements DescFormatter
{
    final /* synthetic */ Minecraft mc;
    
    public Minecraft_DescFormatter(final Minecraft mc) {
        this.mc = mc;
    }
    
    public String format(final String i18nValue) {
        return String.format(i18nValue, Keyboard.getKeyName(this.mc.options.keyBuild.key));
    }
}
