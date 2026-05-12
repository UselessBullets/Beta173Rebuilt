// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network;

import net.minecraft.network.packet.Packet;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.net.SocketException;
import java.util.Collections;
import java.util.ArrayList;
import net.minecraft.network.packet.PacketListener;
import java.util.List;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.net.SocketAddress;
import java.net.Socket;

public class Connection
{
    public static final Object threadCounterLock;
    public static int readThreads;
    public static int writeThreads;
    private Object writeLock;
    private Socket socket;
    private final SocketAddress address;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean running;
    private List incoming;
    private List outgoing;
    private List outgoing_slow;
    private PacketListener packetListener;
    private boolean quitting;
    private Thread writeThread;
    private Thread readThread;
    private boolean disconnected;
    private String disconnectReason;
    private Object[] disconnectReasonObjects;
    private int noInputTicks;
    private int estimatedRemaining;
    public static int[] readSizes;
    public static int[] writeSizes;
    public int fakeLag;
    private int slowWriteDelay;
    
    public Connection(final Socket socket, final String id, final PacketListener packetListener) {
        this.writeLock = new Object();
        this.running = true;
        this.incoming = Collections.synchronizedList(new ArrayList<Object>());
        this.outgoing = Collections.synchronizedList(new ArrayList<Object>());
        this.outgoing_slow = Collections.synchronizedList(new ArrayList<Object>());
        this.quitting = false;
        this.disconnected = false;
        this.disconnectReason = "";
        this.noInputTicks = 0;
        this.estimatedRemaining = 0;
        this.fakeLag = 0;
        this.slowWriteDelay = 50;
        this.socket = socket;
        this.address = socket.getRemoteSocketAddress();
        this.packetListener = packetListener;
        try {
            socket.setSoTimeout(30000);
            socket.setTrafficClass(24);
        }
        catch (final SocketException ex) {
            System.err.println(ex.getMessage());
        }
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream(), 5120));
        this.readThread = new Connection_ReadThread(this, id + " read thread");
        this.writeThread = new Connection_WriteThread(this, id + " write thread");
        this.readThread.start();
        this.writeThread.start();
    }
    
    public void setListener(final PacketListener packetListener) {
        this.packetListener = packetListener;
    }
    
    public void send(final Packet packet) {
        if (this.quitting) {
            return;
        }
        synchronized (this.writeLock) {
            this.estimatedRemaining += packet.getEstimatedSize() + 1;
            if (packet.shouldDelay) {
                this.outgoing_slow.add(packet);
            }
            else {
                this.outgoing.add(packet);
            }
        }
    }
    
    private boolean writeTick() {
        boolean b = false;
        try {
            if (!this.outgoing.isEmpty() && (this.fakeLag == 0 || System.currentTimeMillis() - this.outgoing.get(0).createTime >= this.fakeLag)) {
                final Packet packet;
                synchronized (this.writeLock) {
                    packet = this.outgoing.remove(0);
                    this.estimatedRemaining -= packet.getEstimatedSize() + 1;
                }
                Packet.writePacket(packet, this.dos);
                final int[] writeSizes = Connection.writeSizes;
                final int id = packet.getId();
                writeSizes[id] += packet.getEstimatedSize() + 1;
                b = true;
            }
            if (this.slowWriteDelay-- <= 0 && !this.outgoing_slow.isEmpty() && (this.fakeLag == 0 || System.currentTimeMillis() - this.outgoing_slow.get(0).createTime >= this.fakeLag)) {
                final Packet packet;
                synchronized (this.writeLock) {
                    packet = this.outgoing_slow.remove(0);
                    this.estimatedRemaining -= packet.getEstimatedSize() + 1;
                }
                Packet.writePacket(packet, this.dos);
                final int[] writeSizes2 = Connection.writeSizes;
                final int id2 = packet.getId();
                writeSizes2[id2] += packet.getEstimatedSize() + 1;
                this.slowWriteDelay = 0;
                b = true;
            }
        }
        catch (final Exception ex) {
            if (!this.disconnected) {
                this.handleException(ex);
            }
            return false;
        }
        return b;
    }
    
    public void flush() {
        this.readThread.interrupt();
        this.writeThread.interrupt();
    }
    
    private boolean readTick() {
        boolean b = false;
        try {
            final Packet packet = Packet.readPacket(this.dis, this.packetListener.isServerPacketListener());
            if (packet != null) {
                final int[] readSizes = Connection.readSizes;
                final int id = packet.getId();
                readSizes[id] += packet.getEstimatedSize() + 1;
                this.incoming.add(packet);
                b = true;
            }
            else {
                this.close("disconnect.endOfStream", new Object[0]);
            }
        }
        catch (final Exception ex) {
            if (!this.disconnected) {
                this.handleException(ex);
            }
            return false;
        }
        return b;
    }
    
    private void handleException(final Exception ex) {
        ex.printStackTrace();
        this.close("disconnect.genericReason", "Internal exception: " + ex.toString());
    }
    
    public void close(final String reason, final Object... disconnectReasonObjects) {
        if (!this.running) {
            return;
        }
        this.disconnected = true;
        this.disconnectReason = reason;
        this.disconnectReasonObjects = disconnectReasonObjects;
        new Connection_CloseThread(this).start();
        this.running = false;
        try {
            this.dis.close();
            this.dis = null;
        }
        catch (final Throwable t) {}
        try {
            this.dos.close();
            this.dos = null;
        }
        catch (final Throwable t2) {}
        try {
            this.socket.close();
            this.socket = null;
        }
        catch (final Throwable t3) {}
    }
    
    public void tick() {
        if (this.estimatedRemaining > 1048576) {
            this.close("disconnect.overflow", new Object[0]);
        }
        if (this.incoming.isEmpty()) {
            if (this.noInputTicks++ == 1200) {
                this.close("disconnect.timeout", new Object[0]);
            }
        }
        else {
            this.noInputTicks = 0;
        }
        int n = 100;
        while (!this.incoming.isEmpty() && n-- >= 0) {
            this.incoming.remove(0).handle(this.packetListener);
        }
        this.flush();
        if (this.disconnected && this.incoming.isEmpty()) {
            this.packetListener.onDisconnect(this.disconnectReason, this.disconnectReasonObjects);
        }
    }
    
    public SocketAddress getRemoteAddress() {
        return this.address;
    }
    
    public void sendAndQuit() {
        this.flush();
        this.quitting = true;
        this.readThread.interrupt();
        new Connection_SendAndQuitThread(this).start();
    }
    
    public int countDelayedPackets() {
        return this.outgoing_slow.size();
    }
    
    static {
        threadCounterLock = new Object();
        Connection.readSizes = new int[256];
        Connection.writeSizes = new int[256];
    }
}
