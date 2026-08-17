// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import util.Mth;
import net.minecraft.client.renderer.TileRenderer;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.model.HumanoidModel;

import static org.lwjgl.opengl.GL11.*;

public class PlayerRenderer extends MobRenderer<Player>
{
    private final HumanoidModel humanoidModel = (HumanoidModel)this.model;
    private final HumanoidModel armorParts1 = new HumanoidModel(1.0f);
    private final HumanoidModel armorParts2 = new HumanoidModel(0.5f);
    private static final String[] MATERIAL_NAMES = new String[] { "cloth", "chain", "iron", "diamond", "gold" };
    
    public PlayerRenderer() {
        super(new HumanoidModel(0.0f), 0.5f);
    }
    
    protected boolean prepareArmor(final Player player, final int layer, final float a) {
        final ItemInstance itemInstance = player.inventory.getArmor(3 - layer);
        if (itemInstance != null) {
            final Item item = itemInstance.getItem();
            if (item instanceof ArmorItem) {
                ArmorItem armorItem = (ArmorItem)item;
                this.bindTexture("/armor/" + PlayerRenderer.MATERIAL_NAMES[armorItem.materialIcon] + "_" + ((layer == 2) ? 2 : 1) + ".png");

                final HumanoidModel armor2 = (layer == 2) ? this.armorParts2 : this.armorParts1;

                armor2.head.visible = layer == 0;
                armor2.hair.visible = layer == 0;
                armor2.body.visible = layer == 1 || layer == 2;
                armor2.arm0.visible = layer == 1;
                armor2.arm1.visible = layer == 1;
                armor2.leg0.visible = layer == 2 || layer == 3;
                armor2.leg1.visible = layer == 2 || layer == 3;

                this.setArmor(armor2);
                return true;
            }
        }
        return false;
    }
    
    public void render(final Player player, final double x, final double y, final double z, final float rot, final float a) {
        final ItemInstance item = player.inventory.getSelected();

        this.armorParts1.holdingRightHand = this.armorParts2.holdingRightHand = this.humanoidModel.holdingRightHand = (((item != null) ? 1 : 0) != 0);
        this.armorParts1.sneaking = this.armorParts2.sneaking = this.humanoidModel.sneaking = player.isSneaking();

        double yp = y - player.heightOffset;
        if (player.isSneaking() && !(player instanceof LocalPlayer)) {
            yp -= 2 / 16.0f;
        }

        super.render(player, x, yp, z, rot, a);

        this.armorParts1.sneaking = this.armorParts2.sneaking = this.humanoidModel.sneaking = false;
        this.armorParts1.holdingRightHand = this.armorParts2.holdingRightHand = this.humanoidModel.holdingRightHand = false;
    }
    
    protected void renderName(final Player player, final double x, final double y, final double z) {
        if (Minecraft.renderNames() && player != this.entityRenderDispatcher.player) {
            float size = 1.6f;
            float s = 1 / 60.0f * size;
            double dist = player.distanceTo(this.entityRenderDispatcher.player);

            float maxDist = player.isSneaking() ? 32.0f : 64.0f;

            if (dist < maxDist) {
                final String msg = player.name;

                if (player.isSneaking()) {
                    final Font font = this.getFont();
                    glPushMatrix();
                    glTranslatef((float)x + 0.0f, (float)y + 2.3f, (float)z);
                    glNormal3f(0.0f, 1.0f, 0.0f);

                    glRotatef(-this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);
                    glRotatef(this.entityRenderDispatcher.playerRotX, 1.0f, 0.0f, 0.0f);

                    glScalef(-s, -s, s);
                    glDisable(GL_LIGHTING);

                    glTranslatef(0.0f, 0.25f / s, 0.0f);
                    glDepthMask(false);
                    glEnable(GL_BLEND);
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    final Tesselator t = Tesselator.instance;

                    glDisable(GL_TEXTURE_2D);
                    t.begin();
                    final int w = font.width(msg) / 2;
                    t.color(0.0f, 0.0f, 0.0f, 0.25f);
                    t.vertex(-w - 1, -1.0, 0.0);
                    t.vertex(-w - 1, +8.0, 0.0);
                    t.vertex(+w + 1, +8.0, 0.0);
                    t.vertex(+w + 1, -1.0, 0.0);
                    t.end();
                    glEnable(GL_TEXTURE_2D);
                    glDepthMask(true);
                    font.draw(msg, -font.width(msg) / 2, 0, 0x20ffffff);
                    glEnable(GL_LIGHTING);
                    glDisable(GL_BLEND);
                    glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    glPopMatrix();
                } else {
                    if (player.isSleeping()) {
                        this.renderNameTag(player, msg, x, y - 1.5, z, 64);
                    }
                    else {
                        this.renderNameTag(player, msg, x, y, z, 64);
                    }
                }
            }
        }
    }

    @Override
    protected void additionalRendering(final Player player, final float a) {
        final ItemInstance headGear = player.inventory.getArmor(3);
        if (headGear != null) {
            if (headGear.getItem().id < Tile.TILE_NUM_COUNT) {
                glPushMatrix();
                this.humanoidModel.head.translateTo(1 / 16.0f);
                if (TileRenderer.canRender(Tile.tiles[headGear.id].getRenderShape())) {
                    final float s = 10 / 16.0f;
                    glTranslatef(0 / 16.0f, -4 / 16.0f, 0 / 16.0f);
                    glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                    glScalef(s, -s, s);
                }
                this.entityRenderDispatcher.itemInHandRenderer.renderItem(player, headGear);
                glPopMatrix();
            }
        }
        if (player.name.equals("deadmau5") && this.bindTexture(player.customTextureUrl, null)) {
            for (int i = 0; i < 2; ++i) {
                final float yr = player.yRotO + (player.yRot - player.yRotO) * a - (player.yBodyRotO + (player.yBodyRot - player.yBodyRotO) * a);
                final float xr = player.xRotO + (player.xRot - player.xRotO) * a;
                glPushMatrix();
                glRotatef(yr, 0.0f, 1.0f, 0.0f);
                glRotatef(xr, 1.0f, 0.0f, 0.0f);
                glTranslatef((6 / 16.0f) * (i * 2 - 1), 0.0f, 0.0f);
                glTranslatef(0.0f, -6 / 16.0f, 0.0f);
                glRotatef(-xr, 1.0f, 0.0f, 0.0f);
                glRotatef(-yr, 0.0f, 1.0f, 0.0f);

                final float s = 8 / 6.0f;
                glScalef(s, s, s);
                this.humanoidModel.renderEars(1 / 16.0f);
                glPopMatrix();
            }
        }

        if (this.bindTexture(player.cloakTexture, null)) {
            glPushMatrix();
            glTranslatef(0.0f, 0.0f, 2 / 16.0f);

            final double xd = player.xCloakO + (player.xCloak - player.xCloakO) * a - (player.xo + (player.x - player.xo) * a);
            final double yd = player.yCloakO + (player.yCloak - player.yCloakO) * a - (player.yo + (player.y - player.yo) * a);
            final double zd = player.zCloakO + (player.zCloak - player.zCloakO) * a - (player.zo + (player.z - player.zo) * a);

            final float yr = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO) * a;

            final double xa = Mth.sin(yr * Mth.DEGRAD);
            final double za = -Mth.cos(yr * Mth.DEGRAD);

            float flap = (float)yd * 10.0f;
            if (flap < -6.0f) flap = -6.0f;
            if (flap > 32.0f) flap = 32.0f;
            float lean = (float)(xd * xa + zd * za) * 100.0f;
            final float lean2 = (float)(xd * za - zd * xa) * 100.0f;
            if (lean < 0.0f) lean = 0.0f;

            float pow = (player.oBob + (player.bob - player.oBob) * a);

            flap += Mth.sin((player.walkDistO + (player.walkDist - player.walkDistO) * a) * 6.0f) * 32.0f * pow;
            if (player.isSneaking()) flap += 25.0f;

            float xRot = 6.0f + lean / 2.0f + flap;

            glRotatef(xRot, 1.0f, 0.0f, 0.0f);
            glRotatef(lean2 / 2.0f, 0.0f, 0.0f, 1.0f);
            glRotatef(-lean2 / 2.0f, 0.0f, 1.0f, 0.0f);
            glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            this.humanoidModel.renderCloak(1 / 16.0f);
            glPopMatrix();
        }

        ItemInstance item = player.inventory.getSelected();
        if (item != null) {
            glPushMatrix();
            this.humanoidModel.arm0.translateTo(1 / 16.0f);
            glTranslatef(-1 / 16.0f, 7 / 16.0f, 1 / 16.0f);

            if (player.fishing != null) {
                item = new ItemInstance(Item.stick);
            }

            if (item.id < Tile.TILE_NUM_COUNT && TileRenderer.canRender(Tile.tiles[item.id].getRenderShape())) {
                float s = 8 / 16.0f;
                glTranslatef(0 / 16.0f, 3 / 16.0f, -5 / 16.0f);
                s *= 0.75f;
                glRotatef(20.0f, 1.0f, 0.0f, 0.0f);
                glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
                glScalef(s, -s, s);
            }
            else if (Item.items[item.id].isHandEquipped()) {
                final float s = 10 / 16.0f;
                if (Item.items[item.id].isMirroredArt()) {
                    glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                    glTranslatef(0.0f, -2 / 16.0f, 0.0f);
                }
                glTranslatef(0.0f, 3 / 16.0f, 0.0f);
                glScalef(s, -s, s);
                glRotatef(-100.0f, 1.0f, 0.0f, 0.0f);
                glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
            }
            else {
                final float s = 6 / 16.0f;
                glTranslatef(4 / 16.0f, 3 / 16.0f, -3 / 16.0f);
                glScalef(s, s, s);
                glRotatef(60.0f, 0.0f, 0.0f, 1.0f);
                glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                glRotatef(20.0f, 0.0f, 0.0f, 1.0f);
            }

            this.entityRenderDispatcher.itemInHandRenderer.renderItem(player, item);
            glPopMatrix();
        }
    }

    @Override
    protected void scale(final Player player, final float a) {
        final float s = 15 / 16.0f;
        glScalef(s, s, s);
    }
    
    public void renderHand() {
        this.humanoidModel.attackTime = 0.0f;
        this.humanoidModel.setupAnim(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1 / 16.0f);
        this.humanoidModel.arm0.render(1 / 16.0f);
    }
    
    protected void setupPosition(final Player player, final double x, final double y, final double z) {
        if (player.isAlive() && player.isSleeping()) {
            super.setupPosition(player, x + player.bedOffsetX, y + player.bedOffsetY, z + player.bedOffsetZ);
        }
        else {
            super.setupPosition(player, x, y, z);
        }
    }
    
    protected void setupRotations(final Player player, final float bob, final float bodyRot, final float a) {
        if (player.isAlive() && player.isSleeping()) {
            glRotatef(player.getSleepRotation(), 0.0f, 1.0f, 0.0f);
            glRotatef(this.getFlipDegrees(player), 0.0f, 0.0f, 1.0f);
            glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
        }
        else {
            super.setupRotations(player, bob, bodyRot, a);
        }
    }

}
