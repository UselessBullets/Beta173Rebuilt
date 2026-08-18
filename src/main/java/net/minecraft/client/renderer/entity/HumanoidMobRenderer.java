// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import net.minecraft.client.renderer.TileRenderer;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.model.HumanoidModel;

import static org.lwjgl.opengl.GL11.*;

public class HumanoidMobRenderer<T extends Mob> extends MobRenderer<T>
{
    protected HumanoidModel humanoidModel;
    
    public HumanoidMobRenderer(final HumanoidModel humanoidModel, final float shadow) {
        super(humanoidModel, shadow);
        this.humanoidModel = humanoidModel;
    }
    
    @Override
    protected void additionalRendering(final Mob mob, final float a) {
        final ItemInstance item = mob.getCarriedItem();
        if (item != null) {
            glPushMatrix();

            this.humanoidModel.arm0.translateTo(1 / 16.0f);
            glTranslatef(-1 / 16.0f, 7 / 16.0f, 1 / 16.0f);

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

            this.entityRenderDispatcher.itemInHandRenderer.renderItem(mob, item);
            glPopMatrix();
        }
    }
}
