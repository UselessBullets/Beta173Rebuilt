// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network;

class Connection_CloseThread extends Thread
{
    final /* synthetic */ Connection connection;
    
    Connection_CloseThread(final Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public void run() {
        try {
            Thread.sleep(5000L);
            if (this.connection.readThread.isAlive()) {
                try {
                    this.connection.readThread.stop();
                }
                catch (final Throwable t) {}
            }
            if (this.connection.writeThread.isAlive()) {
                try {
                    this.connection.writeThread.stop();
                }
                catch (final Throwable t2) {}
            }
        }
        catch (final InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
