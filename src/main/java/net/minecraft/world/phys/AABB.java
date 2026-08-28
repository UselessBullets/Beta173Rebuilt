// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.phys;

import java.util.ArrayList;
import java.util.List;

public class AABB
{
    private static List<AABB> pool = new ArrayList<>();
    private static int poolPointer = 0;
    public double x0, y0, z0;
    public double x1, y1, z1;
    
    public static AABB newPermanent(final double x0, final double y0, final double z0, final double x1, final double y1, final double z1) {
        return new AABB(x0, y0, z0, x1, y1, z1);
    }
    
    public static void clearPool() {
        AABB.pool.clear();
        AABB.poolPointer = 0;
    }
    
    public static void resetPool() {
        AABB.poolPointer = 0;
    }
    
    public static AABB newTemp(final double x0, final double y0, final double z0, final double x1, final double y1, final double z1) {
        if (AABB.poolPointer >= AABB.pool.size()) AABB.pool.add(newPermanent(0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        return AABB.pool.get(AABB.poolPointer++).set(x0, y0, z0, x1, y1, z1);
    }
    
    private AABB(final double x0, final double y0, final double z0, final double x1, final double y1, final double z1) {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
    }
    
    public AABB set(final double x0, final double y0, final double z0, final double x1, final double y1, final double z1) {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        return this;
    }
    
    public AABB expand(final double xa, final double ya, final double za) {
        double _x0 = this.x0;
        double _y0 = this.y0;
        double _z0 = this.z0;
        double _x1 = this.x1;
        double _y1 = this.y1;
        double _z1 = this.z1;

        if (xa < 0.0) _x0 += xa;
        if (xa > 0.0) _x1 += xa;

        if (ya < 0.0) _y0 += ya;
        if (ya > 0.0) _y1 += ya;

        if (za < 0.0) _z0 += za;
        if (za > 0.0) _z1 += za;

        return newTemp(_x0, _y0, _z0, _x1, _y1, _z1);
    }
    
    public AABB grow(final double xa, final double ya, final double za) {
        double _x0 = this.x0 - xa;
        double _y0 = this.y0 - ya;
        double _z0 = this.z0 - za;
        double _x1 = this.x1 + xa;
        double _y1 = this.y1 + ya;
        double _z1 = this.z1 + za;

        return newTemp(_x0, _y0, _z0, _x1, _y1, _z1);
    }
    
    public AABB cloneMove(final double xa, final double ya, final double za) {
        return newTemp(this.x0 + xa, this.y0 + ya, this.z0 + za, this.x1 + xa, this.y1 + ya, this.z1 + za);
    }
    
    public double clipXCollide(final AABB c, double xa) {
        if (c.y1 <= this.y0 || c.y0 >= this.y1) return xa;
        if (c.z1 <= this.z0 || c.z0 >= this.z1) return xa;

        if (xa > 0.0 && c.x1 <= this.x0) {
            final double max = this.x0 - c.x1;
            if (max < xa) xa = max;
        }
        if (xa < 0.0 && c.x0 >= this.x1) {
            final double max = this.x1 - c.x0;
            if (max > xa) xa = max;
        }

        return xa;
    }
    
    public double clipYCollide(final AABB c, double ya) {
        if (c.x1 <= this.x0 || c.x0 >= this.x1) return ya;
        if (c.z1 <= this.z0 || c.z0 >= this.z1) return ya;

        if (ya > 0.0 && c.y1 <= this.y0) {
            final double max = this.y0 - c.y1;
            if (max < ya) ya = max;
        }
        if (ya < 0.0 && c.y0 >= this.y1) {
            final double max = this.y1 - c.y0;
            if (max > ya) ya = max;
        }

        return ya;
    }
    
    public double clipZCollide(final AABB c, double za) {
        if (c.x1 <= this.x0 || c.x0 >= this.x1) return za;
        if (c.y1 <= this.y0 || c.y0 >= this.y1) return za;

        if (za > 0.0 && c.z1 <= this.z0) {
            final double max = this.z0 - c.z1;
            if (max < za) za = max;
        }
        if (za < 0.0 && c.z0 >= this.z1) {
            final double max = this.z1 - c.z0;
            if (max > za) za = max;
        }

        return za;
    }
    
    public boolean intersects(final AABB c) {
        if (c.x1 <= this.x0 || c.x0 >= this.x1) return false;
        if (c.y1 <= this.y0 || c.y0 >= this.y1) return false;
        if (c.z1 <= this.z0 || c.z0 >= this.z1) return false;
        return true;
    }

    // Useless - Exists in b1.2 and LCE leaks
    public boolean intersectsInner(AABB c) {
        if (c.x1 < this.x0 || c.x0 > this.x1) return false;
        if (c.y1 < this.y0 || c.y0 > this.y1) return false;
        if (c.z1 < this.z0 || c.z0 > this.z1) return false;
        return true;
    }
    
    public AABB move(final double xa, final double ya, final double za) {
        this.x0 += xa;
        this.y0 += ya;
        this.z0 += za;
        this.x1 += xa;
        this.y1 += ya;
        this.z1 += za;
        return this;
    }

    // Useless - Exists in b1.2 and LCE leaks
    public boolean intersects(double x02, double y02, double z02, double x12, double y12, double z12) {
        if (x12 <= this.x0 || x02 >= this.x1) return false;
        if (y12 <= this.y0 || y02 >= this.y1) return false;
        if (z12 <= this.z0 || z02 >= this.z1) return false;
        return true;
    }
    
    public boolean contains(final Vec3 p) {
        if (p.x <= this.x0 || p.x >= this.x1) return false;
        if (p.y <= this.y0 || p.y >= this.y1) return false;
        if (p.z <= this.z0 || p.z >= this.z1) return false;
        return true;
    }
    
    public double getSize() {
        double xs = this.x1 - this.x0;
        double ys = this.y1 - this.y0;
        double zs = this.z1 - this.z0;
        return (xs + ys + zs) / 3.0;
    }
    
    public AABB shrink(final double xa, final double ya, final double za) {
        double _x0 = this.x0 + xa;
        double _y0 = this.y0 + ya;
        double _z0 = this.z0 + za;
        double _x1 = this.x1 - xa;
        double _y1 = this.y1 - ya;
        double _z1 = this.z1 - za;

        return newTemp(_x0, _y0, _z0, _x1, _y1, _z1);
    }
    
    public AABB copy() {
        return newTemp(this.x0, this.y0, this.z0, this.x1, this.y1, this.z1);
    }
    
    public HitResult clip(final Vec3 a, final Vec3 b) {
        Vec3 xh0 = a.clipX(b, this.x0);
        Vec3 xh1 = a.clipX(b, this.x1);

        Vec3 yh0 = a.clipY(b, this.y0);
        Vec3 yh1 = a.clipY(b, this.y1);

        Vec3 zh0 = a.clipZ(b, this.z0);
        Vec3 zh1 = a.clipZ(b, this.z1);

        if (!this.containsX(xh0)) xh0 = null;
        if (!this.containsX(xh1)) xh1 = null;
        if (!this.containsY(yh0)) yh0 = null;
        if (!this.containsY(yh1)) yh1 = null;
        if (!this.containsZ(zh0)) zh0 = null;
        if (!this.containsZ(zh1)) zh1 = null;

        Vec3 closest = null;
        if (xh0 != null && (closest == null || a.distanceToSqr(xh0) < a.distanceToSqr(closest))) closest = xh0;
        if (xh1 != null && (closest == null || a.distanceToSqr(xh1) < a.distanceToSqr(closest))) closest = xh1;
        if (yh0 != null && (closest == null || a.distanceToSqr(yh0) < a.distanceToSqr(closest))) closest = yh0;
        if (yh1 != null && (closest == null || a.distanceToSqr(yh1) < a.distanceToSqr(closest))) closest = yh1;
        if (zh0 != null && (closest == null || a.distanceToSqr(zh0) < a.distanceToSqr(closest))) closest = zh0;
        if (zh1 != null && (closest == null || a.distanceToSqr(zh1) < a.distanceToSqr(closest))) closest = zh1;

        if (closest == null) return null;

        int face = -1;
        if (closest == xh0) face = 4;
        if (closest == xh1) face = 5;
        if (closest == yh0) face = 0;
        if (closest == yh1) face = 1;
        if (closest == zh0) face = 2;
        if (closest == zh1) face = 3;

        return new HitResult(0, 0, 0, face, closest);
    }
    
    private boolean containsX(final Vec3 v) {
        if (v == null) return false;
        return v.y >= this.y0 && v.y <= this.y1 && v.z >= this.z0 && v.z <= this.z1;
    }
    
    private boolean containsY(final Vec3 v) {
        if (v == null) return false;
        return v.x >= this.x0 && v.x <= this.x1 && v.z >= this.z0 && v.z <= this.z1;
    }
    
    private boolean containsZ(final Vec3 v) {
        if (v == null) return false;
        return v.x >= this.x0 && v.x <= this.x1 && v.y >= this.y0 && v.y <= this.y1;
    }
    
    public void set(final AABB b) {
        this.x0 = b.x0;
        this.y0 = b.y0;
        this.z0 = b.z0;
        this.x1 = b.x1;
        this.y1 = b.y1;
        this.z1 = b.z1;
    }
    
    @Override
    public String toString() {
        return "box[" + this.x0 + ", " + this.y0 + ", " + this.z0 + " -> " + this.x1 + ", " + this.y1 + ", " + this.z1 + "]";
    }

}
