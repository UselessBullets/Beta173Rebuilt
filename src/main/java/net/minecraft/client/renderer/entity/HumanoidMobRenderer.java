// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import net.minecraft.client.renderer.TileRenderer;
import net.minecraft.world.level.tile.Tile;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.model.HumanoidModel;

public class HumanoidMobRenderer<T extends Mob> extends MobRenderer<T>
{
    protected HumanoidModel humanoidModel;
    
    public HumanoidMobRenderer(final HumanoidModel humanoidModel, final float shadow) {
        super(humanoidModel, shadow);
        this.humanoidModel = humanoidModel;
    }
    
    @Override
    protected void additionalRendering(final Mob mob, final float a) {
        final ItemInstance carriedItem = mob.getCarriedItem();
        if (carriedItem != null) {
            GL11.glPushMatrix();
            this.humanoidModel.arm0.translateTo(0.0625f);
            GL11.glTranslatef(-0.0625f, 0.4375f, 0.0625f);
            if (carriedItem.id < 256 && TileRenderer.canRender(Tile.tiles[carriedItem.id].getRenderShape())) {
                final float n = 0.5f;
                GL11.glTranslatef(0.0f, 0.1875f, -0.3125f);
                final float n2 = n * 0.75f;
                GL11.glRotatef(20.0f, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
                GL11.glScalef(n2, -n2, n2);
            }
            else if (Item.items[carriedItem.id].isHandEquipped()) {
                final float n3 = 0.625f;
                GL11.glTranslatef(0.0f, 0.1875f, 0.0f);
                GL11.glScalef(n3, -n3, n3);
                GL11.glRotatef(-100.0f, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
            }
            else {
                final float n4 = 0.375f;
                GL11.glTranslatef(0.25f, 0.1875f, -0.1875f);
                GL11.glScalef(n4, n4, n4);
                GL11.glRotatef(60.0f, 0.0f, 0.0f, 1.0f);
                GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(20.0f, 0.0f, 0.0f, 1.0f);
            }
            this.entityRenderDispatcher.itemInHandRenderer.renderItem(mob, carriedItem);
            GL11.glPopMatrix();
        }
    }
}
