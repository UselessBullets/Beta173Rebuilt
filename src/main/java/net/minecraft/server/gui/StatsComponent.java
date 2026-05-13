// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.gui;

import java.awt.Graphics;
import net.minecraft.network.Connection;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.awt.Dimension;
import javax.swing.JComponent;

public class StatsComponent extends JComponent
{
    private int[] values;
    private int vp;
    private String[] msgs;
    
    public StatsComponent() {
        this.values = new int[256];
        this.vp = 0;
        this.msgs = new String[10];
        this.setPreferredSize(new Dimension(256, 196));
        this.setMinimumSize(new Dimension(256, 196));
        this.setMaximumSize(new Dimension(256, 196));
        new Timer(500, actionEvent -> tick()).start();
        this.setBackground(Color.BLACK);
    }
    
    private void tick() {
        final long n = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.gc();
        this.msgs[0] = "Memory use: " + n / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)";
        this.msgs[1] = "Threads: " + Connection.readThreads + " + " + Connection.writeThreads;
        this.values[this.vp++ & 0xFF] = (int)(n * 100L / Runtime.getRuntime().maxMemory());
        this.repaint();
    }
    
    @Override
    public void paint(final Graphics graphics) {
        graphics.setColor(new Color(16777215));
        graphics.fillRect(0, 0, 256, 192);
        for (int i = 0; i < 256; ++i) {
            final int n = this.values[i + this.vp & 0xFF];
            graphics.setColor(new Color(n + 28 << 16));
            graphics.fillRect(i, 100 - n, 1, n);
        }
        graphics.setColor(Color.BLACK);
        for (int j = 0; j < this.msgs.length; ++j) {
            final String s = this.msgs[j];
            if (s != null) {
                graphics.drawString(s, 32, 116 + j * 16);
            }
        }
    }

}
