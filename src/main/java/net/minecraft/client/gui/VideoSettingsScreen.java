// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.locale.language.Language;
import net.minecraft.client.Options_Option;
import net.minecraft.client.Options;

public class VideoSettingsScreen extends Screen
{
    private Screen lastScreen;
    protected String title;
    private Options options;
    private static Options_Option[] OPTIONS;
    
    public VideoSettingsScreen(final Screen lastScreen, final Options options) {
        this.title = "Video Settings";
        this.lastScreen = lastScreen;
        this.options = options;
    }
    
    @Override
    public void init() {
        final Language instance = Language.getInstance();
        this.title = instance.getElement("options.videoTitle");
        int n = 0;
        for (final Options_Option option : VideoSettingsScreen.OPTIONS) {
            if (!option.isProgress()) {
                this.buttons.add(new SmallButton(option.getId(), this.width / 2 - 155 + n % 2 * 160, this.height / 6 + 24 * (n >> 1), option, this.options.getMessage(option)));
            }
            else {
                this.buttons.add(new SlideButton(option.getId(), this.width / 2 - 155 + n % 2 * 160, this.height / 6 + 24 * (n >> 1), option, this.options.getMessage(option), this.options.getProgressValue(option)));
            }
            ++n;
        }
        this.buttons.add(new Button(200, this.width / 2 - 100, this.height / 6 + 168, instance.getElement("gui.done")));
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (!button.active) {
            return;
        }
        if (button.id < 100 && button instanceof SmallButton) {
            this.options.toggle(((SmallButton)button).getOption(), 1);
            button.msg = this.options.getMessage(Options_Option.getItem(button.id));
        }
        if (button.id == 200) {
            this.minecraft.options.save();
            this.minecraft.setScreen(this.lastScreen);
        }
        final ScreenSizeCalculator screenSizeCalculator = new ScreenSizeCalculator(this.minecraft.options, this.minecraft.width, this.minecraft.height);
        this.init(this.minecraft, screenSizeCalculator.getWidth(), screenSizeCalculator.getHeight());
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
        super.render(xm, ym, partialTick);
    }
    
    static {
        VideoSettingsScreen.OPTIONS = new Options_Option[] { Options_Option.GRAPHICS, Options_Option.RENDER_DISTANCE, Options_Option.AMBIENT_OCCLUSION, Options_Option.FRAMERATE_LIMIT, Options_Option.ANAGLYPH, Options_Option.VIEW_BOBBING, Options_Option.GUI_SCALE, Options_Option.ADVANCED_OPENGL };
    }
}
