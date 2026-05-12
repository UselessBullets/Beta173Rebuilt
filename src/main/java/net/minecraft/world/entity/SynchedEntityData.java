// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.io.DataInputStream;
import net.minecraft.Pos;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.network.packet.Packet;
import java.util.Iterator;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SynchedEntityData
{
    public static final int EOF_MARKER = 127;
    private static final int TYPE_BYTE = 0;
    private static final int TYPE_SHORT = 1;
    private static final int TYPE_INT = 2;
    private static final int TYPE_FLOAT = 3;
    private static final int TYPE_STRING = 4;
    private static final int TYPE_ITEMINSTANCE = 5;
    private static final int TYPE_POS = 6;
    private static final HashMap<Class<?>, Integer> typeToConstant;
    private static final int TYPE_MASK = 0b11100000;
    private static final int TYPE_SHIFT = 5;
    private static final int MAX_ID_VALUE = 31;
    private final Map<Integer, DataItem> itemsById;
    private boolean isDirty;
    
    public SynchedEntityData() {
        this.itemsById = new HashMap();
    }
    
    public void define(final int id, final Object value) {
        final Integer n = SynchedEntityData.typeToConstant.get(value.getClass());
        if (n == null) {
            throw new IllegalArgumentException("Unknown data type: " + value.getClass());
        }
        if (id > MAX_ID_VALUE) {
            throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + MAX_ID_VALUE + ")");
        }
        if (this.itemsById.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate id value for " + id + "!");
        }
        this.itemsById.put(id, new DataItem(n, id, value));
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
        final DataItem dataItem = this.itemsById.get(id);
        if (!value.equals(dataItem.getValue())) {
            dataItem.setValue(value);
            dataItem.setDirty(true);
            this.isDirty = true;
        }
    }
    
    public static void pack(final List<DataItem> items, final DataOutputStream output) throws IOException {
        if (items != null) {
            final Iterator<DataItem> iterator = items.iterator();
            while (iterator.hasNext()) {
                writeDataItem(output, (DataItem)iterator.next());
            }
        }
        output.writeByte(EOF_MARKER);
    }
    
    public void packAll(final DataOutputStream output) throws IOException {
        final Iterator<DataItem> iterator = this.itemsById.values().iterator();
        while (iterator.hasNext()) {
            writeDataItem(output, (DataItem)iterator.next());
        }
        output.writeByte(EOF_MARKER);
    }
    
    private static void writeDataItem(final DataOutputStream output, final DataItem dataItem) throws IOException {
        output.writeByte((dataItem.getType() << TYPE_SHIFT | (dataItem.getId() & ~TYPE_MASK)) & 0xFF);
        switch (dataItem.getType()) {
            case TYPE_BYTE: {
                output.writeByte((byte)dataItem.getValue());
                break;
            }
            case TYPE_SHORT: {
                output.writeShort((short)dataItem.getValue());
                break;
            }
            case TYPE_INT: {
                output.writeInt((int)dataItem.getValue());
                break;
            }
            case TYPE_FLOAT: {
                output.writeFloat((float)dataItem.getValue());
                break;
            }
            case TYPE_STRING: {
                Packet.writeUTF((String)dataItem.getValue(), output);
                break;
            }
            case TYPE_ITEMINSTANCE: {
                final ItemInstance itemInstance = (ItemInstance)dataItem.getValue();
                output.writeShort(itemInstance.getItem().id);
                output.writeByte(itemInstance.count);
                output.writeShort(itemInstance.getAuxValue());
                break;
            }
            case TYPE_POS: {
                final Pos pos = (Pos)dataItem.getValue();
                output.writeInt(pos.x);
                output.writeInt(pos.y);
                output.writeInt(pos.z);
                break;
            }
        }
    }
    
    public static List<DataItem> unpack(final DataInputStream input) throws IOException {
        ArrayList<DataItem> list = null;
        for (byte b = input.readByte(); b != EOF_MARKER; b = input.readByte()) {
            if (list == null) {
                list = new ArrayList<>();
            }
            final int type = (b & TYPE_MASK) >> TYPE_SHIFT;
            final int id = b & ~TYPE_MASK;
            DataItem e = null;
            switch (type) {
                case TYPE_BYTE: {
                    e = new DataItem(type, id, input.readByte());
                    break;
                }
                case TYPE_SHORT: {
                    e = new DataItem(type, id, input.readShort());
                    break;
                }
                case TYPE_INT: {
                    e = new DataItem(type, id, input.readInt());
                    break;
                }
                case TYPE_FLOAT: {
                    e = new DataItem(type, id, input.readFloat());
                    break;
                }
                case TYPE_STRING: {
                    e = new DataItem(type, id, Packet.readUTF(input, 64));
                    break;
                }
                case TYPE_ITEMINSTANCE: {
                    e = new DataItem(type, id, new ItemInstance(input.readShort(), input.readByte(), input.readShort()));
                    break;
                }
                case TYPE_POS: {
                    e = new DataItem(type, id, new Pos(input.readInt(), input.readInt(), input.readInt()));
                    break;
                }
            }
            list.add(e);
        }
        return list;
    }
    
    public void assignValues(final List<DataItem> items) {
        for (final DataItem synchedEntityData_DataItem : items) {
            final DataItem synchedEntityData_DataItem2 = this.itemsById.get(synchedEntityData_DataItem.getId());
            if (synchedEntityData_DataItem2 != null) {
                synchedEntityData_DataItem2.setValue(synchedEntityData_DataItem.getValue());
            }
        }
    }
    
    static {
        (typeToConstant = new HashMap<>()).put(Byte.class, TYPE_BYTE);
        SynchedEntityData.typeToConstant.put(Short.class, TYPE_SHORT);
        SynchedEntityData.typeToConstant.put(Integer.class, TYPE_INT);
        SynchedEntityData.typeToConstant.put(Float.class, TYPE_FLOAT);
        SynchedEntityData.typeToConstant.put(String.class, TYPE_STRING);
        SynchedEntityData.typeToConstant.put(ItemInstance.class, TYPE_ITEMINSTANCE);
        SynchedEntityData.typeToConstant.put(Pos.class, TYPE_POS);
    }

    public static class DataItem
    {
        private final int type;
        private final int id;
        private Object value;
        private boolean dirty;

        public DataItem(final int type, final int id, final Object value) {
            this.id = id;
            this.value = value;
            this.type = type;
            this.dirty = true;
        }

        public int getId() {
            return this.id;
        }

        public void setValue(final Object value) {
            this.value = value;
        }

        public Object getValue() {
            return this.value;
        }

        public int getType() {
            return this.type;
        }

        public void setDirty(final boolean dirty) {
            this.dirty = dirty;
        }
    }
}
