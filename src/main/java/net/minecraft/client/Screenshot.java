// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.text.SimpleDateFormat;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Date;
import org.lwjgl.opengl.GL11;
import org.lwjgl.BufferUtils;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.DateFormat;

public class Screenshot
{
    private static DateFormat df;
    private static ByteBuffer pixels;
    private static byte[] buffer;
    private static int[] pixelBuffer;
    
    public static String grab(final File workDir, final int width, final int height) {
        try {
            final File parent = new File(workDir, "screenshots");
            parent.mkdir();
            if (Screenshot.pixels == null || Screenshot.pixels.capacity() < width * height) {
                Screenshot.pixels = BufferUtils.createByteBuffer(width * height * 3);
            }
            if (Screenshot.pixelBuffer == null || Screenshot.pixelBuffer.length < width * height * 3) {
                Screenshot.buffer = new byte[width * height * 3];
                Screenshot.pixelBuffer = new int[width * height];
            }
            GL11.glPixelStorei(3333, 1);
            GL11.glPixelStorei(3317, 1);
            Screenshot.pixels.clear();
            GL11.glReadPixels(0, 0, width, height, 6407, 5121, Screenshot.pixels);
            Screenshot.pixels.clear();
            final String string = "" + Screenshot.df.format(new Date());
            File output;
            for (int i = 1; (output = new File(parent, string + ((i == 1) ? "" : ("_" + i)) + ".png")).exists(); ++i) {}
            Screenshot.pixels.get(Screenshot.buffer);
            for (int j = 0; j < width; ++j) {
                for (int k = 0; k < height; ++k) {
                    final int n = j + (height - k - 1) * width;
                    Screenshot.pixelBuffer[j + k * width] = (0xFF000000 | (Screenshot.buffer[n * 3 + 0] & 0xFF) << 16 | (Screenshot.buffer[n * 3 + 1] & 0xFF) << 8 | (Screenshot.buffer[n * 3 + 2] & 0xFF));
                }
            }
            final BufferedImage im = new BufferedImage(width, height, 1);
            im.setRGB(0, 0, width, height, Screenshot.pixelBuffer, 0, width);
            ImageIO.write(im, "png", output);
            return "Saved screenshot as " + output.getName();
        }
        catch (final Exception obj) {
            obj.printStackTrace();
            return "Failed to save: " + obj;
        }
    }
    
    static {
        Screenshot.df = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
    }
}
