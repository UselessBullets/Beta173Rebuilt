// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import net.minecraft.locale.language.I18n;
import org.lwjgl.input.Keyboard;
import net.minecraft.locale.language.Language;
import java.io.File;

public class Options
{
    private static final String[] RENDER_DISTANCE_NAMES = new String[] { "options.renderDistance.far", "options.renderDistance.normal", "options.renderDistance.short", "options.renderDistance.tiny" };
    private static final String[] DIFFICULTY_NAMES = new String[] { "options.difficulty.peaceful", "options.difficulty.easy", "options.difficulty.normal", "options.difficulty.hard" };
    private static final String[] GUI_SCALE = new String[] { "options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large" };
    private static final String[] FRAMERATE_LIMITS = new String[] { "performance.max", "performance.balanced", "performance.powersaver" };
    public float music = 1.0f;
    public float sound = 1.0f;
    public float sensitivity = 0.5f;
    public boolean invertYMouse = false;
    public int viewDistance = 0;
    public boolean bobView = true;
    public boolean anaglyph3d = false;
    public boolean advancedOpengl = false;
    public int limitFramerate = 1;
    public boolean fancyGraphics = true;
    public boolean ambientOcclusion = true;
    public String skin = "Default";
    public KeyMapping keyUp = new KeyMapping("key.forward", Keyboard.KEY_W);
    public KeyMapping keyLeft = new KeyMapping("key.left", Keyboard.KEY_A);
    public KeyMapping keyDown = new KeyMapping("key.back", Keyboard.KEY_S);
    public KeyMapping keyRight = new KeyMapping("key.right", Keyboard.KEY_D);
    public KeyMapping keyJump = new KeyMapping("key.jump", Keyboard.KEY_SPACE);
    public KeyMapping keyBuild = new KeyMapping("key.inventory", Keyboard.KEY_E);
    public KeyMapping keyDrop = new KeyMapping("key.drop", Keyboard.KEY_Q);
    public KeyMapping keyChat = new KeyMapping("key.chat", Keyboard.KEY_T);
    public KeyMapping keyFog = new KeyMapping("key.fog", Keyboard.KEY_F);
    public KeyMapping keySneak = new KeyMapping("key.sneak", Keyboard.KEY_LSHIFT);
    public KeyMapping[] keyMappings = new KeyMapping[] {
            this.keyUp,
            this.keyLeft,
            this.keyDown,
            this.keyRight,
            this.keyJump,
            this.keySneak,
            this.keyDrop,
            this.keyBuild,
            this.keyChat,
            this.keyFog
    };;
    protected Minecraft minecraft;
    private File optionsFile;
    public int difficulty = 2;
    public boolean hideGui = false;
    public boolean thirdPersonView = false;
    public boolean renderDebug = false;
    public String lastMpIp = "";
    public boolean isFlying = false;
    public boolean smoothCamera = false;
    public boolean fixedCamera = false;
    public float flySpeed = 1.0f;
    public float cameraSpeed = 1.0f;
    public int guiScale = 0;
    
    public Options(final Minecraft minecraft, final File parent) {
        this.minecraft = minecraft;
        this.optionsFile = new File(parent, "options.txt");
        this.load();
    }
    
    public String getKeyDesciption(final int n) {
        return Language.getInstance().getElement(this.keyMappings[n].name);
    }
    
    public String getKeyMessage(final int n) {
        return Keyboard.getKeyName(this.keyMappings[n].key);
    }
    
    public void setKey(final int n, final int key) {
        this.keyMappings[n].key = key;
        this.save();
    }
    
    public void set(final Option option, final float sensitivity) {
        if (option == Option.MUSIC) {
            this.music = sensitivity;
            this.minecraft.soundEngine.updateOptions();
        }
        if (option == Option.SOUND) {
            this.sound = sensitivity;
            this.minecraft.soundEngine.updateOptions();
        }
        if (option == Option.SENSITIVITY) {
            this.sensitivity = sensitivity;
        }
    }
    
    public void toggle(final Option option, final int dir) {
        if (option == Option.INVERT_MOUSE) this.invertYMouse = !this.invertYMouse;
        if (option == Option.RENDER_DISTANCE) this.viewDistance = (this.viewDistance + dir & 0x3);
        if (option == Option.GUI_SCALE) this.guiScale = (this.guiScale + dir & 0x3);

        if (option == Option.VIEW_BOBBING) this.bobView = !this.bobView;
        if (option == Option.ADVANCED_OPENGL) {
            this.advancedOpengl = !this.advancedOpengl;
            this.minecraft.levelRenderer.allChanged();
        }
        if (option == Option.ANAGLYPH) {
            this.anaglyph3d = !this.anaglyph3d;
            this.minecraft.textures.reloadAll();
        }
        if (option == Option.FRAMERATE_LIMIT) this.limitFramerate = (this.limitFramerate + dir + 3) % 3;
        if (option == Option.DIFFICULTY) this.difficulty = (this.difficulty + dir & 0x3);
        if (option == Option.GRAPHICS) {
            this.fancyGraphics = !this.fancyGraphics;
            this.minecraft.levelRenderer.allChanged();
        }
        if (option == Option.AMBIENT_OCCLUSION) {
            this.ambientOcclusion = !this.ambientOcclusion;
            this.minecraft.levelRenderer.allChanged();
        }

        this.save();
    }
    
    public float getProgressValue(final Option option) {
        if (option == Option.MUSIC) return this.music;
        if (option == Option.SOUND) return this.sound;
        if (option == Option.SENSITIVITY) return this.sensitivity;
        return 0.0f;
    }
    
    public boolean getBooleanValue(final Option option) {
        switch (option) {
            case INVERT_MOUSE: return this.invertYMouse;
            case VIEW_BOBBING: return this.bobView;
            case ANAGLYPH: return this.anaglyph3d;
            case ADVANCED_OPENGL: return this.advancedOpengl;
            case AMBIENT_OCCLUSION: return this.ambientOcclusion;
            default: return false;
        }
    }
    
    public String getMessage(final Option item) {
        final Language language = Language.getInstance();
        final String caption = language.getElement(item.getCaptionId()) + ": ";

        if (item.isProgress()) {
            final float progressValue = this.getProgressValue(item);

            if (item == Option.SENSITIVITY) {
                if (progressValue == 0.0f) return caption + language.getElement("options.sensitivity.min");
                if (progressValue == 1.0f) return caption + language.getElement("options.sensitivity.max");
                return caption + (int)(progressValue * 200.0f) + "%";
            }
            else {
                if (progressValue == 0.0f) return caption + language.getElement("options.off");
                return caption + (int)(progressValue * 100.0f) + "%";
            }
        }
        else if (item.isBoolean()) {
            if (this.getBooleanValue(item)) return caption + language.getElement("options.on");
            return caption + language.getElement("options.off");
        }
        else {
            if (item == Option.RENDER_DISTANCE) return caption + language.getElement(Options.RENDER_DISTANCE_NAMES[this.viewDistance]);
            if (item == Option.DIFFICULTY) return caption + language.getElement(Options.DIFFICULTY_NAMES[this.difficulty]);
            if (item == Option.GUI_SCALE) return caption + language.getElement(Options.GUI_SCALE[this.guiScale]);
            if (item == Option.FRAMERATE_LIMIT) return caption + I18n.get(Options.FRAMERATE_LIMITS[this.limitFramerate]);
            if (item != Option.GRAPHICS) return caption;
            if (this.fancyGraphics) return caption + language.getElement("options.graphics.fancy");
            return caption + language.getElement("options.graphics.fast");
        }
    }
    
    public void load() {
        try {
            if (!this.optionsFile.exists()) return;
            final BufferedReader br = new BufferedReader(new FileReader(this.optionsFile));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    final String[] split = line.split(":");
                    if (split[0].equals("music")) this.music = this.readFloat(split[1]);
                    if (split[0].equals("sound")) this.sound = this.readFloat(split[1]);
                    if (split[0].equals("mouseSensitivity")) this.sensitivity = this.readFloat(split[1]);
                    if (split[0].equals("invertYMouse")) this.invertYMouse = split[1].equals("true");
                    if (split[0].equals("viewDistance")) this.viewDistance = Integer.parseInt(split[1]);
                    if (split[0].equals("guiScale")) this.guiScale = Integer.parseInt(split[1]);
                    if (split[0].equals("bobView")) this.bobView = split[1].equals("true");
                    if (split[0].equals("anaglyph3d")) this.anaglyph3d = split[1].equals("true");
                    if (split[0].equals("advancedOpengl")) this.advancedOpengl = split[1].equals("true");
                    if (split[0].equals("fpsLimit")) this.limitFramerate = Integer.parseInt(split[1]);
                    if (split[0].equals("difficulty")) this.difficulty = Integer.parseInt(split[1]);
                    if (split[0].equals("fancyGraphics")) this.fancyGraphics = split[1].equals("true");
                    if (split[0].equals("ao")) this.ambientOcclusion = split[1].equals("true");
                    if (split[0].equals("skin")) this.skin = split[1];
                    if (split[0].equals("lastServer") && split.length >= 2) this.lastMpIp = split[1];

                    for (int i = 0; i < this.keyMappings.length; ++i) {
                        if (split[0].equals("key_" + this.keyMappings[i].name)) {
                            this.keyMappings[i].key = Integer.parseInt(split[1]);
                        }
                    }
                }
                catch (final Exception e) {
                    System.out.println("Skipping bad option: " + line);
                }
            }
            br.close();
        }
        catch (final Exception e) {
            System.out.println("Failed to load options");
            e.printStackTrace();
        }
    }
    
    private float readFloat(final String s) {
        if (s.equals("true")) return 1.0f;
        if (s.equals("false")) return 0.0f;
        return Float.parseFloat(s);
    }
    
    public void save() {
        try {
            final PrintWriter pw = new PrintWriter(new FileWriter(this.optionsFile));

            pw.println("music:" + this.music);
            pw.println("sound:" + this.sound);
            pw.println("invertYMouse:" + this.invertYMouse);
            pw.println("mouseSensitivity:" + this.sensitivity);
            pw.println("viewDistance:" + this.viewDistance);
            pw.println("guiScale:" + this.guiScale);
            pw.println("bobView:" + this.bobView);
            pw.println("anaglyph3d:" + this.anaglyph3d);
            pw.println("advancedOpengl:" + this.advancedOpengl);
            pw.println("fpsLimit:" + this.limitFramerate);
            pw.println("difficulty:" + this.difficulty);
            pw.println("fancyGraphics:" + this.fancyGraphics);
            pw.println("ao:" + this.ambientOcclusion);
            pw.println("skin:" + this.skin);
            pw.println("lastServer:" + this.lastMpIp);

            for (int i = 0; i < this.keyMappings.length; ++i) {
                pw.println("key_" + this.keyMappings[i].name + ":" + this.keyMappings[i].key);
            }

            pw.close();
        }
        catch (final Exception e) {
            System.out.println("Failed to save options");
            e.printStackTrace();
        }
    }

    public enum Option
    {
        MUSIC("options.music", true, false),
        SOUND("options.sound", true, false),
        INVERT_MOUSE("options.invertMouse", false, true),
        SENSITIVITY("options.sensitivity", true, false),
        RENDER_DISTANCE("options.renderDistance", false, false),
        VIEW_BOBBING("options.viewBobbing", false, true),
        ANAGLYPH("options.anaglyph", false, true),
        ADVANCED_OPENGL("options.advancedOpengl", false, true),
        FRAMERATE_LIMIT("options.framerateLimit", false, false),
        DIFFICULTY("options.difficulty", false, false),
        GRAPHICS("options.graphics", false, false),
        AMBIENT_OCCLUSION("options.ao", false, true),
        GUI_SCALE("options.guiScale", false, false);

        private final boolean isProgress;
        private final boolean isBoolean;
        private final String captionId;

        public static Option getItem(final int id) {
            for (final Option option : values()) {
                if (option.getId() == id) {
                    return option;
                }
            }
            return null;
        }

        private Option(final String captionId, final boolean isProgress, final boolean isBoolean) {
            this.captionId = captionId;
            this.isProgress = isProgress;
            this.isBoolean = isBoolean;
        }

        public boolean isProgress() {
            return this.isProgress;
        }

        public boolean isBoolean() {
            return this.isBoolean;
        }

        public int getId() {
            return this.ordinal();
        }

        public String getCaptionId() {
            return this.captionId;
        }
    }
}
