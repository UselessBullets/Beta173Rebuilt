package net.minecraft.isom;

import java.awt.image.BufferedImage;

import net.minecraft.world.level.Level;

// Useless - Isom package was pulled straight from the Beta 1.9-pre6 jar as it had source for this package, thanks @genericpnpmonit0r for telling me this
public class Zone {
    public static final int CHUNK_SIZE = 16;

    public BufferedImage image;

    public Level level;
    public int x, y;
    public boolean rendered = false;
    public boolean noContent = false;

    public int lastVisible = 0;

    public boolean addedToRenderQueue = false;

    public Zone(Level level, int x, int y) {
        this.level = level;
        init(x, y);
    }

    public void init(int x, int y) {
        rendered = false;
        this.x = x;
        this.y = y;
        lastVisible = 0;
        addedToRenderQueue = false;
    }

    public void init(Level level, int x, int y) {
        this.level = level;
        init(x, y);
    }
}
