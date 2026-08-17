// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import net.minecraft.world.item.RecordingItem;
import net.minecraft.client.particle.HeartParticle;
import net.minecraft.client.particle.SnowShovelParticle;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.world.item.Item;
import net.minecraft.client.particle.RedDustParticle;
import net.minecraft.client.particle.SplashParticle;
import net.minecraft.client.particle.FootstepParticle;
import net.minecraft.client.particle.LavaParticle;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.ExplodeParticle;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.NoteParticle;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.particle.BubbleParticle;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.player.Player;
import java.util.Collections;
import net.minecraft.client.Lighting;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.client.renderer.tileentity.TileEntityRenderDispatcher;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Mob;

import java.util.Arrays;
import net.minecraft.world.entity.Entity;
import util.Mth;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import java.util.Random;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.ARBOcclusionQuery;
import net.minecraft.client.MemoryTracker;
import java.util.ArrayList;
import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import java.util.List;
import net.minecraft.world.level.LevelListener;

import static org.lwjgl.opengl.GL11.*;

public class LevelRenderer implements LevelListener
{
    public static final int CHUNK_SIZE = 16;
    public static final int MAX_VISIBLE_REBUILDS_PER_FRAME = 3;
    public static final int MAX_INVISIBLE_REBUILDS_PER_FRAME = 1;
    public List<TileEntity> renderableTileEntities;
    private Level level;
    private Textures textures;
    private List<Chunk> dirtyChunks;
    private Chunk[] sortedChunks;
    private Chunk[] chunks;
    private int xChunks;
    private int yChunks;
    private int zChunks;
    private int chunkLists;
    private Minecraft mc;
    private TileRenderer tileRenderer;
    private IntBuffer occlusionCheckIds;
    private boolean occlusionCheck;
    private int ticks;
    private int starList;
    private int skyList;
    private int darkList;
    private int xMinChunk;
    private int yMinChunk;
    private int zMinChunk;
    private int xMaxChunk;
    private int yMaxChunk;
    private int zMaxChunk;
    private int lastViewDistance;
    private int noEntityRenderFrames;
    private int totalEntities;
    private int renderedEntities;
    private int culledEntities;
    int[] toRender;
    IntBuffer resultBuffer;
    private int totalChunks;
    private int offscreenChunks;
    private int occludedChunks;
    private int renderedChunks;
    private int emptyChunks;
    private int chunkFixOffs;
    private List<Chunk> renderChunks;
    private OffsettedRenderList[] renderLists;
    int frame;
    int repeatList;
    double xOld;
    double yOld;
    double zOld;
    public float destroyProgress;
    int cullstep;
    
    public LevelRenderer(final Minecraft mc, final Textures textures) {
        this.renderableTileEntities = new ArrayList();
        this.dirtyChunks = new ArrayList();
        this.occlusionCheck = false;
        this.ticks = 0;
        this.lastViewDistance = -1;
        this.noEntityRenderFrames = 2;
        this.toRender = new int[50000];
        this.resultBuffer = MemoryTracker.createIntBuffer(64);
        this.renderChunks = new ArrayList();
        this.renderLists = new OffsettedRenderList[] { new OffsettedRenderList(), new OffsettedRenderList(), new OffsettedRenderList(), new OffsettedRenderList() };
        this.frame = 0;
        this.repeatList = MemoryTracker.genLists(1);
        this.xOld = -9999.0;
        this.yOld = -9999.0;
        this.zOld = -9999.0;
        this.cullstep = 0;
        this.mc = mc;
        this.textures = textures;
        final int maxChunksWidth = 64;
        this.chunkLists = MemoryTracker.genLists(maxChunksWidth * maxChunksWidth * maxChunksWidth * 3);
        this.occlusionCheck = mc.getOpenGLCapabilities().hasOcclusionChecks();
        if (this.occlusionCheck) {
            this.resultBuffer.clear();
            (this.occlusionCheckIds = MemoryTracker.createIntBuffer(maxChunksWidth * maxChunksWidth * maxChunksWidth)).clear();
            this.occlusionCheckIds.position(0);
            this.occlusionCheckIds.limit(maxChunksWidth * maxChunksWidth * maxChunksWidth);
            ARBOcclusionQuery.glGenQueriesARB(this.occlusionCheckIds);
        }
        this.starList = MemoryTracker.genLists(3);
        glPushMatrix();
        glNewList(this.starList, GL_COMPILE);
        this.renderStars();
        glEndList();
        glPopMatrix();
        final Tesselator instance = Tesselator.instance;
        glNewList(this.skyList = this.starList + 1, GL_COMPILE);
        final int n2 = 64;
        final int n3 = 256 / n2 + 2;
        final float n4 = 16.0f;
        for (int i = -n2 * n3; i <= n2 * n3; i += n2) {
            for (int j = -n2 * n3; j <= n2 * n3; j += n2) {
                instance.begin();
                instance.vertex(i + 0, n4, j + 0);
                instance.vertex(i + n2, n4, j + 0);
                instance.vertex(i + n2, n4, j + n2);
                instance.vertex(i + 0, n4, j + n2);
                instance.end();
            }
        }
        glEndList();
        glNewList(this.darkList = this.starList + 2, GL_COMPILE);
        final float n5 = -16.0f;
        instance.begin();
        for (int k = -n2 * n3; k <= n2 * n3; k += n2) {
            for (int l = -n2 * n3; l <= n2 * n3; l += n2) {
                instance.vertex(k + n2, n5, l + 0);
                instance.vertex(k + 0, n5, l + 0);
                instance.vertex(k + 0, n5, l + n2);
                instance.vertex(k + n2, n5, l + n2);
            }
        }
        instance.end();
        glEndList();
    }
    
    private void renderStars() {
        final Random random = new Random(10842L);
        final Tesselator instance = Tesselator.instance;
        instance.begin();
        for (int i = 0; i < 1500; ++i) {
            final double n = random.nextFloat() * 2.0f - 1.0f;
            final double n2 = random.nextFloat() * 2.0f - 1.0f;
            final double n3 = random.nextFloat() * 2.0f - 1.0f;
            final double n4 = 0.25f + random.nextFloat() * 0.25f;
            final double a = n * n + n2 * n2 + n3 * n3;
            if (a < 1.0 && a > 0.01) {
                final double n5 = 1.0 / Math.sqrt(a);
                final double y = n * n5;
                final double x = n2 * n5;
                final double x2 = n3 * n5;
                final double n6 = y * 100.0;
                final double n7 = x * 100.0;
                final double n8 = x2 * 100.0;
                final double atan2 = Math.atan2(y, x2);
                final double sin = Math.sin(atan2);
                final double cos = Math.cos(atan2);
                final double atan3 = Math.atan2(Math.sqrt(y * y + x2 * x2), x);
                final double sin2 = Math.sin(atan3);
                final double cos2 = Math.cos(atan3);
                final double n9 = random.nextDouble() * Math.PI * 2.0;
                final double sin3 = Math.sin(n9);
                final double cos3 = Math.cos(n9);
                for (int j = 0; j < 4; ++j) {
                    final double n10 = 0.0;
                    final double n11 = ((j & 0x2) - 1) * n4;
                    final double n12 = ((j + 1 & 0x2) - 1) * n4;
                    final double n13 = n10;
                    final double n14 = n11 * cos3 - n12 * sin3;
                    final double n15 = n12 * cos3 + n11 * sin3;
                    final double n16 = n14 * sin2 + n13 * cos2;
                    final double n17 = n13 * sin2 - n14 * cos2;
                    instance.vertex(n6 + (n17 * sin - n15 * cos), n7 + n16, n8 + (n15 * sin + n17 * cos));
                }
            }
        }
        instance.end();
    }
    
    public void setLevel(final Level level) {
        if (this.level != null) {
            this.level.removeListener(this);
        }
        this.xOld = -9999.0;
        this.yOld = -9999.0;
        this.zOld = -9999.0;
        EntityRenderDispatcher.instance.setLevel(level);
        this.level = level;
        this.tileRenderer = new TileRenderer(level);
        if (level != null) {
            level.addListener(this);
            this.allChanged();
        }
    }
    
    public void allChanged() {
        Tile.leaves.setFancy(this.mc.options.fancyGraphics);
        this.lastViewDistance = this.mc.options.viewDistance;
        if (this.chunks != null) {
            for (int i = 0; i < this.chunks.length; ++i) {
                this.chunks[i].delete();
            }
        }
        int n = 64 << 3 - this.lastViewDistance;
        if (n > 400) {
            n = 400;
        }
        this.xChunks = n / 16 + 1;
        this.yChunks = 8;
        this.zChunks = n / 16 + 1;
        this.chunks = new Chunk[this.xChunks * this.yChunks * this.zChunks];
        this.sortedChunks = new Chunk[this.xChunks * this.yChunks * this.zChunks];
        int n2 = 0;
        int n3 = 0;
        this.xMinChunk = 0;
        this.yMinChunk = 0;
        this.zMinChunk = 0;
        this.xMaxChunk = this.xChunks;
        this.yMaxChunk = this.yChunks;
        this.zMaxChunk = this.zChunks;
        for (int j = 0; j < this.dirtyChunks.size(); ++j) {
            ((Chunk)this.dirtyChunks.get(j)).dirty = false;
        }
        this.dirtyChunks.clear();
        this.renderableTileEntities.clear();
        for (int k = 0; k < this.xChunks; ++k) {
            for (int l = 0; l < this.yChunks; ++l) {
                for (int n4 = 0; n4 < this.zChunks; ++n4) {
                    this.chunks[(n4 * this.yChunks + l) * this.xChunks + k] = new Chunk(this.level, this.renderableTileEntities, k * 16, l * 16, n4 * 16, 16, this.chunkLists + n2);
                    if (this.occlusionCheck) {
                        this.chunks[(n4 * this.yChunks + l) * this.xChunks + k].occlusion_id = this.occlusionCheckIds.get(n3);
                    }
                    this.chunks[(n4 * this.yChunks + l) * this.xChunks + k].occlusion_querying = false;
                    this.chunks[(n4 * this.yChunks + l) * this.xChunks + k].occlusion_visible = true;
                    this.chunks[(n4 * this.yChunks + l) * this.xChunks + k].visible = true;
                    this.chunks[(n4 * this.yChunks + l) * this.xChunks + k].id = n3++;
                    this.chunks[(n4 * this.yChunks + l) * this.xChunks + k].setDirty();
                    this.sortedChunks[(n4 * this.yChunks + l) * this.xChunks + k] = this.chunks[(n4 * this.yChunks + l) * this.xChunks + k];
                    this.dirtyChunks.add(this.chunks[(n4 * this.yChunks + l) * this.xChunks + k]);
                    n2 += 3;
                }
            }
        }
        if (this.level != null) {
            final Mob cameraTargetPlayer = this.mc.cameraTargetPlayer;
            if (cameraTargetPlayer != null) {
                this.resortChunks(Mth.floor(cameraTargetPlayer.x), Mth.floor(cameraTargetPlayer.y), Mth.floor(cameraTargetPlayer.z));
                Arrays.sort(this.sortedChunks, new DistanceChunkSorter(cameraTargetPlayer));
            }
        }
        this.noEntityRenderFrames = 2;
    }
    
    public void renderEntities(final Vec3 cam, final Culler culler, final float a) {
        if (this.noEntityRenderFrames > 0) {
            --this.noEntityRenderFrames;
            return;
        }
        TileEntityRenderDispatcher.instance.prepare(this.level, this.textures, this.mc.font, this.mc.cameraTargetPlayer, a);
        EntityRenderDispatcher.instance.prepare(this.level, this.textures, this.mc.font, this.mc.cameraTargetPlayer, this.mc.options, a);
        this.totalEntities = 0;
        this.renderedEntities = 0;
        this.culledEntities = 0;
        final Mob cameraTargetPlayer = this.mc.cameraTargetPlayer;
        EntityRenderDispatcher.xOff = cameraTargetPlayer.xOld + (cameraTargetPlayer.x - cameraTargetPlayer.xOld) * a;
        EntityRenderDispatcher.yOff = cameraTargetPlayer.yOld + (cameraTargetPlayer.y - cameraTargetPlayer.yOld) * a;
        EntityRenderDispatcher.zOff = cameraTargetPlayer.zOld + (cameraTargetPlayer.z - cameraTargetPlayer.zOld) * a;
        TileEntityRenderDispatcher.xOff = cameraTargetPlayer.xOld + (cameraTargetPlayer.x - cameraTargetPlayer.xOld) * a;
        TileEntityRenderDispatcher.yOff = cameraTargetPlayer.yOld + (cameraTargetPlayer.y - cameraTargetPlayer.yOld) * a;
        TileEntityRenderDispatcher.zOff = cameraTargetPlayer.zOld + (cameraTargetPlayer.z - cameraTargetPlayer.zOld) * a;
        final List<Entity> allEntities = this.level.getAllEntities();
        this.totalEntities = allEntities.size();
        for (int i = 0; i < this.level.globalEntities.size(); ++i) {
            final Entity entity = this.level.globalEntities.get(i);
            ++this.renderedEntities;
            if (entity.shouldRender(cam)) {
                EntityRenderDispatcher.instance.render(entity, a);
            }
        }
        for (int j = 0; j < allEntities.size(); ++j) {
            final Entity entity2 = allEntities.get(j);
            if (entity2.shouldRender(cam) && (entity2.noCulling || culler.isVisible(entity2.bb))) {
                if (entity2 != this.mc.cameraTargetPlayer || this.mc.options.thirdPersonView || this.mc.cameraTargetPlayer.isSleeping()) {
                    int floor = Mth.floor(entity2.y);
                    if (floor < 0) {
                        floor = 0;
                    }
                    if (floor >= 128) {
                        floor = 127;
                    }
                    if (this.level.hasChunkAt(Mth.floor(entity2.x), floor, Mth.floor(entity2.z))) {
                        ++this.renderedEntities;
                        EntityRenderDispatcher.instance.render(entity2, a);
                    }
                }
            }
        }
        for (int k = 0; k < this.renderableTileEntities.size(); ++k) {
            TileEntityRenderDispatcher.instance.render((TileEntity)this.renderableTileEntities.get(k), a);
        }
    }
    
    public String gatherStats1() {
        return "C: " + this.renderedChunks + "/" + this.totalChunks + ". F: " + this.offscreenChunks + ", O: " + this.occludedChunks + ", E: " + this.emptyChunks;
    }
    
    public String gatherStats2() {
        return "E: " + this.renderedEntities + "/" + this.totalEntities + ". B: " + this.culledEntities + ", I: " + (this.totalEntities - this.culledEntities - this.renderedEntities);
    }
    
    private void resortChunks(int xc, int yc, int zc) {
        xc -= 8;
        yc -= 8;
        zc -= 8;
        this.xMinChunk = Integer.MAX_VALUE;
        this.yMinChunk = Integer.MAX_VALUE;
        this.zMinChunk = Integer.MAX_VALUE;
        this.xMaxChunk = Integer.MIN_VALUE;
        this.yMaxChunk = Integer.MIN_VALUE;
        this.zMaxChunk = Integer.MIN_VALUE;
        final int n = this.xChunks * 16;
        final int n2 = n / 2;
        for (int i = 0; i < this.xChunks; ++i) {
            final int n3 = i * 16;
            int n4 = n3 + n2 - xc;
            if (n4 < 0) {
                n4 -= n - 1;
            }
            final int x = n3 - n4 / n * n;
            if (x < this.xMinChunk) {
                this.xMinChunk = x;
            }
            if (x > this.xMaxChunk) {
                this.xMaxChunk = x;
            }
            for (int j = 0; j < this.zChunks; ++j) {
                final int n5 = j * 16;
                int n6 = n5 + n2 - zc;
                if (n6 < 0) {
                    n6 -= n - 1;
                }
                final int z = n5 - n6 / n * n;
                if (z < this.zMinChunk) {
                    this.zMinChunk = z;
                }
                if (z > this.zMaxChunk) {
                    this.zMaxChunk = z;
                }
                for (int k = 0; k < this.yChunks; ++k) {
                    final int y = k * 16;
                    if (y < this.yMinChunk) {
                        this.yMinChunk = y;
                    }
                    if (y > this.yMaxChunk) {
                        this.yMaxChunk = y;
                    }
                    final Chunk chunk = this.chunks[(j * this.yChunks + k) * this.xChunks + i];
                    final boolean dirty = chunk.dirty;
                    chunk.setPos(x, y, z);
                    if (!dirty && chunk.dirty) {
                        this.dirtyChunks.add(chunk);
                    }
                }
            }
        }
    }
    
    public int render(final Mob player, final int layer, final double alpha) {
        for (int i = 0; i < 10; ++i) {
            this.chunkFixOffs = (this.chunkFixOffs + 1) % this.chunks.length;
            final Chunk chunk = this.chunks[this.chunkFixOffs];
            if (chunk.dirty && !this.dirtyChunks.contains(chunk)) {
                this.dirtyChunks.add(chunk);
            }
        }
        if (this.mc.options.viewDistance != this.lastViewDistance) {
            this.allChanged();
        }
        if (layer == 0) {
            this.totalChunks = 0;
            this.offscreenChunks = 0;
            this.occludedChunks = 0;
            this.renderedChunks = 0;
            this.emptyChunks = 0;
        }
        final double n = player.xOld + (player.x - player.xOld) * alpha;
        final double n2 = player.yOld + (player.y - player.yOld) * alpha;
        final double n3 = player.zOld + (player.z - player.zOld) * alpha;
        final double n4 = player.x - this.xOld;
        final double n5 = player.y - this.yOld;
        final double n6 = player.z - this.zOld;
        if (n4 * n4 + n5 * n5 + n6 * n6 > 16.0) {
            this.xOld = player.x;
            this.yOld = player.y;
            this.zOld = player.z;
            this.resortChunks(Mth.floor(player.x), Mth.floor(player.y), Mth.floor(player.z));
            Arrays.sort(this.sortedChunks, new DistanceChunkSorter(player));
        }
        Lighting.turnOff();
        final int n7 = 0;
        int n9;
        if (this.occlusionCheck && this.mc.options.advancedOpengl && !this.mc.options.anaglyph3d && layer == 0) {
            final int n8 = 0;
            int j = 16;
            this.checkQueryResults(n8, j);
            for (int k = n8; k < j; ++k) {
                this.sortedChunks[k].occlusion_visible = true;
            }
            n9 = n7 + this.renderChunks(n8, j, layer, alpha);
            do {
                final int n10 = j;
                j *= 2;
                if (j > this.sortedChunks.length) {
                    j = this.sortedChunks.length;
                }
                glDisable(GL_TEXTURE_2D);
                glDisable(GL_LIGHTING);
                glDisable(GL_ALPHA_TEST);
                glDisable(GL_FOG);
                glColorMask(false, false, false, false);
                glDepthMask(false);
                this.checkQueryResults(n10, j);
                glPushMatrix();
                float n11 = 0.0f;
                float n12 = 0.0f;
                float n13 = 0.0f;
                for (int l = n10; l < j; ++l) {
                    if (this.sortedChunks[l].isEmpty()) {
                        this.sortedChunks[l].visible = false;
                    }
                    else {
                        if (!this.sortedChunks[l].visible) {
                            this.sortedChunks[l].occlusion_visible = true;
                        }
                        if (this.sortedChunks[l].visible && !this.sortedChunks[l].occlusion_querying) {
                            final int n14 = (int)(1.0f + Mth.sqrt(this.sortedChunks[l].distanceToSqr(player)) / 128.0f);
                            if (this.ticks % n14 == l % n14) {
                                final Chunk chunk2 = this.sortedChunks[l];
                                final float n15 = (float)(chunk2.xRender - n);
                                final float n16 = (float)(chunk2.yRender - n2);
                                final float n17 = (float)(chunk2.zRender - n3);
                                final float n18 = n15 - n11;
                                final float n19 = n16 - n12;
                                final float n20 = n17 - n13;
                                if (n18 != 0.0f || n19 != 0.0f || n20 != 0.0f) {
                                    glTranslatef(n18, n19, n20);
                                    n11 += n18;
                                    n12 += n19;
                                    n13 += n20;
                                }
                                ARBOcclusionQuery.glBeginQueryARB(35092, this.sortedChunks[l].occlusion_id);
                                this.sortedChunks[l].renderBB();
                                ARBOcclusionQuery.glEndQueryARB(35092);
                                this.sortedChunks[l].occlusion_querying = true;
                            }
                        }
                    }
                }
                glPopMatrix();
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
                glDepthMask(true);
                glEnable(GL_TEXTURE_2D);
                glEnable(GL_ALPHA_TEST);
                glEnable(GL_FOG);
                n9 += this.renderChunks(n10, j, layer, alpha);
            } while (j < this.sortedChunks.length);
        }
        else {
            n9 = n7 + this.renderChunks(0, this.sortedChunks.length, layer, alpha);
        }
        return n9;
    }
    
    private void checkQueryResults(final int from, final int to) {
        for (int i = from; i < to; ++i) {
            if (this.sortedChunks[i].occlusion_querying) {
                this.resultBuffer.clear();
                ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedChunks[i].occlusion_id, 34919, this.resultBuffer);
                if (this.resultBuffer.get(0) != 0) {
                    this.sortedChunks[i].occlusion_querying = false;
                    this.resultBuffer.clear();
                    ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedChunks[i].occlusion_id, 34918, this.resultBuffer);
                    this.sortedChunks[i].occlusion_visible = (this.resultBuffer.get(0) != 0);
                }
            }
        }
    }
    
    private int renderChunks(final int from, final int to, final int layer, final double alpha) {
        this.renderChunks.clear();
        int n = 0;
        for (int i = from; i < to; ++i) {
            if (layer == 0) {
                ++this.totalChunks;
                if (this.sortedChunks[i].empty[layer]) {
                    ++this.emptyChunks;
                }
                else if (!this.sortedChunks[i].visible) {
                    ++this.offscreenChunks;
                }
                else if (this.occlusionCheck && !this.sortedChunks[i].occlusion_visible) {
                    ++this.occludedChunks;
                }
                else {
                    ++this.renderedChunks;
                }
            }
            if (!this.sortedChunks[i].empty[layer] && this.sortedChunks[i].visible && (!this.occlusionCheck || this.sortedChunks[i].occlusion_visible) && this.sortedChunks[i].getList(layer) >= 0) {
                this.renderChunks.add(this.sortedChunks[i]);
                ++n;
            }
        }
        final Mob cameraTargetPlayer = this.mc.cameraTargetPlayer;
        final double xOff = cameraTargetPlayer.xOld + (cameraTargetPlayer.x - cameraTargetPlayer.xOld) * alpha;
        final double yOff = cameraTargetPlayer.yOld + (cameraTargetPlayer.y - cameraTargetPlayer.yOld) * alpha;
        final double zOff = cameraTargetPlayer.zOld + (cameraTargetPlayer.z - cameraTargetPlayer.zOld) * alpha;
        int n2 = 0;
        for (int j = 0; j < this.renderLists.length; ++j) {
            this.renderLists[j].clear();
        }
        for (int k = 0; k < this.renderChunks.size(); ++k) {
            final Chunk chunk = this.renderChunks.get(k);
            int n3 = -1;
            for (int l = 0; l < n2; ++l) {
                if (this.renderLists[l].isAt(chunk.xRender, chunk.yRender, chunk.zRender)) {
                    n3 = l;
                }
            }
            if (n3 < 0) {
                n3 = n2++;
                this.renderLists[n3].init(chunk.xRender, chunk.yRender, chunk.zRender, xOff, yOff, zOff);
            }
            this.renderLists[n3].add(chunk.getList(layer));
        }
        this.renderSameAsLast(layer, alpha);
        return n;
    }
    
    public void renderSameAsLast(final int layer, final double alpha) {
        for (int i = 0; i < this.renderLists.length; ++i) {
            this.renderLists[i].render();
        }
    }
    
    public void tick() {
        ++this.ticks;
    }
    
    public void renderSky(final float alpha) {
        if (this.mc.level.dimension.foggy) {
            return;
        }
        glDisable(GL_TEXTURE_2D);
        final Vec3 skyColor = this.level.getSkyColor(this.mc.cameraTargetPlayer, alpha);
        float n = (float)skyColor.x;
        float n2 = (float)skyColor.y;
        float n3 = (float)skyColor.z;
        if (this.mc.options.anaglyph3d) {
            final float n4 = (n * 30.0f + n2 * 59.0f + n3 * 11.0f) / 100.0f;
            final float n5 = (n * 30.0f + n2 * 70.0f) / 100.0f;
            final float n6 = (n * 30.0f + n3 * 70.0f) / 100.0f;
            n = n4;
            n2 = n5;
            n3 = n6;
        }
        glColor3f(n, n2, n3);
        final Tesselator instance = Tesselator.instance;
        glDepthMask(false);
        glEnable(GL_FOG);
        glColor3f(n, n2, n3);
        glCallList(this.skyList);
        glDisable(GL_FOG);
        glDisable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        Lighting.turnOff();
        final float[] sunriseColor = this.level.dimension.getSunriseColor(this.level.getTimeOfDay(alpha), alpha);
        if (sunriseColor != null) {
            glDisable(GL_TEXTURE_2D);
            glShadeModel(GL_SMOOTH);
            glPushMatrix();
            glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            glRotatef((this.level.getTimeOfDay(alpha) > 0.5f) ? 180.0f : 0.0f, 0.0f, 0.0f, 1.0f);
            float r = sunriseColor[0];
            float g = sunriseColor[1];
            float b = sunriseColor[2];
            if (this.mc.options.anaglyph3d) {
                final float n7 = (r * 30.0f + g * 59.0f + b * 11.0f) / 100.0f;
                final float n8 = (r * 30.0f + g * 70.0f) / 100.0f;
                final float n9 = (r * 30.0f + b * 70.0f) / 100.0f;
                r = n7;
                g = n8;
                b = n9;
            }
            instance.begin(GL_TRIANGLE_FAN);
            instance.color(r, g, b, sunriseColor[3]);
            instance.vertex(0.0, 100.0, 0.0);
            final int n10 = 16;
            instance.color(sunriseColor[0], sunriseColor[1], sunriseColor[2], 0.0f);
            for (int i = 0; i <= n10; ++i) {
                final float n11 = i * Mth.PI * 2.0f / n10;
                final float sin = Mth.sin(n11);
                final float cos = Mth.cos(n11);
                instance.vertex(sin * 120.0f, cos * 120.0f, -cos * 40.0f * sunriseColor[3]);
            }
            instance.end();
            glPopMatrix();
            glShadeModel(GL_FLAT);
        }
        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        glPushMatrix();
        final float n12 = 1.0f - this.level.getRainLevel(alpha);
        final float n13 = 0.0f;
        final float n14 = 0.0f;
        final float n15 = 0.0f;
        glColor4f(1.0f, 1.0f, 1.0f, n12);
        glTranslatef(n13, n14, n15);
        glRotatef(0.0f, 0.0f, 0.0f, 1.0f);
        glRotatef(this.level.getTimeOfDay(alpha) * 360.0f, 1.0f, 0.0f, 0.0f);
        final float n16 = 30.0f;
        glBindTexture(GL_TEXTURE_2D, this.textures.loadTexture("/terrain/sun.png"));
        instance.begin();
        instance.vertexUV(-n16, 100.0, -n16, 0.0, 0.0);
        instance.vertexUV(n16, 100.0, -n16, 1.0, 0.0);
        instance.vertexUV(n16, 100.0, n16, 1.0, 1.0);
        instance.vertexUV(-n16, 100.0, n16, 0.0, 1.0);
        instance.end();
        final float n17 = 20.0f;
        glBindTexture(GL_TEXTURE_2D, this.textures.loadTexture("/terrain/moon.png"));
        instance.begin();
        instance.vertexUV(-n17, -100.0, n17, 1.0, 1.0);
        instance.vertexUV(n17, -100.0, n17, 0.0, 1.0);
        instance.vertexUV(n17, -100.0, -n17, 0.0, 0.0);
        instance.vertexUV(-n17, -100.0, -n17, 1.0, 0.0);
        instance.end();
        glDisable(GL_TEXTURE_2D);
        final float n18 = this.level.getStarBrightness(alpha) * n12;
        if (n18 > 0.0f) {
            glColor4f(n18, n18, n18, n18);
            glCallList(this.starList);
        }
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glDisable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glEnable(GL_FOG);
        glPopMatrix();
        if (this.level.dimension.hasGround()) {
            glColor3f(n * 0.2f + 0.04f, n2 * 0.2f + 0.04f, n3 * 0.6f + 0.1f);
        }
        else {
            glColor3f(n, n2, n3);
        }
        glDisable(GL_TEXTURE_2D);
        glCallList(this.darkList);
        glEnable(GL_TEXTURE_2D);
        glDepthMask(true);
    }
    
    public void renderClouds(final float alpha) {
        if (this.mc.level.dimension.foggy) {
            return;
        }
        if (this.mc.options.fancyGraphics) {
            this.renderAdvancedClouds(alpha);
            return;
        }
        glDisable(GL_CULL_FACE);
        final float n = (float)(this.mc.cameraTargetPlayer.yOld + (this.mc.cameraTargetPlayer.y - this.mc.cameraTargetPlayer.yOld) * alpha);
        final int n2 = 32;
        final int n3 = 256 / n2;
        final Tesselator instance = Tesselator.instance;
        glBindTexture(GL_TEXTURE_2D, this.textures.loadTexture("/environment/clouds.png"));
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        final Vec3 cloudColor = this.level.getCloudColor(alpha);
        float r = (float)cloudColor.x;
        float g = (float)cloudColor.y;
        float b = (float)cloudColor.z;
        if (this.mc.options.anaglyph3d) {
            final float n4 = (r * 30.0f + g * 59.0f + b * 11.0f) / 100.0f;
            final float n5 = (r * 30.0f + g * 70.0f) / 100.0f;
            final float n6 = (r * 30.0f + b * 70.0f) / 100.0f;
            r = n4;
            g = n5;
            b = n6;
        }
        final float n7 = 4.8828125E-4f;
        final double n8 = this.mc.cameraTargetPlayer.xo + (this.mc.cameraTargetPlayer.x - this.mc.cameraTargetPlayer.xo) * alpha + (this.ticks + alpha) * 0.03f;
        final double n9 = this.mc.cameraTargetPlayer.zo + (this.mc.cameraTargetPlayer.z - this.mc.cameraTargetPlayer.zo) * alpha;
        final int floor = Mth.floor(n8 / 2048.0);
        final int floor2 = Mth.floor(n9 / 2048.0);
        final double n10 = n8 - floor * 2048;
        final double n11 = n9 - floor2 * 2048;
        final float n12 = this.level.dimension.getCloudHeight() - n + 0.33f;
        final float n13 = (float)(n10 * n7);
        final float n14 = (float)(n11 * n7);
        instance.begin();
        instance.color(r, g, b, 0.8f);
        for (int i = -n2 * n3; i < n2 * n3; i += n2) {
            for (int j = -n2 * n3; j < n2 * n3; j += n2) {
                instance.vertexUV(i + 0, n12, j + n2, (i + 0) * n7 + n13, (j + n2) * n7 + n14);
                instance.vertexUV(i + n2, n12, j + n2, (i + n2) * n7 + n13, (j + n2) * n7 + n14);
                instance.vertexUV(i + n2, n12, j + 0, (i + n2) * n7 + n13, (j + 0) * n7 + n14);
                instance.vertexUV(i + 0, n12, j + 0, (i + 0) * n7 + n13, (j + 0) * n7 + n14);
            }
        }
        instance.end();
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glDisable(GL_BLEND);
        glEnable(GL_CULL_FACE);
    }
    
    public boolean isInCloud(final double double1, final double double2, final double double3, final float float4) {
        return false;
    }
    
    public void renderAdvancedClouds(final float alpha) {
        glDisable(GL_CULL_FACE);
        final float n = (float)(this.mc.cameraTargetPlayer.yOld + (this.mc.cameraTargetPlayer.y - this.mc.cameraTargetPlayer.yOld) * alpha);
        final Tesselator instance = Tesselator.instance;
        final float n2 = 12.0f;
        final float n3 = 4.0f;
        final double n4 = (this.mc.cameraTargetPlayer.xo + (this.mc.cameraTargetPlayer.x - this.mc.cameraTargetPlayer.xo) * alpha + (this.ticks + alpha) * 0.03f) / n2;
        final double n5 = (this.mc.cameraTargetPlayer.zo + (this.mc.cameraTargetPlayer.z - this.mc.cameraTargetPlayer.zo) * alpha) / n2 + 0.33000001311302185;
        final float n6 = this.level.dimension.getCloudHeight() - n + 0.33f;
        final int floor = Mth.floor(n4 / 2048.0);
        final int floor2 = Mth.floor(n5 / 2048.0);
        final double n7 = n4 - floor * 2048;
        final double n8 = n5 - floor2 * 2048;
        glBindTexture(GL_TEXTURE_2D, this.textures.loadTexture("/environment/clouds.png"));
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        final Vec3 cloudColor = this.level.getCloudColor(alpha);
        float r = (float)cloudColor.x;
        float g = (float)cloudColor.y;
        float b = (float)cloudColor.z;
        if (this.mc.options.anaglyph3d) {
            final float n9 = (r * 30.0f + g * 59.0f + b * 11.0f) / 100.0f;
            final float n10 = (r * 30.0f + g * 70.0f) / 100.0f;
            final float n11 = (r * 30.0f + b * 70.0f) / 100.0f;
            r = n9;
            g = n10;
            b = n11;
        }
        final float n12 = (float)(n7 * 0.0);
        final float n13 = (float)(n8 * 0.0);
        final float n14 = 0.00390625f;
        final float n15 = Mth.floor(n7) * n14;
        final float n16 = Mth.floor(n8) * n14;
        final float n17 = (float)(n7 - Mth.floor(n7));
        final float n18 = (float)(n8 - Mth.floor(n8));
        final int n19 = 8;
        final int n20 = 3;
        final float n21 = 9.765625E-4f;
        glScalef(n2, 1.0f, n2);
        for (int i = 0; i < 2; ++i) {
            if (i == 0) {
                glColorMask(false, false, false, false);
            }
            else if (this.mc.options.anaglyph3d) {
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
            for (int j = -n20 + 1; j <= n20; ++j) {
                for (int k = -n20 + 1; k <= n20; ++k) {
                    instance.begin();
                    final float n22 = (float)(j * n19);
                    final float n23 = (float)(k * n19);
                    final float n24 = n22 - n17;
                    final float n25 = n23 - n18;
                    if (n6 > -n3 - 1.0f) {
                        instance.color(r * 0.7f, g * 0.7f, b * 0.7f, 0.8f);
                        instance.normal(0.0f, -1.0f, 0.0f);
                        instance.vertexUV(n24 + 0.0f, n6 + 0.0f, n25 + n19, (n22 + 0.0f) * n14 + n15, (n23 + n19) * n14 + n16);
                        instance.vertexUV(n24 + n19, n6 + 0.0f, n25 + n19, (n22 + n19) * n14 + n15, (n23 + n19) * n14 + n16);
                        instance.vertexUV(n24 + n19, n6 + 0.0f, n25 + 0.0f, (n22 + n19) * n14 + n15, (n23 + 0.0f) * n14 + n16);
                        instance.vertexUV(n24 + 0.0f, n6 + 0.0f, n25 + 0.0f, (n22 + 0.0f) * n14 + n15, (n23 + 0.0f) * n14 + n16);
                    }
                    if (n6 <= n3 + 1.0f) {
                        instance.color(r, g, b, 0.8f);
                        instance.normal(0.0f, 1.0f, 0.0f);
                        instance.vertexUV(n24 + 0.0f, n6 + n3 - n21, n25 + n19, (n22 + 0.0f) * n14 + n15, (n23 + n19) * n14 + n16);
                        instance.vertexUV(n24 + n19, n6 + n3 - n21, n25 + n19, (n22 + n19) * n14 + n15, (n23 + n19) * n14 + n16);
                        instance.vertexUV(n24 + n19, n6 + n3 - n21, n25 + 0.0f, (n22 + n19) * n14 + n15, (n23 + 0.0f) * n14 + n16);
                        instance.vertexUV(n24 + 0.0f, n6 + n3 - n21, n25 + 0.0f, (n22 + 0.0f) * n14 + n15, (n23 + 0.0f) * n14 + n16);
                    }
                    instance.color(r * 0.9f, g * 0.9f, b * 0.9f, 0.8f);
                    if (j > -1) {
                        instance.normal(-1.0f, 0.0f, 0.0f);
                        for (int l = 0; l < n19; ++l) {
                            instance.vertexUV(n24 + l + 0.0f, n6 + 0.0f, n25 + n19, (n22 + l + 0.5f) * n14 + n15, (n23 + n19) * n14 + n16);
                            instance.vertexUV(n24 + l + 0.0f, n6 + n3, n25 + n19, (n22 + l + 0.5f) * n14 + n15, (n23 + n19) * n14 + n16);
                            instance.vertexUV(n24 + l + 0.0f, n6 + n3, n25 + 0.0f, (n22 + l + 0.5f) * n14 + n15, (n23 + 0.0f) * n14 + n16);
                            instance.vertexUV(n24 + l + 0.0f, n6 + 0.0f, n25 + 0.0f, (n22 + l + 0.5f) * n14 + n15, (n23 + 0.0f) * n14 + n16);
                        }
                    }
                    if (j <= 1) {
                        instance.normal(1.0f, 0.0f, 0.0f);
                        for (int n26 = 0; n26 < n19; ++n26) {
                            instance.vertexUV(n24 + n26 + 1.0f - n21, n6 + 0.0f, n25 + n19, (n22 + n26 + 0.5f) * n14 + n15, (n23 + n19) * n14 + n16);
                            instance.vertexUV(n24 + n26 + 1.0f - n21, n6 + n3, n25 + n19, (n22 + n26 + 0.5f) * n14 + n15, (n23 + n19) * n14 + n16);
                            instance.vertexUV(n24 + n26 + 1.0f - n21, n6 + n3, n25 + 0.0f, (n22 + n26 + 0.5f) * n14 + n15, (n23 + 0.0f) * n14 + n16);
                            instance.vertexUV(n24 + n26 + 1.0f - n21, n6 + 0.0f, n25 + 0.0f, (n22 + n26 + 0.5f) * n14 + n15, (n23 + 0.0f) * n14 + n16);
                        }
                    }
                    instance.color(r * 0.8f, g * 0.8f, b * 0.8f, 0.8f);
                    if (k > -1) {
                        instance.normal(0.0f, 0.0f, -1.0f);
                        for (int n27 = 0; n27 < n19; ++n27) {
                            instance.vertexUV(n24 + 0.0f, n6 + n3, n25 + n27 + 0.0f, (n22 + 0.0f) * n14 + n15, (n23 + n27 + 0.5f) * n14 + n16);
                            instance.vertexUV(n24 + n19, n6 + n3, n25 + n27 + 0.0f, (n22 + n19) * n14 + n15, (n23 + n27 + 0.5f) * n14 + n16);
                            instance.vertexUV(n24 + n19, n6 + 0.0f, n25 + n27 + 0.0f, (n22 + n19) * n14 + n15, (n23 + n27 + 0.5f) * n14 + n16);
                            instance.vertexUV(n24 + 0.0f, n6 + 0.0f, n25 + n27 + 0.0f, (n22 + 0.0f) * n14 + n15, (n23 + n27 + 0.5f) * n14 + n16);
                        }
                    }
                    if (k <= 1) {
                        instance.normal(0.0f, 0.0f, 1.0f);
                        for (int n28 = 0; n28 < n19; ++n28) {
                            instance.vertexUV(n24 + 0.0f, n6 + n3, n25 + n28 + 1.0f - n21, (n22 + 0.0f) * n14 + n15, (n23 + n28 + 0.5f) * n14 + n16);
                            instance.vertexUV(n24 + n19, n6 + n3, n25 + n28 + 1.0f - n21, (n22 + n19) * n14 + n15, (n23 + n28 + 0.5f) * n14 + n16);
                            instance.vertexUV(n24 + n19, n6 + 0.0f, n25 + n28 + 1.0f - n21, (n22 + n19) * n14 + n15, (n23 + n28 + 0.5f) * n14 + n16);
                            instance.vertexUV(n24 + 0.0f, n6 + 0.0f, n25 + n28 + 1.0f - n21, (n22 + 0.0f) * n14 + n15, (n23 + n28 + 0.5f) * n14 + n16);
                        }
                    }
                    instance.end();
                }
            }
        }
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glDisable(GL_BLEND);
        glEnable(GL_CULL_FACE);
    }
    
    public boolean updateDirtyChunks(final Mob player, final boolean force) {
        final boolean slow = false;
        if (slow) {
            Collections.sort(this.dirtyChunks, new DirtyChunkSorter(player));
            final int n = this.dirtyChunks.size() - 1;
            for (int size = this.dirtyChunks.size(), i = 0; i < size; ++i) {
                final Chunk chunk = this.dirtyChunks.get(n - i);
                if (!force) {
                    if (chunk.distanceToSqr(player) > 256.0f) {
                        if (chunk.visible) {
                            if (i >= 3) {
                                return false;
                            }
                        }
                        else if (i >= 1) {
                            return false;
                        }
                    }
                }
                else if (!chunk.visible) {
                    continue;
                }
                chunk.rebuild();
                this.dirtyChunks.remove(chunk);
                chunk.dirty = false;
            }
            return this.dirtyChunks.size() == 0;
        }
        final int n2 = 2;
        final DirtyChunkSorter c = new DirtyChunkSorter(player);
        final Chunk[] array = new Chunk[n2];
        List<Chunk> list = null;
        final int size2 = this.dirtyChunks.size();
        int n3 = 0;
        for (int j = 0; j < size2; ++j) {
            final Chunk chunk2 = this.dirtyChunks.get(j);
            if (!force) {
                if (chunk2.distanceToSqr(player) > 256.0f) {
                    int n4;
                    for (n4 = 0; n4 < n2 && (array[n4] == null || c.compare(array[n4], chunk2) <= 0); ++n4) {}
                    if (--n4 > 0) {
                        int n5 = n4;
                        while (--n5 != 0) {
                            array[n5 - 1] = array[n5];
                        }
                        array[n4] = chunk2;
                    }
                    continue;
                }
            }
            else if (!chunk2.visible) {
                continue;
            }
            if (list == null) {
                list = new ArrayList<>();
            }
            ++n3;
            ((ArrayList<Chunk>)list).add(chunk2);
            this.dirtyChunks.set(j, null);
        }
        if (list != null) {
            if (((ArrayList)list).size() > 1) {
                Collections.sort(list, c);
            }
            for (int k = ((ArrayList)list).size() - 1; k >= 0; --k) {
                final Chunk chunk3 = ((ArrayList<Chunk>)list).get(k);
                chunk3.rebuild();
                chunk3.dirty = false;
            }
        }
        int n6 = 0;
        for (int l = n2 - 1; l >= 0; --l) {
            final Chunk chunk4 = array[l];
            if (chunk4 != null) {
                if (!chunk4.visible && l != n2 - 1) {
                    array[0] = (array[l] = null);
                    break;
                }
                array[l].rebuild();
                array[l].dirty = false;
                ++n6;
            }
        }
        int n7 = 0;
        int n8 = 0;
        while (n7 != this.dirtyChunks.size()) {
            final Chunk chunk5 = this.dirtyChunks.get(n7);
            if (chunk5 != null) {
                int n9 = 0;
                for (int n10 = 0; n10 < n2 && n9 == 0; ++n10) {
                    if (chunk5 == array[n10]) {
                        n9 = 1;
                    }
                }
                if (n9 == 0) {
                    if (n8 != n7) {
                        this.dirtyChunks.set(n8, chunk5);
                    }
                    ++n8;
                }
            }
            ++n7;
        }
        while (--n7 >= n8) {
            this.dirtyChunks.remove(n7);
        }
        return size2 == n3 + n6;
    }
    
    public void renderHit(final Player player, final HitResult h, final int mode, final ItemInstance inventoryItem, final float a) {
        final Tesselator instance = Tesselator.instance;
        glEnable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        glColor4f(1.0f, 1.0f, 1.0f, (Mth.sin(System.currentTimeMillis() / 100.0f) * 0.2f + 0.4f) * 0.5f);
        if (mode == 0) {
            if (this.destroyProgress > 0.0f) {
                glBlendFunc(774, 768);
                glBindTexture(GL_TEXTURE_2D, this.textures.loadTexture("/terrain.png"));
                glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
                glPushMatrix();
                final int tile = this.level.getTile(h.x, h.y, h.z);
                Tile rock = (tile > 0) ? Tile.tiles[tile] : null;
                glDisable(GL_ALPHA_TEST);
                glPolygonOffset(-3.0f, -3.0f);
                glEnable(32823);
                final double n = player.xOld + (player.x - player.xOld) * a;
                final double n2 = player.yOld + (player.y - player.yOld) * a;
                final double n3 = player.zOld + (player.z - player.zOld) * a;
                if (rock == null) {
                    rock = Tile.rock;
                }
                glEnable(GL_ALPHA_TEST);
                instance.begin();
                instance.offset(-n, -n2, -n3);
                instance.noColor();
                this.tileRenderer.tesselateInWorld(rock, h.x, h.y, h.z, 240 + (int)(this.destroyProgress * 10.0f));
                instance.end();
                instance.offset(0.0, 0.0, 0.0);
                glDisable(GL_ALPHA_TEST);
                glPolygonOffset(0.0f, 0.0f);
                glDisable(32823);
                glEnable(GL_ALPHA_TEST);
                glDepthMask(true);
                glPopMatrix();
            }
        }
        else if (inventoryItem != null) {
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            final float n4 = Mth.sin(System.currentTimeMillis() / 100.0f) * 0.2f + 0.8f;
            glColor4f(n4, n4, n4, Mth.sin(System.currentTimeMillis() / 200.0f) * 0.2f + 0.5f);
            glBindTexture(GL_TEXTURE_2D, this.textures.loadTexture("/terrain.png"));
            int x = h.x;
            int y = h.y;
            int z = h.z;
            if (h.f == 0) {
                --y;
            }
            if (h.f == 1) {
                ++y;
            }
            if (h.f == 2) {
                --z;
            }
            if (h.f == 3) {
                ++z;
            }
            if (h.f == 4) {
                --x;
            }
            if (h.f == 5) {
                ++x;
            }
        }
        glDisable(GL_BLEND);
        glDisable(GL_ALPHA_TEST);
    }
    
    public void renderHitOutline(final Player player, final HitResult h, final int mode, final ItemInstance inventoryItem, final float a) {
        if (mode == 0 && h.type == HitResult.Type.TILE) {
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glColor4f(0.0f, 0.0f, 0.0f, 0.4f);
            glLineWidth(2.0f);
            glDisable(GL_TEXTURE_2D);
            glDepthMask(false);
            final float n = 0.002f;
            final int tile = this.level.getTile(h.x, h.y, h.z);
            if (tile > 0) {
                Tile.tiles[tile].updateShape(this.level, h.x, h.y, h.z);
                this.render(Tile.tiles[tile].getTileAABB(this.level, h.x, h.y, h.z).grow(n, n, n).cloneMove(-(player.xOld + (player.x - player.xOld) * a), -(player.yOld + (player.y - player.yOld) * a), -(player.zOld + (player.z - player.zOld) * a)));
            }
            glDepthMask(true);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
        }
    }
    
    private void render(final AABB aabb) {
        final Tesselator instance = Tesselator.instance;
        instance.begin(GL_LINE_STRIP);
        instance.vertex(aabb.x0, aabb.y0, aabb.z0);
        instance.vertex(aabb.x1, aabb.y0, aabb.z0);
        instance.vertex(aabb.x1, aabb.y0, aabb.z1);
        instance.vertex(aabb.x0, aabb.y0, aabb.z1);
        instance.vertex(aabb.x0, aabb.y0, aabb.z0);
        instance.end();
        instance.begin(GL_LINE_STRIP);
        instance.vertex(aabb.x0, aabb.y1, aabb.z0);
        instance.vertex(aabb.x1, aabb.y1, aabb.z0);
        instance.vertex(aabb.x1, aabb.y1, aabb.z1);
        instance.vertex(aabb.x0, aabb.y1, aabb.z1);
        instance.vertex(aabb.x0, aabb.y1, aabb.z0);
        instance.end();
        instance.begin(GL_LINES);
        instance.vertex(aabb.x0, aabb.y0, aabb.z0);
        instance.vertex(aabb.x0, aabb.y1, aabb.z0);
        instance.vertex(aabb.x1, aabb.y0, aabb.z0);
        instance.vertex(aabb.x1, aabb.y1, aabb.z0);
        instance.vertex(aabb.x1, aabb.y0, aabb.z1);
        instance.vertex(aabb.x1, aabb.y1, aabb.z1);
        instance.vertex(aabb.x0, aabb.y0, aabb.z1);
        instance.vertex(aabb.x0, aabb.y1, aabb.z1);
        instance.end();
    }
    
    public void setDirty(final int x0, final int y0, final int z0, final int x1, final int y1, final int z1) {
        final int intFloorDiv = Mth.intFloorDiv(x0, 16);
        final int intFloorDiv2 = Mth.intFloorDiv(y0, 16);
        final int intFloorDiv3 = Mth.intFloorDiv(z0, 16);
        final int intFloorDiv4 = Mth.intFloorDiv(x1, 16);
        final int intFloorDiv5 = Mth.intFloorDiv(y1, 16);
        final int intFloorDiv6 = Mth.intFloorDiv(z1, 16);
        for (int i = intFloorDiv; i <= intFloorDiv4; ++i) {
            int n = i % this.xChunks;
            if (n < 0) {
                n += this.xChunks;
            }
            for (int j = intFloorDiv2; j <= intFloorDiv5; ++j) {
                int n2 = j % this.yChunks;
                if (n2 < 0) {
                    n2 += this.yChunks;
                }
                for (int k = intFloorDiv3; k <= intFloorDiv6; ++k) {
                    int n3 = k % this.zChunks;
                    if (n3 < 0) {
                        n3 += this.zChunks;
                    }
                    final Chunk chunk = this.chunks[(n3 * this.yChunks + n2) * this.xChunks + n];
                    if (!chunk.dirty) {
                        this.dirtyChunks.add(chunk);
                        chunk.setDirty();
                    }
                }
            }
        }
    }
    
    public void tileChanged(final int x, final int y, final int z) {
        this.setDirty(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
    }
    
    public void setTilesDirty(final int x0, final int y0, final int z0, final int x1, final int y1, final int z1) {
        this.setDirty(x0 - 1, y0 - 1, z0 - 1, x1 + 1, y1 + 1, z1 + 1);
    }
    
    public void cull(final Culler culler, final float a) {
        for (int i = 0; i < this.chunks.length; ++i) {
            if (!this.chunks[i].isEmpty() && (!this.chunks[i].visible || (i + this.cullstep & 0xF) == 0x0)) {
                this.chunks[i].cull(culler);
            }
        }
        ++this.cullstep;
    }
    
    public void playStreamingMusic(final String name, final int x, final int y, final int z) {
        if (name != null) {
            this.mc.gui.setNowPlaying("C418 - " + name);
        }
        this.mc.soundEngine.playStreaming(name, (float)x, (float)y, (float)z, 1.0f, 1.0f);
    }
    
    public void playSound(final String name, final double x, final double y, final double z, final float volume, final float pitch) {
        float n = 16.0f;
        if (volume > 1.0f) {
            n *= volume;
        }
        if (this.mc.cameraTargetPlayer.distanceToSqr(x, y, z) < n * n) {
            this.mc.soundEngine.play(name, (float)x, (float)y, (float)z, volume, pitch);
        }
    }
    
    public void addParticle(final String name, final double x, final double y, final double z, final double xa, final double ya, final double za) {
        if (this.mc == null || this.mc.cameraTargetPlayer == null || this.mc.particleEngine == null) {
            return;
        }
        final double n = this.mc.cameraTargetPlayer.x - x;
        final double n2 = this.mc.cameraTargetPlayer.y - y;
        final double n3 = this.mc.cameraTargetPlayer.z - z;
        final double n4 = 16.0;
        if (n * n + n2 * n2 + n3 * n3 > n4 * n4) {
            return;
        }
        if (name.equals("bubble")) {
            this.mc.particleEngine.add(new BubbleParticle(this.level, x, y, z, xa, ya, za));
        }
        else if (name.equals("smoke")) {
            this.mc.particleEngine.add(new SmokeParticle(this.level, x, y, z, xa, ya, za));
        }
        else if (name.equals("note")) {
            this.mc.particleEngine.add(new NoteParticle(this.level, x, y, z, xa, ya, za));
        }
        else if (name.equals("portal")) {
            this.mc.particleEngine.add(new PortalParticle(this.level, x, y, z, xa, ya, za));
        }
        else if (name.equals("explode")) {
            this.mc.particleEngine.add(new ExplodeParticle(this.level, x, y, z, xa, ya, za));
        }
        else if (name.equals("flame")) {
            this.mc.particleEngine.add(new FlameParticle(this.level, x, y, z, xa, ya, za));
        }
        else if (name.equals("lava")) {
            this.mc.particleEngine.add(new LavaParticle(this.level, x, y, z));
        }
        else if (name.equals("footstep")) {
            this.mc.particleEngine.add(new FootstepParticle(this.textures, this.level, x, y, z));
        }
        else if (name.equals("splash")) {
            this.mc.particleEngine.add(new SplashParticle(this.level, x, y, z, xa, ya, za));
        }
        else if (name.equals("largesmoke")) {
            this.mc.particleEngine.add(new SmokeParticle(this.level, x, y, z, xa, ya, za, 2.5f));
        }
        else if (name.equals("reddust")) {
            this.mc.particleEngine.add(new RedDustParticle(this.level, x, y, z, (float)xa, (float)ya, (float)za));
        }
        else if (name.equals("snowballpoof")) {
            this.mc.particleEngine.add(new BreakingItemParticle(this.level, x, y, z, Item.snowBall));
        }
        else if (name.equals("snowshovel")) {
            this.mc.particleEngine.add(new SnowShovelParticle(this.level, x, y, z, xa, ya, za));
        }
        else if (name.equals("slime")) {
            this.mc.particleEngine.add(new BreakingItemParticle(this.level, x, y, z, Item.slimeBall));
        }
        else if (name.equals("heart")) {
            this.mc.particleEngine.add(new HeartParticle(this.level, x, y, z, xa, ya, za));
        }
    }
    
    public void entityAdded(final Entity entity) {
        entity.prepareCustomTextures();
        if (entity.customTextureUrl != null) {
            this.textures.addHttpTexture(entity.customTextureUrl, new MobSkinTextureProcessor());
        }
        if (entity.customTextureUrl2 != null) {
            this.textures.addHttpTexture(entity.customTextureUrl2, new MobSkinTextureProcessor());
        }
    }
    
    public void entityRemoved(final Entity entity) {
        if (entity.customTextureUrl != null) {
            this.textures.removeHttpTexture(entity.customTextureUrl);
        }
        if (entity.customTextureUrl2 != null) {
            this.textures.removeHttpTexture(entity.customTextureUrl2);
        }
    }
    
    public void skyColorChanged() {
        for (int i = 0; i < this.chunks.length; ++i) {
            if (this.chunks[i].skyLit && !this.chunks[i].dirty) {
                this.dirtyChunks.add(this.chunks[i]);
                this.chunks[i].setDirty();
            }
        }
    }
    
    public void tileEntityChanged(final int x, final int y, final int z, final TileEntity te) {
    }
    
    public void clear() {
        MemoryTracker.releaseLists(this.chunkLists);
    }
    
    public void levelEvent(final Player source, final int type, final int x, final int y, final int z, final int data) {
        final Random random = this.level.random;
        switch (type) {
            case 1001: {
                this.level.playLocalSound(x, y, z, "random.click", 1.0f, 1.2f);
                break;
            }
            case 1000: {
                this.level.playLocalSound(x, y, z, "random.click", 1.0f, 1.0f);
                break;
            }
            case 1002: {
                this.level.playLocalSound(x, y, z, "random.bow", 1.0f, 1.2f);
                break;
            }
            case 2000: {
                final int n = data % 3 - 1;
                final int n2 = data / 3 % 3 - 1;
                final double n3 = x + n * 0.6 + 0.5;
                final double n4 = y + 0.5;
                final double n5 = z + n2 * 0.6 + 0.5;
                for (int i = 0; i < 10; ++i) {
                    final double n6 = random.nextDouble() * 0.2 + 0.01;
                    this.addParticle("smoke", n3 + n * 0.01 + (random.nextDouble() - 0.5) * n2 * 0.5, n4 + (random.nextDouble() - 0.5) * 0.5, n5 + n2 * 0.01 + (random.nextDouble() - 0.5) * n * 0.5, n * n6 + random.nextGaussian() * 0.01, -0.03 + random.nextGaussian() * 0.01, n2 * n6 + random.nextGaussian() * 0.01);
                }
                break;
            }
            case 2001: {
                final int n7 = data & 0xFF;
                if (n7 > 0) {
                    final Tile tile = Tile.tiles[n7];
                    this.mc.soundEngine.play(tile.soundType.getBreakSound(), x + 0.5f, y + 0.5f, z + 0.5f, (tile.soundType.getVolume() + 1.0f) / 2.0f, tile.soundType.getPitch() * 0.8f);
                }
                this.mc.particleEngine.destroy(x, y, z, data & 0xFF, data >> 8 & 0xFF);
                break;
            }
            case 1003: {
                if (Math.random() < 0.5) {
                    this.level.playLocalSound(x + 0.5, y + 0.5, z + 0.5, "random.door_open", 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f);
                    break;
                }
                this.level.playLocalSound(x + 0.5, y + 0.5, z + 0.5, "random.door_close", 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f);
                break;
            }
            case 1004: {
                this.level.playLocalSound(x + 0.5f, y + 0.5f, z + 0.5f, "random.fizz", 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f);
                break;
            }
            case 1005: {
                if (Item.items[data] instanceof RecordingItem) {
                    this.level.playStreamingMusic(((RecordingItem)Item.items[data]).recording, x, y, z);
                    break;
                }
                this.level.playStreamingMusic(null, x, y, z);
                break;
            }
        }
    }
}
