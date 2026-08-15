// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import util.Mth;
import net.minecraft.client.gui.achievement.StatsScreen;
import net.minecraft.client.gui.achievement.AchievementScreen;
import net.minecraft.client.title.TitleScreen;
import net.minecraft.stats.Stats;
import net.minecraft.locale.language.I18n;

public class PauseScreen extends Screen
{
    private int saveStep;
    private int visibleTime;
    
    public PauseScreen() {
        this.saveStep = 0;
        this.visibleTime = 0;
    }
    
    @Override
    public void init() {
        this.saveStep = 0;
        this.buttons.clear();
        final int n = -16;
        this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 120 + n, "Save and quit to title"));
        if (this.minecraft.isClientSide()) {
            this.buttons.get(0).msg = "Disconnect";
        }
        this.buttons.add(new Button(4, this.width / 2 - 100, this.height / 4 + 24 + n, "Back to game"));
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 96 + n, "Options..."));
        this.buttons.add(new Button(5, this.width / 2 - 100, this.height / 4 + 48 + n, 98, 20, I18n.get("gui.achievements")));
        this.buttons.add(new Button(6, this.width / 2 + 2, this.height / 4 + 48 + n, 98, 20, I18n.get("gui.stats")));
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (button.id == 0) {
            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
        }
        if (button.id == 1) {
            this.minecraft.stats.award(Stats.leaveGame, 1);
            if (this.minecraft.isClientSide()) {
                this.minecraft.level.disconnect();
            }
            this.minecraft.setLevel(null);
            this.minecraft.setScreen(new TitleScreen());
        }
        if (button.id == 4) {
            this.minecraft.setScreen(null);
            this.minecraft.grabMouse();
        }
        if (button.id == 5) {
            this.minecraft.setScreen(new AchievementScreen(this.minecraft.stats));
        }
        if (button.id == 6) {
            this.minecraft.setScreen(new StatsScreen(this, this.minecraft.stats));
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        ++this.visibleTime;
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.renderBackground();
        if (!this.minecraft.level.pauseSave(this.saveStep++) || this.visibleTime < 20) {
            final int n = (int)(255.0f * (Mth.sin((this.visibleTime % 10 + partialTick) / 10.0f * 3.1415927f * 2.0f) * 0.2f + 0.8f));
            this.drawString(this.font, "Saving level..", 8, this.height - 16, n << 16 | n << 8 | n);
        }
        this.drawCenteredString(this.font, "Game menu", this.width / 2, 40, 0xffffff);
        super.render(xm, ym, partialTick);
    }
}
