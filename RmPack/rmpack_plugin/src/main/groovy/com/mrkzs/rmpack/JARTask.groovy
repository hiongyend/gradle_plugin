package com.mrkzs.rmpack

import com.mrkzs.rmpack.ext.OutputLibExtension
import com.mrkzs.rmpack.ext.OutputLibJarExtension
import com.mrkzs.rmpack.ext.PluginExtension
import com.mrkzs.rmpack.utils.StringUtil
import org.gradle.api.tasks.bundling.Jar
/**
 * Created by KINCAI
 * <p>
 * Desc TODO
 * <p>
 * Date 2020-04-16 18:20
 */
class JARTask extends Jar {
    final static def TAG = 'jar'
    JARTask() {
        super()
        println "$TAG task start"
        def targetName = "${project.name}"
        def targetDir = "/build/libs";
        def fromJar = project.file('/build/intermediates/packaged-classes/release/classes.jar')
        def rootExt = project.extensions.findByName(RmPack.EXTENSION_NAME) as PluginExtension
        if (rootExt != null) {
            println "$TAG root ext not null"
            def outputExt = rootExt.getProperty(RmPack.EXTENSION_OUTPUT) as OutputLibExtension
            if(outputExt != null) {
                println "$TAG output ext not null"
                def outputJarExt = outputExt.getProperty(RmPack.EXTENSION_OUTPUT_JAR) as OutputLibJarExtension
                if(outputJarExt != null) {
                    println "$TAG outputJar ext not null "+outputJarExt.fromJar
                    if(!StringUtil.isEmpty(outputJarExt.fromJar)) {
                        fromJar = outputJarExt.fromJar
                    }

                    if(!StringUtil.isEmpty(outputJarExt.targetDir)) {
                        targetDir = outputJarExt.fromJar
                    }

                    if(!StringUtil.isEmpty(outputJarExt.targetName)) {
                        targetName = outputJarExt.targetName
                    }
                }
            }
        }

        println "$TAG fromJar:$fromJar targetDir:$targetDir targetName:$targetName"

        from(project.zipTree(fromJar))
        from(project.fileTree(dir: 'src/main', includes: ['']))
        getArchiveBaseName().set(targetName)
        getDestinationDirectory().set(project.file(targetDir))
    }
}
