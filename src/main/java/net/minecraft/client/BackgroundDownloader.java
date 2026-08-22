// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.io.File;

public class BackgroundDownloader extends Thread
{
    public File workingDirectory;
    private Minecraft minecraft;
    private boolean stopped = false;
    
    public BackgroundDownloader(final File workDir, final Minecraft minecraft) {
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
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(resourceUrl.openStream());
            final NodeList nodeLst = doc.getElementsByTagName("Contents");

            for (int pass = 0; pass < 2; ++pass) {
                for (int s = 0; s < nodeLst.getLength(); ++s) {
                    final Node fstNode = nodeLst.item(s);
                    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                        final Element element = (Element)fstNode;
                        final String key = element.getElementsByTagName("Key").item(0).getChildNodes().item(0).getNodeValue();
                        final long size = Long.parseLong(element.getElementsByTagName("Size").item(0).getChildNodes().item(0).getNodeValue());
                        if (size > 0L) {
                            this.checkDownload(resourceUrl, key, size, pass);
                            if (this.stopped) {
                                return;
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            this.loadAll(this.workingDirectory, "");
            e.printStackTrace();
        }
    }
    
    public void forceReload() {
        this.loadAll(this.workingDirectory, "");
    }
    
    private void loadAll(final File dir, final String prefix) {
        final File[] files = dir.listFiles();

        for (int i = 0; i < files.length; ++i) {
            if (files[i].isDirectory()) {
                this.loadAll(files[i], prefix + files[i].getName() + "/");
            }
            else {
                try {
                    this.minecraft.fileDownloaded(prefix + files[i].getName(), files[i]);
                }
                catch (final Exception ex) {
                    System.out.println("Failed to add " + prefix + files[i].getName());
                }
            }
        }
    }
    
    private void checkDownload(final URL resourceUrl, final String name, final long size, final int pass) {
        try {
            int p = name.indexOf("/");
            final String category = name.substring(0, p);
            if (!category.equals("sound") && !category.equals("newsound")) {
                if (pass != 1) {
                    return;
                }
            } else if (pass != 0) {
                return;
            }

            final File output = new File(this.workingDirectory, name);
            if (!output.exists() || output.length() != size) {
                output.getParentFile().mkdirs();
                String urlName = name.replaceAll(" ", "%20");
                this.download(new URL(resourceUrl, urlName), output, size);
                if (this.stopped) {
                    return;
                }
            }

            this.minecraft.fileDownloaded(name, output);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private void download(final URL url, final File file, final long length) throws IOException {
        final byte[] buffer = new byte[4096];
        final DataInputStream dis = new DataInputStream(url.openStream());
        final DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
        int read = 0;

        while ((read = dis.read(buffer)) >= 0) {
            dos.write(buffer, 0, read);
            if (this.stopped) {
                return;
            }
        }

        dis.close();
        dos.close();
    }
    
    public void halt() {
        this.stopped = true;
    }
}
