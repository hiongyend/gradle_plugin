package com.mrkzs.rmpack

import com.mrkzs.rmpack.ext.PluginExtension
import com.mrkzs.rmpack.utils.FileUtil
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
    def TAG = 'rmpack'

    @TaskAction
    void output() {
        println "rmpack task start"
        def rootExt = project.extensions.findByName(RmPack.EXTENSION_NAME) as PluginExtension
        if (!rootExt) {
            println 'rmpack root ext  null'
            return
        }

        def packList = rootExt.getProperty(RmPack.EXTENSION_RM_LIB_PACK);
        if (!packList) {
            println "$TAG pack list null"
            return
        }
        println "$TAG pack list $packList"
        packList.eachWithIndex { entry, i ->
            println "$TAG pack i=$i entry=$entry"
            def packFilePath = entry["packFile"]
            if (!packFilePath) {
                println "rmpack task packFile path null"
                return true
            }
            println "$TAG pack packFile $packFilePath"
            def packFile = project.file(packFilePath)
            println "$TAG pack file $packFile"
            if (!packFile.exists()) {
                println("$TAG pack file no exists")
                return true
            }
            def packNameList = entry["packName"]
            if (!packNameList) {
                println "rmpack task packName list null"
                return true
            }
            println "$TAG pack packName list size=${packNameList.size()}, list=$packNameList"
            packNameList.eachWithIndex { nameEntry, nameIndex ->
                if (nameEntry) {
                    def split = nameEntry.split("\\.")
                    def packDir = ""
                    for (String p : split) {
                        packDir += (p + "/")
                    }
                    println "$TAG pack dir $packDir"
                    FileUtil.removeDirFromZipArchive(packFile, packDir, null)
                } else {
                    println "$TAG pack pack name null or empty"
                }
            }

        }
    }

}
