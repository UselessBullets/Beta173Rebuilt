// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.locale.language.Language;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.client.GuiMessage;
import java.awt.Color;
import util.Mth;
import net.minecraft.client.Lighting;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import java.util.Random;
import java.util.List;
import net.minecraft.client.renderer.entity.ItemRenderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class Gui extends GuiComponent
{
    private static final int MAX_MESSAGE_WIDTH = 320;
    private static ItemRenderer itemRenderer = new ItemRenderer();
    private List<GuiMessage> guiMessages = new ArrayList<>();
    private Random random = new Random();
    private Minecraft minecraft;
    public String selectedName = null;
    private int tickCount = 0;
    private String overlayMessageString = "";
    private int overlayMessageTime = 0;
    private boolean nowPlayingColor = false;
    public float progress;
    float tbr = 1.0f;
    
    public Gui(final Minecraft minecraft) {
        this.minecraft = minecraft;
    }
    
    public void render(final float partialTick, final boolean mouseFree, final int xMouse, final int yMouse) {
        final ScreenSizeCalculator ssc = new ScreenSizeCalculator(this.minecraft.options, this.minecraft.width, this.minecraft.height);
        final int screenWidth = ssc.getWidth();
        final int screenHeight = ssc.getHeight();
        final int quickSelectWidth = 182;
        final int quickSelectHeight = 22;

        final Font font = this.minecraft.font;
        this.minecraft.gameRenderer.setupGuiScreen();

        glEnable(GL_BLEND);

        if (Minecraft.useFancyGraphics()) {
            this.renderVignette(this.minecraft.player.getBrightness(partialTick), screenWidth, screenHeight);
        }

        final ItemInstance headGear = this.minecraft.player.inventory.getArmor(3);
        if (!this.minecraft.options.thirdPersonView && headGear != null && headGear.id == Tile.pumpkin.id) {
            this.renderPumpkin(screenWidth, screenHeight);
        }

        final float pt = this.minecraft.player.oPortalTime + (this.minecraft.player.portalTime - this.minecraft.player.oPortalTime) * partialTick;
        if (pt > 0.0f) {
            this.renderTp(pt, screenWidth, screenHeight);
        }

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glBindTexture(GL_TEXTURE_2D, this.minecraft.textures.loadTexture("/gui/gui.png"));
        final Inventory inventory = this.minecraft.player.inventory;
        this.blitOffset = -90.0f;
        this.blit((screenWidth - quickSelectWidth) / 2, screenHeight - quickSelectHeight, 0, 0, quickSelectWidth, quickSelectHeight);
        this.blit((screenWidth - quickSelectWidth) / 2 - 1 + inventory.selected * 20, screenHeight - 22 - 1, 0, 22, 24, quickSelectWidth);

        glBindTexture(GL_TEXTURE_2D, this.minecraft.textures.loadTexture("/gui/icons.png"));
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);

        this.blit(screenWidth / 2 - 7, screenHeight / 2 - 7, 0, 0, 16, 16);
        glDisable(GL_BLEND);

        boolean blink = this.minecraft.player.invulnerableTime / 3 % 2 == 1;
        if (this.minecraft.player.invulnerableTime < 10) blink = false;

        final int health = this.minecraft.player.health;
        final int lastHealth = this.minecraft.player.lastHealth;
        this.random.setSeed(this.tickCount * 312871L);

        if (this.minecraft.gameMode.canHurtPlayer()) {
            final int armor = this.minecraft.player.getArmor();
            // render health and armor
            for (int i = 0; i < (Player.MAX_HEALTH / 2); ++i) {
                int yo = screenHeight - 32;
                if (armor > 0) {
                    final int xo = (screenWidth + quickSelectWidth) / 2 - i * 8 - 9;

                    // Useless - Below comment was in LCE leak, is amusing
                    // HEALTH
                    if (i * 2 + 1 < armor) this.blit(xo, yo, 34, 9, 9, 9);
                    if (i * 2 + 1 == armor) this.blit(xo, yo, 25, 9, 9, 9);
                    if (i * 2 + 1 > armor) this.blit(xo, yo, 16, 9, 9, 9);
                }
                final int healthTexBaseX = 16;

                int bg = 0;
                if (blink) bg = 1;

                final int xo = (screenWidth - quickSelectWidth) / 2 + i * 8;
                if (health <= 4) {
                    yo += this.random.nextInt(2);
                }

                this.blit(xo, yo, 16 + bg * 9, 0, 9, 9);
                if (blink) {
                    if (i * 2 + 1 < lastHealth) {
                        this.blit(xo, yo, 70, 0, 9, 9);
                    }
                    if (i * 2 + 1 == lastHealth) {
                        this.blit(xo, yo, 79, 0, 9, 9);
                    }
                }
                if (i * 2 + 1 < health) this.blit(xo, yo, healthTexBaseX + 4 * 9, 0, 9, 9); ;
                if (i * 2 + 1 == health) this.blit(xo, yo, healthTexBaseX + 5 * 9, 0, 9, 9);
            }

            // render air bubbles
            if (this.minecraft.player.isUnderLiquid(Material.water)) {
                int count = (int)Math.ceil((this.minecraft.player.airSupply - 2) * 10.0 / Player.TOTAL_AIR_SUPPLY);
                int extra = (int)Math.ceil(this.minecraft.player.airSupply * 10.0 / Player.TOTAL_AIR_SUPPLY) - count;
                for (int i = 0; i < count + extra; ++i) {
                    // Air bubbles
                    if (i < count) this.blit((screenWidth - quickSelectWidth) / 2 + i * 8, screenHeight - 32 - 9, 16, 18, 9, 9);
                    else this.blit((screenWidth - quickSelectWidth) / 2 + i * 8, screenHeight - 32 - 9, 25, 18, 9, 9);
                }
            }
        }

        glDisable(GL_BLEND);
        glEnable(GL_RESCALE_NORMAL);
        glPushMatrix();
        glRotatef(120.0f, 1.0f, 0.0f, 0.0f);
        Lighting.turnOn();
        glPopMatrix();
        for (int i = 0; i < 9; ++i) {
            final int x = screenWidth / 2 - 9 * 10 + i * 20 + 2;
            final int y = screenHeight - 16 - 3;
            this.renderSlot(i, x, y, partialTick);
        }
        Lighting.turnOff();
        glDisable(GL_RESCALE_NORMAL);

        // if the player is falling asleep we render a dark overlay
        if (this.minecraft.player.getSleepTimer() > 0) {
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_ALPHA_TEST);
            final int timer = this.minecraft.player.getSleepTimer();
            float amount = timer / (float) Player.SLEEP_DURATION;
            if (amount > 1.0f) {
                // waking up
                amount = 1.0f - (timer - Player.SLEEP_DURATION) / (float) Player.WAKE_UP_DURATION;
            }
            final int color = (int)(220.0f * amount) << 24 | 0x101020;
            this.fill(0, 0, screenWidth, screenHeight, color);
            glEnable(GL_ALPHA_TEST);
            glEnable(GL_DEPTH_TEST);
        }

        if (this.minecraft.options.renderDebug) {
            glPushMatrix();
            if (Minecraft.warezTime > 0L) glTranslatef(0.0f, 32.0f, 0.0f);
            font.drawShadow(Minecraft.VERSION_STRING + " (" + this.minecraft.fpsString + ")", 2, 2, 0xffffff);
            font.drawShadow(this.minecraft.gatherStats1(), 2, 2 + 10, 0xffffff);
            font.drawShadow(this.minecraft.gatherStats2(), 2, 2 + 20, 0xffffff);
            font.drawShadow(this.minecraft.gatherStats4(), 2, 2 + 30, 0xffffff);
            font.drawShadow(this.minecraft.gatherStats3(), 2, 2 + 40, 0xffffff);

            final long max = Runtime.getRuntime().maxMemory();
            final long total = Runtime.getRuntime().totalMemory();
            final long free = Runtime.getRuntime().freeMemory();
            final long used = total - free;

            String msg = "Used memory: " + used * 100L / max + "% (" + used / 1024L / 1024L + "MB) of " + max / 1024L / 1024L + "MB";
            this.drawString(font, msg, screenWidth - font.width(msg) - 2, 2, 0xe0e0e0);

            msg = "Allocated memory: " + total * 100L / max + "% (" + total / 1024L / 1024L + "MB)";
            this.drawString(font, msg, screenWidth - font.width(msg) - 2, 12, 0xe0e0e0);

            this.drawString(font, "x: " + this.minecraft.player.x, 2, 8 * 8, 0xe0e0e0);
            this.drawString(font, "y: " + this.minecraft.player.y, 2, 8 * 9, 0xe0e0e0);
            this.drawString(font, "z: " + this.minecraft.player.z, 2, 8 * 10, 0xe0e0e0);
            this.drawString(font, "f: " + (Mth.floor(this.minecraft.player.yRot * 4.0f / 360.0f + 0.5) & 0x3), 2, 8 * 11, 0xe0e0e0);
            glPopMatrix();
        }

        // Jukebox CD message
        if (this.overlayMessageTime > 0) {
            final float t = this.overlayMessageTime - partialTick;
            int alpha = (int)(t * 256.0f / 20.0f);
            if (alpha > 255) alpha = 255;
            if (alpha > 0) {
                glPushMatrix();
                glTranslatef((float)(screenWidth / 2), (float)(screenHeight - 48), 0.0f);
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

                int col = 0xffffff;
                if (this.nowPlayingColor) {
                    col = (Color.HSBtoRGB(t / 50.0f, 0.7f, 0.6f) & 0xFFFFFF);
                }

                font.draw(this.overlayMessageString, -font.width(this.overlayMessageString) / 2, -4, col + (alpha << 24));
                glDisable(GL_BLEND);
                glPopMatrix();
            }
        }

        int max = 10;
        boolean isChatting = false;
        if (this.minecraft.screen instanceof ChatScreen) {
            max = 20;
            isChatting = true;
        }

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_ALPHA_TEST);

        glPushMatrix();
        glTranslatef(0.0f, (float)(screenHeight - 48), 0.0f);
        for (int i = 0; i < this.guiMessages.size() && i < max; ++i) {
            if (this.guiMessages.get(i).ticks < 20 * 10 || isChatting) {
                double t = this.guiMessages.get(i).ticks / (20.0 * 10.0);
                t = 1 - t;
                t = t * 10;
                if (t < 0.0) t = 0.0;
                if (t > 1.0) t = 1.0;
                t = t * t;
                int alpha = (int)(255.0 * t);
                if (isChatting) alpha = 255;

                if (alpha > 0) {
                    final int x = 2;
                    final int y = -i * 9;

                    final String msg = this.guiMessages.get(i).string;
                    this.fill(x, y - 1, x + MAX_MESSAGE_WIDTH, y + 8, alpha / 2 << 24);
                    glEnable(GL_BLEND);

                    font.drawShadow(msg, x, y, 0xffffff + (alpha << 24));
                }
            }
        }
        glPopMatrix();
        glEnable(GL_ALPHA_TEST);
        glDisable(GL_BLEND);
    }
    
    private void renderPumpkin(final int w, final int h) {
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glDisable(GL_ALPHA_TEST);

        glBindTexture(GL_TEXTURE_2D, this.minecraft.textures.loadTexture("%blur%/misc/pumpkinblur.png"));
        final Tesselator t = Tesselator.instance;
        t.begin();
        t.vertexUV(0, h, -90.0, 0.0, 1.0);
        t.vertexUV(w, h, -90.0, 1.0, 1.0);
        t.vertexUV(w, 0, -90.0, 1.0, 0.0);
        t.vertexUV(0, 0, -90.0, 0.0, 0.0);
        t.end();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_ALPHA_TEST);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private void renderVignette(float br, final int w, final int h) {
        br = 1.0f - br;
        if (br < 0.0f) br = 0.0f;
        if (br > 1.0f) br = 1.0f;
        this.tbr += (br - this.tbr) * 0.01f;

        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_COLOR);
        glColor4f(this.tbr, this.tbr, this.tbr, 1.0f);
        glBindTexture(GL_TEXTURE_2D, this.minecraft.textures.loadTexture("%blur%/misc/vignette.png"));
        final Tesselator instance = Tesselator.instance;
        instance.begin();
        instance.vertexUV(0.0, h, -90.0, 0.0, 1.0);
        instance.vertexUV(w, h, -90.0, 1.0, 1.0);
        instance.vertexUV(w, 0.0, -90.0, 1.0, 0.0);
        instance.vertexUV(0.0, 0.0, -90.0, 0.0, 0.0);
        instance.end();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
    
    private void renderTp(float br, final int w, final int h) {
        if (br < 1.0f) {
            br *= br;
            br *= br;
            br = br * 0.8f + 0.2f;
        }

        glDisable(GL_ALPHA_TEST);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(1.0f, 1.0f, 1.0f, br);
        glBindTexture(GL_TEXTURE_2D, this.minecraft.textures.loadTexture("/terrain.png"));

        final float u0 = Tile.portalTile.tex % 16 / 16.0f;
        final float v0 = Tile.portalTile.tex / 16 / 16.0f;
        final float u1 = (Tile.portalTile.tex % 16 + 1) / 16.0f;
        final float v1 = (Tile.portalTile.tex / 16 + 1) / 16.0f;
        final Tesselator t = Tesselator.instance;
        t.begin();
        t.vertexUV(0.0, h, -90.0, u0, v1);
        t.vertexUV(w, h, -90.0, u1, v1);
        t.vertexUV(w, 0.0, -90.0, u1, v0);
        t.vertexUV(0.0, 0.0, -90.0, u0, v0);
        t.end();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_ALPHA_TEST);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private void renderSlot(final int slot, final int x, final int y, final float partialTick) {
        final ItemInstance item = this.minecraft.player.inventory.items[slot];
        if (item == null) return;

        final float pop = item.popTime - partialTick;
        if (pop > 0.0f) {
            glPushMatrix();
            final float squeeze = 1.0f + pop / 5.0f;
            glTranslatef((float)(x + 8), (float)(y + 12), 0.0f);
            glScalef(1.0f / squeeze, (squeeze + 1.0f) / 2.0f, 1.0f);
            glTranslatef((float)(-(x + 8)), (float)(-(y + 12)), 0.0f);
        }

        Gui.itemRenderer.renderGuiItem(this.minecraft.font, this.minecraft.textures, item, x, y);

        if (pop > 0.0f) {
            glPopMatrix();
        }

        Gui.itemRenderer.renderGuiItemDecorations(this.minecraft.font, this.minecraft.textures, item, x, y);
    }
    
    public void tick() {
        if (this.overlayMessageTime > 0) --this.overlayMessageTime;
        ++this.tickCount;

        for (int i = 0; i < this.guiMessages.size(); ++i) {
            this.guiMessages.get(i).ticks++;
        }
    }
    
    public void clearMessages() {
        this.guiMessages.clear();
    }
    
    public void addMessage(String str) {
        while (this.minecraft.font.width(str) > MAX_MESSAGE_WIDTH) {
            int i = 1;
            while (i < str.length() && this.minecraft.font.width(str.substring(0, i + 1)) <= MAX_MESSAGE_WIDTH) {
                ++i;
            }

            this.addMessage(str.substring(0, i));
            str = str.substring(i);
        }
        this.guiMessages.add(0, new GuiMessage(str));
        while (this.guiMessages.size() > 50) {
            this.guiMessages.remove(this.guiMessages.size() - 1);
        }
    }
    
    public void setNowPlaying(final String str) {
        this.overlayMessageString = "Now playing: " + str;
        this.overlayMessageTime = 20 * 3;
        this.nowPlayingColor = true;
    }
    
    public void displayClientMessage(final String messageId) {
        Language language = Language.getInstance();
        String languageString = language.getElement(messageId);
        this.addMessage(languageString);
    }

}
