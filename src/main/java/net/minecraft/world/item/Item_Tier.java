// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

public enum Item_Tier
{
    WOOD(0, 59, 2.0f, 0), 
    STONE(1, 131, 4.0f, 1), 
    IRON(2, 250, 6.0f, 2), 
    EMERALD(3, 1561, 8.0f, 3), 
    GOLD(0, 32, 12.0f, 0);
    
    private final int level;
    private final int uses;
    private final float speed;
    private final int damage;
    
    private Item_Tier(final int level, final int uses, final float speed, final int damage) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
    }
    
    public int getUses() {
        return this.uses;
    }
    
    public float getSpeed() {
        return this.speed;
    }
    
    public int getAttackDamageBonus() {
        return this.damage;
    }
    
    public int getLevel() {
        return this.level;
    }
}
