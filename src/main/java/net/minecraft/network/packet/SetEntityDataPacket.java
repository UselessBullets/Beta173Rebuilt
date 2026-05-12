// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import net.minecraft.world.entity.SynchedEntityData;
import java.io.DataInputStream;
import java.util.List;

public class SetEntityDataPacket extends Packet
{
    public int id;
    private List packedItems;
    
    @Override
    public void read(final DataInputStream dis) {
        this.id = dis.readInt();
        this.packedItems = SynchedEntityData.unpack(dis);
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.id);
        SynchedEntityData.pack(this.packedItems, dos);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleSetEntityData(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 5;
    }
    
    public List getUnpackedData() {
        return this.packedItems;
    }
}
