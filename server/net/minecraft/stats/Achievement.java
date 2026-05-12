// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

import net.minecraft.locale.language.I18n;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;

public class Achievement extends Stat
{
    public final int x;
    public final int y;
    public final Achievement requires;
    private final String desc;
    public final ItemInstance icon;
    private boolean isGolden;
    
    public Achievement(final int id, final String name, final int x, final int y, final Item icon, final Achievement requires) {
        this(id, name, x, y, new ItemInstance(icon), requires);
    }
    
    public Achievement(final int id, final String name, final int x, final int y, final Tile icon, final Achievement requires) {
        this(id, name, x, y, new ItemInstance(icon), requires);
    }
    
    public Achievement(final int id, final String name, final int x, final int y, final ItemInstance icon, final Achievement requires) {
        super(5242880 + id, I18n.get("achievement." + name));
        this.icon = icon;
        this.desc = I18n.get("achievement." + name + ".desc");
        this.x = x;
        this.y = y;
        if (x < Achievements.xMin) {
            Achievements.xMin = x;
        }
        if (y < Achievements.yMin) {
            Achievements.yMin = y;
        }
        if (x > Achievements.xMax) {
            Achievements.xMax = x;
        }
        if (y > Achievements.yMax) {
            Achievements.yMax = y;
        }
        this.requires = requires;
    }
    
    @Override
    public Achievement setAwardLocallyOnly() {
        this.awardLocallyOnly = true;
        return this;
    }
    
    public Achievement setGolden() {
        this.isGolden = true;
        return this;
    }
    
    @Override
    public Achievement postConstruct() {
        super.postConstruct();
        Achievements.achievements.add(this);
        return this;
    }
}
