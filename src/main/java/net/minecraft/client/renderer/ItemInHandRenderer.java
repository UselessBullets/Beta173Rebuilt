// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.level.material.Material;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.world.item.Item;
import util.Mth;
import net.minecraft.client.Lighting;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.client.Minecraft;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class ItemInHandRenderer
{
    private Minecraft mc;
    private ItemInstance selectedItem = null;
    private float height = 0.0f;
    private float oHeight = 0.0f;
    private TileRenderer tileRenderer = new TileRenderer();
    private Minimap minimap;
    private int lastSlot = -1;
    
    public ItemInHandRenderer(final Minecraft mc) {
        this.mc = mc;
        this.minimap = new Minimap(mc.font, mc.options, mc.textures);
    }
    
    public void renderItem(final Mob mob, final ItemInstance item) {
        glPushMatrix();
        if (item.id < Tile.TILE_NUM_COUNT && TileRenderer.canRender(Tile.tiles[item.id].getRenderShape())) {
            glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadTexture("/terrain.png"));
            this.tileRenderer.renderTile(Tile.tiles[item.id], item.getAuxValue(), mob.getBrightness(1.0f));
        }
        else {
            if (item.id < Tile.TILE_NUM_COUNT) {
                glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadTexture("/terrain.png"));
            }
            else {
                glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadTexture("/gui/items.png"));
            }

            final Tesselator t = Tesselator.instance;
            final int icon = mob.getItemInHandIcon(item);
            final float u1 = (icon % 16 * 16 + 0.0f) / 256.0f;
            final float u0 = (icon % 16 * 16 + 15.99f) / 256.0f;
            final float v0 = (icon / 16 * 16 + 0.0f) / 256.0f;
            final float v1 = (icon / 16 * 16 + 15.99f) / 256.0f;

            final float r = 1.0f;
            final float xo = 0.0f;
            final float yo = 0.3f;

            glEnable(GL_RESCALE_NORMAL);
            glTranslatef(-xo, -yo, 0.0f);
            final float s = 1.5f;
            glScalef(s, s, s);

            glRotatef(50.0f, 0.0f, 1.0f, 0.0f);
            glRotatef(45 + 290, 0.0f, 0.0f, 1.0f);
            glTranslatef(-15 / 16.0f, -1 / 16.0f, 0);
            final float dd = 1 / 16.0f;

            t.begin();
            t.normal(0.0f, 0.0f, 1.0f);
            t.vertexUV(0.0, 0.0, 0.0, u0, v1);
            t.vertexUV(r, 0.0, 0.0, u1, v1);
            t.vertexUV(r, 1.0, 0.0, u1, v0);
            t.vertexUV(0.0, 1.0, 0.0, u0, v0);
            t.end();

            t.begin();
            t.normal(0.0f, 0.0f, -1.0f);
            t.vertexUV(0.0, 1.0, 0.0f - dd, u0, v0);
            t.vertexUV(r, 1.0, 0.0f - dd, u1, v0);
            t.vertexUV(r, 0.0, 0.0f - dd, u1, v1);
            t.vertexUV(0.0, 0.0, 0.0f - dd, u0, v1);
            t.end();
            t.begin();
            t.normal(-1.0f, 0.0f, 0.0f);

            for (int i = 0; i < 16; ++i) {
                final float p = i / 16.0f;
                final float uu = u0 + (u1 - u0) * p - 0.5f / 256.0f;
                final float xx = r * p;
                t.vertexUV(xx, 0.0, 0.0f - dd, uu, v1);
                t.vertexUV(xx, 0.0, 0.0, uu, v1);
                t.vertexUV(xx, 1.0, 0.0, uu, v0);
                t.vertexUV(xx, 1.0, 0.0f - dd, uu, v0);
            }

            t.end();
            t.begin();
            t.normal(1.0f, 0.0f, 0.0f);

            for (int i = 0; i < 16; ++i) {
                final float p = i / 16.0f;
                final float uu = u0 + (u1 - u0) * p - 0.5f / 256.0f;
                final float xx = r * p + 0.0625f;
                t.vertexUV(xx, 1.0, 0.0f - dd, uu, v0);
                t.vertexUV(xx, 1.0, 0.0, uu, v0);
                t.vertexUV(xx, 0.0, 0.0, uu, v1);
                t.vertexUV(xx, 0.0, 0.0f - dd, uu, v1);
            }

            t.end();
            t.begin();
            t.normal(0.0f, 1.0f, 0.0f);

            for (int i = 0; i < 16; ++i) {
                final float p = i / 16.0f;
                final float vv = v1 + (v0 - v1) * p - 0.5f / 256.0f;
                final float yy = r * p + 0.0625f;
                t.vertexUV(0.0, yy, 0.0, u0, vv);
                t.vertexUV(r, yy, 0.0, u1, vv);
                t.vertexUV(r, yy, 0.0f - dd, u1, vv);
                t.vertexUV(0.0, yy, 0.0f - dd, u0, vv);
            }

            t.end();
            t.begin();
            t.normal(0.0f, -1.0f, 0.0f);

            for (int l = 0; l < 16; ++l) {
                final float p = l / 16.0f;
                final float vv = v1 + (v0 - v1) * p - 0.5f / 256.0f;
                final float yy = r * p;
                t.vertexUV(r, yy, 0.0, u1, vv);
                t.vertexUV(0.0, yy, 0.0, u0, vv);
                t.vertexUV(0.0, yy, 0.0f - dd, u0, vv);
                t.vertexUV(r, yy, 0.0f - dd, u1, vv);
            }

            t.end();
            glDisable(GL_RESCALE_NORMAL);
        }
        glPopMatrix();
    }
    
    public void render(final float a) {
        final float h = this.oHeight + (this.height - this.oHeight) * a;
        final Player player = this.mc.player;

        final float xr = player.xRotO + (player.xRot - player.xRotO) * a;

        glPushMatrix();
        glRotatef(xr, 1.0f, 0.0f, 0.0f);
        glRotatef(player.yRotO + (player.yRot - player.yRotO) * a, 0.0f, 1.0f, 0.0f);
        Lighting.turnOn();
        glPopMatrix();

        final ItemInstance item = this.selectedItem;
        final float br = this.mc.level.getBrightness(Mth.floor(player.x), Mth.floor(player.y), Mth.floor(player.z));
        if (item != null) {
            final int color = Item.items[item.id].getColor(item.getAuxValue());
            glColor4f(br * ((color >> 16 & 0xFF) / 255.0f), br * ((color >> 8 & 0xFF) / 255.0f), br * ((color & 0xFF) / 255.0f), 1.0f);
        }
        else {
            glColor4f(br, br, br, 1.0f);
        }
        if (item != null && item.id == Item.map.id) {
            glPushMatrix();
            final float n3 = 0.8f;
            final float attackAnim = player.getAttackAnim(a);
            glTranslatef(-Mth.sin(Mth.sqrt(attackAnim) * Mth.PI) * 0.4f, Mth.sin(Mth.sqrt(attackAnim) * Mth.PI * 2.0f) * 0.2f, -Mth.sin(attackAnim * Mth.PI) * 0.2f);
            float n4 = 1.0f - xr / 45.0f + 0.1f;
            if (n4 < 0.0f) {
                n4 = 0.0f;
            }
            if (n4 > 1.0f) {
                n4 = 1.0f;
            }
            final float n5 = -Mth.cos(n4 * Mth.PI) * 0.5f + 0.5f;
            glTranslatef(0.0f, 0.0f * n3 - (1.0f - h) * 1.2f - n5 * 0.5f + 0.04f, -0.9f * n3);
            glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            glRotatef(n5 * -85.0f, 0.0f, 0.0f, 1.0f);
            glEnable(GL_RESCALE_NORMAL);
            glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadHttpTexture(this.mc.player.customTextureUrl, this.mc.player.getTexture()));
            for (int i = 0; i < 2; ++i) {
                final int n6 = i * 2 - 1;
                glPushMatrix();
                glTranslatef(-0.0f, -0.6f, 1.1f * n6);
                glRotatef((float)(-45 * n6), 1.0f, 0.0f, 0.0f);
                glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                glRotatef(59.0f, 0.0f, 0.0f, 1.0f);
                glRotatef((float)(-65 * n6), 0.0f, 1.0f, 0.0f);
                final EntityRenderer<Player> er = EntityRenderDispatcher.instance.getRenderer(this.mc.player);
                final PlayerRenderer playerRenderer = (PlayerRenderer) er;
                final float n7 = 1.0f;
                glScalef(n7, n7, n7);
                playerRenderer.renderHand();
                glPopMatrix();
            }
            final float attackAnim2 = player.getAttackAnim(a);
            final float sin = Mth.sin(attackAnim2 * attackAnim2 * Mth.PI);
            final float sin2 = Mth.sin(Mth.sqrt(attackAnim2) * Mth.PI);
            glRotatef(-sin * 20.0f, 0.0f, 1.0f, 0.0f);
            glRotatef(-sin2 * 20.0f, 0.0f, 0.0f, 1.0f);
            glRotatef(-sin2 * 80.0f, 1.0f, 0.0f, 0.0f);
            final float n8 = 0.38f;
            glScalef(n8, n8, n8);
            glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
            glTranslatef(-1.0f, -1.0f, 0.0f);
            final float n9 = 0.015625f;
            glScalef(n9, n9, n9);
            this.mc.textures.bind(this.mc.textures.loadTexture("/misc/mapbg.png"));
            final Tesselator instance = Tesselator.instance;
            glNormal3f(0.0f, 0.0f, -1.0f);
            instance.begin();
            final int n10 = 7;
            instance.vertexUV(0 - n10, 128 + n10, 0.0, 0.0, 1.0);
            instance.vertexUV(128 + n10, 128 + n10, 0.0, 1.0, 1.0);
            instance.vertexUV(128 + n10, 0 - n10, 0.0, 1.0, 0.0);
            instance.vertexUV(0 - n10, 0 - n10, 0.0, 0.0, 0.0);
            instance.end();
            this.minimap.render(this.mc.player, this.mc.textures, Item.map.getSavedData(item, this.mc.level));
            glPopMatrix();
        }
        else if (item != null) {
            glPushMatrix();
            final float n11 = 0.8f;
            final float attackAnim3 = player.getAttackAnim(a);
            glTranslatef(-Mth.sin(Mth.sqrt(attackAnim3) * Mth.PI) * 0.4f, Mth.sin(Mth.sqrt(attackAnim3) * Mth.PI * 2.0f) * 0.2f, -Mth.sin(attackAnim3 * Mth.PI) * 0.2f);
            glTranslatef(0.7f * n11, -0.65f * n11 - (1.0f - h) * 0.6f, -0.9f * n11);
            glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
            glEnable(GL_RESCALE_NORMAL);
            final float attackAnim4 = player.getAttackAnim(a);
            final float sin3 = Mth.sin(attackAnim4 * attackAnim4 * Mth.PI);
            final float sin4 = Mth.sin(Mth.sqrt(attackAnim4) * Mth.PI);
            glRotatef(-sin3 * 20.0f, 0.0f, 1.0f, 0.0f);
            glRotatef(-sin4 * 20.0f, 0.0f, 0.0f, 1.0f);
            glRotatef(-sin4 * 80.0f, 1.0f, 0.0f, 0.0f);
            final float n12 = 0.4f;
            glScalef(n12, n12, n12);
            if (item.getItem().isMirroredArt()) {
                glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            }
            this.renderItem(player, item);
            glPopMatrix();
        }
        else {
            glPushMatrix();
            final float n13 = 0.8f;
            final float attackAnim5 = player.getAttackAnim(a);
            glTranslatef(-Mth.sin(Mth.sqrt(attackAnim5) * Mth.PI) * 0.3f, Mth.sin(Mth.sqrt(attackAnim5) * Mth.PI * 2.0f) * 0.4f, -Mth.sin(attackAnim5 * Mth.PI) * 0.4f);
            glTranslatef(0.8f * n13, -0.75f * n13 - (1.0f - h) * 0.6f, -0.9f * n13);
            glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
            glEnable(GL_RESCALE_NORMAL);
            final float attackAnim6 = player.getAttackAnim(a);
            final float sin5 = Mth.sin(attackAnim6 * attackAnim6 * Mth.PI);
            glRotatef(Mth.sin(Mth.sqrt(attackAnim6) * Mth.PI) * 70.0f, 0.0f, 1.0f, 0.0f);
            glRotatef(-sin5 * 20.0f, 0.0f, 0.0f, 1.0f);
            glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadHttpTexture(this.mc.player.customTextureUrl, this.mc.player.getTexture()));
            glTranslatef(-1.0f, 3.6f, 3.5f);
            glRotatef(120.0f, 0.0f, 0.0f, 1.0f);
            glRotatef(200.0f, 1.0f, 0.0f, 0.0f);
            glRotatef(-135.0f, 0.0f, 1.0f, 0.0f);
            glScalef(1.0f, 1.0f, 1.0f);
            glTranslatef(5.6f, 0.0f, 0.0f);
            final EntityRenderer<Player> er = EntityRenderDispatcher.instance.getRenderer(this.mc.player);
            final PlayerRenderer playerRenderer2 = (PlayerRenderer)er;
            final float n14 = 1.0f;
            glScalef(n14, n14, n14);
            playerRenderer2.renderHand();
            glPopMatrix();
        }
        glDisable(GL_RESCALE_NORMAL);
        Lighting.turnOff();
    }
    
    public void renderScreenEffect(final float a) {
        glDisable(GL_ALPHA_TEST);
        if (this.mc.player.isOnFire()) {
            glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadTexture("/terrain.png"));
            this.renderFire(a);
        }
        if (this.mc.player.isInWall()) {
            final int floor = Mth.floor(this.mc.player.x);
            final int floor2 = Mth.floor(this.mc.player.y);
            final int floor3 = Mth.floor(this.mc.player.z);
            glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadTexture("/terrain.png"));
            int n = this.mc.level.getTile(floor, floor2, floor3);
            if (this.mc.level.isSolidBlockingTile(floor, floor2, floor3)) {
                this.renderTex(a, Tile.tiles[n].getTexture(2));
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
                this.renderTex(a, Tile.tiles[n].getTexture(2));
            }
        }
        if (this.mc.player.isUnderLiquid(Material.water)) {
            glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadTexture("/misc/water.png"));
            this.renderWater(a);
        }
        glEnable(GL_ALPHA_TEST);
    }
    
    private void renderTex(final float a, final int tex) {
        final Tesselator instance = Tesselator.instance;
        this.mc.player.getBrightness(a);
        final float n = 0.1f;
        glColor4f(n, n, n, 0.5f);
        glPushMatrix();
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
        glPopMatrix();
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private void renderWater(final float a) {
        final Tesselator instance = Tesselator.instance;
        final float brightness = this.mc.player.getBrightness(a);
        glColor4f(brightness, brightness, brightness, 0.5f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glPushMatrix();
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
        glPopMatrix();
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glDisable(GL_BLEND);
    }
    
    private void renderFire(final float a) {
        final Tesselator instance = Tesselator.instance;
        glColor4f(1.0f, 1.0f, 1.0f, 0.9f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        final float n = 1.0f;
        for (int i = 0; i < 2; ++i) {
            glPushMatrix();
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
            glTranslatef(-(i * 2 - 1) * 0.24f, -0.3f, 0.0f);
            glRotatef((i * 2 - 1) * 10.0f, 0.0f, 1.0f, 0.0f);
            instance.begin();
            instance.vertexUV(n9, n11, n13, n6, n8);
            instance.vertexUV(n10, n11, n13, n5, n8);
            instance.vertexUV(n10, n12, n13, n5, n7);
            instance.vertexUV(n9, n12, n13, n6, n7);
            instance.end();
            glPopMatrix();
        }
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glDisable(GL_BLEND);
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
