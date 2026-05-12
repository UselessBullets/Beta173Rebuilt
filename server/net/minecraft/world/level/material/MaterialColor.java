// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.material;

public class MaterialColor
{
    public static final MaterialColor[] colors;
    public static final MaterialColor none;
    public static final MaterialColor grass;
    public static final MaterialColor sand;
    public static final MaterialColor cloth;
    public static final MaterialColor fire;
    public static final MaterialColor ice;
    public static final MaterialColor metal;
    public static final MaterialColor plant;
    public static final MaterialColor snow;
    public static final MaterialColor clay;
    public static final MaterialColor dirt;
    public static final MaterialColor stone;
    public static final MaterialColor water;
    public static final MaterialColor wood;
    public final int col;
    public final int id;
    
    private MaterialColor(final int id, final int col) {
        this.id = id;
        this.col = col;
        MaterialColor.colors[id] = this;
    }
    
    static {
        colors = new MaterialColor[16];
        none = new MaterialColor(0, 0);
        grass = new MaterialColor(1, 8368696);
        sand = new MaterialColor(2, 16247203);
        cloth = new MaterialColor(3, 10987431);
        fire = new MaterialColor(4, 16711680);
        ice = new MaterialColor(5, 10526975);
        metal = new MaterialColor(6, 10987431);
        plant = new MaterialColor(7, 31744);
        snow = new MaterialColor(8, 16777215);
        clay = new MaterialColor(9, 10791096);
        dirt = new MaterialColor(10, 12020271);
        stone = new MaterialColor(11, 7368816);
        water = new MaterialColor(12, 4210943);
        wood = new MaterialColor(13, 6837042);
    }
}
