// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network;

class Connection_SendAndQuitThread extends Thread
{
    final /* synthetic */ Connection connection;
    
    Connection_SendAndQuitThread(final Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public void run() {
        try {
            Thread.sleep(2000L);
            if (this.connection.running) {
                this.connection.writeThread.interrupt();
                this.connection.close("disconnect.closed", new Object[0]);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}
