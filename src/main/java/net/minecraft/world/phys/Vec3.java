// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.phys;

import java.util.ArrayList;
import util.Mth;
import java.util.List;

public class Vec3
{
    private static List<Vec3> pool = new ArrayList<>();
    private static int poolPointer = 0;
    public double x, y, z;
    
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
        if (Vec3.poolPointer >= Vec3.pool.size()) Vec3.pool.add(newPermanent(0.0, 0.0, 0.0));
        return Vec3.pool.get(Vec3.poolPointer++).set(x, y, z);
    }
    
    private Vec3(double x, double y, double z) {
        if (x == -0.0) x = 0.0;
        if (y == -0.0) y = 0.0;
        if (z == -0.0) z = 0.0;
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

    // Useless - Exists in b1.2 and LCE leaks
    public Vec3 interpolateTo(Vec3 t, double p) {
        double xt = this.x + (t.x - this.x) * p;
        double yt = this.y + (t.y - this.y) * p;
        double zt = this.z + (t.z - this.z) * p;

        return newTemp(xt, yt, zt);
    }
    
    public Vec3 vectorTo(final Vec3 p) {
        return newTemp(p.x - this.x, p.y - this.y, p.z - this.z);
    }
    
    public Vec3 normalize() {
        final double dist = Mth.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        if (dist < 0.0001) return newTemp(0.0, 0.0, 0.0);
        return newTemp(this.x / dist, this.y / dist, this.z / dist);
    }

    // Useless - Exists in b1.2 and LCE leaks
    public double dot(Vec3 p) {
        return this.x * p.x + this.y * p.y + this.z * p.z;
    }
    
    public Vec3 cross(final Vec3 p) {
        return newTemp(this.y * p.z - this.z * p.y, this.z * p.x - this.x * p.z, this.x * p.y - this.y * p.x);
    }
    
    public Vec3 add(final double x, final double y, final double z) {
        return newTemp(this.x + x, this.y + y, this.z + z);
    }
    
    public double distanceTo(final Vec3 p) {
        final double xd = p.x - this.x;
        final double yd = p.y - this.y;
        final double zd = p.z - this.z;
        return Mth.sqrt(xd * xd + yd * yd + zd * zd);
    }
    
    public double distanceToSqr(final Vec3 p) {
        final double xd = p.x - this.x;
        final double yd = p.y - this.y;
        final double zd = p.z - this.z;
        return xd * xd + yd * yd + zd * zd;
    }
    
    public double distanceToSqr(final double x, final double y, final double z) {
        final double xd = x - this.x;
        final double yd = y - this.y;
        final double zd = z - this.z;
        return xd * xd + yd * yd + zd * zd;
    }

    // Useless - Exists in b1.2 and LCE leaks
    public Vec3 scale(double l) {
        return newTemp(this.x * l, this.y * l, this.z * l);
    }

    public double length() {
        return Mth.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public Vec3 clipX(final Vec3 bec3, final double n) {
        final double xd = bec3.x - this.x;
        final double yd = bec3.y - this.y;
        final double zd = bec3.z - this.z;

        if (xd * xd < 0.0000001f) return null;

        final double d = (n - this.x) / xd;
        if (d < 0.0 || d > 1.0) return null;
        return newTemp(this.x + xd * d, this.y + yd * d, this.z + zd * d);
    }
    
    public Vec3 clipY(final Vec3 vec3, final double n) {
        final double xd = vec3.x - this.x;
        final double yd = vec3.y - this.y;
        final double zd = vec3.z - this.z;

        if (yd * yd < 0.0000001f) return null;

        final double d = (n - this.y) / yd;
        if (d < 0.0 || d > 1.0) return null;
        return newTemp(this.x + xd * d, this.y + yd * d, this.z + zd * d);
    }
    
    public Vec3 clipZ(final Vec3 vec3, final double n) {
        final double xd = vec3.x - this.x;
        final double yd = vec3.y - this.y;
        final double zd = vec3.z - this.z;

        if (zd * zd < 0.0000001f) return null;

        final double d = (n - this.z) / zd;
        if (d < 0.0 || d > 1.0) return null;
        return newTemp(this.x + xd * d, this.y + yd * d, this.z + zd * d);
    }
    
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    // Useless - Exists in b1.2 and LCE leaks
    public Vec3 lerp(Vec3 v, double a) {
        return newTemp(this.x + (v.x - this.x) * a, this.y + (v.y - this.y) * a, this.z + (v.z - this.z) * a);
    }

    public void xRot(final float degs) {
        final float _cos = Mth.cos(degs);
        final float _sin = Mth.sin(degs);

        final double x = this.x;
        final double y = this.y * _cos + this.z * _sin;
        final double z = this.z * _cos - this.y * _sin;

        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void yRot(final float degs) {
        final float _cos = Mth.cos(degs);
        final float _sin = Mth.sin(degs);

        final double x = this.x * _cos + this.z * _sin;
        final double y = this.y;
        final double z = this.z * _cos - this.x * _sin;

        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Useless - Exists in b1.2 and LCE leaks
    public void zRot(float degs) {
        float _cos = Mth.cos(degs);
        float _sin = Mth.sin(degs);

        double x = this.x * _cos + this.y * _sin;
        double y = this.y * _cos - this.x * _sin;
        double z = this.z;

        this.x = x;
        this.y = y;
        this.z = z;
    }

}
