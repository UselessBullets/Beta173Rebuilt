// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MoveEntityPacket extends Packet
{
    public int id;
    public byte xa;
    public byte ya;
    public byte za;
    public byte yRot;
    public byte xRot;
    public boolean hasRot;
    
    public MoveEntityPacket() {
        this.hasRot = false;
    }

    public MoveEntityPacket(final int id) {
        this.hasRot = false;
        this.id = id;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.id = dis.readInt();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.id);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleMoveEntity(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 4;
    }

    public static class PosRot extends MoveEntityPacket
    {
        public PosRot() {
            this.hasRot = true;
        }

        public PosRot(final int id, final byte xa, final byte ya, final byte za, final byte yRot, final byte xRot) {
            super(id);
            this.xa = xa;
            this.ya = ya;
            this.za = za;
            this.yRot = yRot;
            this.xRot = xRot;
            this.hasRot = true;
        }

        @Override
        public void read(final DataInputStream dis) throws IOException {
            super.read(dis);
            this.xa = dis.readByte();
            this.ya = dis.readByte();
            this.za = dis.readByte();
            this.yRot = dis.readByte();
            this.xRot = dis.readByte();
        }

        @Override
        public void write(final DataOutputStream dos) throws IOException {
            super.write(dos);
            dos.writeByte(this.xa);
            dos.writeByte(this.ya);
            dos.writeByte(this.za);
            dos.writeByte(this.yRot);
            dos.writeByte(this.xRot);
        }

        @Override
        public int getEstimatedSize() {
            return 9;
        }
    }

    public static class Pos extends MoveEntityPacket
    {

        public Pos() {
        }

        public Pos(final int id, final byte xa, final byte ya, final byte za) {
            super(id);
            this.xa = xa;
            this.ya = ya;
            this.za = za;
        }

        @Override
        public void read(final DataInputStream dis) throws IOException {
            super.read(dis);
            this.xa = dis.readByte();
            this.ya = dis.readByte();
            this.za = dis.readByte();
        }

        @Override
        public void write(final DataOutputStream dos) throws IOException {
            super.write(dos);
            dos.writeByte(this.xa);
            dos.writeByte(this.ya);
            dos.writeByte(this.za);
        }

        @Override
        public int getEstimatedSize() {
            return 7;
        }
    }

    public static class Rot extends MoveEntityPacket
    {
        public Rot() {
            this.hasRot = true;
        }

        public Rot(final int id, final byte yRot, final byte xRot) {
            super(id);
            this.yRot = yRot;
            this.xRot = xRot;
            this.hasRot = true;
        }

        @Override
        public void read(final DataInputStream dis) throws IOException {
            super.read(dis);
            this.yRot = dis.readByte();
            this.xRot = dis.readByte();
        }

        @Override
        public void write(final DataOutputStream dos) throws IOException {
            super.write(dos);
            dos.writeByte(this.yRot);
            dos.writeByte(this.xRot);
        }

        @Override
        public int getEstimatedSize() {
            return 6;
        }
    }
}
