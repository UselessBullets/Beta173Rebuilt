// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.title;

import util.Mth;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.skins.TexturePackSelectScreen;
import net.minecraft.client.gui.JoinMultiplayerScreen;
import net.minecraft.client.gui.SelectWorldScreen;
import net.minecraft.client.gui.OptionsScreen;
import net.minecraft.locale.language.Language;
import java.util.Date;
import java.util.Calendar;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import net.minecraft.client.gui.Button;
import java.util.Random;
import net.minecraft.client.gui.Screen;

public class TitleScreen extends Screen
{
    private static final Random random;
    private float vo;
    private String splash;
    private Button multiplayerButton;
    
    public TitleScreen() {
        this.vo = 0.0f;
        this.splash = "missingno";
        try {
            final ArrayList list = new ArrayList();
            String line;
            while ((line = new BufferedReader(new InputStreamReader(TitleScreen.class.getResourceAsStream("/title/splashes.txt"), Charset.forName("UTF-8"))).readLine()) != null) {
                final String trim = line.trim();
                if (trim.length() > 0) {
                    list.add(trim);
                }
            }
            this.splash = (String)list.get(TitleScreen.random.nextInt(list.size()));
        }
        catch (final Exception ex) {}
    }
    
    @Override
    public void tick() {
        ++this.vo;
    }
    
    @Override
    protected void keyPressed(final char ch, final int eventKey) {
    }
    
    @Override
    public void init() {
        final Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        if (instance.get(2) + 1 == 11 && instance.get(5) == 9) {
            this.splash = "Happy birthday, ez!";
        }
        else if (instance.get(2) + 1 == 6 && instance.get(5) == 1) {
            this.splash = "Happy birthday, Notch!";
        }
        else if (instance.get(2) + 1 == 12 && instance.get(5) == 24) {
            this.splash = "Merry X-mas!";
        }
        else if (instance.get(2) + 1 == 1 && instance.get(5) == 1) {
            this.splash = "Happy new year!";
        }
        final Language instance2 = Language.getInstance();
        final int y = this.height / 4 + 48;
        this.buttons.add(new Button(1, this.width / 2 - 100, y, instance2.getElement("menu.singleplayer")));
        this.buttons.add(this.multiplayerButton = new Button(2, this.width / 2 - 100, y + 24, instance2.getElement("menu.multiplayer")));
        this.buttons.add(new Button(3, this.width / 2 - 100, y + 48, instance2.getElement("menu.mods")));
        if (this.minecraft.appletMode) {
            this.buttons.add(new Button(0, this.width / 2 - 100, y + 72, instance2.getElement("menu.options")));
        }
        else {
            this.buttons.add(new Button(0, this.width / 2 - 100, y + 72 + 12, 98, 20, instance2.getElement("menu.options")));
            this.buttons.add(new Button(4, this.width / 2 + 2, y + 72 + 12, 98, 20, instance2.getElement("menu.quit")));
        }
        if (this.minecraft.user == null) {
            this.multiplayerButton.active = false;
        }
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (button.id == 0) {
            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
        }
        if (button.id == 1) {
            this.minecraft.setScreen(new SelectWorldScreen(this));
        }
        if (button.id == 2) {
            this.minecraft.setScreen(new JoinMultiplayerScreen(this));
        }
        if (button.id == 3) {
            this.minecraft.setScreen(new TexturePackSelectScreen(this));
        }
        if (button.id == 4) {
            this.minecraft.stop();
        }
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.renderBackground();
        final Tesselator instance = Tesselator.instance;
        final int n = this.width / 2 - 274 / 2;
        final int n2 = 30;
        GL11.glBindTexture(3553, this.minecraft.textures.loadTexture("/title/mclogo.png"));
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.blit(n + 0, n2 + 0, 0, 0, 155, 44);
        this.blit(n + 155, n2 + 0, 0, 45, 155, 44);
        instance.color(16777215);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(this.width / 2 + 90), 70.0f, 0.0f);
        GL11.glRotatef(-20.0f, 0.0f, 0.0f, 1.0f);
        final float n3 = (1.8f - Mth.abs(Mth.sin(System.currentTimeMillis() % 1000L / 1000.0f * 3.1415927f * 2.0f) * 0.1f)) * 100.0f / (this.font.width(this.splash) + 32);
        GL11.glScalef(n3, n3, n3);
        this.drawCenteredString(this.font, this.splash, 0, -8, 16776960);
        GL11.glPopMatrix();
        this.drawString(this.font, "Minecraft Beta 1.7.3", 2, 2, 5263440);
        final String s = "Copyright Mojang AB. Do not distribute.";
        this.drawString(this.font, s, this.width - this.font.width(s) - 2, this.height - 10, 16777215);
        super.render(xm, ym, partialTick);
    }
    
    static {
        random = new Random();
    }
}
