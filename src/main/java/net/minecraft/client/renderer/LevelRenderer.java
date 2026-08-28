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
import net.minecraft.world.level.LevelEvent;
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
    public static final int MAX_VISIBLE_REBUILDS_PER_FRAME = 3; // TODO Useless - find out where these constants are actually used, they're in b1.2 but finding usage is hard
    public static final int MAX_INVISIBLE_REBUILDS_PER_FRAME = 1;
    public List<TileEntity> renderableTileEntities = new ArrayList<>();
    private Level level;
    private Textures textures;
    private List<Chunk> dirtyChunks = new ArrayList<>();
    private Chunk[] sortedChunks;
    private Chunk[] chunks;
    private int xChunks, yChunks, zChunks;
    private int chunkLists;
    private Minecraft mc;
    private TileRenderer tileRenderer;
    private IntBuffer occlusionCheckIds;
    private boolean occlusionCheck = false;
    private int ticks = 0;
    private int starList, skyList, darkList;
    private int xMinChunk, yMinChunk, zMinChunk;
    private int xMaxChunk, yMaxChunk, zMaxChunk;
    private int lastViewDistance = -1;
    private int noEntityRenderFrames = 2;
    private int totalEntities;
    private int renderedEntities;
    private int culledEntities;
    int[] toRender = new int[50000];
    IntBuffer resultBuffer = MemoryTracker.createIntBuffer(64);
    private int totalChunks;
    private int offscreenChunks;
    private int occludedChunks;
    private int renderedChunks;
    private int emptyChunks;
    private int chunkFixOffs;
    private List<Chunk> renderChunks = new ArrayList<>();
    private OffsettedRenderList[] renderLists = new OffsettedRenderList[] { new OffsettedRenderList(), new OffsettedRenderList(), new OffsettedRenderList(), new OffsettedRenderList() };
    int frame = 0;
    int repeatList = MemoryTracker.genLists(1);
    double xOld = -9999.0, yOld = -9999.0, zOld = -9999.0;
    public float destroyProgress;
    int cullstep = 0;
    
    public LevelRenderer(final Minecraft mc, final Textures textures) {
        this.mc = mc;
        this.textures = textures;

        final int maxChunksWidth = 64;
        this.chunkLists = MemoryTracker.genLists(maxChunksWidth * maxChunksWidth * maxChunksWidth * 3);

        this.occlusionCheck = mc.getOpenGLCapabilities().hasOcclusionChecks();
        if (this.occlusionCheck) {
            this.resultBuffer.clear();
            this.occlusionCheckIds = MemoryTracker.createIntBuffer(maxChunksWidth * maxChunksWidth * maxChunksWidth);
            this.occlusionCheckIds.clear();
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

        final Tesselator t = Tesselator.instance;
        this.skyList = this.starList + 1;
        glNewList(this.skyList, GL_COMPILE);
        float yy;
        final int s = 64;
        final int d = 256 / s + 2;
        yy = 16.0f;
        for (int xx = -s * d; xx <= s * d; xx += s) {
            for (int zz = -s * d; zz <= s * d; zz += s) {
                t.begin();
                t.vertex(xx + 0, yy, zz + 0);
                t.vertex(xx + s, yy, zz + 0);
                t.vertex(xx + s, yy, zz + s);
                t.vertex(xx + 0, yy, zz + s);
                t.end();
            }
        }
        glEndList();

        this.darkList = this.starList + 2;
        glNewList(this.darkList, GL_COMPILE);
        yy = -16.0f;
        t.begin();
        for (int xx = -s * d; xx <= s * d; xx += s) {
            for (int zz = -s * d; zz <= s * d; zz += s) {
                t.vertex(xx + s, yy, zz + 0);
                t.vertex(xx + 0, yy, zz + 0);
                t.vertex(xx + 0, yy, zz + s);
                t.vertex(xx + s, yy, zz + s);
            }
        }
        t.end();
        glEndList();
    }
    
    private void renderStars() {
        final Random random = new Random(10842L);
        final Tesselator t = Tesselator.instance;
        t.begin();
        for (int i = 0; i < 1500; ++i) {
            double x = random.nextFloat() * 2.0f - 1.0f;
            double y = random.nextFloat() * 2.0f - 1.0f;
            double z = random.nextFloat() * 2.0f - 1.0f;
            double ss = 0.25f + random.nextFloat() * 0.25f;
            double d = x * x + y * y + z * z;
            if (d < 1.0 && d > 0.01) {
                d = 1.0 / Math.sqrt(d);
                x *= d;
                y *= d;
                z *= d;
                final double xp = x * 100.0;
                final double yp = y * 100.0;
                final double zp = z * 100.0;

                final double yRot = Math.atan2(x, z);
                final double ySin = Math.sin(yRot);
                final double yCos = Math.cos(yRot);

                final double xRot = Math.atan2(Math.sqrt(x * x + z * z), y);
                final double xSin = Math.sin(xRot);
                final double xCos = Math.cos(xRot);

                final double zRot = random.nextDouble() * Math.PI * 2.0;
                final double zSin = Math.sin(zRot);
                final double zCos = Math.cos(zRot);

                for (int c = 0; c < 4; ++c) {
                    final double ___xo = 0.0;
                    final double ___yo = ((c & 0x2) - 1) * ss;
                    final double ___zo = ((c + 1 & 0x2) - 1) * ss;

                    final double __xo = ___xo;
                    final double __yo = ___yo * zCos - ___zo * zSin;
                    final double __zo = ___zo * zCos + ___yo * zSin;

                    final double _zo = __zo;
                    final double _yo = __yo * xSin + __xo * xCos;
                    final double _xo = __xo * xSin - __yo * xCos;

                    final double xo = _xo * ySin - _zo * yCos;
                    final double yo = _yo;
                    final double zo = _zo * ySin + _xo * yCos;

                    t.vertex(xp + xo, yp + yo, zp + zo);
                }
            }
        }
        t.end();
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

        int dist = 64 << 3 - this.lastViewDistance;
        if (dist > 400) dist = 400;
        this.xChunks = dist / CHUNK_SIZE + 1;
        this.yChunks = Level.MAX_HEIGHT / CHUNK_SIZE;
        this.zChunks = dist / CHUNK_SIZE + 1;

        this.chunks = new Chunk[this.xChunks * this.yChunks * this.zChunks];
        this.sortedChunks = new Chunk[this.xChunks * this.yChunks * this.zChunks];
        int id = 0;
        int count = 0;

        this.xMinChunk = 0;
        this.yMinChunk = 0;
        this.zMinChunk = 0;
        this.xMaxChunk = this.xChunks;
        this.yMaxChunk = this.yChunks;
        this.zMaxChunk = this.zChunks;

        for (int i = 0; i < this.dirtyChunks.size(); ++i) {
            this.dirtyChunks.get(i).dirty = false;
        }
        this.dirtyChunks.clear();
        this.renderableTileEntities.clear();

        for (int x = 0; x < this.xChunks; ++x) {
            for (int y = 0; y < this.yChunks; ++y) {
                for (int z = 0; z < this.zChunks; ++z) {
                    this.chunks[(z * this.yChunks + y) * this.xChunks + x] = new Chunk(this.level, this.renderableTileEntities, x * CHUNK_SIZE, y * CHUNK_SIZE, z * CHUNK_SIZE, CHUNK_SIZE, this.chunkLists + id);
                    if (this.occlusionCheck) {
                        this.chunks[(z * this.yChunks + y) * this.xChunks + x].occlusion_id = this.occlusionCheckIds.get(count);
                    }
                    this.chunks[(z * this.yChunks + y) * this.xChunks + x].occlusion_querying = false;
                    this.chunks[(z * this.yChunks + y) * this.xChunks + x].occlusion_visible = true;
                    this.chunks[(z * this.yChunks + y) * this.xChunks + x].visible = true;
                    this.chunks[(z * this.yChunks + y) * this.xChunks + x].id = count++;
                    this.chunks[(z * this.yChunks + y) * this.xChunks + x].setDirty();
                    this.sortedChunks[(z * this.yChunks + y) * this.xChunks + x] = this.chunks[(z * this.yChunks + y) * this.xChunks + x];
                    this.dirtyChunks.add(this.chunks[(z * this.yChunks + y) * this.xChunks + x]);
                    id += 3;
                }
            }
        }

        if (this.level != null) {
            final Mob player = this.mc.cameraTargetPlayer;
            if (player != null) {
                this.resortChunks(Mth.floor(player.x), Mth.floor(player.y), Mth.floor(player.z));
                Arrays.sort(this.sortedChunks, new DistanceChunkSorter(player));
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

        final Entity player = this.mc.cameraTargetPlayer;

        EntityRenderDispatcher.xOff = player.xOld + (player.x - player.xOld) * a;
        EntityRenderDispatcher.yOff = player.yOld + (player.y - player.yOld) * a;
        EntityRenderDispatcher.zOff = player.zOld + (player.z - player.zOld) * a;
        TileEntityRenderDispatcher.xOff = player.xOld + (player.x - player.xOld) * a;
        TileEntityRenderDispatcher.yOff = player.yOld + (player.y - player.yOld) * a;
        TileEntityRenderDispatcher.zOff = player.zOld + (player.z - player.zOld) * a;

        final List<Entity> entities = this.level.getAllEntities();
        this.totalEntities = entities.size();

        for (int i = 0; i < this.level.globalEntities.size(); ++i) {
            final Entity entity = this.level.globalEntities.get(i);
            ++this.renderedEntities;
            if (entity.shouldRender(cam)) EntityRenderDispatcher.instance.render(entity, a);
        }

        for (int i = 0; i < entities.size(); ++i) {
            final Entity entity = entities.get(i);
            if (entity.shouldRender(cam) && (entity.noCulling || culler.isVisible(entity.bb))) {
                if (entity == this.mc.cameraTargetPlayer && !this.mc.options.thirdPersonView && !this.mc.cameraTargetPlayer.isSleeping()) continue;

                int floor = Mth.floor(entity.y);
                if (floor < 0) floor = 0;
                if (floor >= 128) floor = 127;

                if (!this.level.hasChunkAt(Mth.floor(entity.x), floor, Mth.floor(entity.z))) continue;

                ++this.renderedEntities;
                EntityRenderDispatcher.instance.render(entity, a);
            }
        }

        for (int i = 0; i < this.renderableTileEntities.size(); ++i) {
            TileEntityRenderDispatcher.instance.render(this.renderableTileEntities.get(i), a);
        }
    }
    
    public String gatherStats1() {
        return "C: " + this.renderedChunks + "/" + this.totalChunks + ". F: " + this.offscreenChunks + ", O: " + this.occludedChunks + ", E: " + this.emptyChunks;
    }
    
    public String gatherStats2() {
        return "E: " + this.renderedEntities + "/" + this.totalEntities + ". B: " + this.culledEntities + ", I: " + (this.totalEntities - this.culledEntities - this.renderedEntities);
    }
    
    private void resortChunks(int xc, int yc, int zc) {
        xc -= CHUNK_SIZE / 2;
        yc -= CHUNK_SIZE / 2;
        zc -= CHUNK_SIZE / 2;
        this.xMinChunk = Integer.MAX_VALUE;
        this.yMinChunk = Integer.MAX_VALUE;
        this.zMinChunk = Integer.MAX_VALUE;
        this.xMaxChunk = Integer.MIN_VALUE;
        this.yMaxChunk = Integer.MIN_VALUE;
        this.zMaxChunk = Integer.MIN_VALUE;

        final int s2 = this.xChunks * CHUNK_SIZE;
        final int s1 = s2 / 2;

        for (int x = 0; x < this.xChunks; ++x) {
            int xx = x * CHUNK_SIZE;

            int xOff = xx + s1 - xc;
            if (xOff < 0) xOff -= s2 - 1;
            xOff /= s2;
            xx = xx - xOff * s2;

            if (xx < this.xMinChunk) this.xMinChunk = xx;
            if (xx > this.xMaxChunk) this.xMaxChunk = xx;

            for (int z = 0; z < this.zChunks; ++z) {
                int zz = z * CHUNK_SIZE;
                int zOff = zz + s1 - zc;
                if (zOff < 0) zOff -= s2 - 1;
                zOff /= s2;
                zz = zz - zOff * s2;

                if (zz < this.zMinChunk) this.zMinChunk = zz;
                if (zz > this.zMaxChunk) this.zMaxChunk = zz;

                for (int y = 0; y < this.yChunks; ++y) {
                    final int yy = y * CHUNK_SIZE;
                    if (yy < this.yMinChunk) this.yMinChunk = yy;
                    if (yy > this.yMaxChunk) this.yMaxChunk = yy;

                    final Chunk chunk = this.chunks[(z * this.yChunks + y) * this.xChunks + x];
                    final boolean wasDirty = chunk.dirty;
                    chunk.setPos(xx, yy, zz);
                    if (!wasDirty && chunk.dirty) {
                        this.dirtyChunks.add(chunk);
                    }
                }
            }
        }
    }
    
    public int render(final Mob player, final int layer, final double alpha) {
        for (int i = 0; i < 10; ++i) {
            this.chunkFixOffs = (this.chunkFixOffs + 1) % this.chunks.length;
            final Chunk c = this.chunks[this.chunkFixOffs];
            if (c.dirty && !this.dirtyChunks.contains(c)) {
                this.dirtyChunks.add(c);
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

        final double xOff = player.xOld + (player.x - player.xOld) * alpha;
        final double yOff = player.yOld + (player.y - player.yOld) * alpha;
        final double zOff = player.zOld + (player.z - player.zOld) * alpha;
        final double xd = player.x - this.xOld;
        final double yd = player.y - this.yOld;
        final double zd = player.z - this.zOld;
        if (xd * xd + yd * yd + zd * zd > 4 * 4) {
            this.xOld = player.x;
            this.yOld = player.y;
            this.zOld = player.z;

            this.resortChunks(Mth.floor(player.x), Mth.floor(player.y), Mth.floor(player.z));
            Arrays.sort(this.sortedChunks, new DistanceChunkSorter(player));
        }

        Lighting.turnOff();
        int count = 0;
        if (this.occlusionCheck && this.mc.options.advancedOpengl && !this.mc.options.anaglyph3d && layer == 0) {
            int from = 0;
            int to = 16;
            this.checkQueryResults(from, to);

            for (int i = from; i < to; ++i) {
                this.sortedChunks[i].occlusion_visible = true;
            }

            count += this.renderChunks(from, to, layer, alpha);

            do {
                from = to;
                to *= 2;
                if (to > this.sortedChunks.length) {
                    to = this.sortedChunks.length;
                }

                glDisable(GL_TEXTURE_2D);
                glDisable(GL_LIGHTING);
                glDisable(GL_ALPHA_TEST);
                glDisable(GL_FOG);
                glColorMask(false, false, false, false);
                glDepthMask(false);
                this.checkQueryResults(from, to);
                glPushMatrix();
                float xo = 0.0f;
                float yo = 0.0f;
                float zo = 0.0f;

                for (int i = from; i < to; ++i) {
                    if (this.sortedChunks[i].isEmpty()) {
                        this.sortedChunks[i].visible = false;
                    }
                    else {
                        if (!this.sortedChunks[i].visible) {
                            this.sortedChunks[i].occlusion_visible = true;
                        }

                        if (this.sortedChunks[i].visible && !this.sortedChunks[i].occlusion_querying) {
                            float dist = Mth.sqrt(this.sortedChunks[i].distanceToSqr(player));
                            final int frequency = (int)(1.0f + dist / 128.0f);
                            if (this.ticks % frequency == i % frequency) {
                                final Chunk chunk = this.sortedChunks[i];
                                final float xt = (float)(chunk.xRender - xOff);
                                final float yt = (float)(chunk.yRender - yOff);
                                final float zt = (float)(chunk.zRender - zOff);
                                final float xdd = xt - xo;
                                final float ydd = yt - yo;
                                final float zdd = zt - zo;
                                if (xdd != 0.0f || ydd != 0.0f || zdd != 0.0f) {
                                    glTranslatef(xdd, ydd, zdd);
                                    xo += xdd;
                                    yo += ydd;
                                    zo += zdd;
                                }

                                ARBOcclusionQuery.glBeginQueryARB(ARBOcclusionQuery.GL_SAMPLES_PASSED_ARB, this.sortedChunks[i].occlusion_id);
                                this.sortedChunks[i].renderBB();
                                ARBOcclusionQuery.glEndQueryARB(ARBOcclusionQuery.GL_SAMPLES_PASSED_ARB);
                                this.sortedChunks[i].occlusion_querying = true;
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
                count += this.renderChunks(from, to, layer, alpha);
            } while (to < this.sortedChunks.length);
        }
        else {
            count += this.renderChunks(0, this.sortedChunks.length, layer, alpha);
        }

        return count;
    }
    
    private void checkQueryResults(final int from, final int to) {
        for (int i = from; i < to; ++i) {
            if (this.sortedChunks[i].occlusion_querying) {
                this.resultBuffer.clear();
                ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedChunks[i].occlusion_id, ARBOcclusionQuery.GL_QUERY_RESULT_AVAILABLE_ARB, this.resultBuffer);
                if (this.resultBuffer.get(0) != 0) {
                    this.sortedChunks[i].occlusion_querying = false;
                    this.resultBuffer.clear();
                    ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedChunks[i].occlusion_id, ARBOcclusionQuery.GL_QUERY_RESULT_ARB, this.resultBuffer);
                    this.sortedChunks[i].occlusion_visible = (this.resultBuffer.get(0) != 0);
                }
            }
        }
    }
    
    private int renderChunks(final int from, final int to, final int layer, final double alpha) {
        this.renderChunks.clear();
        int count = 0;

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

            if (!this.sortedChunks[i].empty[layer] && this.sortedChunks[i].visible && (!this.occlusionCheck || this.sortedChunks[i].occlusion_visible)) {
                int list = this.sortedChunks[i].getList(layer);
                if (list >= 0) {
                    this.renderChunks.add(this.sortedChunks[i]);
                    ++count;
                }
            }
        }

        final Mob player = this.mc.cameraTargetPlayer;
        final double xOff = player.xOld + (player.x - player.xOld) * alpha;
        final double yOff = player.yOld + (player.y - player.yOld) * alpha;
        final double zOff = player.zOld + (player.z - player.zOld) * alpha;
        int lists = 0;

        for (int l = 0; l < this.renderLists.length; ++l) {
            this.renderLists[l].clear();
        }

        for (int i = 0; i < this.renderChunks.size(); ++i) {
            final Chunk chunk = this.renderChunks.get(i);
            int list = -1;

            for (int l = 0; l < lists; ++l) {
                if (this.renderLists[l].isAt(chunk.xRender, chunk.yRender, chunk.zRender)) {
                    list = l;
                }
            }

            if (list < 0) {
                list = lists++;
                this.renderLists[list].init(chunk.xRender, chunk.yRender, chunk.zRender, xOff, yOff, zOff);
            }

            this.renderLists[list].add(chunk.getList(layer));
        }

        this.renderSameAsLast(layer, alpha);
        return count;
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
        if (this.mc.level.dimension.foggy) return;

        glDisable(GL_TEXTURE_2D);

        final Vec3 sc = this.level.getSkyColor(this.mc.cameraTargetPlayer, alpha);
        float sr = (float) sc.x;
        float sg = (float) sc.y;
        float sb = (float) sc.z;

        if (this.mc.options.anaglyph3d) {
            final float srr = (sr * 30.0f + sg * 59.0f + sb * 11.0f) / 100.0f;
            final float sgg = (sr * 30.0f + sg * 70.0f) / 100.0f;
            final float sbb = (sr * 30.0f + sb * 70.0f) / 100.0f;
            sr = srr;
            sg = sgg;
            sb = sbb;
        }

        glColor3f(sr, sg, sb);

        final Tesselator t = Tesselator.instance;

        glDepthMask(false);

        glEnable(GL_FOG);
        glColor3f(sr, sg, sb);
        glCallList(this.skyList);

        glDisable(GL_FOG);
        glDisable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        Lighting.turnOff();

        final float[] c = this.level.dimension.getSunriseColor(this.level.getTimeOfDay(alpha), alpha);
        if (c != null) {
            glDisable(GL_TEXTURE_2D);
            glShadeModel(GL_SMOOTH);

            glPushMatrix();
            {
                glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                glRotatef((this.level.getTimeOfDay(alpha) > 0.5f) ? 180.0f : 0.0f, 0.0f, 0.0f, 1.0f);

                float r = c[0];
                float g = c[1];
                float b = c[2];
                if (this.mc.options.anaglyph3d) {
                    final float srr = (r * 30.0f + g * 59.0f + b * 11.0f) / 100.0f;
                    final float sgg = (r * 30.0f + g * 70.0f) / 100.0f;
                    final float sbb = (r * 30.0f + b * 70.0f) / 100.0f;
                    r = srr;
                    g = sgg;
                    b = sbb;
                }

                t.begin(GL_TRIANGLE_FAN);
                t.color(r, g, b, c[3]);

                t.vertex(0.0, 100.0, 0.0);
                final int steps = 16;
                t.color(c[0], c[1], c[2], 0.0f);
                for (int i = 0; i <= steps; ++i) {
                    final float a = i * Mth.PI * 2.0f / steps;
                    final float _sin = Mth.sin(a);
                    final float _cos = Mth.cos(a);
                    t.vertex(_sin * 120.0f, _cos * 120.0f, -_cos * 40.0f * c[3]);
                }
                t.end();
            }
            glPopMatrix();
            glShadeModel(GL_FLAT);
        }

        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        glPushMatrix();
        {
            final float rainBrightness = 1.0f - this.level.getRainLevel(alpha);
            final float xp = 0.0f;
            final float yp = 0.0f;
            final float zp = 0.0f;
            glColor4f(1.0f, 1.0f, 1.0f, rainBrightness);
            glTranslatef(xp, yp, zp);
            glRotatef(0.0f, 0.0f, 0.0f, 1.0f);
            glRotatef(this.level.getTimeOfDay(alpha) * 360.0f, 1.0f, 0.0f, 0.0f);
            float ss = 30.0f;

            glBindTexture(GL_TEXTURE_2D, this.textures.loadTexture("/terrain/sun.png"));
            t.begin();
            t.vertexUV(-ss, 100.0, -ss, 0.0, 0.0);
            t.vertexUV(+ss, 100.0, -ss, 1.0, 0.0);
            t.vertexUV(+ss, 100.0, +ss, 1.0, 1.0);
            t.vertexUV(-ss, 100.0, +ss, 0.0, 1.0);
            t.end();

            ss = 20.0f;
            glBindTexture(GL_TEXTURE_2D, this.textures.loadTexture("/terrain/moon.png"));
            t.begin();
            t.vertexUV(-ss, -100.0, +ss, 1.0, 1.0);
            t.vertexUV(+ss, -100.0, +ss, 0.0, 1.0);
            t.vertexUV(+ss, -100.0, -ss, 0.0, 0.0);
            t.vertexUV(-ss, -100.0, -ss, 1.0, 0.0);
            t.end();

            glDisable(GL_TEXTURE_2D);
            final float br = this.level.getStarBrightness(alpha) * rainBrightness;
            if (br > 0.0f) {
                glColor4f(br, br, br, br);
                glCallList(this.starList);
            }
            glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        glDisable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glEnable(GL_FOG);

        glPopMatrix();
        if (this.level.dimension.hasGround()) {
            glColor3f(sr * 0.2f + 0.04f, sg * 0.2f + 0.04f, sb * 0.6f + 0.1f);
        } else {
            glColor3f(sr, sg, sb);
        }
        glDisable(GL_TEXTURE_2D);
        glCallList(this.darkList);
        glEnable(GL_TEXTURE_2D);

        glDepthMask(true);
    }
    
    public void renderClouds(final float alpha) {
        if (this.mc.level.dimension.foggy) return;

        if (this.mc.options.fancyGraphics) {
            this.renderAdvancedClouds(alpha);
            return;
        }

        glDisable(GL_CULL_FACE);
        final float yOffs = (float)(this.mc.cameraTargetPlayer.yOld + (this.mc.cameraTargetPlayer.y - this.mc.cameraTargetPlayer.yOld) * alpha);
        final int s = 32;
        final int d = 256 / s;
        final Tesselator t = Tesselator.instance;

        glBindTexture(GL_TEXTURE_2D, this.textures.loadTexture("/environment/clouds.png"));
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        final Vec3 cc = this.level.getCloudColor(alpha);
        float cr = (float)cc.x;
        float cg = (float)cc.y;
        float cb = (float)cc.z;

        if (this.mc.options.anaglyph3d) {
            final float crr = (cr * 30.0f + cg * 59.0f + cb * 11.0f) / 100.0f;
            final float cgg = (cr * 30.0f + cg * 70.0f) / 100.0f;
            final float cbb = (cr * 30.0f + cb * 70.0f) / 100.0f;
            cr = crr;
            cg = cgg;
            cb = cbb;
        }

        float scale = 1f / 2048.0f;
        double xo = this.mc.cameraTargetPlayer.xo + (this.mc.cameraTargetPlayer.x - this.mc.cameraTargetPlayer.xo) * alpha + (this.ticks + alpha) * 0.03f;
        double zo = this.mc.cameraTargetPlayer.zo + (this.mc.cameraTargetPlayer.z - this.mc.cameraTargetPlayer.zo) * alpha;
        int xOffs = Mth.floor(xo / 2048.0);
        int zOffs = Mth.floor(zo / 2048.0);
        xo -= xOffs * 2048;
        zo -= zOffs * 2048;

        float yy = this.level.dimension.getCloudHeight() - yOffs + 0.33f;
        float uo = (float)(xo * scale);
        float vo = (float)(zo * scale);
        t.begin();

        t.color(cr, cg, cb, 0.8f);
        for (int xx = -s * d; xx < s * d; xx += s) {
            for (int zz = -s * d; zz < s * d; zz += s) {
                t.vertexUV(xx + 0, yy, zz + s, (xx + 0) * scale + uo, (zz + s) * scale + vo);
                t.vertexUV(xx + s, yy, zz + s, (xx + s) * scale + uo, (zz + s) * scale + vo);
                t.vertexUV(xx + s, yy, zz + 0, (xx + s) * scale + uo, (zz + 0) * scale + vo);
                t.vertexUV(xx + 0, yy, zz + 0, (xx + 0) * scale + uo, (zz + 0) * scale + vo);
            }
        }
        t.end();

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glDisable(GL_BLEND);
        glEnable(GL_CULL_FACE);
    }
    
    public boolean isInCloud(final double x, final double y, final double z, final float alpha) {
        return false;
    }
    
    public void renderAdvancedClouds(final float alpha) {
        glDisable(GL_CULL_FACE);
        float yOffs = (float)(this.mc.cameraTargetPlayer.yOld + (this.mc.cameraTargetPlayer.y - this.mc.cameraTargetPlayer.yOld) * alpha);
        Tesselator t = Tesselator.instance;

        float ss = 12.0f;
        float h = 4.0f;

        double xo = (this.mc.cameraTargetPlayer.xo + (this.mc.cameraTargetPlayer.x - this.mc.cameraTargetPlayer.xo) * alpha + (this.ticks + alpha) * 0.03f) / ss;
        double zo = (this.mc.cameraTargetPlayer.zo + (this.mc.cameraTargetPlayer.z - this.mc.cameraTargetPlayer.zo) * alpha) / ss + 0.33000001311302185;
        float yy = this.level.dimension.getCloudHeight() - yOffs + 0.33f;
        int xOffs = Mth.floor(xo / 2048.0);
        int zOffs = Mth.floor(zo / 2048.0);
        xo -= xOffs * 2048;
        zo -= zOffs * 2048;

        glBindTexture(GL_TEXTURE_2D, this.textures.loadTexture("/environment/clouds.png"));
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        final Vec3 cc = this.level.getCloudColor(alpha);
        float cr = (float)cc.x;
        float cg = (float)cc.y;
        float cb = (float)cc.z;

        if (this.mc.options.anaglyph3d) {
            final float crr = (cr * 30.0f + cg * 59.0f + cb * 11.0f) / 100.0f;
            final float cgg = (cr * 30.0f + cg * 70.0f) / 100.0f;
            final float cbb = (cr * 30.0f + cb * 70.0f) / 100.0f;
            cr = crr;
            cg = cgg;
            cb = cbb;
        }

        float uo = (float)(xo * 0.0);
        float vo = (float)(zo * 0.0);

        float scale = 1 / 256.0f;

        uo = Mth.floor(xo) * scale;
        vo = Mth.floor(zo) * scale;
        float xoffs = (float)(xo - Mth.floor(xo));
        float zoffs = (float)(zo - Mth.floor(zo));

        int D = 8;

        int radius = 3;
        float e = 1 / 1024.0f;
        glScalef(ss, 1.0f, ss);

        for (int pass = 0; pass < 2; ++pass) {
            if (pass == 0) {
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
            for (int xPos = -radius + 1; xPos <= radius; ++xPos) {
                for (int zPos = -radius + 1; zPos <= radius; ++zPos) {
                    t.begin();
                    final float xx = (float)(xPos * D);
                    final float zz = (float)(zPos * D);
                    final float xp = xx - xoffs;
                    final float zp = zz - zoffs;

                    if (yy > -h - 1) {
                        t.color(cr * 0.7f, cg * 0.7f, cb * 0.7f, 0.8f);
                        t.normal(0, -1, 0);
                        t.vertexUV(xp + 0, yy + 0, zp + D, (xx + 0) * scale + uo, (zz + D) * scale + vo);
                        t.vertexUV(xp + D, yy + 0, zp + D, (xx + D) * scale + uo, (zz + D) * scale + vo);
                        t.vertexUV(xp + D, yy + 0, zp + 0, (xx + D) * scale + uo, (zz + 0) * scale + vo);
                        t.vertexUV(xp + 0, yy + 0, zp + 0, (xx + 0) * scale + uo, (zz + 0) * scale + vo);
                    }

                    if (yy <= h + 1) {
                        t.color(cr, cg, cb, 0.8f);
                        t.normal(0, 1, 0);
                        t.vertexUV(xp + 0, yy + h - e, zp + D, (xx + 0) * scale + uo, (zz + D) * scale + vo);
                        t.vertexUV(xp + D, yy + h - e, zp + D, (xx + D) * scale + uo, (zz + D) * scale + vo);
                        t.vertexUV(xp + D, yy + h - e, zp + 0, (xx + D) * scale + uo, (zz + 0) * scale + vo);
                        t.vertexUV(xp + 0, yy + h - e, zp + 0, (xx + 0) * scale + uo, (zz + 0) * scale + vo);
                    }

                    t.color(cr * 0.9f, cg * 0.9f, cb * 0.9f, 0.8f);
                    if (xPos > -1) {
                        t.normal(-1, 0, 0);
                        for (int i = 0; i < D; ++i) {
                            t.vertexUV(xp + i + 0, yy + 0, zp + D, (xx + i + 0.5f) * scale + uo, (zz + D) * scale + vo);
                            t.vertexUV(xp + i + 0, yy + h, zp + D, (xx + i + 0.5f) * scale + uo, (zz + D) * scale + vo);
                            t.vertexUV(xp + i + 0, yy + h, zp + 0, (xx + i + 0.5f) * scale + uo, (zz + 0) * scale + vo);
                            t.vertexUV(xp + i + 0, yy + 0, zp + 0, (xx + i + 0.5f) * scale + uo, (zz + 0) * scale + vo);
                        }
                    }

                    if (xPos <= 1) {
                        t.normal(1, 0, 0);
                        for (int i = 0; i < D; ++i) {
                            t.vertexUV(xp + i + 1 - e, yy + 0, zp + D, (xx + i + 0.5f) * scale + uo, (zz + D) * scale + vo);
                            t.vertexUV(xp + i + 1 - e, yy + h, zp + D, (xx + i + 0.5f) * scale + uo, (zz + D) * scale + vo);
                            t.vertexUV(xp + i + 1 - e, yy + h, zp + 0, (xx + i + 0.5f) * scale + uo, (zz + 0) * scale + vo);
                            t.vertexUV(xp + i + 1 - e, yy + 0, zp + 0, (xx + i + 0.5f) * scale + uo, (zz + 0) * scale + vo);
                        }
                    }

                    t.color(cr * 0.8f, cg * 0.8f, cb * 0.8f, 0.8f);
                    if (zPos > -1) {
                        t.normal(0, 0, -1);
                        for (int i = 0; i < D; ++i) {
                            t.vertexUV(xp + 0, yy + h, zp + i + 0, (xx + 0) * scale + uo, (zz + i + 0.5f) * scale + vo);
                            t.vertexUV(xp + D, yy + h, zp + i + 0, (xx + D) * scale + uo, (zz + i + 0.5f) * scale + vo);
                            t.vertexUV(xp + D, yy + 0, zp + i + 0, (xx + D) * scale + uo, (zz + i + 0.5f) * scale + vo);
                            t.vertexUV(xp + 0, yy + 0, zp + i + 0, (xx + 0) * scale + uo, (zz + i + 0.5f) * scale + vo);
                        }
                    }

                    if (zPos <= 1) {
                        t.normal(0, 0, 1);
                        for (int i = 0; i < D; ++i) {
                            t.vertexUV(xp + 0, yy + h, zp + i + 1 - e, (xx + 0) * scale + uo, (zz + i + 0.5f) * scale + vo);
                            t.vertexUV(xp + D, yy + h, zp + i + 1 - e, (xx + D) * scale + uo, (zz + i + 0.5f) * scale + vo);
                            t.vertexUV(xp + D, yy + 0, zp + i + 1 - e, (xx + D) * scale + uo, (zz + i + 0.5f) * scale + vo);
                            t.vertexUV(xp + 0, yy + 0, zp + i + 1 - e, (xx + 0) * scale + uo, (zz + i + 0.5f) * scale + vo);
                        }
                    }
                    t.end();
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
            final int s = this.dirtyChunks.size() - 1;
            int amount = this.dirtyChunks.size();

            for (int i = 0; i < amount; ++i) {
                final Chunk chunk = this.dirtyChunks.get(s - i);
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
        DirtyChunkSorter dirtyChunkSorter = new DirtyChunkSorter(player);
        Chunk[] toAdd = new Chunk[n2];
        ArrayList<Chunk> nearChunks = null;
        int pendingChunkSize = this.dirtyChunks.size();
        int pendingChunkRemoved = 0;

        for (int i = 0; i < pendingChunkSize; ++i) {
            final Chunk chunk = this.dirtyChunks.get(i);
            if (!force) {
                if (chunk.distanceToSqr(player) > 256.0f) {
                    int index = 0;

                    while (index < n2 && (toAdd[index] == null || dirtyChunkSorter.compare(toAdd[index], chunk) <= 0)) {
                        ++index;
                    }

                    if (--index <= 0) continue;

                    int x = index;
                    while (--x != 0) {
                        toAdd[x - 1] = toAdd[x];
                    }

                    toAdd[index] = chunk;
                    continue;
                }
            }
            else if (!chunk.visible) {
                continue;
            }

            if (nearChunks == null) {
                nearChunks = new ArrayList<>();
            }

            ++pendingChunkRemoved;
            nearChunks.add(chunk);
            this.dirtyChunks.set(i, null);
        }

        if (nearChunks != null) {
            if (nearChunks.size() > 1) {
                Collections.sort(nearChunks, dirtyChunkSorter);
            }

            for (int i = nearChunks.size() - 1; i >= 0; --i) {
                final Chunk chunk = nearChunks.get(i);
                chunk.rebuild();
                chunk.dirty = false;
            }
        }

        int secondaryRemoved = 0;
        for (int i = n2 - 1; i >= 0; --i) {
            final Chunk chunk = toAdd[i];
            if (chunk != null) {
                if (!chunk.visible && i != n2 - 1) {
                    toAdd[0] = null;
                    toAdd[i] = null;
                    break;
                }

                toAdd[i].rebuild();
                toAdd[i].dirty = false;
                ++secondaryRemoved;
            }
        }

        int cursor = 0;
        int target = 0;

        for (int arraySize = this.dirtyChunks.size(); cursor != arraySize; cursor++) {
            Chunk chunk = this.dirtyChunks.get(cursor);
            if (chunk != null) {
                boolean found = false;
                for (int i = 0; i < n2; ++i) {
                    if (chunk == toAdd[i]) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    if (target != cursor) {
                        this.dirtyChunks.set(target, chunk);
                    }
                    ++target;
                }
            }
        }

        while (--cursor >= target) {
            this.dirtyChunks.remove(cursor);
        }
        return pendingChunkSize == pendingChunkRemoved + secondaryRemoved;
    }
    
    public void renderHit(final Player player, final HitResult h, final int mode, final ItemInstance inventoryItem, final float a) {
        final Tesselator t = Tesselator.instance;
        glEnable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        glColor4f(1.0f, 1.0f, 1.0f, (Mth.sin(System.currentTimeMillis() / 100.0f) * 0.2f + 0.4f) * 0.5f);
        if (mode == 0) {
            if (this.destroyProgress > 0.0f) {
                glBlendFunc(GL_DST_COLOR, GL_SRC_COLOR);

                glBindTexture(GL_TEXTURE_2D, this.textures.loadTexture("/terrain.png"));
                glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
                glPushMatrix();

                final int tileId = this.level.getTile(h.x, h.y, h.z);
                Tile tile = (tileId > 0) ? Tile.tiles[tileId] : null;
                glDisable(GL_ALPHA_TEST);
                glPolygonOffset(-3.0f, -3.0f);
                glEnable(GL_POLYGON_OFFSET_FILL);

                final double xo = player.xOld + (player.x - player.xOld) * a;
                final double yo = player.yOld + (player.y - player.yOld) * a;
                final double zo = player.zOld + (player.z - player.zOld) * a;
                if (tile == null) tile = Tile.rock;

                glEnable(GL_ALPHA_TEST);
                t.begin();
                t.offset(-xo, -yo, -zo);
                t.noColor();
                this.tileRenderer.tesselateInWorld(tile, h.x, h.y, h.z, 240 + (int)(this.destroyProgress * 10.0f));
                t.end();
                t.offset(0.0, 0.0, 0.0);
                glDisable(GL_ALPHA_TEST);

                glPolygonOffset(0.0f, 0.0f);
                glDisable(GL_POLYGON_OFFSET_FILL);
                glEnable(GL_ALPHA_TEST);
                glDepthMask(true);
                glPopMatrix();
            }
        }
        else if (inventoryItem != null) {
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            final float br = Mth.sin(System.currentTimeMillis() / 100.0f) * 0.2f + 0.8f;
            glColor4f(br, br, br, Mth.sin(System.currentTimeMillis() / 200.0f) * 0.2f + 0.5f);
            int tex = this.textures.loadTexture("/terrain.png");
            glBindTexture(GL_TEXTURE_2D, tex);

            int x = h.x;
            int y = h.y;
            int z = h.z;
            if (h.f == 0) --y;
            if (h.f == 1) ++y;
            if (h.f == 2) --z;
            if (h.f == 3) ++z;
            if (h.f == 4) --x;
            if (h.f == 5) ++x;
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
            final float ss = 0.002f;
            final int tileId = this.level.getTile(h.x, h.y, h.z);

            if (tileId > 0) {
                Tile.tiles[tileId].updateShape(this.level, h.x, h.y, h.z);
                double xo = (player.xOld + (player.x - player.xOld) * a);
                double yo = (player.yOld + (player.y - player.yOld) * a);
                double zo = (player.zOld + (player.z - player.zOld) * a);
                this.render(Tile.tiles[tileId].getTileAABB(this.level, h.x, h.y, h.z).grow(ss, ss, ss).cloneMove(-xo, -yo, -zo));
            }
            glDepthMask(true);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
        }
    }
    
    private void render(final AABB aabb) {
        final Tesselator t = Tesselator.instance;

        t.begin(GL_LINE_STRIP);
        t.vertex(aabb.x0, aabb.y0, aabb.z0);
        t.vertex(aabb.x1, aabb.y0, aabb.z0);
        t.vertex(aabb.x1, aabb.y0, aabb.z1);
        t.vertex(aabb.x0, aabb.y0, aabb.z1);
        t.vertex(aabb.x0, aabb.y0, aabb.z0);
        t.end();

        t.begin(GL_LINE_STRIP);
        t.vertex(aabb.x0, aabb.y1, aabb.z0);
        t.vertex(aabb.x1, aabb.y1, aabb.z0);
        t.vertex(aabb.x1, aabb.y1, aabb.z1);
        t.vertex(aabb.x0, aabb.y1, aabb.z1);
        t.vertex(aabb.x0, aabb.y1, aabb.z0);
        t.end();

        t.begin(GL_LINES);
        t.vertex(aabb.x0, aabb.y0, aabb.z0);
        t.vertex(aabb.x0, aabb.y1, aabb.z0);
        t.vertex(aabb.x1, aabb.y0, aabb.z0);
        t.vertex(aabb.x1, aabb.y1, aabb.z0);
        t.vertex(aabb.x1, aabb.y0, aabb.z1);
        t.vertex(aabb.x1, aabb.y1, aabb.z1);
        t.vertex(aabb.x0, aabb.y0, aabb.z1);
        t.vertex(aabb.x0, aabb.y1, aabb.z1);
        t.end();
    }
    
    public void setDirty(final int x0, final int y0, final int z0, final int x1, final int y1, final int z1) {
        final int _x0 = Mth.intFloorDiv(x0, CHUNK_SIZE);
        final int _y0 = Mth.intFloorDiv(y0, CHUNK_SIZE);
        final int _z0 = Mth.intFloorDiv(z0, CHUNK_SIZE);
        final int _x1 = Mth.intFloorDiv(x1, CHUNK_SIZE);
        final int _y1 = Mth.intFloorDiv(y1, CHUNK_SIZE);
        final int _z1 = Mth.intFloorDiv(z1, CHUNK_SIZE);

        for (int x = _x0; x <= _x1; ++x) {
            int xx = x % this.xChunks;
            if (xx < 0) xx += this.xChunks;

            for (int y = _y0; y <= _y1; ++y) {
                int yy = y % this.yChunks;
                if (yy < 0) yy += this.yChunks;

                for (int z = _z0; z <= _z1; ++z) {
                    int zz = z % this.zChunks;
                    if (zz < 0) zz += this.zChunks;

                    int p = (zz * this.yChunks + yy) * this.xChunks + xx;
                    final Chunk chunk = this.chunks[p];
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
        if (name != null) this.mc.gui.setNowPlaying("C418 - " + name);

        this.mc.soundEngine.playStreaming(name, (float)x, (float)y, (float)z, 1.0f, 1.0f);
    }
    
    public void playSound(final String name, final double x, final double y, final double z, final float volume, final float pitch) {
        float dd = 16.0f;
        if (volume > 1.0f) dd *= volume;

        if (this.mc.cameraTargetPlayer.distanceToSqr(x, y, z) < dd * dd) {
            this.mc.soundEngine.play(name, (float)x, (float)y, (float)z, volume, pitch);
        }
    }
    
    public void addParticle(final String name, final double x, final double y, final double z, final double xa, final double ya, final double za) {
        if (this.mc == null || this.mc.cameraTargetPlayer == null || this.mc.particleEngine == null) return;

        final double xd = this.mc.cameraTargetPlayer.x - x;
        final double yd = this.mc.cameraTargetPlayer.y - y;
        final double zd = this.mc.cameraTargetPlayer.z - z;

        final double particleDistance = 16.0;
        if (xd * xd + yd * yd + zd * zd > particleDistance * particleDistance) return;

        if (name.equals("bubble")) this.mc.particleEngine.add(new BubbleParticle(this.level, x, y, z, xa, ya, za));
        else if (name.equals("smoke")) this.mc.particleEngine.add(new SmokeParticle(this.level, x, y, z, xa, ya, za));
        else if (name.equals("note")) this.mc.particleEngine.add(new NoteParticle(this.level, x, y, z, xa, ya, za));
        else if (name.equals("portal")) this.mc.particleEngine.add(new PortalParticle(this.level, x, y, z, xa, ya, za));
        else if (name.equals("explode")) this.mc.particleEngine.add(new ExplodeParticle(this.level, x, y, z, xa, ya, za));
        else if (name.equals("flame")) this.mc.particleEngine.add(new FlameParticle(this.level, x, y, z, xa, ya, za));
        else if (name.equals("lava")) this.mc.particleEngine.add(new LavaParticle(this.level, x, y, z));
        else if (name.equals("footstep")) this.mc.particleEngine.add(new FootstepParticle(this.textures, this.level, x, y, z));
        else if (name.equals("splash")) this.mc.particleEngine.add(new SplashParticle(this.level, x, y, z, xa, ya, za));
        else if (name.equals("largesmoke")) this.mc.particleEngine.add(new SmokeParticle(this.level, x, y, z, xa, ya, za, 2.5f));
        else if (name.equals("reddust")) this.mc.particleEngine.add(new RedDustParticle(this.level, x, y, z, (float) xa, (float) ya, (float) za));
        else if (name.equals("snowballpoof")) this.mc.particleEngine.add(new BreakingItemParticle(this.level, x, y, z, Item.snowBall));
        else if (name.equals("snowshovel")) this.mc.particleEngine.add(new SnowShovelParticle(this.level, x, y, z, xa, ya, za));
        else if (name.equals("slime")) this.mc.particleEngine.add(new BreakingItemParticle(this.level, x, y, z, Item.slimeBall));
        else if (name.equals("heart")) this.mc.particleEngine.add(new HeartParticle(this.level, x, y, z, xa, ya, za));
    }
    
    public void entityAdded(final Entity entity) {
        entity.prepareCustomTextures();

        if (entity.customTextureUrl != null) this.textures.addHttpTexture(entity.customTextureUrl, new MobSkinTextureProcessor());
        if (entity.customTextureUrl2 != null) this.textures.addHttpTexture(entity.customTextureUrl2, new MobSkinTextureProcessor());
    }
    
    public void entityRemoved(final Entity entity) {
        if (entity.customTextureUrl != null) this.textures.removeHttpTexture(entity.customTextureUrl);
        if (entity.customTextureUrl2 != null) this.textures.removeHttpTexture(entity.customTextureUrl2);
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
            case LevelEvent.SOUND_CLICK_FAIL: {
                this.level.playSound(x, y, z, "random.click", 1.0f, 1.2f);
                break;
            }
            case LevelEvent.SOUND_CLICK: {
                this.level.playSound(x, y, z, "random.click", 1.0f, 1.0f);
                break;
            }
            case LevelEvent.SOUND_LAUNCH: {
                this.level.playSound(x, y, z, "random.bow", 1.0f, 1.2f);
                break;
            }
            case LevelEvent.PARTICLES_SHOOT: {
                final int xd = data % 3 - 1;
                final int zd = data / 3 % 3 - 1;
                final double xp = x + xd * 0.6 + 0.5;
                final double yp = y + 0.5;
                final double zp = z + zd * 0.6 + 0.5;
                for (int i = 0; i < 10; ++i) {
                    final double pow = random.nextDouble() * 0.2 + 0.01;
                    double xs = xp + xd * 0.01 + (random.nextDouble() - 0.5) * zd * 0.5;
                    double ys = yp + (random.nextDouble() - 0.5) * 0.5;
                    double zs = zp + zd * 0.01 + (random.nextDouble() - 0.5) * xd * 0.5;
                    double xsa = xd * pow + random.nextGaussian() * 0.01;
                    double ysa = -0.03 + random.nextGaussian() * 0.01;
                    double zsa = zd * pow + random.nextGaussian() * 0.01;
                    this.addParticle("smoke", xs, ys, zs, xsa, ysa, zsa);
                }
                break;
            }
            case LevelEvent.PARTICLES_DESTROY_BLOCK: {
                final int t = data & Tile.TILE_NUM_MASK;
                if (t > 0) {
                    final Tile oldTIle = Tile.tiles[t];
                    this.mc.soundEngine.play(oldTIle.soundType.getBreakSound(), x + 0.5f, y + 0.5f, z + 0.5f, (oldTIle.soundType.getVolume() + 1.0f) / 2.0f, oldTIle.soundType.getPitch() * 0.8f);
                }
                this.mc.particleEngine.destroy(x, y, z, data & 0xFF, data >> 8 & 0xFF);
                break;
            }
            case LevelEvent.SOUND_OPEN_DOOR: {
                if (Math.random() < 0.5) {
                    this.level.playSound(x + 0.5, y + 0.5, z + 0.5, "random.door_open", 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f);
                } else {
                    this.level.playSound(x + 0.5, y + 0.5, z + 0.5, "random.door_close", 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f);
                }
                break;
            }
            case LevelEvent.SOUND_FIZZ: {
                this.level.playSound(x + 0.5f, y + 0.5f, z + 0.5f, "random.fizz", 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f);
                break;
            }
            case LevelEvent.SOUND_PLAY_RECORDING: {
                if (Item.items[data] instanceof RecordingItem) {
                    RecordingItem recording = (RecordingItem)Item.items[data];
                    this.level.playStreamingMusic(recording.recording, x, y, z);
                } else {
                    this.level.playStreamingMusic(null, x, y, z);
                }
                break;
            }
        }
    }
}
