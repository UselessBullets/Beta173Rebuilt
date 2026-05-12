// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network;

import java.io.IOException;

class Connection_WriteThread extends Thread
{
    final /* synthetic */ Connection connection;
    
    Connection_WriteThread(final Connection connection, final String name) {
        this.connection = connection;
        super(name);
    }
    
    @Override
    public void run() {
        synchronized (Connection.threadCounterLock) {
            ++Connection.writeThreads;
        }
        try {
            while (this.connection.running) {
                while (this.connection.writeTick()) {}
                try {
                    Thread.sleep(100L);
                }
                catch (final InterruptedException ex) {}
                try {
                    if (this.connection.dos == null) {
                        continue;
                    }
                    this.connection.dos.flush();
                }
                catch (final IOException ex2) {
                    if (!this.connection.disconnected) {
                        this.connection.handleException(ex2);
                    }
                    ex2.printStackTrace();
                }
            }
        }
        finally {
            synchronized (Connection.threadCounterLock) {
                --Connection.writeThreads;
            }
        }
    }
}
