// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound;

class SimpleThread$1 extends Thread
{
    final /* synthetic */ SimpleThread this$0;
    
    SimpleThread$1(final SimpleThread simpleThread) {
        this.this$0 = simpleThread;
    }
    
    @Override
    public void run() {
        this.this$0.rerun();
    }
}
