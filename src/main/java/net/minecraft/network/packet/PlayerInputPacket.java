// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PlayerInputPacket extends Packet
{
    private float xa;
    private float ya;
    private boolean isJumping;
    private boolean isSneaking;
    private float xRot;
    private float yRot;
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.xa = dis.readFloat();
        this.ya = dis.readFloat();
        this.xRot = dis.readFloat();
        this.yRot = dis.readFloat();
        this.isJumping = dis.readBoolean();
        this.isSneaking = dis.readBoolean();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeFloat(this.xa);
        dos.writeFloat(this.ya);
        dos.writeFloat(this.xRot);
        dos.writeFloat(this.yRot);
        dos.writeBoolean(this.isJumping);
        dos.writeBoolean(this.isSneaking);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handlePlayerInput(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 18;
    }

    public float getXa() {
        return this.xa;
    }

    public float getXRot() {
        return this.xRot;
    }

    public float getYa() {
        return this.ya;
    }

    public float getYRot() {
        return this.yRot;
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public boolean isSneaking() {
        return this.isSneaking;
    }
}
