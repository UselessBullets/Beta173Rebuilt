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

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;

/**
 * Project.java
 * <p/>
 * <p/>
 * Created 11-jan-2004
 *
 * @author Erik Duijs
 */
// Useless - Only copied relevant parts for b173
public class Project {

	private static final float[] IDENTITY_MATRIX =
		new float[] {
			1.0f, 0.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f,
			0.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f };

	private static final FloatBuffer matrix = BufferUtils.createFloatBuffer(16);

	/**
	 * Make matrix an identity matrix
	 */
	private static void __gluMakeIdentityf(FloatBuffer m) {
		int oldPos = m.position();
		m.put(IDENTITY_MATRIX);
		m.position(oldPos);
	}

	/**
	 * Method gluPerspective.
	 *
	 * @param fovy
	 * @param aspect
	 * @param zNear
	 * @param zFar
	 */
	public static void gluPerspective(float fovy, float aspect, float zNear, float zFar) {
		float sine, cotangent, deltaZ;
		float radians = fovy / 2 * GLU.PI / 180;

		deltaZ = zFar - zNear;
		sine = (float) Math.sin(radians);

		if ((deltaZ == 0) || (sine == 0) || (aspect == 0)) {
			return;
		}

		cotangent = (float) Math.cos(radians) / sine;

		__gluMakeIdentityf(matrix);

		matrix.put(0 * 4 + 0, cotangent / aspect);
		matrix.put(1 * 4 + 1, cotangent);
		matrix.put(2 * 4 + 2, - (zFar + zNear) / deltaZ);
		matrix.put(2 * 4 + 3, -1);
		matrix.put(3 * 4 + 2, -2 * zNear * zFar / deltaZ);
		matrix.put(3 * 4 + 3, 0);

		glMultMatrixf(matrix);
	}

}
