// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import net.minecraft.client.renderer.culling.Culler;
import org.lwjgl.opengl.GLContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.Level;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.Lighting;
import net.minecraft.client.renderer.culling.FrustumCuller;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.level.chunk.ChunkCache;
import org.lwjgl.input.Mouse;
import net.minecraft.client.gui.ScreenSizeCalculator;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.NVFogDistance;
import org.lwjgl.util.glu.GLU;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import java.util.List;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import net.minecraft.client.gamemode.CreativeMode;
import util.Mth;
import net.minecraft.client.MemoryTracker;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.world.SmoothFloat;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.Minecraft;

import static org.lwjgl.opengl.GL11.*;

public class GameRenderer
{
    public static boolean anaglyph3d = false;
    public static int anaglyphPass;
    private Minecraft mc;
    private float renderDistance = 0.0f;
    public ItemInHandRenderer itemInHandRenderer;
    private int tick;
    private Entity hovered = null;
    private SmoothFloat smoothTurnX = new SmoothFloat();
    private SmoothFloat smoothTurnY = new SmoothFloat();
    private SmoothFloat smoothDistance = new SmoothFloat();
    private SmoothFloat smoothRoation = new SmoothFloat();
    private SmoothFloat smoothTilt = new SmoothFloat();
    private SmoothFloat smoothRoll = new SmoothFloat();
    private float thirdDistance = 4.0f;
    private float thirdDistanceO = 4.0f;
    private float thirdRotation = 0.0f;
    private float thirdRotationO = 0.0f;
    private float thirdTilt = 0.0f;
    private float thirdTiltO = 0.0f;
    private float fovOffset = 0.0f;
    private float fovOffsetO = 0.0f;
    private float cameraRoll = 0.0f;
    private float cameraRollO = 0.0f;
    private boolean isInClouds = false;
    private double zoom = 1.0;
    private double zoom_x = 0.0;
    private double zoom_y = 0.0;
    private long lastActiveTime = System.currentTimeMillis();
    private long lastNsTime = 0L;
    private Random random = new Random();
    private int rainSoundTime = 0;
    volatile int xMod = 0;
    volatile int yMod = 0;
    FloatBuffer lb = MemoryTracker.createFloatBuffer(16);
    float fr;
    float fg;
    float fb;
    private float fogBrO;
    private float fogBr;
    
    public GameRenderer(final Minecraft mc) {
        this.mc = mc;
        this.itemInHandRenderer = new ItemInHandRenderer(mc);
    }
    
    public void tick() {
        this.fogBrO = this.fogBr;
        this.thirdDistanceO = this.thirdDistance;
        this.thirdRotationO = this.thirdRotation;
        this.thirdTiltO = this.thirdTilt;
        this.fovOffsetO = this.fovOffset;
        this.cameraRollO = this.cameraRoll;

        if (this.mc.cameraTargetPlayer == null) {
            this.mc.cameraTargetPlayer = this.mc.player;
        }

        final float brr = this.mc.level.getBrightness(Mth.floor(this.mc.cameraTargetPlayer.x), Mth.floor(this.mc.cameraTargetPlayer.y), Mth.floor(this.mc.cameraTargetPlayer.z));
        final float whiteness = (3 - this.mc.options.viewDistance) / 3.0f;
        float fogBrT = brr * (1.0f - whiteness) + whiteness;
        this.fogBr += (fogBrT - this.fogBr) * 0.1f;

        ++this.tick;
        this.itemInHandRenderer.tick();
        this.tickRain();
    }
    
    public void pick(final float a) {
        if (this.mc.cameraTargetPlayer == null) return;
        if (this.mc.level == null) return;

        double range = this.mc.gameMode.getPickRange();
        this.mc.hitResult = this.mc.cameraTargetPlayer.pick(range, a);

        double dist = range;
        final Vec3 from = this.mc.cameraTargetPlayer.getPos(a);

        if (this.mc.hitResult != null) {
            dist = this.mc.hitResult.pos.distanceTo(from);
        }

        if (this.mc.gameMode instanceof CreativeMode) {
            range = 32.0;
        }
        else {
            if (dist > 3.0) dist = 3.0;
            range = dist;
        }

        final Vec3 b = this.mc.cameraTargetPlayer.getViewVector(a);
        final Vec3 to = from.add(b.x * range, b.y * range, b.z * range);
        this.hovered = null;
        final float overlap = 1.0f;
        final List<Entity> objects = this.mc.level.getEntities(this.mc.cameraTargetPlayer, this.mc.cameraTargetPlayer.bb.expand(b.x * range, b.y * range, b.z * range).grow(overlap, overlap, overlap));
        double nearest = 0.0;

        for (int i = 0; i < objects.size(); ++i) {
            final Entity e = objects.get(i);
            if (!e.isPickable()) continue;

            final float rr = e.getPickRadius();
            final AABB bb = e.bb.grow(rr, rr, rr);
            final HitResult p = bb.clip(from, to);
            if (bb.contains(from)) {
                if (0.0 < nearest || nearest == 0.0) {
                    this.hovered = e;
                    nearest = 0.0;
                }
            }
            else if (p != null) {
                final double dd = from.distanceTo(p.pos);
                if (dd < nearest || nearest == 0.0) {
                    this.hovered = e;
                    nearest = dd;
                }
            }
        }

        if (this.hovered != null && !(this.mc.gameMode instanceof CreativeMode)) {
            this.mc.hitResult = new HitResult(this.hovered);
        }
    }
    
    private float getFov(final float a) {
        final Mob player = this.mc.cameraTargetPlayer;
        float fov = 70.0f;
        if (player.isUnderLiquid(Material.water)) {
            fov = 60.0f;
        }

        if (player.health <= 0) {
            fov /= (1.0f - 500.0f / (player.deathTime + a + 500.0f)) * 2.0f + 1.0f;
        }

        return fov + this.fovOffsetO + (this.fovOffset - this.fovOffsetO) * a;
    }
    
    private void bobHurt(final float a) {
        final Mob player = this.mc.cameraTargetPlayer;

        float hurt = player.hurtTime - a;

        if (player.health <= 0) {
            float duration = player.deathTime + a;

            glRotatef(40.0f - 8000.0f / (duration + 200.0f), 0.0f, 0.0f, 1.0f);
        }

        if (hurt < 0.0f) return;
        hurt = hurt / player.hurtDuration;
        hurt = Mth.sin(hurt * hurt * hurt * hurt * Mth.PI);

        final float rr = player.hurtDir;

        glRotatef(-rr, 0.0f, 1.0f, 0.0f);
        glRotatef(-hurt * 14.0f, 0.0f, 0.0f, 1.0f);
        glRotatef(rr, 0.0f, 1.0f, 0.0f);
    }
    
    private void bobView(final float a) {
        if (!(this.mc.cameraTargetPlayer instanceof Player)) return;

        final Player player = (Player)this.mc.cameraTargetPlayer;
        final float wda = player.walkDist - player.walkDistO;
        final float b = -(player.walkDist + wda * a);
        final float bob = player.oBob + (player.bob - player.oBob) * a;
        final float tilt = player.oTilt + (player.tilt - player.oTilt) * a;
        glTranslatef(Mth.sin(b * Mth.PI) * bob * 0.5f, -Math.abs(Mth.cos(b * Mth.PI) * bob), 0.0f);
        glRotatef(Mth.sin(b * Mth.PI) * bob * 3.0f, 0.0f, 0.0f, 1.0f);
        glRotatef(Math.abs(Mth.cos(b * Mth.PI - 0.2f) * bob) * 5.0f, 1.0f, 0.0f, 0.0f);
        glRotatef(tilt, 1.0f, 0.0f, 0.0f);
    }
    
    private void moveCameraToPlayer(final float a) {
        final Mob player = this.mc.cameraTargetPlayer;
        float heightOffset = player.heightOffset - 1.62f;

        double x = player.xo + (player.x - player.xo) * a;
        double y = player.yo + (player.y - player.yo) * a - heightOffset;
        double z = player.zo + (player.z - player.zo) * a;

        glRotatef(this.cameraRollO + (this.cameraRoll - this.cameraRollO) * a, 0.0f, 0.0f, 1.0f);

        if (player.isSleeping()) {
            heightOffset += 1.0f;
            glTranslatef(0.0f, 0.3f, 0.0f);
            if (!this.mc.options.fixedCamera) {
                int t = this.mc.level.getTile(Mth.floor(player.x), Mth.floor(player.y), Mth.floor(player.z));
                if (t == Tile.bed.id) {
                    int data = this.mc.level.getData(Mth.floor(player.x), Mth.floor(player.y), Mth.floor(player.z));

                    int direction = data & 0x3;
                    glRotatef((float)(direction * 90), 0.0f, 1.0f, 0.0f);
                }
                glRotatef(player.yRotO + (player.yRot - player.yRotO) * a + 180.0f, 0.0f, -1.0f, 0.0f);
                glRotatef(player.xRotO + (player.xRot - player.xRotO) * a, -1.0f, 0.0f, 0.0f);
            }
        }
        else if (this.mc.options.thirdPersonView) {
            double cameraDist = this.thirdDistanceO + (this.thirdDistance - this.thirdDistanceO) * a;

            if (this.mc.options.fixedCamera) {
                final float rotationY = this.thirdRotationO + (this.thirdRotation - this.thirdRotationO) * a;
                final float xRot = this.thirdTiltO + (this.thirdTilt - this.thirdTiltO) * a;

                glTranslatef(0.0f, 0.0f, (float)(-cameraDist));
                glRotatef(xRot, 1.0f, 0.0f, 0.0f);
                glRotatef(rotationY, 0.0f, 1.0f, 0.0f);
            }
            else {
                final float yRot = player.yRot;
                final float xRot = player.xRot;

                final double xd = -Mth.sin(yRot / 180.0f * Mth.PI) * Mth.cos(xRot / 180.0f * Mth.PI) * cameraDist;
                final double zd = Mth.cos(yRot / 180.0f * Mth.PI) * Mth.cos(xRot / 180.0f * Mth.PI) * cameraDist;
                final double yd = -Mth.sin(xRot / 180.0f * Mth.PI) * cameraDist;

                for (int i = 0; i < 8; ++i) {
                    float xo = (float)((i & 0x1) * 2 - 1);
                    float yo = (float)((i >> 1 & 0x1) * 2 - 1);
                    float zo = (float)((i >> 2 & 0x1) * 2 - 1);

                    xo *= 0.1f;
                    yo *= 0.1f;
                    zo *= 0.1f;

                    final HitResult hr = this.mc.level.clip(Vec3.newTemp(x + xo, y + yo, z + zo), Vec3.newTemp(x - xd + xo + zo, y - yd + yo, z - zd + zo));
                    if (hr != null) {
                        final double dist = hr.pos.distanceTo(Vec3.newTemp(x, y, z));
                        if (dist < cameraDist) cameraDist = dist;
                    }
                }

                glRotatef(player.xRot - xRot, 1.0f, 0.0f, 0.0f);
                glRotatef(player.yRot - yRot, 0.0f, 1.0f, 0.0f);
                glTranslatef(0.0f, 0.0f, (float)(-cameraDist));
                glRotatef(yRot - player.yRot, 0.0f, 1.0f, 0.0f);
                glRotatef(xRot - player.xRot, 1.0f, 0.0f, 0.0f);
            }
        }
        else {
            glTranslatef(0.0f, 0.0f, -0.1f);
        }
        if (!this.mc.options.fixedCamera) {
            glRotatef(player.xRotO + (player.xRot - player.xRotO) * a, 1.0f, 0.0f, 0.0f);
            glRotatef(player.yRotO + (player.yRot - player.yRotO) * a + 180.0f, 0.0f, 1.0f, 0.0f);
        }

        glTranslatef(0.0f, heightOffset, 0.0f);

        x = player.xo + (player.x - player.xo) * a;
        y = player.yo + (player.y - player.yo) * a - heightOffset;
        z = player.zo + (player.z - player.zo) * a;

        this.isInClouds = this.mc.levelRenderer.isInCloud(x, y, z, a);
    }

    public void zoomRegion(double zoom, double xa, double za) { // Useless - In b1.2 & LCE leaks
        this.zoom = zoom;
        this.zoom_x = xa;
        this.zoom_y = za;
    }

    public void unZoomRegion() { // Useless - In b1.2 & LCE leaks
        this.zoom = 1.0;
    }


    private void setupCamera(final float a, final int eye) {
        this.renderDistance = (float)((16 * 16) >> this.mc.options.viewDistance);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        final float stereoScale = 0.07f;
        if (this.mc.options.anaglyph3d) glTranslatef(-(eye * 2 - 1) * stereoScale, 0.0f, 0.0f);

        if (this.zoom != 1.0) {
            glTranslatef((float)this.zoom_x, (float)(-this.zoom_y), 0.0f);
            glScaled(this.zoom, this.zoom, 1.0);
            GLU.gluPerspective(this.getFov(a), this.mc.width / (float)this.mc.height, 0.05f, this.renderDistance * 2.0f);
        }
        else {
            GLU.gluPerspective(this.getFov(a), this.mc.width / (float)this.mc.height, 0.05f, this.renderDistance * 2.0f);
        }

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        if (this.mc.options.anaglyph3d) glTranslatef((eye * 2 - 1) * 0.1f, 0.0f, 0.0f);

        this.bobHurt(a);

        if (this.mc.options.bobView) this.bobView(a);

        final float pt = this.mc.player.oPortalTime + (this.mc.player.portalTime - this.mc.player.oPortalTime) * a;
        if (pt > 0.0f) {
            int multiplier = 20;

            float skew = 5.0f / (pt * pt + 5.0f) - pt * 0.04f;
            skew *= skew;
            glRotatef((this.tick + a) * multiplier, 0.0f, 1.0f, 1.0f);
            glScalef(1.0f / skew, 1.0f, 1.0f);
            glRotatef(-(this.tick + a) * multiplier, 0.0f, 1.0f, 1.0f);
        }

        this.moveCameraToPlayer(a);
    }
    
    private void renderItemInHand(final float a, final int eye) {
        glLoadIdentity();
        if (this.mc.options.anaglyph3d) glTranslatef((eye * 2 - 1) * 0.1f, 0.0f, 0.0f);

        glPushMatrix();
        this.bobHurt(a);
        if (this.mc.options.bobView) this.bobView(a);

        if (!this.mc.options.thirdPersonView && !this.mc.cameraTargetPlayer.isSleeping() && !this.mc.options.hideGui) {
            this.itemInHandRenderer.render(a);
        }

        glPopMatrix();
        if (!this.mc.options.thirdPersonView && !this.mc.cameraTargetPlayer.isSleeping()) {
            this.itemInHandRenderer.renderScreenEffect(a);
            this.bobHurt(a);
        }

        if (this.mc.options.bobView) this.bobView(a);
    }
    
    public void render(final float a) {
        if (Display.isActive()) {
            this.lastActiveTime = System.currentTimeMillis();
        } else {
            if (System.currentTimeMillis() - this.lastActiveTime > 500L) {
                this.mc.pauseGame();
            }
        }

        if (this.mc.mouseGrabbed) {
            this.mc.mouseHandler.poll();

            final float ss = this.mc.options.sensitivity * 0.6f + 0.2f;
            final float sens = ss * ss * ss * 8.0f;
            float xo = this.mc.mouseHandler.xd * sens;
            float yo = this.mc.mouseHandler.yd * sens;

            int yAxis = 1;
            if (this.mc.options.invertYMouse) yAxis = -1;

            if (this.mc.options.smoothCamera) {
                xo = this.smoothTurnX.getNewDeltaValue(xo, 0.05f * sens);
                yo = this.smoothTurnY.getNewDeltaValue(yo, 0.05f * sens);
            }

            this.mc.player.turn(xo, yo * yAxis);
        }
        if (this.mc.noRender) return;
        GameRenderer.anaglyph3d = this.mc.options.anaglyph3d;

        final ScreenSizeCalculator ssc = new ScreenSizeCalculator(this.mc.options, this.mc.width, this.mc.height);
        final int screenWidth = ssc.getWidth();
        final int screenHeight = ssc.getHeight();
        final int xMouse = Mouse.getX() * screenWidth / this.mc.width;
        final int yMouse = screenHeight - Mouse.getY() * screenHeight / this.mc.height - 1;

        int maxFps = 200;
        if (this.mc.options.limitFramerate == 1) maxFps = 120;
        if (this.mc.options.limitFramerate == 2) maxFps = 40;

        if (this.mc.level != null) {
            if (this.mc.options.limitFramerate == 0) {
                this.renderLevel(a, 0L);
            }
            else {
                this.renderLevel(a, this.lastNsTime + 1000000000 / maxFps);
            }

            if (this.mc.options.limitFramerate == 2) { // TODO Useless - cannot find references for this framelimit code, not in the LCE leak nor was in b1.2 leak
                final long millis = (this.lastNsTime + 1000000000 / maxFps - System.nanoTime()) / 1000000L;
                if (millis > 0L && millis < 500L) {
                    try {
                        Thread.sleep(millis);
                    }
                    catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            this.lastNsTime = System.nanoTime();
            if (!this.mc.options.hideGui || this.mc.screen != null) {
                this.mc.gui.render(a, this.mc.screen != null, xMouse, yMouse);
            }
        }
        else {
            glViewport(0, 0, this.mc.width, this.mc.height);
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
            this.setupGuiScreen();

            if (this.mc.options.limitFramerate == 2) { // TODO Useless - cannot find references for this framelimit code, not in the LCE leak nor was in b1.2 leak
                long millis = (this.lastNsTime + 1000000000 / maxFps - System.nanoTime()) / 1000000L;
                if (millis < 0L) {
                    millis += 10L;
                }
                if (millis > 0L && millis < 500L) {
                    try {
                        Thread.sleep(millis);
                    }
                    catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            this.lastNsTime = System.nanoTime();
        }
        if (this.mc.screen != null) {
            glClear(GL_DEPTH_BUFFER_BIT);
            this.mc.screen.render(xMouse, yMouse, a);
            if (this.mc.screen != null && this.mc.screen.particles != null) this.mc.screen.particles.render(a);
        }
    }
    
    public void renderLevel(final float a, final long until) {
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);

        if (this.mc.cameraTargetPlayer == null) this.mc.cameraTargetPlayer = this.mc.player;

        this.pick(a);

        final Mob cameraEntity = this.mc.cameraTargetPlayer;
        final LevelRenderer levelRenderer = this.mc.levelRenderer;
        final ParticleEngine particleEngine = this.mc.particleEngine;
        final double xOff = cameraEntity.xOld + (cameraEntity.x - cameraEntity.xOld) * a;
        final double yOff = cameraEntity.yOld + (cameraEntity.y - cameraEntity.yOld) * a;
        final double zOff = cameraEntity.zOld + (cameraEntity.z - cameraEntity.zOld) * a;

        final ChunkSource chunkSource = this.mc.level.getChunkSource();
        if (chunkSource instanceof ChunkCache) { // Useless - Names here are a best guess given the code structure that existed in b1.2 leak
            ChunkCache cache = (ChunkCache)chunkSource;
            int x = Mth.floor((float)(int)xOff) >> 4;
            int z = Mth.floor((float)(int)zOff) >> 4;
            cache.centerOn(x, z);
        }

        for (int i = 0; i < 2; ++i) {
            if (this.mc.options.anaglyph3d) {
                GameRenderer.anaglyphPass = i;
                if (GameRenderer.anaglyphPass == 0) glColorMask(false, true, true, false);
                else glColorMask(true, false, false, false);
            }

            glViewport(0, 0, this.mc.width, this.mc.height);
            this.setupClearColor(a);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glEnable(GL_CULL_FACE);

            this.setupCamera(a, i);

            Frustum.getFrustum();
            if (this.mc.options.viewDistance < 2) {
                this.setupFog(-1, a);
                levelRenderer.renderSky(a);
            }
            glEnable(GL_FOG);
            this.setupFog(1, a);

            if (this.mc.options.ambientOcclusion) {
                glShadeModel(GL_SMOOTH);
            }

            final Culler frustum = new FrustumCuller();
            frustum.prepare(xOff, yOff, zOff);

            this.mc.levelRenderer.cull(frustum, a);
            if (i == 0) {
                do {
                    boolean retval = this.mc.levelRenderer.updateDirtyChunks(cameraEntity, false);
                    if (retval) break;

                    if (until == 0L) break;

                    final long diff = until - System.nanoTime();
                    if (diff < 0L) break;
                    if (diff > 1000000000L) break;
                } while (true);
            }
            this.setupFog(0, a);
            glEnable(GL_FOG);
            glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadTexture("/terrain.png"));
            Lighting.turnOff();
            levelRenderer.render(cameraEntity, 0, a);

            glShadeModel(GL_FLAT);

            Lighting.turnOn();
            levelRenderer.renderEntities(cameraEntity.getPos(a), frustum, a);
            particleEngine.renderLit(cameraEntity, a);
            Lighting.turnOff();
            this.setupFog(0, a);
            particleEngine.render(cameraEntity, a);

            if (this.mc.hitResult != null && cameraEntity.isUnderLiquid(Material.water) && cameraEntity instanceof Player) {
                final Player player = (Player)cameraEntity;
                glDisable(GL_ALPHA_TEST);
                levelRenderer.renderHit(player, this.mc.hitResult, 0, player.inventory.getSelected(), a);
                levelRenderer.renderHitOutline(player, this.mc.hitResult, 0, player.inventory.getSelected(), a);
                glEnable(GL_ALPHA_TEST);
            }

            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            this.setupFog(0, a);
            glEnable(GL_BLEND);
            glDisable(GL_CULL_FACE);
            glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadTexture("/terrain.png"));

            if (this.mc.options.fancyGraphics) {
                if (this.mc.options.ambientOcclusion) {
                    glShadeModel(GL_SMOOTH);
                }

                glColorMask(false, false, false, false);
                final int visibleWaterChunks = levelRenderer.render(cameraEntity, 1, a);
                if (this.mc.options.anaglyph3d) {
                    if (GameRenderer.anaglyphPass == 0) {
                        glColorMask(false, true, true, true);
                    }
                    else {
                        glColorMask(true, false, false, true);
                    }
                }
                else {
                    glColorMask(true, true, true, true);
                }

                if (visibleWaterChunks > 0) {
                    levelRenderer.renderSameAsLast(1, a);
                }

                glShadeModel(GL_FLAT);
            }
            else {
                levelRenderer.render(cameraEntity, 1, a);
            }

            glDepthMask(true);
            glEnable(GL_CULL_FACE);
            glDisable(GL_BLEND);

            if (this.zoom == 1.0 && cameraEntity instanceof Player) {
                if (this.mc.hitResult != null && !cameraEntity.isUnderLiquid(Material.water)) {
                    final Player player = (Player)cameraEntity;
                    glDisable(GL_ALPHA_TEST);
                    levelRenderer.renderHit(player, this.mc.hitResult, 0, player.inventory.getSelected(), a);
                    levelRenderer.renderHitOutline(player, this.mc.hitResult, 0, player.inventory.getSelected(), a);
                    glEnable(GL_ALPHA_TEST);
                }
            }

            this.renderSnowAndRain(a);
            glDisable(GL_FOG);

            if (this.hovered != null) {
                // Useless - Exists in b1.2 leak and decomp, unsure if this had commented out code
            }

            this.setupFog(0, a);
            glEnable(GL_FOG);
            levelRenderer.renderClouds(a);
            glDisable(GL_FOG);
            this.setupFog(1, a);

            if (this.zoom == 1.0) {
                glClear(GL_DEPTH_BUFFER_BIT);
                this.renderItemInHand(a, i);
            }

            if (!this.mc.options.anaglyph3d) return;
        }
        glColorMask(true, true, true, false);
    }
    
    private void tickRain() {
        float rainLevel = this.mc.level.getRainLevel(1.0f);

        if (!this.mc.options.fancyGraphics) rainLevel /= 2.0f;
        if (rainLevel == 0.0f) return;

        this.random.setSeed(this.tick * 312987231L);
        final Mob player = this.mc.cameraTargetPlayer;
        final Level level = this.mc.level;

        final int x0 = Mth.floor(player.x);
        final int y0 = Mth.floor(player.y);
        final int z0 = Mth.floor(player.z);

        final int r = 10;

        double rainPosX = 0.0;
        double rainPosY = 0.0;
        double rainPosZ = 0.0;
        int rainPosSamples = 0;

        int rainCount = (int)(100.0f * rainLevel * rainLevel);
        for (int i = 0; i < rainCount; ++i) {
            final int x = x0 + this.random.nextInt(r) - this.random.nextInt(r);
            final int z = z0 + this.random.nextInt(r) - this.random.nextInt(r);
            final int y = level.getTopSolidBlock(x, z);
            final int t = level.getTile(x, y - 1, z);
            if (y <= y0 + r && y >= y0 - r && level.getBiomeSource().getBiome(x, z).hasRain()) {
                final float xa = this.random.nextFloat();
                final float za = this.random.nextFloat();
                if (t > 0) {
                    if (Tile.tiles[t].material == Material.lava) {
                        this.mc.particleEngine.add(new SmokeParticle(level, x + xa, y + 0.1f - Tile.tiles[t].yy0, z + za, 0.0, 0.0, 0.0));
                    }
                    else {
                        if (this.random.nextInt(++rainPosSamples) == 0) {
                            rainPosX = x + xa;
                            rainPosY = y + 0.1f - Tile.tiles[t].yy0;
                            rainPosZ = z + za;
                        }
                        this.mc.particleEngine.add(new WaterDropParticle(level, x + xa, y + 0.1f - Tile.tiles[t].yy0, z + za));
                    }
                }
            }
        }

        if (rainPosSamples > 0 && this.random.nextInt(3) < this.rainSoundTime++) {
            this.rainSoundTime = 0;
            if (rainPosY > player.y + 1.0 && level.getTopSolidBlock(Mth.floor(player.x), Mth.floor(player.z)) > Mth.floor(player.y)) {
                this.mc.level.playLocalSound(rainPosX, rainPosY, rainPosZ, "ambient.weather.rain", 0.1f, 0.5f);
            }
            else {
                this.mc.level.playLocalSound(rainPosX, rainPosY, rainPosZ, "ambient.weather.rain", 0.2f, 1.0f);
            }
        }
    }
    
    protected void renderSnowAndRain(final float a) {
        final float rainLevel = this.mc.level.getRainLevel(a);
        if (rainLevel <= 0.0f) return;

        final Mob player = this.mc.cameraTargetPlayer;
        final Level level = this.mc.level;

        final int x0 = Mth.floor(player.x);
        final int y0 = Mth.floor(player.y);
        final int z0 = Mth.floor(player.z);

        final Tesselator t = Tesselator.instance;
        glDisable(GL_CULL_FACE);
        glNormal3f(0.0f, 1.0f, 0.0f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glAlphaFunc(GL_GREATER, 0.01f);

        glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadTexture("/environment/snow.png"));

        final double xo = player.xOld + (player.x - player.xOld) * a;
        final double yo = player.yOld + (player.y - player.yOld) * a;
        final double zo = player.zOld + (player.z - player.zOld) * a;

        final int yMin = Mth.floor(yo);

        int r = 5;
        if (this.mc.options.fancyGraphics) r = 10;

        final Biome[] biomeBlock = level.getBiomeSource().getBiomeBlock(x0 - r, z0 - r, r * 2 + 1, r * 2 + 1);
        {
            int i = 0;
            for (int x = x0 - r; x <= x0 + r; ++x) {
                for (int z = z0 - r; z <= z0 + r; ++z) {
                    if (!biomeBlock[i++].hasSnow()) continue;

                    int floor = level.getTopSolidBlock(x, z);
                    if (floor < 0) floor = 0;

                    int y = floor;
                    if (y < yMin) y = yMin;

                    int yy0 = y0 - r;
                    int yy1 = y0 + r;

                    if (yy0 < floor) yy0 = floor;
                    if (yy1 < floor) yy1 = floor;

                    final float s = 1.0f;

                    if (yy0 != yy1) {
                        this.random.setSeed((x * x * 3121L) + (x * 45238971L) + (z * z * 418711L) + (z * 13761L));

                        final float time = this.tick + a;
                        final float ra = ((this.tick & 0x1FF) + a) / 512.0f;
                        final float uo = this.random.nextFloat() + time * 0.01f * (float) this.random.nextGaussian();
                        final float vo = this.random.nextFloat() + time * (float) this.random.nextGaussian() * 0.001f;

                        final double xd = x + 0.5f - player.x;
                        final double zd = z + 0.5f - player.z;
                        final float dd = Mth.sqrt(xd * xd + zd * zd) / r;

                        t.begin();
                        final float br = level.getBrightness(x, y, z);
                        glColor4f(br, br, br, ((1.0f - dd * dd) * 0.3f + 0.5f) * rainLevel);
                        t.offset(-xo * 1.0, -yo * 1.0, -zo * 1.0);
                        t.vertexUV(x + 0, yy0, z + 0.5, 0.0f * s + uo, yy0 * s / 4.0f + ra * s + vo);
                        t.vertexUV(x + 1, yy0, z + 0.5, 1.0f * s + uo, yy0 * s / 4.0f + ra * s + vo);
                        t.vertexUV(x + 1, yy1, z + 0.5, 1.0f * s + uo, yy1 * s / 4.0f + ra * s + vo);
                        t.vertexUV(x + 0, yy1, z + 0.5, 0.0f * s + uo, yy1 * s / 4.0f + ra * s + vo);
                        t.vertexUV(x + 0.5, yy0, z + 0, 0.0f * s + uo, yy0 * s / 4.0f + ra * s + vo);
                        t.vertexUV(x + 0.5, yy0, z + 1, 1.0f * s + uo, yy0 * s / 4.0f + ra * s + vo);
                        t.vertexUV(x + 0.5, yy1, z + 1, 1.0f * s + uo, yy1 * s / 4.0f + ra * s + vo);
                        t.vertexUV(x + 0.5, yy1, z + 0, 0.0f * s + uo, yy1 * s / 4.0f + ra * s + vo);
                        t.offset(0.0, 0.0, 0.0);
                        t.end();
                    }
                }
            }
        }

        glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadTexture("/environment/rain.png"));
        if (this.mc.options.fancyGraphics) r = 10;

        {
            int i = 0;
            for (int x = x0 - r; x <= x0 + r; ++x) {
                for (int z = z0 - r; z <= z0 + r; ++z) {
                    if (!biomeBlock[i++].hasRain()) continue;

                    final int floor = level.getTopSolidBlock(x, z);

                    int yy0 = y0 - r;
                    int yy1 = y0 + r;

                    if (yy0 < floor) yy0 = floor;
                    if (yy1 < floor) yy1 = floor;

                    final float s = 1.0f;

                    if (yy0 != yy1) {
                        this.random.setSeed(x * x * 3121 + x * 45238971 + z * z * 418711 + z * 13761);
                        final float ra = ((this.tick + x * x * 3121 + x * 45238971 + z * z * 418711 + z * 13761 & 0x1F) + a) / 32.0f * (3.0f + this.random.nextFloat());

                        final double xd = x + 0.5f - player.x;
                        final double zd = z + 0.5f - player.z;
                        final float dd = Mth.sqrt(xd * xd + zd * zd) / r;

                        t.begin();
                        final float br = level.getBrightness(x, 128, z) * 0.85f + 0.15f;
                        glColor4f(br, br, br, ((1.0f - dd * dd) * 0.5f + 0.5f) * rainLevel);
                        t.offset(-xo * 1.0, -yo * 1.0, -zo * 1.0);
                        t.vertexUV(x + 0, yy0, z + 0.5, 0.0f * s, yy0 * s / 4.0f + ra * s);
                        t.vertexUV(x + 1, yy0, z + 0.5, 1.0f * s, yy0 * s / 4.0f + ra * s);
                        t.vertexUV(x + 1, yy1, z + 0.5, 1.0f * s, yy1 * s / 4.0f + ra * s);
                        t.vertexUV(x + 0, yy1, z + 0.5, 0.0f * s, yy1 * s / 4.0f + ra * s);
                        t.vertexUV(x + 0.5, yy0, z + 0, 0.0f * s, yy0 * s / 4.0f + ra * s);
                        t.vertexUV(x + 0.5, yy0, z + 1, 1.0f * s, yy0 * s / 4.0f + ra * s);
                        t.vertexUV(x + 0.5, yy1, z + 1, 1.0f * s, yy1 * s / 4.0f + ra * s);
                        t.vertexUV(x + 0.5, yy1, z + 0, 0.0f * s, yy1 * s / 4.0f + ra * s);
                        t.offset(0.0, 0.0, 0.0);
                        t.end();
                    }
                }
            }
        }
        glEnable(GL_CULL_FACE);
        glDisable(GL_BLEND);
        glAlphaFunc(GL_GREATER, 0.1f);
    }
    
    public void setupGuiScreen() {
        final ScreenSizeCalculator ssc = new ScreenSizeCalculator(this.mc.options, this.mc.width, this.mc.height);

        glClear(GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, ssc.rawWidth, ssc.rawHeight, 0.0, 1000.0, 3000.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(0.0f, 0.0f, -2000.0f);
    }
    
    private void setupClearColor(final float a) {
        final Level level = this.mc.level;
        final Mob player = this.mc.cameraTargetPlayer;

        float whiteness = 1.0f / (4 - this.mc.options.viewDistance);
        whiteness = 1.0f - (float)Math.pow(whiteness, 0.25);

        final Vec3 skyColor = level.getSkyColor(this.mc.cameraTargetPlayer, a);
        final float sr = (float)skyColor.x;
        final float sg = (float)skyColor.y;
        final float sb = (float)skyColor.z;

        final Vec3 fogColor = level.getFogColor(a);
        this.fr = (float)fogColor.x;
        this.fg = (float)fogColor.y;
        this.fb = (float)fogColor.z;

        this.fr += (sr - this.fr) * whiteness;
        this.fg += (sg - this.fg) * whiteness;
        this.fb += (sb - this.fb) * whiteness;

        final float rainLevel = level.getRainLevel(a);
        if (rainLevel > 0.0f) {
            final float ba = 1.0f - rainLevel * 0.5f;
            final float bb = 1.0f - rainLevel * 0.4f;
            this.fr *= ba;
            this.fg *= ba;
            this.fb *= bb;
        }

        final float thunderLevel = level.getThunderLevel(a);
        if (thunderLevel > 0.0f) {
            final float ba = 1.0f - thunderLevel * 0.5f;
            this.fr *= ba;
            this.fg *= ba;
            this.fb *= ba;
        }

        if (this.isInClouds) {
            final Vec3 cc = level.getCloudColor(a);
            this.fr = (float)cc.x;
            this.fg = (float)cc.y;
            this.fb = (float)cc.z;
        }
        else if (player.isUnderLiquid(Material.water)) {
            this.fr = 0.02f;
            this.fg = 0.02f;
            this.fb = 0.2f;
        }
        else if (player.isUnderLiquid(Material.lava)) {
            this.fr = 0.6f;
            this.fg = 0.1f;
            this.fb = 0.0f;
        }
        final float brr = this.fogBrO + (this.fogBr - this.fogBrO) * a;
        this.fr *= brr;
        this.fg *= brr;
        this.fb *= brr;

        if (this.mc.options.anaglyph3d) {
            final float frr = (this.fr * 30.0f + this.fg * 59.0f + this.fb * 11.0f) / 100.0f;
            final float fgg = (this.fr * 30.0f + this.fg * 70.0f) / 100.0f;
            final float fbb = (this.fr * 30.0f + this.fb * 70.0f) / 100.0f;

            this.fr = frr;
            this.fg = fgg;
            this.fb = fbb;
        }

        glClearColor(this.fr, this.fg, this.fb, 0.0f);
    }
    
    private void setupFog(final int i, final float alpha) {
        final Mob player = this.mc.cameraTargetPlayer;

        glFog(GL_FOG_COLOR, this.getBuffer(this.fr, this.fg, this.fb, 1.0f));
        glNormal3f(0.0f, -1.0f, 0.0f);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        if (this.isInClouds) {
            glFogi(GL_FOG_MODE, GL_EXP);
            glFogf(GL_FOG_DENSITY, 0.1f);
            if (this.mc.options.anaglyph3d) {}
        }
        else if (player.isUnderLiquid(Material.water)) {
            glFogi(GL_FOG_MODE, GL_EXP);
            glFogf(GL_FOG_DENSITY, 0.1f);
            if (this.mc.options.anaglyph3d) {}
        }
        else if (player.isUnderLiquid(Material.lava)) {
            glFogi(GL_FOG_MODE, GL_EXP);
            glFogf(GL_FOG_DENSITY, 2.0f);
            if (this.mc.options.anaglyph3d) {}
        }
        else {
            glFogi(GL_FOG_MODE, GL_LINEAR);
            glFogf(GL_FOG_START, this.renderDistance * 0.25f);
            glFogf(GL_FOG_END, this.renderDistance);
            if (i < 0) {
                glFogf(GL_FOG_START, 0.0f);
                glFogf(GL_FOG_END, this.renderDistance * 0.8f);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance) {
                glFogi(NVFogDistance.GL_FOG_DISTANCE_MODE_NV, NVFogDistance.GL_EYE_RADIAL_NV);
            }

            if (this.mc.level.dimension.foggy) {
                glFogf(GL_FOG_START, 0.0f);
            }
        }

        glEnable(GL_COLOR_MATERIAL);
        glColorMaterial(GL_FRONT, GL_AMBIENT);
    }
    
    private FloatBuffer getBuffer(final float a, final float b, final float c, final float d) {
        this.lb.clear();
        this.lb.put(a).put(b).put(c).put(d);
        this.lb.flip();
        return this.lb;
    }

}
