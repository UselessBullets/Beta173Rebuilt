// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import net.minecraft.world.entity.Painting_Motive;
import java.io.DataInputStream;
import net.minecraft.world.entity.Painting;

public class AddPaintingPacket extends Packet
{
    public int id;
    public int x;
    public int y;
    public int z;
    public int dir;
    public String motive;
    
    public AddPaintingPacket() {
    }
    
    public AddPaintingPacket(final Painting painting) {
        this.id = painting.entityId;
        this.x = painting.xTile;
        this.y = painting.yTile;
        this.z = painting.zTile;
        this.dir = painting.dir;
        this.motive = painting.motive.name;
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.id = dis.readInt();
        this.motive = Packet.readUTF(dis, Painting_Motive.MAX_MOTIVE_NAME_LENGTH);
        this.x = dis.readInt();
        this.y = dis.readInt();
        this.z = dis.readInt();
        this.dir = dis.readInt();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.id);
        Packet.writeUTF(this.motive, dos);
        dos.writeInt(this.x);
        dos.writeInt(this.y);
        dos.writeInt(this.z);
        dos.writeInt(this.dir);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleAddPainting(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 24;
    }
}
