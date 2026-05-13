// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.tile.Tile;

public class WeaponItem extends Item
{
    private int damage;
    
    public WeaponItem(final int id, final Tier tier) {
        super(id);
        this.maxStackSize = 1;
        this.setMaxDamage(tier.getUses());
        this.damage = 4 + tier.getAttackDamageBonus() * 2;
    }
    
    @Override
    public float getDestroySpeed(final ItemInstance itemInstance, final Tile tile) {
        if (tile.id == Tile.web.id) {
            return 15.0f;
        }
        return 1.5f;
    }
    
    @Override
    public boolean hurtEnemy(final ItemInstance itemInstance, final Mob mob, final Mob attacker) {
        itemInstance.hurt(1, attacker);
        return true;
    }
    
    @Override
    public boolean mineBlock(final ItemInstance itemInstance, final int tile, final int x, final int y, final int z, final Mob owner) {
        itemInstance.hurt(2, owner);
        return true;
    }
    
    @Override
    public int getAttackDamage(final Entity entity) {
        return this.damage;
    }
    
    @Override
    public boolean isHandEquipped() {
        return true;
    }
    
    @Override
    public boolean canDestroySpecial(final Tile tile) {
        return tile.id == Tile.web.id;
    }
}
