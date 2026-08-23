// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network;

import net.minecraft.network.packet.Packet;

import java.io.IOException;
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
    // This should always be enabled, except for debugging use
    public static final boolean CONNECTION_ENABLE_TIMEOUT_DISCONNECT = true;
    public static final Object threadCounterLock = new Object();
    public static final int SEND_BUFFER_SIZE = 1024 * 5;
    public static int readThreads, writeThreads;
    private Object writeLock = new Object();
    private static final int MAX_TICKS_WITHOUT_INPUT = 20 * 60;
    public static final int IPTOS_LOWCOST = 0x2;
    public static final int IPTOS_RELIABILITY = 0x4;
    public static final int IPTOS_THROUGHPUT = 0x8;
    public static final int IPTOS_LOWDELAY = 0x10;
    private Socket socket;
    private final SocketAddress address;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean running = true;
    private List<Packet> incoming = Collections.synchronizedList(new ArrayList<>());
    private List<Packet> outgoing = Collections.synchronizedList(new ArrayList<>());
    private List<Packet> outgoing_slow = Collections.synchronizedList(new ArrayList<>());
    private PacketListener packetListener;
    private boolean quitting = false;
    private Thread writeThread;
    private Thread readThread;
    private boolean disconnected = false;
    private String disconnectReason = "";
    private Object[] disconnectReasonObjects;
    private int noInputTicks = 0;
    private int estimatedRemaining = 0;
    public static int[] readSizes = new int[256];
    public static int[] writeSizes = new int[256];
    public int fakeLag = 0;
    private int slowWriteDelay = 50;
    
    public Connection(final Socket socket, final String id, final PacketListener packetListener) throws IOException {
        this.socket = socket;

        this.address = socket.getRemoteSocketAddress();

        this.packetListener = packetListener;

        try {
            socket.setSoTimeout(30000);
            socket.setTrafficClass(IPTOS_THROUGHPUT | IPTOS_LOWDELAY);
        }
        catch (final SocketException e) {
            // catching this exception because it (apparently?) causes problems
            // on OSX Tiger
            System.err.println(e.getMessage());
        }

        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream(), SEND_BUFFER_SIZE));

        this.readThread = new Thread(id + " read thread") {

            @Override
            public void run() {
                synchronized (threadCounterLock) {
                    readThreads++;
                }
                try {
                    while (Connection.this.running && !Connection.this.quitting) {
                        while (readTick()) {}

                        try {
                            Thread.sleep(100L);
                        }
                        catch (final InterruptedException e) {}
                    }
                }
                finally {
                    synchronized (threadCounterLock) {
                        readThreads--;
                    }
                }
            }
        };
        this.writeThread = new Thread(id + " write thread") {

            @Override
            public void run() {
                synchronized (threadCounterLock) {
                    writeThreads++;
                }
                try {
                    while (Connection.this.running) {
                        while (writeTick()) {}

                        try {
                            Thread.sleep(100L);
                        }
                        catch (final InterruptedException e) {}

                        try {
                            if (Connection.this.dos == null) continue;
                            Connection.this.dos.flush();
                        }
                        catch (final IOException e) {
                            if (!Connection.this.disconnected) {
                                handleException(e);
                            }
                            e.printStackTrace();
                        }
                    }
                }
                finally {
                    synchronized (threadCounterLock) {
                        writeThreads--;
                    }
                }
            }
        };
        this.readThread.start();
        this.writeThread.start();
    }

    public void setListener(final PacketListener packetListener) {
        this.packetListener = packetListener;
    }
    
    public void send(final Packet packet) {
        if (this.quitting) return;

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

    // Useless - In b1.2 and LCE leak, just unused
    public void queueSend(Packet var1) {
        if (this.quitting) return;
        this.outgoing_slow.add(var1);
    }
    
    private boolean writeTick() {
        boolean didSomething = false;

        try {
            if (!this.outgoing.isEmpty() && (this.fakeLag == 0 || System.currentTimeMillis() - this.outgoing.get(0).createTime >= this.fakeLag)) {
                final Packet packet;
                synchronized (this.writeLock) {
                    packet = this.outgoing.remove(0);
                    this.estimatedRemaining -= packet.getEstimatedSize() + 1;
                }

                Packet.writePacket(packet, this.dos);

                Connection.writeSizes[packet.getId()] += packet.getEstimatedSize() + 1;
                didSomething = true;
            }

            if (this.slowWriteDelay-- <= 0 && !this.outgoing_slow.isEmpty() && (this.fakeLag == 0 || System.currentTimeMillis() - this.outgoing_slow.get(0).createTime >= this.fakeLag)) {
                final Packet packet;
                synchronized (this.writeLock) {
                    packet = this.outgoing_slow.remove(0);
                    this.estimatedRemaining -= packet.getEstimatedSize() + 1;
                }

                Packet.writePacket(packet, this.dos);

                Connection.writeSizes[packet.getId()] += packet.getEstimatedSize() + 1;
                this.slowWriteDelay = 0;
                didSomething = true;
            }
        }
        catch (final Exception e) {
            if (!this.disconnected) this.handleException(e);
            return false;
        }

        return didSomething;
    }
    
    public void flush() {
        this.readThread.interrupt();
        this.writeThread.interrupt();
    }
    
    private boolean readTick() {
        boolean didSomething = false;

        try {
            final Packet packet = Packet.readPacket(this.dis, this.packetListener.isServerPacketListener());
            if (packet != null) {
                Connection.readSizes[packet.getId()] += packet.getEstimatedSize() + 1;
                this.incoming.add(packet);
                didSomething = true;
            }
            else {
                this.close("disconnect.endOfStream");
            }
        }
        catch (final Exception e) {
            if (!this.disconnected) this.handleException(e);
            return false;
        }

        return didSomething;
    }
    
    private void handleException(final Exception e) {
        e.printStackTrace();
        this.close("disconnect.genericReason", "Internal exception: " + e);
    }
    
    public void close(final String reason, final Object... disconnectReasonObjects) {
        if (!this.running) return;

        this.disconnected = true;
        this.disconnectReason = reason;
        this.disconnectReasonObjects = disconnectReasonObjects;

        new Thread(() -> {
            try {
                Thread.sleep(5000L);
                if (this.readThread.isAlive()) {
                    try {
                        this.readThread.stop();
                    }
                    catch (final Throwable e) {}
                }

                if (this.writeThread.isAlive()) {
                    try {
                        this.writeThread.stop();
                    }
                    catch (final Throwable e) {}
                }
            }
            catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        this.running = false;

        // The input stream needs closed before the readThread, or the readThread
        // may get stuck whilst blocking waiting on a read
        try {
            this.dis.close();
            this.dis = null;
        }
        catch (final Throwable e) {}

        try {
            this.dos.close();
            this.dos = null;
        }
        catch (final Throwable e) {}

        try {
            this.socket.close();
            this.socket = null;
        }
        catch (final Throwable e) {}
    }
    
    public void tick() {
        if (this.estimatedRemaining > 1 * 1024 * 1024) {
            this.close("disconnect.overflow");
        }

        boolean empty = this.incoming.isEmpty();
        if (empty) {
            if (CONNECTION_ENABLE_TIMEOUT_DISCONNECT) {
                if (this.noInputTicks++ == MAX_TICKS_WITHOUT_INPUT) {
                    this.close("disconnect.timeout");
                }
            }
        }
        else {
            this.noInputTicks = 0;
        }

        int max = 100;
        while (!this.incoming.isEmpty() && max-- >= 0) {
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

        new Thread(() -> {
            try {
                Thread.sleep(2000L);
                if (this.running) {
                    this.writeThread.interrupt();
                    close("disconnect.closed");
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public int countDelayedPackets() {
        return this.outgoing_slow.size();
    }

}
