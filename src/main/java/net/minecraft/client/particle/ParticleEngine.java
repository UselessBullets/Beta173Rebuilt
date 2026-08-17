// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.world.level.tile.Tile;
import net.minecraft.client.renderer.Tesselator;
import util.Mth;
import net.minecraft.world.entity.Entity;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.renderer.Textures;
import java.util.List;
import net.minecraft.world.level.Level;

import static org.lwjgl.opengl.GL11.*;

public class ParticleEngine
{
    private static final int MAX_PARTICLES_PER_LAYER = 4000;
    public static final int MISC_TEXTURE = 0;
    public static final int TERRAIN_TEXTURE = 1;
    public static final int ITEM_TEXTURE = 2;
    public static final int ENTITY_PARTICLE_TEXTURE = 3;
    public static final int TEXTURE_COUNT = 4;

    protected Level level;
    private List<Particle>[] particles = new List[TEXTURE_COUNT];
    private Textures textures;
    private Random random = new Random();
    
    public ParticleEngine(final Level level, final Textures textures) {
        if (level != null) {
            this.level = level;
        }
        this.textures = textures;
        for (int i = 0; i < TEXTURE_COUNT; ++i) {
            this.particles[i] = new ArrayList<>();
        }
    }
    
    public void add(final Particle p) {
        final int t = p.getParticleTexture();
        if (this.particles[t].size() >= MAX_PARTICLES_PER_LAYER) this.particles[t].remove(0);
        this.particles[t].add(p);
    }
    
    public void tick() {
        for (int tt = 0; tt < TEXTURE_COUNT; ++tt) {
            for (int i = 0; i < this.particles[tt].size(); ++i) {
                final Particle p = this.particles[tt].get(i);
                p.tick();
                if (p.removed) this.particles[tt].remove(i--);
            }
        }
    }
    
    public void render(final Entity player, final float a) {
        final float xa = Mth.cos(player.yRot * Mth.DEGRAD);
        final float za = Mth.sin(player.yRot * Mth.DEGRAD);

        final float xa2 = -za * Mth.sin(player.xRot * Mth.DEGRAD);
        final float za2 = xa * Mth.sin(player.xRot * Mth.DEGRAD);
        final float ya = Mth.cos(player.xRot * Mth.DEGRAD);

        Particle.xOff = player.xOld + (player.x - player.xOld) * a;
        Particle.yOff = player.yOld + (player.y - player.yOld) * a;
        Particle.zOff = player.zOld + (player.z - player.zOld) * a;
        for (int tt = 0; tt < ENTITY_PARTICLE_TEXTURE; ++tt) { // Useless - Beta 1.7.3 loops only on the first 3 layers to avoid running the entity particles here, so that constant is used as the bound for this loop
            if (this.particles[tt].size() != 0) {
                int tex = 0;
                if (tt == MISC_TEXTURE) tex = this.textures.loadTexture("/particles.png");
                if (tt == TERRAIN_TEXTURE) tex = this.textures.loadTexture("/terrain.png");
                if (tt == ITEM_TEXTURE) tex = this.textures.loadTexture("/gui/items.png");

                glBindTexture(GL_TEXTURE_2D, tex);
                final Tesselator t = Tesselator.instance;
                t.begin();
                for (int i = 0; i < this.particles[tt].size(); ++i) {
                    Particle p = this.particles[tt].get(i);
                    p.render(t, a, xa, ya, za, xa2, za2);
                }
                t.end();
            }
        }
    }
    
    public void renderLit(final Entity player, final float a) {
        final int tt = 3;
        if (this.particles[tt].size() == 0) return;

        final Tesselator t = Tesselator.instance;
        for (int i = 0; i < this.particles[tt].size(); ++i) {
            Particle p = this.particles[tt].get(i);
            p.render(t, a, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        }
    }
    
    public void setLevel(final Level level) {
        this.level = level;
        for (int i = 0; i < TEXTURE_COUNT; ++i) {
            this.particles[i].clear();
        }
    }
    
    public void destroy(final int x, final int y, final int z, final int tid, final int data) {
        if (tid == 0) return;

        final Tile tile = Tile.tiles[tid];
        int SD = 4;
        for (int xx = 0; xx < SD; ++xx) {
            for (int yy = 0; yy < SD; ++yy) {
                for (int zz = 0; zz < SD; ++zz) {
                    final double xp = x + (xx + 0.5) / SD;
                    final double yp = y + (yy + 0.5) / SD;
                    final double zp = z + (zz + 0.5) / SD;
                    final int face = this.random.nextInt(6);
                    this.add(new TerrainParticle(this.level, xp, yp, zp, xp - x - 0.5, yp - y - 0.5, zp - z - 0.5, tile, face, data).init(x, y, z));
                }
            }
        }
    }
    
    public void crack(final int x, final int y, final int z, final int face) {
        final int tid = this.level.getTile(x, y, z);
        if (tid == 0) return;
        final Tile tile = Tile.tiles[tid];
        final float r = 0.1f;
        double xp = x + this.random.nextDouble() * (tile.xx1 - tile.xx0 - r * 2.0f) + r + tile.xx0;
        double yp = y + this.random.nextDouble() * (tile.yy1 - tile.yy0 - r * 2.0f) + r + tile.yy0;
        double zp = z + this.random.nextDouble() * (tile.zz1 - tile.zz0 - r * 2.0f) + r + tile.zz0;
        if (face == 0) yp = y + tile.yy0 - r;
        if (face == 1) yp = y + tile.yy1 + r;
        if (face == 2) zp = z + tile.zz0 - r;
        if (face == 3) zp = z + tile.zz1 + r;
        if (face == 4) xp = x + tile.xx0 - r;
        if (face == 5) xp = x + tile.xx1 + r;
        this.add(new TerrainParticle(this.level, xp, yp, zp, 0.0, 0.0, 0.0, tile, face, this.level.getData(x, y, z)).init(x, y, z).setPower(0.2f).scale(0.6f));
    }
    
    public String countParticles() {
        return "" + (this.particles[0].size() + this.particles[1].size() + this.particles[2].size());
    }
}
