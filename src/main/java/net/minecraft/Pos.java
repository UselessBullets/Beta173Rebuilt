// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft;

public class Pos implements Comparable<Pos> {
    public int x, y, z;

    public Pos() {
    }

    public Pos(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Pos(final Pos pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Pos)) return false;

        final Pos pos = (Pos) o;
        return this.x == pos.x && this.y == pos.y && this.z == pos.z;
    }

    @Override
    public int hashCode() {
        return this.x + this.z << 8 + this.y << 16;
    }

    public int compareTo(final Pos pos) {
        if (this.y != pos.y) return this.y - pos.y;
        if (this.z == pos.z) return this.x - pos.x;
        return this.z - pos.z;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    Pos offset(int x, int y, int z) {
        return new Pos(this.x + x, this.y + y, this.z + z);
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void set(Pos pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    Pos above() {
        return new Pos(this.x, this.y + 1, this.z);
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    Pos above(int steps) {
        return new Pos(this.x, this.y + steps, this.z);
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    Pos below() {
        return new Pos(this.x, this.y - 1, this.z);
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    Pos below(int steps) {
        return new Pos(this.x, this.y - steps, this.z);
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    Pos north() {
        return new Pos(this.x, this.y, this.z - 1);
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    Pos north(int steps) {
        return new Pos(this.x, this.y, this.z - steps);
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    Pos south() {
        return new Pos(this.x, this.y, this.z + 1);
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    Pos south(int steps) {
        return new Pos(this.x, this.y, this.z + steps);
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    Pos west() {
        return new Pos(this.x - 1, this.y, this.z);
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    Pos west(int steps) {
        return new Pos(this.x - steps, this.y, this.z);
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    Pos east() {
        return new Pos(this.x + 1, this.y, this.z);
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    Pos east(int steps) {
        return new Pos(this.x + steps, this.y, this.z);
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void move(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void move(Pos pos) {
        this.x += pos.x;
        this.y += pos.y;
        this.z += pos.z;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void moveX(int steps) {
        this.x += steps;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void moveY(int steps) {
        this.y += steps;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void moveZ(int steps) {
        this.z += steps;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void moveUp(int steps) {
        this.y += steps;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void moveUp() {
        this.y++;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void moveDown(int steps) {
        this.y -= steps;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void moveDown() {
        this.y--;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void moveEast(int steps) {
        this.x += steps;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void moveEast() {
        this.x++;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void moveWest(int steps) {
        this.x -= steps;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void moveWest() {
        this.x--;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void moveNorth(int steps) {
        this.z -= steps;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void moveNorth() {
        this.z--;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void moveSouth(int steps) {
        this.z += steps;
    }

    // Useless - In LCE and b1.2 leaks, stripped by proguard
    void moveSouth() {
        this.z++;
    }

    public double dist(final int x, final int y, final int z) {
        final int dx = this.x - x;
        final int dy = this.y - y;
        final int dz = this.z - z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }


    // Useless - In LCE, presumably stripped by proguard
    double dist(Pos pos) {
        return dist(pos.x, pos.y, pos.z);
    }

    // Useless - In LCE, presumably stripped by proguard
    float distSqr(int x, int y, int z) {
        int dx = this.x - x;
        int dy = this.y - y;
        int dz = this.z - z;
        return dx * dx + dy * dy + dz * dz;
    }
}
