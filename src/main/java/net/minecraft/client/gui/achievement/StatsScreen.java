// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.achievement;

import net.minecraft.client.gui.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.item.Item;
import net.minecraft.client.Lighting;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.Button;
import net.minecraft.locale.language.Language;
import net.minecraft.locale.language.I18n;
import net.minecraft.client.gui.ScrolledSelectionList;
import net.minecraft.stats.StatsCounter;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.gui.Screen;

public class StatsScreen extends Screen
{
    private static ItemRenderer itemRenderer;
    protected Screen lastScreen;
    protected String title;
    private StatsScreen_GeneralStatisticsList statsList;
    private StatsScreen_ItemStatisticsList itemStatsList;
    private StatsScreen_BlockStatisticsList blockStatsList;
    private StatsCounter stats;
    private ScrolledSelectionList activeList;
    
    public StatsScreen(final Screen lastScreen, final StatsCounter stats) {
        this.title = "Select world";
        this.activeList = null;
        this.lastScreen = lastScreen;
        this.stats = stats;
    }
    
    @Override
    public void init() {
        this.title = I18n.get("gui.stats");
        (this.statsList = new StatsScreen_GeneralStatisticsList(this)).init(this.buttons, 1, 1);
        (this.itemStatsList = new StatsScreen_ItemStatisticsList(this)).init(this.buttons, 1, 1);
        (this.blockStatsList = new StatsScreen_BlockStatisticsList(this)).init(this.buttons, 1, 1);
        this.activeList = this.statsList;
        this.postInit();
    }
    
    public void postInit() {
        final Language instance = Language.getInstance();
        this.buttons.add(new Button(0, this.width / 2 + 4, this.height - 28, 150, 20, instance.getElement("gui.done")));
        this.buttons.add(new Button(1, this.width / 2 - 154, this.height - 52, 100, 20, instance.getElement("stat.generalButton")));
        final Button button;
        this.buttons.add(button = new Button(2, this.width / 2 - 46, this.height - 52, 100, 20, instance.getElement("stat.blocksButton")));
        final Button button2;
        this.buttons.add(button2 = new Button(3, this.width / 2 + 62, this.height - 52, 100, 20, instance.getElement("stat.itemsButton")));
        if (this.blockStatsList.getNumberOfItems() == 0) {
            button.active = false;
        }
        if (this.itemStatsList.getNumberOfItems() == 0) {
            button2.active = false;
        }
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (!button.active) {
            return;
        }
        if (button.id == 0) {
            this.minecraft.setScreen(this.lastScreen);
        }
        else if (button.id == 1) {
            this.activeList = this.statsList;
        }
        else if (button.id == 3) {
            this.activeList = this.itemStatsList;
        }
        else if (button.id == 2) {
            this.activeList = this.blockStatsList;
        }
        else {
            this.activeList.buttonClicked(button);
        }
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.activeList.render(xm, ym, partialTick);
        this.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
        super.render(xm, ym, partialTick);
    }
    
    private void blitSlot(final int x, final int y, final int item) {
        this.blitSlotBg(x + 1, y + 1);
        GL11.glEnable(32826);
        GL11.glPushMatrix();
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        Lighting.turnOn();
        GL11.glPopMatrix();
        StatsScreen.itemRenderer.renderGuiItem(this.font, this.minecraft.textures, item, 0, Item.items[item].getIcon(0), x + 2, y + 2);
        Lighting.turnOff();
        GL11.glDisable(32826);
    }
    
    private void blitSlotBg(final int x, final int y) {
        this.blitSlotIcon(x, y, 0, 0);
    }
    
    private void blitSlotIcon(final int x, final int y, final int sx, final int sy) {
        final int loadTexture = this.minecraft.textures.loadTexture("/gui/slot.png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.textures.bind(loadTexture);
        final Tesselator instance = Tesselator.instance;
        instance.begin();
        instance.vertexUV(x + 0, y + 18, this.blitOffset, (sx + 0) * 0.0078125f, (sy + 18) * 0.0078125f);
        instance.vertexUV(x + 18, y + 18, this.blitOffset, (sx + 18) * 0.0078125f, (sy + 18) * 0.0078125f);
        instance.vertexUV(x + 18, y + 0, this.blitOffset, (sx + 18) * 0.0078125f, (sy + 0) * 0.0078125f);
        instance.vertexUV(x + 0, y + 0, this.blitOffset, (sx + 0) * 0.0078125f, (sy + 0) * 0.0078125f);
        instance.end();
    }
    
    static {
        StatsScreen.itemRenderer = new ItemRenderer();
    }
}
