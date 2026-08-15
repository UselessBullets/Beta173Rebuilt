// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.locale.language.Language;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.entity.player.Inventory;
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
    private static ItemRenderer itemRenderer;
    private List<GuiMessage> guiMessages;
    private Random random;
    private Minecraft minecraft;
    public String selectedName;
    private int tickCount;
    private String nowPlayingString;
    private int nowPlayingTime;
    private boolean nowPlayingColor;
    public float progress;
    float tbr;
    
    public Gui(final Minecraft minecraft) {
        this.guiMessages = new ArrayList();
        this.random = new Random();
        this.selectedName = null;
        this.tickCount = 0;
        this.nowPlayingString = "";
        this.nowPlayingTime = 0;
        this.nowPlayingColor = false;
        this.tbr = 1.0f;
        this.minecraft = minecraft;
    }
    
    public void render(final float partialTick, final boolean mouseFree, final int xMouse, final int yMouse) {
        final ScreenSizeCalculator screenSizeCalculator = new ScreenSizeCalculator(this.minecraft.options, this.minecraft.width, this.minecraft.height);
        final int width = screenSizeCalculator.getWidth();
        final int height = screenSizeCalculator.getHeight();
        final Font font = this.minecraft.font;
        this.minecraft.gameRenderer.setupGuiScreen();
        glEnable(GL_BLEND);
        if (Minecraft.useFancyGraphics()) {
            this.renderVignette(this.minecraft.player.getBrightness(partialTick), width, height);
        }
        final ItemInstance armor = this.minecraft.player.inventory.getArmor(3);
        if (!this.minecraft.options.thirdPersonView && armor != null && armor.id == Tile.pumpkin.id) {
            this.renderPumpkin(width, height);
        }
        final float br = this.minecraft.player.oPortalTime + (this.minecraft.player.portalTime - this.minecraft.player.oPortalTime) * partialTick;
        if (br > 0.0f) {
            this.renderTp(br, width, height);
        }
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glBindTexture(GL_TEXTURE_2D, this.minecraft.textures.loadTexture("/gui/gui.png"));
        final Inventory inventory = this.minecraft.player.inventory;
        this.blitOffset = -90.0f;
        this.blit(width / 2 - 91, height - 22, 0, 0, 182, 22);
        this.blit(width / 2 - 91 - 1 + inventory.selected * 20, height - 22 - 1, 0, 22, 24, 22);
        glBindTexture(GL_TEXTURE_2D, this.minecraft.textures.loadTexture("/gui/icons.png"));
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);
        this.blit(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
        glDisable(GL_BLEND);
        boolean b = this.minecraft.player.invulnerableTime / 3 % 2 == 1;
        if (this.minecraft.player.invulnerableTime < 10) {
            b = false;
        }
        final int health = this.minecraft.player.health;
        final int lastHealth = this.minecraft.player.lastHealth;
        this.random.setSeed(this.tickCount * 312871);
        if (this.minecraft.gameMode.canHurtPlayer()) {
            final int armor2 = this.minecraft.player.getArmor();
            for (int i = 0; i < 10; ++i) {
                int n = height - 32;
                if (armor2 > 0) {
                    final int x = width / 2 + 91 - i * 8 - 9;
                    if (i * 2 + 1 < armor2) {
                        this.blit(x, n, 34, 9, 9, 9);
                    }
                    if (i * 2 + 1 == armor2) {
                        this.blit(x, n, 25, 9, 9, 9);
                    }
                    if (i * 2 + 1 > armor2) {
                        this.blit(x, n, 16, 9, 9, 9);
                    }
                }
                int n2 = 0;
                if (b) {
                    n2 = 1;
                }
                final int x2 = width / 2 - 91 + i * 8;
                if (health <= 4) {
                    n += this.random.nextInt(2);
                }
                this.blit(x2, n, 16 + n2 * 9, 0, 9, 9);
                if (b) {
                    if (i * 2 + 1 < lastHealth) {
                        this.blit(x2, n, 70, 0, 9, 9);
                    }
                    if (i * 2 + 1 == lastHealth) {
                        this.blit(x2, n, 79, 0, 9, 9);
                    }
                }
                if (i * 2 + 1 < health) {
                    this.blit(x2, n, 52, 0, 9, 9);
                }
                if (i * 2 + 1 == health) {
                    this.blit(x2, n, 61, 0, 9, 9);
                }
            }
            if (this.minecraft.player.isUnderLiquid(Material.water)) {
                for (int n3 = (int)Math.ceil((this.minecraft.player.airSupply - 2) * 10.0 / 300.0), n4 = (int)Math.ceil(this.minecraft.player.airSupply * 10.0 / 300.0) - n3, j = 0; j < n3 + n4; ++j) {
                    if (j < n3) {
                        this.blit(width / 2 - 91 + j * 8, height - 32 - 9, 16, 18, 9, 9);
                    }
                    else {
                        this.blit(width / 2 - 91 + j * 8, height - 32 - 9, 25, 18, 9, 9);
                    }
                }
            }
        }
        glDisable(GL_BLEND);
        glEnable(GL_RESCALE_NORMAL);
        glPushMatrix();
        glRotatef(120.0f, 1.0f, 0.0f, 0.0f);
        Lighting.turnOn();
        glPopMatrix();
        for (int k = 0; k < 9; ++k) {
            this.renderSlot(k, width / 2 - 90 + k * 20 + 2, height - 16 - 3, partialTick);
        }
        Lighting.turnOff();
        glDisable(GL_RESCALE_NORMAL);
        if (this.minecraft.player.getSleepTimer() > 0) {
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_ALPHA_TEST);
            final int sleepTimer = this.minecraft.player.getSleepTimer();
            float n5 = sleepTimer / 100.0f;
            if (n5 > 1.0f) {
                n5 = 1.0f - (sleepTimer - 100) / 10.0f;
            }
            this.fill(0, 0, width, height, (int)(220.0f * n5) << 24 | 0x101020);
            glEnable(GL_ALPHA_TEST);
            glEnable(GL_DEPTH_TEST);
        }
        if (this.minecraft.options.renderDebug) {
            glPushMatrix();
            if (Minecraft.warezTime > 0L) {
                glTranslatef(0.0f, 32.0f, 0.0f);
            }
            font.drawShadow("Minecraft Beta 1.7.3 (" + this.minecraft.fpsString + ")", 2, 2, 16777215);
            font.drawShadow(this.minecraft.gatherStats1(), 2, 12, 16777215);
            font.drawShadow(this.minecraft.gatherStats2(), 2, 22, 16777215);
            font.drawShadow(this.minecraft.gatherStats4(), 2, 32, 16777215);
            font.drawShadow(this.minecraft.gatherStats3(), 2, 42, 16777215);
            final long maxMemory = Runtime.getRuntime().maxMemory();
            final long totalMemory = Runtime.getRuntime().totalMemory();
            final long n6 = totalMemory - Runtime.getRuntime().freeMemory();
            final String string = "Used memory: " + n6 * 100L / maxMemory + "% (" + n6 / 1024L / 1024L + "MB) of " + maxMemory / 1024L / 1024L + "MB";
            this.drawString(font, string, width - font.width(string) - 2, 2, 14737632);
            final String string2 = "Allocated memory: " + totalMemory * 100L / maxMemory + "% (" + totalMemory / 1024L / 1024L + "MB)";
            this.drawString(font, string2, width - font.width(string2) - 2, 12, 14737632);
            this.drawString(font, "x: " + this.minecraft.player.x, 2, 64, 14737632);
            this.drawString(font, "y: " + this.minecraft.player.y, 2, 72, 14737632);
            this.drawString(font, "z: " + this.minecraft.player.z, 2, 80, 14737632);
            this.drawString(font, "f: " + (Mth.floor(this.minecraft.player.yRot * 4.0f / 360.0f + 0.5) & 0x3), 2, 88, 14737632);
            glPopMatrix();
        }
        if (this.nowPlayingTime > 0) {
            final float n7 = this.nowPlayingTime - partialTick;
            int n8 = (int)(n7 * 256.0f / 20.0f);
            if (n8 > 255) {
                n8 = 255;
            }
            if (n8 > 0) {
                glPushMatrix();
                glTranslatef((float)(width / 2), (float)(height - 48), 0.0f);
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                int n9 = 16777215;
                if (this.nowPlayingColor) {
                    n9 = (Color.HSBtoRGB(n7 / 50.0f, 0.7f, 0.6f) & 0xFFFFFF);
                }
                font.draw(this.nowPlayingString, -font.width(this.nowPlayingString) / 2, -4, n9 + (n8 << 24));
                glDisable(GL_BLEND);
                glPopMatrix();
            }
        }
        int n10 = 10;
        boolean b2 = false;
        if (this.minecraft.screen instanceof ChatScreen) {
            n10 = 20;
            b2 = true;
        }
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_ALPHA_TEST);
        glPushMatrix();
        glTranslatef(0.0f, (float)(height - 48), 0.0f);
        for (int n11 = 0; n11 < this.guiMessages.size() && n11 < n10; ++n11) {
            if (((GuiMessage)this.guiMessages.get(n11)).ticks < 200 || b2) {
                double n12 = (1.0 - this.guiMessages.get(n11).ticks / 200.0) * 10.0;
                if (n12 < 0.0) {
                    n12 = 0.0;
                }
                if (n12 > 1.0) {
                    n12 = 1.0;
                }
                int n13 = (int)(255.0 * (n12 * n12));
                if (b2) {
                    n13 = 255;
                }
                if (n13 > 0) {
                    final int n14 = 2;
                    final int y = -n11 * 9;
                    final String string3 = this.guiMessages.get(n11).string;
                    this.fill(n14, y - 1, n14 + 320, y + 8, n13 / 2 << 24);
                    glEnable(GL_BLEND);
                    font.drawShadow(string3, n14, y, 16777215 + (n13 << 24));
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
        final Tesselator instance = Tesselator.instance;
        instance.begin();
        instance.vertexUV(0.0, h, -90.0, 0.0, 1.0);
        instance.vertexUV(w, h, -90.0, 1.0, 1.0);
        instance.vertexUV(w, 0.0, -90.0, 1.0, 0.0);
        instance.vertexUV(0.0, 0.0, -90.0, 0.0, 0.0);
        instance.end();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_ALPHA_TEST);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private void renderVignette(float br, final int w, final int h) {
        br = 1.0f - br;
        if (br < 0.0f) {
            br = 0.0f;
        }
        if (br > 1.0f) {
            br = 1.0f;
        }
        this.tbr += (float)((br - this.tbr) * 0.01);
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
        final float n = Tile.portalTile.tex % 16 / 16.0f;
        final float n2 = Tile.portalTile.tex / 16 / 16.0f;
        final float n3 = (Tile.portalTile.tex % 16 + 1) / 16.0f;
        final float n4 = (Tile.portalTile.tex / 16 + 1) / 16.0f;
        final Tesselator instance = Tesselator.instance;
        instance.begin();
        instance.vertexUV(0.0, h, -90.0, n, n4);
        instance.vertexUV(w, h, -90.0, n3, n4);
        instance.vertexUV(w, 0.0, -90.0, n3, n2);
        instance.vertexUV(0.0, 0.0, -90.0, n, n2);
        instance.end();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_ALPHA_TEST);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private void renderSlot(final int slot, final int x, final int y, final float partialTick) {
        final ItemInstance itemInstance = this.minecraft.player.inventory.items[slot];
        if (itemInstance == null) {
            return;
        }
        final float n = itemInstance.popTime - partialTick;
        if (n > 0.0f) {
            glPushMatrix();
            final float n2 = 1.0f + n / 5.0f;
            glTranslatef((float)(x + 8), (float)(y + 12), 0.0f);
            glScalef(1.0f / n2, (n2 + 1.0f) / 2.0f, 1.0f);
            glTranslatef((float)(-(x + 8)), (float)(-(y + 12)), 0.0f);
        }
        Gui.itemRenderer.renderGuiItem(this.minecraft.font, this.minecraft.textures, itemInstance, x, y);
        if (n > 0.0f) {
            glPopMatrix();
        }
        Gui.itemRenderer.renderGuiItemDecorations(this.minecraft.font, this.minecraft.textures, itemInstance, x, y);
    }
    
    public void tick() {
        if (this.nowPlayingTime > 0) {
            --this.nowPlayingTime;
        }
        ++this.tickCount;
        for (int i = 0; i < this.guiMessages.size(); ++i) {
            final GuiMessage guiMessage = this.guiMessages.get(i);
            ++guiMessage.ticks;
        }
    }
    
    public void clearMessages() {
        this.guiMessages.clear();
    }
    
    public void addMessage(String str) {
        while (this.minecraft.font.width(str) > 320) {
            int n;
            for (n = 1; n < str.length() && this.minecraft.font.width(str.substring(0, n + 1)) <= 320; ++n) {}
            this.addMessage(str.substring(0, n));
            str = str.substring(n);
        }
        this.guiMessages.add(0, new GuiMessage(str));
        while (this.guiMessages.size() > 50) {
            this.guiMessages.remove(this.guiMessages.size() - 1);
        }
    }
    
    public void setNowPlaying(final String str) {
        this.nowPlayingString = "Now playing: " + str;
        this.nowPlayingTime = 60;
        this.nowPlayingColor = true;
    }
    
    public void displayClientMessage(final String message) {
        this.addMessage(Language.getInstance().getElement(message));
    }
    
    static {
        Gui.itemRenderer = new ItemRenderer();
    }
}
