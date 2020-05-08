package com.mrkzs.rmpack.utils

import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.model.FileHeader

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
/**
 * Created by KINCAI
 * <p>
 * Desc TODO
 * <p>
 * Date 2020-04-09 15:55
 */
class FileUtil {
    static void copy(File fromFile, File toFile) {
        if (fromFile.isFile()) {
            copyFile(fromFile, toFile)
        } else {
            if (!toFile.exists()) {
                toFile.mkdirs()
            }

            File[] files = fromFile.listFiles()
            for (File file : files) {
                copy(file, new File(toFile.getAbsolutePath(), file.getName()))
            }

        }
    }

    static void copyFile(File oldFile, File newFile) {
        InputStream inStream = null
        FileOutputStream fos = null
        try {
            // int byteSum = 0
            int byteRead = 0

            if (!oldFile.exists()) {
                return
            }
            inStream = new FileInputStream(oldFile)
            fos = new FileOutputStream(newFile)
            byte[] buffer = new byte[1024]
            while ((byteRead = inStream.read(buffer)) != -1) {
                // byteSum += byteRead
                fos.write(buffer, 0, byteRead)
            }
            fos.flush()

        } catch (Exception e) {
            e.printStackTrace()
        } finally {
            try {
                if (fos != null)
                    fos.close()
                if (inStream != null)
                    inStream.close()
            } catch (IOException e) {
                e.printStackTrace()
            }
        }
    }


    def static List<ZipInfo> getNeedZipFile(File zip, filter) {
        List<ZipInfo> zips = new ArrayList<>()
        ZipFile zipfile = null
        try {
            zipfile = new ZipFile(zip)
            Enumeration<?> entries = zipfile.entries()
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement())
                String entryName = entry.getName();
                if (filter && filter.call(entryName)) {
                    println "getNeedZipFile file name:$entryName"
                    InputStream inputStream = zipfile.getInputStream(entry)
                    InputStream newIs = null
                    if (inputStream != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream()
                        byte[] buffer = new byte[1024]
                        int len
                        while ((len = inputStream.read(buffer)) > -1) {
                            baos.write(buffer, 0, len)
                        }
                        baos.flush()
                        newIs = new ByteArrayInputStream(baos.toByteArray())
                        def info = new ZipInfo()
                        info.entryName = entryName
                        info.entry = newIs
                        zips.add(info)
                    }
                }
            }
            return zips
        } catch (IOException e) {
            e.printStackTrace()
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close()
                } catch (IOException e) {
                    e.printStackTrace()
                }
            }
        }

        return null
    }

    static void writeBytesToFile(InputStream is, File file) {
        FileOutputStream fos = null
        try {
            byte[] data = new byte[1024]
            int index
            fos = new FileOutputStream(file)
            while ((index = is.read(data)) != -1) {
                fos.write(data, 0, index)
                fos.flush()
            }
        } catch (Exception e) {
            e.printStackTrace()
        } finally {
            try {
                if (fos != null) {
                    fos.close()
                }
                if (is != null) {
                    is.close()
                }
            } catch (IOException e) {
                e.printStackTrace()
            }
        }
    }

    static void removeDirFromZipArchive(def file, String removeDir, filter) {
        net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(file)
        try {
            ArrayList<String> removes = new ArrayList<>()
            List<FileHeader> headersList = zipFile.getFileHeaders()
            for (FileHeader subHeader : headersList) {
                String subHeaderName = subHeader.getFileName()
                if((filter && filter.call(subHeaderName))
                        || (removeDir && subHeaderName.startsWith(removeDir))) {
                    println("rmpack remove dir: "+subHeaderName)
                    removes.add(subHeaderName)
                }
            }
            for (String remove : removes) {
                zipFile.removeFile(remove)
            }
        } catch (ZipException e) {
            e.printStackTrace()
        }
    }
}
