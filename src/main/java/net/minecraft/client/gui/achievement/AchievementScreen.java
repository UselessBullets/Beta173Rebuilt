// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.achievement;

import net.minecraft.client.Lighting;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.stats.Achievement;
import net.minecraft.world.level.tile.Tile;
import java.util.Random;
import util.Mth;
import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Mouse;
import net.minecraft.client.gui.Button;
import net.minecraft.client.gui.SmallButton;
import net.minecraft.locale.language.I18n;
import net.minecraft.stats.Achievements;
import net.minecraft.stats.StatsCounter;
import net.minecraft.client.gui.Screen;

public class AchievementScreen extends Screen
{
    private static final int xMin;
    private static final int yMin;
    private static final int xMax;
    private static final int yMax;
    protected int imageWidth;
    protected int imageHeight;
    protected int xLastScroll;
    protected int yLastScroll;
    protected double xScrollO;
    protected double yScrollO;
    protected double xScrollP;
    protected double yScrollP;
    protected double xScrollTarget;
    protected double yScrollTarget;
    private int scrolling;
    private StatsCounter statsCounter;
    
    public AchievementScreen(final StatsCounter statsCounter) {
        this.imageWidth = 256;
        this.imageHeight = 202;
        this.xLastScroll = 0;
        this.yLastScroll = 0;
        this.scrolling = 0;
        this.statsCounter = statsCounter;
        final int n = 141;
        final int n2 = 141;
        final double xScrollO = Achievements.openInventory.x * 24 - n / 2 - 12;
        this.xScrollTarget = xScrollO;
        this.xScrollP = xScrollO;
        this.xScrollO = xScrollO;
        final double yScrollO = Achievements.openInventory.y * 24 - n2 / 2;
        this.yScrollTarget = yScrollO;
        this.yScrollP = yScrollO;
        this.yScrollO = yScrollO;
    }
    
    @Override
    public void init() {
        this.buttons.clear();
        this.buttons.add(new SmallButton(1, this.width / 2 + 24, this.height / 2 + 74, 80, 20, I18n.get("gui.done")));
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (button.id == 1) {
            this.minecraft.setScreen(null);
            this.minecraft.grabMouse();
        }
        super.buttonClicked(button);
    }
    
    @Override
    protected void keyPressed(final char ch, final int eventKey) {
        if (eventKey == this.minecraft.options.keyBuild.key) {
            this.minecraft.setScreen(null);
            this.minecraft.grabMouse();
        }
        else {
            super.keyPressed(ch, eventKey);
        }
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        if (Mouse.isButtonDown(0)) {
            final int n = (this.width - this.imageWidth) / 2;
            final int n2 = (this.height - this.imageHeight) / 2;
            final int n3 = n + 8;
            final int n4 = n2 + 17;
            if ((this.scrolling == 0 || this.scrolling == 1) && xm >= n3 && xm < n3 + 224 && ym >= n4 && ym < n4 + 155) {
                if (this.scrolling == 0) {
                    this.scrolling = 1;
                }
                else {
                    this.xScrollP -= xm - this.xLastScroll;
                    this.yScrollP -= ym - this.yLastScroll;
                    final double xScrollP = this.xScrollP;
                    this.xScrollO = xScrollP;
                    this.xScrollTarget = xScrollP;
                    final double yScrollP = this.yScrollP;
                    this.yScrollO = yScrollP;
                    this.yScrollTarget = yScrollP;
                }
                this.xLastScroll = xm;
                this.yLastScroll = ym;
            }
            if (this.xScrollTarget < AchievementScreen.xMin) {
                this.xScrollTarget = AchievementScreen.xMin;
            }
            if (this.yScrollTarget < AchievementScreen.yMin) {
                this.yScrollTarget = AchievementScreen.yMin;
            }
            if (this.xScrollTarget >= AchievementScreen.xMax) {
                this.xScrollTarget = AchievementScreen.xMax - 1;
            }
            if (this.yScrollTarget >= AchievementScreen.yMax) {
                this.yScrollTarget = AchievementScreen.yMax - 1;
            }
        }
        else {
            this.scrolling = 0;
        }
        this.renderBackground();
        this.renderBg(xm, ym, partialTick);
        GL11.glDisable(2896);
        GL11.glDisable(2929);
        this.renderLabels();
        GL11.glEnable(2896);
        GL11.glEnable(2929);
    }
    
    @Override
    public void tick() {
        this.xScrollO = this.xScrollP;
        this.yScrollO = this.yScrollP;
        final double n = this.xScrollTarget - this.xScrollP;
        final double n2 = this.yScrollTarget - this.yScrollP;
        if (n * n + n2 * n2 < 4.0) {
            this.xScrollP += n;
            this.yScrollP += n2;
        }
        else {
            this.xScrollP += n * 0.85;
            this.yScrollP += n2 * 0.85;
        }
    }
    
    protected void renderLabels() {
        this.font.draw("Achievements", (this.width - this.imageWidth) / 2 + 15, (this.height - this.imageHeight) / 2 + 5, 4210752);
    }
    
    protected void renderBg(final int xm, final int ym, final float partialTick) {
        int n = Mth.floor(this.xScrollO + (this.xScrollP - this.xScrollO) * partialTick);
        int n2 = Mth.floor(this.yScrollO + (this.yScrollP - this.yScrollO) * partialTick);
        if (n < AchievementScreen.xMin) {
            n = AchievementScreen.xMin;
        }
        if (n2 < AchievementScreen.yMin) {
            n2 = AchievementScreen.yMin;
        }
        if (n >= AchievementScreen.xMax) {
            n = AchievementScreen.xMax - 1;
        }
        if (n2 >= AchievementScreen.yMax) {
            n2 = AchievementScreen.yMax - 1;
        }
        final int loadTexture = this.minecraft.textures.loadTexture("/terrain.png");
        final int loadTexture2 = this.minecraft.textures.loadTexture("/achievement/bg.png");
        final int x = (this.width - this.imageWidth) / 2;
        final int y = (this.height - this.imageHeight) / 2;
        final int n3 = x + 16;
        final int n4 = y + 17;
        this.blitOffset = 0.0f;
        GL11.glDepthFunc(518);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 0.0f, -200.0f);
        GL11.glEnable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        this.minecraft.textures.bind(loadTexture);
        final int n5 = n + 288 >> 4;
        final int n6 = n2 + 288 >> 4;
        final int n7 = (n + 288) % 16;
        final int n8 = (n2 + 288) % 16;
        final Random random = new Random();
        for (int n9 = 0; n9 * 16 - n8 < 155; ++n9) {
            final float n10 = 0.6f - (n6 + n9) / 25.0f * 0.3f;
            GL11.glColor4f(n10, n10, n10, 1.0f);
            for (int n11 = 0; n11 * 16 - n7 < 224; ++n11) {
                random.setSeed(1234 + n5 + n11);
                random.nextInt();
                final int n12 = random.nextInt(1 + n6 + n9) + (n6 + n9) / 2;
                int n13 = Tile.sand.tex;
                if (n12 > 37 || n6 + n9 == 35) {
                    n13 = Tile.unbreakable.tex;
                }
                else if (n12 == 22) {
                    if (random.nextInt(2) == 0) {
                        n13 = Tile.emeraldOre.tex;
                    }
                    else {
                        n13 = Tile.redStoneOre.tex;
                    }
                }
                else if (n12 == 10) {
                    n13 = Tile.ironOre.tex;
                }
                else if (n12 == 8) {
                    n13 = Tile.coalOre.tex;
                }
                else if (n12 > 4) {
                    n13 = Tile.rock.tex;
                }
                else if (n12 > 0) {
                    n13 = Tile.dirt.tex;
                }
                this.blit(n3 + n11 * 16 - n7, n4 + n9 * 16 - n8, n13 % 16 << 4, n13 >> 4 << 4, 16, 16);
            }
        }
        GL11.glEnable(2929);
        GL11.glDepthFunc(515);
        GL11.glDisable(3553);
        for (int i = 0; i < Achievements.achievements.size(); ++i) {
            final Achievement achievement = Achievements.achievements.get(i);
            if (achievement.requires != null) {
                final int x2 = achievement.x * 24 - n + 11 + n3;
                final int n14 = achievement.y * 24 - n2 + 11 + n4;
                final int n15 = achievement.requires.x * 24 - n + 11 + n3;
                final int y2 = achievement.requires.y * 24 - n2 + 11 + n4;
                final boolean hasTaken = this.statsCounter.hasTaken(achievement);
                final boolean canTake = this.statsCounter.canTake(achievement);
                final int n16 = (Math.sin(System.currentTimeMillis() % 600L / 600.0 * 3.141592653589793 * 2.0) > 0.6) ? 255 : 130;
                int n17;
                if (hasTaken) {
                    n17 = -9408400;
                }
                else if (canTake) {
                    n17 = 65280 + (n16 << 24);
                }
                else {
                    n17 = -16777216;
                }
                this.hLine(x2, n15, n14, n17);
                this.vLine(n15, n14, y2, n17);
            }
        }
        Achievement achievement2 = null;
        final ItemRenderer itemRenderer = new ItemRenderer();
        GL11.glPushMatrix();
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        Lighting.turnOn();
        GL11.glPopMatrix();
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        for (int j = 0; j < Achievements.achievements.size(); ++j) {
            final Achievement achievement3 = Achievements.achievements.get(j);
            final int n18 = achievement3.x * 24 - n;
            final int n19 = achievement3.y * 24 - n2;
            if (n18 >= -24 && n19 >= -24 && n18 <= 224 && n19 <= 155) {
                if (this.statsCounter.hasTaken(achievement3)) {
                    final float n20 = 1.0f;
                    GL11.glColor4f(n20, n20, n20, 1.0f);
                }
                else if (this.statsCounter.canTake(achievement3)) {
                    final float n21 = (Math.sin(System.currentTimeMillis() % 600L / 600.0 * 3.141592653589793 * 2.0) < 0.6) ? 0.6f : 0.8f;
                    GL11.glColor4f(n21, n21, n21, 1.0f);
                }
                else {
                    final float n22 = 0.3f;
                    GL11.glColor4f(n22, n22, n22, 1.0f);
                }
                this.minecraft.textures.bind(loadTexture2);
                final int n23 = n3 + n18;
                final int n24 = n4 + n19;
                if (achievement3.isGolden()) {
                    this.blit(n23 - 2, n24 - 2, 26, 202, 26, 26);
                }
                else {
                    this.blit(n23 - 2, n24 - 2, 0, 202, 26, 26);
                }
                if (!this.statsCounter.canTake(achievement3)) {
                    final float n25 = 0.1f;
                    GL11.glColor4f(n25, n25, n25, 1.0f);
                    itemRenderer.setColor = false;
                }
                GL11.glEnable(2896);
                GL11.glEnable(2884);
                itemRenderer.renderGuiItem(this.minecraft.font, this.minecraft.textures, achievement3.icon, n23 + 3, n24 + 3);
                GL11.glDisable(2896);
                if (!this.statsCounter.canTake(achievement3)) {
                    itemRenderer.setColor = true;
                }
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                if (xm >= n3 && ym >= n4 && xm < n3 + 224 && ym < n4 + 155 && xm >= n23 && xm <= n23 + 22 && ym >= n24 && ym <= n24 + 22) {
                    achievement2 = achievement3;
                }
            }
        }
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.textures.bind(loadTexture2);
        this.blit(x, y, 0, 0, this.imageWidth, this.imageHeight);
        GL11.glPopMatrix();
        this.blitOffset = 0.0f;
        GL11.glDepthFunc(515);
        GL11.glDisable(2929);
        GL11.glEnable(3553);
        super.render(xm, ym, partialTick);
        if (achievement2 != null) {
            final Achievement achievement4 = achievement2;
            final String name = achievement4.name;
            final String description = achievement4.getDescription();
            final int n26 = xm + 12;
            final int y3 = ym - 4;
            if (this.statsCounter.canTake(achievement4)) {
                final int max = Math.max(this.font.width(name), 120);
                int wordWrapHeight = this.font.wordWrapHeight(description, max);
                if (this.statsCounter.hasTaken(achievement4)) {
                    wordWrapHeight += 12;
                }
                this.fillGradient(n26 - 3, y3 - 3, n26 + max + 3, y3 + wordWrapHeight + 3 + 12, -1073741824, -1073741824);
                this.font.drawWordWrapInternal(description, n26, y3 + 12, max, -6250336);
                if (this.statsCounter.hasTaken(achievement4)) {
                    this.font.drawShadow(I18n.get("achievement.taken"), n26, y3 + wordWrapHeight + 4, -7302913);
                }
            }
            else {
                final int max2 = Math.max(this.font.width(name), 120);
                final String value = I18n.get("achievement.requires", achievement4.requires.name);
                this.fillGradient(n26 - 3, y3 - 3, n26 + max2 + 3, y3 + this.font.wordWrapHeight(value, max2) + 12 + 3, -1073741824, -1073741824);
                this.font.drawWordWrapInternal(value, n26, y3 + 12, max2, -9416624);
            }
            this.font.drawShadow(name, n26, y3, this.statsCounter.canTake(achievement4) ? (achievement4.isGolden() ? -128 : -1) : (achievement4.isGolden() ? -8355776 : -8355712));
        }
        GL11.glEnable(2929);
        GL11.glEnable(2896);
        Lighting.turnOff();
    }
    
    @Override
    public boolean isPauseScreen() {
        return true;
    }
    
    static {
        xMin = Achievements.xMin * 24 - 112;
        yMin = Achievements.yMin * 24 - 112;
        xMax = Achievements.xMax * 24 - 77;
        yMax = Achievements.yMax * 24 - 77;
    }
}
