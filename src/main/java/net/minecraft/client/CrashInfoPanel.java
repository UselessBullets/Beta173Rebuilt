// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.awt.*;

import org.lwjgl.Sys;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.lwjgl.opengl.GL11.*;

public class CrashInfoPanel extends Panel
{
    public CrashInfoPanel(final CrashReport report) {
        this.setBackground(new Color(0x2e3444));
        this.setLayout(new BorderLayout());
        final StringWriter sw = new StringWriter();
        report.e.printStackTrace(new PrintWriter(sw));
        final String stacktrace = sw.toString();
        String vendor = "";
        String msg = "";

        try {
            msg = msg + "Generated " + new SimpleDateFormat().format(new Date()) + "\n";
            msg = msg + "\n";
            msg = msg + "Minecraft: " + Minecraft.VERSION_STRING + "\n";
            msg = msg + "OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version") + "\n";
            msg = msg + "Java: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor") + "\n";
            msg = msg + "VM: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor") + "\n";
            msg = msg + "LWJGL: " + Sys.getVersion() + "\n";
            vendor = glGetString(GL_VENDOR);
            msg = msg + "OpenGL: " + glGetString(GL_RENDERER) + " version " + glGetString(GL_VERSION) + ", " + glGetString(GL_VENDOR) + "\n";
        }
        catch (final Throwable obj) {
            msg = msg + "[failed to get system properties (" + obj + ")]\n";
        }

        msg = msg + "\n";
        msg = msg + stacktrace;
        String text = "";
        text = text + "\n";
        text = text + "\n";
        if (stacktrace.contains("Pixel format not accelerated")) {
            text = text + "      Bad video card drivers!      \n";
            text = text + "      -----------------------      \n";
            text = text + "\n";
            text = text + "Minecraft was unable to start because it failed to find an accelerated OpenGL mode.\n";
            text = text + "This can usually be fixed by updating the video card drivers.\n";
            if (vendor.toLowerCase().contains("nvidia")) {
                text = text + "\n";
                text = text + "You might be able to find drivers for your video card here:\n";
                text = text + "  http://www.nvidia.com/\n";
            }
            else if (vendor.toLowerCase().contains("ati")) {
                text = text + "\n";
                text = text + "You might be able to find drivers for your video card here:\n";
                text = text + "  http://www.amd.com/\n";
            }
        }
        else {
            text = text + "      Minecraft has crashed!      \n";
            text = text + "      ----------------------      \n";
            text = text + "\n";
            text = text + "Minecraft has stopped running because it encountered a problem.\n";
            text = text + "\n";
            text = text + "If you wish to report this, please copy this entire text and email it to support@mojang.com.\n";
            text = text + "Please include a description of what you did when the error occured.\n";
        }

        text = text + "\n";
        text = text + "\n";
        text = text + "\n";
        text = text + "--- BEGIN ERROR REPORT " + Integer.toHexString(text.hashCode()) + " --------\n";
        text = text + msg;
        text = text + "--- END ERROR REPORT " + Integer.toHexString(text.hashCode()) + " ----------\n";
        text = text + "\n";
        text = text + "\n";
        final TextArea textArea = new TextArea(text, 0, 0, 1);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        this.add(new LogoBorder(), "North");
        this.add(new Border(80), "East");
        this.add(new Border(80), "West");
        this.add(new Border(100), "South");
        this.add(textArea, "Center");
    }

    private static class Border extends Canvas
    {
        public Border(final int size) {
            this.setPreferredSize(new Dimension(size, size));
            this.setMinimumSize(new Dimension(size, size));
        }
    }

    private static class LogoBorder extends Canvas
    {
        private BufferedImage image;

        public LogoBorder() {
            try {
                this.image = ImageIO.read(CrashInfoPanel.class.getResource("/gui/logo.png"));
            }
            catch (final IOException ignored) {}

            final int size = 100;
            this.setPreferredSize(new Dimension(size, size));
            this.setMinimumSize(new Dimension(size, size));
        }

        @Override
        public void paint(final Graphics g) {
            super.paint(g);
            g.drawImage(this.image, this.getWidth() / 2 - this.image.getWidth() / 2, 32, null);
        }
    }
}
