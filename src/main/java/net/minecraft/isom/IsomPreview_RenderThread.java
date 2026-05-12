// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.isom;

class IsomPreview_RenderThread extends Thread
{
    final /* synthetic */ IsomPreview a;
    
    IsomPreview_RenderThread(final IsomPreview bd) {
        this.a = bd;
    }
    
    @Override
    public void run() {
        while (this.a.running) {
            this.a.render();
            try {
                Thread.sleep(1L);
            }
            catch (final Exception ex) {}
        }
    }
}
