package org.useless;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.sdl.*;
import org.lwjgl.system.Callback;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.sdl.SDLError.*;
import static org.lwjgl.sdl.SDLInit.*;
import static org.lwjgl.sdl.SDLProperties.*;
import static org.lwjgl.sdl.SDLVideo.*;

public class SDL3Window {
    private final long SDL_Window_Handle;
    private final long SDL_GLContext_Handle;
    private final Callback debugCallback;
    private EventHandler eventHandler;
    private SDL3InputImplementation inputImplementation;
    public SDL3Window(CharSequence title, int width, int height, EventHandler eventHandler) throws SDLException {
        this.eventHandler = eventHandler;
        inputImplementation = new SDL3InputImplementation(this);

        if (!SDL_Init(SDL_INIT_VIDEO)) throw new SDLException("Failed to initialized SDL: " + SDL_GetError());

        int props = SDL_CreateProperties();
        checkSdlError(SDL_SetNumberProperty(props, SDL_PROP_WINDOW_CREATE_X_NUMBER, SDL_WINDOWPOS_CENTERED));
        checkSdlError(SDL_SetNumberProperty(props, SDL_PROP_WINDOW_CREATE_Y_NUMBER, SDL_WINDOWPOS_CENTERED));
        checkSdlError(SDL_SetNumberProperty(props, SDL_PROP_WINDOW_CREATE_WIDTH_NUMBER, width));
        checkSdlError(SDL_SetNumberProperty(props, SDL_PROP_WINDOW_CREATE_HEIGHT_NUMBER, height));
        checkSdlError(SDL_SetStringProperty(props, SDL_PROP_WINDOW_CREATE_TITLE_STRING, title));
        checkSdlError(SDL_SetBooleanProperty(props, SDL_PROP_WINDOW_CREATE_OPENGL_BOOLEAN, true));
        checkSdlError(SDL_SetBooleanProperty(props, SDL_PROP_WINDOW_CREATE_HIDDEN_BOOLEAN, true));
        checkSdlError(SDL_SetBooleanProperty(props, SDL_PROP_WINDOW_CREATE_RESIZABLE_BOOLEAN, true));

        checkSdlError(SDL_GL_SetAttribute(SDL_GL_CONTEXT_FLAGS, SDL_GL_CONTEXT_DEBUG_FLAG));
        checkSdlError(SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION, 2));
        checkSdlError(SDL_GL_SetAttribute(SDL_GL_CONTEXT_MINOR_VERSION, 1));
        checkSdlError(SDL_GL_SetAttribute(SDL_GL_DOUBLEBUFFER, 1));
        checkSdlError(SDL_GL_SetAttribute(SDL_GL_DEPTH_SIZE, 24));
        checkSdlError(SDL_GL_SetAttribute(SDL_GL_STENCIL_SIZE, 8));

        SDL_Window_Handle = SDL_CreateWindowWithProperties(props);
        if (SDL_Window_Handle == MemoryUtil.NULL) throw new SDLException("Could not create window: " + SDL_GetError());
        SDL_DestroyProperties(props);

        SDL_SetWindowMinimumSize(SDL_Window_Handle, width, height);

        SDL_GLContext_Handle = checkSdlError(SDL_GL_CreateContext(SDL_Window_Handle));

        checkSdlError(SDL_GL_LoadLibrary((ByteBuffer) null));
        Configuration.OPENGL_EXPLICIT_INIT.set(true);

        GL.create(SDLVideo::SDL_GL_GetProcAddress);
        GL.createCapabilities(null);

        debugCallback = GLUtil.setupDebugMessageCallback();

        checkSdlError(SDL_GL_SetSwapInterval(1));
        checkSdlError(SDL_ShowWindow(SDL_Window_Handle));
    }

    public SDL3InputImplementation getInputImplementation() {
        return inputImplementation;
    }

    public long getRawSDLWindowHandle() {
        return SDL_Window_Handle;
    }

    public long getRawOpenGLContextHandle() {
        return SDL_GLContext_Handle;
    }

    private IntBuffer buf1 = BufferUtils.createIntBuffer(1);
    private IntBuffer buf2 = BufferUtils.createIntBuffer(1);

    /**
     * @return Width of the window, in pixels
     */
    public int getWidth() {
        checkSdlError(SDL_GetWindowSizeInPixels(SDL_Window_Handle, buf1, buf2));
        return buf1.get(0);
    }

    /**
     * @return Height of the window, in pixels
     */
    public int getHeight() {
        checkSdlError(SDL_GetWindowSizeInPixels(SDL_Window_Handle, buf1, buf2));
        return buf2.get(0);
    }

    public int getMonitorWidth() {
        var display = SDL_GetDisplayForWindow(SDL_Window_Handle);
        var mode = SDL_GetCurrentDisplayMode(display);
        return mode.w();
    }

    public int getMonitorHeight() {
        var display = SDL_GetDisplayForWindow(SDL_Window_Handle);
        var mode = SDL_GetCurrentDisplayMode(display);
        return mode.h();
    }

    public void setTitle(CharSequence title) {
        checkSdlError(SDLVideo.SDL_SetWindowTitle(SDL_Window_Handle, title));
    }

    public void setFullscreen(boolean fullscreen) {
        checkSdlError(SDL_SetWindowFullscreen(SDL_Window_Handle, fullscreen));
    }

    public void poll() {
        SDL_Event event = SDL_Event.calloc();
        while (SDLEvents.SDL_PollEvent(event)) {
            this.eventHandler.handleEvent(event);
            this.inputImplementation.handleEvent(event);
        }
        event.free();
        Keyboard.poll();
        Mouse.poll();
    }

    public void swapBuffers() {
        checkSdlError(SDL_GL_SwapWindow(SDL_Window_Handle));
    }

    public void update() {
        swapBuffers();
        poll();
    }

    public void quit() {
        GL.destroy();
        debugCallback.free();

        SDL_GL_DestroyContext(SDL_GLContext_Handle);
        SDL_DestroyWindow(SDL_Window_Handle);
        SDL_Quit();
    }

    private static void checkSdlError(boolean success) {
        if (!success) throw new SDLRuntimeException("SDL error encountered: " + SDL_GetError());
    }

    private static long checkSdlError(long resultPointer) {
        if (resultPointer == MemoryUtil.NULL) throw new SDLRuntimeException("SDL error encountered: " + SDL_GetError());
        return resultPointer;
    }

    public static class SDLException extends Exception {
        public SDLException(String s) { super(s); }
    }

    public static class SDLRuntimeException extends RuntimeException {
        public SDLRuntimeException(String s) { super(s); }
    }

    public static interface EventHandler {
        void handleEvent(SDL_Event event);
    }
}
