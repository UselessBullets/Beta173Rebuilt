package org.useless;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.InputImplementation;
import org.lwjgl.sdl.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

public class SDL3InputImplementation implements InputImplementation {
    private final SDL3Window window;

    private final byte[] mButtons = new byte[getButtonCount()];
    private final int[] mPosition = new int[3]; // x, y, mwheel

    private boolean grabbed = false;

    private static class EventEntry {
        public static final Queue<EventEntry> objectBuffer = new ArrayDeque<>(50);
        static { for (int i = 0; i < 50; i++) objectBuffer.add(new EventEntry()); }
        public static final Queue<EventEntry> ringBuffer = new ArrayDeque<>(50);

        private byte eventButton;
        private byte eventState;
        private int x;
        private int y;
        private int wheel;
        private long nano;

        public void set(byte eventButton, byte eventState, int x, int y, int wheel, long nano) {
            this.eventButton = eventButton;
            this.eventState = eventState;
            this.x = x;
            this.y = y;
            this.wheel = wheel;
            this.nano = nano;
        }
        private static void addEntry(ByteBuffer buffer, EventEntry entry) {
            buffer.put(entry.eventButton);
            buffer.put(entry.eventState);
            buffer.putInt(entry.x);
            buffer.putInt(entry.y);
            buffer.putInt(entry.wheel);
            buffer.putLong( entry.nano);
        }

        public static void putEvent(byte eventButton, byte eventState, int x, int y, int wheel, long nano) {
            EventEntry entry;
            if (ringBuffer.size() < 50) {
                entry = objectBuffer.remove();
            } else {
                entry = ringBuffer.remove();
            }
            entry.set(eventButton, eventState, x, y, wheel, nano);
            ringBuffer.add(entry);
        }
    }

    public SDL3InputImplementation(SDL3Window window) {
        this.window = window;
    }

    public void handleEvent(SDL_Event event) {
        switch (event.type()) {
            case SDLEvents.SDL_EVENT_MOUSE_MOTION -> {
                var m = event.motion();
                if (this.grabbed) { // Accumulate relative motions
                    mPosition[0] += (int) m.xrel();
                    mPosition[1] -= (int) m.yrel();
                } else {
                    mPosition[0] = (int)m.x();
                    mPosition[1] = (int)(window.getHeight() - m.y());
                }
            }
            case SDLEvents.SDL_EVENT_MOUSE_WHEEL -> {
                var m = event.wheel();
                mPosition[2] += m.integer_y();

                int x = (int)m.x();
                int y = (int)(window.getHeight() - m.y());
                EventEntry.putEvent((byte) -1, (byte) 0, x, y, mPosition[2], m.timestamp());
            }
            case SDLEvents.SDL_EVENT_MOUSE_BUTTON_DOWN, SDLEvents.SDL_EVENT_MOUSE_BUTTON_UP -> {
                var m = event.button();

                byte button = m.button();
                // SDL has middle mouse as button 2, and right mouse as button 3, while lwjgl2 had them swapped so manually swapping here to emulate old behavior
                if (button == SDLMouse.SDL_BUTTON_MIDDLE) {
                    button = 3;
                } else if (button == SDLMouse.SDL_BUTTON_RIGHT) {
                    button = 2;
                }
                button--; // LWJGL2 is index 0 (lmb being 0) while SDL3 is index 1 (lmb being 1) so need to shift button down by 1

                byte state = (byte) (m.down() ? 1 : 0);

                mButtons[button & 0xFF] = state;
                for (int i = 0; i < m.clicks(); i++) {
                    int x = (int)m.x();
                    int y = (int)(window.getHeight() - m.y());
                    EventEntry.putEvent(button, state, x, y, 0, m.timestamp());
                }
            }
        }
    }

    @Override
    public boolean hasWheel() {
        return true; // TODO figure out how to query
    }

    @Override
    public int getButtonCount() {
        return 256; // TODO figure out how to query
    }

    @Override
    public void createMouse() {

    }

    @Override
    public void destroyMouse() {

    }

    @Override
    public void pollMouse(IntBuffer coord_buffer, ByteBuffer buttons) {
        coord_buffer.put(0, mPosition);
        buttons.put(0, mButtons);

        if (grabbed) { // Clear relatives since last poll
            mPosition[0] = 0;
            mPosition[1] = 0;
        }
        mPosition[2] = 0; // Wheel always treated as relative so needs to reset each poll
    }

    @Override
    public void readMouse(ByteBuffer buffer) {
        while (!EventEntry.ringBuffer.isEmpty() && buffer.remaining() >= Mouse.EVENT_SIZE) {
            EventEntry eventEntry = EventEntry.ringBuffer.remove();
            EventEntry.addEntry(buffer, eventEntry);
            EventEntry.objectBuffer.add(eventEntry);
        }
    }

    @Override
    public void grabMouse(boolean grab) {
        SDLVideo.SDL_SetWindowMouseGrab(this.window.getRawSDLWindowHandle(), grab);
        SDLMouse.SDL_SetWindowRelativeMouseMode(this.window.getRawSDLWindowHandle(), grab);
        this.grabbed = grab;
        if (grab) { // Since xy pos become delta, sets deltas to 0 on grab
            mPosition[0] = 0;
            mPosition[1] = 0;
        }
    }

    @Override
    public void setCursorPosition(int x, int y) {
        SDLMouse.SDL_WarpMouseInWindow(this.window.getRawSDLWindowHandle(), x, y);
    }

    @Override
    public void createKeyboard() {

    }

    @Override
    public void destroyKeyboard() {

    }

    @Override
    public void pollKeyboard(ByteBuffer keyDownBuffer) {

    }

    @Override
    public void readKeyboard(ByteBuffer buffer) {

    }

}
