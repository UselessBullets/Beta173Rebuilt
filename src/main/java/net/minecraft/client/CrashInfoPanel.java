// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.awt.*;

import net.minecraft.SharedConstants;
import org.lwjgl.opengl.GL11;
import org.lwjgl.Sys;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CrashInfoPanel extends Panel
{
    public CrashInfoPanel(final CrashReport report) {
        this.setBackground(new Color(3028036));
        this.setLayout(new BorderLayout());
        final StringWriter out = new StringWriter();
        report.e.printStackTrace(new PrintWriter(out));
        final String string = out.toString();
        String glGetString = "";
        String s = "";
        try {
            s = s + "Generated " + new SimpleDateFormat().format(new Date()) + "\n";
            s += "\n";
            s += "Minecraft: Minecraft " + SharedConstants.VERSION_STRING + "\n";
            s = s + "OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version") + "\n";
            s = s + "Java: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor") + "\n";
            s = s + "VM: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor") + "\n";
            s = s + "LWJGL: " + Sys.getVersion() + "\n";
            glGetString = GL11.glGetString(7936);
            s = s + "OpenGL: " + GL11.glGetString(7937) + " version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936) + "\n";
        }
        catch (final Throwable obj) {
            s = s + "[failed to get system properties (" + obj + ")]\n";
        }
        final String string2 = s + "\n" + string;
        final String string3 = "" + "\n" + "\n";
        String str;
        if (string.contains("Pixel format not accelerated")) {
            str = string3 + "      Bad video card drivers!      \n" + "      -----------------------      \n" + "\n" + "Minecraft was unable to start because it failed to find an accelerated OpenGL mode.\n" + "This can usually be fixed by updating the video card drivers.\n";
            if (glGetString.toLowerCase().contains("nvidia")) {
                str = str + "\n" + "You might be able to find drivers for your video card here:\n" + "  http://www.nvidia.com/\n";
            }
            else if (glGetString.toLowerCase().contains("ati")) {
                str = str + "\n" + "You might be able to find drivers for your video card here:\n" + "  http://www.amd.com/\n";
            }
        }
        else {
            str = string3 + "      Minecraft has crashed!      \n" + "      ----------------------      \n" + "\n" + "Minecraft has stopped running because it encountered a problem.\n" + "\n" + "If you wish to report this, please copy this entire text and email it to support@mojang.com.\n" + "Please include a description of what you did when the error occured.\n";
        }
        final String string4 = str + "\n" + "\n" + "\n";
        final String string5 = string4 + "--- BEGIN ERROR REPORT " + Integer.toHexString(string4.hashCode()) + " --------\n" + string2;
        final TextArea comp = new TextArea(string5 + "--- END ERROR REPORT " + Integer.toHexString(string5.hashCode()) + " ----------\n" + "\n" + "\n", 0, 0, 1);
        comp.setFont(new Font("Monospaced", 0, 12));
        this.add(new LogoBorder(), "North");
        this.add(new Border(80), "East");
        this.add(new Border(80), "West");
        this.add(new Border(100), "South");
        this.add(comp, "Center");
    }

    static class LogoBorder extends Canvas
    {
        private BufferedImage image;

        public LogoBorder() {
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

    static class Border extends Canvas
    {
        public Border(final int size) {
            this.setPreferredSize(new Dimension(size, size));
            this.setMinimumSize(new Dimension(size, size));
        }
    }
}
