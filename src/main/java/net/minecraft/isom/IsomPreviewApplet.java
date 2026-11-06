package net.minecraft.isom;

import java.applet.Applet;
import java.awt.BorderLayout;

// Useless - Isom package was pulled straight from the Beta 1.9-pre6 jar as it had source for this package, thanks @genericpnpmonit0r for telling me this
public class IsomPreviewApplet extends Applet {
    private static final long serialVersionUID = 1L;

    private IsomPreview isomPreview = new IsomPreview();

    public IsomPreviewApplet() {
        this.setLayout(new BorderLayout());
        add(isomPreview, BorderLayout.CENTER);
    }

    public void start() {
        isomPreview.start();
    }

    public void stop() {
        isomPreview.stop();
    }
}
