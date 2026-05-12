// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound;

public class SoundSystemLogger
{
    public void message(final String string, final int integer) {
        String string2 = "";
        for (int i = 0; i < integer; ++i) {
            string2 += "    ";
        }
        System.out.println(string2 + string);
    }
    
    public void importantMessage(final String string, final int integer) {
        String string2 = "";
        for (int i = 0; i < integer; ++i) {
            string2 += "    ";
        }
        System.out.println(string2 + string);
    }
    
    public boolean errorCheck(final boolean boolean1, final String string2, final String string3, final int integer) {
        if (boolean1) {
            this.errorMessage(string2, string3, integer);
        }
        return boolean1;
    }
    
    public void errorMessage(final String string1, final String string2, final int integer) {
        String string3 = "";
        for (int i = 0; i < integer; ++i) {
            string3 += "    ";
        }
        final String string4 = string3 + "Error in class '" + string1 + "'";
        final String string5 = "    " + string3 + string2;
        System.out.println(string4);
        System.out.println(string5);
    }
    
    public void printStackTrace(final Exception exception, final int integer) {
        this.printExceptionMessage(exception, integer);
        this.importantMessage("STACK TRACE:", integer);
        if (exception == null) {
            return;
        }
        final StackTraceElement[] stackTrace = exception.getStackTrace();
        if (stackTrace == null) {
            return;
        }
        for (int i = 0; i < stackTrace.length; ++i) {
            final StackTraceElement stackTraceElement = stackTrace[i];
            if (stackTraceElement != null) {
                this.message(stackTraceElement.toString(), integer + 1);
            }
        }
    }
    
    public void printExceptionMessage(final Exception exception, final int integer) {
        this.importantMessage("ERROR MESSAGE:", integer);
        if (exception.getMessage() == null) {
            this.message("(none)", integer + 1);
        }
        else {
            this.message(exception.getMessage(), integer + 1);
        }
    }
}
