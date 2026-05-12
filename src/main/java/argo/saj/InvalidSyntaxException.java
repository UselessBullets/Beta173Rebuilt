/*
 * Copyright 2010 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.saj;

/**
 * Thrown to indicate a given character stream is not valid JSON.
 */
public final class InvalidSyntaxException extends Exception {

    private final int column;
    private final int row;

    InvalidSyntaxException(final String s, final ThingWithPosition thingWithPosition) {
        super("At line " + thingWithPosition.getRow() + ", column " + thingWithPosition.getColumn() + ":  " + s);
        this.column = thingWithPosition.getColumn();
        this.row = thingWithPosition.getRow();
    }

    InvalidSyntaxException(final String s, final Throwable throwable, final ThingWithPosition thingWithPosition) {
        super("At line " + thingWithPosition.getRow() + ", column " + thingWithPosition.getColumn() + ":  " + s, throwable);
        this.column = thingWithPosition.getColumn();
        this.row = thingWithPosition.getRow();
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return row;
    }
}
