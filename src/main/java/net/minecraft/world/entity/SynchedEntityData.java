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
    public static final int MAX_STRING_DATA_LENGTH = 64;
    public static final int EOF_MARKER = 127;
    private static final int TYPE_BYTE = 0;
    private static final int TYPE_SHORT = 1;
    private static final int TYPE_INT = 2;
    private static final int TYPE_FLOAT = 3;
    private static final int TYPE_STRING = 4;
    private static final int TYPE_ITEMINSTANCE = 5;
    private static final int TYPE_POS = 6;
    private static final HashMap<Class<?>, Integer> typeToConstant = new HashMap<>();
    static {
        SynchedEntityData.typeToConstant.put(Byte.class, TYPE_BYTE);
        SynchedEntityData.typeToConstant.put(Short.class, TYPE_SHORT);
        SynchedEntityData.typeToConstant.put(Integer.class, TYPE_INT);
        SynchedEntityData.typeToConstant.put(Float.class, TYPE_FLOAT);
        SynchedEntityData.typeToConstant.put(String.class, TYPE_STRING);
        SynchedEntityData.typeToConstant.put(ItemInstance.class, TYPE_ITEMINSTANCE);
        SynchedEntityData.typeToConstant.put(Pos.class, TYPE_POS);
    }
    private static final int TYPE_MASK = 0b11100000;
    private static final int TYPE_SHIFT = 5;
    private static final int MAX_ID_VALUE = ~TYPE_MASK & 0xff;
    private final Map<Integer, DataItem> itemsById;

    private boolean isDirty;

    public SynchedEntityData() {
        this.itemsById = new HashMap<>();
    }

    public void define(final int id, final Object value) {
        final Integer type = SynchedEntityData.typeToConstant.get(value.getClass());
        if (type == null) throw new IllegalArgumentException("Unknown data type: " + value.getClass());
        if (id > MAX_ID_VALUE) throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + MAX_ID_VALUE + ")");
        if (this.itemsById.containsKey(id)) throw new IllegalArgumentException("Duplicate id value for " + id + "!");

        this.itemsById.put(id, new DataItem(type, id, value));
    }

    public byte getByte(final int id) {
        return (Byte) this.itemsById.get(id).getValue();
    }

    // Useless - Exists in b1.2 and LCE leaks, constants maintained in b1.7.3 also support its existance
    public short getShort(int id) {
        return (Short) this.itemsById.get(id).getValue();
    }

    public int getInteger(final int id) {
        return (Integer) this.itemsById.get(id).getValue();
    }

    // Useless - Exists in b1.2 and LCE leaks, constants maintained in b1.7.3 also support its existance
    public float getFloat(int id) {
        return (Float) this.itemsById.get(id).getValue();
    }

    public String getString(final int id) {
        return (String)this.itemsById.get(id).getValue();
    }

    // Useless - Exists in b1.2 and LCE leaks, constants maintained in b1.7.3 also support its existance
    public ItemInstance getItemInstance(int id) {
        return (ItemInstance)this.itemsById.get(id).getValue();
    }

    // Useless - Has type in b1.7.3 and load/unload code, LCE also has a stub for this
    public Pos getPos(int id) {
        return (Pos) this.itemsById.get(id).getValue();
    }

    public void set(final int id, final Object value) {
        final DataItem dataItem = this.itemsById.get(id);

        // update the value if it has changed
        if (!value.equals(dataItem.getValue())) {
            dataItem.setValue(value);
            dataItem.setDirty(true);
            this.isDirty = true;
        }
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public static void pack(final List<DataItem> items, final DataOutputStream output) throws IOException {
        if (items != null) {
            for (DataItem item : items) {
                writeDataItem(output, item);
            }
        }

        // add an eof
        output.writeByte(EOF_MARKER);
    }

    public ArrayList<DataItem> packDirty() {
        ArrayList<DataItem> result = null;

        if (this.isDirty) {
            for (final DataItem dataItem : this.itemsById.values()) {
                if (dataItem.isDirty()) {
                    dataItem.setDirty(false);

                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(dataItem);
                }
            }
        }
        this.isDirty = false;

        return result;
    }

    public void packAll(final DataOutputStream output) throws IOException {
        for (DataItem dataItem : this.itemsById.values()) {
            writeDataItem(output, dataItem);
        }

        // add an eof
        output.writeByte(EOF_MARKER);
    }

    private static void writeDataItem(final DataOutputStream output, final DataItem dataItem) throws IOException {
        // pack type and id
        int header = ((dataItem.getType() << TYPE_SHIFT) | (dataItem.getId() & ~TYPE_MASK)) & 0xFF;
        output.writeByte(header);

        // write value
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
                final ItemInstance instance = (ItemInstance)dataItem.getValue();
                output.writeShort(instance.getItem().id);
                output.writeByte(instance.count);
                output.writeShort(instance.getAuxValue());
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
        ArrayList<DataItem> result = null;

        byte currentHeader = input.readByte();
        while (currentHeader != EOF_MARKER) {
            if (result == null) result = new ArrayList<>();

            // split type and id
            final int itemType = (currentHeader & TYPE_MASK) >> TYPE_SHIFT;
            final int itemId = currentHeader & ~TYPE_MASK;

            DataItem item = null;
            switch (itemType) {
                case TYPE_BYTE: {
                    item = new DataItem(itemType, itemId, input.readByte());
                    break;
                }
                case TYPE_SHORT: {
                    item = new DataItem(itemType, itemId, input.readShort());
                    break;
                }
                case TYPE_INT: {
                    item = new DataItem(itemType, itemId, input.readInt());
                    break;
                }
                case TYPE_FLOAT: {
                    item = new DataItem(itemType, itemId, input.readFloat());
                    break;
                }
                case TYPE_STRING: {
                    item = new DataItem(itemType, itemId, Packet.readUTF(input, MAX_STRING_DATA_LENGTH));
                    break;
                }
                case TYPE_ITEMINSTANCE: {
                    item = new DataItem(itemType, itemId, new ItemInstance(input.readShort(), input.readByte(), input.readShort()));
                    break;
                }
                case TYPE_POS: {
                    item = new DataItem(itemType, itemId, new Pos(input.readInt(), input.readInt(), input.readInt()));
                    break;
                }
            }
            result.add(item);

            currentHeader = input.readByte();
        }
        return result;
    }

    public void assignValues(final List<DataItem> items) {
        for (final DataItem item : items) {
            final DataItem itemFromId = this.itemsById.get(item.getId());
            if (itemFromId != null) itemFromId.setValue(item.getValue());
        }
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

        public boolean isDirty() {
            return this.dirty;
        }

        public void setDirty(final boolean dirty) {
            this.dirty = dirty;
        }
    }
}
