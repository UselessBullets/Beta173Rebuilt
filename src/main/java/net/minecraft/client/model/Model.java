// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import net.minecraft.world.entity.Mob;

public abstract class Model
{
    public float attackTime;
    public boolean riding = false;
    
    public void render(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
    }
    
    public void setupAnim(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
    }
    
    public void prepareMobModel(final Mob mob, final float time, final float r, final float a) {
    }
}
