// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.locale.language.Language;
import net.minecraft.client.Options.Option;
import net.minecraft.client.Options;

public class OptionsScreen extends Screen
{
    private Screen lastScreen;
    protected String title;
    private Options options;
    private static Option[] OPTIONS;
    
    public OptionsScreen(final Screen lastScreeen, final Options options) {
        this.title = "Options";
        this.lastScreen = lastScreeen;
        this.options = options;
    }
    
    @Override
    public void init() {
        final Language instance = Language.getInstance();
        this.title = instance.getElement("options.title");
        int n = 0;
        for (final Option option : OptionsScreen.OPTIONS) {
            if (!option.isProgress()) {
                this.buttons.add(new SmallButton(option.getId(), this.width / 2 - 155 + n % 2 * 160, this.height / 6 + 24 * (n >> 1), option, this.options.getMessage(option)));
            }
            else {
                this.buttons.add(new SlideButton(option.getId(), this.width / 2 - 155 + n % 2 * 160, this.height / 6 + 24 * (n >> 1), option, this.options.getMessage(option), this.options.getProgressValue(option)));
            }
            ++n;
        }
        this.buttons.add(new Button(101, this.width / 2 - 100, this.height / 6 + 96 + 12, instance.getElement("options.video")));
        this.buttons.add(new Button(100, this.width / 2 - 100, this.height / 6 + 120 + 12, instance.getElement("options.controls")));
        this.buttons.add(new Button(200, this.width / 2 - 100, this.height / 6 + 168, instance.getElement("gui.done")));
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (!button.active) {
            return;
        }
        if (button.id < 100 && button instanceof SmallButton) {
            this.options.toggle(((SmallButton)button).getOption(), 1);
            button.msg = this.options.getMessage(Option.getItem(button.id));
        }
        if (button.id == 101) {
            this.minecraft.options.save();
            this.minecraft.setScreen(new VideoSettingsScreen(this, this.options));
        }
        if (button.id == 100) {
            this.minecraft.options.save();
            this.minecraft.setScreen(new ControlsScreen(this, this.options));
        }
        if (button.id == 200) {
            this.minecraft.options.save();
            this.minecraft.setScreen(this.lastScreen);
        }
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xffffff);
        super.render(xm, ym, partialTick);
    }
    
    static {
        OptionsScreen.OPTIONS = new Option[] { Option.MUSIC, Option.SOUND, Option.INVERT_MOUSE, Option.SENSITIVITY, Option.DIFFICULTY };
    }
}
