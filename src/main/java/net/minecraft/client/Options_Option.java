// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

public enum Options_Option
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
    
    public static Options_Option getItem(final int n) {
        for (final Options_Option options_Option : values()) {
            if (options_Option.getId() == n) {
                return options_Option;
            }
        }
        return null;
    }
    
    private Options_Option(final String captionId, final boolean isProgress, final boolean isBoolean) {
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
