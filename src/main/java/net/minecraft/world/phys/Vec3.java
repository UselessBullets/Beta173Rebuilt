// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.phys;

import java.util.ArrayList;
import util.Mth;
import java.util.List;

public class Vec3
{
    private static List<Vec3> pool;
    private static int poolPointer;
    public double x;
    public double y;
    public double z;
    
    public static Vec3 newPermanent(final double x, final double y, final double z) {
        return new Vec3(x, y, z);
    }
    
    public static void clearPool() {
        Vec3.pool.clear();
        Vec3.poolPointer = 0;
    }
    
    public static void resetPool() {
        Vec3.poolPointer = 0;
    }
    
    public static Vec3 newTemp(final double x, final double y, final double z) {
        if (Vec3.poolPointer >= Vec3.pool.size()) {
            Vec3.pool.add(newPermanent(0.0, 0.0, 0.0));
        }
        return Vec3.pool.get(Vec3.poolPointer++).set(x, y, z);
    }
    
    private Vec3(double x, double y, double z) {
        if (x == -0.0) {
            x = 0.0;
        }
        if (y == -0.0) {
            y = 0.0;
        }
        if (z == -0.0) {
            z = 0.0;
        }
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    private Vec3 set(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
    
    public Vec3 vectorTo(final Vec3 vec3) {
        return newTemp(vec3.x - this.x, vec3.y - this.y, vec3.z - this.z);
    }
    
    public Vec3 normalize() {
        final double n = Mth.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        if (n < 1.0E-4) {
            return newTemp(0.0, 0.0, 0.0);
        }
        return newTemp(this.x / n, this.y / n, this.z / n);
    }
    
    public Vec3 cross(final Vec3 vec3) {
        return newTemp(this.y * vec3.z - this.z * vec3.y, this.z * vec3.x - this.x * vec3.z, this.x * vec3.y - this.y * vec3.x);
    }
    
    public Vec3 add(final double x, final double y, final double z) {
        return newTemp(this.x + x, this.y + y, this.z + z);
    }
    
    public double distanceTo(final Vec3 vec3) {
        final double n = vec3.x - this.x;
        final double n2 = vec3.y - this.y;
        final double n3 = vec3.z - this.z;
        return Mth.sqrt(n * n + n2 * n2 + n3 * n3);
    }
    
    public double distanceToSqr(final Vec3 vec3) {
        final double n = vec3.x - this.x;
        final double n2 = vec3.y - this.y;
        final double n3 = vec3.z - this.z;
        return n * n + n2 * n2 + n3 * n3;
    }
    
    public double distanceToSqr(final double x, final double y, final double z) {
        final double n = x - this.x;
        final double n2 = y - this.y;
        final double n3 = z - this.z;
        return n * n + n2 * n2 + n3 * n3;
    }
    
    public double length() {
        return Mth.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public Vec3 clipX(final Vec3 bec3, final double n) {
        final double n2 = bec3.x - this.x;
        final double n3 = bec3.y - this.y;
        final double n4 = bec3.z - this.z;
        if (n2 * n2 < 1.0000000116860974E-7) {
            return null;
        }
        final double n5 = (n - this.x) / n2;
        if (n5 < 0.0 || n5 > 1.0) {
            return null;
        }
        return newTemp(this.x + n2 * n5, this.y + n3 * n5, this.z + n4 * n5);
    }
    
    public Vec3 clipY(final Vec3 vec3, final double n) {
        final double n2 = vec3.x - this.x;
        final double n3 = vec3.y - this.y;
        final double n4 = vec3.z - this.z;
        if (n3 * n3 < 1.0000000116860974E-7) {
            return null;
        }
        final double n5 = (n - this.y) / n3;
        if (n5 < 0.0 || n5 > 1.0) {
            return null;
        }
        return newTemp(this.x + n2 * n5, this.y + n3 * n5, this.z + n4 * n5);
    }
    
    public Vec3 clipZ(final Vec3 vec3, final double n) {
        final double n2 = vec3.x - this.x;
        final double n3 = vec3.y - this.y;
        final double n4 = vec3.z - this.z;
        if (n4 * n4 < 1.0000000116860974E-7) {
            return null;
        }
        final double n5 = (n - this.z) / n4;
        if (n5 < 0.0 || n5 > 1.0) {
            return null;
        }
        return newTemp(this.x + n2 * n5, this.y + n3 * n5, this.z + n4 * n5);
    }
    
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
    
    public void xRot(final float n) {
        final float cos = Mth.cos(n);
        final float sin = Mth.sin(n);
        final double x = this.x;
        final double y = this.y * cos + this.z * sin;
        final double z = this.z * cos - this.y * sin;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void yRot(final float n) {
        final float cos = Mth.cos(n);
        final float sin = Mth.sin(n);
        final double x = this.x * cos + this.z * sin;
        final double y = this.y;
        final double z = this.z * cos - this.x * sin;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    static {
        Vec3.pool = new ArrayList();
        Vec3.poolPointer = 0;
    }
}
