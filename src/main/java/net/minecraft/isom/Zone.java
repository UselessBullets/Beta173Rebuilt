// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.isom;

import net.minecraft.world.level.Level;
import java.awt.image.BufferedImage;

public class Zone
{
    public BufferedImage image;
    public Level level;
    public int x;
    public int y;
    public boolean rendered = false;
    public boolean noContent = false;
    public int lastVisible = 0;
    public boolean addedToRenderQueue = false;
    
    public Zone(final Level level, final int x, final int y) {
        this.level = level;
        this.init(x, y);
    }
    
    public void init(final int x, final int y) {
        this.rendered = false;
        this.x = x;
        this.y = y;
        this.lastVisible = 0;
        this.addedToRenderQueue = false;
    }
    
    public void init(final Level level, final int x, final int y) {
        this.level = level;
        this.init(x, y);
    }
}
