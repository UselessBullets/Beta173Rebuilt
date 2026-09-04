// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import net.minecraft.SharedConstants;
import net.minecraft.client.Options.Option;
import net.minecraft.client.gamemode.SurvivalMode;
import net.minecraft.client.multiplayer.MultiplayerLocalPlayer;
import net.minecraft.client.multiplayer.ClientConnection;

import java.awt.BorderLayout;
import java.awt.Frame;
import net.minecraft.Pos;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.PortalForcer;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.client.gui.ChatScreen;
import net.minecraft.client.gui.inventory.InventoryScreen;
import net.minecraft.client.gui.InBedChatScreen;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import util.Mth;
import net.minecraft.world.level.chunk.ChunkCache;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.gamemode.CreativeMode;
import net.minecraft.client.gui.PauseScreen;
import net.minecraft.client.gui.OutOfMemoryScreen;
import net.minecraft.client.renderer.Chunk;
import net.minecraft.client.renderer.TileRenderer;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.client.gui.LevelConflictScreen;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import org.lwjgl.util.glu.GLU;
import net.minecraft.client.gui.DeathScreen;
import net.minecraft.client.gui.ErrorScreen;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.gui.ScreenSizeCalculator;
import java.awt.Graphics;
import net.minecraft.client.title.TitleScreen;
import net.minecraft.client.multiplayer.ConnectScreen;
import net.minecraft.client.renderer.ptexture.FireTexture;
import net.minecraft.client.renderer.ptexture.LavaSideTexture;
import net.minecraft.client.renderer.ptexture.WaterSideTexture;
import net.minecraft.client.renderer.ptexture.ClockTexture;
import net.minecraft.client.renderer.ptexture.CompassTexture;
import net.minecraft.client.renderer.ptexture.PortalTexture;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Mouse;
import org.lwjgl.input.Keyboard;
import net.minecraft.stats.Achievements;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.WaterColor;
import net.minecraft.world.level.storage.McRegionLevelStorageSource;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.Display;
import java.awt.Color;
import net.minecraft.stats.Stats;
import java.awt.Component;
import net.minecraft.client.renderer.ptexture.LavaTexture;
import net.minecraft.client.renderer.ptexture.WaterTexture;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.File;
import net.minecraft.client.skins.TexturePackRepository;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.world.phys.HitResult;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.achievement.AchievementPopup;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.Textures;
import java.awt.Canvas;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.Level;
import net.minecraft.client.gamemode.GameMode;

import static org.lwjgl.opengl.GL11.*;

public abstract class Minecraft implements Runnable
{
    public static byte[] __unused_byte_buffer = new byte[0xa00000];
    private static Minecraft instance;
    public static final boolean FLYBY_MODE = false; // Useless - In the LCE and b1.2 leaks
    public static final boolean DEADMAU5_CAMERA_CHEATS = false; // Useless - In the LCE leaks
    public static final String VERSION_STRING = "Minecraft " + SharedConstants.VERSION_STRING;
    public GameMode gameMode;
    private boolean fullscreen = false;
    private boolean hasCrashed = false;
    public int width, height;
    private OpenGLCapabilities openGLCapabilities;
    private Timer timer = new Timer(SharedConstants.TICKS_PER_SECOND);
    public Level level;
    public LevelRenderer levelRenderer;
    public LocalPlayer player;
    public Mob cameraTargetPlayer;
    public ParticleEngine particleEngine;
    public User user = null;
    public String serverDomain;
    public Canvas parent;
    public boolean appletMode = true;
    public volatile boolean pause = false;
    public Textures textures;
    public Font font;
    public Screen screen = null;
    public ProgressRenderer progressRenderer = new ProgressRenderer(this);
    public GameRenderer gameRenderer;
    private BackgroundDownloader bgLoader;
    private int ticks = 0;
    private int missTime = 0;
    private int orgWidth, orgHeight;
    public AchievementPopup achievementPopup = new AchievementPopup(this);
    public Gui gui;
    public boolean noRender = false;
    public HumanoidModel humanoidModel = new HumanoidModel(0.0f);
    public HitResult hitResult = null;
    public Options options;
    protected MinecraftApplet minecraftApplet;
    public SoundEngine soundEngine = new SoundEngine();
    public MouseHandler mouseHandler;
    public TexturePackRepository skins;
    private File workingDirectory;
    private LevelStorageSource levelSource;
    public static long[] frameTimes = new long[512];
    public static long[] tickTimes = new long[512];
    public static int frameTimePos = 0;
    public static long warezTime = 0L;
    public StatsCounter stats;
    private String connectToIp;
    private int connectToPort;
    private WaterTexture waterTexture = new WaterTexture();
    private LavaTexture lavaTexture = new LavaTexture();
    private static File workDir = null;
    public volatile boolean running = true;
    public String fpsString = "";
    boolean wasDown = false;
    long lastTimer = -1L;
    public boolean mouseGrabbed = false;
    private int lastClickTick = 0;
    public boolean isRaining = false;
    long lastTickTime = System.currentTimeMillis();
    private int recheckPlayerIn = 0;
    
    public Minecraft(final Component component, final Canvas parent, final MinecraftApplet minecraftApplet, final int width, final int height, final boolean fullscreen) {
        Stats.init();
        this.orgHeight = height;
        this.fullscreen = fullscreen;
        this.minecraftApplet = minecraftApplet;
        new Thread("Timer hack thread") {

            {
                this.setDaemon(true);
                this.start();
            }

            @Override
            public void run() {
                while (Minecraft.this.running) {
                    try {
                        Thread.sleep(2147483647L);
                    }
                    catch (final InterruptedException ex) {}
                }
            }
        };
        this.parent = parent;
        this.width = width;
        this.height = height;
        this.fullscreen = fullscreen;
        if (minecraftApplet == null || "true".equals(minecraftApplet.getParameter("stand-alone"))) {
            this.appletMode = false;
        }
        Minecraft.instance = this;
    }
    
    public void crash(final CrashReport crashReport) {
        this.hasCrashed = true;
        this.onCrash(crashReport);
    }
    
    public abstract void onCrash(final CrashReport crashReport);
    
    public void connectTo(final String ip, final int port) {
        this.connectToIp = ip;
        this.connectToPort = port;
    }
    
    public void init() throws LWJGLException {
        if (this.parent != null) {
            final Graphics g = this.parent.getGraphics();
            if (g != null) {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, this.width, this.height);
                g.dispose();
            }
            Display.setParent(this.parent);
        }
        else {
            if (this.fullscreen) {
                Display.setFullscreen(true);
                this.width = Display.getDisplayMode().getWidth();
                this.height = Display.getDisplayMode().getHeight();
                if (this.width <= 0) this.width = 1;
                if (this.height <= 0) this.height = 1;
            } else {
                Display.setDisplayMode(new DisplayMode(this.width, this.height));
            }
        }
        Display.setTitle(VERSION_STRING);
        try {
            Display.create();
            // Useless - Below commented out java code is from the LCE leak
            /*
             * System.out.println("LWJGL version: " + Sys.getVersion());
             * System.out.println("GL RENDERER: " +
             * GL11.glGetString(GL11.GL_RENDERER));
             * System.out.println("GL VENDOR: " +
             * GL11.glGetString(GL11.GL_VENDOR));
             * System.out.println("GL VERSION: " +
             * GL11.glGetString(GL11.GL_VERSION)); ContextCapabilities caps =
             * GLContext.getCapabilities(); System.out.println("OpenGL 3.0: " +
             * caps.OpenGL30); System.out.println("OpenGL 3.1: " +
             * caps.OpenGL31); System.out.println("OpenGL 3.2: " +
             * caps.OpenGL32); System.out.println("ARB_compatibility: " +
             * caps.GL_ARB_compatibility); if (caps.OpenGL32) { IntBuffer buffer
             * = ByteBuffer.allocateDirect(16 *
             * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
             * GL11.glGetInteger(GL32.GL_CONTEXT_PROFILE_MASK, buffer); int
             * profileMask = buffer.get(0); System.out.println("PROFILE MASK: "
             * + Integer.toBinaryString(profileMask));
             * System.out.println("CORE PROFILE: " + ((profileMask &
             * GL32.GL_CONTEXT_CORE_PROFILE_BIT) != 0));
             * System.out.println("COMPATIBILITY PROFILE: " + ((profileMask &
             * GL32.GL_CONTEXT_COMPATIBILITY_PROFILE_BIT) != 0)); }
             */
        }
        catch (final LWJGLException e) {
            // This COULD be because of a bug! A delay followed by a new attempt
            // is supposed to fix it.
            e.printStackTrace();
            try {
                Thread.sleep(1000L);
            }
            catch (final InterruptedException ex2) {}
            Display.create();
        }

        // Useless - Sourced from LCE leak
        if (Minecraft.FLYBY_MODE) {
            glPixelStorei(GL_PACK_ALIGNMENT, 1);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        }

        // glClearColor(0.2f, 0.2f, 0.2f, 1);

        this.workingDirectory = getWorkingDirectory();
        this.levelSource = new McRegionLevelStorageSource(new File(this.workingDirectory, "saves"));
        this.options = new Options(this, this.workingDirectory);
        this.skins = new TexturePackRepository(this, this.workingDirectory);
        this.textures = new Textures(this.skins, this.options);
//        renderLoadingScreen();

        this.font = new Font(this.options, "/font/default.png", this.textures);

        WaterColor.init(this.textures.loadTexturePixels("/misc/watercolor.png"));
        GrassColor.init(this.textures.loadTexturePixels("/misc/grasscolor.png"));
        FoliageColor.init(this.textures.loadTexturePixels("/misc/foliagecolor.png"));

        this.gameRenderer = new GameRenderer(this);
        EntityRenderDispatcher.instance.itemInHandRenderer = new ItemInHandRenderer(this);

        this.stats = new StatsCounter(this.user, this.workingDirectory);

        Achievements.openInventory.setDescFormatter(i18nValue -> String.format(i18nValue, Keyboard.getKeyName(options.keyBuild.key)));

        this.renderLoadingScreen();

        Keyboard.create();
        Mouse.create();
        this.mouseHandler = new MouseHandler(this.parent);
        try {
            Controllers.create();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        this.checkGlError("Pre startup");

        // width = Display.getDisplayMode().getWidth();
        // height = Display.getDisplayMode().getHeight();

        glEnable(GL_TEXTURE_2D);
        glShadeModel(GL_SMOOTH);
        glClearDepth(1.0);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0.1f);
        glCullFace(GL_BACK);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        this.checkGlError("Startup");

        this.openGLCapabilities = new OpenGLCapabilities();
        this.soundEngine.init(this.options);

        this.textures.addDynamicTexture(this.lavaTexture);
        this.textures.addDynamicTexture(this.waterTexture);
        this.textures.addDynamicTexture(new PortalTexture());
        this.textures.addDynamicTexture(new CompassTexture(this));
        this.textures.addDynamicTexture(new ClockTexture(this));
        this.textures.addDynamicTexture(new WaterSideTexture());
        this.textures.addDynamicTexture(new LavaSideTexture());
        this.textures.addDynamicTexture(new FireTexture(0));
        this.textures.addDynamicTexture(new FireTexture(1));

        this.levelRenderer = new LevelRenderer(this, this.textures);
        glViewport(0, 0, this.width, this.height);
        this.particleEngine = new ParticleEngine(this.level, this.textures);
        try {
            this.bgLoader = new BackgroundDownloader(this.workingDirectory, this);
            this.bgLoader.start();
        }
        catch (final Exception e) {}

        this.checkGlError("Post startup");
        this.gui = new Gui(this);

        if (this.connectToIp != null) {
            this.setScreen(new ConnectScreen(this, this.connectToIp, this.connectToPort));
        }
        else {
            this.setScreen(new TitleScreen());
        }
    }
    
    private void renderLoadingScreen() throws LWJGLException {
        final ScreenSizeCalculator ssc = new ScreenSizeCalculator(this.options, this.width, this.height);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, ssc.rawWidth, ssc.rawHeight, 0.0, 1000.0, 3000.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(0.0f, 0.0f, -2000.0f);
        glViewport(0, 0, this.width, this.height);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        final Tesselator t = Tesselator.instance;
        glDisable(GL_LIGHTING);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_FOG);
        glBindTexture(GL_TEXTURE_2D, this.textures.loadTexture("/title/mojang.png"));
        t.begin();
        t.color(0xffffff);
        t.vertexUV(0.0, this.height, 0.0, 0.0, 0.0);
        t.vertexUV(this.width, this.height, 0.0, 0.0, 0.0);
        t.vertexUV(this.width, 0.0, 0.0, 0.0, 0.0);
        t.vertexUV(0.0, 0.0, 0.0, 0.0, 0.0);
        t.end();

        final int lw = 256;
        final int lh = 256;
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        t.color(0xffffff);
        this.blit((ssc.getWidth() - lw) / 2, (ssc.getHeight() - lh) / 2, 0, 0, lw, lh);
        glDisable(GL_LIGHTING);
        glDisable(GL_FOG);

        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0.1f);

        Display.swapBuffers();
    }
    
    public void blit(final int x, final int y, final int sx, final int sy, final int w, final int h) {
        final float us = 1 / 256.0f;
        final float vs = 1 / 256.0f;
        final Tesselator t = Tesselator.instance;
        t.begin();
        t.vertexUV(x + 0, y + h, 0.0, (sx + 0) * us, (sy + h) * vs);
        t.vertexUV(x + w, y + h, 0.0, (sx + w) * us, (sy + h) * vs);
        t.vertexUV(x + w, y + 0, 0.0, (sx + w) * us, (sy + 0) * vs);
        t.vertexUV(x + 0, y + 0, 0.0, (sx + 0) * us, (sy + 0) * vs);
        t.end();
    }
    
    public static File getWorkingDirectory() {
        if (Minecraft.workDir == null) Minecraft.workDir = getWorkingDirectory("minecraft");
        return Minecraft.workDir;
    }
    
    public static File getWorkingDirectory(final String applicationName) {
        final String userHome = System.getProperty("user.home", ".");
        File workingDirectory;
        switch (getPlatform()) {
            case linux:
            case solaris: {
                workingDirectory = new File(userHome, '.' + applicationName + '/');
                break;
            }
            case windows: {
                final String applicationData = System.getenv("APPDATA");
                if (applicationData != null) workingDirectory = new File(applicationData, "." + applicationName + '/');
                else workingDirectory = new File(userHome, '.' + applicationName + '/');
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
        if (!workingDirectory.exists() && !workingDirectory.mkdirs()) throw new RuntimeException("The working directory could not be created: " + workingDirectory);
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
    
    public LevelStorageSource getLevelSource() {
        return this.levelSource;
    }
    
    public void setScreen(Screen screen) {
        if (this.screen instanceof ErrorScreen) return;

        if (this.screen != null) {
            this.screen.removed();
        }

        if (screen instanceof TitleScreen) {
            this.stats.forceSend();
        }
        this.stats.forceSave();

        if (screen == null && this.level == null) {
            screen = new TitleScreen();
        }
        else if (screen == null && this.player.health <= 0) {
            screen = new DeathScreen();
        }

        if (screen instanceof TitleScreen) {
            this.gui.clearMessages();
        }

        this.screen = screen;
        if (this.screen != null) {
            this.releaseMouse();
            final ScreenSizeCalculator ssc = new ScreenSizeCalculator(this.options, this.width, this.height);
            int screenWidth = ssc.getWidth();
            int screenHeight = ssc.getHeight();
            screen.init(this, screenWidth, screenHeight);
            this.noRender = false;
        }
        else {
            this.grabMouse();
        }
    }
    
    private void checkGlError(final String str) {
        final int glError = glGetError();
        if (glError != 0) {
            final String errorString = GLU.gluErrorString(glError);
            System.out.println("########## GL ERROR ##########");
            System.out.println("@ " + str);
            System.out.println(glError + ": " + errorString);
        }
    }
    
    public void destroy() {
        try {
            this.stats.forceSend();
            this.stats.forceSave();

            if (this.minecraftApplet != null) {
                this.minecraftApplet.clearMemory();
            }

            try {
                if (this.bgLoader != null) {
                    this.bgLoader.halt();
                }
            }
            catch (final Exception e) {}

            System.out.println("Stopping!");

            try {
                this.setLevel(null);
            }
            catch (final Throwable e) {}

            try {
                MemoryTracker.release();
            }
            catch (final Throwable e) {}

            this.soundEngine.destroy();
            Mouse.destroy();
            Keyboard.destroy();
        }
        finally {
            Display.destroy();
            if (!this.hasCrashed) System.exit(0);
        }
        System.gc();
    }

    // Useless - Sourced from the b1.2 leak, local variable names are best guesses by me, there is no sources for that afaik
    public void generateFlyby() {
        this.gameMode = new SurvivalMode(this);
        this.selectLevel("flyby", "flyby", 0L); // Useless - Added second and third args, b1.2 didn't require those, seed can change what world seed is flown through though
        this.setScreen(null);

        double y = 0.0;
        ByteBuffer pixelBuffer = BufferUtils.createByteBuffer(this.width * this.height * 3);
        File flybyDirectory = new File(getWorkingDirectory(), "flyby");
        flybyDirectory.mkdir();

        // Useless - Sets the Truevision TGA image header bytes
        byte[] header = new byte[18];
        header[2] = 2; // Useless - Image type, 2 is "uncompressed true-color image"
        header[12] = (byte)(this.width % 256); // Useless - Image Width Byte 1
        header[13] = (byte)(this.width / 256); // Useless - Image Width Byte 2
        header[14] = (byte)(this.height % 256); // Useless - Image Height Byte 1
        header[15] = (byte)(this.height / 256); // Useless - Image Height Byte 2
        header[16] = 24;  // Useless - Pixel Depth bits per pixel

        byte[] pb = new byte[this.width * this.height * 3];
        int frame = -20;
        short fps = 352; // Useless - Raw assumption, thinking this is the FPS notch typically had for mc, so its used to calculate how many seconds flyByLength should be
        int flybyLength = fps * 60;
        this.player.yRot = this.player.yRotO = 12.0F; // Useless - Set the angle that the flyby goes in
        double ySin = -Math.sin(this.player.yRot * Math.PI / 180.0);
        double yCos = Math.cos(this.player.yRot * Math.PI / 180.0);
        // Useless - Initial position of the player for the flyby
        this.player.x = this.player.xo = this.player.xOld = 0.0;
        this.player.z = this.player.zo = this.player.zOld = 0.0;

        // Useless - Used to be this.level.time = 0, changed to new func call
        for (this.level.setTime(0L); frame < flybyLength; frame++) {
            if (frame % 100 == 0) {
                System.out.println(frame * 100.0 / flybyLength + "%, free: " + (float)(Runtime.getRuntime().freeMemory() / 1024L) / 1024.0F + " MB");
                System.gc();
            }

            double speed = 0.125 + (double)frame / flybyLength * 5.0;
            AABB.resetPool();
            Vec3.resetPool();
            if (frame < 0) {
                this.level.setSpawnSettings(this.options.difficulty > 0, true);
                this.level.tick();
            }

            this.gameRenderer.tick();
            glEnable(GL_TEXTURE_2D);

            while (this.level.updateLights()) {}

            this.player.x = this.player.xo = this.player.xOld += ySin * speed;
            this.player.z = this.player.zo = this.player.zOld += yCos * speed;
            byte shiftRange = 100;
            double highestPoint = 0.0;
            double shiftAmount = 1.0;

            // Useless - Collects a few samples along a straight line in the direction a travel to collect the heighest point
            for (double shift = -4.0; shift < shiftRange; shift += shiftAmount) {
                // Useless - get heighest point in a 9x9 region
                for (int c = 0; c < 9; c++) {
                    double xo = c % 3 / 2.0F - 0.5;
                    double zo = c / 3 / 2.0F - 0.5;
                    int tileX = Mth.floor(this.player.x + ySin * shift + xo);
                    int tileZ = Mth.floor(this.player.z + yCos * shift + zo);
                    double tileHeight = this.level.getHeightmap(tileX, tileZ);
                    if (tileHeight > highestPoint) highestPoint = tileHeight;
                }
            }

            double nextY = highestPoint + 4.0;
            if (y == 0.0) {
                y = nextY;
            } else {
                y += (nextY - y) * speed / shiftRange * 4.0;
            }

            this.player.xRot = this.player.xRotO = (float)(y - 64.0) / 2.0F; // Useless - makes the player look up/down based on being above or below the 64 block mark
            this.player.y = this.player.yo = this.player.yOld = y;
            this.gameRenderer.renderLevel(1.0F, 0L);
            glBindTexture(GL_TEXTURE_2D, this.textures.loadTexture("/terrain.png"));
            this.textures.tick();
            Display.update();

            pixelBuffer.clear();
            glReadPixels(0, 0, this.width, this.height, GL12.GL_BGR, GL_UNSIGNED_BYTE, pixelBuffer);
            pixelBuffer.clear();

            if (frame >= 0) {
                StringBuilder id = new StringBuilder("" + frame);

                while (id.length() < 6) {
                    id.insert(0, "0");
                }

                try {
                    pixelBuffer.get(pb);
                    File imageFile = new File(flybyDirectory, "img" + id + ".tga");
                    DataOutputStream dos = new DataOutputStream(new FileOutputStream(imageFile));
                    dos.write(header);
                    dos.write(pb);
                    dos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void run() {
        this.running = true;
        try {
            this.init();
        }
        catch (final Exception e) {
            e.printStackTrace();
            this.crash(new CrashReport("Failed to start game", e));
            return;
        }

        // Useless - Sourced from LCE, calls the b1.2leak flyby function
        if (Minecraft.FLYBY_MODE) {
            generateFlyby();
            return;
        }

        try {
            long lastTime = System.currentTimeMillis();
            int frames = 0;

            while (this.running) {
                try {
                    if (this.minecraftApplet != null && !this.minecraftApplet.isActive()) break;

                    AABB.resetPool();
                    Vec3.resetPool();

                    if (this.parent == null && Display.isCloseRequested()) {
                        this.stop();
                    }

                    if (this.pause && this.level != null) {
                        final float a = this.timer.a;
                        this.timer.advanceTime();
                        this.timer.a = a;
                    }
                    else {
                        this.timer.advanceTime();
                    }

                    final long beforeTickTime = System.nanoTime();
                    for (int i = 0; i < this.timer.ticks; ++i) {
                        ++this.ticks;
                        try {
                            this.tick();
                        }
                        catch (final LevelStorageException e) {
                            this.level = null;
                            this.setLevel(this.level);
                            this.setScreen(new LevelConflictScreen());
                        }
                    }
                    final long tickDuration = System.nanoTime() - beforeTickTime;
                    this.checkGlError("Pre render");

                    TileRenderer.fancy = this.options.fancyGraphics;

                    this.soundEngine.update(this.player, this.timer.a);

                    glEnable(GL_TEXTURE_2D);
                    if (this.level != null) this.level.updateLights();

                    if (!Keyboard.isKeyDown(Keyboard.KEY_F7)) Display.update();

                    if (this.player != null && this.player.isInWall()) this.options.thirdPersonView = false;
                    if (!this.noRender) {
                        if (this.gameMode != null) this.gameMode.render(this.timer.a);
                        this.gameRenderer.render(this.timer.a);
                    }

                    if (!Display.isActive()) {
                        if (this.fullscreen) {
                            this.toggleFullScreen();
                        }
                        Thread.sleep(10L);
                    }

                    if (this.options.renderDebug) {
                        this.renderFpsMeter(tickDuration);
                    }
                    else {
                        this.lastTimer = System.nanoTime();
                    }

                    this.achievementPopup.render();

                    Thread.yield();

                    if (Keyboard.isKeyDown(Keyboard.KEY_F7)) Display.update();

                    this.checkScreenshot();
                    if (this.parent != null && !this.fullscreen)
                        if (this.parent.getWidth() != this.width || this.parent.getHeight() != this.height) {
                            this.width = this.parent.getWidth();
                            this.height = this.parent.getHeight();
                            if (this.width <= 0) this.width = 1;
                            if (this.height <= 0) this.height = 1;
                            this.resize(this.width, this.height);
                        }
                    this.checkGlError("Post render");
                    ++frames;
                    this.pause = !this.isClientSide() && this.screen != null && this.screen.isPauseScreen();

                    while (System.currentTimeMillis() >= lastTime + 1000L) {
                        this.fpsString = frames + " fps, " + Chunk.updates + " chunk updates";
                        Chunk.updates = 0;
                        lastTime += 1000L;
                        frames = 0;
                    }
                }
                catch (final LevelStorageException e) {
                    this.level = null;
                    this.setLevel(this.level);
                    this.setScreen(new LevelConflictScreen());
                }
                catch (final OutOfMemoryError e) {
                    this.emergencySave();
                    this.setScreen(new OutOfMemoryScreen());
                    System.gc();
                }
            }
        }
        catch (final StopGameException e) {}
        catch (final Throwable e) {
            this.emergencySave();
            e.printStackTrace();
            this.crash(new CrashReport("Unexpected error", e));
        }
        finally {
            this.destroy();
        }
    }
    
    public void emergencySave() {
        try {
            Minecraft.__unused_byte_buffer = new byte[0];
            this.levelRenderer.clear();
        }
        catch (final Throwable e) {}
        try {
            System.gc();
            AABB.clearPool();
            Vec3.clearPool();
        }
        catch (final Throwable e) {}
        try {
            System.gc();
            this.setLevel(null);
        }
        catch (final Throwable e) {}
        System.gc();
    }
    
    private void checkScreenshot() {
        if (Keyboard.isKeyDown(Keyboard.KEY_F2)) {
            if (!this.wasDown) {
                this.wasDown = true;
                // Useless - b1.2 huge screenshot code, speculatively believe it was probably just commented out instead of deleted
//                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
//                    this.gui.addMessage(this.grabHugeScreenshot(workDir, this.width, this.height, 36450, 17700));
//                } else {
                    this.gui.addMessage(Screenshot.grab(Minecraft.workDir, this.width, this.height));
//                }
            }
        }
        else {
            this.wasDown = false;
        }
    }

    private String grabHugeScreenshot(File workDir, int width, int height, int ssWidth, int ssHeight) {
        try {
            ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 3);
            Screenshot ss = new Screenshot(workDir, ssWidth, ssHeight, height);
            double rawZoomW = (double)ssWidth / width;
            double rawZoomH = (double)ssHeight / height;
            double zoom = rawZoomW > rawZoomH ? rawZoomW : rawZoomH;

            for (int yo = (ssHeight - 1) / height * height; yo >= 0; yo -= height) {
                for (int xo = 0; xo < ssWidth; xo += width) {
                    int rowWidth = width;
                    int rowHeight = height;
                    glBindTexture(GL_TEXTURE_2D, this.textures.loadTexture("/terrain.png"));
                    double zoomX = (ssWidth - width) / 2.0 * 2.0 - xo * 2;
                    double zoomY = (ssHeight - height) / 2.0 * 2.0 - yo * 2;
                    zoomX /= width;
                    zoomY /= height;
                    this.gameRenderer.zoomRegion(zoom, zoomX, zoomY);
                    this.gameRenderer.renderLevel(1.0F, 0);
                    this.gameRenderer.unZoomRegion();
                    Display.update();

                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Display.update();
                    pixels.clear();
                    glPixelStorei(GL_PACK_ALIGNMENT, 1);
                    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
                    glReadPixels(0, 0, rowWidth, rowHeight, GL12.GL_BGR, GL_UNSIGNED_BYTE, pixels);
                    ss.addRegion(pixels, xo, yo, rowWidth, rowHeight);
                }

                ss.saveRow();
            }

            return ss.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to save image: " + e;
        }
    }
    
    private void renderFpsMeter(final long tickTime) {
        final long nsPer60Fps = 1000000000L / 60;
        if (this.lastTimer == -1L) {
            this.lastTimer = System.nanoTime();
        }

        final long now = System.nanoTime();
        Minecraft.tickTimes[Minecraft.frameTimePos & Minecraft.frameTimes.length - 1] = tickTime;
        Minecraft.frameTimes[Minecraft.frameTimePos++ & Minecraft.frameTimes.length - 1] = now - this.lastTimer;
        this.lastTimer = now;

        glClear(GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, this.width, this.height, 0.0, 1000.0, 3000.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(0.0f, 0.0f, -2000.0f);

        glLineWidth(1.0f);
        glDisable(GL_TEXTURE_2D);
        final Tesselator t = Tesselator.instance;
        t.begin(GL_QUADS);
        final int hh1 = (int)(nsPer60Fps / 200000L);
        t.color(0x20000000);
        t.vertex(0.0, this.height - hh1, 0.0);
        t.vertex(0.0, this.height, 0.0);
        t.vertex(Minecraft.frameTimes.length, this.height, 0.0);
        t.vertex(Minecraft.frameTimes.length, this.height - hh1, 0.0);

        t.color(0x20200000);
        t.vertex(0.0, this.height - hh1 * 2, 0.0);
        t.vertex(0.0, this.height - hh1, 0.0);
        t.vertex(Minecraft.frameTimes.length, this.height - hh1, 0.0);
        t.vertex(Minecraft.frameTimes.length, this.height - hh1 * 2, 0.0);

        t.end();
        long totalTime = 0L;
        for (int i = 0; i < Minecraft.frameTimes.length; ++i) {
            totalTime += Minecraft.frameTimes[i];
        }
        final int hh = (int)(totalTime / 200000L / Minecraft.frameTimes.length);
        t.begin(GL_QUADS);
        t.color(0x20400000);
        t.vertex(0.0, this.height - hh, 0.0);
        t.vertex(0.0, this.height, 0.0);
        t.vertex(Minecraft.frameTimes.length, this.height, 0.0);
        t.vertex(Minecraft.frameTimes.length, this.height - hh, 0.0);
        t.end();
        t.begin(GL_LINES);
        for (int i = 0; i < Minecraft.frameTimes.length; ++i) {
            final int col = (i - Minecraft.frameTimePos & Minecraft.frameTimes.length - 1) * 255 / Minecraft.frameTimes.length;
            int cc = col * col / 255;
            cc = cc * cc / 255;
            if (Minecraft.frameTimes[i] > nsPer60Fps) {
                t.color(0xff000000 + cc * 0x10000);
            }
            else {
                t.color(0xff000000 + cc * 0x100);
            }

            final long time = Minecraft.frameTimes[i] / 200000L;
            final long time2 = Minecraft.tickTimes[i] / 200000L;

            t.vertex(i + 0.5f, this.height - time + 0.5f, 0.0);
            t.vertex(i + 0.5f, this.height + 0.5f, 0.0);

            // if (Minecraft.frameTimes[i]>nsPer60Fps) {
            t.color(0xff000000 + cc * 0x10000 + cc * 0x100 + cc * 0x1);
            // } else {
            // t.color(0xff808080 + cc/2 * 256);
            // }

            t.vertex(i + 0.5f, this.height - time + 0.5f, 0.0);
            t.vertex(i + 0.5f, this.height - (time - time2) + 0.5f, 0.0);
        }
        t.end();

        glEnable(GL_TEXTURE_2D);
    }
    
    public void stop() {
        this.running = false;
    }
    
    public void grabMouse() {
        if (!Display.isActive()) return;
        if (this.mouseGrabbed) return;

        this.mouseGrabbed = true;
        this.mouseHandler.grab();
        this.setScreen(null);
        this.missTime = 10000;
        this.lastClickTick = this.ticks + 10000;
    }
    
    public void releaseMouse() {
        if (!this.mouseGrabbed) return;

        if (this.player != null) {
            this.player.releaseAllKeys();
        }
        this.mouseGrabbed = false;
        this.mouseHandler.release();
    }
    
    public void pauseGame() {
        if (this.screen != null) return;
        this.setScreen(new PauseScreen());
    }
    
    private void handleMouseDown(final int button, final boolean down) {
        if (this.gameMode.instaBuild) return;

        if (!down) this.missTime = 0;
        if (button == 0 && this.missTime > 0) return;

        if (down && this.hitResult != null && this.hitResult.type == HitResult.Type.TILE && button == 0) {
            final int x = this.hitResult.x;
            final int y = this.hitResult.y;
            final int z = this.hitResult.z;
            this.gameMode.continueDestroyBlock(x, y, z, this.hitResult.f);
            this.particleEngine.crack(x, y, z, this.hitResult.f);
        }
        else {
            this.gameMode.stopDestroyBlock();
        }
    }
    
    private void handleMouseClick(final int button) {
        if (button == 0 && this.missTime > 0) return;

        if (button == 0) {
            this.player.swing();
        }

        boolean mayUse = true;
        if (this.hitResult == null) {
            if (button == 0 && !(this.gameMode instanceof CreativeMode)) this.missTime = 10;
        }
        else if (this.hitResult.type == HitResult.Type.ENTITY) {
            if (button == 0) {
                this.gameMode.attack(this.player, this.hitResult.entity);
            }
            if (button == 1) {
                this.gameMode.interact(this.player, this.hitResult.entity);
            }
        }
        else if (this.hitResult.type == HitResult.Type.TILE) {
            final int x = this.hitResult.x;
            final int y = this.hitResult.y;
            final int z = this.hitResult.z;
            final int face = this.hitResult.f;
            if (button == 0) {
                this.gameMode.startDestroyBlock(x, y, z, this.hitResult.f);
            }
            else {
                final ItemInstance item = this.player.inventory.getSelected();
                final int oldCount = (item != null) ? item.count : 0;
                if (this.gameMode.useItemOn(this.player, this.level, item, x, y, z, face)) {
                    mayUse = false;
                    this.player.swing();
                }
                if (item == null) {
                    return;
                }
                if (item.count == 0) {
                    this.player.inventory.items[this.player.inventory.selected] = null;
                }
                else if (item.count != oldCount) {
                    this.gameRenderer.itemInHandRenderer.itemPlaced();
                }
            }
        }
        if (mayUse && button == 1) {
            final ItemInstance item = this.player.inventory.getSelected();
            if (item != null && this.gameMode.useItem(this.player, this.level, item)) {
                this.gameRenderer.itemInHandRenderer.itemUsed();
            }
        }
    }
    
    public void toggleFullScreen() {
        try {
            this.fullscreen = !this.fullscreen;
            if (this.fullscreen) {
                Display.setDisplayMode(Display.getDesktopDisplayMode());
                this.width = Display.getDisplayMode().getWidth();
                this.height = Display.getDisplayMode().getHeight();
                if (this.width <= 0) this.width = 1;
                if (this.height <= 0) this.height = 1;
            }
            else {
                if (this.parent != null) {
                    this.width = this.parent.getWidth();
                    this.height = this.parent.getHeight();
                }
                else {
                    this.width = this.orgWidth;
                    this.height = this.orgHeight;
                }

                if (this.width <= 0) this.width = 1;
                if (this.height <= 0) this.height = 1;
            }
            if (this.screen != null) {
                this.resize(this.width, this.height);
            }
            Display.setFullscreen(this.fullscreen);
            Display.update();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private void resize(int width, int height) {
        if (width <= 0) width = 1;
        if (height <= 0) height = 1;
        this.width = width;
        this.height = height;

        if (this.screen != null) {
            final ScreenSizeCalculator ssc = new ScreenSizeCalculator(this.options, width, height);
            int screenWidth = ssc.getWidth();
            int screenHeight = ssc.getHeight();
            this.screen.init(this, screenWidth, screenHeight);
        }
    }
    
    private void handleGrabTexture() {
        if (this.hitResult != null) {
            int id = this.level.getTile(this.hitResult.x, this.hitResult.y, this.hitResult.z);
            if (id == Tile.grass.id) id = Tile.dirt.id;
            if (id == Tile.stoneSlab.id) id = Tile.stoneSlabHalf.id;
            if (id == Tile.unbreakable.id) id = Tile.rock.id;
            this.player.inventory.grabTexture(id, this.gameMode instanceof CreativeMode);
        }
    }
    
    private void verify() {
        new Thread(() -> {
            try {
                final HttpURLConnection huc = (HttpURLConnection)new URL("https://login.minecraft.net/session?name=" + this.user.name + "&session=" + this.user.sessionId).openConnection();
                huc.connect();
                if (huc.getResponseCode() == 400) warezTime = System.currentTimeMillis();
                huc.disconnect();
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public void tick() {
        if (this.ticks == 6000) {
            this.verify();
        }

        this.stats.tick();
        this.gui.tick();
        this.gameRenderer.pick(1.0f);

        if (this.player != null) {
            final ChunkSource cs = this.level.getChunkSource();
            if (cs instanceof ChunkCache) {
                ChunkCache spcc = (ChunkCache) cs;

                int xt = Mth.floor((float)(int)this.player.x) >> 4;
                int zt = Mth.floor((float)(int)this.player.z) >> 4;
                spcc.centerOn(xt, zt);
            }
        }

        if (!this.pause && this.level != null) this.gameMode.tick();

        glBindTexture(GL_TEXTURE_2D, this.textures.loadTexture("/terrain.png"));
        if (!this.pause) this.textures.tick();

        if (this.screen == null && this.player != null) {
            if (this.player.health <= 0) {
                this.setScreen(null);
            }
            else if (this.player.isSleeping() && this.level != null && this.level.isClientSide) {
                this.setScreen(new InBedChatScreen());
            }
        }
        else if (this.screen != null && this.screen instanceof InBedChatScreen && !this.player.isSleeping()) {
            this.setScreen(null);
        }

        if (this.screen != null) {
            this.missTime = 10000;
            this.lastClickTick = this.ticks + 10000;
        }

        if (this.screen != null) {
            this.screen.updateEvents();
            if (this.screen != null) {
                this.screen.particles.tick();
                this.screen.tick();
            }
        }

        if (this.screen == null || this.screen.passEvents) {
            while (Mouse.next()) {
                long passedTime = System.currentTimeMillis() - this.lastTickTime;
                if (passedTime > 200L) continue;

                int wheel = Mouse.getEventDWheel();
                if (wheel != 0) {
                    this.player.inventory.swapPaint(wheel);
                    if (this.options.isFlying) {
                        if (wheel > 0) wheel = 1;
                        if (wheel < 0) wheel = -1;

                        this.options.flySpeed += wheel * 0.25f;
                    }
                }

                if (this.screen == null) {
                    if (!this.mouseGrabbed && Mouse.getEventButtonState()) {
                        this.grabMouse();
                    }
                    else {
                        if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
                            this.handleMouseClick(0);
                            this.lastClickTick = this.ticks;
                        }
                        if (Mouse.getEventButton() == 1 && Mouse.getEventButtonState()) {
                            this.handleMouseClick(1);
                            this.lastClickTick = this.ticks;
                        }
                        if (Mouse.getEventButton() != 2 || !Mouse.getEventButtonState()) {
                            continue;
                        }
                        this.handleGrabTexture();
                    }
                } else if (this.screen != null) {
                    this.screen.mouseEvent();
                }
            }

            if (this.missTime > 0) --this.missTime;

            while (Keyboard.next()) {
                this.player.setKey(Keyboard.getEventKey(), Keyboard.getEventKeyState());
                if (Keyboard.getEventKeyState()) {
                    if (Keyboard.getEventKey() == Keyboard.KEY_F11) {
                        this.toggleFullScreen();
                        continue;
                    }

                    // Useless - Below was a comment from LCE leak, presumably a quick debug way to spawn a nether portal in
                     /*if (Keyboard.getEventKey() == Keyboard.KEY_F4) {
                         new PortalForcer().createPortal(level, player);
                         continue;
                     }*/

                    // Useless - Below was a comment from LCE leaks, unsure if this was for a later version or not can't actually find any references to level.pathFind() existing anywhere
                   /* if (Keyboard.getEventKey() == Keyboard.KEY_RETURN) {
                        level.pathFind(); continue;
                    }*/


                    if (this.screen != null) {
                        this.screen.keyboardEvent();
                    }
                    else {
                        if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                            this.pauseGame();
                        }

                        if (Keyboard.getEventKey() == Keyboard.KEY_S && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
                            this.reloadSound();
                        }

                        if (Keyboard.getEventKey() == 59) {
                            this.options.hideGui = !this.options.hideGui;
                        }
                        if (Keyboard.getEventKey() == 61) {
                            this.options.renderDebug = !this.options.renderDebug;
                        }
                        if (Keyboard.getEventKey() == 63) {
                            this.options.thirdPersonView = !this.options.thirdPersonView;
                        }
                        if (Keyboard.getEventKey() == 66) {
                            this.options.smoothCamera = !this.options.smoothCamera;
                        }

                        if (DEADMAU5_CAMERA_CHEATS) {
                            if (Keyboard.getEventKey() == Keyboard.KEY_F6) {
                                options.isFlying = !options.isFlying;
                            }
                            if (Keyboard.getEventKey() == Keyboard.KEY_F9) {
                                options.fixedCamera = !options.fixedCamera;
                            }
                            if (Keyboard.getEventKey() == Keyboard.KEY_ADD) {
                                options.cameraSpeed += .1f;
                            }
                            if (Keyboard.getEventKey() == Keyboard.KEY_SUBTRACT) {
                                options.cameraSpeed -= .1f;
                                if (options.cameraSpeed < 0) {
                                    options.cameraSpeed = 0;
                                }
                            }
                        }

                        if (Keyboard.getEventKey() == this.options.keyBuild.key) {
                            this.setScreen(new InventoryScreen(this.player));
                        }

                        if (Keyboard.getEventKey() == this.options.keyDrop.key) {
                            this.player.drop();
                        }

                        if (this.isClientSide() && Keyboard.getEventKey() == this.options.keyChat.key) {
                            this.setScreen(new ChatScreen());
                        }
                    }

                    for (int i = 0; i < 9; ++i) {
                        if (Keyboard.getEventKey() == Keyboard.KEY_1 + i) this.player.inventory.selected = i;
                    }
                    if (Keyboard.getEventKey() == this.options.keyFog.key) {
                        this.options.toggle(Option.RENDER_DISTANCE, (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) ? -1 : 1);
                    }
                }
            }

            if (this.screen == null) {
                if (Mouse.isButtonDown(0) && this.ticks - this.lastClickTick >= this.timer.ticksPerSecond / 4.0f && this.mouseGrabbed) {
                    this.handleMouseClick(0);
                    this.lastClickTick = this.ticks;
                }
                if (Mouse.isButtonDown(1) && this.ticks - this.lastClickTick >= this.timer.ticksPerSecond / 4.0f && this.mouseGrabbed) {
                    this.handleMouseClick(1);
                    this.lastClickTick = this.ticks;
                }
            }

            this.handleMouseDown(0, this.screen == null && Mouse.isButtonDown(0) && this.mouseGrabbed);
        }

        if (this.level != null) {
            if (this.player != null) {
                ++this.recheckPlayerIn;
                if (this.recheckPlayerIn == 30) {
                    this.recheckPlayerIn = 0;
                    this.level.ensureAdded(this.player);
                }
            }

            this.level.difficulty = this.options.difficulty;
            if (this.level.isClientSide) {
                this.level.difficulty = 3;
            }

            if (!this.pause) this.gameRenderer.tick();
            if (!this.pause) this.levelRenderer.tick();

            if (!this.pause) {
                if (this.level.lightningBoltTime > 0) {
                    final Level level = this.level;
                    --level.lightningBoltTime;
                }
                this.level.tickEntities();
            }

            if (!this.pause || this.isClientSide()) {
                this.level.setSpawnSettings(this.options.difficulty > 0, true);
                this.level.tick();
            }

            if (!this.pause && this.level != null) this.level.animateTick(Mth.floor(this.player.x), Mth.floor(this.player.y), Mth.floor(this.player.z));

            if (!this.pause) this.particleEngine.tick();
        }
        this.lastTickTime = System.currentTimeMillis();
    }
    
    private void reloadSound() {
        System.out.println("FORCING RELOAD!");
        this.soundEngine = new SoundEngine();
        this.soundEngine.init(this.options);
        this.bgLoader.forceReload();
    }
    
    public boolean isClientSide() {
        return this.level != null && this.level.isClientSide;
    }
    
    public void selectLevel(final String levelId, final String levelName, final long seed) {
        this.setLevel(null);
        System.gc();
        if (this.levelSource.requiresConversion(levelId)) {
            this.convertLevel(levelId, levelName);
        }
        else {
            final Level level = new Level(this.levelSource.selectLevel(levelId, false), levelName, seed);
            if (level.isNew) {
                this.stats.award(Stats.createWorld, 1);
                this.stats.award(Stats.startGame, 1);
                this.setLevel(level, "Generating level");
            }
            else {
                this.stats.award(Stats.loadWorld, 1);
                this.stats.award(Stats.startGame, 1);
                this.setLevel(level, "Loading level");
            }
        }
    }
    
    public void toggleDimension() {
        System.out.println("Toggling dimension!!");
        if (this.player.dimension == -1) {
            this.player.dimension = 0;
        }
        else {
            this.player.dimension = -1;
        }

        this.level.removeEntity(this.player);
        this.player.removed = false;

        double xt = this.player.x;
        double zt = this.player.z;
        final double scale = 8.0;
        if (this.player.dimension == -1) {
            xt /= scale;
            zt /= scale;
            this.player.moveTo(xt, this.player.y, zt, this.player.yRot, this.player.xRot);
            if (this.player.isAlive()) {
                this.level.tick(this.player, false);
            }
            this.setLevel(new Level(this.level, Dimension.getNew(-1)), "Entering the Nether", this.player);
        }
        else {
            xt *= scale;
            zt *= scale;
            this.player.moveTo(xt, this.player.y, zt, this.player.yRot, this.player.xRot);
            if (this.player.isAlive()) {
                this.level.tick(this.player, false);
            }
            this.setLevel(new Level(this.level, Dimension.getNew(0)), "Leaving the Nether", this.player);
        }

        this.player.level = this.level;
        if (this.player.isAlive()) {
            this.player.moveTo(xt, this.player.y, zt, this.player.yRot, this.player.xRot);
            this.level.tick(this.player, false);
            new PortalForcer().force(this.level, this.player);
        }
    }
    
    public void setLevel(final Level level) {
        this.setLevel(level, "");
    }
    
    public void setLevel(final Level level, final String message) {
        this.setLevel(level, message, null);
    }
    
    public void setLevel(final Level level, final String message, final Player forceInsertPlayer) {
        this.stats.forceSend();
        this.stats.forceSave();
        this.cameraTargetPlayer = null;

        this.progressRenderer.progressStart(message);
        this.progressRenderer.progressStage("");

        this.soundEngine.playStreaming(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);

        if (this.level != null) this.level.forceSave(this.progressRenderer);

        this.level = level;
        if (level != null) {
            this.gameMode.initLevel(level);

            if (!this.isClientSide()) {
                if (forceInsertPlayer == null) {
                    this.player = (LocalPlayer)level.findSubclassOf(LocalPlayer.class);
                }
            }
            else if (this.player != null) {
                this.player.resetPos();
                if (level != null) {
                    level.addEntity(this.player);
                }
            }

            if (!level.isClientSide) this.prepareLevel(message);

            if (this.player == null) {
                this.player = (LocalPlayer)this.gameMode.createPlayer(level);
                this.player.resetPos();
                this.gameMode.initPlayer(this.player);
            }

            this.player.input = new KeyboardInput(this.options);

            if (this.levelRenderer != null) this.levelRenderer.setLevel(level);
            if (this.particleEngine != null) this.particleEngine.setLevel(level);

            this.gameMode.adjustPlayer(this.player);
            if (forceInsertPlayer != null) {
                level.clearLoadedPlayerData();
            }

            final ChunkSource cs = level.getChunkSource();
            if (cs instanceof ChunkCache) {
                ChunkCache spcc = (ChunkCache)cs;

                int xt =  Mth.floor((float)(int)this.player.x) >> 4;
                int zt = Mth.floor((float)(int)this.player.z) >> 4;
                spcc.centerOn(xt, zt);
            }

            level.loadPlayer(this.player);
            if (level.isNew) level.forceSave(this.progressRenderer);

            this.cameraTargetPlayer = this.player;
        }
        else {
            this.player = null;
        }
        System.gc();
        this.lastTickTime = 0L;
    }
    
    private void convertLevel(final String levelId, final String levelName) {
        this.progressRenderer.progressStart("Converting World to " + this.levelSource.getName());
        this.progressRenderer.progressStage("This may take a while :)");
        this.levelSource.convertLevel(levelId, this.progressRenderer);
        this.selectLevel(levelId, levelName, 0L);
    }
    
    private void prepareLevel(final String title) {
        this.progressRenderer.progressStart(title);
        this.progressRenderer.progressStage("Building terrain");

        final int r = 128;
        int pp = 0;
        int max = r * 2 / 16 + 1;
        max = max * max;
        final ChunkSource cs = this.level.getChunkSource();

        final Pos spawnPos = this.level.getSharedSpawnPos();
        if (this.player != null) {
            spawnPos.x = (int)this.player.x;
            spawnPos.z = (int)this.player.z;
        }

        if (cs instanceof ChunkCache) {
            ChunkCache spcc = (ChunkCache) cs;

            spcc.centerOn(spawnPos.x >> 4, spawnPos.z >> 4);
        }

        for (int x = -r; x <= r; x += 16) {
            for (int y = -r; y <= r; y += 16) {
                this.progressRenderer.progressStagePercentage(pp++ * 100 / max);
                this.level.getTile(spawnPos.x + x, 64, spawnPos.z + y);
                while (this.level.updateLights());
            }
        }

        this.progressRenderer.progressStage("Simulating world for a bit");
        this.level.prepare();
    }
    
    public void fileDownloaded(String name, final File file) {
        final int p = name.indexOf("/");
        final String category = name.substring(0, p);
        name = name.substring(p + 1);
        if (category.equalsIgnoreCase("sound")) {
            this.soundEngine.add(name, file);
        }
        else if (category.equalsIgnoreCase("newsound")) {
            this.soundEngine.add(name, file);
        }
        else if (category.equalsIgnoreCase("streaming")) {
            this.soundEngine.addStreaming(name, file);
        }
        else if (category.equalsIgnoreCase("music")) {
            this.soundEngine.addMusic(name, file);
        }
        else if (category.equalsIgnoreCase("newmusic")) {
            this.soundEngine.addMusic(name, file);
        }
    }
    
    public OpenGLCapabilities getOpenGLCapabilities() {
        return this.openGLCapabilities;
    }
    
    public String gatherStats1() {
        return this.levelRenderer.gatherStats1();
    }
    
    public String gatherStats2() {
        return this.levelRenderer.gatherStats2();
    }
    
    public String gatherStats3() {
        return this.level.gatherChunkSourceStats();
    }
    
    public String gatherStats4() {
        return "P: " + this.particleEngine.countParticles() + ". T: " + this.level.gatherStats();
    }
    
    public void respawnPlayer(final boolean boolean1 /*TODO Useless - find name for boolean*/, final int dimension) {
        if (!this.level.isClientSide && !this.level.dimension.mayRespawn()) this.toggleDimension();

        Pos bedPosition = null;
        Pos respawnPosition = null;
        boolean hasBed = true;
        if (this.player != null && !boolean1) {
            bedPosition = this.player.getRespawnPosition();
            if (bedPosition != null) {
                respawnPosition = Player.checkBedValidRespawnPosition(this.level, bedPosition);
                if (respawnPosition == null) {
                    this.player.displayClientMessage("tile.bed.notValid");
                }
            }
        }
        if (respawnPosition == null) {
            respawnPosition = this.level.getSharedSpawnPos();
            hasBed = false;
        }

        final ChunkSource cs = this.level.getChunkSource();
        if (cs instanceof ChunkCache) {
            ChunkCache spcc = (ChunkCache)cs;
            spcc.centerOn(respawnPosition.x >> 4, respawnPosition.z >> 4);
        }

        this.level.validateSpawn();
        this.level.removeAllPendingEntityRemovals();
        int entityId = 0;
        if (this.player != null) {
            entityId = this.player.entityId;
            this.level.removeEntity(this.player);
        }

        this.cameraTargetPlayer = null;
        this.player = (LocalPlayer)this.gameMode.createPlayer(this.level);
        this.player.dimension = dimension;
        this.cameraTargetPlayer = this.player;
        this.player.resetPos();
        if (hasBed) {
            this.player.setRespawnPosition(bedPosition);
            this.player.moveTo(respawnPosition.x + 0.5f, respawnPosition.y + 0.1f, respawnPosition.z + 0.5f, 0.0f, 0.0f);
        }
        this.gameMode.initPlayer(this.player);
        this.level.loadPlayer(this.player);
        this.player.input = new KeyboardInput(this.options);
        this.player.entityId = entityId;
        this.player.animateRespawn();
        this.gameMode.adjustPlayer(this.player);
        this.prepareLevel("Respawning");
        if (this.screen instanceof DeathScreen) {
            this.setScreen(null);
        }
    }
    
    public static void start(final String name, final String sessionId) {
        startAndConnectTo(name, sessionId, null);
    }
    
    public static void startAndConnectTo(final String name, final String sessionId, final String url) {
        final boolean fullscreen = false;

        final Frame frame = new Frame("Minecraft");
        final Canvas canvas = new Canvas();
        frame.setLayout(new BorderLayout());

        frame.add(canvas, "Center");

        // OverlayLayout oll = new OverlayLayout(frame);
        // oll.addLayoutComponent(canvas, BorderLayout.CENTER);
        // oll.addLayoutComponent(new JLabel("TEST"), BorderLayout.EAST);

        canvas.setPreferredSize(new java.awt.Dimension(854, 480));
        frame.pack();
        frame.setLocationRelativeTo(null);
        final Minecraft minecraft = new Minecraft(frame, canvas, null, 854, 480, fullscreen) {
            @Override
            public void onCrash(final CrashReport crashReport) {
                frame.removeAll();
                frame.add(new CrashInfoPanel(crashReport), "Center");
                frame.validate();
            }
        };

        final Thread thread = new Thread(minecraft, "Minecraft main thread");
        thread.setPriority(Thread.MAX_PRIORITY);

        minecraft.serverDomain = "www.minecraft.net";
        if (name != null && sessionId != null) {
            minecraft.user = new User(name, sessionId);
        }
        else {
            minecraft.user = new User("Player" + System.currentTimeMillis() % 1000L, "");
        }

        if (url != null) {
            final String[] tokens = url.split(":");
            minecraft.connectTo(tokens[0], Integer.parseInt(tokens[1]));
        }

        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                minecraft.stop();
                try {
                    thread.join();
                }
                catch (final InterruptedException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }
        });
        thread.start();
    }
    
    public ClientConnection getConnection() {
        return this.player instanceof MultiplayerLocalPlayer ? ((MultiplayerLocalPlayer) this.player).connection : null;
    }
    
    public static void main(final String[] args) {
        String name = "Player" + System.currentTimeMillis() % 1000L;
        if (args.length > 0) name = args[0];

        String sessionId = "-";
        if (args.length > 1) sessionId = args[1];

        start(name, sessionId);
    }
    
    public static boolean renderNames() {
        return Minecraft.instance == null || !Minecraft.instance.options.hideGui;
    }
    
    public static boolean useFancyGraphics() {
        return Minecraft.instance != null && Minecraft.instance.options.fancyGraphics;
    }
    
    public static boolean useAmbientOcclusion() {
        return Minecraft.instance != null && Minecraft.instance.options.ambientOcclusion;
    }
    
    public static boolean renderDebug() {
        return Minecraft.instance != null && Minecraft.instance.options.renderDebug;
    }
    
    public boolean handleClientSideCommand(final String chatMessage) {
        if (chatMessage.startsWith("/")) {
            // Useless - recovered from LCE, presumably is the usage of the flying stuff stored in option
            // Presumably why the empty if statement has existed here at all
            if (DEADMAU5_CAMERA_CHEATS) {
                if (chatMessage.startsWith("/follow")) {
                    String[] tokens = chatMessage.split(" ");
                    if (tokens.length >= 2) {
                        String playerName = tokens[1];

                        boolean found = false;
                        for (Player player : this.level.players) {
                            if (playerName.equalsIgnoreCase(player.name)) {
                                this.cameraTargetPlayer = player;
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            try {
                                int entityId = Integer.parseInt(playerName);
                                for (Entity e : this.level.entities) {
                                    if (e.entityId == entityId && e instanceof Mob) {
                                        this.cameraTargetPlayer = (Mob) e;
                                        found = true;
                                        break;
                                    }
                                }
                            } catch (NumberFormatException e) {}
                        }
                    }

                    return true;
                }
            }
        }
        return false;
    }

    public enum OS
    {
        linux,
        solaris,
        windows,
        macos,
        unknown;
    }
}
