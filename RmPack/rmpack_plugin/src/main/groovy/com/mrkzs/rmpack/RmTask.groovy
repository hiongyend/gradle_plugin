package com.mrkzs.rmpack

import com.mrkzs.rmpack.ext.PluginExtension
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.model.FileHeader
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by KINCAI
 * <p>
 * Desc TODO
 * <p>
 * Date 2020-03-16 15:27
 */
class RmTask extends DefaultTask {
    @TaskAction
    void output() {
        println "rmpack task start"
        def rootExt = project.extensions.findByName(RmPack.EXTENSION_NAME) as PluginExtension
        if(rootExt == null) {
            println 'rmpack root ext  null'
            return
        }
        def ext = rootExt.getProperty(RmPack.EXTENSION_RM_LIB_PACK);
        if(ext == null) {
            println 'rmpack ext null'
            return
        }
        def packFilePath = ext.packFile
        if(packFilePath == null || packFilePath.length() == 0) {
            println "rmpack task packFile path null"
            return
        }
        def packFile = project.file(packFilePath)
        println "rmpack task pack file $packFile"
        if(packFile == null || !packFile.exists()) {
            println("rmpack task pack file no exists")
        }

        String packName = ext.packName
        println("rmpack task pack name $packName")
        if(packName != null && packName.length() > 0) {
            def split = packName.split("\\.")
            def packDir = ""
            for (String p : split) {
                packDir += (p+"/")
            }
            println("rmpack task pack dir $packDir")
            removeDirFromZipArchive(packFile, packDir)
        } else {
            println "rmpack task pack name null or empty"
        }
    }

    static void removeDirFromZipArchive(def file, String removeDir) {
        ZipFile zipFile = new ZipFile(file)
        try {
            ArrayList<String> removes = new ArrayList<>()
            List<FileHeader> headersList = zipFile.getFileHeaders()
            for (FileHeader subHeader : headersList) {
                String subHeaderName = subHeader.getFileName()
                if (subHeaderName.startsWith(removeDir)) {
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
