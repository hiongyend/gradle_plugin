package com.mrkzs.rmpack

import com.android.dx.command.Main
import com.mrkzs.rmpack.ext.DexTempSplitPackExtension
import com.mrkzs.rmpack.ext.PluginExtension
import com.mrkzs.rmpack.utils.FileUtil
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
/**
 * Created by KINCAI
 * <p>
 * Desc TODO
 * <p>
 * Date 2020-05-07 11:55
 */
public class DexTempSplitPackTask extends DefaultTask {
    def TAG = 'dex split'
    @TaskAction
    void output() {
        println "$TAG task start"
        def rootExt = project.extensions.findByName(RmPack.EXTENSION_NAME) as PluginExtension
        if(rootExt == null) {
            println "$TAG root ext null"
            return
        }

        def dexExt = rootExt.getProperty(RmPack.EXTENSION_DEX_SPLIT) as DexTempSplitPackExtension
        if(dexExt == null) {
            println "$TAG  dex ext null"
            return
        }
        def mainApk = dexExt.mainApk
        if (mainApk) {
            def mainApkFile = project.file(mainApk)
            if(!mainApkFile.exists()) {
                println "$TAG  main apk file not exists"
                return
            }
            def dexApk = dexExt.dexApk
            if(!dexApk) {
                println "$TAG  dex apk null"
                return
            }
            def dexApkFile = project.file(dexApk)
            if(!dexApkFile.exists()) {
                println "$TAG  dex apk file not exists"
                return
            }
            def mainApkParent = mainApkFile.getParentFile()
            def tempDir = project.file(mainApkParent.getAbsolutePath()+File.separator+"temp_dex")
            if(!tempDir.exists()) {
                tempDir.mkdirs()
            }

            def lib = dexExt.mainLib
            if(!lib) {
                println "$TAG  lib null"
                return
            }
            def libClassesJar
            def libFile = project.file(lib)
            if(lib.endsWith(".aar")) {
                //提取jar
                def fileList = FileUtil.getNeedZipFile(libFile, {
                    filter ->
                        return filter && filter == "classes.jar"
                })
                if(fileList) {
                    libClassesJar = project.file(tempDir.getAbsolutePath()+File.separator+"libclasses.jar")
                    FileUtil.writeBytesToFile(fileList.get(0).entry, libClassesJar)
                }

            } else {
                libClassesJar = libFile
            }

            //jar 转 dex --dex --output=xxx.dex xxx.jar
            def mainDex = project.file(tempDir.getAbsolutePath()+File.separator+'classes.dex')
            def args = ['--dex', "--output=${mainDex}", libClassesJar] as String[]
            println "$TAG main jar to dex start"
            //dx.jar
            Main.main(args)
            println "$TAG main jar to dex end"
            if(!mainDex.exists()) {
                println "$TAG main dex not exists"
                return
            }

            //默认主包dex 1个
            def mainDexCount = 1
            //main apk 移除dex
            FileUtil.removeDirFromZipArchive(mainApkFile, null, {
                filter ->
                    return filter && filter.endsWith('.dex') && !filter.contains("/")
            })
            ZipFile mainApkZipFile = new ZipFile(mainApkFile)
            ZipParameters mainZipParameters = new ZipParameters()
            mainZipParameters.setFileNameInZip(mainDex.getName())
            mainApkZipFile.addFile(mainDex, mainZipParameters)

            //dex apk提取from dex重命名并添在main apk添加 main dex 和 from dex
            List<String> dexApkList = new ArrayList<>()
            def dexApkFileList = FileUtil.getNeedZipFile(dexApkFile, {
                filter ->
                    return filter && filter.endsWith('.dex') && !filter.contains("/")
            })
            if(dexApkFileList) {
                dexApkFileList.sort { a, b ->
                    return a.entryName.compareTo(b.entryName)
                }

                dexApkFileList.eachWithIndex { zipInfo, index ->
                    println "$TAG dex apk get need ${zipInfo.entryName}"
                    ZipParameters fromZipParameters = new ZipParameters()
                    fromZipParameters.setFileNameInZip("classes${mainDexCount + 1 + index}.dex")
                    mainApkZipFile.addStream(zipInfo.entry, fromZipParameters)
                }
            }

            //清除临时目录
            tempDir.deleteDir()
            println "$TAG complete"

        } else {
            println "$TAG  main apk null"
        }
    }
}
