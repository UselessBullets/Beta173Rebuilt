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
    public static final int MISC_TEXTURE = 0;
    public static final int TERRAIN_TEXTURE = 1;
    public static final int ITEM_TEXTURE = 2;
    public static final int ENTITY_PARTICLE_TEXTURE = 3;
    public static final int TEXTURE_COUNT = 4;
    protected Level level;
    private List<Particle>[] particles;
    private Textures textures;
    private Random random;
    
    public ParticleEngine(final Level level, final Textures textures) {
        this.particles = new List[TEXTURE_COUNT];
        this.random = new Random();
        if (level != null) {
            this.level = level;
        }
        this.textures = textures;
        for (int i = 0; i < 4; ++i) {
            this.particles[i] = new ArrayList<>();
        }
    }
    
    public void add(final Particle p) {
        final int particleTexture = p.getParticleTexture();
        if (this.particles[particleTexture].size() >= 4000) {
            this.particles[particleTexture].remove(0);
        }
        this.particles[particleTexture].add(p);
    }
    
    public void tick() {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < this.particles[i].size(); ++j) {
                final Particle particle = this.particles[i].get(j);
                particle.tick();
                if (particle.removed) {
                    this.particles[i].remove(j--);
                }
            }
        }
    }
    
    public void render(final Entity player, final float partialTick) {
        final float cos = Mth.cos(player.yRot * Mth.DEGRAD);
        final float sin = Mth.sin(player.yRot * Mth.DEGRAD);
        final float xa2 = -sin * Mth.sin(player.xRot * Mth.DEGRAD);
        final float za2 = cos * Mth.sin(player.xRot * Mth.DEGRAD);
        final float cos2 = Mth.cos(player.xRot * Mth.DEGRAD);
        Particle.xOff = player.xOld + (player.x - player.xOld) * partialTick;
        Particle.yOff = player.yOld + (player.y - player.yOld) * partialTick;
        Particle.zOff = player.zOld + (player.z - player.zOld) * partialTick;
        for (int i = MISC_TEXTURE; i < ENTITY_PARTICLE_TEXTURE; ++i) {
            if (this.particles[i].size() != 0) {
                int n = 0;
                if (i == MISC_TEXTURE) {
                    n = this.textures.loadTexture("/particles.png");
                }
                if (i == TERRAIN_TEXTURE) {
                    n = this.textures.loadTexture("/terrain.png");
                }
                if (i == ITEM_TEXTURE) {
                    n = this.textures.loadTexture("/gui/items.png");
                }
                glBindTexture(GL_TEXTURE_2D, n);
                final Tesselator instance = Tesselator.instance;
                instance.begin();
                for (int j = 0; j < this.particles[i].size(); ++j) {
                    ((Particle)this.particles[i].get(j)).render(instance, partialTick, cos, cos2, sin, xa2, za2);
                }
                instance.end();
            }
        }
    }
    
    public void renderLit(final Entity player, final float partialTick) {
        final int n = 3;
        if (this.particles[n].size() == 0) {
            return;
        }
        final Tesselator instance = Tesselator.instance;
        for (int i = 0; i < this.particles[n].size(); ++i) {
            ((Particle)this.particles[n].get(i)).render(instance, partialTick, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        }
    }
    
    public void setLevel(final Level level) {
        this.level = level;
        for (int i = 0; i < 4; ++i) {
            this.particles[i].clear();
        }
    }
    
    public void destroy(final int x, final int y, final int z, final int tid, final int data) {
        if (tid == 0) {
            return;
        }
        final Tile tile = Tile.tiles[tid];
        for (int n = 4, i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                for (int k = 0; k < n; ++k) {
                    final double x2 = x + (i + 0.5) / n;
                    final double y2 = y + (j + 0.5) / n;
                    final double z2 = z + (k + 0.5) / n;
                    this.add(new TerrainParticle(this.level, x2, y2, z2, x2 - x - 0.5, y2 - y - 0.5, z2 - z - 0.5, tile, this.random.nextInt(6), data).init(x, y, z));
                }
            }
        }
    }
    
    public void crack(final int x, final int y, final int z, final int face) {
        final int tile = this.level.getTile(x, y, z);
        if (tile == 0) {
            return;
        }
        final Tile tile2 = Tile.tiles[tile];
        final float n = 0.1f;
        double x2 = x + this.random.nextDouble() * (tile2.xx1 - tile2.xx0 - n * 2.0f) + n + tile2.xx0;
        double y2 = y + this.random.nextDouble() * (tile2.yy1 - tile2.yy0 - n * 2.0f) + n + tile2.yy0;
        double z2 = z + this.random.nextDouble() * (tile2.zz1 - tile2.zz0 - n * 2.0f) + n + tile2.zz0;
        if (face == 0) {
            y2 = y + tile2.yy0 - n;
        }
        if (face == 1) {
            y2 = y + tile2.yy1 + n;
        }
        if (face == 2) {
            z2 = z + tile2.zz0 - n;
        }
        if (face == 3) {
            z2 = z + tile2.zz1 + n;
        }
        if (face == 4) {
            x2 = x + tile2.xx0 - n;
        }
        if (face == 5) {
            x2 = x + tile2.xx1 + n;
        }
        this.add(new TerrainParticle(this.level, x2, y2, z2, 0.0, 0.0, 0.0, tile2, face, this.level.getData(x, y, z)).init(x, y, z).setPower(0.2f).scale(0.6f));
    }
    
    public String countParticles() {
        return "" + (this.particles[0].size() + this.particles[1].size() + this.particles[2].size());
    }
}
