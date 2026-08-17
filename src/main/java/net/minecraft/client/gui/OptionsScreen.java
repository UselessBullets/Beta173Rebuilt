// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.locale.language.Language;
import net.minecraft.client.Options.Option;
import net.minecraft.client.Options;

public class OptionsScreen extends Screen
{
    private static final int CONTROLS_BUTTON_ID = 100;
    private static final int VIDEO_BUTTON_ID = 101;
    private Screen lastScreen;
    protected String title = "Options";
    private Options options;
    private static Option[] OPTIONS = new Option[] { Option.MUSIC, Option.SOUND, Option.INVERT_MOUSE, Option.SENSITIVITY, Option.DIFFICULTY };
    
    public OptionsScreen(final Screen lastScreen, final Options options) {
        this.lastScreen = lastScreen;
        this.options = options;
    }
    
    @Override
    public void init() {
        final Language language = Language.getInstance();
        this.title = language.getElement("options.title");

        int position = 0;
        for (final Option item : OptionsScreen.OPTIONS) {
            if (!item.isProgress()) {
                this.buttons.add(new SmallButton(item.getId(), this.width / 2 - 155 + position % 2 * 160, this.height / 6 + 24 * (position >> 1), item, this.options.getMessage(item)));
            }
            else {
                this.buttons.add(new SlideButton(item.getId(), this.width / 2 - 155 + position % 2 * 160, this.height / 6 + 24 * (position >> 1), item, this.options.getMessage(item), this.options.getProgressValue(item)));
            }
            ++position;
        }

        this.buttons.add(new Button(VIDEO_BUTTON_ID, this.width / 2 - 100, this.height / 6 + 24 * 4 + 12, language.getElement("options.video")));
        this.buttons.add(new Button(CONTROLS_BUTTON_ID, this.width / 2 - 100, this.height / 6 + 24 * 5 + 12, language.getElement("options.controls")));
        this.buttons.add(new Button(200, this.width / 2 - 100, this.height / 6 + 24 * 7, language.getElement("gui.done")));
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (!button.active) return;
        if (button.id < 100 && button instanceof SmallButton) {
            this.options.toggle(((SmallButton)button).getOption(), 1);
            button.msg = this.options.getMessage(Option.getItem(button.id));
        }
        if (button.id == VIDEO_BUTTON_ID) {
            this.minecraft.options.save();
            this.minecraft.setScreen(new VideoSettingsScreen(this, this.options));
        }
        if (button.id == CONTROLS_BUTTON_ID) {
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

}
