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
    private int saveStep = 0;
    private int visibleTime = 0;

    @Override
    public void init() {
        this.saveStep = 0;
        this.buttons.clear();
        final int yo = -16;
        this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 24 * 5 + yo, "Save and quit to title"));
        if (this.minecraft.isClientSide()) {
            this.buttons.get(0).msg = "Disconnect";
        }

        this.buttons.add(new Button(4, this.width / 2 - 100, this.height / 4 + 24 * 1 + yo, "Back to game"));
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 24 * 4 + yo, "Options..."));
        this.buttons.add(new Button(5, this.width / 2 - 100, this.height / 4 + 24 * 2 + yo, 98, 20, I18n.get("gui.achievements")));
        this.buttons.add(new Button(6, this.width / 2 + 2, this.height / 4 + 24 * 2 + yo, 98, 20, I18n.get("gui.stats")));
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
    public void render(final int xm, final int ym, final float a) {
        this.renderBackground();

        boolean isSaving = !this.minecraft.level.pauseSave(this.saveStep++);
        if (isSaving || this.visibleTime < 20) {
            float col = (this.visibleTime % 10 + a) / 10.0f;
            col = Mth.sin(col * Mth.PI * 2.0f) * 0.2f + 0.8f;
            final int br = (int)(255.0f * col);

            this.drawString(this.font, "Saving level..", 8, this.height - 16, br << 16 | br << 8 | br);
        }

        this.drawCenteredString(this.font, "Game menu", this.width / 2, 40, 0xffffff);

        super.render(xm, ym, a);
    }
}
