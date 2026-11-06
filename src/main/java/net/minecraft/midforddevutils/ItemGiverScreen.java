package net.minecraft.midforddevutils;

import net.minecraft.client.gui.Button;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.Screen;
import net.minecraft.world.item.ItemInstance;
import org.lwjgl.input.Keyboard;

public class ItemGiverScreen extends Screen {
private EditBox itemID;
private EditBox itemCount;
private EditBox meta;
private ItemInstance item;

    @Override
    public void init(){
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.buttons.add(new Button(0,this.width / 2 -20, (this.height / 2),40,20, "Give!"));
        this.itemID = new EditBox(this, this.font, (this.width / 2)-75, 60, 30, 20,"");
        this.itemCount = new EditBox(this, this.font, (this.width / 2)-15, 60, 30, 20,"1");
        this.meta = new EditBox(this, this.font, (this.width / 2)+45, 60, 30, 20,"0");
        this.itemID.setMaxLength(4);

    }
    @Override
    public void render(final int xm, final int ym, final float a) {
        this.drawCenteredString(font,"You're seeing the ItemGiver!", this.width/2,(this.height/2)-20,0xFFFFFF);
        this.drawCenteredString(font,"ID", (this.width/2)-60,40,0xFFFFFF);
        this.drawCenteredString(font,"Count", (this.width/2),40,0xFFFFFF);
        this.drawCenteredString(font,"Meta", (this.width/2)+60,40,0xFFFFFF);
        super.render(xm,ym,a);
        this.itemID.render();
        this.itemCount.render();
        this.meta.render();

    }

    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
        if (eventKey == Keyboard.KEY_ESCAPE) {
            this.minecraft.setScreen(null);
            this.minecraft.grabMouse();
        }
        if (this.itemID.inFocus) this.itemID.keyPressed(eventCharacter, eventKey);
        if (this.itemCount.inFocus) this.itemCount.keyPressed(eventCharacter, eventKey);
        if (this.meta.inFocus) this.meta.keyPressed(eventCharacter, eventKey);


    }

    @Override
    protected void mouseClicked(final int x, final int y, final int buttonNum) {
        super.mouseClicked(x, y, buttonNum);


        this.itemID.mouseClicked(x, y, buttonNum);
        this.itemCount.mouseClicked(x,y,buttonNum);
        this.meta.mouseClicked(x,y,buttonNum);
    }

    @Override
    protected void buttonClicked(final Button button) {
        if (button.id == 0){
            ItemInstance item = new ItemInstance(Integer.parseInt(itemID.getValue()),Integer.parseInt(itemCount.getValue()),Integer.parseInt(meta.getValue()));

            this.minecraft.player.inventory.add(item);

        }
    }
}

