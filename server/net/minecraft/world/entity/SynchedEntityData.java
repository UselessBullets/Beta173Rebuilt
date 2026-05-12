// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity;

import java.io.DataInputStream;
import net.minecraft.Pos;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.network.packet.Packet;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SynchedEntityData
{
    private static final HashMap typeToConstant;
    private final Map itemsById;
    private boolean isDirty;
    
    public SynchedEntityData() {
        this.itemsById = new HashMap();
    }
    
    public void define(final int id, final Object value) {
        final Integer n = SynchedEntityData.typeToConstant.get(value.getClass());
        if (n == null) {
            throw new IllegalArgumentException("Unknown data type: " + value.getClass());
        }
        if (id > 31) {
            throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + 31 + ")");
        }
        if (this.itemsById.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate id value for " + id + "!");
        }
        this.itemsById.put(id, new SynchedEntityData_DataItem(n, id, value));
    }
    
    public byte getByte(final int id) {
        return (byte)this.itemsById.get(id).getValue();
    }
    
    public int getInteger(final int id) {
        return (int)this.itemsById.get(id).getValue();
    }
    
    public String getString(final int id) {
        return (String)this.itemsById.get(id).getValue();
    }
    
    public void set(final int id, final Object value) {
        final SynchedEntityData_DataItem synchedEntityData_DataItem = this.itemsById.get(id);
        if (!value.equals(synchedEntityData_DataItem.getValue())) {
            synchedEntityData_DataItem.setValue(value);
            synchedEntityData_DataItem.setDirty(true);
            this.isDirty = true;
        }
    }
    
    public boolean isDirty() {
        return this.isDirty;
    }
    
    public static void pack(final List items, final DataOutputStream output) {
        if (items != null) {
            final Iterator iterator = items.iterator();
            while (iterator.hasNext()) {
                writeDataItem(output, (SynchedEntityData_DataItem)iterator.next());
            }
        }
        output.writeByte(127);
    }
    
    public ArrayList packDirty() {
        ArrayList<SynchedEntityData_DataItem> list = null;
        if (this.isDirty) {
            for (final SynchedEntityData_DataItem e : this.itemsById.values()) {
                if (e.isDirty()) {
                    e.setDirty(false);
                    if (list == null) {
                        list = new ArrayList<SynchedEntityData_DataItem>();
                    }
                    list.add(e);
                }
            }
        }
        this.isDirty = false;
        return list;
    }
    
    public void packAll(final DataOutputStream output) {
        final Iterator iterator = this.itemsById.values().iterator();
        while (iterator.hasNext()) {
            writeDataItem(output, (SynchedEntityData_DataItem)iterator.next());
        }
        output.writeByte(127);
    }
    
    private static void writeDataItem(final DataOutputStream output, final SynchedEntityData_DataItem dataItem) {
        output.writeByte((dataItem.getType() << 5 | (dataItem.getId() & 0x1F)) & 0xFF);
        switch (dataItem.getType()) {
            case 0: {
                output.writeByte((byte)dataItem.getValue());
                break;
            }
            case 1: {
                output.writeShort((short)dataItem.getValue());
                break;
            }
            case 2: {
                output.writeInt((int)dataItem.getValue());
                break;
            }
            case 3: {
                output.writeFloat((float)dataItem.getValue());
                break;
            }
            case 4: {
                Packet.writeUTF((String)dataItem.getValue(), output);
                break;
            }
            case 5: {
                final ItemInstance itemInstance = (ItemInstance)dataItem.getValue();
                output.writeShort(itemInstance.getItem().id);
                output.writeByte(itemInstance.count);
                output.writeShort(itemInstance.getAuxValue());
                break;
            }
            case 6: {
                final Pos pos = (Pos)dataItem.getValue();
                output.writeInt(pos.x);
                output.writeInt(pos.y);
                output.writeInt(pos.z);
                break;
            }
        }
    }
    
    public static List unpack(final DataInputStream input) {
        ArrayList<Object> list = null;
        for (byte b = input.readByte(); b != 127; b = input.readByte()) {
            if (list == null) {
                list = new ArrayList<Object>();
            }
            final int type = (b & 0xE0) >> 5;
            final int id = b & 0x1F;
            Object e = null;
            switch (type) {
                case 0: {
                    e = new SynchedEntityData_DataItem(type, id, input.readByte());
                    break;
                }
                case 1: {
                    e = new SynchedEntityData_DataItem(type, id, input.readShort());
                    break;
                }
                case 2: {
                    e = new SynchedEntityData_DataItem(type, id, input.readInt());
                    break;
                }
                case 3: {
                    e = new SynchedEntityData_DataItem(type, id, input.readFloat());
                    break;
                }
                case 4: {
                    e = new SynchedEntityData_DataItem(type, id, Packet.readUTF(input, 64));
                    break;
                }
                case 5: {
                    e = new SynchedEntityData_DataItem(type, id, new ItemInstance(input.readShort(), input.readByte(), input.readShort()));
                    break;
                }
                case 6: {
                    e = new SynchedEntityData_DataItem(type, id, new Pos(input.readInt(), input.readInt(), input.readInt()));
                    break;
                }
            }
            list.add(e);
        }
        return list;
    }
    
    static {
        (typeToConstant = new HashMap()).put(Byte.class, 0);
        SynchedEntityData.typeToConstant.put(Short.class, 1);
        SynchedEntityData.typeToConstant.put(Integer.class, 2);
        SynchedEntityData.typeToConstant.put(Float.class, 3);
        SynchedEntityData.typeToConstant.put(String.class, 4);
        SynchedEntityData.typeToConstant.put(ItemInstance.class, 5);
        SynchedEntityData.typeToConstant.put(Pos.class, 6);
    }
}
