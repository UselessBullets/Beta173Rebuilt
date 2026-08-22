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
    private static final int SOUND_DISTANCE = 16;
    private static SoundSystem soundSystem;
    private SoundRepository sounds = new SoundRepository();
    private SoundRepository streamingSounds = new SoundRepository();
    private SoundRepository songs = new SoundRepository();
    private int idCounter = 0;
    private Options options;
    private static boolean loaded = false;
    private Random random = new Random();
    private int noMusicDelay = this.random.nextInt(12000);
    
    public void init(final Options options) {
        this.streamingSounds.trimDigits = false;
        this.options = options;
        if (!SoundEngine.loaded && (options == null || options.sound != 0.0f || options.music != 0.0f)) {
            this.loadLibrary();
        }
    }
    
    private void loadLibrary() {
        try {
            final float hadSound = this.options.sound;
            final float hadMusic = this.options.music;
            this.options.sound = 0.0f;
            this.options.music = 0.0f;
            this.options.save();
            SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
            SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
            SoundSystemConfig.setCodec("mus", CodecMus.class);
            SoundSystemConfig.setCodec("wav", CodecWav.class);
            SoundEngine.soundSystem = new SoundSystem();
            this.options.sound = hadSound;
            this.options.music = hadMusic;
            this.options.save();
        }
        catch (final Throwable e) {
            e.printStackTrace();
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
        if (!SoundEngine.loaded || this.options.music == 0.0f) return;

        if (SoundEngine.soundSystem.playing("BgMusic") || SoundEngine.soundSystem.playing("streaming")) return;

        if (this.noMusicDelay > 0) {
            --this.noMusicDelay;
            return;
        }

        final Sound song = this.songs.any();
        if (song == null) return;

        this.noMusicDelay = this.random.nextInt(12000) + 12000;
        SoundEngine.soundSystem.backgroundMusic("BgMusic", song.url, song.name, false);
        SoundEngine.soundSystem.setVolume("BgMusic", this.options.music);
        SoundEngine.soundSystem.play("BgMusic");
    }
    
    public void update(final Mob player, final float a) {
        if (!SoundEngine.loaded || this.options.sound == 0.0f) return;
        if (player == null) return;

        float yRot = player.yRotO + (player.yRot - player.yRotO) * a;
        double x = player.xo + (player.x - player.xo) * a;
        double y = player.yo + (player.y - player.yo) * a;
        double z = player.zo + (player.z - player.zo) * a;
        float yCos = Mth.cos(-yRot * Mth.DEGRAD - Mth.PI);
        float ySin = Mth.sin(-yRot * Mth.DEGRAD - Mth.PI);

        float xa = -ySin;
        float ya = 0.0f;
        float za = -yCos;

        float xa2 = 0.0f;
        float ya2 = 1.0f;
        float za2 = 0.0f;
        SoundEngine.soundSystem.setListenerPosition((float)x, (float)y, (float)z);
        SoundEngine.soundSystem.setListenerOrientation(xa, ya, za, xa2, ya2, za2);
    }
    
    public void playStreaming(final String name, final float x, final float y, final float z, final float volume, final float pitch) {
        if (!SoundEngine.loaded || this.options.sound == 0.0f) return;

        final String id = "streaming";
        if (SoundEngine.soundSystem.playing("streaming")) {
            SoundEngine.soundSystem.stop("streaming");
        }

        if (name == null) return;

        final Sound sound = this.streamingSounds.get(name);
        if (sound == null || volume <= 0.0f) return;

        if (SoundEngine.soundSystem.playing("BgMusic")) {
            SoundEngine.soundSystem.stop("BgMusic");
        }

        float dist = SOUND_DISTANCE;
        SoundEngine.soundSystem.newStreamingSource(true, id, sound.url, sound.name, false, x, y, z, 2, dist * 4.0f);
        SoundEngine.soundSystem.setVolume(id, 0.5f * this.options.sound);
        SoundEngine.soundSystem.play(id);
    }
    
    public void play(final String name, final float x, final float y, final float z, float volume, final float pitch) {
        if (!SoundEngine.loaded || this.options.sound == 0.0f) return;

        final Sound sound = this.sounds.get(name);
        if (sound == null || !(volume > 0.0f)) return;

        this.idCounter = (this.idCounter + 1) % 256;
        final String id = "sound_" + this.idCounter;

        float dist = SOUND_DISTANCE;
        if (volume > 1.0f) {
            dist *= volume;
        }

        SoundEngine.soundSystem.newSource(volume > 1.0f, id, sound.url, sound.name, false, x, y, z, 2, dist);
        SoundEngine.soundSystem.setPitch(id, pitch);
        if (volume > 1.0f) {
            volume = 1.0f;
        }

        SoundEngine.soundSystem.setVolume(id, volume * this.options.sound);
        SoundEngine.soundSystem.play(id);
    }
    
    public void playUI(final String name, float volume, final float pitch) {
        if (!SoundEngine.loaded || this.options.sound == 0.0f) return;

        final Sound sound = this.sounds.get(name);
        if (sound == null) return;

        this.idCounter = (this.idCounter + 1) % 256;
        final String id = "sound_" + this.idCounter;

        SoundEngine.soundSystem.newSource(false, id, sound.url, sound.name, false, 0.0f, 0.0f, 0.0f, 0, 0.0f);
        if (volume > 1.0f) {
            volume = 1.0f;
        }

        volume *= 0.25f;
        SoundEngine.soundSystem.setPitch(id, pitch);
        SoundEngine.soundSystem.setVolume(id, volume * this.options.sound);
        SoundEngine.soundSystem.play(id);
    }

}
