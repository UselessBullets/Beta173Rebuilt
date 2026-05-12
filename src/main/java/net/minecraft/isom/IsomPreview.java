// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.isom;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import net.minecraft.Pos;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.Graphics2D;
import java.awt.Graphics;
import net.minecraft.world.level.storage.LevelStorage;
import java.util.Random;
import net.minecraft.world.level.storage.DirectoryLevelStorage;
import java.awt.Color;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.io.File;
import net.minecraft.world.level.Level;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.KeyListener;
import java.awt.Canvas;

public class IsomPreview extends Canvas implements KeyListener, MouseListener, MouseMotionListener, Runnable
{
    private int currentReader;
    private int zoom;
    private boolean showHelp;
    private Level level;
    private File workDir;
    private boolean running;
    private List zonesToRender;
    private Zone[][] zoneMap;
    private int xCam;
    private int yCam;
    private int xDrag;
    private int yDrag;
    
    public File getWorkingDirectory() {
        if (this.workDir == null) {
            this.workDir = this.getWorkingDirectory("minecraft");
        }
        return this.workDir;
    }
    
    public File getWorkingDirectory(final String applicationName) {
        final String property = System.getProperty("user.home", ".");
        File obj = null;
        switch (IsomPreview_GetOsValueSwitchObfuscation.arr[getPlatform().ordinal()]) {
            case 1:
            case 2: {
                obj = new File(property, '.' + applicationName + '/');
                break;
            }
            case 3: {
                final String getenv = System.getenv("APPDATA");
                if (getenv != null) {
                    obj = new File(getenv, "." + applicationName + '/');
                    break;
                }
                obj = new File(property, '.' + applicationName + '/');
                break;
            }
            case 4: {
                obj = new File(property, "Library/Application Support/" + applicationName);
                break;
            }
            default: {
                obj = new File(property, applicationName + '/');
                break;
            }
        }
        if (!obj.exists() && !obj.mkdirs()) {
            throw new RuntimeException("The working directory could not be created: " + obj);
        }
        return obj;
    }
    
    private static IsomPreview_OS getPlatform() {
        final String lowerCase = System.getProperty("os.name").toLowerCase();
        if (lowerCase.contains("win")) {
            return IsomPreview_OS.windows;
        }
        if (lowerCase.contains("mac")) {
            return IsomPreview_OS.macos;
        }
        if (lowerCase.contains("solaris")) {
            return IsomPreview_OS.solaris;
        }
        if (lowerCase.contains("sunos")) {
            return IsomPreview_OS.solaris;
        }
        if (lowerCase.contains("linux")) {
            return IsomPreview_OS.linux;
        }
        if (lowerCase.contains("unix")) {
            return IsomPreview_OS.linux;
        }
        return IsomPreview_OS.unknown;
    }
    
    public IsomPreview() {
        this.currentReader = 0;
        this.zoom = 2;
        this.showHelp = true;
        this.running = true;
        this.zonesToRender = Collections.synchronizedList(new LinkedList<Object>());
        this.zoneMap = new Zone[64][64];
        this.workDir = this.getWorkingDirectory();
        for (int i = 0; i < 64; ++i) {
            for (int j = 0; j < 64; ++j) {
                this.zoneMap[i][j] = new Zone(null, i, j);
            }
        }
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addKeyListener(this);
        this.setFocusable(true);
        this.requestFocus();
        this.setBackground(Color.red);
    }
    
    public void loadLevel(final String levelName) {
        final int n = 0;
        this.yCam = n;
        this.xCam = n;
        this.level = new Level(new DirectoryLevelStorage(new File(this.workDir, "saves"), levelName, false), levelName, new Random().nextLong());
        this.level.skyDarken = 0;
        synchronized (this.zonesToRender) {
            this.zonesToRender.clear();
            for (int i = 0; i < 64; ++i) {
                for (int j = 0; j < 64; ++j) {
                    this.zoneMap[i][j].init(this.level, i, j);
                }
            }
        }
    }
    
    private void setBrightness(final int i) {
        synchronized (this.zonesToRender) {
            this.level.skyDarken = i;
            this.zonesToRender.clear();
            for (int j = 0; j < 64; ++j) {
                for (int k = 0; k < 64; ++k) {
                    this.zoneMap[j][k].init(this.level, j, k);
                }
            }
        }
    }
    
    public void start() {
        new IsomPreview_RenderThread(this).start();
        for (int i = 0; i < 8; ++i) {
            new Thread(this).start();
        }
    }
    
    public void stop() {
        this.running = false;
    }
    
    private Zone getZone(final int x, final int y) {
        final Zone zone = this.zoneMap[x & 0x3F][y & 0x3F];
        if (zone.x == x && zone.y == y) {
            return zone;
        }
        synchronized (this.zonesToRender) {
            this.zonesToRender.remove(zone);
        }
        zone.init(x, y);
        return zone;
    }
    
    public void run() {
        final ZoneRenderer zoneRenderer = new ZoneRenderer();
        while (this.running) {
            Zone zone = null;
            synchronized (this.zonesToRender) {
                if (this.zonesToRender.size() > 0) {
                    zone = this.zonesToRender.remove(0);
                }
            }
            if (zone != null) {
                if (this.currentReader - zone.lastVisible < 2) {
                    zoneRenderer.render(zone);
                    this.repaint();
                }
                else {
                    zone.addedToRenderQueue = false;
                }
            }
            try {
                Thread.sleep(2L);
            }
            catch (final InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @Override
    public void update(final Graphics g) {
    }
    
    @Override
    public void paint(final Graphics g) {
    }
    
    public void render() {
        final BufferStrategy bufferStrategy = this.getBufferStrategy();
        if (bufferStrategy == null) {
            this.createBufferStrategy(2);
            return;
        }
        this.render((Graphics2D)bufferStrategy.getDrawGraphics());
        bufferStrategy.show();
    }
    
    public void render(final Graphics2D g) {
        ++this.currentReader;
        final AffineTransform transform = g.getTransform();
        g.setClip(0, 0, this.getWidth(), this.getHeight());
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.translate(this.getWidth() / 2, this.getHeight() / 2);
        g.scale(this.zoom, this.zoom);
        g.translate(this.xCam, this.yCam);
        if (this.level != null) {
            final Pos sharedSpawnPos = this.level.getSharedSpawnPos();
            g.translate(-(sharedSpawnPos.x + sharedSpawnPos.z), -(-sharedSpawnPos.x + sharedSpawnPos.z) + 64);
        }
        final Rectangle clipBounds = g.getClipBounds();
        g.setColor(new Color(-15724512));
        g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
        final int n = 16;
        final int n2 = 3;
        final int n3 = clipBounds.x / n / 2 - 2 - n2;
        final int n4 = (clipBounds.x + clipBounds.width) / n / 2 + 1 + n2;
        final int n5 = clipBounds.y / n - 1 - n2 * 2;
        for (int n6 = (clipBounds.y + clipBounds.height + 16 + 128) / n + 1 + n2 * 2, i = n5; i <= n6; ++i) {
            for (int j = n3; j <= n4; ++j) {
                final Zone zone = this.getZone(j - (i >> 1), j + (i + 1 >> 1));
                zone.lastVisible = this.currentReader;
                if (!zone.rendered) {
                    if (!zone.addedToRenderQueue) {
                        zone.addedToRenderQueue = true;
                        this.zonesToRender.add(zone);
                    }
                }
                else {
                    zone.addedToRenderQueue = false;
                    if (!zone.noContent) {
                        g.drawImage(zone.image, j * n * 2 + (i & 0x1) * n, i * n - 128 - 16, null);
                    }
                }
            }
        }
        if (this.showHelp) {
            g.setTransform(transform);
            final int n7 = this.getHeight() - 32 - 4;
            g.setColor(new Color(Integer.MIN_VALUE, true));
            g.fillRect(4, this.getHeight() - 32 - 4, this.getWidth() - 8, 32);
            g.setColor(Color.WHITE);
            final String str = "F1 - F5: load levels   |   0-9: Set time of day   |   Space: return to spawn   |   Double click: zoom   |   Escape: hide this text";
            g.drawString(str, this.getWidth() / 2 - g.getFontMetrics().stringWidth(str) / 2, n7 + 20);
        }
        g.dispose();
    }
    
    public void mouseDragged(final MouseEvent m) {
        final int xDrag = m.getX() / this.zoom;
        final int yDrag = m.getY() / this.zoom;
        this.xCam += xDrag - this.xDrag;
        this.yCam += yDrag - this.yDrag;
        this.xDrag = xDrag;
        this.yDrag = yDrag;
        this.repaint();
    }
    
    public void mouseMoved(final MouseEvent m) {
    }
    
    public void mouseClicked(final MouseEvent m) {
        if (m.getClickCount() == 2) {
            this.zoom = 3 - this.zoom;
            this.repaint();
        }
    }
    
    public void mouseEntered(final MouseEvent m) {
    }
    
    public void mouseExited(final MouseEvent m) {
    }
    
    public void mousePressed(final MouseEvent m) {
        final int xDrag = m.getX() / this.zoom;
        final int yDrag = m.getY() / this.zoom;
        this.xDrag = xDrag;
        this.yDrag = yDrag;
    }
    
    public void mouseReleased(final MouseEvent m) {
    }
    
    public void keyPressed(final KeyEvent ke) {
        if (ke.getKeyCode() == 48) {
            this.setBrightness(11);
        }
        if (ke.getKeyCode() == 49) {
            this.setBrightness(10);
        }
        if (ke.getKeyCode() == 50) {
            this.setBrightness(9);
        }
        if (ke.getKeyCode() == 51) {
            this.setBrightness(7);
        }
        if (ke.getKeyCode() == 52) {
            this.setBrightness(6);
        }
        if (ke.getKeyCode() == 53) {
            this.setBrightness(5);
        }
        if (ke.getKeyCode() == 54) {
            this.setBrightness(3);
        }
        if (ke.getKeyCode() == 55) {
            this.setBrightness(2);
        }
        if (ke.getKeyCode() == 56) {
            this.setBrightness(1);
        }
        if (ke.getKeyCode() == 57) {
            this.setBrightness(0);
        }
        if (ke.getKeyCode() == 112) {
            this.loadLevel("World1");
        }
        if (ke.getKeyCode() == 113) {
            this.loadLevel("World2");
        }
        if (ke.getKeyCode() == 114) {
            this.loadLevel("World3");
        }
        if (ke.getKeyCode() == 115) {
            this.loadLevel("World4");
        }
        if (ke.getKeyCode() == 116) {
            this.loadLevel("World5");
        }
        if (ke.getKeyCode() == 32) {
            final int n = 0;
            this.yCam = n;
            this.xCam = n;
        }
        if (ke.getKeyCode() == 27) {
            this.showHelp = !this.showHelp;
        }
        this.repaint();
    }
    
    public void keyReleased(final KeyEvent ke) {
    }
    
    public void keyTyped(final KeyEvent ke) {
    }
}
