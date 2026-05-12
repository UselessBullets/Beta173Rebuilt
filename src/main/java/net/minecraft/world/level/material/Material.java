// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.material;

public class Material
{
    public static final Material air;
    public static final Material grass;
    public static final Material dirt;
    public static final Material wood;
    public static final Material stone;
    public static final Material metal;
    public static final Material water;
    public static final Material lava;
    public static final Material leaves;
    public static final Material replaceable_plant;
    public static final Material sponge;
    public static final Material cloth;
    public static final Material fire;
    public static final Material sand;
    public static final Material decoration;
    public static final Material glass;
    public static final Material explosive;
    public static final Material coral;
    public static final Material ice;
    public static final Material topSnow;
    public static final Material snow;
    public static final Material cactus;
    public static final Material clay;
    public static final Material vegetable;
    public static final Material portal;
    public static final Material cake;
    public static final Material web;
    public static final Material piston;
    private boolean flammable;
    private boolean replaceable;
    private boolean neverBuildable;
    public final MaterialColor color;
    private boolean isAlwaysDestroyable;
    private int pushReaction;
    
    public Material(final MaterialColor color) {
        this.isAlwaysDestroyable = true;
        this.color = color;
    }
    
    public boolean isLiquid() {
        return false;
    }
    
    public boolean isSolid() {
        return true;
    }
    
    public boolean blocksLight() {
        return true;
    }
    
    public boolean blocksMotion() {
        return true;
    }
    
    private Material neverBuildable() {
        this.neverBuildable = true;
        return this;
    }
    
    private Material notAlwaysDestroyable() {
        this.isAlwaysDestroyable = false;
        return this;
    }
    
    private Material flammable() {
        this.flammable = true;
        return this;
    }
    
    public boolean isFlammable() {
        return this.flammable;
    }
    
    public Material replaceable() {
        this.replaceable = true;
        return this;
    }
    
    public boolean isReplaceable() {
        return this.replaceable;
    }
    
    public boolean isSolidBlocking() {
        return !this.neverBuildable && this.blocksMotion();
    }
    
    public boolean isDestroyedByHand() {
        return this.isAlwaysDestroyable;
    }
    
    public int getPushReaction() {
        return this.pushReaction;
    }
    
    protected Material destroyOnPush() {
        this.pushReaction = 1;
        return this;
    }
    
    protected Material notPushable() {
        this.pushReaction = 2;
        return this;
    }
    
    static {
        air = new GasMaterial(MaterialColor.none);
        grass = new Material(MaterialColor.grass);
        dirt = new Material(MaterialColor.dirt);
        wood = new Material(MaterialColor.wood).flammable();
        stone = new Material(MaterialColor.stone).notAlwaysDestroyable();
        metal = new Material(MaterialColor.metal).notAlwaysDestroyable();
        water = new LiquidMaterial(MaterialColor.water).destroyOnPush();
        lava = new LiquidMaterial(MaterialColor.fire).destroyOnPush();
        leaves = new Material(MaterialColor.plant).flammable().neverBuildable().destroyOnPush();
        replaceable_plant = new DecorationMaterial(MaterialColor.plant).destroyOnPush();
        sponge = new Material(MaterialColor.cloth);
        cloth = new Material(MaterialColor.cloth).flammable();
        fire = new GasMaterial(MaterialColor.none).destroyOnPush();
        sand = new Material(MaterialColor.sand);
        decoration = new DecorationMaterial(MaterialColor.none).destroyOnPush();
        glass = new Material(MaterialColor.none).neverBuildable();
        explosive = new Material(MaterialColor.fire).flammable().neverBuildable();
        coral = new Material(MaterialColor.plant).destroyOnPush();
        ice = new Material(MaterialColor.ice).neverBuildable();
        topSnow = new DecorationMaterial(MaterialColor.snow).replaceable().neverBuildable().notAlwaysDestroyable().destroyOnPush();
        snow = new Material(MaterialColor.snow).notAlwaysDestroyable();
        cactus = new Material(MaterialColor.plant).neverBuildable().destroyOnPush();
        clay = new Material(MaterialColor.clay);
        vegetable = new Material(MaterialColor.plant).destroyOnPush();
        portal = new PortalMaterial(MaterialColor.none).notPushable();
        cake = new Material(MaterialColor.none).destroyOnPush();
        web = new Material(MaterialColor.cloth).notAlwaysDestroyable().destroyOnPush();
        piston = new Material(MaterialColor.stone).notPushable();
    }
}
