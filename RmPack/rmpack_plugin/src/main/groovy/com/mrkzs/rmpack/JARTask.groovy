package com.mrkzs.rmpack

import com.mrkzs.rmpack.ext.OutputLibExtension
import com.mrkzs.rmpack.ext.OutputLibJarExtension
import com.mrkzs.rmpack.ext.PluginExtension
import org.gradle.api.tasks.bundling.Jar

/**
 * Created by KINCAI
 * <p>
 * Desc 打包jar
 * 在build.gradle引入插件后，配置我们的扩展参数即可
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
        //因为是继承的org.gradle.api.tasks.bundling.Jar，所以这个任务实际运行的时候先实例化，所以是拿不到扩展参数的
        //暂时忽略下面的参数获取
        def rootExt = project.extensions.findByName(RmPack.EXTENSION_NAME) as PluginExtension
        if (rootExt) {
            println "$TAG root ext not null"
            def outputExt = rootExt.getProperty(RmPack.EXTENSION_OUTPUT) as OutputLibExtension
            if(outputExt) {
                println "$TAG output ext not null"
                def outputJarExt = outputExt.getProperty(RmPack.EXTENSION_OUTPUT_JAR) as OutputLibJarExtension
                if(outputJarExt) {
                    println "$TAG outputJar ext not null "+outputJarExt.fromJar
                    if(outputJarExt.fromJar) {
                        fromJar = outputJarExt.fromJar
                    }

                    if(outputJarExt.targetDir) {
                        targetDir = outputJarExt.fromJar
                    }

                    if(outputJarExt.targetName) {
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
