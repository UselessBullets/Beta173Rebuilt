// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.phys;

import java.util.ArrayList;
import util.Mth;
import java.util.List;

public class Vec3
{
    private static List d;
    private static int e;
    public double x;
    public double y;
    public double z;
    
    public static Vec3 a(final double double1, final double double2, final double double3) {
        return new Vec3(double1, double2, double3);
    }
    
    public static void resetPool() {
        Vec3.e = 0;
    }
    
    public static Vec3 newTemp(final double double1, final double double2, final double double3) {
        if (Vec3.e >= Vec3.d.size()) {
            Vec3.d.add(a(0.0, 0.0, 0.0));
        }
        return Vec3.d.get(Vec3.e++).e(double1, double2, double3);
    }
    
    private Vec3(double double1, double double2, double double3) {
        if (double1 == -0.0) {
            double1 = 0.0;
        }
        if (double2 == -0.0) {
            double2 = 0.0;
        }
        if (double3 == -0.0) {
            double3 = 0.0;
        }
        this.x = double1;
        this.y = double2;
        this.z = double3;
    }
    
    private Vec3 e(final double double1, final double double2, final double double3) {
        this.x = double1;
        this.y = double2;
        this.z = double3;
        return this;
    }
    
    public Vec3 normalize() {
        final double n = Mth.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        if (n < 1.0E-4) {
            return newTemp(0.0, 0.0, 0.0);
        }
        return newTemp(this.x / n, this.y / n, this.z / n);
    }
    
    public Vec3 add(final double double1, final double double2, final double double3) {
        return newTemp(this.x + double1, this.y + double2, this.z + double3);
    }
    
    public double distanceTo(final Vec3 ba) {
        final double n = ba.x - this.x;
        final double n2 = ba.y - this.y;
        final double n3 = ba.z - this.z;
        return Mth.sqrt(n * n + n2 * n2 + n3 * n3);
    }
    
    public double distanceToSqr(final Vec3 ba) {
        final double n = ba.x - this.x;
        final double n2 = ba.y - this.y;
        final double n3 = ba.z - this.z;
        return n * n + n2 * n2 + n3 * n3;
    }
    
    public double distanceToSqr(final double double1, final double double2, final double double3) {
        final double n = double1 - this.x;
        final double n2 = double2 - this.y;
        final double n3 = double3 - this.z;
        return n * n + n2 * n2 + n3 * n3;
    }
    
    public double length() {
        return Mth.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public Vec3 clipX(final Vec3 ba, final double double2) {
        final double n = ba.x - this.x;
        final double n2 = ba.y - this.y;
        final double n3 = ba.z - this.z;
        if (n * n < 1.0000000116860974E-7) {
            return null;
        }
        final double n4 = (double2 - this.x) / n;
        if (n4 < 0.0 || n4 > 1.0) {
            return null;
        }
        return newTemp(this.x + n * n4, this.y + n2 * n4, this.z + n3 * n4);
    }
    
    public Vec3 clipY(final Vec3 ba, final double double2) {
        final double n = ba.x - this.x;
        final double n2 = ba.y - this.y;
        final double n3 = ba.z - this.z;
        if (n2 * n2 < 1.0000000116860974E-7) {
            return null;
        }
        final double n4 = (double2 - this.y) / n2;
        if (n4 < 0.0 || n4 > 1.0) {
            return null;
        }
        return newTemp(this.x + n * n4, this.y + n2 * n4, this.z + n3 * n4);
    }
    
    public Vec3 clipZ(final Vec3 ba, final double double2) {
        final double n = ba.x - this.x;
        final double n2 = ba.y - this.y;
        final double n3 = ba.z - this.z;
        if (n3 * n3 < 1.0000000116860974E-7) {
            return null;
        }
        final double n4 = (double2 - this.z) / n3;
        if (n4 < 0.0 || n4 > 1.0) {
            return null;
        }
        return newTemp(this.x + n * n4, this.y + n2 * n4, this.z + n3 * n4);
    }
    
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
    
    static {
        Vec3.d = new ArrayList();
        Vec3.e = 0;
    }
}
