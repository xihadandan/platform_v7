package com.wellsoft.pt.repository.support;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZip {
    final static int BUFFER = 2048;

    public static void depress(String inputZipFile, String outputDir) throws IOException {
        try {
            BufferedOutputStream dest = null;
            FileInputStream fis = new FileInputStream(inputZipFile);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                System.out.println("Extracting: " + entry);
                System.out.println(entry.getName());
                if (entry.isDirectory()) {
                    File file = new File(outputDir + File.separator + entry.getName());
                    file.mkdirs();
                    continue;
                }
                int count;
                byte data[] = new byte[BUFFER];

                // write the files to the disk
                File file = new File(outputDir + File.separator + entry.getName());
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                dest = new BufferedOutputStream(fos, BUFFER);
                while ((count = zis.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
            }
            zis.close();
        } catch (IOException e) {
            throw e;
        }
    }

    public static void main(String argv[]) throws IOException {

        String outputDir = "F:" + File.separator + "custom";
        String inputZipFile = "f:\\temp.zip";
        UnZip.depress(inputZipFile, outputDir);

    }
}
