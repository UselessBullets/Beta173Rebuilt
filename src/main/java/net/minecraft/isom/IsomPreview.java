package net.minecraft.isom;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.util.*;
import java.util.List;

import javax.swing.JFrame;

import net.minecraft.Pos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.DirectoryLevelStorage;

// Useless - Isom package was pulled straight from the Beta 1.9-pre6 jar as it had source for this package, thanks @genericpnpmonit0r for telling me this
public class IsomPreview extends Canvas implements Runnable, MouseListener, MouseMotionListener, KeyListener {
    private enum OS {
        linux, solaris, windows, macos, unknown
    }

    private static final int CACHE_WIDTH = 64;
    private static final int CACHE_HEIGHT = 64;
    private static final long serialVersionUID = 1L;
    private int currentRender = 0;
    private int zoom = 2;
    private boolean showHelp = true;


    private Level level;
    private File workDir;
    private boolean running = true;

    private List<Zone> zonesToRender = Collections.synchronizedList(new LinkedList<Zone>());
    private Zone[][] zoneMap = new Zone[CACHE_WIDTH][CACHE_HEIGHT];
    private int xCam, yCam;

    public File getWorkingDirectory() {
        if (workDir == null) workDir = getWorkingDirectory("minecraft");
        return workDir;
    }

    public File getWorkingDirectory(final String applicationName) {
        final String userHome = System.getProperty("user.home", ".");
        final File workingDirectory;
        switch (getPlatform()) {
        case linux:
        case solaris:
            workingDirectory = new File(userHome, '.' + applicationName + '/');
            break;
        case windows:
            final String applicationData = System.getenv("APPDATA");
            if (applicationData != null) workingDirectory = new File(applicationData, "." + applicationName + '/');
            else workingDirectory = new File(userHome, '.' + applicationName + '/');
            break;
        case macos:
            workingDirectory = new File(userHome, "Library/Application Support/" + applicationName);
            break;
        default:
            workingDirectory = new File(userHome, applicationName + '/');
        }
        if (!workingDirectory.exists()) if (!workingDirectory.mkdirs()) throw new RuntimeException("The working directory could not be created: " + workingDirectory);
        return workingDirectory;
    }

    private static OS getPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) return OS.windows;
        if (osName.contains("mac")) return OS.macos;
        if (osName.contains("solaris")) return OS.solaris;
        if (osName.contains("sunos")) return OS.solaris;
        if (osName.contains("linux")) return OS.linux;
        if (osName.contains("unix")) return OS.linux;
        return OS.unknown;
    }

    public IsomPreview() {
        workDir = getWorkingDirectory();

        for (int x = 0; x < CACHE_WIDTH; x++)
            for (int y = 0; y < CACHE_HEIGHT; y++) {
                zoneMap[x][y] = new Zone(null, x, y);
            }

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);
        requestFocus();
        setBackground(Color.red);
    }

    public void loadLevel(String levelName) {
        xCam = yCam = 0;
        level = new Level(new DirectoryLevelStorage(new File(workDir, "saves"), levelName, false), levelName, new Random().nextLong()); // Useless - this used LevelSettings in b1.9-pre6 while b1.7.3 just takes in the seed, so the seed of the LevelSettings was taken and used here

        level.skyDarken = 0;

        synchronized (zonesToRender) {
            zonesToRender.clear();

            for (int x = 0; x < CACHE_WIDTH; x++)
                for (int y = 0; y < CACHE_HEIGHT; y++) {
                    zoneMap[x][y].init(level, x, y);
                }
        }
    }


    private void setBrightness(int i) {
        synchronized (zonesToRender) {
            level.skyDarken = i;
            zonesToRender.clear();

            for (int x = 0; x < CACHE_WIDTH; x++)
                for (int y = 0; y < CACHE_HEIGHT; y++) {
                    zoneMap[x][y].init(level, x, y);
                }
        }
    }

    public void start() {
        new Thread() {
            public void run() {
                while (running) {
                    render();
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                    }
                }
            }
        }.start();

        for (int i = 0; i < 8; i++) {
            new Thread(this).start();
        }
    }

    public void stop() {
        running = false;
    }

    private Zone getZone(int x, int y) {
        int xSlot = x & (CACHE_WIDTH - 1);
        int ySlot = y & (CACHE_HEIGHT - 1);
        Zone z = zoneMap[xSlot][ySlot];
        if (z.x == x && z.y == y) {
            return z;
        } else {
            synchronized (zonesToRender) {
                zonesToRender.remove(z);
            }
            z.init(x, y);
            return z;
        }
    }

    public void run() {
        ZoneRenderer zoneRenderer = new ZoneRenderer();

        while (running) {
            Zone zone = null;
            synchronized (zonesToRender) {
                if (zonesToRender.size() > 0) {
                    zone = zonesToRender.remove(0);
                }
            }

            if (zone != null) {
                if ((currentRender - zone.lastVisible) < 2) {
                    zoneRenderer.render(zone);
                    repaint();
                } else {
                    zone.addedToRenderQueue = false;
                }
            }
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(Graphics g) {
    }

    public void paint(Graphics _g) {
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(2);
            return;
        }
        render((Graphics2D) bs.getDrawGraphics());
        bs.show();
    }

    public void render(Graphics2D g) {
        currentRender++;

        AffineTransform at = g.getTransform();
        g.setClip(0, 0, getWidth(), getHeight());
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        g.translate(getWidth() / 2, getHeight() / 2);
        g.scale(zoom, zoom);
        g.translate(xCam, yCam);
        if (level != null) {
            Pos spawnPos = level.getSharedSpawnPos();
            g.translate(-(spawnPos.x + spawnPos.z), -(-spawnPos.x + spawnPos.z) + 64);
        }
        Rectangle r = g.getClipBounds();
        g.setColor(new Color(0xff101020));
        g.fillRect(r.x, r.y, r.width, r.height);

        int w = Zone.CHUNK_SIZE;

        int rr = 3;
        int x0 = (r.x) / w / 2 - 2 - rr;
        int x1 = ((r.x + r.width)) / w / 2 + 1 + rr;
        int y0 = (r.y) / w - 1 - rr * 2;
        int y1 = ((r.y + r.height) + Zone.CHUNK_SIZE + Level.MAX_HEIGHT /*Useless - was level.depth in b1.9-pre6*/) / w + 1 + rr * 2;

        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                int xSlot = x - (y >> 1);
                int ySlot = x + ((y + 1) >> 1);
                Zone zone = getZone(xSlot, ySlot);
                zone.lastVisible = currentRender;
                if (!zone.rendered) {
                    if (!zone.addedToRenderQueue) {
                        zone.addedToRenderQueue = true;
                        zonesToRender.add(zone);
                    }
                } else {
                    zone.addedToRenderQueue = false;
                    if (!zone.noContent) {
                        int xp = x * w * 2 + (y & 1) * w;
                        int yp = y * w - Level.MAX_HEIGHT /*Useless - was level.depth in b1.9-pre6*/ - Zone.CHUNK_SIZE;

                        g.drawImage(zone.image, xp, yp, null);
                    }
                }
            }
        }

        if (showHelp) {
            g.setTransform(at);
            int y = getHeight() - 32 - 4;
            g.setColor(new Color(0x80000000, true));
            // g.setComposite(AlphaComposite.SrcOver);
            // g.setPaint(Paint)
            g.fillRect(4, getHeight() - 32 - 4, getWidth() - 8, 32);
            // g.fillRect(4, getHeight() / 2 - 50, getWidth() - 8, 32);
            g.setColor(Color.WHITE);
            /*
             * String msg = "Minecraft Level Visualizer"; if (level != null) {
             * msg = level.name; } g.drawString(msg, getWidth() / 2 -
             * g.getFontMetrics().stringWidth(msg) / 2, getHeight() / 2 - 50 +
             * 20);
             */

            String msg = "F1 - F5: load levels   |   0-9: Set time of day   |   Space: return to spawn   |   Double click: zoom   |   Escape: hide this text";
            g.drawString(msg, getWidth() / 2 - g.getFontMetrics().stringWidth(msg) / 2, y + 20);
        }

        g.dispose();
    }

    public static void main(String[] args) {
        IsomPreview isomPreview = new IsomPreview();

        JFrame frame = new JFrame("IsomPreview");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(isomPreview, BorderLayout.CENTER);
        frame.setSize(854, 480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        isomPreview.start();
    }

    private int xDrag, yDrag;

    public void mouseDragged(MouseEvent m) {
        int x = m.getX() / zoom;
        int y = m.getY() / zoom;
        xCam += (x - xDrag);
        yCam += (y - yDrag);
        xDrag = x;
        yDrag = y;
        repaint();
    }

    public void mouseMoved(MouseEvent arg0) {
    }

    public void mouseClicked(MouseEvent m) {
        if (m.getClickCount() == 2) {
            zoom = 3 - zoom;
            repaint();
        }
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mousePressed(MouseEvent m) {
        int x = m.getX() / zoom;
        int y = m.getY() / zoom;
        xDrag = x;
        yDrag = y;
    }

    public void mouseReleased(MouseEvent arg0) {
    }

    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_0) setBrightness(11);
        if (ke.getKeyCode() == KeyEvent.VK_1) setBrightness(10);
        if (ke.getKeyCode() == KeyEvent.VK_2) setBrightness(9);
        if (ke.getKeyCode() == KeyEvent.VK_3) setBrightness(7);
        if (ke.getKeyCode() == KeyEvent.VK_4) setBrightness(6);
        if (ke.getKeyCode() == KeyEvent.VK_5) setBrightness(5);
        if (ke.getKeyCode() == KeyEvent.VK_6) setBrightness(3);
        if (ke.getKeyCode() == KeyEvent.VK_7) setBrightness(2);
        if (ke.getKeyCode() == KeyEvent.VK_8) setBrightness(1);
        if (ke.getKeyCode() == KeyEvent.VK_9) setBrightness(0);

        if (ke.getKeyCode() == KeyEvent.VK_F1) loadLevel("World1");
        if (ke.getKeyCode() == KeyEvent.VK_F2) loadLevel("World2");
        if (ke.getKeyCode() == KeyEvent.VK_F3) loadLevel("World3");
        if (ke.getKeyCode() == KeyEvent.VK_F4) loadLevel("World4");
        if (ke.getKeyCode() == KeyEvent.VK_F5) loadLevel("World5");

        if (ke.getKeyCode() == KeyEvent.VK_SPACE) xCam = yCam = 0;
        if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) showHelp = !showHelp;
        repaint();
    }

    public void keyReleased(KeyEvent arg0) {
    }

    public void keyTyped(KeyEvent arg0) {
    }
}
