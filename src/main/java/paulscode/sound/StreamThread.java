// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound;

import java.util.ListIterator;
import java.util.LinkedList;
import java.util.List;

public class StreamThread extends SimpleThread
{
    private SoundSystemLogger logger;
    private List streamingSources;
    private final Object listLock;
    
    public StreamThread() {
        this.listLock = new Object();
        this.logger = SoundSystemConfig.getLogger();
        this.streamingSources = new LinkedList();
    }
    
    @Override
    protected void cleanup() {
        this.kill();
        super.cleanup();
    }
    
    @Override
    public void run() {
        try {
            this.snooze(3600000L);
            while (!this.dying()) {
                while (!this.dying() && !this.streamingSources.isEmpty()) {
                    synchronized (this.listLock) {
                        final ListIterator listIterator = this.streamingSources.listIterator();
                        while (!this.dying() && listIterator.hasNext()) {
                            final Source source = (Source)listIterator.next();
                            if (source == null) {
                                listIterator.remove();
                            }
                            else if (source.stopped()) {
                                if (source.rawDataStream) {
                                    continue;
                                }
                                listIterator.remove();
                            }
                            else if (!source.active()) {
                                if (source.toLoop || source.rawDataStream) {
                                    source.toPlay = true;
                                }
                                listIterator.remove();
                            }
                            else {
                                if (source.paused()) {
                                    continue;
                                }
                                source.checkFadeOut();
                                if (source.stream() || source.rawDataStream || (source.channel != null && source.channel.processBuffer())) {
                                    continue;
                                }
                                if (source.toLoop) {
                                    if (source.playing()) {
                                        continue;
                                    }
                                    if (source.checkFadeOut()) {
                                        source.preLoad = true;
                                    }
                                    else {
                                        source.incrementSoundSequence();
                                        source.preLoad = true;
                                    }
                                }
                                else {
                                    if (source.playing() || source.checkFadeOut()) {
                                        continue;
                                    }
                                    if (source.incrementSoundSequence()) {
                                        source.preLoad = true;
                                    }
                                    else {
                                        listIterator.remove();
                                    }
                                }
                            }
                        }
                    }
                    if (!this.dying() && !this.streamingSources.isEmpty()) {
                        this.snooze(20L);
                    }
                }
                if (!this.dying() && this.streamingSources.isEmpty()) {
                    this.snooze(3600000L);
                }
            }
        }
        finally {
            this.cleanup();
        }
    }
    
    public void watch(final Source source) {
        if (source == null) {
            return;
        }
        if (this.streamingSources.contains(source)) {
            return;
        }
        synchronized (this.listLock) {
            final ListIterator listIterator = this.streamingSources.listIterator();
            while (listIterator.hasNext()) {
                final Source source2 = (Source)listIterator.next();
                if (source2 == null) {
                    listIterator.remove();
                }
                else {
                    if (source.channel != source2.channel) {
                        continue;
                    }
                    source2.stop();
                    listIterator.remove();
                }
            }
            this.streamingSources.add(source);
        }
    }
    
    private void message(final String string) {
        this.logger.message(string, 0);
    }
    
    private void importantMessage(final String string) {
        this.logger.importantMessage(string, 0);
    }
    
    private boolean errorCheck(final boolean boolean1, final String string) {
        return this.logger.errorCheck(boolean1, "StreamThread", string, 0);
    }
    
    private void errorMessage(final String string) {
        this.logger.errorMessage("StreamThread", string, 0);
    }
}
