// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

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
import org.lwjgl.util.glu.GLU;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.opengl.GL11;
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
    public static boolean anaglyph3d;
    public static int anaglyphPass;
    private Minecraft mc;
    private float renderDistance;
    public ItemInHandRenderer itemInHandRenderer;
    private int tick;
    private Entity hovered;
    private SmoothFloat smoothTurnX;
    private SmoothFloat smoothTurnY;
    private SmoothFloat smoothDistance;
    private SmoothFloat smoothRoation;
    private SmoothFloat smoothTilt;
    private SmoothFloat smoothRoll;
    private float thirdDistance;
    private float thirdDistanceO;
    private float thirdRotation;
    private float thirdRotationO;
    private float thirdTilt;
    private float thirdTiltO;
    private float fovOffset;
    private float fovOffsetO;
    private float cameraRoll;
    private float cameraRollO;
    private boolean isInClouds;
    private double zoom;
    private double zoom_x;
    private double zoom_y;
    private long lastActiveTime;
    private long lastNsTime;
    private Random random;
    private int rainSoundTime;
    volatile int xMod;
    volatile int yMod;
    FloatBuffer lb;
    float fr;
    float fg;
    float fb;
    private float fogBrO;
    private float fogBr;
    
    public GameRenderer(final Minecraft mc) {
        this.renderDistance = 0.0f;
        this.hovered = null;
        this.smoothTurnX = new SmoothFloat();
        this.smoothTurnY = new SmoothFloat();
        this.smoothDistance = new SmoothFloat();
        this.smoothRoation = new SmoothFloat();
        this.smoothTilt = new SmoothFloat();
        this.smoothRoll = new SmoothFloat();
        this.thirdDistance = 4.0f;
        this.thirdDistanceO = 4.0f;
        this.thirdRotation = 0.0f;
        this.thirdRotationO = 0.0f;
        this.thirdTilt = 0.0f;
        this.thirdTiltO = 0.0f;
        this.fovOffset = 0.0f;
        this.fovOffsetO = 0.0f;
        this.cameraRoll = 0.0f;
        this.cameraRollO = 0.0f;
        this.isInClouds = false;
        this.zoom = 1.0;
        this.zoom_x = 0.0;
        this.zoom_y = 0.0;
        this.lastActiveTime = System.currentTimeMillis();
        this.lastNsTime = 0L;
        this.random = new Random();
        this.rainSoundTime = 0;
        this.xMod = 0;
        this.yMod = 0;
        this.lb = MemoryTracker.createFloatBuffer(16);
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
        final float brightness = this.mc.level.getBrightness(Mth.floor(this.mc.cameraTargetPlayer.x), Mth.floor(this.mc.cameraTargetPlayer.y), Mth.floor(this.mc.cameraTargetPlayer.z));
        final float n = (3 - this.mc.options.viewDistance) / 3.0f;
        this.fogBr += (brightness * (1.0f - n) + n - this.fogBr) * 0.1f;
        ++this.tick;
        this.itemInHandRenderer.tick();
        this.tickRain();
    }
    
    public void pick(final float partialTick) {
        if (this.mc.cameraTargetPlayer == null) {
            return;
        }
        if (this.mc.level == null) {
            return;
        }
        final double range = this.mc.gameMode.getPickRange();
        this.mc.hitResult = this.mc.cameraTargetPlayer.pick(range, partialTick);
        double distanceTo = range;
        final Vec3 pos = this.mc.cameraTargetPlayer.getPos(partialTick);
        if (this.mc.hitResult != null) {
            distanceTo = this.mc.hitResult.pos.distanceTo(pos);
        }
        double n;
        if (this.mc.gameMode instanceof CreativeMode) {
            n = 32.0;
        }
        else {
            if (distanceTo > 3.0) {
                distanceTo = 3.0;
            }
            n = distanceTo;
        }
        final Vec3 viewVector = this.mc.cameraTargetPlayer.getViewVector(partialTick);
        final Vec3 add = pos.add(viewVector.x * n, viewVector.y * n, viewVector.z * n);
        this.hovered = null;
        final float n2 = 1.0f;
        final List<Entity> entities = this.mc.level.getEntities(this.mc.cameraTargetPlayer, this.mc.cameraTargetPlayer.bb.expand(viewVector.x * n, viewVector.y * n, viewVector.z * n).grow(n2, n2, n2));
        double n3 = 0.0;
        for (int i = 0; i < entities.size(); ++i) {
            final Entity entity = entities.get(i);
            if (entity.isPickable()) {
                final float pickRadius = entity.getPickRadius();
                final AABB grow = entity.bb.grow(pickRadius, pickRadius, pickRadius);
                final HitResult clip = grow.clip(pos, add);
                if (grow.contains(pos)) {
                    if (0.0 < n3 || n3 == 0.0) {
                        this.hovered = entity;
                        n3 = 0.0;
                    }
                }
                else if (clip != null) {
                    final double distanceTo2 = pos.distanceTo(clip.pos);
                    if (distanceTo2 < n3 || n3 == 0.0) {
                        this.hovered = entity;
                        n3 = distanceTo2;
                    }
                }
            }
        }
        if (this.hovered != null && !(this.mc.gameMode instanceof CreativeMode)) {
            this.mc.hitResult = new HitResult(this.hovered);
        }
    }
    
    private float getFov(final float partialTick) {
        final Mob cameraTargetPlayer = this.mc.cameraTargetPlayer;
        float n = 70.0f;
        if (cameraTargetPlayer.isUnderLiquid(Material.water)) {
            n = 60.0f;
        }
        if (cameraTargetPlayer.health <= 0) {
            n /= (1.0f - 500.0f / (cameraTargetPlayer.deathTime + partialTick + 500.0f)) * 2.0f + 1.0f;
        }
        return n + this.fovOffsetO + (this.fovOffset - this.fovOffsetO) * partialTick;
    }
    
    private void bobHurt(final float partialTick) {
        final Mob cameraTargetPlayer = this.mc.cameraTargetPlayer;
        final float n = cameraTargetPlayer.hurtTime - partialTick;
        if (cameraTargetPlayer.health <= 0) {
            GL11.glRotatef(40.0f - 8000.0f / (cameraTargetPlayer.deathTime + partialTick + 200.0f), 0.0f, 0.0f, 1.0f);
        }
        if (n < 0.0f) {
            return;
        }
        final float n2 = n / cameraTargetPlayer.hurtDuration;
        final float sin = Mth.sin(n2 * n2 * n2 * n2 * 3.1415927f);
        final float hurtDir = cameraTargetPlayer.hurtDir;
        GL11.glRotatef(-hurtDir, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-sin * 14.0f, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(hurtDir, 0.0f, 1.0f, 0.0f);
    }
    
    private void bobView(final float partialTick) {
        if (!(this.mc.cameraTargetPlayer instanceof Player)) {
            return;
        }
        final Player player = (Player)this.mc.cameraTargetPlayer;
        final float n = -(player.walkDist + (player.walkDist - player.walkDistO) * partialTick);
        final float n2 = player.oBob + (player.bob - player.oBob) * partialTick;
        final float n3 = player.oTilt + (player.tilt - player.oTilt) * partialTick;
        GL11.glTranslatef(Mth.sin(n * 3.1415927f) * n2 * 0.5f, -Math.abs(Mth.cos(n * 3.1415927f) * n2), 0.0f);
        GL11.glRotatef(Mth.sin(n * 3.1415927f) * n2 * 3.0f, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(Math.abs(Mth.cos(n * 3.1415927f - 0.2f) * n2) * 5.0f, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(n3, 1.0f, 0.0f, 0.0f);
    }
    
    private void moveCameraToPlayer(final float partialTick) {
        final Mob cameraTargetPlayer = this.mc.cameraTargetPlayer;
        float n = cameraTargetPlayer.heightOffset - 1.62f;
        final double x = cameraTargetPlayer.xo + (cameraTargetPlayer.x - cameraTargetPlayer.xo) * partialTick;
        final double y = cameraTargetPlayer.yo + (cameraTargetPlayer.y - cameraTargetPlayer.yo) * partialTick - n;
        final double z = cameraTargetPlayer.zo + (cameraTargetPlayer.z - cameraTargetPlayer.zo) * partialTick;
        GL11.glRotatef(this.cameraRollO + (this.cameraRoll - this.cameraRollO) * partialTick, 0.0f, 0.0f, 1.0f);
        if (cameraTargetPlayer.isSleeping()) {
            ++n;
            GL11.glTranslatef(0.0f, 0.3f, 0.0f);
            if (!this.mc.options.fixedCamera) {
                if (this.mc.level.getTile(Mth.floor(cameraTargetPlayer.x), Mth.floor(cameraTargetPlayer.y), Mth.floor(cameraTargetPlayer.z)) == Tile.bed.id) {
                    GL11.glRotatef((float)((this.mc.level.getData(Mth.floor(cameraTargetPlayer.x), Mth.floor(cameraTargetPlayer.y), Mth.floor(cameraTargetPlayer.z)) & 0x3) * 90), 0.0f, 1.0f, 0.0f);
                }
                GL11.glRotatef(cameraTargetPlayer.yRotO + (cameraTargetPlayer.yRot - cameraTargetPlayer.yRotO) * partialTick + 180.0f, 0.0f, -1.0f, 0.0f);
                GL11.glRotatef(cameraTargetPlayer.xRotO + (cameraTargetPlayer.xRot - cameraTargetPlayer.xRotO) * partialTick, -1.0f, 0.0f, 0.0f);
            }
        }
        else if (this.mc.options.thirdPersonView) {
            double n2 = this.thirdDistanceO + (this.thirdDistance - this.thirdDistanceO) * partialTick;
            if (this.mc.options.fixedCamera) {
                final float n3 = this.thirdRotationO + (this.thirdRotation - this.thirdRotationO) * partialTick;
                final float n4 = this.thirdTiltO + (this.thirdTilt - this.thirdTiltO) * partialTick;
                GL11.glTranslatef(0.0f, 0.0f, (float)(-n2));
                GL11.glRotatef(n4, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(n3, 0.0f, 1.0f, 0.0f);
            }
            else {
                final float yRot = cameraTargetPlayer.yRot;
                final float xRot = cameraTargetPlayer.xRot;
                final double n5 = -Mth.sin(yRot / 180.0f * 3.1415927f) * Mth.cos(xRot / 180.0f * 3.1415927f) * n2;
                final double n6 = Mth.cos(yRot / 180.0f * 3.1415927f) * Mth.cos(xRot / 180.0f * 3.1415927f) * n2;
                final double n7 = -Mth.sin(xRot / 180.0f * 3.1415927f) * n2;
                for (int i = 0; i < 8; ++i) {
                    final float n8 = (float)((i & 0x1) * 2 - 1);
                    final float n9 = (float)((i >> 1 & 0x1) * 2 - 1);
                    final float n10 = (float)((i >> 2 & 0x1) * 2 - 1);
                    final float n11 = n8 * 0.1f;
                    final float n12 = n9 * 0.1f;
                    final float n13 = n10 * 0.1f;
                    final HitResult clip = this.mc.level.clip(Vec3.newTemp(x + n11, y + n12, z + n13), Vec3.newTemp(x - n5 + n11 + n13, y - n7 + n12, z - n6 + n13));
                    if (clip != null) {
                        final double distanceTo = clip.pos.distanceTo(Vec3.newTemp(x, y, z));
                        if (distanceTo < n2) {
                            n2 = distanceTo;
                        }
                    }
                }
                GL11.glRotatef(cameraTargetPlayer.xRot - xRot, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(cameraTargetPlayer.yRot - yRot, 0.0f, 1.0f, 0.0f);
                GL11.glTranslatef(0.0f, 0.0f, (float)(-n2));
                GL11.glRotatef(yRot - cameraTargetPlayer.yRot, 0.0f, 1.0f, 0.0f);
                GL11.glRotatef(xRot - cameraTargetPlayer.xRot, 1.0f, 0.0f, 0.0f);
            }
        }
        else {
            GL11.glTranslatef(0.0f, 0.0f, -0.1f);
        }
        if (!this.mc.options.fixedCamera) {
            GL11.glRotatef(cameraTargetPlayer.xRotO + (cameraTargetPlayer.xRot - cameraTargetPlayer.xRotO) * partialTick, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(cameraTargetPlayer.yRotO + (cameraTargetPlayer.yRot - cameraTargetPlayer.yRotO) * partialTick + 180.0f, 0.0f, 1.0f, 0.0f);
        }
        GL11.glTranslatef(0.0f, n, 0.0f);
        this.isInClouds = this.mc.levelRenderer.isInCloud(cameraTargetPlayer.xo + (cameraTargetPlayer.x - cameraTargetPlayer.xo) * partialTick, cameraTargetPlayer.yo + (cameraTargetPlayer.y - cameraTargetPlayer.yo) * partialTick - n, cameraTargetPlayer.zo + (cameraTargetPlayer.z - cameraTargetPlayer.zo) * partialTick, partialTick);
    }
    
    private void setupCamera(final float partialTick, final int eye) {
        this.renderDistance = (float)(256 >> this.mc.options.viewDistance);
        GL11.glMatrixMode(GL_PROJECTION);
        GL11.glLoadIdentity();
        final float n = 0.07f;
        if (this.mc.options.anaglyph3d) {
            GL11.glTranslatef(-(eye * 2 - 1) * n, 0.0f, 0.0f);
        }
        if (this.zoom != 1.0) {
            GL11.glTranslatef((float)this.zoom_x, (float)(-this.zoom_y), 0.0f);
            GL11.glScaled(this.zoom, this.zoom, 1.0);
            GLU.gluPerspective(this.getFov(partialTick), this.mc.width / (float)this.mc.height, 0.05f, this.renderDistance * 2.0f);
        }
        else {
            GLU.gluPerspective(this.getFov(partialTick), this.mc.width / (float)this.mc.height, 0.05f, this.renderDistance * 2.0f);
        }
        GL11.glMatrixMode(GL_MODELVIEW);
        GL11.glLoadIdentity();
        if (this.mc.options.anaglyph3d) {
            GL11.glTranslatef((eye * 2 - 1) * 0.1f, 0.0f, 0.0f);
        }
        this.bobHurt(partialTick);
        if (this.mc.options.bobView) {
            this.bobView(partialTick);
        }
        final float n2 = this.mc.player.oPortalTime + (this.mc.player.portalTime - this.mc.player.oPortalTime) * partialTick;
        if (n2 > 0.0f) {
            final float n3 = 5.0f / (n2 * n2 + 5.0f) - n2 * 0.04f;
            final float n4 = n3 * n3;
            GL11.glRotatef((this.tick + partialTick) * 20.0f, 0.0f, 1.0f, 1.0f);
            GL11.glScalef(1.0f / n4, 1.0f, 1.0f);
            GL11.glRotatef(-(this.tick + partialTick) * 20.0f, 0.0f, 1.0f, 1.0f);
        }
        this.moveCameraToPlayer(partialTick);
    }
    
    private void renderItemInHand(final float partialTick, final int eye) {
        GL11.glLoadIdentity();
        if (this.mc.options.anaglyph3d) {
            GL11.glTranslatef((eye * 2 - 1) * 0.1f, 0.0f, 0.0f);
        }
        GL11.glPushMatrix();
        this.bobHurt(partialTick);
        if (this.mc.options.bobView) {
            this.bobView(partialTick);
        }
        if (!this.mc.options.thirdPersonView && !this.mc.cameraTargetPlayer.isSleeping() && !this.mc.options.hideGui) {
            this.itemInHandRenderer.render(partialTick);
        }
        GL11.glPopMatrix();
        if (!this.mc.options.thirdPersonView && !this.mc.cameraTargetPlayer.isSleeping()) {
            this.itemInHandRenderer.renderScreenEffect(partialTick);
            this.bobHurt(partialTick);
        }
        if (this.mc.options.bobView) {
            this.bobView(partialTick);
        }
    }
    
    public void render(final float partialTick) {
        if (!Display.isActive()) {
            if (System.currentTimeMillis() - this.lastActiveTime > 500L) {
                this.mc.pauseGame();
            }
        }
        else {
            this.lastActiveTime = System.currentTimeMillis();
        }
        if (this.mc.mouseGrabbed) {
            this.mc.mouseHandler.poll();
            final float n = this.mc.options.sensitivity * 0.6f + 0.2f;
            final float n2 = n * n * n * 8.0f;
            float newDeltaValue = this.mc.mouseHandler.xd * n2;
            float newDeltaValue2 = this.mc.mouseHandler.yd * n2;
            int n3 = 1;
            if (this.mc.options.invertYMouse) {
                n3 = -1;
            }
            if (this.mc.options.smoothCamera) {
                newDeltaValue = this.smoothTurnX.getNewDeltaValue(newDeltaValue, 0.05f * n2);
                newDeltaValue2 = this.smoothTurnY.getNewDeltaValue(newDeltaValue2, 0.05f * n2);
            }
            this.mc.player.turn(newDeltaValue, newDeltaValue2 * n3);
        }
        if (this.mc.noRender) {
            return;
        }
        GameRenderer.anaglyph3d = this.mc.options.anaglyph3d;
        final ScreenSizeCalculator screenSizeCalculator = new ScreenSizeCalculator(this.mc.options, this.mc.width, this.mc.height);
        final int width = screenSizeCalculator.getWidth();
        final int height = screenSizeCalculator.getHeight();
        final int n4 = Mouse.getX() * width / this.mc.width;
        final int n5 = height - Mouse.getY() * height / this.mc.height - 1;
        int n6 = 200;
        if (this.mc.options.limitFramerate == 1) {
            n6 = 120;
        }
        if (this.mc.options.limitFramerate == 2) {
            n6 = 40;
        }
        if (this.mc.level != null) {
            if (this.mc.options.limitFramerate == 0) {
                this.renderLevel(partialTick, 0L);
            }
            else {
                this.renderLevel(partialTick, this.lastNsTime + 1000000000 / n6);
            }
            if (this.mc.options.limitFramerate == 2) {
                final long millis = (this.lastNsTime + 1000000000 / n6 - System.nanoTime()) / 1000000L;
                if (millis > 0L && millis < 500L) {
                    try {
                        Thread.sleep(millis);
                    }
                    catch (final InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            this.lastNsTime = System.nanoTime();
            if (!this.mc.options.hideGui || this.mc.screen != null) {
                this.mc.gui.render(partialTick, this.mc.screen != null, n4, n5);
            }
        }
        else {
            GL11.glViewport(0, 0, this.mc.width, this.mc.height);
            GL11.glMatrixMode(GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL_MODELVIEW);
            GL11.glLoadIdentity();
            this.setupGuiScreen();
            if (this.mc.options.limitFramerate == 2) {
                long millis2 = (this.lastNsTime + 1000000000 / n6 - System.nanoTime()) / 1000000L;
                if (millis2 < 0L) {
                    millis2 += 10L;
                }
                if (millis2 > 0L && millis2 < 500L) {
                    try {
                        Thread.sleep(millis2);
                    }
                    catch (final InterruptedException ex2) {
                        ex2.printStackTrace();
                    }
                }
            }
            this.lastNsTime = System.nanoTime();
        }
        if (this.mc.screen != null) {
            GL11.glClear(GL_DEPTH_BUFFER_BIT);
            this.mc.screen.render(n4, n5, partialTick);
            if (this.mc.screen != null && this.mc.screen.particles != null) {
                this.mc.screen.particles.render(partialTick);
            }
        }
    }
    
    public void renderLevel(final float partialTick, final long until) {
        GL11.glEnable(GL_CULL_FACE);
        GL11.glEnable(GL_DEPTH_TEST);
        if (this.mc.cameraTargetPlayer == null) {
            this.mc.cameraTargetPlayer = this.mc.player;
        }
        this.pick(partialTick);
        final Mob cameraTargetPlayer = this.mc.cameraTargetPlayer;
        final LevelRenderer levelRenderer = this.mc.levelRenderer;
        final ParticleEngine particleEngine = this.mc.particleEngine;
        final double xOff = cameraTargetPlayer.xOld + (cameraTargetPlayer.x - cameraTargetPlayer.xOld) * partialTick;
        final double yOff = cameraTargetPlayer.yOld + (cameraTargetPlayer.y - cameraTargetPlayer.yOld) * partialTick;
        final double zOff = cameraTargetPlayer.zOld + (cameraTargetPlayer.z - cameraTargetPlayer.zOld) * partialTick;
        final ChunkSource chunkSource = this.mc.level.getChunkSource();
        if (chunkSource instanceof ChunkCache) {
            ((ChunkCache)chunkSource).centerOn(Mth.floor((float)(int)xOff) >> 4, Mth.floor((float)(int)zOff) >> 4);
        }
        for (int i = 0; i < 2; ++i) {
            if (this.mc.options.anaglyph3d) {
                GameRenderer.anaglyphPass = i;
                if (GameRenderer.anaglyphPass == 0) {
                    GL11.glColorMask(false, true, true, false);
                }
                else {
                    GL11.glColorMask(true, false, false, false);
                }
            }
            GL11.glViewport(0, 0, this.mc.width, this.mc.height);
            this.setupClearColor(partialTick);
            GL11.glClear(16640);
            GL11.glEnable(GL_CULL_FACE);
            this.setupCamera(partialTick, i);
            Frustum.getFrustum();
            if (this.mc.options.viewDistance < 2) {
                this.setupFog(-1, partialTick);
                levelRenderer.renderSky(partialTick);
            }
            GL11.glEnable(GL_FOG);
            this.setupFog(1, partialTick);
            if (this.mc.options.ambientOcclusion) {
                GL11.glShadeModel(7425);
            }
            final FrustumCuller frustumCuller = new FrustumCuller();
            frustumCuller.prepare(xOff, yOff, zOff);
            this.mc.levelRenderer.cull(frustumCuller, partialTick);
            if (i == 0) {
                while (!this.mc.levelRenderer.updateDirtyChunks(cameraTargetPlayer, false)) {
                    if (until == 0L) {
                        break;
                    }
                    final long n = until - System.nanoTime();
                    if (n < 0L) {
                        break;
                    }
                    if (n > 1000000000L) {
                        break;
                    }
                }
            }
            this.setupFog(0, partialTick);
            GL11.glEnable(GL_FOG);
            GL11.glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadTexture("/terrain.png"));
            Lighting.turnOff();
            levelRenderer.render(cameraTargetPlayer, 0, partialTick);
            GL11.glShadeModel(GL_FLAT);
            Lighting.turnOn();
            levelRenderer.renderEntities(cameraTargetPlayer.getPos(partialTick), frustumCuller, partialTick);
            particleEngine.renderLit(cameraTargetPlayer, partialTick);
            Lighting.turnOff();
            this.setupFog(0, partialTick);
            particleEngine.render(cameraTargetPlayer, partialTick);
            if (this.mc.hitResult != null && cameraTargetPlayer.isUnderLiquid(Material.water) && cameraTargetPlayer instanceof Player) {
                final Player player = (Player)cameraTargetPlayer;
                GL11.glDisable(GL_ALPHA_TEST);
                levelRenderer.renderHit(player, this.mc.hitResult, 0, player.inventory.getSelected(), partialTick);
                levelRenderer.renderHitOutline(player, this.mc.hitResult, 0, player.inventory.getSelected(), partialTick);
                GL11.glEnable(GL_ALPHA_TEST);
            }
            GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            this.setupFog(0, partialTick);
            GL11.glEnable(GL_BLEND);
            GL11.glDisable(GL_CULL_FACE);
            GL11.glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadTexture("/terrain.png"));
            if (this.mc.options.fancyGraphics) {
                if (this.mc.options.ambientOcclusion) {
                    GL11.glShadeModel(7425);
                }
                GL11.glColorMask(false, false, false, false);
                final int render = levelRenderer.render(cameraTargetPlayer, 1, partialTick);
                if (this.mc.options.anaglyph3d) {
                    if (GameRenderer.anaglyphPass == 0) {
                        GL11.glColorMask(false, true, true, true);
                    }
                    else {
                        GL11.glColorMask(true, false, false, true);
                    }
                }
                else {
                    GL11.glColorMask(true, true, true, true);
                }
                if (render > 0) {
                    levelRenderer.renderSameAsLast(1, partialTick);
                }
                GL11.glShadeModel(GL_FLAT);
            }
            else {
                levelRenderer.render(cameraTargetPlayer, 1, partialTick);
            }
            GL11.glDepthMask(true);
            GL11.glEnable(GL_CULL_FACE);
            GL11.glDisable(GL_BLEND);
            if (this.zoom == 1.0 && cameraTargetPlayer instanceof Player && this.mc.hitResult != null && !cameraTargetPlayer.isUnderLiquid(Material.water)) {
                final Player player2 = (Player)cameraTargetPlayer;
                GL11.glDisable(GL_ALPHA_TEST);
                levelRenderer.renderHit(player2, this.mc.hitResult, 0, player2.inventory.getSelected(), partialTick);
                levelRenderer.renderHitOutline(player2, this.mc.hitResult, 0, player2.inventory.getSelected(), partialTick);
                GL11.glEnable(GL_ALPHA_TEST);
            }
            this.renderSnowAndRain(partialTick);
            GL11.glDisable(GL_FOG);
            if (this.hovered != null) {}
            this.setupFog(0, partialTick);
            GL11.glEnable(GL_FOG);
            levelRenderer.renderClouds(partialTick);
            GL11.glDisable(GL_FOG);
            this.setupFog(1, partialTick);
            if (this.zoom == 1.0) {
                GL11.glClear(GL_DEPTH_BUFFER_BIT);
                this.renderItemInHand(partialTick, i);
            }
            if (!this.mc.options.anaglyph3d) {
                return;
            }
        }
        GL11.glColorMask(true, true, true, false);
    }
    
    private void tickRain() {
        float rainLevel = this.mc.level.getRainLevel(1.0f);
        if (!this.mc.options.fancyGraphics) {
            rainLevel /= 2.0f;
        }
        if (rainLevel == 0.0f) {
            return;
        }
        this.random.setSeed(this.tick * 312987231L);
        final Mob cameraTargetPlayer = this.mc.cameraTargetPlayer;
        final Level level = this.mc.level;
        final int floor = Mth.floor(cameraTargetPlayer.x);
        final int floor2 = Mth.floor(cameraTargetPlayer.y);
        final int floor3 = Mth.floor(cameraTargetPlayer.z);
        final int n = 10;
        double n2 = 0.0;
        double n3 = 0.0;
        double n4 = 0.0;
        int n5 = 0;
        for (int i = 0; i < (int)(100.0f * rainLevel * rainLevel); ++i) {
            final int x = floor + this.random.nextInt(n) - this.random.nextInt(n);
            final int z = floor3 + this.random.nextInt(n) - this.random.nextInt(n);
            final int topSolidBlock = level.getTopSolidBlock(x, z);
            final int tile = level.getTile(x, topSolidBlock - 1, z);
            if (topSolidBlock <= floor2 + n && topSolidBlock >= floor2 - n && level.getBiomeSource().getBiome(x, z).hasRain()) {
                final float nextFloat = this.random.nextFloat();
                final float nextFloat2 = this.random.nextFloat();
                if (tile > 0) {
                    if (Tile.tiles[tile].material == Material.lava) {
                        this.mc.particleEngine.add(new SmokeParticle(level, x + nextFloat, topSolidBlock + 0.1f - Tile.tiles[tile].yy0, z + nextFloat2, 0.0, 0.0, 0.0));
                    }
                    else {
                        if (this.random.nextInt(++n5) == 0) {
                            n2 = x + nextFloat;
                            n3 = topSolidBlock + 0.1f - Tile.tiles[tile].yy0;
                            n4 = z + nextFloat2;
                        }
                        this.mc.particleEngine.add(new WaterDropParticle(level, x + nextFloat, topSolidBlock + 0.1f - Tile.tiles[tile].yy0, z + nextFloat2));
                    }
                }
            }
        }
        if (n5 > 0 && this.random.nextInt(3) < this.rainSoundTime++) {
            this.rainSoundTime = 0;
            if (n3 > cameraTargetPlayer.y + 1.0 && level.getTopSolidBlock(Mth.floor(cameraTargetPlayer.x), Mth.floor(cameraTargetPlayer.z)) > Mth.floor(cameraTargetPlayer.y)) {
                this.mc.level.playLocalSound(n2, n3, n4, "ambient.weather.rain", 0.1f, 0.5f);
            }
            else {
                this.mc.level.playLocalSound(n2, n3, n4, "ambient.weather.rain", 0.2f, 1.0f);
            }
        }
    }
    
    protected void renderSnowAndRain(final float partialTick) {
        final float rainLevel = this.mc.level.getRainLevel(partialTick);
        if (rainLevel <= 0.0f) {
            return;
        }
        final Mob cameraTargetPlayer = this.mc.cameraTargetPlayer;
        final Level level = this.mc.level;
        final int floor = Mth.floor(cameraTargetPlayer.x);
        final int floor2 = Mth.floor(cameraTargetPlayer.y);
        final int floor3 = Mth.floor(cameraTargetPlayer.z);
        final Tesselator instance = Tesselator.instance;
        GL11.glDisable(GL_CULL_FACE);
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(516, 0.01f);
        GL11.glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadTexture("/environment/snow.png"));
        final double n = cameraTargetPlayer.xOld + (cameraTargetPlayer.x - cameraTargetPlayer.xOld) * partialTick;
        final double v = cameraTargetPlayer.yOld + (cameraTargetPlayer.y - cameraTargetPlayer.yOld) * partialTick;
        final double n2 = cameraTargetPlayer.zOld + (cameraTargetPlayer.z - cameraTargetPlayer.zOld) * partialTick;
        final int floor4 = Mth.floor(v);
        int n3 = 5;
        if (this.mc.options.fancyGraphics) {
            n3 = 10;
        }
        final Biome[] biomeBlock = level.getBiomeSource().getBiomeBlock(floor - n3, floor3 - n3, n3 * 2 + 1, n3 * 2 + 1);
        int n4 = 0;
        for (int i = floor - n3; i <= floor + n3; ++i) {
            for (int j = floor3 - n3; j <= floor3 + n3; ++j) {
                if (biomeBlock[n4++].hasSnow()) {
                    int topSolidBlock = level.getTopSolidBlock(i, j);
                    if (topSolidBlock < 0) {
                        topSolidBlock = 0;
                    }
                    int y = topSolidBlock;
                    if (y < floor4) {
                        y = floor4;
                    }
                    int n5 = floor2 - n3;
                    int n6 = floor2 + n3;
                    if (n5 < topSolidBlock) {
                        n5 = topSolidBlock;
                    }
                    if (n6 < topSolidBlock) {
                        n6 = topSolidBlock;
                    }
                    final float n7 = 1.0f;
                    if (n5 != n6) {
                        this.random.setSeed(i * i * 3121 + i * 45238971 + j * j * 418711 + j * 13761);
                        final float n8 = this.tick + partialTick;
                        final float n9 = ((this.tick & 0x1FF) + partialTick) / 512.0f;
                        final float n10 = this.random.nextFloat() + n8 * 0.01f * (float)this.random.nextGaussian();
                        final float n11 = this.random.nextFloat() + n8 * (float)this.random.nextGaussian() * 0.001f;
                        final double n12 = i + 0.5f - cameraTargetPlayer.x;
                        final double n13 = j + 0.5f - cameraTargetPlayer.z;
                        final float n14 = Mth.sqrt(n12 * n12 + n13 * n13) / n3;
                        instance.begin();
                        final float brightness = level.getBrightness(i, y, j);
                        GL11.glColor4f(brightness, brightness, brightness, ((1.0f - n14 * n14) * 0.3f + 0.5f) * rainLevel);
                        instance.offset(-n * 1.0, -v * 1.0, -n2 * 1.0);
                        instance.vertexUV(i + 0, n5, j + 0.5, 0.0f * n7 + n10, n5 * n7 / 4.0f + n9 * n7 + n11);
                        instance.vertexUV(i + 1, n5, j + 0.5, 1.0f * n7 + n10, n5 * n7 / 4.0f + n9 * n7 + n11);
                        instance.vertexUV(i + 1, n6, j + 0.5, 1.0f * n7 + n10, n6 * n7 / 4.0f + n9 * n7 + n11);
                        instance.vertexUV(i + 0, n6, j + 0.5, 0.0f * n7 + n10, n6 * n7 / 4.0f + n9 * n7 + n11);
                        instance.vertexUV(i + 0.5, n5, j + 0, 0.0f * n7 + n10, n5 * n7 / 4.0f + n9 * n7 + n11);
                        instance.vertexUV(i + 0.5, n5, j + 1, 1.0f * n7 + n10, n5 * n7 / 4.0f + n9 * n7 + n11);
                        instance.vertexUV(i + 0.5, n6, j + 1, 1.0f * n7 + n10, n6 * n7 / 4.0f + n9 * n7 + n11);
                        instance.vertexUV(i + 0.5, n6, j + 0, 0.0f * n7 + n10, n6 * n7 / 4.0f + n9 * n7 + n11);
                        instance.offset(0.0, 0.0, 0.0);
                        instance.end();
                    }
                }
            }
        }
        GL11.glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadTexture("/environment/rain.png"));
        if (this.mc.options.fancyGraphics) {
            n3 = 10;
        }
        int n15 = 0;
        for (int k = floor - n3; k <= floor + n3; ++k) {
            for (int l = floor3 - n3; l <= floor3 + n3; ++l) {
                if (biomeBlock[n15++].hasRain()) {
                    final int topSolidBlock2 = level.getTopSolidBlock(k, l);
                    int n16 = floor2 - n3;
                    int n17 = floor2 + n3;
                    if (n16 < topSolidBlock2) {
                        n16 = topSolidBlock2;
                    }
                    if (n17 < topSolidBlock2) {
                        n17 = topSolidBlock2;
                    }
                    final float n18 = 1.0f;
                    if (n16 != n17) {
                        this.random.setSeed(k * k * 3121 + k * 45238971 + l * l * 418711 + l * 13761);
                        final float n19 = ((this.tick + k * k * 3121 + k * 45238971 + l * l * 418711 + l * 13761 & 0x1F) + partialTick) / 32.0f * (3.0f + this.random.nextFloat());
                        final double n20 = k + 0.5f - cameraTargetPlayer.x;
                        final double n21 = l + 0.5f - cameraTargetPlayer.z;
                        final float n22 = Mth.sqrt(n20 * n20 + n21 * n21) / n3;
                        instance.begin();
                        final float n23 = level.getBrightness(k, 128, l) * 0.85f + 0.15f;
                        GL11.glColor4f(n23, n23, n23, ((1.0f - n22 * n22) * 0.5f + 0.5f) * rainLevel);
                        instance.offset(-n * 1.0, -v * 1.0, -n2 * 1.0);
                        instance.vertexUV(k + 0, n16, l + 0.5, 0.0f * n18, n16 * n18 / 4.0f + n19 * n18);
                        instance.vertexUV(k + 1, n16, l + 0.5, 1.0f * n18, n16 * n18 / 4.0f + n19 * n18);
                        instance.vertexUV(k + 1, n17, l + 0.5, 1.0f * n18, n17 * n18 / 4.0f + n19 * n18);
                        instance.vertexUV(k + 0, n17, l + 0.5, 0.0f * n18, n17 * n18 / 4.0f + n19 * n18);
                        instance.vertexUV(k + 0.5, n16, l + 0, 0.0f * n18, n16 * n18 / 4.0f + n19 * n18);
                        instance.vertexUV(k + 0.5, n16, l + 1, 1.0f * n18, n16 * n18 / 4.0f + n19 * n18);
                        instance.vertexUV(k + 0.5, n17, l + 1, 1.0f * n18, n17 * n18 / 4.0f + n19 * n18);
                        instance.vertexUV(k + 0.5, n17, l + 0, 0.0f * n18, n17 * n18 / 4.0f + n19 * n18);
                        instance.offset(0.0, 0.0, 0.0);
                        instance.end();
                    }
                }
            }
        }
        GL11.glEnable(GL_CULL_FACE);
        GL11.glDisable(GL_BLEND);
        GL11.glAlphaFunc(516, 0.1f);
    }
    
    public void setupGuiScreen() {
        final ScreenSizeCalculator screenSizeCalculator = new ScreenSizeCalculator(this.mc.options, this.mc.width, this.mc.height);
        GL11.glClear(GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, screenSizeCalculator.rawWidth, screenSizeCalculator.rawHeight, 0.0, 1000.0, 3000.0);
        GL11.glMatrixMode(GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0f, 0.0f, -2000.0f);
    }
    
    private void setupClearColor(final float partialTick) {
        final Level level = this.mc.level;
        final Mob cameraTargetPlayer = this.mc.cameraTargetPlayer;
        final float n = 1.0f - (float)Math.pow(1.0f / (4 - this.mc.options.viewDistance), 0.25);
        final Vec3 skyColor = level.getSkyColor(this.mc.cameraTargetPlayer, partialTick);
        final float n2 = (float)skyColor.x;
        final float n3 = (float)skyColor.y;
        final float n4 = (float)skyColor.z;
        final Vec3 fogColor = level.getFogColor(partialTick);
        this.fr = (float)fogColor.x;
        this.fg = (float)fogColor.y;
        this.fb = (float)fogColor.z;
        this.fr += (n2 - this.fr) * n;
        this.fg += (n3 - this.fg) * n;
        this.fb += (n4 - this.fb) * n;
        final float rainLevel = level.getRainLevel(partialTick);
        if (rainLevel > 0.0f) {
            final float n5 = 1.0f - rainLevel * 0.5f;
            final float n6 = 1.0f - rainLevel * 0.4f;
            this.fr *= n5;
            this.fg *= n5;
            this.fb *= n6;
        }
        final float thunderLevel = level.getThunderLevel(partialTick);
        if (thunderLevel > 0.0f) {
            final float n7 = 1.0f - thunderLevel * 0.5f;
            this.fr *= n7;
            this.fg *= n7;
            this.fb *= n7;
        }
        if (this.isInClouds) {
            final Vec3 cloudColor = level.getCloudColor(partialTick);
            this.fr = (float)cloudColor.x;
            this.fg = (float)cloudColor.y;
            this.fb = (float)cloudColor.z;
        }
        else if (cameraTargetPlayer.isUnderLiquid(Material.water)) {
            this.fr = 0.02f;
            this.fg = 0.02f;
            this.fb = 0.2f;
        }
        else if (cameraTargetPlayer.isUnderLiquid(Material.lava)) {
            this.fr = 0.6f;
            this.fg = 0.1f;
            this.fb = 0.0f;
        }
        final float n8 = this.fogBrO + (this.fogBr - this.fogBrO) * partialTick;
        this.fr *= n8;
        this.fg *= n8;
        this.fb *= n8;
        if (this.mc.options.anaglyph3d) {
            final float fr = (this.fr * 30.0f + this.fg * 59.0f + this.fb * 11.0f) / 100.0f;
            final float fg = (this.fr * 30.0f + this.fg * 70.0f) / 100.0f;
            final float fb = (this.fr * 30.0f + this.fb * 70.0f) / 100.0f;
            this.fr = fr;
            this.fg = fg;
            this.fb = fb;
        }
        GL11.glClearColor(this.fr, this.fg, this.fb, 0.0f);
    }
    
    private void setupFog(final int i, final float alpha) {
        final Mob cameraTargetPlayer = this.mc.cameraTargetPlayer;
        GL11.glFog(2918, this.getBuffer(this.fr, this.fg, this.fb, 1.0f));
        GL11.glNormal3f(0.0f, -1.0f, 0.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.isInClouds) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, 0.1f);
            if (this.mc.options.anaglyph3d) {}
        }
        else if (cameraTargetPlayer.isUnderLiquid(Material.water)) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, 0.1f);
            if (this.mc.options.anaglyph3d) {}
        }
        else if (cameraTargetPlayer.isUnderLiquid(Material.lava)) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, 2.0f);
            if (this.mc.options.anaglyph3d) {}
        }
        else {
            GL11.glFogi(2917, 9729);
            GL11.glFogf(2915, this.renderDistance * 0.25f);
            GL11.glFogf(2916, this.renderDistance);
            if (i < 0) {
                GL11.glFogf(2915, 0.0f);
                GL11.glFogf(2916, this.renderDistance * 0.8f);
            }
            if (GLContext.getCapabilities().GL_NV_fog_distance) {
                GL11.glFogi(34138, 34139);
            }
            if (this.mc.level.dimension.foggy) {
                GL11.glFogf(2915, 0.0f);
            }
        }
        GL11.glEnable(GL_COLOR_MATERIAL);
        GL11.glColorMaterial(1028, 4608);
    }
    
    private FloatBuffer getBuffer(final float a, final float b, final float c, final float d) {
        this.lb.clear();
        this.lb.put(a).put(b).put(c).put(d);
        this.lb.flip();
        return this.lb;
    }
    
    static {
        GameRenderer.anaglyph3d = false;
    }
}
