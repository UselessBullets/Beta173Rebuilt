// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.phys;

import java.util.ArrayList;
import java.util.List;

public class AABB
{
    private static List pool;
    private static int poolPointer;
    public double x0;
    public double y0;
    public double z0;
    public double x1;
    public double y1;
    public double z1;
    
    public static AABB newPermanent(final double x0, final double y0, final double z0, final double x1, final double y1, final double z1) {
        return new AABB(x0, y0, z0, x1, y1, z1);
    }
    
    public static void resetPool() {
        AABB.poolPointer = 0;
    }
    
    public static AABB newTemp(final double x0, final double y0, final double z0, final double x1, final double y1, final double z1) {
        if (AABB.poolPointer >= AABB.pool.size()) {
            AABB.pool.add(newPermanent(0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        }
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
        double x0 = this.x0;
        double y0 = this.y0;
        double z0 = this.z0;
        double x2 = this.x1;
        double y2 = this.y1;
        double z2 = this.z1;
        if (xa < 0.0) {
            x0 += xa;
        }
        if (xa > 0.0) {
            x2 += xa;
        }
        if (ya < 0.0) {
            y0 += ya;
        }
        if (ya > 0.0) {
            y2 += ya;
        }
        if (za < 0.0) {
            z0 += za;
        }
        if (za > 0.0) {
            z2 += za;
        }
        return newTemp(x0, y0, z0, x2, y2, z2);
    }
    
    public AABB grow(final double xa, final double ya, final double za) {
        return newTemp(this.x0 - xa, this.y0 - ya, this.z0 - za, this.x1 + xa, this.y1 + ya, this.z1 + za);
    }
    
    public AABB cloneMove(final double xa, final double ya, final double za) {
        return newTemp(this.x0 + xa, this.y0 + ya, this.z0 + za, this.x1 + xa, this.y1 + ya, this.z1 + za);
    }
    
    public double clipXCollide(final AABB aabb, double xa) {
        if (aabb.y1 <= this.y0 || aabb.y0 >= this.y1) {
            return xa;
        }
        if (aabb.z1 <= this.z0 || aabb.z0 >= this.z1) {
            return xa;
        }
        if (xa > 0.0 && aabb.x1 <= this.x0) {
            final double n = this.x0 - aabb.x1;
            if (n < xa) {
                xa = n;
            }
        }
        if (xa < 0.0 && aabb.x0 >= this.x1) {
            final double n2 = this.x1 - aabb.x0;
            if (n2 > xa) {
                xa = n2;
            }
        }
        return xa;
    }
    
    public double clipYCollide(final AABB aabb, double ya) {
        if (aabb.x1 <= this.x0 || aabb.x0 >= this.x1) {
            return ya;
        }
        if (aabb.z1 <= this.z0 || aabb.z0 >= this.z1) {
            return ya;
        }
        if (ya > 0.0 && aabb.y1 <= this.y0) {
            final double n = this.y0 - aabb.y1;
            if (n < ya) {
                ya = n;
            }
        }
        if (ya < 0.0 && aabb.y0 >= this.y1) {
            final double n2 = this.y1 - aabb.y0;
            if (n2 > ya) {
                ya = n2;
            }
        }
        return ya;
    }
    
    public double clipZCollide(final AABB aabb, double za) {
        if (aabb.x1 <= this.x0 || aabb.x0 >= this.x1) {
            return za;
        }
        if (aabb.y1 <= this.y0 || aabb.y0 >= this.y1) {
            return za;
        }
        if (za > 0.0 && aabb.z1 <= this.z0) {
            final double n = this.z0 - aabb.z1;
            if (n < za) {
                za = n;
            }
        }
        if (za < 0.0 && aabb.z0 >= this.z1) {
            final double n2 = this.z1 - aabb.z0;
            if (n2 > za) {
                za = n2;
            }
        }
        return za;
    }
    
    public boolean intersects(final AABB aabb) {
        return aabb.x1 > this.x0 && aabb.x0 < this.x1 && aabb.y1 > this.y0 && aabb.y0 < this.y1 && aabb.z1 > this.z0 && aabb.z0 < this.z1;
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
    
    public boolean contains(final Vec3 vec3) {
        return vec3.x > this.x0 && vec3.x < this.x1 && vec3.y > this.y0 && vec3.y < this.y1 && vec3.z > this.z0 && vec3.z < this.z1;
    }
    
    public AABB shrink(final double xa, final double ya, final double za) {
        return newTemp(this.x0 + xa, this.y0 + ya, this.z0 + za, this.x1 - xa, this.y1 - ya, this.z1 - za);
    }
    
    public AABB copy() {
        return newTemp(this.x0, this.y0, this.z0, this.x1, this.y1, this.z1);
    }
    
    public HitResult clip(final Vec3 a, final Vec3 b) {
        Vec3 clipX = a.clipX(b, this.x0);
        Vec3 clipX2 = a.clipX(b, this.x1);
        Vec3 clipY = a.clipY(b, this.y0);
        Vec3 clipY2 = a.clipY(b, this.y1);
        Vec3 clipZ = a.clipZ(b, this.z0);
        Vec3 clipZ2 = a.clipZ(b, this.z1);
        if (!this.containsX(clipX)) {
            clipX = null;
        }
        if (!this.containsX(clipX2)) {
            clipX2 = null;
        }
        if (!this.containsY(clipY)) {
            clipY = null;
        }
        if (!this.containsY(clipY2)) {
            clipY2 = null;
        }
        if (!this.containsZ(clipZ)) {
            clipZ = null;
        }
        if (!this.containsZ(clipZ2)) {
            clipZ2 = null;
        }
        Vec3 ba = null;
        if (clipX != null && (ba == null || a.distanceToSqr(clipX) < a.distanceToSqr(ba))) {
            ba = clipX;
        }
        if (clipX2 != null && (ba == null || a.distanceToSqr(clipX2) < a.distanceToSqr(ba))) {
            ba = clipX2;
        }
        if (clipY != null && (ba == null || a.distanceToSqr(clipY) < a.distanceToSqr(ba))) {
            ba = clipY;
        }
        if (clipY2 != null && (ba == null || a.distanceToSqr(clipY2) < a.distanceToSqr(ba))) {
            ba = clipY2;
        }
        if (clipZ != null && (ba == null || a.distanceToSqr(clipZ) < a.distanceToSqr(ba))) {
            ba = clipZ;
        }
        if (clipZ2 != null && (ba == null || a.distanceToSqr(clipZ2) < a.distanceToSqr(ba))) {
            ba = clipZ2;
        }
        if (ba == null) {
            return null;
        }
        int integer4 = -1;
        if (ba == clipX) {
            integer4 = 4;
        }
        if (ba == clipX2) {
            integer4 = 5;
        }
        if (ba == clipY) {
            integer4 = 0;
        }
        if (ba == clipY2) {
            integer4 = 1;
        }
        if (ba == clipZ) {
            integer4 = 2;
        }
        if (ba == clipZ2) {
            integer4 = 3;
        }
        return new HitResult(0, 0, 0, integer4, ba);
    }
    
    private boolean containsX(final Vec3 vec3) {
        return vec3 != null && vec3.y >= this.y0 && vec3.y <= this.y1 && vec3.z >= this.z0 && vec3.z <= this.z1;
    }
    
    private boolean containsY(final Vec3 vec3) {
        return vec3 != null && vec3.x >= this.x0 && vec3.x <= this.x1 && vec3.z >= this.z0 && vec3.z <= this.z1;
    }
    
    private boolean containsZ(final Vec3 vec3) {
        return vec3 != null && vec3.x >= this.x0 && vec3.x <= this.x1 && vec3.y >= this.y0 && vec3.y <= this.y1;
    }
    
    public void set(final AABB aabb) {
        this.x0 = aabb.x0;
        this.y0 = aabb.y0;
        this.z0 = aabb.z0;
        this.x1 = aabb.x1;
        this.y1 = aabb.y1;
        this.z1 = aabb.z1;
    }
    
    @Override
    public String toString() {
        return "box[" + this.x0 + ", " + this.y0 + ", " + this.z0 + " -> " + this.x1 + ", " + this.y1 + ", " + this.z1 + "]";
    }
    
    static {
        AABB.pool = new ArrayList();
        AABB.poolPointer = 0;
    }
}
