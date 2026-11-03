package org.useless;

import org.lwjgl.opengl.InputImplementation;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class SDL3InputImplementation implements InputImplementation {
    public SDL3InputImplementation() {

    }
    @Override
    public boolean hasWheel() {
        return false;
    }

    @Override
    public int getButtonCount() {
        return 0;
    }

    @Override
    public void createMouse() {

    }

    @Override
    public void destroyMouse() {

    }

    @Override
    public void pollMouse(IntBuffer coord_buffer, ByteBuffer buttons) {

    }

    @Override
    public void readMouse(ByteBuffer buffer) {

    }

    @Override
    public void grabMouse(boolean grab) {

    }

    @Override
    public int getNativeCursorCapabilities() {
        return 0;
    }

    @Override
    public void setCursorPosition(int x, int y) {

    }

    @Override
    public void setNativeCursor(Object handle) {

    }

    @Override
    public int getMinCursorSize() {
        return 0;
    }

    @Override
    public int getMaxCursorSize() {
        return 0;
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

    @Override
    public Object createCursor(int width, int height, int xHotspot, int yHotspot, int numImages, IntBuffer images, IntBuffer delays) {
        return null;
    }

    @Override
    public void destroyCursor(Object cursor_handle) {

    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public boolean isInsideWindow() {
        return false;
    }
}
