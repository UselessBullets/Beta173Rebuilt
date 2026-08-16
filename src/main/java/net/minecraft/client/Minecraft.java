// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import net.minecraft.SharedConstants;
import net.minecraft.client.Options.Option;
import net.minecraft.client.multiplayer.MultiplayerLocalPlayer;
import net.minecraft.client.multiplayer.ClientConnection;

import java.awt.BorderLayout;
import java.awt.Frame;
import net.minecraft.Pos;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.level.PortalForcer;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.client.gui.ChatScreen;
import net.minecraft.client.gui.inventory.InventoryScreen;
import net.minecraft.client.gui.InBedChatScreen;
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
import org.lwjgl.opengl.GL11;
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
import java.net.HttpURLConnection;
import java.net.URL;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.Level;
import net.minecraft.client.gamemode.GameMode;

import static org.lwjgl.opengl.GL11.*;

public abstract class Minecraft implements Runnable
{
    public static byte[] __unused_byte_buffer;
    private static Minecraft instance;

    public static final String VERSION_STRING = "Minecraft " + SharedConstants.VERSION_STRING;
    public GameMode gameMode;
    private boolean fullscreen;
    private boolean hasCrashed;
    public int width;
    public int height;
    private OpenGLCapabilities openGLCapabilities;
    private Timer timer;
    public Level level;
    public LevelRenderer levelRenderer;
    public LocalPlayer player;
    public Mob cameraTargetPlayer;
    public ParticleEngine particleEngine;
    public User user;
    public String serverDomain;
    public Canvas parent;
    public boolean appletMode;
    public volatile boolean pause;
    public Textures textures;
    public Font font;
    public Screen screen;
    public ProgressRenderer progressRenderer;
    public GameRenderer gameRenderer;
    private BackgroundDownloader bgLoader;
    private int ticks;
    private int missTime;
    private int orgWidth;
    private int orgHeight;
    public AchievementPopup achievementPopup;
    public Gui gui;
    public boolean noRender;
    public HumanoidModel humanoidModel;
    public HitResult hitResult;
    public Options options;
    protected MinecraftApplet minecraftApplet;
    public SoundEngine soundEngine;
    public MouseHandler mouseHandler;
    public TexturePackRepository skins;
    private File workingDirectory;
    private LevelStorageSource levelSource;
    public static long[] frameTimes;
    public static long[] tickTimes;
    public static int frameTimePos;
    public static long warezTime;
    public StatsCounter stats;
    private String connectToIp;
    private int connectToPort;
    private WaterTexture waterTexture;
    private LavaTexture lavaTexture;
    private static File workDir;
    public volatile boolean running;
    public String fpsString;
    boolean wasDown;
    long lastTimer;
    public boolean mouseGrabbed;
    private int lastClickTick;
    public boolean isRaining;
    long lastTickTime;
    private int recheckPlayerIn;
    
    public Minecraft(final Component component, final Canvas parent, final MinecraftApplet minecraftApplet, final int width, final int height, final boolean fullscreen) {
        this.fullscreen = false;
        this.hasCrashed = false;
        this.timer = new Timer(20.0f);
        this.user = null;
        this.appletMode = true;
        this.pause = false;
        this.screen = null;
        this.progressRenderer = new ProgressRenderer(this);
        this.ticks = 0;
        this.missTime = 0;
        this.achievementPopup = new AchievementPopup(this);
        this.noRender = false;
        this.humanoidModel = new HumanoidModel(0.0f);
        this.hitResult = null;
        this.soundEngine = new SoundEngine();
        this.waterTexture = new WaterTexture();
        this.lavaTexture = new LavaTexture();
        this.running = true;
        this.fpsString = "";
        this.wasDown = false;
        this.lastTimer = -1L;
        this.mouseGrabbed = false;
        this.lastClickTick = 0;
        this.isRaining = false;
        this.lastTickTime = System.currentTimeMillis();
        this.recheckPlayerIn = 0;
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
            final Graphics graphics = this.parent.getGraphics();
            if (graphics != null) {
                graphics.setColor(Color.BLACK);
                graphics.fillRect(0, 0, this.width, this.height);
                graphics.dispose();
            }
            Display.setParent(this.parent);
        }
        else if (this.fullscreen) {
            Display.setFullscreen(true);
            this.width = Display.getDisplayMode().getWidth();
            this.height = Display.getDisplayMode().getHeight();
            if (this.width <= 0) {
                this.width = 1;
            }
            if (this.height <= 0) {
                this.height = 1;
            }
        }
        else {
            Display.setDisplayMode(new DisplayMode(this.width, this.height));
        }
        Display.setTitle(VERSION_STRING);
        try {
            Display.create();
        }
        catch (final LWJGLException ex) {
            ex.printStackTrace();
            try {
                Thread.sleep(1000L);
            }
            catch (final InterruptedException ex2) {}
            Display.create();
        }
        this.workingDirectory = getWorkingDirectory();
        this.levelSource = new McRegionLevelStorageSource(new File(this.workingDirectory, "saves"));
        this.options = new Options(this, this.workingDirectory);
        this.skins = new TexturePackRepository(this, this.workingDirectory);
        this.textures = new Textures(this.skins, this.options);
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
        catch (final Exception ex3) {
            ex3.printStackTrace();
        }
        this.checkGlError("Pre startup");
        glEnable(GL_TEXTURE_2D);
        glShadeModel(GL_SMOOTH);
        glClearDepth(1.0);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(516, 0.1f);
        glCullFace(1029);
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
            (this.bgLoader = new BackgroundDownloader(this.workingDirectory, this)).start();
        }
        catch (final Exception ex4) {}
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
        final ScreenSizeCalculator screenSizeCalculator = new ScreenSizeCalculator(this.options, this.width, this.height);
        glClear(16640);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, screenSizeCalculator.rawWidth, screenSizeCalculator.rawHeight, 0.0, 1000.0, 3000.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(0.0f, 0.0f, -2000.0f);
        glViewport(0, 0, this.width, this.height);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        final Tesselator instance = Tesselator.instance;
        glDisable(GL_LIGHTING);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_FOG);
        glBindTexture(GL_TEXTURE_2D, this.textures.loadTexture("/title/mojang.png"));
        instance.begin();
        instance.color(0xffffff);
        instance.vertexUV(0.0, this.height, 0.0, 0.0, 0.0);
        instance.vertexUV(this.width, this.height, 0.0, 0.0, 0.0);
        instance.vertexUV(this.width, 0.0, 0.0, 0.0, 0.0);
        instance.vertexUV(0.0, 0.0, 0.0, 0.0, 0.0);
        instance.end();
        final int w = 256;
        final int h = 256;
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        instance.color(0xffffff);
        this.blit((screenSizeCalculator.getWidth() - w) / 2, (screenSizeCalculator.getHeight() - h) / 2, 0, 0, w, h);
        glDisable(GL_LIGHTING);
        glDisable(GL_FOG);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(516, 0.1f);
        Display.swapBuffers();
    }
    
    public void blit(final int x, final int y, final int sx, final int sy, final int w, final int h) {
        final float n = 0.00390625f;
        final float n2 = 0.00390625f;
        final Tesselator instance = Tesselator.instance;
        instance.begin();
        instance.vertexUV(x + 0, y + h, 0.0, (sx + 0) * n, (sy + h) * n2);
        instance.vertexUV(x + w, y + h, 0.0, (sx + w) * n, (sy + h) * n2);
        instance.vertexUV(x + w, y + 0, 0.0, (sx + w) * n, (sy + 0) * n2);
        instance.vertexUV(x + 0, y + 0, 0.0, (sx + 0) * n, (sy + 0) * n2);
        instance.end();
    }
    
    public static File getWorkingDirectory() {
        if (Minecraft.workDir == null) {
            Minecraft.workDir = getWorkingDirectory("minecraft");
        }
        return Minecraft.workDir;
    }
    
    public static File getWorkingDirectory(final String applicationName) {
        final String property = System.getProperty("user.home", ".");
        File obj = null;
        switch (getPlatform()) {
            case linux:
            case solaris: {
                obj = new File(property, '.' + applicationName + '/');
                break;
            }
            case windows: {
                final String getenv = System.getenv("APPDATA");
                if (getenv != null) {
                    obj = new File(getenv, "." + applicationName + '/');
                    break;
                }
                obj = new File(property, '.' + applicationName + '/');
                break;
            }
            case macos: {
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
    
    private static OS getPlatform() {
        final String lowerCase = System.getProperty("os.name").toLowerCase();
        if (lowerCase.contains("win")) {
            return OS.windows;
        }
        if (lowerCase.contains("mac")) {
            return OS.macos;
        }
        if (lowerCase.contains("solaris")) {
            return OS.solaris;
        }
        if (lowerCase.contains("sunos")) {
            return OS.solaris;
        }
        if (lowerCase.contains("linux")) {
            return OS.linux;
        }
        if (lowerCase.contains("unix")) {
            return OS.linux;
        }
        return OS.unknown;
    }
    
    public LevelStorageSource getLevelSource() {
        return this.levelSource;
    }
    
    public void setScreen(Screen var_1_40) {
        if (this.screen instanceof ErrorScreen) {
            return;
        }
        if (this.screen != null) {
            this.screen.removed();
        }
        if (var_1_40 instanceof TitleScreen) {
            this.stats.forceSend();
        }
        this.stats.forceSave();
        if (var_1_40 == null && this.level == null) {
            var_1_40 = new TitleScreen();
        }
        else if (var_1_40 == null && this.player.health <= 0) {
            var_1_40 = new DeathScreen();
        }
        if (var_1_40 instanceof TitleScreen) {
            this.gui.clearMessages();
        }
        if ((this.screen = var_1_40) != null) {
            this.releaseMouse();
            final ScreenSizeCalculator screenSizeCalculator = new ScreenSizeCalculator(this.options, this.width, this.height);
            var_1_40.init(this, screenSizeCalculator.getWidth(), screenSizeCalculator.getHeight());
            this.noRender = false;
        }
        else {
            this.grabMouse();
        }
    }
    
    private void checkGlError(final String str) {
        final int glGetError = glGetError();
        if (glGetError != 0) {
            final String gluErrorString = GLU.gluErrorString(glGetError);
            System.out.println("########## GL ERROR ##########");
            System.out.println("@ " + str);
            System.out.println(glGetError + ": " + gluErrorString);
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
            catch (final Exception ex) {}
            System.out.println("Stopping!");
            try {
                this.setLevel(null);
            }
            catch (final Throwable t) {}
            try {
                MemoryTracker.release();
            }
            catch (final Throwable t2) {}
            this.soundEngine.destroy();
            Mouse.destroy();
            Keyboard.destroy();
        }
        finally {
            Display.destroy();
            if (!this.hasCrashed) {
                System.exit(0);
            }
        }
        System.gc();
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
        try {
            long currentTimeMillis = System.currentTimeMillis();
            int i = 0;
            while (this.running) {
                try {
                    if (this.minecraftApplet != null && !this.minecraftApplet.isActive()) {
                        break;
                    }
                    AABB.resetPool();
                    Vec3.resetPool();
                    if (this.parent == null && Display.isCloseRequested()) {
                        this.stop();
                    }
                    if (this.pause && this.level != null) {
                        final float partialTick = this.timer.partialTick;
                        this.timer.advanceTime();
                        this.timer.partialTick = partialTick;
                    }
                    else {
                        this.timer.advanceTime();
                    }
                    final long nanoTime = System.nanoTime();
                    for (int j = 0; j < this.timer.ticks; ++j) {
                        ++this.ticks;
                        try {
                            this.tick();
                        }
                        catch (final LevelStorageException ex) {
                            this.setLevel(this.level = null);
                            this.setScreen(new LevelConflictScreen());
                        }
                    }
                    final long tickTime = System.nanoTime() - nanoTime;
                    this.checkGlError("Pre render");
                    TileRenderer.fancy = this.options.fancyGraphics;
                    this.soundEngine.update(this.player, this.timer.partialTick);
                    glEnable(GL_TEXTURE_2D);
                    if (this.level != null) {
                        this.level.updateLights();
                    }
                    if (!Keyboard.isKeyDown(65)) {
                        Display.update();
                    }
                    if (this.player != null && this.player.isInWall()) {
                        this.options.thirdPersonView = false;
                    }
                    if (!this.noRender) {
                        if (this.gameMode != null) {
                            this.gameMode.render(this.timer.partialTick);
                        }
                        this.gameRenderer.render(this.timer.partialTick);
                    }
                    if (!Display.isActive()) {
                        if (this.fullscreen) {
                            this.toggleFullScreen();
                        }
                        Thread.sleep(10L);
                    }
                    if (this.options.renderDebug) {
                        this.renderFpsMeter(tickTime);
                    }
                    else {
                        this.lastTimer = System.nanoTime();
                    }
                    this.achievementPopup.render();
                    Thread.yield();
                    if (Keyboard.isKeyDown(65)) {
                        Display.update();
                    }
                    this.checkScreenshot();
                    if (this.parent != null && !this.fullscreen && (this.parent.getWidth() != this.width || this.parent.getHeight() != this.height)) {
                        this.width = this.parent.getWidth();
                        this.height = this.parent.getHeight();
                        if (this.width <= 0) {
                            this.width = 1;
                        }
                        if (this.height <= 0) {
                            this.height = 1;
                        }
                        this.resize(this.width, this.height);
                    }
                    this.checkGlError("Post render");
                    ++i;
                    this.pause = (!this.isClientSide() && this.screen != null && this.screen.isPauseScreen());
                    while (System.currentTimeMillis() >= currentTimeMillis + 1000L) {
                        this.fpsString = i + " fps, " + Chunk.updates + " chunk updates";
                        Chunk.updates = 0;
                        currentTimeMillis += 1000L;
                        i = 0;
                    }
                }
                catch (final LevelStorageException ex2) {
                    this.setLevel(this.level = null);
                    this.setScreen(new LevelConflictScreen());
                }
                catch (final OutOfMemoryError outOfMemoryError) {
                    this.emergencySave();
                    this.setScreen(new OutOfMemoryScreen());
                    System.gc();
                }
            }
        }
        catch (final StopGameException ex3) {}
        catch (final Throwable e2) {
            this.emergencySave();
            e2.printStackTrace();
            this.crash(new CrashReport("Unexpected error", e2));
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
        catch (final Throwable t) {}
        try {
            System.gc();
            AABB.clearPool();
            Vec3.clearPool();
        }
        catch (final Throwable t2) {}
        try {
            System.gc();
            this.setLevel(null);
        }
        catch (final Throwable t3) {}
        System.gc();
    }
    
    private void checkScreenshot() {
        if (Keyboard.isKeyDown(60)) {
            if (!this.wasDown) {
                this.wasDown = true;
                this.gui.addMessage(Screenshot.grab(Minecraft.workDir, this.width, this.height));
            }
        }
        else {
            this.wasDown = false;
        }
    }
    
    private void renderFpsMeter(final long tickTime) {
        final long n = 16666666L;
        if (this.lastTimer == -1L) {
            this.lastTimer = System.nanoTime();
        }
        final long nanoTime = System.nanoTime();
        Minecraft.tickTimes[Minecraft.frameTimePos & Minecraft.frameTimes.length - 1] = tickTime;
        Minecraft.frameTimes[Minecraft.frameTimePos++ & Minecraft.frameTimes.length - 1] = nanoTime - this.lastTimer;
        this.lastTimer = nanoTime;
        glClear(GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, (double)this.width, (double)this.height, 0.0, 1000.0, 3000.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(0.0f, 0.0f, -2000.0f);
        glLineWidth(1.0f);
        glDisable(GL_TEXTURE_2D);
        final Tesselator instance = Tesselator.instance;
        instance.begin(GL_QUADS);
        final int n2 = (int)(n / 200000L);
        instance.color(0x20000000);
        instance.vertex(0.0, this.height - n2, 0.0);
        instance.vertex(0.0, this.height, 0.0);
        instance.vertex(Minecraft.frameTimes.length, this.height, 0.0);
        instance.vertex(Minecraft.frameTimes.length, this.height - n2, 0.0);
        instance.color(0x20200000);
        instance.vertex(0.0, this.height - n2 * 2, 0.0);
        instance.vertex(0.0, this.height - n2, 0.0);
        instance.vertex(Minecraft.frameTimes.length, this.height - n2, 0.0);
        instance.vertex(Minecraft.frameTimes.length, this.height - n2 * 2, 0.0);
        instance.end();
        long n3 = 0L;
        for (int i = 0; i < Minecraft.frameTimes.length; ++i) {
            n3 += Minecraft.frameTimes[i];
        }
        final int n4 = (int)(n3 / 200000L / Minecraft.frameTimes.length);
        instance.begin(GL_QUADS);
        instance.color(0x20400000);
        instance.vertex(0.0, this.height - n4, 0.0);
        instance.vertex(0.0, this.height, 0.0);
        instance.vertex(Minecraft.frameTimes.length, this.height, 0.0);
        instance.vertex(Minecraft.frameTimes.length, this.height - n4, 0.0);
        instance.end();
        instance.begin(GL_LINES);
        for (int j = 0; j < Minecraft.frameTimes.length; ++j) {
            final int n5 = (j - Minecraft.frameTimePos & Minecraft.frameTimes.length - 1) * 255 / Minecraft.frameTimes.length;
            final int n6 = n5 * n5 / 255;
            final int n7 = n6 * n6 / 255;
            if (Minecraft.frameTimes[j] > n) {
                instance.color(0xff000000 + n7 * 0x10000);
            }
            else {
                instance.color(0xff000000 + n7 * 0x100);
            }
            final long n8 = Minecraft.frameTimes[j] / 200000L;
            final long n9 = Minecraft.tickTimes[j] / 200000L;
            instance.vertex(j + 0.5f, this.height - n8 + 0.5f, 0.0);
            instance.vertex(j + 0.5f, this.height + 0.5f, 0.0);
            instance.color(0xff000000 + n7 * 0x10000 + n7 * 0x100 + n7 * 0x1);
            instance.vertex(j + 0.5f, this.height - n8 + 0.5f, 0.0);
            instance.vertex(j + 0.5f, this.height - (n8 - n9) + 0.5f, 0.0);
        }
        instance.end();
        glEnable(GL_TEXTURE_2D);
    }
    
    public void stop() {
        this.running = false;
    }
    
    public void grabMouse() {
        if (!Display.isActive()) {
            return;
        }
        if (this.mouseGrabbed) {
            return;
        }
        this.mouseGrabbed = true;
        this.mouseHandler.grab();
        this.setScreen(null);
        this.missTime = 10000;
        this.lastClickTick = this.ticks + 10000;
    }
    
    public void releaseMouse() {
        if (!this.mouseGrabbed) {
            return;
        }
        if (this.player != null) {
            this.player.releaseAllKeys();
        }
        this.mouseGrabbed = false;
        this.mouseHandler.release();
    }
    
    public void pauseGame() {
        if (this.screen != null) {
            return;
        }
        this.setScreen(new PauseScreen());
    }
    
    private void handleMouseDown(final int button, final boolean down) {
        if (this.gameMode.instaBuild) {
            return;
        }
        if (!down) {
            this.missTime = 0;
        }
        if (button == 0 && this.missTime > 0) {
            return;
        }
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
        if (button == 0 && this.missTime > 0) {
            return;
        }
        if (button == 0) {
            this.player.swing();
        }
        boolean b = true;
        if (this.hitResult == null) {
            if (button == 0 && !(this.gameMode instanceof CreativeMode)) {
                this.missTime = 10;
            }
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
            final int f = this.hitResult.f;
            if (button == 0) {
                this.gameMode.startDestroyBlock(x, y, z, this.hitResult.f);
            }
            else {
                final ItemInstance selected = this.player.inventory.getSelected();
                final int n = (selected != null) ? selected.count : 0;
                if (this.gameMode.useItemOn(this.player, this.level, selected, x, y, z, f)) {
                    b = false;
                    this.player.swing();
                }
                if (selected == null) {
                    return;
                }
                if (selected.count == 0) {
                    this.player.inventory.items[this.player.inventory.selected] = null;
                }
                else if (selected.count != n) {
                    this.gameRenderer.itemInHandRenderer.itemPlaced();
                }
            }
        }
        if (b && button == 1) {
            final ItemInstance selected2 = this.player.inventory.getSelected();
            if (selected2 != null && this.gameMode.useItem(this.player, this.level, selected2)) {
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
                if (this.width <= 0) {
                    this.width = 1;
                }
                if (this.height <= 0) {
                    this.height = 1;
                }
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
                if (this.width <= 0) {
                    this.width = 1;
                }
                if (this.height <= 0) {
                    this.height = 1;
                }
            }
            if (this.screen != null) {
                this.resize(this.width, this.height);
            }
            Display.setFullscreen(this.fullscreen);
            Display.update();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void resize(int width, int height) {
        if (width <= 0) {
            width = 1;
        }
        if (height <= 0) {
            height = 1;
        }
        this.width = width;
        this.height = height;
        if (this.screen != null) {
            final ScreenSizeCalculator screenSizeCalculator = new ScreenSizeCalculator(this.options, width, height);
            this.screen.init(this, screenSizeCalculator.getWidth(), screenSizeCalculator.getHeight());
        }
    }
    
    private void handleGrabTexture() {
        if (this.hitResult != null) {
            int id = this.level.getTile(this.hitResult.x, this.hitResult.y, this.hitResult.z);
            if (id == Tile.grass.id) {
                id = Tile.dirt.id;
            }
            if (id == Tile.stoneSlab.id) {
                id = Tile.stoneSlabHalf.id;
            }
            if (id == Tile.unbreakable.id) {
                id = Tile.rock.id;
            }
            this.player.inventory.grabTexture(id, this.gameMode instanceof CreativeMode);
        }
    }
    
    private void verify() {
        new Thread(() -> {
            try {
                final HttpURLConnection httpURLConnection = (HttpURLConnection)new URL("https://login.minecraft.net/session?name=" + user.name + "&session=" + user.sessionId).openConnection();
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() == 400) {
                    warezTime = System.currentTimeMillis();
                }
                httpURLConnection.disconnect();
            }
            catch (final Exception ex) {
                ex.printStackTrace();
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
            final ChunkSource chunkSource = this.level.getChunkSource();
            if (chunkSource instanceof ChunkCache) {
                ((ChunkCache)chunkSource).centerOn(Mth.floor((float)(int)this.player.x) >> 4, Mth.floor((float)(int)this.player.z) >> 4);
            }
        }
        if (!this.pause && this.level != null) {
            this.gameMode.tick();
        }
        glBindTexture(GL_TEXTURE_2D, this.textures.loadTexture("/terrain.png"));
        if (!this.pause) {
            this.textures.tick();
        }
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
                if (System.currentTimeMillis() - this.lastTickTime > 200L) {
                    continue;
                }
                int eventDWheel = Mouse.getEventDWheel();
                if (eventDWheel != 0) {
                    this.player.inventory.swapPaint(eventDWheel);
                    if (this.options.isFlying) {
                        if (eventDWheel > 0) {
                            eventDWheel = 1;
                        }
                        if (eventDWheel < 0) {
                            eventDWheel = -1;
                        }
                        final Options options = this.options;
                        options.flySpeed += eventDWheel * 0.25f;
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
                }
                else {
                    if (this.screen == null) {
                        continue;
                    }
                    this.screen.mouseEvent();
                }
            }
            if (this.missTime > 0) {
                --this.missTime;
            }
            while (Keyboard.next()) {
                this.player.setKey(Keyboard.getEventKey(), Keyboard.getEventKeyState());
                if (Keyboard.getEventKeyState()) {
                    if (Keyboard.getEventKey() == 87) {
                        this.toggleFullScreen();
                    }
                    else {
                        if (this.screen != null) {
                            this.screen.keyboardEvent();
                        }
                        else {
                            if (Keyboard.getEventKey() == 1) {
                                this.pauseGame();
                            }
                            if (Keyboard.getEventKey() == 31 && Keyboard.isKeyDown(61)) {
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
                            if (Keyboard.getEventKey() == 2 + i) {
                                this.player.inventory.selected = i;
                            }
                        }
                        if (Keyboard.getEventKey() != this.options.keyFog.key) {
                            continue;
                        }
                        this.options.toggle(Option.RENDER_DISTANCE, (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)) ? -1 : 1);
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
            if (!this.pause) {
                this.gameRenderer.tick();
            }
            if (!this.pause) {
                this.levelRenderer.tick();
            }
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
            if (!this.pause && this.level != null) {
                this.level.animateTick(Mth.floor(this.player.x), Mth.floor(this.player.y), Mth.floor(this.player.z));
            }
            if (!this.pause) {
                this.particleEngine.tick();
            }
        }
        this.lastTickTime = System.currentTimeMillis();
    }
    
    private void reloadSound() {
        System.out.println("FORCING RELOAD!");
        (this.soundEngine = new SoundEngine()).init(this.options);
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
        final double x = this.player.x;
        final double z = this.player.z;
        final double n = 8.0;
        double x2;
        double z2;
        if (this.player.dimension == -1) {
            x2 = x / n;
            z2 = z / n;
            this.player.moveTo(x2, this.player.y, z2, this.player.yRot, this.player.xRot);
            if (this.player.isAlive()) {
                this.level.tick(this.player, false);
            }
            this.setLevel(new Level(this.level, Dimension.getNew(-1)), "Entering the Nether", this.player);
        }
        else {
            x2 = x * n;
            z2 = z * n;
            this.player.moveTo(x2, this.player.y, z2, this.player.yRot, this.player.xRot);
            if (this.player.isAlive()) {
                this.level.tick(this.player, false);
            }
            this.setLevel(new Level(this.level, Dimension.getNew(0)), "Leaving the Nether", this.player);
        }
        this.player.level = this.level;
        if (this.player.isAlive()) {
            this.player.moveTo(x2, this.player.y, z2, this.player.yRot, this.player.xRot);
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
        if (this.level != null) {
            this.level.forceSave(this.progressRenderer);
        }
        if ((this.level = level) != null) {
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
            if (!level.isClientSide) {
                this.prepareLevel(message);
            }
            if (this.player == null) {
                (this.player = (LocalPlayer)this.gameMode.createPlayer(level)).resetPos();
                this.gameMode.initPlayer(this.player);
            }
            this.player.input = new KeyboardInput(this.options);
            if (this.levelRenderer != null) {
                this.levelRenderer.setLevel(level);
            }
            if (this.particleEngine != null) {
                this.particleEngine.setLevel(level);
            }
            this.gameMode.adjustPlayer(this.player);
            if (forceInsertPlayer != null) {
                level.clearLoadedPlayerData();
            }
            final ChunkSource chunkSource = level.getChunkSource();
            if (chunkSource instanceof ChunkCache) {
                ((ChunkCache)chunkSource).centerOn(Mth.floor((float)(int)this.player.x) >> 4, Mth.floor((float)(int)this.player.z) >> 4);
            }
            level.loadPlayer(this.player);
            if (level.isNew) {
                level.forceSave(this.progressRenderer);
            }
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
        final int n = 128;
        int n2 = 0;
        final int n3 = n * 2 / 16 + 1;
        final int n4 = n3 * n3;
        final ChunkSource chunkSource = this.level.getChunkSource();
        final Pos sharedSpawnPos = this.level.getSharedSpawnPos();
        if (this.player != null) {
            sharedSpawnPos.x = (int)this.player.x;
            sharedSpawnPos.z = (int)this.player.z;
        }
        if (chunkSource instanceof ChunkCache) {
            ((ChunkCache)chunkSource).centerOn(sharedSpawnPos.x >> 4, sharedSpawnPos.z >> 4);
        }
        for (int i = -n; i <= n; i += 16) {
            for (int j = -n; j <= n; j += 16) {
                this.progressRenderer.progressStagePercentage(n2++ * 100 / n4);
                this.level.getTile(sharedSpawnPos.x + i, 64, sharedSpawnPos.z + j);
                while (this.level.updateLights()) {}
            }
        }
        this.progressRenderer.progressStage("Simulating world for a bit");
        this.level.prepare();
    }
    
    public void fileDownloaded(String substring, final File file) {
        final int index = substring.indexOf("/");
        final String substring2 = substring.substring(0, index);
        substring = substring.substring(index + 1);
        if (substring2.equalsIgnoreCase("sound")) {
            this.soundEngine.add(substring, file);
        }
        else if (substring2.equalsIgnoreCase("newsound")) {
            this.soundEngine.add(substring, file);
        }
        else if (substring2.equalsIgnoreCase("streaming")) {
            this.soundEngine.addStreaming(substring, file);
        }
        else if (substring2.equalsIgnoreCase("music")) {
            this.soundEngine.addMusic(substring, file);
        }
        else if (substring2.equalsIgnoreCase("newmusic")) {
            this.soundEngine.addMusic(substring, file);
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
    
    public void respawnPlayer(final boolean boolean1, final int integer) {
        if (!this.level.isClientSide && !this.level.dimension.mayRespawn()) {
            this.toggleDimension();
        }
        Pos respawnPosition = null;
        Pos pos = null;
        boolean b = true;
        if (this.player != null && !boolean1) {
            respawnPosition = this.player.getRespawnPosition();
            if (respawnPosition != null) {
                pos = Player.checkBedValidRespawnPosition(this.level, respawnPosition);
                if (pos == null) {
                    this.player.displayClientMessage("tile.bed.notValid");
                }
            }
        }
        if (pos == null) {
            pos = this.level.getSharedSpawnPos();
            b = false;
        }
        final ChunkSource chunkSource = this.level.getChunkSource();
        if (chunkSource instanceof ChunkCache) {
            ((ChunkCache)chunkSource).centerOn(pos.x >> 4, pos.z >> 4);
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
        this.player.dimension = integer;
        this.cameraTargetPlayer = this.player;
        this.player.resetPos();
        if (b) {
            this.player.setRespawnPosition(respawnPosition);
            this.player.moveTo(pos.x + 0.5f, pos.y + 0.1f, pos.z + 0.5f, 0.0f, 0.0f);
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
        thread.setPriority(10);
        minecraft.serverDomain = "www.minecraft.net";
        if (name != null && sessionId != null) {
            minecraft.user = new User(name, sessionId);
        }
        else {
            minecraft.user = new User("Player" + System.currentTimeMillis() % 1000L, "");
        }
        if (url != null) {
            final String[] split = url.split(":");
            minecraft.connectTo(split[0], Integer.parseInt(split[1]));
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
        if (this.player instanceof MultiplayerLocalPlayer) {
            return ((MultiplayerLocalPlayer)this.player).connection;
        }
        return null;
    }
    
    public static void main(final String[] args) {
        String string = "Player" + System.currentTimeMillis() % 1000L;
        if (args.length > 0) {
            string = args[0];
        }
        String sessionId = "-";
        if (args.length > 1) {
            sessionId = args[1];
        }
        start(string, sessionId);
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
        if (chatMessage.startsWith("/")) {}
        return false;
    }
    
    static {
        Minecraft.__unused_byte_buffer = new byte[10485760];
        Minecraft.frameTimes = new long[512];
        Minecraft.tickTimes = new long[512];
        Minecraft.frameTimePos = 0;
        Minecraft.warezTime = 0L;
        Minecraft.workDir = null;
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
