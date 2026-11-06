package net.minecraft.midforddevutils;

import net.minecraft.client.gui.Button;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.Screen;
import org.lwjgl.input.Keyboard;


public class TimeScreen extends Screen {
    private EditBox time;


    @Override
    public void init(){
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.buttons.add(new Button(0,this.width / 2 -20, (this.height / 2),40,20, "Set!"));
        this.time = new EditBox(this, this.font, (this.width / 2)-25, 60, 50, 20,"0");


    }
    @Override
    public void render(final int xm, final int ym, final float a) {
        this.drawCenteredString(this.font,"You're seeing the TimeSetter!", this.width/2,(this.height/2)-20,0xFFFFFF);
        this.drawCenteredString(this.font,"Jump To Time", (this.width/2),40,0xFFFFFF);
        this.drawCenteredString(this.font,"Current Time:"+ this.minecraft.level.getTime(), (this.width/2),20,0xFFFFFF);
        this.time.render();
        super.render(xm,ym,a);

    }

    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
        if (eventKey == Keyboard.KEY_ESCAPE) {
            this.minecraft.setScreen(null);
            this.minecraft.grabMouse();
        }
        if (this.time.inFocus) this.time.keyPressed(eventCharacter, eventKey);


    }

    @Override
    protected void mouseClicked(final int x, final int y, final int buttonNum) {
        super.mouseClicked(x, y, buttonNum);
        this.time.mouseClicked(x, y, buttonNum);
    }

    @Override
    protected void buttonClicked(final Button button) {
        if (button.id == 0){

            this.minecraft.level.setTime(Integer.parseInt(this.time.getValue()));

        }
    }
}
