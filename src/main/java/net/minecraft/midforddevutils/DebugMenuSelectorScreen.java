package net.minecraft.midforddevutils;
import net.minecraft.client.gui.Button;
import net.minecraft.client.gui.Screen;


import org.lwjgl.input.Keyboard;

public class DebugMenuSelectorScreen extends Screen {
private final int  BUTTON_ITEMGIVER = 0;
private final int BUTTON_TIMESETTER = 1;

    @Override
    public void init(){
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.buttons.add(new Button(this.BUTTON_ITEMGIVER,(this.width / 2) -30, (this.height / 2),60,20, "ItemGiver"));
        this.buttons.add(new Button(this.BUTTON_TIMESETTER,(this.width / 2)-110, (this.height / 2),60,20, "TimeSetter"));

    }
    @Override
    public void render(final int xm, final int ym, final float a) {
        this.drawCenteredString(this.font,"Welcome to the Debug Menu!", this.width/2,(this.height/2)-20,0xFFFFFF);
        super.render(xm,ym,a);


    }

    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
        if (eventKey == Keyboard.KEY_ESCAPE) {
            this.minecraft.setScreen(null);
            this.minecraft.grabMouse();
        }

    }

    @Override
    protected void mouseClicked(final int x, final int y, final int buttonNum) {
        super.mouseClicked(x, y, buttonNum);


    }

    @Override
    protected void buttonClicked(final Button button) {
        if (button.id == this.BUTTON_ITEMGIVER){
            this.minecraft.setScreen(null);
            this.minecraft.setScreen(new ItemGiverScreen());
        }
        if (button.id == this.BUTTON_TIMESETTER){
            this.minecraft.setScreen(null);
            this.minecraft.setScreen(new TimeScreen());
        }

    }
}