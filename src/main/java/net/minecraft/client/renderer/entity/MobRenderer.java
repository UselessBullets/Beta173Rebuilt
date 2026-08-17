// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.Minecraft;
import util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.model.Model;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class MobRenderer<T extends Mob> extends EntityRenderer<T>
{
    private static final int MAX_ARMOR_LAYER = 4;

    protected Model model;
    protected Model armor;
    
    public MobRenderer(final Model model, final float shadow) {
        this.model = model;
        this.shadowRadius = shadow;
    }
    
    public void setArmor(final Model armor) {
        this.armor = armor;
    }
    
    public void render(final T mob, final double x, final double y, final double z, final float rot, final float a) {
        glPushMatrix();
        glDisable(GL_CULL_FACE);

        this.model.attackTime = this.getAttackAnim(mob, a);
        if (this.armor != null) this.armor.attackTime = this.model.attackTime;
        this.model.riding = mob.isRiding();
        if (this.armor != null) this.armor.riding = this.model.riding;

        try {
            final float bodyRot = mob.yBodyRotO + (mob.yBodyRot - mob.yBodyRotO) * a;
            final float headRot = mob.yRotO + (mob.yRot - mob.yRotO) * a;
            final float headRotx = mob.xRotO + (mob.xRot - mob.xRotO) * a;

            this.setupPosition(mob, x, y, z);

            final float bob = this.getBob(mob, a);
            this.setupRotations(mob, bob, bodyRot, a);

            final float scale = 1 / 16.0f;
            glEnable(GL_RESCALE_NORMAL);
            glScalef(-1.0f, -1.0f, 1.0f);

            this.scale(mob, a);
            glTranslatef(0.0f, -24.0f * scale - 0.125f / 16.0f, 0.0f);

            float ws = mob.walkAnimSpeedO + (mob.walkAnimSpeed - mob.walkAnimSpeedO) * a;
            final float wp = mob.walkAnimPos - mob.walkAnimSpeed * (1.0f - a);
            if (ws > 1.0f) ws = 1.0f;

            this.bindTexture(mob.customTextureUrl, mob.getTexture());
            glEnable(GL_ALPHA_TEST);

            this.model.prepareMobModel(mob, wp, ws, a);
            this.model.render(wp, ws, bob, headRot - bodyRot, headRotx, scale);
            for (int i = 0; i < MAX_ARMOR_LAYER; ++i) {
                if (this.prepareArmor(mob, i, a)) {
                    this.armor.render(wp, ws, bob, headRot - bodyRot, headRotx, scale);
                    glDisable(GL_BLEND);
                    glEnable(GL_ALPHA_TEST);
                }
            }

            this.additionalRendering(mob, a);
            final float br = mob.getBrightness(a);
            final int overlayColor = this.getOverlayColor(mob, br, a);

            if ((overlayColor >> 24 & 0xFF) > 0 || mob.hurtTime > 0 || mob.deathTime > 0) {
                glDisable(GL_TEXTURE_2D);
                glDisable(GL_ALPHA_TEST);
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                glDepthFunc(GL_EQUAL);

                if (mob.hurtTime > 0 || mob.deathTime > 0) {
                    glColor4f(br, 0.0f, 0.0f, 0.4f);
                    this.model.render(wp, ws, bob, headRot - bodyRot, headRotx, scale);
                    for (int j = 0; j < MAX_ARMOR_LAYER; ++j) {
                        if (this.prepareArmorOverlay(mob, j, a)) {
                            glColor4f(br, 0.0f, 0.0f, 0.4f);
                            this.armor.render(wp, ws, bob, headRot - bodyRot, headRotx, scale);
                        }
                    }
                }

                if ((overlayColor >> 24 & 0xFF) > 0) {
                    final float r = (overlayColor >> 16 & 0xFF) / 255.0f;
                    final float g = (overlayColor >> 8 & 0xFF) / 255.0f;
                    final float b = (overlayColor & 0xFF) / 255.0f;
                    final float aa = (overlayColor >> 24 & 0xFF) / 255.0f;
                    glColor4f(r, g, b, aa);
                    this.model.render(wp, ws, bob, headRot - bodyRot, headRotx, scale);
                    for (int i = 0; i < MAX_ARMOR_LAYER; ++i) {
                        if (this.prepareArmorOverlay(mob, i, a)) {
                            glColor4f(r, g, b, aa);
                            this.armor.render(wp, ws, bob, headRot - bodyRot, headRotx, scale);
                        }
                    }
                }

                glDepthFunc(GL_LEQUAL);
                glDisable(GL_BLEND);
                glEnable(GL_ALPHA_TEST);
                glEnable(GL_TEXTURE_2D);
            }
            glDisable(GL_RESCALE_NORMAL);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        glEnable(GL_CULL_FACE);
        glPopMatrix();
        this.renderName(mob, x, y, z);
    }
    
    protected void setupPosition(final T mob, final double x, final double y, final double z) {
        glTranslatef((float)x, (float)y, (float)z);
    }
    
    protected void setupRotations(final T mob, final float bob, final float bodyRot, final float a) {
        glRotatef(180.0f - bodyRot, 0.0f, 1.0f, 0.0f);
        if (mob.deathTime > 0) {
            float fall = Mth.sqrt((mob.deathTime + a - 1.0f) / 20.0f * 1.6f);
            if (fall > 1.0f) fall = 1.0f;
            glRotatef(fall * this.getFlipDegrees(mob), 0.0f, 0.0f, 1.0f);
        }
    }
    
    protected float getAttackAnim(final T mob, final float a) {
        return mob.getAttackAnim(a);
    }
    
    protected float getBob(final T mob, final float a) {
        return mob.tickCount + a;
    }
    
    protected void additionalRendering(final T mob, final float a) {
    }
    
    protected boolean prepareArmorOverlay(final T mob, final int layer, final float a) {
        return this.prepareArmor(mob, layer, a);
    }
    
    protected boolean prepareArmor(final T mob, final int layer, final float a) {
        return false;
    }
    
    protected float getFlipDegrees(final T mob) {
        return 90.0f;
    }
    
    protected int getOverlayColor(final T mob, final float br, final float a) {
        return 0;
    }
    
    protected void scale(final T mob, final float a) {
    }
    
    protected void renderName(final T mob, final double x, final double y, final double z) {
        if (Minecraft.renderDebug()) {
            this.renderNameTag(mob, Integer.toString(mob.entityId), x, y, z, 64);
        }
    }
    
    protected void renderNameTag(final T mob, final String name, final double x, final double y, final double z, final int maxDist) {
        float dist = mob.distanceTo(this.entityRenderDispatcher.player);
        if (dist > maxDist) return;

        final Font font = this.getFont();

        float size = 1.6f;
        final float s = 1.0f / 60.0f * size;

        glPushMatrix();
        glTranslatef((float)x + 0.0f, (float)y + 2.3f, (float)z);
        glNormal3f(0.0f, 1.0f, 0.0f);

        glRotatef(-this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);
        glRotatef(this.entityRenderDispatcher.playerRotX, 1.0f, 0.0f, 0.0f);

        glScalef(-s, -s, s);
        glDisable(GL_LIGHTING);

        glDepthMask(false);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        final Tesselator t = Tesselator.instance;

        int offs = 0;
        if (name.equals("deadmau5")) offs = -10;

        glDisable(GL_TEXTURE_2D);
        t.begin();
        final int w = font.width(name) / 2;

        t.color(0.0f, 0.0f, 0.0f, 0.25f);
        t.vertex(-w - 1, -1 + offs, 0);
        t.vertex(-w - 1, +8 + offs, 0);
        t.vertex(+w + 1, +8 + offs, 0);
        t.vertex(+w + 1, -1 + offs, 0);
        t.end();

        glEnable(GL_TEXTURE_2D);
        font.draw(name, -font.width(name) / 2, offs, 0x20ffffff);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        font.draw(name, -font.width(name) / 2, offs, 0xffffffff);

        glEnable(GL_LIGHTING);
        glDisable(GL_BLEND);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glPopMatrix();
    }
}
