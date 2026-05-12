// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

public class SignModel
{
    public Cube cube;
    public Cube cube2;
    
    public SignModel() {
        (this.cube = new Cube(0, 0)).addBox(-12.0f, -14.0f, -1.0f, 24, 12, 2, 0.0f);
        (this.cube2 = new Cube(0, 14)).addBox(-1.0f, -2.0f, -1.0f, 2, 14, 2, 0.0f);
    }
    
    public void render() {
        this.cube.render(0.0625f);
        this.cube2.render(0.0625f);
    }
}
