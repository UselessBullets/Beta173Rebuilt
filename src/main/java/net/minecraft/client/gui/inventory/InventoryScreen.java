// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.inventory;

import net.minecraft.client.gui.achievement.StatsScreen;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.achievement.AchievementScreen;
import net.minecraft.client.gui.Button;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.Lighting;
import org.lwjgl.opengl.GL11;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Achievements;
import net.minecraft.world.entity.player.Player;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class InventoryScreen extends AbstractContainerScreen
{
    private float xMouse;
    private float yMouse;
    
    public InventoryScreen(final Player player) {
        super(player.inventoryMenu);

        this.passEvents = true;
        player.awardStat(Achievements.openInventory, 1);
    }
    
    @Override
    public void init() {
        this.buttons.clear();
    }
    
    @Override
    protected void renderLabels() {
        this.font.draw("Crafting", 84 + 2, 8 * 2, 0x404040);
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        super.render(xm, ym, partialTick);
        this.xMouse = (float)xm;
        this.yMouse = (float)ym;
    }
    
    @Override
    protected void renderBg(final float partialTick) {
        final int tex = this.minecraft.textures.loadTexture("/gui/inventory.png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.textures.bind(tex);
        final int xo = (this.width - this.imageWidth) / 2;
        final int yo = (this.height - this.imageHeight) / 2;
        this.blit(xo, yo, 0, 0, this.imageWidth, this.imageHeight);

        GL11.glEnable(GL_RESCALE_NORMAL);
        GL11.glEnable(GL_COLOR_MATERIAL);

        GL11.glPushMatrix();
        GL11.glTranslatef((float)(xo + 51), (float)(yo + 75), 50.0f);
        final float ss = 30.0f;
        GL11.glScalef(-ss, ss, ss);
        GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);

        final float oybr = this.minecraft.player.yBodyRot;
        final float oyr = this.minecraft.player.yRot;
        final float oxr = this.minecraft.player.xRot;

        final float xd = xo + 51 - this.xMouse;
        final float yd = yo + 75 - 50 - this.yMouse;

        GL11.glRotatef(45 + 90, 0.0f, 1.0f, 0.0f);
        Lighting.turnOn();
        GL11.glRotatef(-45 - 90, 0.0f, 1.0f, 0.0f);

        GL11.glRotatef(-(float)Math.atan(yd / 40.0f) * 20.0f, 1.0f, 0.0f, 0.0f);

        this.minecraft.player.yBodyRot = (float)Math.atan(xd / 40.0f) * 20.0f;
        this.minecraft.player.yRot = (float)Math.atan(xd / 40.0f) * 40.0f;
        this.minecraft.player.xRot = -(float)Math.atan(yd / 40.0f) * 20.0f;
        this.minecraft.player.emission = 1.0f;
        GL11.glTranslatef(0.0f, this.minecraft.player.heightOffset, 0.0f);
        EntityRenderDispatcher.instance.playerRotY = 180.0f;
        EntityRenderDispatcher.instance.render(this.minecraft.player, 0.0, 0.0, 0.0, 0.0f, 1.0f);
        this.minecraft.player.emission = 0.0f;
        this.minecraft.player.yBodyRot = oybr;
        this.minecraft.player.yRot = oyr;
        this.minecraft.player.xRot = oxr;
        GL11.glPopMatrix();
        Lighting.turnOff();
        GL11.glDisable(GL_RESCALE_NORMAL);
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (button.id == 0) {
            this.minecraft.setScreen(new AchievementScreen(this.minecraft.stats));
        }
        if (button.id == 1) {
            this.minecraft.setScreen(new StatsScreen(this, this.minecraft.stats));
        }
    }
}
