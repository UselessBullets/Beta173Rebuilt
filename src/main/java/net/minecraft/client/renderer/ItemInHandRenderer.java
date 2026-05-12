// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.level.material.Material;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.world.item.Item;
import util.Mth;
import net.minecraft.client.Lighting;
import net.minecraft.world.level.tile.Tile;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.Minimap;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.client.Minecraft;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class ItemInHandRenderer
{
    private Minecraft mc;
    private ItemInstance selectedItem;
    private float height;
    private float oHeight;
    private TileRenderer tileRenderer;
    private Minimap minimap;
    private int lastSlot;
    
    public ItemInHandRenderer(final Minecraft mc) {
        this.selectedItem = null;
        this.height = 0.0f;
        this.oHeight = 0.0f;
        this.tileRenderer = new TileRenderer();
        this.lastSlot = -1;
        this.mc = mc;
        this.minimap = new Minimap(mc.font, mc.options, mc.textures);
    }
    
    public void renderItem(final Mob mob, final ItemInstance item) {
        GL11.glPushMatrix();
        if (item.id < 256 && TileRenderer.canRender(Tile.tiles[item.id].getRenderShape())) {
            GL11.glBindTexture(3553, this.mc.textures.loadTexture("/terrain.png"));
            this.tileRenderer.renderTile(Tile.tiles[item.id], item.getAuxValue(), mob.getBrightness(1.0f));
        }
        else {
            if (item.id < 256) {
                GL11.glBindTexture(3553, this.mc.textures.loadTexture("/terrain.png"));
            }
            else {
                GL11.glBindTexture(3553, this.mc.textures.loadTexture("/gui/items.png"));
            }
            final Tesselator instance = Tesselator.instance;
            final int itemInHandIcon = mob.getItemInHandIcon(item);
            final float n = (itemInHandIcon % 16 * 16 + 0.0f) / 256.0f;
            final float n2 = (itemInHandIcon % 16 * 16 + 15.99f) / 256.0f;
            final float n3 = (itemInHandIcon / 16 * 16 + 0.0f) / 256.0f;
            final float n4 = (itemInHandIcon / 16 * 16 + 15.99f) / 256.0f;
            final float n5 = 1.0f;
            final float n6 = 0.0f;
            final float n7 = 0.3f;
            GL11.glEnable(GL_RESCALE_NORMAL);
            GL11.glTranslatef(-n6, -n7, 0.0f);
            final float n8 = 1.5f;
            GL11.glScalef(n8, n8, n8);
            GL11.glRotatef(50.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(335.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslatef(-0.9375f, -0.0625f, 0.0f);
            final float n9 = 0.0625f;
            instance.begin();
            instance.normal(0.0f, 0.0f, 1.0f);
            instance.vertexUV(0.0, 0.0, 0.0, n2, n4);
            instance.vertexUV(n5, 0.0, 0.0, n, n4);
            instance.vertexUV(n5, 1.0, 0.0, n, n3);
            instance.vertexUV(0.0, 1.0, 0.0, n2, n3);
            instance.end();
            instance.begin();
            instance.normal(0.0f, 0.0f, -1.0f);
            instance.vertexUV(0.0, 1.0, 0.0f - n9, n2, n3);
            instance.vertexUV(n5, 1.0, 0.0f - n9, n, n3);
            instance.vertexUV(n5, 0.0, 0.0f - n9, n, n4);
            instance.vertexUV(0.0, 0.0, 0.0f - n9, n2, n4);
            instance.end();
            instance.begin();
            instance.normal(-1.0f, 0.0f, 0.0f);
            for (int i = 0; i < 16; ++i) {
                final float n10 = i / 16.0f;
                final float n11 = n2 + (n - n2) * n10 - 0.001953125f;
                final float n12 = n5 * n10;
                instance.vertexUV(n12, 0.0, 0.0f - n9, n11, n4);
                instance.vertexUV(n12, 0.0, 0.0, n11, n4);
                instance.vertexUV(n12, 1.0, 0.0, n11, n3);
                instance.vertexUV(n12, 1.0, 0.0f - n9, n11, n3);
            }
            instance.end();
            instance.begin();
            instance.normal(1.0f, 0.0f, 0.0f);
            for (int j = 0; j < 16; ++j) {
                final float n13 = j / 16.0f;
                final float n14 = n2 + (n - n2) * n13 - 0.001953125f;
                final float n15 = n5 * n13 + 0.0625f;
                instance.vertexUV(n15, 1.0, 0.0f - n9, n14, n3);
                instance.vertexUV(n15, 1.0, 0.0, n14, n3);
                instance.vertexUV(n15, 0.0, 0.0, n14, n4);
                instance.vertexUV(n15, 0.0, 0.0f - n9, n14, n4);
            }
            instance.end();
            instance.begin();
            instance.normal(0.0f, 1.0f, 0.0f);
            for (int k = 0; k < 16; ++k) {
                final float n16 = k / 16.0f;
                final float n17 = n4 + (n3 - n4) * n16 - 0.001953125f;
                final float n18 = n5 * n16 + 0.0625f;
                instance.vertexUV(0.0, n18, 0.0, n2, n17);
                instance.vertexUV(n5, n18, 0.0, n, n17);
                instance.vertexUV(n5, n18, 0.0f - n9, n, n17);
                instance.vertexUV(0.0, n18, 0.0f - n9, n2, n17);
            }
            instance.end();
            instance.begin();
            instance.normal(0.0f, -1.0f, 0.0f);
            for (int l = 0; l < 16; ++l) {
                final float n19 = l / 16.0f;
                final float n20 = n4 + (n3 - n4) * n19 - 0.001953125f;
                final float n21 = n5 * n19;
                instance.vertexUV(n5, n21, 0.0, n, n20);
                instance.vertexUV(0.0, n21, 0.0, n2, n20);
                instance.vertexUV(0.0, n21, 0.0f - n9, n2, n20);
                instance.vertexUV(n5, n21, 0.0f - n9, n, n20);
            }
            instance.end();
            GL11.glDisable(32826);
        }
        GL11.glPopMatrix();
    }
    
    public void render(final float partialTick) {
        final float n = this.oHeight + (this.height - this.oHeight) * partialTick;
        final LocalPlayer player = this.mc.player;
        final float n2 = player.xRotO + (player.xRot - player.xRotO) * partialTick;
        GL11.glPushMatrix();
        GL11.glRotatef(n2, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(player.yRotO + (player.yRot - player.yRotO) * partialTick, 0.0f, 1.0f, 0.0f);
        Lighting.turnOn();
        GL11.glPopMatrix();
        final ItemInstance selectedItem = this.selectedItem;
        final float brightness = this.mc.level.getBrightness(Mth.floor(player.x), Mth.floor(player.y), Mth.floor(player.z));
        if (selectedItem != null) {
            final int color = Item.items[selectedItem.id].getColor(selectedItem.getAuxValue());
            GL11.glColor4f(brightness * ((color >> 16 & 0xFF) / 255.0f), brightness * ((color >> 8 & 0xFF) / 255.0f), brightness * ((color & 0xFF) / 255.0f), 1.0f);
        }
        else {
            GL11.glColor4f(brightness, brightness, brightness, 1.0f);
        }
        if (selectedItem != null && selectedItem.id == Item.map.id) {
            GL11.glPushMatrix();
            final float n3 = 0.8f;
            final float attackAnim = player.getAttackAnim(partialTick);
            GL11.glTranslatef(-Mth.sin(Mth.sqrt(attackAnim) * 3.1415927f) * 0.4f, Mth.sin(Mth.sqrt(attackAnim) * 3.1415927f * 2.0f) * 0.2f, -Mth.sin(attackAnim * 3.1415927f) * 0.2f);
            float n4 = 1.0f - n2 / 45.0f + 0.1f;
            if (n4 < 0.0f) {
                n4 = 0.0f;
            }
            if (n4 > 1.0f) {
                n4 = 1.0f;
            }
            final float n5 = -Mth.cos(n4 * 3.1415927f) * 0.5f + 0.5f;
            GL11.glTranslatef(0.0f, 0.0f * n3 - (1.0f - n) * 1.2f - n5 * 0.5f + 0.04f, -0.9f * n3);
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(n5 * -85.0f, 0.0f, 0.0f, 1.0f);
            GL11.glEnable(GL_RESCALE_NORMAL);
            GL11.glBindTexture(3553, this.mc.textures.loadHttpTexture(this.mc.player.customTextureUrl, this.mc.player.getTexture()));
            for (int i = 0; i < 2; ++i) {
                final int n6 = i * 2 - 1;
                GL11.glPushMatrix();
                GL11.glTranslatef(-0.0f, -0.6f, 1.1f * n6);
                GL11.glRotatef((float)(-45 * n6), 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                GL11.glRotatef(59.0f, 0.0f, 0.0f, 1.0f);
                GL11.glRotatef((float)(-65 * n6), 0.0f, 1.0f, 0.0f);
                final EntityRenderer<Player> er = EntityRenderDispatcher.instance.getRenderer(this.mc.player);
                final PlayerRenderer playerRenderer = (PlayerRenderer) er;
                final float n7 = 1.0f;
                GL11.glScalef(n7, n7, n7);
                playerRenderer.renderHand();
                GL11.glPopMatrix();
            }
            final float attackAnim2 = player.getAttackAnim(partialTick);
            final float sin = Mth.sin(attackAnim2 * attackAnim2 * 3.1415927f);
            final float sin2 = Mth.sin(Mth.sqrt(attackAnim2) * 3.1415927f);
            GL11.glRotatef(-sin * 20.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(-sin2 * 20.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(-sin2 * 80.0f, 1.0f, 0.0f, 0.0f);
            final float n8 = 0.38f;
            GL11.glScalef(n8, n8, n8);
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslatef(-1.0f, -1.0f, 0.0f);
            final float n9 = 0.015625f;
            GL11.glScalef(n9, n9, n9);
            this.mc.textures.bind(this.mc.textures.loadTexture("/misc/mapbg.png"));
            final Tesselator instance = Tesselator.instance;
            GL11.glNormal3f(0.0f, 0.0f, -1.0f);
            instance.begin();
            final int n10 = 7;
            instance.vertexUV(0 - n10, 128 + n10, 0.0, 0.0, 1.0);
            instance.vertexUV(128 + n10, 128 + n10, 0.0, 1.0, 1.0);
            instance.vertexUV(128 + n10, 0 - n10, 0.0, 1.0, 0.0);
            instance.vertexUV(0 - n10, 0 - n10, 0.0, 0.0, 0.0);
            instance.end();
            this.minimap.render(this.mc.player, this.mc.textures, Item.map.getSavedData(selectedItem, this.mc.level));
            GL11.glPopMatrix();
        }
        else if (selectedItem != null) {
            GL11.glPushMatrix();
            final float n11 = 0.8f;
            final float attackAnim3 = player.getAttackAnim(partialTick);
            GL11.glTranslatef(-Mth.sin(Mth.sqrt(attackAnim3) * 3.1415927f) * 0.4f, Mth.sin(Mth.sqrt(attackAnim3) * 3.1415927f * 2.0f) * 0.2f, -Mth.sin(attackAnim3 * 3.1415927f) * 0.2f);
            GL11.glTranslatef(0.7f * n11, -0.65f * n11 - (1.0f - n) * 0.6f, -0.9f * n11);
            GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
            GL11.glEnable(GL_RESCALE_NORMAL);
            final float attackAnim4 = player.getAttackAnim(partialTick);
            final float sin3 = Mth.sin(attackAnim4 * attackAnim4 * 3.1415927f);
            final float sin4 = Mth.sin(Mth.sqrt(attackAnim4) * 3.1415927f);
            GL11.glRotatef(-sin3 * 20.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(-sin4 * 20.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(-sin4 * 80.0f, 1.0f, 0.0f, 0.0f);
            final float n12 = 0.4f;
            GL11.glScalef(n12, n12, n12);
            if (selectedItem.getItem().isMirroredArt()) {
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            }
            this.renderItem(player, selectedItem);
            GL11.glPopMatrix();
        }
        else {
            GL11.glPushMatrix();
            final float n13 = 0.8f;
            final float attackAnim5 = player.getAttackAnim(partialTick);
            GL11.glTranslatef(-Mth.sin(Mth.sqrt(attackAnim5) * 3.1415927f) * 0.3f, Mth.sin(Mth.sqrt(attackAnim5) * 3.1415927f * 2.0f) * 0.4f, -Mth.sin(attackAnim5 * 3.1415927f) * 0.4f);
            GL11.glTranslatef(0.8f * n13, -0.75f * n13 - (1.0f - n) * 0.6f, -0.9f * n13);
            GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
            GL11.glEnable(GL_RESCALE_NORMAL);
            final float attackAnim6 = player.getAttackAnim(partialTick);
            final float sin5 = Mth.sin(attackAnim6 * attackAnim6 * 3.1415927f);
            GL11.glRotatef(Mth.sin(Mth.sqrt(attackAnim6) * 3.1415927f) * 70.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(-sin5 * 20.0f, 0.0f, 0.0f, 1.0f);
            GL11.glBindTexture(3553, this.mc.textures.loadHttpTexture(this.mc.player.customTextureUrl, this.mc.player.getTexture()));
            GL11.glTranslatef(-1.0f, 3.6f, 3.5f);
            GL11.glRotatef(120.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(200.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(-135.0f, 0.0f, 1.0f, 0.0f);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glTranslatef(5.6f, 0.0f, 0.0f);
            final EntityRenderer<Player> er = EntityRenderDispatcher.instance.getRenderer(this.mc.player);
            final PlayerRenderer playerRenderer2 = (PlayerRenderer)er;
            final float n14 = 1.0f;
            GL11.glScalef(n14, n14, n14);
            playerRenderer2.renderHand();
            GL11.glPopMatrix();
        }
        GL11.glDisable(32826);
        Lighting.turnOff();
    }
    
    public void renderScreenEffect(final float partialTick) {
        GL11.glDisable(3008);
        if (this.mc.player.isOnFire()) {
            GL11.glBindTexture(3553, this.mc.textures.loadTexture("/terrain.png"));
            this.renderFire(partialTick);
        }
        if (this.mc.player.isInWall()) {
            final int floor = Mth.floor(this.mc.player.x);
            final int floor2 = Mth.floor(this.mc.player.y);
            final int floor3 = Mth.floor(this.mc.player.z);
            GL11.glBindTexture(3553, this.mc.textures.loadTexture("/terrain.png"));
            int n = this.mc.level.getTile(floor, floor2, floor3);
            if (this.mc.level.isSolidBlockingTile(floor, floor2, floor3)) {
                this.renderTex(partialTick, Tile.tiles[n].getTexture(2));
            }
            else {
                for (int i = 0; i < 8; ++i) {
                    final float n2 = ((i >> 0) % 2 - 0.5f) * this.mc.player.bbWidth * 0.9f;
                    final float n3 = ((i >> 1) % 2 - 0.5f) * this.mc.player.bbHeight * 0.2f;
                    final float n4 = ((i >> 2) % 2 - 0.5f) * this.mc.player.bbWidth * 0.9f;
                    final int floor4 = Mth.floor(floor + n2);
                    final int floor5 = Mth.floor(floor2 + n3);
                    final int floor6 = Mth.floor(floor3 + n4);
                    if (this.mc.level.isSolidBlockingTile(floor4, floor5, floor6)) {
                        n = this.mc.level.getTile(floor4, floor5, floor6);
                    }
                }
            }
            if (Tile.tiles[n] != null) {
                this.renderTex(partialTick, Tile.tiles[n].getTexture(2));
            }
        }
        if (this.mc.player.isUnderLiquid(Material.water)) {
            GL11.glBindTexture(3553, this.mc.textures.loadTexture("/misc/water.png"));
            this.renderWater(partialTick);
        }
        GL11.glEnable(3008);
    }
    
    private void renderTex(final float partialTick, final int tex) {
        final Tesselator instance = Tesselator.instance;
        this.mc.player.getBrightness(partialTick);
        final float n = 0.1f;
        GL11.glColor4f(n, n, n, 0.5f);
        GL11.glPushMatrix();
        final float n2 = -1.0f;
        final float n3 = 1.0f;
        final float n4 = -1.0f;
        final float n5 = 1.0f;
        final float n6 = -0.5f;
        final float n7 = 0.0078125f;
        final float n8 = tex % 16 / 256.0f - n7;
        final float n9 = (tex % 16 + 15.99f) / 256.0f + n7;
        final float n10 = tex / 16 / 256.0f - n7;
        final float n11 = (tex / 16 + 15.99f) / 256.0f + n7;
        instance.begin();
        instance.vertexUV(n2, n4, n6, n9, n11);
        instance.vertexUV(n3, n4, n6, n8, n11);
        instance.vertexUV(n3, n5, n6, n8, n10);
        instance.vertexUV(n2, n5, n6, n9, n10);
        instance.end();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private void renderWater(final float partialTick) {
        final Tesselator instance = Tesselator.instance;
        final float brightness = this.mc.player.getBrightness(partialTick);
        GL11.glColor4f(brightness, brightness, brightness, 0.5f);
        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(770, 771);
        GL11.glPushMatrix();
        final float n = 4.0f;
        final float n2 = -1.0f;
        final float n3 = 1.0f;
        final float n4 = -1.0f;
        final float n5 = 1.0f;
        final float n6 = -0.5f;
        final float n7 = -this.mc.player.yRot / 64.0f;
        final float n8 = this.mc.player.xRot / 64.0f;
        instance.begin();
        instance.vertexUV(n2, n4, n6, n + n7, n + n8);
        instance.vertexUV(n3, n4, n6, 0.0f + n7, n + n8);
        instance.vertexUV(n3, n5, n6, 0.0f + n7, 0.0f + n8);
        instance.vertexUV(n2, n5, n6, n + n7, 0.0f + n8);
        instance.end();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3042);
    }
    
    private void renderFire(final float partialTick) {
        final Tesselator instance = Tesselator.instance;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.9f);
        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(770, 771);
        final float n = 1.0f;
        for (int i = 0; i < 2; ++i) {
            GL11.glPushMatrix();
            final int n2 = Tile.fire.tex + i * 16;
            final int n3 = (n2 & 0xF) << 4;
            final int n4 = n2 & 0xF0;
            final float n5 = n3 / 256.0f;
            final float n6 = (n3 + 15.99f) / 256.0f;
            final float n7 = n4 / 256.0f;
            final float n8 = (n4 + 15.99f) / 256.0f;
            final float n9 = (0.0f - n) / 2.0f;
            final float n10 = n9 + n;
            final float n11 = 0.0f - n / 2.0f;
            final float n12 = n11 + n;
            final float n13 = -0.5f;
            GL11.glTranslatef(-(i * 2 - 1) * 0.24f, -0.3f, 0.0f);
            GL11.glRotatef((i * 2 - 1) * 10.0f, 0.0f, 1.0f, 0.0f);
            instance.begin();
            instance.vertexUV(n9, n11, n13, n6, n8);
            instance.vertexUV(n10, n11, n13, n5, n8);
            instance.vertexUV(n10, n12, n13, n5, n7);
            instance.vertexUV(n9, n12, n13, n6, n7);
            instance.end();
            GL11.glPopMatrix();
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3042);
    }
    
    public void tick() {
        this.oHeight = this.height;
        final LocalPlayer player = this.mc.player;
        final ItemInstance selected = player.inventory.getSelected();
        boolean b = this.lastSlot == player.inventory.selected && selected == this.selectedItem;
        if (this.selectedItem == null && selected == null) {
            b = true;
        }
        if (selected != null && this.selectedItem != null && selected != this.selectedItem && selected.id == this.selectedItem.id && selected.getAuxValue() == this.selectedItem.getAuxValue()) {
            this.selectedItem = selected;
            b = true;
        }
        final float n = 0.4f;
        float n2 = (b ? 1.0f : 0.0f) - this.height;
        if (n2 < -n) {
            n2 = -n;
        }
        if (n2 > n) {
            n2 = n;
        }
        this.height += n2;
        if (this.height < 0.1f) {
            this.selectedItem = selected;
            this.lastSlot = player.inventory.selected;
        }
    }
    
    public void itemPlaced() {
        this.height = 0.0f;
    }
    
    public void itemUsed() {
        this.height = 0.0f;
    }
}
