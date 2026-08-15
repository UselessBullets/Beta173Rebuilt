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
import org.lwjgl.input.Mouse;
import net.minecraft.client.gui.Button;
import net.minecraft.client.gui.SmallButton;
import net.minecraft.locale.language.I18n;
import net.minecraft.stats.Achievements;
import net.minecraft.stats.StatsCounter;
import net.minecraft.client.gui.Screen;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class AchievementScreen extends Screen
{
    private static final int BIGMAP_X = 16;
    private static final int BIGMAP_Y = 17;
    private static final int BIGMAP_WIDTH = 224;
    private static final int BIGMAP_HEIGHT = 155;

    // number of pixels per achievement
    private static final int ACHIEVEMENT_COORD_SCALE = 24;
    private static final int EDGE_VALUE_X = Achievements.ACHIEVEMENT_WIDTH_POSITION * ACHIEVEMENT_COORD_SCALE;
    private static final int EDGE_VALUE_Y = Achievements.ACHIEVEMENT_HEIGHT_POSITION * ACHIEVEMENT_COORD_SCALE;
    private static final int xMin;
    private static final int yMin;
    private static final int xMax;
    private static final int yMax;
    private static final int MAX_BG_TILE_Y = (EDGE_VALUE_Y * 2 - 1) / 16;
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
        final int wBigMap = 141;
        final int hBigMap = 141;


        this.xScrollO = this.xScrollP = this.xScrollTarget = Achievements.openInventory.x * ACHIEVEMENT_COORD_SCALE - wBigMap / 2 - 12;
        this.yScrollO = this.yScrollP = this.yScrollTarget = Achievements.openInventory.y * ACHIEVEMENT_COORD_SCALE - hBigMap / 2;
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
            final int xo = (this.width - this.imageWidth) / 2;
            final int yo = (this.height - this.imageHeight) / 2;

            final int xBigMap = xo + 8;
            final int yBigMap = yo + 17;

            if ((this.scrolling == 0 || this.scrolling == 1) && xm >= xBigMap && xm < xBigMap + BIGMAP_WIDTH && ym >= yBigMap && ym < yBigMap + BIGMAP_HEIGHT) {
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

        glDisable(GL_LIGHTING);
        glDisable(GL_DEPTH_TEST);

        this.renderLabels();

        glEnable(GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
    }
    
    @Override
    public void tick() {
        this.xScrollO = this.xScrollP;
        this.yScrollO = this.yScrollP;

        final double xd = this.xScrollTarget - this.xScrollP;
        final double yd = this.yScrollTarget - this.yScrollP;
        if (xd * xd + yd * yd < 4.0) {
            this.xScrollP += xd;
            this.yScrollP += yd;
        }
        else {
            this.xScrollP += xd * 0.85;
            this.yScrollP += yd * 0.85;
        }
    }
    
    protected void renderLabels() {
        this.font.draw("Achievements", (this.width - this.imageWidth) / 2 + 15, (this.height - this.imageHeight) / 2 + 5, 0x404040);
    }
    
    protected void renderBg(final int xm, final int ym, final float partialTick) {
        int xScroll = Mth.floor(this.xScrollO + (this.xScrollP - this.xScrollO) * partialTick);
        int yScroll = Mth.floor(this.yScrollO + (this.yScrollP - this.yScrollO) * partialTick);

        if (xScroll < AchievementScreen.xMin) xScroll = AchievementScreen.xMin;
        if (yScroll < AchievementScreen.yMin) yScroll = AchievementScreen.yMin;
        if (xScroll >= AchievementScreen.xMax) xScroll = AchievementScreen.xMax - 1;
        if (yScroll >= AchievementScreen.yMax) yScroll = AchievementScreen.yMax - 1;

        final int terrainTex = this.minecraft.textures.loadTexture("/terrain.png");
        final int tex = this.minecraft.textures.loadTexture("/achievement/bg.png");

        final int xo = (this.width - this.imageWidth) / 2;
        final int yo = (this.height - this.imageHeight) / 2;

        final int xBigMap = xo + BIGMAP_X;
        final int yBigMap = yo + BIGMAP_Y;
        this.blitOffset = 0.0f;
        glDepthFunc(GL_GEQUAL);
        glPushMatrix();
        glTranslatef(0.0f, 0.0f, -200.0f);

        {
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_LIGHTING);
            glEnable(GL_RESCALE_NORMAL);
            glEnable(GL_COLOR_MATERIAL);
            this.minecraft.textures.bind(terrainTex);

            final int leftTile = xScroll + EDGE_VALUE_X >> 4;
            final int topTile = yScroll + EDGE_VALUE_Y >> 4;
            final int xMob = (xScroll + EDGE_VALUE_X) % 16;
            final int yMod = (yScroll + EDGE_VALUE_Y) % 16;

            final int rockLevel = (Achievements.ACHIEVEMENT_HEIGHT_POSITION * 4) / 10;
            final int coalLevel = (Achievements.ACHIEVEMENT_HEIGHT_POSITION * 7) / 10;
            final int ironLevel = (Achievements.ACHIEVEMENT_HEIGHT_POSITION * 9) / 10;
            final int emeraldLevel = (Achievements.ACHIEVEMENT_HEIGHT_POSITION * 19) / 10;
            final int bedrockLevel = (Achievements.ACHIEVEMENT_HEIGHT_POSITION * 31) / 10;

            final Random random = new Random();
            for (int tileY = 0; tileY * 16 - yMod < BIGMAP_HEIGHT; ++tileY) {
                final float amount = 0.6f - (topTile + tileY) / (float)(Achievements.ACHIEVEMENT_HEIGHT_POSITION * 2 + 1) * 0.3f;
                glColor4f(amount, amount, amount, 1.0f);

                for (int tileX = 0; tileX * 16 - xMob < BIGMAP_WIDTH; ++tileX) {
                    random.setSeed(1234 + leftTile + tileX);
                    random.nextInt();
                    final int heightValue = random.nextInt(1 + topTile + tileY) + (topTile + tileY) / 2;
                    int tileType = Tile.sand.tex;

                    if (heightValue > bedrockLevel || topTile + tileY == MAX_BG_TILE_Y) {
                        tileType = Tile.unbreakable.tex;
                    } else if (heightValue == emeraldLevel) {
                        if (random.nextInt(2) == 0) {
                            tileType = Tile.emeraldOre.tex;
                        } else {
                            tileType = Tile.redStoneOre.tex;
                        }
                    } else if (heightValue == ironLevel) {
                        tileType = Tile.ironOre.tex;
                    } else if (heightValue == coalLevel) {
                        tileType = Tile.coalOre.tex;
                    } else if (heightValue > rockLevel) {
                        tileType = Tile.rock.tex;
                    } else if (heightValue > 0) {
                        tileType = Tile.dirt.tex;
                    }
                    this.blit(xBigMap + tileX * 16 - xMob, yBigMap + tileY * 16 - yMod, tileType % 16 << 4, tileType >> 4 << 4, 16, 16);
                }
            }
        }
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glDisable(GL_TEXTURE_2D);

        for (int i = 0; i < Achievements.achievements.size(); ++i) {
            final Achievement achievement = Achievements.achievements.get(i);
            if (achievement.requires == null) continue;

            final int x1 = achievement.x * ACHIEVEMENT_COORD_SCALE - xScroll + 11 + xBigMap;
            final int y1 = achievement.y * ACHIEVEMENT_COORD_SCALE - yScroll + 11 + yBigMap;

            final int x2 = achievement.requires.x * ACHIEVEMENT_COORD_SCALE - xScroll + 11 + xBigMap;
            final int y2 = achievement.requires.y * ACHIEVEMENT_COORD_SCALE - yScroll + 11 + yBigMap;

            final boolean hasTaken = this.statsCounter.hasTaken(achievement);
            final boolean canTake = this.statsCounter.canTake(achievement);

            final int alph = (Math.sin(System.currentTimeMillis() % 600L / 600.0 * Math.PI * 2.0) > 0.6) ? 255 : 130;
            int color;
            if (hasTaken) color = 0xff707070;
            else if (canTake) color = 0x00ff00 + (alph << 24);
            else color = 0xff000000;

            this.hLine(x1, x2, y1, color);
            this.vLine(x2, y1, y2, color);
        }

        Achievement hoveredAchievement = null;
        final ItemRenderer itemRenderer = new ItemRenderer();

        glPushMatrix();
        glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        Lighting.turnOn();
        glPopMatrix();
        glDisable(GL_LIGHTING);
        glEnable(GL_RESCALE_NORMAL);
        glEnable(GL_COLOR_MATERIAL);
        
        for (int j = 0; j < Achievements.achievements.size(); ++j) {
            final Achievement ach = Achievements.achievements.get(j);
            final int x = ach.x * ACHIEVEMENT_COORD_SCALE - xScroll;
            final int y = ach.y * ACHIEVEMENT_COORD_SCALE - yScroll;
            if (x >= -24 && y >= -24 && x <= BIGMAP_WIDTH && y <= BIGMAP_HEIGHT) {
                if (this.statsCounter.hasTaken(ach)) {
                    final float br = 1.0f;
                    glColor4f(br, br, br, 1.0f);
                }
                else if (this.statsCounter.canTake(ach)) {
                    final float br = (Math.sin(System.currentTimeMillis() % 600L / 600.0 * 3.141592653589793 * 2.0) < 0.6) ? 0.6f : 0.8f;
                    glColor4f(br, br, br, 1.0f);
                }
                else {
                    final float br = 0.3f;
                    glColor4f(br, br, br, 1.0f);
                }
                
                this.minecraft.textures.bind(tex);
                final int xx = xBigMap + x;
                final int yy = yBigMap + y;
                if (ach.isGolden()) {
                    this.blit(xx - 2, yy - 2, 26, 202, 26, 26);
                }
                else {
                    this.blit(xx - 2, yy - 2, 0, 202, 26, 26);
                }
                
                if (!this.statsCounter.canTake(ach)) {
                    final float br = 0.1f;
                    glColor4f(br, br, br, 1.0f);
                    itemRenderer.setColor = false;
                }
                glEnable(GL_LIGHTING);
                glEnable(GL_CULL_FACE);
                itemRenderer.renderGuiItem(this.minecraft.font, this.minecraft.textures, ach.icon, xx + 3, yy + 3);
                glDisable(GL_LIGHTING);
                if (!this.statsCounter.canTake(ach)) {
                    itemRenderer.setColor = true;
                }
                glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

                if (xm >= xBigMap && ym >= yBigMap && xm < xBigMap + BIGMAP_WIDTH && ym < yBigMap + BIGMAP_HEIGHT && xm >= xx && xm <= xx + 22 && ym >= yy && ym <= yy + 22) {
                    hoveredAchievement = ach;
                }
            }
        }
        
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.textures.bind(tex);
        this.blit(xo, yo, 0, 0, this.imageWidth, this.imageHeight);

        glPopMatrix();

        this.blitOffset = 0.0f;
        glDepthFunc(GL_LEQUAL);

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        super.render(xm, ym, partialTick);

        if (hoveredAchievement != null) {
            final Achievement ach = hoveredAchievement;
            final String name = ach.name;
            final String description = ach.getDescription();

            final int x = xm + 12;
            final int y = ym - 4;

            if (this.statsCounter.canTake(ach)) {
                final int max = Math.max(this.font.width(name), 120);
                int height = this.font.wordWrapHeight(description, max);
                if (this.statsCounter.hasTaken(ach)) {
                    height += 12;
                }
                this.fillGradient(x - 3, y - 3, x + max + 3, y + height + 3 + 12, 0xc0000000, 0xc0000000);

                this.font.drawWordWrapInternal(description, x, y + 12, max, 0xffa0a0a0);
                if (this.statsCounter.hasTaken(ach)) {
                    this.font.drawShadow(I18n.get("achievement.taken"), x, y + height + 4, 0xff9090ff);
                }
            }
            else {
                final int width = Math.max(this.font.width(name), 120);
                final String value = I18n.get("achievement.requires", ach.requires.name);
                this.fillGradient(x - 3, y - 3, x + width + 3, y + this.font.wordWrapHeight(value, width) + 12 + 3, 0xc0000000, 0xc0000000);
                this.font.drawWordWrapInternal(value, x, y + 12, width, 0xff705050);
            }
            this.font.drawShadow(name, x, y, this.statsCounter.canTake(ach) ? (ach.isGolden() ? -128 : -1) : (ach.isGolden() ? 0xff808040 : 0xff808080));
        }
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LIGHTING);
        Lighting.turnOff();
    }
    
    @Override
    public boolean isPauseScreen() {
        return true;
    }
    
    static {
        xMin = Achievements.xMin * ACHIEVEMENT_COORD_SCALE - BIGMAP_WIDTH / 2;
        yMin = Achievements.yMin * ACHIEVEMENT_COORD_SCALE - BIGMAP_WIDTH / 2;
        xMax = Achievements.xMax * ACHIEVEMENT_COORD_SCALE - BIGMAP_HEIGHT / 2;
        yMax = Achievements.yMax * ACHIEVEMENT_COORD_SCALE - BIGMAP_HEIGHT / 2;
    }
}
