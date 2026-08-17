// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

public class SheepModel extends QuadrupedModel
{
    public SheepModel() {
        super(12, 0.0f);
        this.head = new Cube(0, 0);
        this.head.addBox(-3.0f, -4.0f, -6.0f, 6, 6, 8, 0.0f); // Head
        this.head.setPos(0.0f, 6.0f, -8.0f);

        this.body = new Cube(28, 8);
        this.body.addBox(-4.0f, -10.0f, -7.0f, 8, 16, 6, 0.0f); // Body
        this.body.setPos(0.0f, 5.0f, 2.0f);
    }
}
