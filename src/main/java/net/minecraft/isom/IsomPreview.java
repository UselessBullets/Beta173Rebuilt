// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.isom;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import net.minecraft.Pos;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.Random;
import net.minecraft.world.level.storage.DirectoryLevelStorage;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.io.File;
import net.minecraft.world.level.Level;

import javax.swing.*;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.KeyListener;

public class IsomPreview extends Canvas implements KeyListener, MouseListener, MouseMotionListener, Runnable
{
    private static final int CACHE_WIDTH = 64;
    private static final int CACHE_HEIGHT = 64;
    private int currentReader = 0;
    private int zoom = 2;
    private boolean showHelp = true;
    private Level level;
    private File workDir;
    private boolean running = true;
    private List<Zone> zonesToRender = Collections.synchronizedList(new LinkedList<>());
    private Zone[][] zoneMap = new Zone[CACHE_WIDTH][CACHE_HEIGHT];
    private int xCam;
    private int yCam;
    private int xDrag;
    private int yDrag;
    
    public File getWorkingDirectory() {
        if (this.workDir == null) this.workDir = this.getWorkingDirectory("minecraft");
        return this.workDir;
    }
    
    public File getWorkingDirectory(final String applicationName) {
        final String userHome = System.getProperty("user.home", ".");
        File workingDirectory;
        switch (getPlatform()) {
            case linux:
            case solaris: {
                workingDirectory = new File(userHome, '.' + applicationName + '/');
                break;
            }
            case windows: {
                final String getenv = System.getenv("APPDATA");
                if (getenv != null) {
                    workingDirectory = new File(getenv, "." + applicationName + '/');
                    break;
                }
                workingDirectory = new File(userHome, '.' + applicationName + '/');
                break;
            }
            case macos: {
                workingDirectory = new File(userHome, "Library/Application Support/" + applicationName);
                break;
            }
            default: {
                workingDirectory = new File(userHome, applicationName + '/');
                break;
            }
        }
        if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
            throw new RuntimeException("The working directory could not be created: " + workingDirectory);
        }
        return workingDirectory;
    }
    
    private static OS getPlatform() {
        final String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) return OS.windows;
        if (osName.contains("mac")) return OS.macos;
        if (osName.contains("solaris")) return OS.solaris;
        if (osName.contains("sunos")) return OS.solaris;
        if (osName.contains("linux")) return OS.linux;
        if (osName.contains("unix")) return OS.linux;
        return OS.unknown;
    }
    
    public IsomPreview() {
        this.workDir = this.getWorkingDirectory();

        for (int x = 0; x < CACHE_WIDTH; ++x) {
            for (int y = 0; y < CACHE_HEIGHT; ++y) {
                this.zoneMap[x][y] = new Zone(null, x, y);
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
        this.xCam = this.yCam = 0;

        this.level = new Level(new DirectoryLevelStorage(new File(this.workDir, "saves"), levelName, false), levelName, new Random().nextLong());
        this.level.skyDarken = 0;
        synchronized (this.zonesToRender) {
            this.zonesToRender.clear();

            for (int x = 0; x < CACHE_WIDTH; ++x) {
                for (int y = 0; y < CACHE_HEIGHT; ++y) {
                    this.zoneMap[x][y].init(this.level, x, y);
                }
            }
        }
    }
    
    private void setBrightness(final int i) {
        synchronized (this.zonesToRender) {
            this.level.skyDarken = i;
            this.zonesToRender.clear();

            for (int x = 0; x < CACHE_WIDTH; ++x) {
                for (int y = 0; y < CACHE_HEIGHT; ++y) {
                    this.zoneMap[x][y].init(this.level, x, y);
                }
            }
        }
    }
    
    public void start() {
        new Thread(() -> {
            while (this.running) {
                render();

                try {
                    Thread.sleep(1L);
                }
                catch (final Exception e) {}
            }
        }).start();
        for (int i = 0; i < 8; ++i) {
            new Thread(this).start();
        }
    }
    
    public void stop() {
        this.running = false;
    }
    
    private Zone getZone(final int x, final int y) {
        int xSlot = x & (CACHE_WIDTH - 1);
        int ySlot = y & (CACHE_HEIGHT - 1);
        final Zone z = this.zoneMap[xSlot][ySlot];
        if (z.x == x && z.y == y) return z;

        synchronized (this.zonesToRender) {
            this.zonesToRender.remove(z);
        }

        z.init(x, y);
        return z;
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
        final BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(2);
        } else {
            this.render((Graphics2D) bs.getDrawGraphics());
            bs.show();
        }
    }
    
    public void render(final Graphics2D g) {
        ++this.currentReader;
        final AffineTransform at = g.getTransform();
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
        g.setColor(new Color(0xff101020));
        g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
        final int w = 16;
        final int rr = 3;
        final int x0 = clipBounds.x / w / 2 - 2 - rr;
        final int x1 = (clipBounds.x + clipBounds.width) / w / 2 + 1 + rr;
        final int y0 = clipBounds.y / w - 1 - rr * 2;
        final int y1 = (clipBounds.y + clipBounds.height + 16 + 128) / w + 1 + rr * 2;

        for (int y = y0; y <= y1; ++y) {
            for (int x = x0; x <= x1; ++x) {
                int xSlot = x - (y >> 1);
                int ySlot = x + (y + 1 >> 1);
                Zone zone = this.getZone(xSlot, ySlot);
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
                        int xp = x * w * 2 + (y & 0x1) * w;
                        int yp = y * w - 128 - 16;
                        g.drawImage(zone.image, xp, yp, null);
                    }
                }
            }
        }

        if (this.showHelp) {
            g.setTransform(at);
            final int n7 = this.getHeight() - 32 - 4;
            g.setColor(new Color(Integer.MIN_VALUE, true));
            g.fillRect(4, this.getHeight() - 32 - 4, this.getWidth() - 8, 32);
            g.setColor(Color.WHITE);
            final String str = "F1 - F5: load levels   |   0-9: Set time of day   |   Space: return to spawn   |   Double click: zoom   |   Escape: hide this text";
            g.drawString(str, this.getWidth() / 2 - g.getFontMetrics().stringWidth(str) / 2, n7 + 20);
        }

        g.dispose();
    }

    // Useless - Below method taken from b1.2 leak, presumably was just cut out since its not the main entrypoint for either client or server jar, running it as the entrypoint seems to work as intended
    public static void main(String[] args) {
        IsomPreview isomPreview = new IsomPreview();
        JFrame frame = new JFrame("IsomPreview");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(isomPreview, "Center");
        frame.setSize(854, 480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        isomPreview.start();
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
        if (ke.getKeyCode() == KeyEvent.VK_0) this.setBrightness(11);
        if (ke.getKeyCode() == KeyEvent.VK_1) this.setBrightness(10);
        if (ke.getKeyCode() == KeyEvent.VK_2) this.setBrightness(9);
        if (ke.getKeyCode() == KeyEvent.VK_3) this.setBrightness(7);
        if (ke.getKeyCode() == KeyEvent.VK_4) this.setBrightness(6);
        if (ke.getKeyCode() == KeyEvent.VK_5) this.setBrightness(5);
        if (ke.getKeyCode() == KeyEvent.VK_6) this.setBrightness(3);
        if (ke.getKeyCode() == KeyEvent.VK_7) this.setBrightness(2);
        if (ke.getKeyCode() == KeyEvent.VK_8) this.setBrightness(1);
        if (ke.getKeyCode() == KeyEvent.VK_9) this.setBrightness(0);

        if (ke.getKeyCode() == KeyEvent.VK_F1) this.loadLevel("World1");
        if (ke.getKeyCode() == KeyEvent.VK_F2) this.loadLevel("World2");
        if (ke.getKeyCode() == KeyEvent.VK_F3) this.loadLevel("World3");
        if (ke.getKeyCode() == KeyEvent.VK_F4) this.loadLevel("World4");
        if (ke.getKeyCode() == KeyEvent.VK_F5) this.loadLevel("World5");

        if (ke.getKeyCode() == KeyEvent.VK_SPACE) this.xCam = this.yCam = 0;
        if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) this.showHelp = !this.showHelp;
        this.repaint();
    }
    
    public void keyReleased(final KeyEvent ke) {
    }
    
    public void keyTyped(final KeyEvent ke) {
    }

    enum OS
    {
        linux,
        solaris,
        windows,
        macos,
        unknown;
    }
}
