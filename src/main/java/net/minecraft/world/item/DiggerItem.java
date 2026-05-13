// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.tile.Tile;

public class DiggerItem extends Item
{
    private Tile[] tiles;
    private float speed;
    private int attackDamage;
    protected Tier tier;
    
    protected DiggerItem(final int id, final int attackDamage, final Tier tier, final Tile[] tiles) {
        super(id);
        this.speed = 4.0f;
        this.tier = tier;
        this.tiles = tiles;
        this.maxStackSize = 1;
        this.setMaxDamage(tier.getUses());
        this.speed = tier.getSpeed();
        this.attackDamage = attackDamage + tier.getAttackDamageBonus();
    }
    
    @Override
    public float getDestroySpeed(final ItemInstance itemInstance, final Tile tile) {
        for (int i = 0; i < this.tiles.length; ++i) {
            if (this.tiles[i] == tile) {
                return this.speed;
            }
        }
        return 1.0f;
    }
    
    @Override
    public boolean hurtEnemy(final ItemInstance itemInstance, final Mob mob, final Mob attacker) {
        itemInstance.hurt(2, attacker);
        return true;
    }
    
    @Override
    public boolean mineBlock(final ItemInstance itemInstance, final int tile, final int x, final int y, final int z, final Mob owner) {
        itemInstance.hurt(1, owner);
        return true;
    }
    
    @Override
    public int getAttackDamage(final Entity entity) {
        return this.attackDamage;
    }
    
    @Override
    public boolean isHandEquipped() {
        return true;
    }
}
