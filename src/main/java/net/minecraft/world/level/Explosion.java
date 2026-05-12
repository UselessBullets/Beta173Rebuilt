// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.tile.Tile;
import util.Mth;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.entity.Entity;
import java.util.Random;

public class Explosion
{
    public boolean fire;
    private Random random;
    private Level level;
    public double x;
    public double y;
    public double z;
    public Entity source;
    public float r;
    public Set toBlow;
    
    public Explosion(final Level level, final Entity source, final double x, final double y, final double z, final float r) {
        this.fire = false;
        this.random = new Random();
        this.toBlow = new HashSet();
        this.level = level;
        this.source = source;
        this.r = r;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void explode() {
        final float r = this.r;
        for (int n = 16, i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                for (int k = 0; k < n; ++k) {
                    if (i == 0 || i == n - 1 || j == 0 || j == n - 1 || k == 0 || k == n - 1) {
                        final double n2 = i / (n - 1.0f) * 2.0f - 1.0f;
                        final double n3 = j / (n - 1.0f) * 2.0f - 1.0f;
                        final double n4 = k / (n - 1.0f) * 2.0f - 1.0f;
                        final double sqrt = Math.sqrt(n2 * n2 + n3 * n3 + n4 * n4);
                        final double n5 = n2 / sqrt;
                        final double n6 = n3 / sqrt;
                        final double n7 = n4 / sqrt;
                        float n8 = this.r * (0.7f + this.level.random.nextFloat() * 0.6f);
                        double x = this.x;
                        double y = this.y;
                        double z = this.z;
                        for (float n9 = 0.3f; n8 > 0.0f; n8 -= n9 * 0.75f) {
                            final int floor = Mth.floor(x);
                            final int floor2 = Mth.floor(y);
                            final int floor3 = Mth.floor(z);
                            final int tile = this.level.getTile(floor, floor2, floor3);
                            if (tile > 0) {
                                n8 -= (Tile.tiles[tile].getExplosionResistance(this.source) + 0.3f) * n9;
                            }
                            if (n8 > 0.0f) {
                                this.toBlow.add(new TilePos(floor, floor2, floor3));
                            }
                            x += n5 * n9;
                            y += n6 * n9;
                            z += n7 * n9;
                        }
                    }
                }
            }
        }
        this.r *= 2.0f;
        final List entities = this.level.getEntities(this.source, AABB.newTemp(Mth.floor(this.x - this.r - 1.0), Mth.floor(this.y - this.r - 1.0), Mth.floor(this.z - this.r - 1.0), Mth.floor(this.x + this.r + 1.0), Mth.floor(this.y + this.r + 1.0), Mth.floor(this.z + this.r + 1.0)));
        final Vec3 temp = Vec3.newTemp(this.x, this.y, this.z);
        for (int l = 0; l < entities.size(); ++l) {
            final Entity entity = entities.get(l);
            final double n10 = entity.distanceTo(this.x, this.y, this.z) / this.r;
            if (n10 <= 1.0) {
                final double n11 = entity.x - this.x;
                final double n12 = entity.y - this.y;
                final double n13 = entity.z - this.z;
                final double n14 = Mth.sqrt(n11 * n11 + n12 * n12 + n13 * n13);
                final double n15 = n11 / n14;
                final double n16 = n12 / n14;
                final double n17 = n13 / n14;
                final double n18 = (1.0 - n10) * this.level.getSeenPercent(temp, entity.bb);
                entity.hurt(this.source, (int)((n18 * n18 + n18) / 2.0 * 8.0 * this.r + 1.0));
                final double n19 = n18;
                final Entity entity2 = entity;
                entity2.xd += n15 * n19;
                final Entity entity3 = entity;
                entity3.yd += n16 * n19;
                final Entity entity4 = entity;
                entity4.zd += n17 * n19;
            }
        }
        this.r = r;
        final ArrayList list = new ArrayList();
        list.addAll(this.toBlow);
        if (this.fire) {
            for (int n20 = list.size() - 1; n20 >= 0; --n20) {
                final TilePos tilePos = (TilePos)list.get(n20);
                final int x2 = tilePos.x;
                final int y2 = tilePos.y;
                final int z2 = tilePos.z;
                final int tile2 = this.level.getTile(x2, y2, z2);
                final int tile3 = this.level.getTile(x2, y2 - 1, z2);
                if (tile2 == 0 && Tile.solid[tile3] && this.random.nextInt(3) == 0) {
                    this.level.setTile(x2, y2, z2, Tile.fire.id);
                }
            }
        }
    }
    
    public void addParticles(final boolean generateParticles) {
        this.level.playLocalSound(this.x, this.y, this.z, "random.explode", 4.0f, (1.0f + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2f) * 0.7f);
        final ArrayList list = new ArrayList();
        list.addAll(this.toBlow);
        for (int i = list.size() - 1; i >= 0; --i) {
            final TilePos tilePos = (TilePos)list.get(i);
            final int x = tilePos.x;
            final int y = tilePos.y;
            final int z = tilePos.z;
            final int tile = this.level.getTile(x, y, z);
            if (generateParticles) {
                final double x2 = x + this.level.random.nextFloat();
                final double y2 = y + this.level.random.nextFloat();
                final double z2 = z + this.level.random.nextFloat();
                final double n = x2 - this.x;
                final double n2 = y2 - this.y;
                final double n3 = z2 - this.z;
                final double n4 = Mth.sqrt(n * n + n2 * n2 + n3 * n3);
                final double n5 = n / n4;
                final double n6 = n2 / n4;
                final double n7 = n3 / n4;
                final double n8 = 0.5 / (n4 / this.r + 0.1) * (this.level.random.nextFloat() * this.level.random.nextFloat() + 0.3f);
                final double n9 = n5 * n8;
                final double n10 = n6 * n8;
                final double n11 = n7 * n8;
                this.level.addParticle("explode", (x2 + this.x * 1.0) / 2.0, (y2 + this.y * 1.0) / 2.0, (z2 + this.z * 1.0) / 2.0, n9, n10, n11);
                this.level.addParticle("smoke", x2, y2, z2, n9, n10, n11);
            }
            if (tile > 0) {
                Tile.tiles[tile].spawnResources(this.level, x, y, z, this.level.getData(x, y, z), 0.3f);
                this.level.setTile(x, y, z, 0);
                Tile.tiles[tile].wasExploded(this.level, x, y, z);
            }
        }
    }
}
