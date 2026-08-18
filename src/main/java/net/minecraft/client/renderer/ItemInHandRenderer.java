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
            final int col = Item.items[item.id].getColor(item.getAuxValue());
            float r = br * ((col >> 16 & 0xFF) / 255.0f);
            float g = br * ((col >> 8 & 0xFF) / 255.0f);
            float b = br * ((col & 0xFF) / 255.0f);

            glColor4f(r, g, b, 1.0f);
        }
        else {
            glColor4f(br, br, br, 1.0f);
        }

        if (item != null && item.id == Item.map.id) {
            glPushMatrix();
            final float d = 0.8f;

            {
                final float swing = player.getAttackAnim(a);

                float swing1 = swing * Mth.PI;
                float swing2 = Mth.sqrt(swing) * Mth.PI;
                glTranslatef(-Mth.sin(swing2) * 0.4f, Mth.sin(Mth.sqrt(swing) * Mth.PI * 2.0f) * 0.2f, -Mth.sin(swing1) * 0.2f);
            }

            float tilt = 1.0f - xr / 45.0f + 0.1f;
            if (tilt < 0.0f) tilt = 0.0f;
            if (tilt > 1.0f) tilt = 1.0f;
            tilt = -Mth.cos(tilt * Mth.PI) * 0.5f + 0.5f;

            glTranslatef(0.0f, 0.0f * d - (1.0f - h) * 1.2f - tilt * 0.5f + 0.04f, -0.9f * d);

            glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            glRotatef(tilt * -85.0f, 0.0f, 0.0f, 1.0f);
            glEnable(GL_RESCALE_NORMAL);

            {
                glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadHttpTexture(this.mc.player.customTextureUrl, this.mc.player.getTexture()));
                for (int i = 0; i < 2; ++i) {
                    final int flip = i * 2 - 1;
                    glPushMatrix();

                    glTranslatef(-0.0f, -0.6f, 1.1f * flip);
                    glRotatef((float) (-45 * flip), 1.0f, 0.0f, 0.0f);
                    glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                    glRotatef(59.0f, 0.0f, 0.0f, 1.0f);
                    glRotatef((float) (-65 * flip), 0.0f, 1.0f, 0.0f);

                    final EntityRenderer<Player> er = EntityRenderDispatcher.instance.getRenderer(this.mc.player);
                    final PlayerRenderer playerRenderer = (PlayerRenderer) er;
                    final float ss = 1.0f;
                    glScalef(ss, ss, ss);

                    playerRenderer.renderHand();
                    glPopMatrix();
                }
            }

            {
                final float swing = player.getAttackAnim(a);
                final float swing3 = Mth.sin(swing * swing * Mth.PI);
                final float swing2 = Mth.sin(Mth.sqrt(swing) * Mth.PI);
                glRotatef(-swing3 * 20.0f, 0.0f, 1.0f, 0.0f);
                glRotatef(-swing2 * 20.0f, 0.0f, 0.0f, 1.0f);
                glRotatef(-swing2 * 80.0f, 1.0f, 0.0f, 0.0f);
            }

            final float ss = 0.38f;
            glScalef(ss, ss, ss);

            glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            glRotatef(180.0f, 0.0f, 0.0f, 1.0f);

            glTranslatef(-1.0f, -1.0f, 0.0f);

            final float s = 2 / 128.0f;
            glScalef(s, s, s);

            this.mc.textures.bind(this.mc.textures.loadTexture("/misc/mapbg.png"));
            final Tesselator t = Tesselator.instance;

            glNormal3f(0.0f, 0.0f, -1.0f);
            t.begin();
            final int vo = 7;
            t.vertexUV(0 - vo, 128 + vo, 0.0, 0.0, 1.0);
            t.vertexUV(128 + vo, 128 + vo, 0.0, 1.0, 1.0);
            t.vertexUV(128 + vo, 0 - vo, 0.0, 1.0, 0.0);
            t.vertexUV(0 - vo, 0 - vo, 0.0, 0.0, 0.0);
            t.end();

            this.minimap.render(this.mc.player, this.mc.textures, Item.map.getSavedData(item, this.mc.level));
            glPopMatrix();
        }
        else if (item != null) {
            glPushMatrix();
            final float d = 0.8f;

            {
                final float swing = player.getAttackAnim(a);

                float swing1 = Mth.sin(swing * Mth.PI);
                float swing2 = Mth.sin(Mth.sqrt(swing) * Mth.PI);
                glTranslatef(-swing2 * 0.4f, Mth.sin(Mth.sqrt(swing) * Mth.PI * 2.0f) * 0.2f, -swing1 * 0.2f);
                glTranslatef(0.7f * d, -0.65f * d - (1.0f - h) * 0.6f, -0.9f * d);
            }

            glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
            glEnable(GL_RESCALE_NORMAL);

            final float swing = player.getAttackAnim(a);
            final float swing3 = Mth.sin(swing * swing * Mth.PI);
            final float swing2 = Mth.sin(Mth.sqrt(swing) * Mth.PI);
            glRotatef(-swing3 * 20.0f, 0.0f, 1.0f, 0.0f);
            glRotatef(-swing2 * 20.0f, 0.0f, 0.0f, 1.0f);
            glRotatef(-swing2 * 80.0f, 1.0f, 0.0f, 0.0f);

            final float ss = 0.4f;
            glScalef(ss, ss, ss);

            if (item.getItem().isMirroredArt()) {
                glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            }

            this.renderItem(player, item);
            glPopMatrix();
        }
        else {
            glPushMatrix();
            final float d = 0.8f;

            {
                final float swing = player.getAttackAnim(a);

                float swing1 = Mth.sin(swing * Mth.PI);
                float swing2 = Mth.sin(Mth.sqrt(swing) * Mth.PI);
                glTranslatef(-swing2 * 0.3f, Mth.sin(Mth.sqrt(swing) * Mth.PI * 2.0f) * 0.4f, -swing1 * 0.4f);
            }

            glTranslatef(0.8f * d, -0.75f * d - (1.0f - h) * 0.6f, -0.9f * d);

            glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
            glEnable(GL_RESCALE_NORMAL);
            {
                final float swing = player.getAttackAnim(a);
                final float swing3 = Mth.sin(swing * swing * Mth.PI);
                final float swing2 = Mth.sin(Mth.sqrt(swing) * Mth.PI);
                glRotatef(swing2 * 70.0f, 0.0f, 1.0f, 0.0f);
                glRotatef(-swing3 * 20.0f, 0.0f, 0.0f, 1.0f);
            }

            glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadHttpTexture(this.mc.player.customTextureUrl, this.mc.player.getTexture()));
            glTranslatef(-1.0f, 3.6f, 3.5f);
            glRotatef(120.0f, 0.0f, 0.0f, 1.0f);
            glRotatef(180.0f + 20.0f, 1.0f, 0.0f, 0.0f);
            glRotatef(-90.0f - 45.0f, 0.0f, 1.0f, 0.0f);
            glScalef(1.5f / 24.0f * 16, 1.5f / 24.0f * 16, 1.5f / 24.0f * 16);
            glTranslatef(5.6f, 0.0f, 0.0f);

            final EntityRenderer<Player> er = EntityRenderDispatcher.instance.getRenderer(this.mc.player);
            final PlayerRenderer playerRenderer = (PlayerRenderer)er;
            final float ss = 1.0f;
            glScalef(ss, ss, ss);

            playerRenderer.renderHand();
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
            final int x = Mth.floor(this.mc.player.x);
            final int y = Mth.floor(this.mc.player.y);
            final int z = Mth.floor(this.mc.player.z);

            glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadTexture("/terrain.png"));
            int tile = this.mc.level.getTile(x, y, z);
            if (this.mc.level.isSolidBlockingTile(x, y, z)) {
                this.renderTex(a, Tile.tiles[tile].getTexture(2));
            }
            else {
                for (int i = 0; i < 8; ++i) {
                    final float xo = ((i >> 0) % 2 - 0.5f) * this.mc.player.bbWidth * 0.9f;
                    final float yo = ((i >> 1) % 2 - 0.5f) * this.mc.player.bbHeight * 0.2f;
                    final float zo = ((i >> 2) % 2 - 0.5f) * this.mc.player.bbWidth * 0.9f;
                    final int xt = Mth.floor(x + xo);
                    final int yt = Mth.floor(y + yo);
                    final int zt = Mth.floor(z + zo);
                    if (this.mc.level.isSolidBlockingTile(xt, yt, zt)) {
                        tile = this.mc.level.getTile(xt, yt, zt);
                    }
                }
            }

            if (Tile.tiles[tile] != null) this.renderTex(a, Tile.tiles[tile].getTexture(2));
        }

        if (this.mc.player.isUnderLiquid(Material.water)) {
            glBindTexture(GL_TEXTURE_2D, this.mc.textures.loadTexture("/misc/water.png"));
            this.renderWater(a);
        }
        glEnable(GL_ALPHA_TEST);
    }
    
    private void renderTex(final float a, final int tex) {
        final Tesselator t = Tesselator.instance;

        this.mc.player.getBrightness(a);
        final float br = 0.1f;
        glColor4f(br, br, br, 0.5f);

        glPushMatrix();
        final float x0 = -1.0f;
        final float x1 = +1.0f;
        final float y0 = -1.0f;
        final float y1 = +1.0f;
        final float z0 = -0.5f;

        final float r = 2 / 256.0f;
        final float u0 = tex % 16 / 256.0f - r;
        final float u1 = (tex % 16 + 15.99f) / 256.0f + r;
        final float v0 = tex / 16 / 256.0f - r;
        final float v1 = (tex / 16 + 15.99f) / 256.0f + r;

        t.begin();
        t.vertexUV(x0, y0, z0, u1, v1);
        t.vertexUV(x1, y0, z0, u0, v1);
        t.vertexUV(x1, y1, z0, u0, v0);
        t.vertexUV(x0, y1, z0, u1, v0);
        t.end();

        glPopMatrix();
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private void renderWater(final float a) {
        final Tesselator t = Tesselator.instance;

        final float br = this.mc.player.getBrightness(a);
        glColor4f(br, br, br, 0.5f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glPushMatrix();

        final float size = 4.0f;

        final float x0 = -1.0f;
        final float x1 = +1.0f;
        final float y0 = -1.0f;
        final float y1 = +1.0f;
        final float z0 = -0.5f;

        final float uo = -this.mc.player.yRot / 64.0f;
        final float vo = +this.mc.player.xRot / 64.0f;

        t.begin();
        t.vertexUV(x0, y0, z0, size + uo, size + vo);
        t.vertexUV(x1, y0, z0, 0.0f + uo, size + vo);
        t.vertexUV(x1, y1, z0, 0.0f + uo, 0.0f + vo);
        t.vertexUV(x0, y1, z0, size + uo, 0.0f + vo);
        t.end();
        glPopMatrix();

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glDisable(GL_BLEND);
    }
    
    private void renderFire(final float a) {
        final Tesselator t = Tesselator.instance;
        glColor4f(1.0f, 1.0f, 1.0f, 0.9f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        final float size = 1.0f;
        for (int i = 0; i < 2; ++i) {
            glPushMatrix();
            final int tex = Tile.fire.tex + i * 16;
            final int texX = (tex & 0xF) << 4;
            final int texY = tex & 0xF0;

            final float u0 = texX / 256.0f;
            final float u1 = (texX + 15.99f) / 256.0f;
            final float v0 = texY / 256.0f;
            final float v1 = (texY + 15.99f) / 256.0f;

            final float x0 = (0.0f - size) / 2.0f;
            final float x1 = x0 + size;
            final float y0 = 0.0f - size / 2.0f;
            final float y1 = y0 + size;
            final float z0 = -0.5f;
            glTranslatef(-(i * 2 - 1) * 0.24f, -0.3f, 0.0f);
            glRotatef((i * 2 - 1) * 10.0f, 0.0f, 1.0f, 0.0f);

            t.begin();
            t.vertexUV(x0, y0, z0, u1, v1);
            t.vertexUV(x1, y0, z0, u0, v1);
            t.vertexUV(x1, y1, z0, u0, v0);
            t.vertexUV(x0, y1, z0, u1, v0);
            t.end();
            glPopMatrix();
        }
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glDisable(GL_BLEND);
    }
    
    public void tick() {
        this.oHeight = this.height;

        final LocalPlayer player = this.mc.player;
        final ItemInstance nextTile = player.inventory.getSelected();

        boolean matches = this.lastSlot == player.inventory.selected && nextTile == this.selectedItem;
        if (this.selectedItem == null && nextTile == null) {
            matches = true;
        }
        if (nextTile != null && this.selectedItem != null && nextTile != this.selectedItem && nextTile.id == this.selectedItem.id && nextTile.getAuxValue() == this.selectedItem.getAuxValue()) {
            this.selectedItem = nextTile;
            matches = true;
        }

        final float max = 0.4f;
        float tHeight = (matches ? 1.0f : 0.0f);
        float dd = tHeight - this.height;
        if (dd < -max) dd = -max;
        if (dd > max) dd = max;

        this.height += dd;
        if (this.height < 0.1f) {
            this.selectedItem = nextTile;
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
