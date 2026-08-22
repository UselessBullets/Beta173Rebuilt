// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Date;

import org.lwjgl.BufferUtils;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.DateFormat;

import static org.lwjgl.opengl.GL11.*;

public class Screenshot
{
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
    private static ByteBuffer pixels;
    private static byte[] buffer;
    private static int[] pixelBuffer;
    // Useless - Below Fields were added from the b1.2 leak in for use in methods only used by big screenshots
    private int rowHeight;
    private DataOutputStream dos;
    private byte[] pb;
    private int w;
    private int h;
    private File file;
    
    public static String grab(final File workDir, final int width, final int height) {
        try {
            final File picDir = new File(workDir, "screenshots");
            picDir.mkdir();
            if (Screenshot.pixels == null || Screenshot.pixels.capacity() < width * height) {
                Screenshot.pixels = BufferUtils.createByteBuffer(width * height * 3);
            }

            if (Screenshot.pixelBuffer == null || Screenshot.pixelBuffer.length < width * height * 3) {
                Screenshot.buffer = new byte[width * height * 3];
                Screenshot.pixelBuffer = new int[width * height];
            }

            glPixelStorei(GL_PACK_ALIGNMENT, 1);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            Screenshot.pixels.clear();
            glReadPixels(0, 0, width, height, 6407, 5121, Screenshot.pixels);
            Screenshot.pixels.clear();
            final String picName = "" + Screenshot.df.format(new Date());
            int count = 1;

            File file;
            while ((file = new File(picDir, picName + ((count == 1) ? "" : ("_" + count)) + ".png")).exists()) {
                ++count;
            }

            Screenshot.pixels.get(Screenshot.buffer);

            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    final int i = x + (height - y - 1) * width;
                    int r = Screenshot.buffer[i * 3 + 0] & 0xFF;
                    int g = Screenshot.buffer[i * 3 + 1] & 0xFF;
                    int b = Screenshot.buffer[i * 3 + 2] & 0xFF;
                    int col = 0xFF000000 | r << 16 | g << 8 | b;
                    Screenshot.pixelBuffer[x + y * width] = col;
                }
            }

            final BufferedImage image = new BufferedImage(width, height, 1);
            image.setRGB(0, 0, width, height, Screenshot.pixelBuffer, 0, width);
            ImageIO.write(image, "png", file);
            return "Saved screenshot as " + file.getName();
        }
        catch (final Exception e) {
            e.printStackTrace();
            return "Failed to save: " + e;
        }
    }

    // Useless - Below methods were added from the b1.2 leak, seems highly plausible these were stripped for being unused as this class is only ever normally called in a static context
    public Screenshot(File workDir, int w, int h, int rowHeight) throws IOException {
        this.w = w;
        this.h = h;
        this.rowHeight = rowHeight;
        File picDir = new File(workDir, "screenshots");
        picDir.mkdir();
        String picName = "huge_" + df.format(new Date());
        int count = 1;

        while ((this.file = new File(picDir, picName + (count == 1 ? "" : "_" + count) + ".tga")).exists()) {
            count++;
        }

        // Useless - Sets the Truevision TGA image header bytes
        byte[] header = new byte[18];
        header[2] = 2; // Useless - Image type, 2 is "uncompressed true-color image"
        header[12] = (byte)(w % 256); // Useless - Image Width Byte 1
        header[13] = (byte)(w / 256); // Useless - Image Width Byte 2
        header[14] = (byte)(h % 256); // Useless - Image Height Byte 1
        header[15] = (byte)(h / 256); // Useless - Image Height Byte 2
        header[16] = 24; // Useless - Pixel Depth bits per pixel
        this.pb = new byte[w * rowHeight * 3];
        this.dos = new DataOutputStream(new FileOutputStream(this.file));
        this.dos.write(header);
    }

    public void addRegion(ByteBuffer pixels, int xo, int yo, int rw, int rh) {
        int ww = rw;
        int hh = rh;
        if (ww > this.w - xo) {
            ww = this.w - xo;
        }

        if (hh > this.h - yo) {
            hh = this.h - yo;
        }

        this.rowHeight = hh;

        for (int y = 0; y < hh; y++) {
            pixels.position((rh - hh) * rw * 3 + y * rw * 3);
            int dp = (xo + y * this.w) * 3;
            pixels.get(this.pb, dp, ww * 3);
        }
    }

    public void saveRow() throws IOException {
        this.dos.write(this.pb, 0, this.w * 3 * this.rowHeight);
    }

    public String close() throws IOException {
        this.dos.close();
        return "Saved screenshot as " + this.file.getName();
    }
}
