// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

import java.util.List;
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
    public boolean fire = false;
    private Random random = new Random();
    private Level level;
    public double x, y, z;
    public Entity source;
    public float r;
    public Set<TilePos> toBlow = new HashSet<>();
    
    public Explosion(final Level level, final Entity source, final double x, final double y, final double z, final float r) {
        this.level = level;
        this.source = source;
        this.r = r;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void explode() {
        final float oR = this.r;

        int size = 16;
        for (int xx = 0; xx < size; ++xx) {
            for (int yy = 0; yy < size; ++yy) {
                for (int zz = 0; zz < size; ++zz) {
                    if (xx != 0 && xx != size - 1 && yy != 0 && yy != size - 1 && zz != 0 && zz != size - 1) continue;

                    double xd = xx / (size - 1.0f) * 2.0f - 1.0f;
                    double yd = yy / (size - 1.0f) * 2.0f - 1.0f;
                    double zd = zz / (size - 1.0f) * 2.0f - 1.0f;
                    double d = Math.sqrt(xd * xd + yd * yd + zd * zd);

                    xd /= d;
                    yd /= d;
                    zd /= d;

                    float remainingPower = this.r * (0.7f + this.level.random.nextFloat() * 0.6f);
                    double xp = this.x;
                    double yp = this.y;
                    double zp = this.z;

                    float stepSize = 0.3f;
                    while (remainingPower > 0.0f) {
                        final int xt = Mth.floor(xp);
                        final int yt = Mth.floor(yp);
                        final int zt = Mth.floor(zp);
                        final int t = this.level.getTile(xt, yt, zt);
                        if (t > 0) {
                            remainingPower -= (Tile.tiles[t].getExplosionResistance(this.source) + 0.3f) * stepSize;
                        }
                        if (remainingPower > 0.0f) {
                            this.toBlow.add(new TilePos(xt, yt, zt));
                        }

                        xp += xd * stepSize;
                        yp += yd * stepSize;
                        zp += zd * stepSize;
                        remainingPower -= stepSize * 0.75f;
                    }
                    // if (xd*xd+yd*yd+zd*zd>1) continue;
                }
            }
        }

        this.r *= 2.0f;
        int x0 = Mth.floor(this.x - this.r - 1.0);
        int y0 = Mth.floor(this.y - this.r - 1.0);
        int z0 = Mth.floor(this.z - this.r - 1.0);
        int x1 = Mth.floor(this.x + this.r + 1.0);
        int y1 = Mth.floor(this.y + this.r + 1.0);
        int z1 = Mth.floor(this.z + this.r + 1.0);

        final List<Entity> entities = this.level.getEntities(this.source, AABB.newTemp(x0, y0, z0, x1, y1, z1));
        final Vec3 center = Vec3.newTemp(this.x, this.y, this.z);

        for (int i = 0; i < entities.size(); ++i) {
            final Entity e = entities.get(i);

            final double dist = e.distanceTo(this.x, this.y, this.z) / this.r;
            if (dist <= 1.0) {
                double xa = e.x - this.x;
                double ya = e.y - this.y;
                double za = e.z - this.z;

                double da = Mth.sqrt(xa * xa + ya * ya + za * za);

                xa /= da;
                ya /= da;
                za /= da;

                float sp = this.level.getSeenPercent(center, e.bb);
                final double pow = (1.0 - dist) * sp;
                e.hurt(this.source, (int)((pow * pow + pow) / 2.0 * 8.0 * this.r + 1.0));

                final double push = pow;
                e.xd += xa * push;
                e.yd += ya * push;
                e.zd += za * push;
            }
        }
        this.r = oR;
        final ArrayList<TilePos> toBlowArray = new ArrayList<>();
        toBlowArray.addAll(this.toBlow);

        if (this.fire) {
            for (int j = toBlowArray.size() - 1; j >= 0; --j) {
                final TilePos tp = toBlowArray.get(j);
                final int xt = tp.x;
                final int yt = tp.y;
                final int zt = tp.z;
                final int t = this.level.getTile(xt, yt, zt);
                final int b = this.level.getTile(xt, yt - 1, zt);
                if (t == 0 && Tile.solid[b] && this.random.nextInt(3) == 0) {
                    this.level.setTile(xt, yt, zt, Tile.fire.id);
                }
            }
        }
    }
    
    public void finalizeExplosion(final boolean generateParticles) {
        this.level.playLocalSound(this.x, this.y, this.z, "random.explode", 4.0f, (1.0f + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2f) * 0.7f);

        final ArrayList<TilePos> toBlowArray = new ArrayList<>();
        toBlowArray.addAll(this.toBlow);
        for (int j = toBlowArray.size() - 1; j >= 0; --j) {
            final TilePos tp = toBlowArray.get(j);
            final int xt = tp.x;
            final int yt = tp.y;
            final int zt = tp.z;
            final int t = this.level.getTile(xt, yt, zt);

            if (generateParticles) {
                final double xa = xt + this.level.random.nextFloat();
                final double ya = yt + this.level.random.nextFloat();
                final double za = zt + this.level.random.nextFloat();

                double xd = xa - this.x;
                double yd = ya - this.y;
                double zd = za - this.z;

                double dd = Mth.sqrt(xd * xd + yd * yd + zd * zd);

                xd /= dd;
                yd /= dd;
                zd /= dd;

                double speed = 0.5 / (dd / this.r + 0.1);
                speed *= (this.level.random.nextFloat() * this.level.random.nextFloat() + 0.3f);
                xd *= speed;
                yd *= speed;
                zd *= speed;

                this.level.addParticle("explode", (xa + this.x * 1.0) / 2.0, (ya + this.y * 1.0) / 2.0, (za + this.z * 1.0) / 2.0, xd, yd, zd);
                this.level.addParticle("smoke", xa, ya, za, xd, yd, zd);
            }

            if (t > 0) {
                Tile.tiles[t].spawnResources(this.level, xt, yt, zt, this.level.getData(xt, yt, zt), 0.3f);
                this.level.setTile(xt, yt, zt, 0);
                Tile.tiles[t].wasExploded(this.level, xt, yt, zt);
            }
        }
    }
}
