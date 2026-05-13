// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MovePlayerPacket extends Packet
{
    public double x;
    public double y;
    public double z;
    public double yView;
    public float yRot;
    public float xRot;
    public boolean onGround;
    public boolean hasPos;
    public boolean hasRot;
    
    public MovePlayerPacket() {
    }
    
    public MovePlayerPacket(final boolean onGround) {
        this.onGround = onGround;
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleMovePlayer(this);
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.onGround = (dis.read() != 0);
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.write(this.onGround ? 1 : 0);
    }
    
    @Override
    public int getEstimatedSize() {
        return 1;
    }

    public static class Pos extends MovePlayerPacket
    {
        public Pos() {
            this.hasPos = true;
        }

        public Pos(final double x, final double y, final double yView, final double z, final boolean hasPos) {
            this.x = x;
            this.y = y;
            this.yView = yView;
            this.z = z;
            this.onGround = hasPos;
            this.hasPos = true;
        }

        @Override
        public void read(final DataInputStream dis) throws IOException {
            this.x = dis.readDouble();
            this.y = dis.readDouble();
            this.yView = dis.readDouble();
            this.z = dis.readDouble();
            super.read(dis);
        }

        @Override
        public void write(final DataOutputStream dos) throws IOException {
            dos.writeDouble(this.x);
            dos.writeDouble(this.y);
            dos.writeDouble(this.yView);
            dos.writeDouble(this.z);
            super.write(dos);
        }

        @Override
        public int getEstimatedSize() {
            return 33;
        }
    }

    public static class PosRot extends MovePlayerPacket
    {
        public PosRot() {
            this.hasRot = true;
            this.hasPos = true;
        }

        public PosRot(final double x, final double y, final double yView, final double z, final float yRot, final float xRot, final boolean onGround) {
            this.x = x;
            this.y = y;
            this.yView = yView;
            this.z = z;
            this.yRot = yRot;
            this.xRot = xRot;
            this.onGround = onGround;
            this.hasRot = true;
            this.hasPos = true;
        }

        @Override
        public void read(final DataInputStream dis) throws IOException {
            this.x = dis.readDouble();
            this.y = dis.readDouble();
            this.yView = dis.readDouble();
            this.z = dis.readDouble();
            this.yRot = dis.readFloat();
            this.xRot = dis.readFloat();
            super.read(dis);
        }

        @Override
        public void write(final DataOutputStream dos) throws IOException {
            dos.writeDouble(this.x);
            dos.writeDouble(this.y);
            dos.writeDouble(this.yView);
            dos.writeDouble(this.z);
            dos.writeFloat(this.yRot);
            dos.writeFloat(this.xRot);
            super.write(dos);
        }

        @Override
        public int getEstimatedSize() {
            return 41;
        }
    }

    public static class Rot extends MovePlayerPacket
    {
        public Rot() {
            this.hasRot = true;
        }

        public Rot(final float yRot, final float xRot, final boolean onGround) {
            this.yRot = yRot;
            this.xRot = xRot;
            this.onGround = onGround;
            this.hasRot = true;
        }

        @Override
        public void read(final DataInputStream dis) throws IOException {
            this.yRot = dis.readFloat();
            this.xRot = dis.readFloat();
            super.read(dis);
        }

        @Override
        public void write(final DataOutputStream dos) throws IOException {
            dos.writeFloat(this.yRot);
            dos.writeFloat(this.xRot);
            super.write(dos);
        }

        @Override
        public int getEstimatedSize() {
            return 9;
        }
    }
}
