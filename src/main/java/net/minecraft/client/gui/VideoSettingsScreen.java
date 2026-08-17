// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.locale.language.Language;
import net.minecraft.client.Options.Option;
import net.minecraft.client.Options;

public class VideoSettingsScreen extends Screen
{
    private Screen lastScreen;
    protected String title = "Video Settings";
    private Options options;
    private static Option[] OPTIONS = new Option[] { Option.GRAPHICS, Option.RENDER_DISTANCE, Option.AMBIENT_OCCLUSION, Option.FRAMERATE_LIMIT, Option.ANAGLYPH, Option.VIEW_BOBBING, Option.GUI_SCALE, Option.ADVANCED_OPENGL };
    
    public VideoSettingsScreen(final Screen lastScreen, final Options options) {
        this.lastScreen = lastScreen;
        this.options = options;
    }
    
    @Override
    public void init() {
        final Language language = Language.getInstance();
        this.title = language.getElement("options.videoTitle");

        int position = 0;
        for (final Option option : VideoSettingsScreen.OPTIONS) {
            if (!option.isProgress()) {
                this.buttons.add(new SmallButton(option.getId(), this.width / 2 - 155 + position % 2 * 160, this.height / 6 + 24 * (position >> 1), option, this.options.getMessage(option)));
            }
            else {
                this.buttons.add(new SlideButton(option.getId(), this.width / 2 - 155 + position % 2 * 160, this.height / 6 + 24 * (position >> 1), option, this.options.getMessage(option), this.options.getProgressValue(option)));
            }
            ++position;
        }

        this.buttons.add(new Button(200, this.width / 2 - 100, this.height / 6 + 24 * 7, language.getElement("gui.done")));
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (!button.active) return;
        if (button.id < 100 && button instanceof SmallButton) {
            this.options.toggle(((SmallButton)button).getOption(), 1);
            button.msg = this.options.getMessage(Option.getItem(button.id));
        }
        if (button.id == 200) {
            this.minecraft.options.save();
            this.minecraft.setScreen(this.lastScreen);
        }
        final ScreenSizeCalculator ssc = new ScreenSizeCalculator(this.minecraft.options, this.minecraft.width, this.minecraft.height);
        int screenWidth = ssc.getWidth();
        int screenHeight = ssc.getHeight();
        this.init(this.minecraft, screenWidth, screenHeight);
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xffffff);
        super.render(xm, ym, partialTick);
    }

}
