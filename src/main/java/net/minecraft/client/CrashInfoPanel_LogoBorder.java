// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Dimension;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Canvas;

class CrashInfoPanel_LogoBorder extends Canvas
{
    private BufferedImage image;
    
    public CrashInfoPanel_LogoBorder() {
        try {
            this.image = ImageIO.read(CrashInfoPanel.class.getResource("/gui/logo.png"));
        }
        catch (final IOException ex) {}
        final int n = 100;
        this.setPreferredSize(new Dimension(n, n));
        this.setMinimumSize(new Dimension(n, n));
    }
    
    @Override
    public void paint(final Graphics g) {
        super.paint(g);
        g.drawImage(this.image, this.getWidth() / 2 - this.image.getWidth() / 2, 32, null);
    }
}
