// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound;

class MidiChannel$FadeThread extends SimpleThread
{
    final /* synthetic */ MidiChannel this$0;
    
    private MidiChannel$FadeThread(final MidiChannel midiChannel) {
        this.this$0 = midiChannel;
    }
    
    @Override
    public void run() {
        while (!this.dying()) {
            if (this.this$0.fadeOutGain == -1.0f && this.this$0.fadeInGain == 1.0f) {
                this.snooze(3600000L);
            }
            this.this$0.checkFadeOut();
            this.snooze(50L);
        }
        this.cleanup();
    }
}
