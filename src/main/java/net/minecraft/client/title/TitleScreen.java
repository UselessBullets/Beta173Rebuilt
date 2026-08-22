// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.title;

import net.minecraft.client.Minecraft;
import util.Mth;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.skins.TexturePackSelectScreen;
import net.minecraft.client.gui.JoinMultiplayerScreen;
import net.minecraft.client.gui.SelectWorldScreen;
import net.minecraft.client.gui.OptionsScreen;
import net.minecraft.locale.language.Language;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Calendar;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import net.minecraft.client.gui.Button;
import java.util.Random;
import net.minecraft.client.gui.Screen;

import static org.lwjgl.opengl.GL11.*;

public class TitleScreen extends Screen
{
    private static final Random random = new Random();
    private float vo = 0.0f;
    private String splash = "missingno";
    private Button multiplayerButton;
    
    public TitleScreen() {
        try {
            final ArrayList<String> splashes = new ArrayList<>();

            BufferedReader br = new BufferedReader(new InputStreamReader(TitleScreen.class.getResourceAsStream("/title/splashes.txt"), StandardCharsets.UTF_8));

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0) {
                    splashes.add(line);
                }
            }

            this.splash = splashes.get(TitleScreen.random.nextInt(splashes.size()));
        }
        catch (final Exception e) {}
    }
    
    @Override
    public void tick() {
        ++this.vo;
    }
    
    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
    }
    
    @Override
    public void init() {
        final Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        if (c.get(Calendar.MONTH) + 1 == 11 && c.get(Calendar.DAY_OF_MONTH) == 9) {
            this.splash = "Happy birthday, ez!";
        }
        else if (c.get(Calendar.MONTH) + 1 == 6 && c.get(Calendar.DAY_OF_MONTH) == 1) {
            this.splash = "Happy birthday, Notch!";
        }
        else if (c.get(Calendar.MONTH) + 1 == 12 && c.get(Calendar.DAY_OF_MONTH) == 24) {
            this.splash = "Merry X-mas!";
        }
        else if (c.get(Calendar.MONTH) + 1 == 1 && c.get(Calendar.DAY_OF_MONTH) == 1) {
            this.splash = "Happy new year!";
        }

        final Language language = Language.getInstance();

        final int spacing = 24;
        final int topPos = this.height / 4 + spacing * 2;

        this.buttons.add(new Button(1, this.width / 2 - 100, topPos, language.getElement("menu.singleplayer")));
        this.buttons.add(this.multiplayerButton = new Button(2, this.width / 2 - 100, topPos + spacing, language.getElement("menu.multiplayer")));
        this.buttons.add(new Button(3, this.width / 2 - 100, topPos + spacing * 2, language.getElement("menu.mods")));

        if (this.minecraft.appletMode) {
            this.buttons.add(new Button(0, this.width / 2 - 100, topPos + spacing * 3, language.getElement("menu.options")));
        }
        else {
            this.buttons.add(new Button(0, this.width / 2 - 100, topPos + spacing * 3 + 12, 98, 20, language.getElement("menu.options")));
            this.buttons.add(new Button(4, this.width / 2 + 2, topPos + spacing * 3 + 12, 98, 20, language.getElement("menu.quit")));
        }

        if (this.minecraft.user == null) {
            this.multiplayerButton.active = false;
        }
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (button.id == 0) this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
        if (button.id == 1) this.minecraft.setScreen(new SelectWorldScreen(this));
        if (button.id == 2) this.minecraft.setScreen(new JoinMultiplayerScreen(this));
        if (button.id == 3) this.minecraft.setScreen(new TexturePackSelectScreen(this));
        if (button.id == 4) this.minecraft.stop();
    }
    
    @Override
    public void render(final int xm, final int ym, final float a) {
        this.renderBackground();
        final Tesselator t = Tesselator.instance;

        final int logoWidth = 155 + 119;
        final int logoX = this.width / 2 - logoWidth / 2;
        final int logoY = 30;

        glBindTexture(GL_TEXTURE_2D, this.minecraft.textures.loadTexture("/title/mclogo.png"));
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.blit(logoX + 0, logoY + 0, 0, 0, 155, 44);
        this.blit(logoX + 155, logoY + 0, 0, 45, 155, 44);
        t.color(0xffffff);
        glPushMatrix();
        glTranslatef((float)(this.width / 2 + 90), 70.0f, 0.0f);

        glRotatef(-20.0f, 0.0f, 0.0f, 1.0f);
        float sss = (1.8f - Mth.abs(Mth.sin(System.currentTimeMillis() % 1000L / 1000.0f * Mth.PI * 2.0f) * 0.1f));

        sss = sss * 100.0f / (this.font.width(this.splash) + 8 * 4);
        glScalef(sss, sss, sss);
        this.drawCenteredString(this.font, this.splash, 0, -8, 0xffff00);
        glPopMatrix();

        this.drawString(this.font, Minecraft.VERSION_STRING, 2, 2, 0x505050);
        final String msg = "Copyright Mojang AB. Do not distribute.";
        this.drawString(this.font, msg, this.width - this.font.width(msg) - 2, this.height - 10, 0xffffff);

        super.render(xm, ym, a);
    }

}
