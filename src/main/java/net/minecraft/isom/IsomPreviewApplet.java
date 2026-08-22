// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.isom;

import java.awt.BorderLayout;
import java.applet.Applet;

public class IsomPreviewApplet extends Applet
{
    private IsomPreview isomPreview = new IsomPreview();
    
    public IsomPreviewApplet() {
        this.setLayout(new BorderLayout());
        this.add(this.isomPreview, "Center");
    }
    
    @Override
    public void start() {
        this.isomPreview.start();
    }
    
    @Override
    public void stop() {
        this.isomPreview.stop();
    }
}
