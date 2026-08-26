// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.material;

public class MaterialColor
{
    public static final MaterialColor[] colors = new MaterialColor[16];
    public static final MaterialColor none = new MaterialColor(0, 0x000000);
    public static final MaterialColor grass = new MaterialColor(1, 0x7fb238);
    public static final MaterialColor sand = new MaterialColor(2, 0xf7e9a3);
    public static final MaterialColor cloth = new MaterialColor(3, 0xa7a7a7);
    public static final MaterialColor fire = new MaterialColor(4, 0xff0000);
    public static final MaterialColor ice = new MaterialColor(5, 0xa0a0ff);
    public static final MaterialColor metal = new MaterialColor(6, 0xa7a7a7);
    public static final MaterialColor plant = new MaterialColor(7, 0x007c00);
    public static final MaterialColor snow = new MaterialColor(8, 0xffffff);
    public static final MaterialColor clay = new MaterialColor(9, 0xa4a8b8);
    public static final MaterialColor dirt = new MaterialColor(10, 0xb76a2f);
    public static final MaterialColor stone = new MaterialColor(11, 0x707070);
    public static final MaterialColor water = new MaterialColor(12, 0x4040ff);
    public static final MaterialColor wood = new MaterialColor(13, 0x685332);
    public final int col;
    public final int id;
    
    private MaterialColor(final int id, final int col) {
        this.id = id;
        this.col = col;
        MaterialColor.colors[id] = this;
    }

}
