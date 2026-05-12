// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network;

class Connection_ReadThread extends Thread
{
    final /* synthetic */ Connection connection;
    
    Connection_ReadThread(final Connection connection, final String name) {
        this.connection = connection;
        super(name);
    }
    
    @Override
    public void run() {
        synchronized (Connection.threadCounterLock) {
            ++Connection.readThreads;
        }
        try {
            while (this.connection.running && !this.connection.quitting) {
                while (this.connection.readTick()) {}
                try {
                    Thread.sleep(100L);
                }
                catch (final InterruptedException ex) {}
            }
        }
        finally {
            synchronized (Connection.threadCounterLock) {
                --Connection.readThreads;
            }
        }
    }
}
