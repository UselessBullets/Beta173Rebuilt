package org.useless;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.InputImplementation;
import org.lwjgl.sdl.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

import static org.lwjgl.sdl.SDLKeycode.*;

public class SDL3InputImplementation implements InputImplementation {
    private static class MouseEventEntry {
        public static final Queue<MouseEventEntry> objectBuffer = new ArrayDeque<>(50);
        static { for (int i = 0; i < 50; i++) objectBuffer.add(new MouseEventEntry()); }
        public static final Queue<MouseEventEntry> ringBuffer = new ArrayDeque<>(50);

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
        private static void addEntry(ByteBuffer buffer, MouseEventEntry entry) {
            buffer.put(entry.eventButton);
            buffer.put(entry.eventState);
            buffer.putInt(entry.x);
            buffer.putInt(entry.y);
            buffer.putInt(entry.wheel);
            buffer.putLong( entry.nano);
        }

        public static void putEvent(byte eventButton, byte eventState, int x, int y, int wheel, long nano) {
            MouseEventEntry entry;
            if (ringBuffer.size() < 50) {
                entry = objectBuffer.remove();
            } else {
                entry = ringBuffer.remove();
            }
            entry.set(eventButton, eventState, x, y, wheel, nano);
            ringBuffer.add(entry);
        }
    }
    private static class KeyboardEventEntry {
        public static final Queue<KeyboardEventEntry> objectBuffer = new ArrayDeque<>(50);
        static { for (int i = 0; i < 50; i++) objectBuffer.add(new KeyboardEventEntry()); }
        public static final Queue<KeyboardEventEntry> ringBuffer = new ArrayDeque<>(50);

        private int keycode;
        private byte state;
        private int charcode;
        private long nano;
        private byte repeat;

        public void set(int keycode, byte state, int charcode, long nano, byte repeat) {
            this.keycode = keycode;
            this.state = state;
            this.charcode = charcode;
            this.nano = nano;
            this.repeat = repeat;
        }

        private static void addEntry(ByteBuffer buffer, KeyboardEventEntry entry) {
            buffer.putInt(entry.keycode);
            buffer.put(entry.state);
            buffer.putInt(entry.charcode);
            buffer.putLong(entry.nano);
            buffer.put(entry.repeat);
        }

        public static KeyboardEventEntry putEvent(int keycode, byte state, int charcode, long nano, byte repeat) {
            KeyboardEventEntry entry;
            if (ringBuffer.size() < 50) {
                entry = objectBuffer.remove();
            } else {
                entry = ringBuffer.remove();
            }
            entry.set(keycode, state, charcode, nano, repeat);
            ringBuffer.add(entry);
            return entry;
        }
    }

    public static int SDLKeycodeToLWJGLKeycode(int sdlCode) {
        // Hand written, would've been an array but sdlCodes use a wide amount of integer space and would've made for a needlessly massive array
        return switch (sdlCode) {
            case SDLK_ESCAPE -> Keyboard.KEY_ESCAPE;
            case SDLK_1 -> Keyboard.KEY_1;
            case SDLK_2 -> Keyboard.KEY_2;
            case SDLK_3 -> Keyboard.KEY_3;
            case SDLK_4 -> Keyboard.KEY_4;
            case SDLK_5 -> Keyboard.KEY_5;
            case SDLK_6 -> Keyboard.KEY_6;
            case SDLK_7 -> Keyboard.KEY_7;
            case SDLK_8 -> Keyboard.KEY_8;
            case SDLK_9 -> Keyboard.KEY_9;
            case SDLK_0 -> Keyboard.KEY_0;
            case SDLK_MINUS -> Keyboard.KEY_MINUS;
            case SDLK_EQUALS -> Keyboard.KEY_EQUALS;
            case SDLK_BACKSPACE -> Keyboard.KEY_BACK;
            case SDLK_TAB -> Keyboard.KEY_TAB;
            case SDLK_Q -> Keyboard.KEY_Q;
            case SDLK_W -> Keyboard.KEY_W;
            case SDLK_E -> Keyboard.KEY_E;
            case SDLK_R -> Keyboard.KEY_R;
            case SDLK_T -> Keyboard.KEY_T;
            case SDLK_Y -> Keyboard.KEY_Y;
            case SDLK_U -> Keyboard.KEY_U;
            case SDLK_I -> Keyboard.KEY_I;
            case SDLK_O -> Keyboard.KEY_O;
            case SDLK_P -> Keyboard.KEY_P;
            case SDLK_LEFTBRACKET -> Keyboard.KEY_LBRACKET;
            case SDLK_RIGHTBRACKET -> Keyboard.KEY_RBRACKET;
            case SDLK_RETURN -> Keyboard.KEY_RETURN;
            case SDLK_LCTRL -> Keyboard.KEY_LCONTROL;
            case SDLK_A-> Keyboard.KEY_A;
            case SDLK_S-> Keyboard.KEY_S;
            case SDLK_D-> Keyboard.KEY_D;
            case SDLK_F-> Keyboard.KEY_F;
            case SDLK_G-> Keyboard.KEY_G;
            case SDLK_H-> Keyboard.KEY_H;
            case SDLK_J-> Keyboard.KEY_J;
            case SDLK_K-> Keyboard.KEY_K;
            case SDLK_L-> Keyboard.KEY_L;
            case SDLK_SEMICOLON -> Keyboard.KEY_SEMICOLON;
            case SDLK_APOSTROPHE -> Keyboard.KEY_APOSTROPHE;
            case SDLK_GRAVE -> Keyboard.KEY_GRAVE;
            case SDLK_LSHIFT -> Keyboard.KEY_LSHIFT;
            case SDLK_BACKSLASH -> Keyboard.KEY_BACKSLASH;
            case SDLK_Z -> Keyboard.KEY_Z;
            case SDLK_X -> Keyboard.KEY_X;
            case SDLK_C -> Keyboard.KEY_C;
            case SDLK_V -> Keyboard.KEY_V;
            case SDLK_B -> Keyboard.KEY_B;
            case SDLK_N -> Keyboard.KEY_N;
            case SDLK_M -> Keyboard.KEY_M;
            case SDLK_COMMA -> Keyboard.KEY_COMMA;
            case SDLK_PERIOD -> Keyboard.KEY_PERIOD;
            case SDLK_SLASH -> Keyboard.KEY_SLASH;
            case SDLK_RSHIFT -> Keyboard.KEY_RSHIFT;
            case SDLK_KP_MULTIPLY -> Keyboard.KEY_MULTIPLY;
            case SDLK_LALT -> Keyboard.KEY_LMENU;
            case SDLK_SPACE -> Keyboard.KEY_SPACE;
            case SDLK_CAPSLOCK -> Keyboard.KEY_CAPITAL;
            case SDLK_F1 -> Keyboard.KEY_F1;
            case SDLK_F2 -> Keyboard.KEY_F2;
            case SDLK_F3 -> Keyboard.KEY_F3;
            case SDLK_F4 -> Keyboard.KEY_F4;
            case SDLK_F5 -> Keyboard.KEY_F5;
            case SDLK_F6 -> Keyboard.KEY_F6;
            case SDLK_F7 -> Keyboard.KEY_F7;
            case SDLK_F8 -> Keyboard.KEY_F8;
            case SDLK_F9 -> Keyboard.KEY_F9;
            case SDLK_F10 -> Keyboard.KEY_F10;
            case SDLK_NUMLOCKCLEAR -> Keyboard.KEY_NUMLOCK;
            case SDLK_SCROLLLOCK -> Keyboard.KEY_SCROLL;
            case SDLK_KP_7 -> Keyboard.KEY_NUMPAD7;
            case SDLK_KP_8 -> Keyboard.KEY_NUMPAD8;
            case SDLK_KP_9 -> Keyboard.KEY_NUMPAD9;
            case SDLK_KP_MINUS -> Keyboard.KEY_SUBTRACT;
            case SDLK_KP_4 -> Keyboard.KEY_NUMPAD4;
            case SDLK_KP_5 -> Keyboard.KEY_NUMPAD5;
            case SDLK_KP_6 -> Keyboard.KEY_NUMPAD6;
            case SDLK_KP_PLUS -> Keyboard.KEY_ADD;
            case SDLK_KP_1 -> Keyboard.KEY_NUMPAD1;
            case SDLK_KP_2 -> Keyboard.KEY_NUMPAD2;
            case SDLK_KP_3 -> Keyboard.KEY_NUMPAD3;
            case SDLK_KP_0 -> Keyboard.KEY_NUMPAD0;
            case SDLK_KP_DECIMAL -> Keyboard.KEY_DECIMAL;
            case SDLK_F11 -> Keyboard.KEY_F11;
            case SDLK_F12 -> Keyboard.KEY_F12;
            case SDLK_F13 -> Keyboard.KEY_F13;
            case SDLK_F14 -> Keyboard.KEY_F14;
            case SDLK_F15 -> Keyboard.KEY_F15;
            case SDLK_F16 -> Keyboard.KEY_F16;
            case SDLK_F17 -> Keyboard.KEY_F17;
            case SDLK_F18 -> Keyboard.KEY_F18;
//            case SDLK_ -> Keyboard.KEY_KANA; TODO find SDL3 equivalent?
            case SDLK_F19 -> Keyboard.KEY_F19;
//            case SDLK_ -> Keyboard.KEY_CONVERT; TODO find SDL3 equivalent?
//            case SDLK_ -> Keyboard.KEY_NOCONVERT; TODO find SDL3 equivalent?
//            case SDLK_ -> Keyboard.KEY_YEN; TODO find SDL3 equivalent?
            case SDLK_KP_EQUALS -> Keyboard.KEY_NUMPADEQUALS;
//            case SDLK_ -> Keyboard.KEY_CIRCUMFLEX; TODO find SDL3 equivalent?
            case SDLK_AT -> Keyboard.KEY_AT;
            case SDLK_COLON -> Keyboard.KEY_COLON;
            case SDLK_UNDERSCORE -> Keyboard.KEY_UNDERLINE;
//            case SDLK_ -> Keyboard.KEY_KANJI; TODO find SDL3 equivalent?
            case SDLK_STOP -> Keyboard.KEY_STOP;
//            case SDLK_ -> Keyboard.KEY_AX; TODO find SDL3 equivalent?
//            case SDLK_ -> Keyboard.KEY_UNLABELED; TODO find SDL3 equivalent?
            case SDLK_KP_ENTER -> Keyboard.KEY_NUMPADENTER;
            case SDLK_RCTRL -> Keyboard.KEY_RCONTROL;
//            case SDLK -> Keyboard.KEY_SECTION; TODO find SDL3 equivalent?
            case SDLK_KP_COMMA -> Keyboard.KEY_NUMPADCOMMA;
            case SDLK_KP_DIVIDE -> Keyboard.KEY_DIVIDE;
            case SDLK_SYSREQ -> Keyboard.KEY_SYSRQ;
            case SDLK_RALT -> Keyboard.KEY_RMENU;
//            case SDLK_ -> Keyboard.KEY_FUNCTION; TODO find SDL3 equivalent?
            case SDLK_PAUSE -> Keyboard.KEY_PAUSE;
            case SDLK_HOME -> Keyboard.KEY_HOME;
            case SDLK_UP -> Keyboard.KEY_UP;
            case SDLK_PRIOR -> Keyboard.KEY_PRIOR;
            case SDLK_LEFT -> Keyboard.KEY_LEFT;
            case SDLK_RIGHT -> Keyboard.KEY_RIGHT;
            case SDLK_END -> Keyboard.KEY_END;
            case SDLK_DOWN -> Keyboard.KEY_DOWN;
            case SDLK_PAGEDOWN -> Keyboard.KEY_NEXT;
            case SDLK_INSERT -> Keyboard.KEY_INSERT;
            case SDLK_DELETE -> Keyboard.KEY_DELETE;
            case SDLK_CLEAR -> Keyboard.KEY_CLEAR;
//            case SDLK_ -> Keyboard.KEY_LMETA; TODO find SDL3 equivalent?
//            case SDLK_ -> Keyboard.KEY_RMETA; TODO find SDL3 equivalent?
            case SDLK_APPLICATION -> Keyboard.KEY_APPS;
            case SDLK_POWER -> Keyboard.KEY_POWER;
            case SDLK_SLEEP -> Keyboard.KEY_SLEEP;
            default -> Keyboard.KEY_NONE;
        };
    }

    private final SDL3Window window;

    private final byte[] mButtons = new byte[getButtonCount()];

    private float mPosX = 0;
    private float mPosDX = 0;
    private float mPosY = 0;
    private float mPosDY = 0;
    private float mPosDW = 0;

    private final byte[] kButtons = new byte[Keyboard.KEYBOARD_SIZE];

    private KeyboardEventEntry lastKeyboardEvent;
    private boolean grabbed = false;

    public SDL3InputImplementation(SDL3Window window) {
        this.window = window;
    }

    public void handleEvent(SDL_Event event) {
        switch (event.type()) {
            case SDLEvents.SDL_EVENT_MOUSE_MOTION -> {
                var m = event.motion();
                this.mPosDX += m.xrel();
                this.mPosDY -= m.yrel();
                this.mPosX = m.x();
                this.mPosY = window.getHeight() - m.y() - 1;
            }
            case SDLEvents.SDL_EVENT_MOUSE_WHEEL -> {
                var m = event.wheel();
                this.mPosDW += m.y();

                int x = (int)m.x();
                int y = (int)(window.getHeight() - m.y() - 1);
                MouseEventEntry.putEvent((byte) -1, (byte) 0, x, y, m.integer_y(), m.timestamp());
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
                int x = (int)m.x();
                int y = (int)(window.getHeight() - m.y() - 1);
                MouseEventEntry.putEvent(button, state, x, y, 0, m.timestamp());
            }
            case SDLEvents.SDL_EVENT_KEY_DOWN, SDLEvents.SDL_EVENT_KEY_UP -> {
                var k = event.key();

                int lwjglKey = SDLKeycodeToLWJGLKeycode(k.key());
                var state = (byte) (k.down() ? 1 : 0);
                kButtons[lwjglKey] = state;

                this.lastKeyboardEvent = KeyboardEventEntry.putEvent(lwjglKey, state, Keyboard.CHAR_NONE, k.timestamp(), (byte) (k.repeat() ? 1 : 0));
            }
            case SDLEvents.SDL_EVENT_TEXT_INPUT -> { // Key_UP/Down fires before text input so we're going to just modify the prior event to fix it's lack of char code
                var t = event.text();
                var s = t.textString();
                if (this.lastKeyboardEvent != null && s != null && !s.isEmpty()) {
                    this.lastKeyboardEvent.charcode = s.charAt(0);
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
        if (this.grabbed) {
            coord_buffer.put(0, (int)mPosDX);
            coord_buffer.put(1, (int)mPosDY);
        } else {
            coord_buffer.put(0, (int)mPosX);
            coord_buffer.put(1, (int)mPosY);
        }
        coord_buffer.put(2, (int)mPosDW);
        buttons.put(0, mButtons);

        mPosDX = 0;
        mPosDY = 0;
        mPosDW = 0;
    }

    @Override
    public void readMouse(ByteBuffer buffer) {
        while (!MouseEventEntry.ringBuffer.isEmpty() && buffer.remaining() >= Mouse.EVENT_SIZE) {
            MouseEventEntry mouseEventEntry = MouseEventEntry.ringBuffer.remove();
            MouseEventEntry.addEntry(buffer, mouseEventEntry);
            MouseEventEntry.objectBuffer.add(mouseEventEntry);
        }
    }

    @Override
    public void grabMouse(boolean grab) {
        SDLVideo.SDL_SetWindowMouseGrab(this.window.getRawSDLWindowHandle(), grab);
        SDLMouse.SDL_SetWindowRelativeMouseMode(this.window.getRawSDLWindowHandle(), grab);
        this.grabbed = grab;
    }

    @Override
    public void setCursorPosition(int x, int y) {
        SDLMouse.SDL_WarpMouseInWindow(this.window.getRawSDLWindowHandle(), x, y);
        mPosX = x;
        mPosY = y;
    }

    @Override
    public void createKeyboard() {
        SDLKeyboard.SDL_StartTextInput(this.window.getRawSDLWindowHandle());
    }

    @Override
    public void destroyKeyboard() {
        SDLKeyboard.SDL_StopTextInput(this.window.getRawSDLWindowHandle());
    }

    @Override
    public void pollKeyboard(ByteBuffer keyDownBuffer) {
        keyDownBuffer.put(0, kButtons);
    }

    @Override
    public void readKeyboard(ByteBuffer buffer) {
        while (!KeyboardEventEntry.ringBuffer.isEmpty() && buffer.remaining() >= Keyboard.EVENT_SIZE) {
            KeyboardEventEntry keyboardEventEntry = KeyboardEventEntry.ringBuffer.remove();
            KeyboardEventEntry.addEntry(buffer, keyboardEventEntry);
            KeyboardEventEntry.objectBuffer.add(keyboardEventEntry);
        }
    }

}
