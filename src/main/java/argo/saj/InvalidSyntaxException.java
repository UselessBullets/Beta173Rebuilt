// 
// Decompiled by Procyon v0.6.0
// 

package argo.saj;

public final class InvalidSyntaxException extends Exception
{
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
}
