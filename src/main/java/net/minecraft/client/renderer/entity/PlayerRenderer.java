// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import util.Mth;
import net.minecraft.client.renderer.TileRenderer;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.Tesselator;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.HumanoidModel;

import static org.lwjgl.opengl.GL11.*;

public class PlayerRenderer extends MobRenderer<Player>
{
    private HumanoidModel humanoidModel;
    private HumanoidModel armorParts1;
    private HumanoidModel armorParts2;
    private static final String[] MATERIAL_NAMES;
    
    public PlayerRenderer() {
        super(new HumanoidModel(0.0f), 0.5f);
        this.humanoidModel = (HumanoidModel)this.model;
        this.armorParts1 = new HumanoidModel(1.0f);
        this.armorParts2 = new HumanoidModel(0.5f);
    }
    
    protected boolean prepareArmor(final Player mob, final int layer, final float partialTick) {
        final ItemInstance armor = mob.inventory.getArmor(3 - layer);
        if (armor != null) {
            final Item item = armor.getItem();
            if (item instanceof ArmorItem) {
                this.bindTexture("/armor/" + PlayerRenderer.MATERIAL_NAMES[((ArmorItem)item).materialIcon] + "_" + ((layer == 2) ? 2 : 1) + ".png");
                final HumanoidModel armor2 = (layer == 2) ? this.armorParts2 : this.armorParts1;
                armor2.head.visible = (layer == 0);
                armor2.hair.visible = (layer == 0);
                armor2.body.visible = (layer == 1 || layer == 2);
                armor2.arm0.visible = (layer == 1);
                armor2.arm1.visible = (layer == 1);
                armor2.leg0.visible = (layer == 2 || layer == 3);
                armor2.leg1.visible = (layer == 2 || layer == 3);
                this.setArmor(armor2);
                return true;
            }
        }
        return false;
    }
    
    public void render(final Player entity, final double x, final double y, final double z, final float rot, final float partialTick) {
        final ItemInstance selected = entity.inventory.getSelected();
        final HumanoidModel armorParts1 = this.armorParts1;
        final HumanoidModel armorParts2 = this.armorParts2;
        final HumanoidModel humanoidModel = this.humanoidModel;
        final boolean holdingRightHand;
        final boolean b = holdingRightHand = (((selected != null) ? 1 : 0) != 0);
        humanoidModel.holdingRightHand = b;
        armorParts2.holdingRightHand = b;
        armorParts1.holdingRightHand = holdingRightHand;
        final HumanoidModel armorParts3 = this.armorParts1;
        final HumanoidModel armorParts4 = this.armorParts2;
        final HumanoidModel humanoidModel2 = this.humanoidModel;
        final boolean sneaking = entity.isSneaking();
        humanoidModel2.sneaking = sneaking;
        armorParts4.sneaking = sneaking;
        armorParts3.sneaking = sneaking;
        double y2 = y - entity.heightOffset;
        if (entity.isSneaking() && !(entity instanceof LocalPlayer)) {
            y2 -= 0.125;
        }
        super.render(entity, x, y2, z, rot, partialTick);
        final HumanoidModel armorParts5 = this.armorParts1;
        final HumanoidModel armorParts6 = this.armorParts2;
        final HumanoidModel humanoidModel3 = this.humanoidModel;
        final boolean sneaking2 = false;
        humanoidModel3.sneaking = sneaking2;
        armorParts6.sneaking = sneaking2;
        armorParts5.sneaking = sneaking2;
        final HumanoidModel armorParts7 = this.armorParts1;
        final HumanoidModel armorParts8 = this.armorParts2;
        final HumanoidModel humanoidModel4 = this.humanoidModel;
        final boolean holdingRightHand2 = false;
        humanoidModel4.holdingRightHand = holdingRightHand2;
        armorParts8.holdingRightHand = holdingRightHand2;
        armorParts7.holdingRightHand = holdingRightHand2;
    }
    
    protected void renderName(final Player mob, final double x, final double y, final double z) {
        if (Minecraft.renderNames() && mob != this.entityRenderDispatcher.player) {
            final float n = 0.016666668f * 1.6f;
            if (mob.distanceTo(this.entityRenderDispatcher.player) < (mob.isSneaking() ? 32.0f : 64.0f)) {
                final String name = mob.name;
                if (!mob.isSneaking()) {
                    if (mob.isSleeping()) {
                        this.renderNameTag(mob, name, x, y - 1.5, z, 64);
                    }
                    else {
                        this.renderNameTag(mob, name, x, y, z, 64);
                    }
                }
                else {
                    final Font font = this.getFont();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)x + 0.0f, (float)y + 2.3f, (float)z);
                    GL11.glNormal3f(0.0f, 1.0f, 0.0f);
                    GL11.glRotatef(-this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);
                    GL11.glRotatef(this.entityRenderDispatcher.playerRotX, 1.0f, 0.0f, 0.0f);
                    GL11.glScalef(-n, -n, n);
                    GL11.glDisable(GL_LIGHTING);
                    GL11.glTranslatef(0.0f, 0.25f / n, 0.0f);
                    GL11.glDepthMask(false);
                    GL11.glEnable(GL_BLEND);
                    GL11.glBlendFunc(770, 771);
                    final Tesselator instance = Tesselator.instance;
                    GL11.glDisable(GL_TEXTURE_2D);
                    instance.begin();
                    final int n2 = font.width(name) / 2;
                    instance.color(0.0f, 0.0f, 0.0f, 0.25f);
                    instance.vertex(-n2 - 1, -1.0, 0.0);
                    instance.vertex(-n2 - 1, 8.0, 0.0);
                    instance.vertex(n2 + 1, 8.0, 0.0);
                    instance.vertex(n2 + 1, -1.0, 0.0);
                    instance.end();
                    GL11.glEnable(GL_TEXTURE_2D);
                    GL11.glDepthMask(true);
                    font.draw(name, -font.width(name) / 2, 0, 553648127);
                    GL11.glEnable(GL_LIGHTING);
                    GL11.glDisable(3042);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    @Override
    protected void additionalRendering(final Player mob, final float partialTick) {
        final ItemInstance armor = mob.inventory.getArmor(3);
        if (armor != null && armor.getItem().id < 256) {
            GL11.glPushMatrix();
            this.humanoidModel.head.translateTo(0.0625f);
            if (TileRenderer.canRender(Tile.tiles[armor.id].getRenderShape())) {
                final float n = 0.625f;
                GL11.glTranslatef(0.0f, -0.25f, 0.0f);
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                GL11.glScalef(n, -n, n);
            }
            this.entityRenderDispatcher.itemInHandRenderer.renderItem(mob, armor);
            GL11.glPopMatrix();
        }
        if (mob.name.equals("deadmau5") && this.bindTexture(mob.customTextureUrl, null)) {
            for (int i = 0; i < 2; ++i) {
                final float n2 = mob.yRotO + (mob.yRot - mob.yRotO) * partialTick - (mob.yBodyRotO + (mob.yBodyRot - mob.yBodyRotO) * partialTick);
                final float n3 = mob.xRotO + (mob.xRot - mob.xRotO) * partialTick;
                GL11.glPushMatrix();
                GL11.glRotatef(n2, 0.0f, 1.0f, 0.0f);
                GL11.glRotatef(n3, 1.0f, 0.0f, 0.0f);
                GL11.glTranslatef(0.375f * (i * 2 - 1), 0.0f, 0.0f);
                GL11.glTranslatef(0.0f, -0.375f, 0.0f);
                GL11.glRotatef(-n3, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(-n2, 0.0f, 1.0f, 0.0f);
                final float n4 = 1.3333334f;
                GL11.glScalef(n4, n4, n4);
                this.humanoidModel.renderEars(0.0625f);
                GL11.glPopMatrix();
            }
        }
        if (this.bindTexture(mob.cloakTexture, null)) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0f, 0.0f, 0.125f);
            final double n5 = mob.xCloakO + (mob.xCloak - mob.xCloakO) * partialTick - (mob.xo + (mob.x - mob.xo) * partialTick);
            final double n6 = mob.yCloakO + (mob.yCloak - mob.yCloakO) * partialTick - (mob.yo + (mob.y - mob.yo) * partialTick);
            final double n7 = mob.zCloakO + (mob.zCloak - mob.zCloakO) * partialTick - (mob.zo + (mob.z - mob.zo) * partialTick);
            final float n8 = mob.yBodyRotO + (mob.yBodyRot - mob.yBodyRotO) * partialTick;
            final double n9 = Mth.sin(n8 * 3.1415927f / 180.0f);
            final double n10 = -Mth.cos(n8 * 3.1415927f / 180.0f);
            float n11 = (float)n6 * 10.0f;
            if (n11 < -6.0f) {
                n11 = -6.0f;
            }
            if (n11 > 32.0f) {
                n11 = 32.0f;
            }
            float n12 = (float)(n5 * n9 + n7 * n10) * 100.0f;
            final float n13 = (float)(n5 * n10 - n7 * n9) * 100.0f;
            if (n12 < 0.0f) {
                n12 = 0.0f;
            }
            float n14 = n11 + Mth.sin((mob.walkDistO + (mob.walkDist - mob.walkDistO) * partialTick) * 6.0f) * 32.0f * (mob.oBob + (mob.bob - mob.oBob) * partialTick);
            if (mob.isSneaking()) {
                n14 += 25.0f;
            }
            GL11.glRotatef(6.0f + n12 / 2.0f + n14, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(n13 / 2.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(-n13 / 2.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            this.humanoidModel.renderCloak(0.0625f);
            GL11.glPopMatrix();
        }
        ItemInstance selected = mob.inventory.getSelected();
        if (selected != null) {
            GL11.glPushMatrix();
            this.humanoidModel.arm0.translateTo(0.0625f);
            GL11.glTranslatef(-0.0625f, 0.4375f, 0.0625f);
            if (mob.fishing != null) {
                selected = new ItemInstance(Item.stick);
            }
            if (selected.id < 256 && TileRenderer.canRender(Tile.tiles[selected.id].getRenderShape())) {
                final float n15 = 0.5f;
                GL11.glTranslatef(0.0f, 0.1875f, -0.3125f);
                final float n16 = n15 * 0.75f;
                GL11.glRotatef(20.0f, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
                GL11.glScalef(n16, -n16, n16);
            }
            else if (Item.items[selected.id].isHandEquipped()) {
                final float n17 = 0.625f;
                if (Item.items[selected.id].isMirroredArt()) {
                    GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                    GL11.glTranslatef(0.0f, -0.125f, 0.0f);
                }
                GL11.glTranslatef(0.0f, 0.1875f, 0.0f);
                GL11.glScalef(n17, -n17, n17);
                GL11.glRotatef(-100.0f, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
            }
            else {
                final float n18 = 0.375f;
                GL11.glTranslatef(0.25f, 0.1875f, -0.1875f);
                GL11.glScalef(n18, n18, n18);
                GL11.glRotatef(60.0f, 0.0f, 0.0f, 1.0f);
                GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(20.0f, 0.0f, 0.0f, 1.0f);
            }
            this.entityRenderDispatcher.itemInHandRenderer.renderItem(mob, selected);
            GL11.glPopMatrix();
        }
    }

    @Override
    protected void scale(final Player mob, final float partialTick) {
        final float n = 0.9375f;
        GL11.glScalef(n, n, n);
    }
    
    public void renderHand() {
        this.humanoidModel.attackTime = 0.0f;
        this.humanoidModel.setupAnim(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        this.humanoidModel.arm0.render(0.0625f);
    }
    
    protected void setupPosition(final Player mob, final double x, final double y, final double z) {
        if (mob.isAlive() && mob.isSleeping()) {
            super.setupPosition(mob, x + mob.bedOffsetX, y + mob.bedOffsetY, z + mob.bedOffsetZ);
        }
        else {
            super.setupPosition(mob, x, y, z);
        }
    }
    
    protected void setupRotations(final Player mob, final float bob, final float bodyRot, final float partialTick) {
        if (mob.isAlive() && mob.isSleeping()) {
            GL11.glRotatef(mob.getSleepRotation(), 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(this.getFlipDegrees(mob), 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
        }
        else {
            super.setupRotations(mob, bob, bodyRot, partialTick);
        }
    }
    
    static {
        MATERIAL_NAMES = new String[] { "cloth", "chain", "iron", "diamond", "gold" };
    }
}
