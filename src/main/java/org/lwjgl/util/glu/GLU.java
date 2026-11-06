/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.lwjgl.util.glu;

import static org.lwjgl.opengl.ARBImaging.GL_TABLE_TOO_LARGE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_INVALID_OPERATION;
import static org.lwjgl.opengl.GL11.GL_OUT_OF_MEMORY;
import static org.lwjgl.opengl.GL11.GL_STACK_OVERFLOW;
import static org.lwjgl.opengl.GL11.GL_STACK_UNDERFLOW;
import static org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION;


/**
 * GLU.java
 *
 *
 * Created 23-dec-2003
 * @author Erik Duijs
 */
// Useless - Only copied relevant parts for b173
public class GLU {
    static final float PI = (float)Math.PI;

    /* Errors: (return value 0 = no error) */
    public static final int GLU_INVALID_ENUM        = 100900;
    public static final int GLU_INVALID_VALUE       = 100901;
    public static final int GLU_OUT_OF_MEMORY       = 100902;

    /**
     * Method gluPerspective
     * @param fovy
     * @param aspect
     * @param zNear
     * @param zFar
     */
    public static void gluPerspective(
            float fovy,
            float aspect,
            float zNear,
            float zFar) {

        Project.gluPerspective(fovy, aspect, zNear, zFar);
    }

    public static String gluErrorString(int error_code) {
        return switch (error_code) {
            case GLU_INVALID_ENUM -> "Invalid enum (glu)";
            case GLU_INVALID_VALUE -> "Invalid value (glu)";
            case GLU_OUT_OF_MEMORY -> "Out of memory (glu)";
            case GL_NO_ERROR -> "No error";
            case GL_INVALID_ENUM -> "Invalid enum";
            case GL_INVALID_VALUE -> "Invalid value";
            case GL_INVALID_OPERATION -> "Invalid operation";
            case GL_STACK_OVERFLOW -> "Stack overflow";
            case GL_STACK_UNDERFLOW -> "Stack underflow";
            case GL_OUT_OF_MEMORY -> "Out of memory";
            case GL_TABLE_TOO_LARGE -> "Table too large";
            case GL_INVALID_FRAMEBUFFER_OPERATION -> "Invalid framebuffer operation";
            default -> null;
        };
    };

}