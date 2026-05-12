// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import net.minecraft.locale.language.I18n;
import org.lwjgl.input.Keyboard;
import net.minecraft.locale.language.Language;
import java.io.File;

public class Options
{
    private static final String[] RENDER_DISTANCE_NAMES;
    private static final String[] DIFFICULTY_NAMES;
    private static final String[] GUI_SCALE;
    private static final String[] FRAMERATE_LIMITS;
    public float music;
    public float sound;
    public float sensitivity;
    public boolean invertYMouse;
    public int viewDistance;
    public boolean bobView;
    public boolean anaglyph3d;
    public boolean advancedOpengl;
    public int limitFramerate;
    public boolean fancyGraphics;
    public boolean ambientOcclusion;
    public String skin;
    public KeyMapping keyUp;
    public KeyMapping keyLeft;
    public KeyMapping keyDown;
    public KeyMapping keyRight;
    public KeyMapping keyJump;
    public KeyMapping keyBuild;
    public KeyMapping keyDrop;
    public KeyMapping keyChat;
    public KeyMapping keyFog;
    public KeyMapping keySneak;
    public KeyMapping[] keyMappings;
    protected Minecraft minecraft;
    private File optionsFile;
    public int difficulty;
    public boolean hideGui;
    public boolean thirdPersonView;
    public boolean renderDebug;
    public String lastMpIp;
    public boolean isFlying;
    public boolean smoothCamera;
    public boolean fixedCamera;
    public float flySpeed;
    public float cameraSpeed;
    public int guiScale;
    
    public Options(final Minecraft minecraft, final File parent) {
        this.music = 1.0f;
        this.sound = 1.0f;
        this.sensitivity = 0.5f;
        this.invertYMouse = false;
        this.viewDistance = 0;
        this.bobView = true;
        this.anaglyph3d = false;
        this.advancedOpengl = false;
        this.limitFramerate = 1;
        this.fancyGraphics = true;
        this.ambientOcclusion = true;
        this.skin = "Default";
        this.keyUp = new KeyMapping("key.forward", 17);
        this.keyLeft = new KeyMapping("key.left", 30);
        this.keyDown = new KeyMapping("key.back", 31);
        this.keyRight = new KeyMapping("key.right", 32);
        this.keyJump = new KeyMapping("key.jump", 57);
        this.keyBuild = new KeyMapping("key.inventory", 18);
        this.keyDrop = new KeyMapping("key.drop", 16);
        this.keyChat = new KeyMapping("key.chat", 20);
        this.keyFog = new KeyMapping("key.fog", 33);
        this.keySneak = new KeyMapping("key.sneak", 42);
        this.keyMappings = new KeyMapping[] { this.keyUp, this.keyLeft, this.keyDown, this.keyRight, this.keyJump, this.keySneak, this.keyDrop, this.keyBuild, this.keyChat, this.keyFog };
        this.difficulty = 2;
        this.hideGui = false;
        this.thirdPersonView = false;
        this.renderDebug = false;
        this.lastMpIp = "";
        this.isFlying = false;
        this.smoothCamera = false;
        this.fixedCamera = false;
        this.flySpeed = 1.0f;
        this.cameraSpeed = 1.0f;
        this.guiScale = 0;
        this.minecraft = minecraft;
        this.optionsFile = new File(parent, "options.txt");
        this.load();
    }
    
    public Options() {
        this.music = 1.0f;
        this.sound = 1.0f;
        this.sensitivity = 0.5f;
        this.invertYMouse = false;
        this.viewDistance = 0;
        this.bobView = true;
        this.anaglyph3d = false;
        this.advancedOpengl = false;
        this.limitFramerate = 1;
        this.fancyGraphics = true;
        this.ambientOcclusion = true;
        this.skin = "Default";
        this.keyUp = new KeyMapping("key.forward", 17);
        this.keyLeft = new KeyMapping("key.left", 30);
        this.keyDown = new KeyMapping("key.back", 31);
        this.keyRight = new KeyMapping("key.right", 32);
        this.keyJump = new KeyMapping("key.jump", 57);
        this.keyBuild = new KeyMapping("key.inventory", 18);
        this.keyDrop = new KeyMapping("key.drop", 16);
        this.keyChat = new KeyMapping("key.chat", 20);
        this.keyFog = new KeyMapping("key.fog", 33);
        this.keySneak = new KeyMapping("key.sneak", 42);
        this.keyMappings = new KeyMapping[] { this.keyUp, this.keyLeft, this.keyDown, this.keyRight, this.keyJump, this.keySneak, this.keyDrop, this.keyBuild, this.keyChat, this.keyFog };
        this.difficulty = 2;
        this.hideGui = false;
        this.thirdPersonView = false;
        this.renderDebug = false;
        this.lastMpIp = "";
        this.isFlying = false;
        this.smoothCamera = false;
        this.fixedCamera = false;
        this.flySpeed = 1.0f;
        this.cameraSpeed = 1.0f;
        this.guiScale = 0;
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
    
    public void set(final Options_Option option, final float sensitivity) {
        if (option == Options_Option.MUSIC) {
            this.music = sensitivity;
            this.minecraft.soundEngine.updateOptions();
        }
        if (option == Options_Option.SOUND) {
            this.sound = sensitivity;
            this.minecraft.soundEngine.updateOptions();
        }
        if (option == Options_Option.SENSITIVITY) {
            this.sensitivity = sensitivity;
        }
    }
    
    public void toggle(final Options_Option option, final int n) {
        if (option == Options_Option.INVERT_MOUSE) {
            this.invertYMouse = !this.invertYMouse;
        }
        if (option == Options_Option.RENDER_DISTANCE) {
            this.viewDistance = (this.viewDistance + n & 0x3);
        }
        if (option == Options_Option.GUI_SCALE) {
            this.guiScale = (this.guiScale + n & 0x3);
        }
        if (option == Options_Option.VIEW_BOBBING) {
            this.bobView = !this.bobView;
        }
        if (option == Options_Option.ADVANCED_OPENGL) {
            this.advancedOpengl = !this.advancedOpengl;
            this.minecraft.levelRenderer.allChanged();
        }
        if (option == Options_Option.ANAGLYPH) {
            this.anaglyph3d = !this.anaglyph3d;
            this.minecraft.textures.reloadAll();
        }
        if (option == Options_Option.FRAMERATE_LIMIT) {
            this.limitFramerate = (this.limitFramerate + n + 3) % 3;
        }
        if (option == Options_Option.DIFFICULTY) {
            this.difficulty = (this.difficulty + n & 0x3);
        }
        if (option == Options_Option.GRAPHICS) {
            this.fancyGraphics = !this.fancyGraphics;
            this.minecraft.levelRenderer.allChanged();
        }
        if (option == Options_Option.AMBIENT_OCCLUSION) {
            this.ambientOcclusion = !this.ambientOcclusion;
            this.minecraft.levelRenderer.allChanged();
        }
        this.save();
    }
    
    public float getProgressValue(final Options_Option option) {
        if (option == Options_Option.MUSIC) {
            return this.music;
        }
        if (option == Options_Option.SOUND) {
            return this.sound;
        }
        if (option == Options_Option.SENSITIVITY) {
            return this.sensitivity;
        }
        return 0.0f;
    }
    
    public boolean getBooleanValue(final Options_Option option) {
        switch (Options_GetBooleanValueSwitchObfuscation.arr[option.ordinal()]) {
            case 1: {
                return this.invertYMouse;
            }
            case 2: {
                return this.bobView;
            }
            case 3: {
                return this.anaglyph3d;
            }
            case 4: {
                return this.advancedOpengl;
            }
            case 5: {
                return this.ambientOcclusion;
            }
            default: {
                return false;
            }
        }
    }
    
    public String getMessage(final Options_Option option) {
        final Language instance = Language.getInstance();
        final String string = instance.getElement(option.getCaptionId()) + ": ";
        if (option.isProgress()) {
            final float progressValue = this.getProgressValue(option);
            if (option == Options_Option.SENSITIVITY) {
                if (progressValue == 0.0f) {
                    return string + instance.getElement("options.sensitivity.min");
                }
                if (progressValue == 1.0f) {
                    return string + instance.getElement("options.sensitivity.max");
                }
                return string + (int)(progressValue * 200.0f) + "%";
            }
            else {
                if (progressValue == 0.0f) {
                    return string + instance.getElement("options.off");
                }
                return string + (int)(progressValue * 100.0f) + "%";
            }
        }
        else if (option.isBoolean()) {
            if (this.getBooleanValue(option)) {
                return string + instance.getElement("options.on");
            }
            return string + instance.getElement("options.off");
        }
        else {
            if (option == Options_Option.RENDER_DISTANCE) {
                return string + instance.getElement(Options.RENDER_DISTANCE_NAMES[this.viewDistance]);
            }
            if (option == Options_Option.DIFFICULTY) {
                return string + instance.getElement(Options.DIFFICULTY_NAMES[this.difficulty]);
            }
            if (option == Options_Option.GUI_SCALE) {
                return string + instance.getElement(Options.GUI_SCALE[this.guiScale]);
            }
            if (option == Options_Option.FRAMERATE_LIMIT) {
                return string + I18n.get(Options.FRAMERATE_LIMITS[this.limitFramerate]);
            }
            if (option != Options_Option.GRAPHICS) {
                return string;
            }
            if (this.fancyGraphics) {
                return string + instance.getElement("options.graphics.fancy");
            }
            return string + instance.getElement("options.graphics.fast");
        }
    }
    
    public void load() {
        try {
            if (!this.optionsFile.exists()) {
                return;
            }
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(this.optionsFile));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                try {
                    final String[] split = line.split(":");
                    if (split[0].equals("music")) {
                        this.music = this.readFloat(split[1]);
                    }
                    if (split[0].equals("sound")) {
                        this.sound = this.readFloat(split[1]);
                    }
                    if (split[0].equals("mouseSensitivity")) {
                        this.sensitivity = this.readFloat(split[1]);
                    }
                    if (split[0].equals("invertYMouse")) {
                        this.invertYMouse = split[1].equals("true");
                    }
                    if (split[0].equals("viewDistance")) {
                        this.viewDistance = Integer.parseInt(split[1]);
                    }
                    if (split[0].equals("guiScale")) {
                        this.guiScale = Integer.parseInt(split[1]);
                    }
                    if (split[0].equals("bobView")) {
                        this.bobView = split[1].equals("true");
                    }
                    if (split[0].equals("anaglyph3d")) {
                        this.anaglyph3d = split[1].equals("true");
                    }
                    if (split[0].equals("advancedOpengl")) {
                        this.advancedOpengl = split[1].equals("true");
                    }
                    if (split[0].equals("fpsLimit")) {
                        this.limitFramerate = Integer.parseInt(split[1]);
                    }
                    if (split[0].equals("difficulty")) {
                        this.difficulty = Integer.parseInt(split[1]);
                    }
                    if (split[0].equals("fancyGraphics")) {
                        this.fancyGraphics = split[1].equals("true");
                    }
                    if (split[0].equals("ao")) {
                        this.ambientOcclusion = split[1].equals("true");
                    }
                    if (split[0].equals("skin")) {
                        this.skin = split[1];
                    }
                    if (split[0].equals("lastServer") && split.length >= 2) {
                        this.lastMpIp = split[1];
                    }
                    for (int i = 0; i < this.keyMappings.length; ++i) {
                        if (split[0].equals("key_" + this.keyMappings[i].name)) {
                            this.keyMappings[i].key = Integer.parseInt(split[1]);
                        }
                    }
                }
                catch (final Exception ex) {
                    System.out.println("Skipping bad option: " + line);
                }
            }
            bufferedReader.close();
        }
        catch (final Exception ex2) {
            System.out.println("Failed to load options");
            ex2.printStackTrace();
        }
    }
    
    private float readFloat(final String s) {
        if (s.equals("true")) {
            return 1.0f;
        }
        if (s.equals("false")) {
            return 0.0f;
        }
        return Float.parseFloat(s);
    }
    
    public void save() {
        try {
            final PrintWriter printWriter = new PrintWriter(new FileWriter(this.optionsFile));
            printWriter.println("music:" + this.music);
            printWriter.println("sound:" + this.sound);
            printWriter.println("invertYMouse:" + this.invertYMouse);
            printWriter.println("mouseSensitivity:" + this.sensitivity);
            printWriter.println("viewDistance:" + this.viewDistance);
            printWriter.println("guiScale:" + this.guiScale);
            printWriter.println("bobView:" + this.bobView);
            printWriter.println("anaglyph3d:" + this.anaglyph3d);
            printWriter.println("advancedOpengl:" + this.advancedOpengl);
            printWriter.println("fpsLimit:" + this.limitFramerate);
            printWriter.println("difficulty:" + this.difficulty);
            printWriter.println("fancyGraphics:" + this.fancyGraphics);
            printWriter.println("ao:" + this.ambientOcclusion);
            printWriter.println("skin:" + this.skin);
            printWriter.println("lastServer:" + this.lastMpIp);
            for (int i = 0; i < this.keyMappings.length; ++i) {
                printWriter.println("key_" + this.keyMappings[i].name + ":" + this.keyMappings[i].key);
            }
            printWriter.close();
        }
        catch (final Exception ex) {
            System.out.println("Failed to save options");
            ex.printStackTrace();
        }
    }
    
    static {
        RENDER_DISTANCE_NAMES = new String[] { "options.renderDistance.far", "options.renderDistance.normal", "options.renderDistance.short", "options.renderDistance.tiny" };
        DIFFICULTY_NAMES = new String[] { "options.difficulty.peaceful", "options.difficulty.easy", "options.difficulty.normal", "options.difficulty.hard" };
        GUI_SCALE = new String[] { "options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large" };
        FRAMERATE_LIMITS = new String[] { "performance.max", "performance.balanced", "performance.powersaver" };
    }
}
