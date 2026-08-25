package net.minecraft.locale;

// Useless - class in b1.2 leaks
public interface Descriptive<T> {
    T setDescriptionId(String id);

    String getDescriptionId();
}
