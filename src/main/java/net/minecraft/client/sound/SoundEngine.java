// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.sound;

import util.Mth;
import net.minecraft.world.entity.Mob;
import java.io.File;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;
import java.util.Random;
import net.minecraft.client.Options;
import paulscode.sound.SoundSystem;

public class SoundEngine
{
    private static SoundSystem soundSystem;
    private SoundRepository sounds;
    private SoundRepository streamingSounds;
    private SoundRepository songs;
    private int idCounter;
    private Options options;
    private static boolean loaded;
    private Random random;
    private int noMusicDelay;
    
    public SoundEngine() {
        this.sounds = new SoundRepository();
        this.streamingSounds = new SoundRepository();
        this.songs = new SoundRepository();
        this.idCounter = 0;
        this.random = new Random();
        this.noMusicDelay = this.random.nextInt(12000);
    }
    
    public void init(final Options options) {
        this.streamingSounds.trimDigits = false;
        this.options = options;
        if (!SoundEngine.loaded && (options == null || options.sound != 0.0f || options.music != 0.0f)) {
            this.loadLibrary();
        }
    }
    
    private void loadLibrary() {
        try {
            final float sound = this.options.sound;
            final float music = this.options.music;
            this.options.sound = 0.0f;
            this.options.music = 0.0f;
            this.options.save();
            SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
            SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
            SoundSystemConfig.setCodec("mus", CodecMus.class);
            SoundSystemConfig.setCodec("wav", CodecWav.class);
            SoundEngine.soundSystem = new SoundSystem();
            this.options.sound = sound;
            this.options.music = music;
            this.options.save();
        }
        catch (final Throwable t) {
            t.printStackTrace();
            System.err.println("error linking with the LibraryJavaSound plug-in");
        }
        SoundEngine.loaded = true;
    }
    
    public void updateOptions() {
        if (!SoundEngine.loaded && (this.options.sound != 0.0f || this.options.music != 0.0f)) {
            this.loadLibrary();
        }
        if (SoundEngine.loaded) {
            if (this.options.music == 0.0f) {
                SoundEngine.soundSystem.stop("BgMusic");
            }
            else {
                SoundEngine.soundSystem.setVolume("BgMusic", this.options.music);
            }
        }
    }
    
    public void destroy() {
        if (SoundEngine.loaded) {
            SoundEngine.soundSystem.cleanup();
        }
    }
    
    public void add(final String name, final File file) {
        this.sounds.add(name, file);
    }
    
    public void addStreaming(final String name, final File file) {
        this.streamingSounds.add(name, file);
    }
    
    public void addMusic(final String name, final File file) {
        this.songs.add(name, file);
    }
    
    public void playMusicTick() {
        if (!SoundEngine.loaded || this.options.music == 0.0f) {
            return;
        }
        if (!SoundEngine.soundSystem.playing("BgMusic") && !SoundEngine.soundSystem.playing("streaming")) {
            if (this.noMusicDelay > 0) {
                --this.noMusicDelay;
                return;
            }
            final Sound any = this.songs.any();
            if (any != null) {
                this.noMusicDelay = this.random.nextInt(12000) + 12000;
                SoundEngine.soundSystem.backgroundMusic("BgMusic", any.url, any.name, false);
                SoundEngine.soundSystem.setVolume("BgMusic", this.options.music);
                SoundEngine.soundSystem.play("BgMusic");
            }
        }
    }
    
    public void update(final Mob player, final float partialTick) {
        if (!SoundEngine.loaded || this.options.sound == 0.0f) {
            return;
        }
        if (player == null) {
            return;
        }
        final float n = player.yRotO + (player.yRot - player.yRotO) * partialTick;
        final double n2 = player.xo + (player.x - player.xo) * partialTick;
        final double n3 = player.yo + (player.y - player.yo) * partialTick;
        final double n4 = player.zo + (player.z - player.zo) * partialTick;
        final float cos = Mth.cos(-n * 0.017453292f - 3.1415927f);
        final float lookX = -Mth.sin(-n * 0.017453292f - 3.1415927f);
        final float lookY = 0.0f;
        final float lookZ = -cos;
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;
        SoundEngine.soundSystem.setListenerPosition((float)n2, (float)n3, (float)n4);
        SoundEngine.soundSystem.setListenerOrientation(lookX, lookY, lookZ, upX, upY, upZ);
    }
    
    public void playStreaming(final String name, final float x, final float y, final float z, final float volume, final float pitch) {
        if (!SoundEngine.loaded || this.options.sound == 0.0f) {
            return;
        }
        final String sourcename = "streaming";
        if (SoundEngine.soundSystem.playing("streaming")) {
            SoundEngine.soundSystem.stop("streaming");
        }
        if (name == null) {
            return;
        }
        final Sound value = this.streamingSounds.get(name);
        if (value != null && volume > 0.0f) {
            if (SoundEngine.soundSystem.playing("BgMusic")) {
                SoundEngine.soundSystem.stop("BgMusic");
            }
            SoundEngine.soundSystem.newStreamingSource(true, sourcename, value.url, value.name, false, x, y, z, 2, 16.0f * 4.0f);
            SoundEngine.soundSystem.setVolume(sourcename, 0.5f * this.options.sound);
            SoundEngine.soundSystem.play(sourcename);
        }
    }
    
    public void play(final String name, final float x, final float y, final float z, float volume, final float pitch) {
        if (!SoundEngine.loaded || this.options.sound == 0.0f) {
            return;
        }
        final Sound value = this.sounds.get(name);
        if (value != null && volume > 0.0f) {
            this.idCounter = (this.idCounter + 1) % 256;
            final String string = "sound_" + this.idCounter;
            float distOrRoll = 16.0f;
            if (volume > 1.0f) {
                distOrRoll *= volume;
            }
            SoundEngine.soundSystem.newSource(volume > 1.0f, string, value.url, value.name, false, x, y, z, 2, distOrRoll);
            SoundEngine.soundSystem.setPitch(string, pitch);
            if (volume > 1.0f) {
                volume = 1.0f;
            }
            SoundEngine.soundSystem.setVolume(string, volume * this.options.sound);
            SoundEngine.soundSystem.play(string);
        }
    }
    
    public void playUI(final String name, float volume, final float pitch) {
        if (!SoundEngine.loaded || this.options.sound == 0.0f) {
            return;
        }
        final Sound value = this.sounds.get(name);
        if (value != null) {
            this.idCounter = (this.idCounter + 1) % 256;
            final String string = "sound_" + this.idCounter;
            SoundEngine.soundSystem.newSource(false, string, value.url, value.name, false, 0.0f, 0.0f, 0.0f, 0, 0.0f);
            if (volume > 1.0f) {
                volume = 1.0f;
            }
            volume *= 0.25f;
            SoundEngine.soundSystem.setPitch(string, pitch);
            SoundEngine.soundSystem.setVolume(string, volume * this.options.sound);
            SoundEngine.soundSystem.play(string);
        }
    }
    
    static {
        SoundEngine.loaded = false;
    }
}
