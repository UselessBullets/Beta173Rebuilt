package net.minecraft.world.entity;

// Useless - Class existed in b1.2 and LCE leaks
public class EntityPos {
    public double x, y, z;
    public float yRot, xRot;
    public boolean rot = false;
    public boolean move = false;

    public EntityPos(double x, double y, double z, float yRot, float xRot) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yRot = yRot;
        this.xRot = xRot;
        this.rot = true;
        this.move = true;
    }

    public EntityPos(double x, double y, double z) {
        this.yRot = this.xRot = 0.0f;

        this.x = x;
        this.y = y;
        this.z = z;
        this.move = true;
        this.rot = false;
    }

    public EntityPos(float yRot, float xRot) {
        this.x = this.y = this.z = 0.0;

        this.yRot = yRot;
        this.xRot = xRot;
        this.rot = true;
        this.move = false;
    }

    public EntityPos lerp(Entity e, float f) {
        double xd = e.x + (this.x - e.x) * f;
        double yd = e.y + (this.y - e.y) * f;
        double zd = e.z + (this.z - e.z) * f;

        float yrdd = this.yRot - e.yRot;
        float xrdd = this.xRot - e.xRot;

        while (yrdd >= 180.0F) yrdd -= 360.0F;
        while (yrdd < -180.0F) yrdd += 360.0F;
        while (xrdd >= 180.0F) xrdd -= 360.0F;
        while (xrdd < -180.0F) xrdd += 360.0F;

        float yrd = e.yRot + yrdd * f;
        float xrd = e.xRot + xrdd * f;

        while (yrd >= 180.0F) yrd -= 360.0F;
        while (yrd < -180.0F) yrd += 360.0F;
        while (xrd >= 180.0F) xrd -= 360.0F;
        while (xrd < -180.0F) xrd += 360.0F;

        if (this.rot && this.move) return new EntityPos(xd, yd, zd, yrd, xrd);
        if (this.move) return new EntityPos(xd, yd, zd);
        if (this.rot) return new EntityPos(yrd, xrd);
        return null;
    }
}
