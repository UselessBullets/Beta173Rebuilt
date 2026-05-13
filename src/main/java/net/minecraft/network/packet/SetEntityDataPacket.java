// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import net.minecraft.world.entity.SynchedEntityData;
import net.minecraft.world.entity.SynchedEntityData.DataItem;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

public class SetEntityDataPacket extends Packet
{
    public int id;
    private List<DataItem> packedItems;

    public SetEntityDataPacket() {
    }

    public SetEntityDataPacket(final int id, final SynchedEntityData entityData) {
        this.id = id;
        this.packedItems = entityData.packDirty();
    }

    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.id = dis.readInt();
        this.packedItems = SynchedEntityData.unpack(dis);
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
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
