// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.Minecraft;
import util.Mth;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.model.Model;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class MobRenderer<T extends Mob> extends EntityRenderer<T>
{
    protected Model model;
    protected Model armor;
    
    public MobRenderer(final Model model, final float shadow) {
        this.model = model;
        this.shadowRadius = shadow;
    }
    
    public void setArmor(final Model armor) {
        this.armor = armor;
    }
    
    public void render(final T entity, final double x, final double y, final double z, final float rot, final float partialTick) {
        GL11.glPushMatrix();
        GL11.glDisable(GL_CULL_FACE);
        this.model.attackTime = this.getAttackAnim(entity, partialTick);
        if (this.armor != null) {
            this.armor.attackTime = this.model.attackTime;
        }
        this.model.riding = entity.isRiding();
        if (this.armor != null) {
            this.armor.riding = this.model.riding;
        }
        try {
            final float bodyRot = entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTick;
            final float n = entity.yRotO + (entity.yRot - entity.yRotO) * partialTick;
            final float n2 = entity.xRotO + (entity.xRot - entity.xRotO) * partialTick;
            this.setupPosition(entity, x, y, z);
            final float bob = this.getBob(entity, partialTick);
            this.setupRotations(entity, bob, bodyRot, partialTick);
            final float n3 = 0.0625f;
            GL11.glEnable(GL_RESCALE_NORMAL);
            GL11.glScalef(-1.0f, -1.0f, 1.0f);
            this.scale(entity, partialTick);
            GL11.glTranslatef(0.0f, -24.0f * n3 - 0.0078125f, 0.0f);
            float r = entity.walkAnimSpeedO + (entity.walkAnimSpeed - entity.walkAnimSpeedO) * partialTick;
            final float time = entity.walkAnimPos - entity.walkAnimSpeed * (1.0f - partialTick);
            if (r > 1.0f) {
                r = 1.0f;
            }
            this.bindTexture(entity.customTextureUrl, entity.getTexture());
            GL11.glEnable(GL_ALPHA_TEST);
            this.model.prepareMobModel(entity, time, r, partialTick);
            this.model.render(time, r, bob, n - bodyRot, n2, n3);
            for (int i = 0; i < 4; ++i) {
                if (this.prepareArmor(entity, i, partialTick)) {
                    this.armor.render(time, r, bob, n - bodyRot, n2, n3);
                    GL11.glDisable(GL_BLEND);
                    GL11.glEnable(GL_ALPHA_TEST);
                }
            }
            this.additionalRendering(entity, partialTick);
            final float brightness = entity.getBrightness(partialTick);
            final int overlayColor = this.getOverlayColor(entity, brightness, partialTick);
            if ((overlayColor >> 24 & 0xFF) > 0 || entity.hurtTime > 0 || entity.deathTime > 0) {
                GL11.glDisable(GL_TEXTURE_2D);
                GL11.glDisable(GL_ALPHA_TEST);
                GL11.glEnable(GL_BLEND);
                GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDepthFunc(514);
                if (entity.hurtTime > 0 || entity.deathTime > 0) {
                    GL11.glColor4f(brightness, 0.0f, 0.0f, 0.4f);
                    this.model.render(time, r, bob, n - bodyRot, n2, n3);
                    for (int j = 0; j < 4; ++j) {
                        if (this.prepareArmorOverlay(entity, j, partialTick)) {
                            GL11.glColor4f(brightness, 0.0f, 0.0f, 0.4f);
                            this.armor.render(time, r, bob, n - bodyRot, n2, n3);
                        }
                    }
                }
                if ((overlayColor >> 24 & 0xFF) > 0) {
                    final float n4 = (overlayColor >> 16 & 0xFF) / 255.0f;
                    final float n5 = (overlayColor >> 8 & 0xFF) / 255.0f;
                    final float n6 = (overlayColor & 0xFF) / 255.0f;
                    final float n7 = (overlayColor >> 24 & 0xFF) / 255.0f;
                    GL11.glColor4f(n4, n5, n6, n7);
                    this.model.render(time, r, bob, n - bodyRot, n2, n3);
                    for (int k = 0; k < 4; ++k) {
                        if (this.prepareArmorOverlay(entity, k, partialTick)) {
                            GL11.glColor4f(n4, n5, n6, n7);
                            this.armor.render(time, r, bob, n - bodyRot, n2, n3);
                        }
                    }
                }
                GL11.glDepthFunc(GL_LEQUAL);
                GL11.glDisable(GL_BLEND);
                GL11.glEnable(GL_ALPHA_TEST);
                GL11.glEnable(GL_TEXTURE_2D);
            }
            GL11.glDisable(GL_RESCALE_NORMAL);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        GL11.glEnable(GL_CULL_FACE);
        GL11.glPopMatrix();
        this.renderName(entity, x, y, z);
    }
    
    protected void setupPosition(final T mob, final double x, final double y, final double z) {
        GL11.glTranslatef((float)x, (float)y, (float)z);
    }
    
    protected void setupRotations(final T mob, final float bob, final float bodyRot, final float partialTick) {
        GL11.glRotatef(180.0f - bodyRot, 0.0f, 1.0f, 0.0f);
        if (mob.deathTime > 0) {
            float sqrt = Mth.sqrt((mob.deathTime + partialTick - 1.0f) / 20.0f * 1.6f);
            if (sqrt > 1.0f) {
                sqrt = 1.0f;
            }
            GL11.glRotatef(sqrt * this.getFlipDegrees(mob), 0.0f, 0.0f, 1.0f);
        }
    }
    
    protected float getAttackAnim(final T mob, final float partialTick) {
        return mob.getAttackAnim(partialTick);
    }
    
    protected float getBob(final T mob, final float partialTick) {
        return mob.tickCount + partialTick;
    }
    
    protected void additionalRendering(final T mob, final float partialTick) {
    }
    
    protected boolean prepareArmorOverlay(final T mob, final int layer, final float partialTick) {
        return this.prepareArmor(mob, layer, partialTick);
    }
    
    protected boolean prepareArmor(final T mob, final int layer, final float partialTick) {
        return false;
    }
    
    protected float getFlipDegrees(final T mob) {
        return 90.0f;
    }
    
    protected int getOverlayColor(final T mob, final float br, final float partialTick) {
        return 0;
    }
    
    protected void scale(final T mob, final float partialTick) {
    }
    
    protected void renderName(final T mob, final double x, final double y, final double z) {
        if (Minecraft.renderDebug()) {
            this.renderNameTag(mob, Integer.toString(mob.entityId), x, y, z, 64);
        }
    }
    
    protected void renderNameTag(final T mob, final String name, final double x, final double y, final double z, final int maxDist) {
        if (mob.distanceTo(this.entityRenderDispatcher.player) > maxDist) {
            return;
        }
        final Font font = this.getFont();
        final float n = 0.016666668f * 1.6f;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x + 0.0f, (float)y + 2.3f, (float)z);
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(this.entityRenderDispatcher.playerRotX, 1.0f, 0.0f, 0.0f);
        GL11.glScalef(-n, -n, n);
        GL11.glDisable(GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL_DEPTH_TEST);
        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        final Tesselator instance = Tesselator.instance;
        int n2 = 0;
        if (name.equals("deadmau5")) {
            n2 = -10;
        }
        GL11.glDisable(GL_TEXTURE_2D);
        instance.begin();
        final int n3 = font.width(name) / 2;
        instance.color(0.0f, 0.0f, 0.0f, 0.25f);
        instance.vertex(-n3 - 1, -1 + n2, 0.0);
        instance.vertex(-n3 - 1, 8 + n2, 0.0);
        instance.vertex(n3 + 1, 8 + n2, 0.0);
        instance.vertex(n3 + 1, -1 + n2, 0.0);
        instance.end();
        GL11.glEnable(GL_TEXTURE_2D);
        font.draw(name, -font.width(name) / 2, n2, 553648127);
        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        font.draw(name, -font.width(name) / 2, n2, -1);
        GL11.glEnable(GL_LIGHTING);
        GL11.glDisable(GL_BLEND);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
}
