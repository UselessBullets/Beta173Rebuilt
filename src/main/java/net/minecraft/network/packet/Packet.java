// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.util.HashSet;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

public abstract class Packet
{
    private static Map<Integer, Class<? extends Packet>> idToClassMap;
    private static Map<Class<? extends Packet>, Integer> classToIdMap;
    private static Set<Integer> clientReceivedPackets;
    private static Set<Integer> serverReceivedPackets;
    public final long createTime;
    public boolean shouldDelay;
    private static HashMap<Integer, PacketStatistics> packetStatistics;
    private static int readCounter;
    
    public Packet() {
        this.createTime = System.currentTimeMillis();
        this.shouldDelay = false;
    }
    
    static void map(final int id, final boolean receiveOnClient, final boolean receiveOnServer, final Class<? extends Packet> clazz) {
        if (Packet.idToClassMap.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate packet id:" + id);
        }
        if (Packet.classToIdMap.containsKey(clazz)) {
            throw new IllegalArgumentException("Duplicate packet class:" + clazz);
        }
        Packet.idToClassMap.put(id, clazz);
        Packet.classToIdMap.put(clazz, id);
        if (receiveOnClient) {
            Packet.clientReceivedPackets.add(id);
        }
        if (receiveOnServer) {
            Packet.serverReceivedPackets.add(id);
        }
    }
    
    public static Packet getPacket(final int id) {
        try {
            final Class<? extends Packet> clazz = Packet.idToClassMap.get(id);
            if (clazz == null) {
                return null;
            }
            return clazz.newInstance();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            System.out.println("Skipping packet with id " + id);
            return null;
        }
    }
    
    public final int getId() {
        return Packet.classToIdMap.get(this.getClass());
    }
    
    public static Packet readPacket(final DataInputStream dis, final boolean isServer) {
        int read;
        Packet packet;
        try {
            read = dis.read();
            if (read == -1) {
                return null;
            }
            if ((isServer && !Packet.serverReceivedPackets.contains(read)) || (!isServer && !Packet.clientReceivedPackets.contains(read))) {
                throw new IOException("Bad packet id " + read);
            }
            packet = getPacket(read);
            if (packet == null) {
                throw new IOException("Bad packet id " + read);
            }
            packet.read(dis);
        }
        catch (final IOException ex) {
            System.out.println("Reached end of stream");
            return null;
        }
        PacketStatistics value = Packet.packetStatistics.get(read);
        if (value == null) {
            value = new PacketStatistics();
            Packet.packetStatistics.put(read, value);
        }
        value.addPacket(packet.getEstimatedSize());
        ++Packet.readCounter;
        if (Packet.readCounter % 1000 == 0) {}
        return packet;
    }
    
    public static void writePacket(final Packet packet, final DataOutputStream dos) throws IOException {
        dos.write(packet.getId());
        packet.write(dos);
    }
    
    public static void writeUTF(final String value, final DataOutputStream dos) throws IOException {
        if (value.length() > 32767) {
            throw new IOException("String too big");
        }
        dos.writeShort(value.length());
        dos.writeChars(value);
    }
    
    public static String readUTF(final DataInputStream dis, final int maxLength) throws IOException {
        final short short1 = dis.readShort();
        if (short1 > maxLength) {
            throw new IOException("Received string length longer than maximum allowed (" + short1 + " > " + maxLength + ")");
        }
        if (short1 < 0) {
            throw new IOException("Received string length is less than zero! Weird string!");
        }
        final StringBuilder sb = new StringBuilder();
        for (short n = 0; n < short1; ++n) {
            sb.append(dis.readChar());
        }
        return sb.toString();
    }
    
    public abstract void read(final DataInputStream dis) throws IOException;
    
    public abstract void write(final DataOutputStream dos) throws IOException;
    
    public abstract void handle(final PacketListener listener);
    
    public abstract int getEstimatedSize();
    
    static {
        Packet.idToClassMap = new HashMap<>();
        Packet.classToIdMap = new HashMap<>();
        Packet.clientReceivedPackets = new HashSet<>();
        Packet.serverReceivedPackets = new HashSet<>();
        map(0, true, true, KeepAlivePacket.class);
        map(1, true, true, LoginPacket.class);
        map(2, true, true, PreLoginPacket.class);
        map(3, true, true, ChatPacket.class);
        map(4, true, false, SetTimePacket.class);
        map(5, true, false, SetEquippedItemPacket.class);
        map(6, true, false, SetSpawnPositionPacket.class);
        map(7, false, true, InteractPacket.class);
        map(8, true, false, SetHealthPacket.class);
        map(9, true, true, RespawnPacket.class);
        map(10, true, true, MovePlayerPacket.class);
        map(11, true, true, MovePlayerPacket.Pos.class);
        map(12, true, true, MovePlayerPacket.Rot.class);
        map(13, true, true, MovePlayerPacket.PosRot.class);
        map(14, false, true, PlayerActionPacket.class);
        map(15, false, true, UseItemPacket.class);
        map(16, false, true, SetCarriedItemPacket.class);
        map(17, true, false, EntityActionAtPositionPacket.class);
        map(18, true, true, AnimatePacket.class);
        map(19, false, true, PlayerCommandPacket.class);
        map(20, true, false, AddPlayerPacket.class);
        map(21, true, false, AddItemEntityPacket.class);
        map(22, true, false, TakeItemEntityPacket.class);
        map(23, true, false, AddEntityPacket.class);
        map(24, true, false, AddMobPacket.class);
        map(25, true, false, AddPaintingPacket.class);
        map(27, false, true, PlayerInputPacket.class);
        map(28, true, false, SetEntityMotionPacket.class);
        map(29, true, false, RemoveEntityPacket.class);
        map(30, true, false, MoveEntityPacket.class);
        map(31, true, false, MoveEntityPacket.Pos.class);
        map(32, true, false, MoveEntityPacket.Rot.class);
        map(33, true, false, MoveEntityPacket.PosRot.class);
        map(34, true, false, TeleportEntityPacket.class);
        map(38, true, false, EntityEventPacket.class);
        map(39, true, false, SetRidingPacket.class);
        map(40, true, false, SetEntityDataPacket.class);
        map(50, true, false, ChunkVisibilityPacket.class);
        map(51, true, false, BlockRegionUpdatePacket.class);
        map(52, true, false, ChunkTilesUpdatePacket.class);
        map(53, true, false, TileUpdatePacket.class);
        map(54, true, false, TileEventPacket.class);
        map(60, true, false, ExplodePacket.class);
        map(61, true, false, LevelEventPacket.class);
        map(70, true, false, BedResponsePacket.class);
        map(71, true, false, AddGlobalEntityPacketPacket.class);
        map(100, true, false, ContainerOpenPacket.class);
        map(101, true, true, ContainerClosePacket.class);
        map(102, false, true, ContainerClickPacket.class);
        map(103, true, false, ContainerSetSlotPacket.class);
        map(104, true, false, ContainerSetContentPacket.class);
        map(105, true, false, ContainerSetDataPacket.class);
        map(106, true, true, ContainerAckPacket.class);
        map(130, true, true, SignUpdatePacket.class);
        map(131, true, false, ComplexItemDataPacket.class);
        map(200, true, false, AwardStatPacket.class);
        map(255, true, true, DisconnectPacket.class);
        Packet.packetStatistics = new HashMap<>();
        Packet.readCounter = 0;
    }

    static class PacketStatistics
    {
        private int count;
        private long totalSize;

        private PacketStatistics() {
        }

        public void addPacket(final int bytes) {
            ++this.count;
            this.totalSize += bytes;
        }
    }
}
