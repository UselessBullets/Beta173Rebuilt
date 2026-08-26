// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.material;

public class Material
{
    public static final Material air = new GasMaterial(MaterialColor.none);
    public static final Material grass = new Material(MaterialColor.grass);
    public static final Material dirt = new Material(MaterialColor.dirt);
    public static final Material wood = new Material(MaterialColor.wood).flammable();
    public static final Material stone = new Material(MaterialColor.stone).notAlwaysDestroyable();
    public static final Material metal = new Material(MaterialColor.metal).notAlwaysDestroyable();
    public static final Material water = new LiquidMaterial(MaterialColor.water).destroyOnPush();
    public static final Material lava = new LiquidMaterial(MaterialColor.fire).destroyOnPush();
    public static final Material leaves = new Material(MaterialColor.plant).flammable().neverBuildable().destroyOnPush();
    public static final Material replaceable_plant = new DecorationMaterial(MaterialColor.plant).destroyOnPush();
    public static final Material sponge = new Material(MaterialColor.cloth);
    public static final Material cloth = new Material(MaterialColor.cloth).flammable();
    public static final Material fire = new GasMaterial(MaterialColor.none).destroyOnPush();
    public static final Material sand = new Material(MaterialColor.sand);
    public static final Material decoration = new DecorationMaterial(MaterialColor.none).destroyOnPush();
    public static final Material glass = new Material(MaterialColor.none).neverBuildable();
    public static final Material explosive = new Material(MaterialColor.fire).flammable().neverBuildable();
    public static final Material coral = new Material(MaterialColor.plant).destroyOnPush();
    public static final Material ice = new Material(MaterialColor.ice).neverBuildable();
    public static final Material topSnow = new DecorationMaterial(MaterialColor.snow).replaceable().neverBuildable().notAlwaysDestroyable().destroyOnPush();
    public static final Material snow = new Material(MaterialColor.snow).notAlwaysDestroyable();
    public static final Material cactus = new Material(MaterialColor.plant).neverBuildable().destroyOnPush();
    public static final Material clay = new Material(MaterialColor.clay);
    public static final Material vegetable = new Material(MaterialColor.plant).destroyOnPush();
    public static final Material portal = new PortalMaterial(MaterialColor.none).notPushable();
    public static final Material cake = new Material(MaterialColor.none).destroyOnPush();
    public static final Material web = new Material(MaterialColor.cloth).notAlwaysDestroyable().destroyOnPush();
    public static final Material piston = new Material(MaterialColor.stone).notPushable();

    public static final int PUSH_NORMAL = 0;
    public static final int PUSH_DESTROY = 1;
    public static final int PUSH_BLOCK = 2;		// not pushable
    private boolean flammable;
    private boolean replaceable;
    private boolean neverBuildable;
    public final MaterialColor color;
    private boolean isAlwaysDestroyable;
    private int pushReaction = PUSH_NORMAL;
    
    public Material(final MaterialColor color) {
        this.isAlwaysDestroyable = true;
        this.color = color;
    }
    
    public boolean isLiquid() {
        return false;
    }

    // Useless - In B1.2 and LCE leaks
    public boolean letsWaterThrough() {
        return !this.isLiquid() && !this.isSolid();
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
    
    public boolean isAlwaysDestroyable() {
        // these materials will always drop resources when destroyed, regardless
        // of player's equipment
        return this.isAlwaysDestroyable;
    }
    
    public int getPushReaction() {
        return this.pushReaction;
    }
    
    protected Material destroyOnPush() {
        this.pushReaction = PUSH_DESTROY;
        return this;
    }
    
    protected Material notPushable() {
        this.pushReaction = PUSH_BLOCK;
        return this;
    }

}
