package com.mrkzs.rmpack.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by KINCAI
 * <p>
 * Desc TODO
 * <p>
 * Date 2020-04-09 15:55
 */
public class FileUtil {
    public void copy(File fromFile, File toFile) {
        if (fromFile.isFile()) {
            copyFile(fromFile, toFile);
        } else {
            if (!toFile.exists()) {
                toFile.mkdirs();
            }

            File[] files = fromFile.listFiles();
            for (File file : files) {
                copy(file, new File(toFile.getAbsolutePath(), file.getName()));
            }

        }
    }

    public static void copyFile(File oldFile, File newFile) {
        InputStream inStream = null;
        FileOutputStream fos = null;
        try {
            // int byteSum = 0;
            int byteRead = 0;

            if (!oldFile.exists()) {
                return;
            }
            inStream = new FileInputStream(oldFile);
            fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            while ((byteRead = inStream.read(buffer)) != -1) {
                // byteSum += byteRead;
                fos.write(buffer, 0, byteRead);
            }
            fos.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
                if (inStream != null)
                    inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
