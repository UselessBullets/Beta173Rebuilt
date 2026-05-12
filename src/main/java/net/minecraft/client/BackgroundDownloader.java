// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.io.File;

public class BackgroundDownloader extends Thread
{
    public File workingDirectory;
    private Minecraft minecraft;
    private boolean stopped;
    
    public BackgroundDownloader(final File workDir, final Minecraft minecraft) {
        this.stopped = false;
        this.minecraft = minecraft;
        this.setName("Resource download thread");
        this.setDaemon(true);
        this.workingDirectory = new File(workDir, "resources/");
        if (!this.workingDirectory.exists() && !this.workingDirectory.mkdirs()) {
            throw new RuntimeException("The working directory could not be created: " + this.workingDirectory);
        }
    }
    
    @Override
    public void run() {
        try {
            final URL resourceUrl = new URL("http://s3.amazonaws.com/MinecraftResources/");
            final NodeList elementsByTagName = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(resourceUrl.openStream()).getElementsByTagName("Contents");
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < elementsByTagName.getLength(); ++j) {
                    final Node item = elementsByTagName.item(j);
                    if (item.getNodeType() == 1) {
                        final Element element = (Element)item;
                        final String nodeValue = element.getElementsByTagName("Key").item(0).getChildNodes().item(0).getNodeValue();
                        final long long1 = Long.parseLong(element.getElementsByTagName("Size").item(0).getChildNodes().item(0).getNodeValue());
                        if (long1 > 0L) {
                            this.checkDownload(resourceUrl, nodeValue, long1, i);
                            if (this.stopped) {
                                return;
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.loadAll(this.workingDirectory, "");
            ex.printStackTrace();
        }
    }
    
    public void forceReload() {
        this.loadAll(this.workingDirectory, "");
    }
    
    private void loadAll(final File dir, final String prefix) {
        final File[] listFiles = dir.listFiles();
        for (int i = 0; i < listFiles.length; ++i) {
            if (listFiles[i].isDirectory()) {
                this.loadAll(listFiles[i], prefix + listFiles[i].getName() + "/");
            }
            else {
                try {
                    this.minecraft.fileDownloaded(prefix + listFiles[i].getName(), listFiles[i]);
                }
                catch (final Exception ex) {
                    System.out.println("Failed to add " + prefix + listFiles[i].getName());
                }
            }
        }
    }
    
    private void checkDownload(final URL resourceUrl, final String name, final long size, final int pass) {
        try {
            final String substring = name.substring(0, name.indexOf("/"));
            if (substring.equals("sound") || substring.equals("newsound")) {
                if (pass != 0) {
                    return;
                }
            }
            else if (pass != 1) {
                return;
            }
            final File file = new File(this.workingDirectory, name);
            if (!file.exists() || file.length() != size) {
                file.getParentFile().mkdirs();
                this.download(new URL(resourceUrl, name.replaceAll(" ", "%20")), file, size);
                if (this.stopped) {
                    return;
                }
            }
            this.minecraft.fileDownloaded(name, file);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void download(final URL url, final File file, final long length) {
        final byte[] array = new byte[4096];
        final DataInputStream dataInputStream = new DataInputStream(url.openStream());
        final DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
        int read;
        while ((read = dataInputStream.read(array)) >= 0) {
            dataOutputStream.write(array, 0, read);
            if (this.stopped) {
                return;
            }
        }
        dataInputStream.close();
        dataOutputStream.close();
    }
    
    public void halt() {
        this.stopped = true;
    }
}
