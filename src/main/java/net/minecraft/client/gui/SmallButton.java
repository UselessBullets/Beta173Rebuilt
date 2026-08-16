// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.client.Options.Option;

public class SmallButton extends Button
{
    private final Option option;
    
    public SmallButton(final int id, final int x, final int y, final String msg) {
        this(id, x, y, null, msg);
    }
    
    public SmallButton(final int id, final int x, final int y, final int w, final int h, final String msg) {
        super(id, x, y, w, h, msg);
        this.option = null;
    }
    
    public SmallButton(final int id, final int x, final int y, final Option item, final String msg) {
        super(id, x, y, 150, 20, msg);
        this.option = item;
    }
    
    public Option getOption() {
        return this.option;
    }
}
