package com.mrkzs.rmpack

import com.mrkzs.rmpack.ext.OutputLibAarExtension
import com.mrkzs.rmpack.ext.OutputLibExtension
import com.mrkzs.rmpack.ext.PluginExtension
import org.gradle.api.internal.file.copy.CopyActionExecuter
import org.gradle.api.tasks.Copy
/**
 * Created by KINCAI
 * <p>
 * Desc 打包aar
 * 在build.gradle引入插件后，配置我们的扩展参数即可
 * <p>
 * Date 2020-03-16 15:27
 */
class AARTask extends Copy {
    final static def TAG = 'aar'
    AARTask(){
        super()
        def fromDir = 'build/outputs/aar/'
        //带后缀
        def fromName =  "${project.name}-release.aar"
        def targetDir = 'build/libs'
        //带后
        def targetName = "${project.name }.aar";
        //因为是继承的org.gradle.api.tasks.Copy，所以这个任务实际运行的时候先实例化，所以是拿不到扩展参数的
        //暂时忽略下面的参数获取
        def rootExt = project.extensions.findByName(RmPack.EXTENSION_NAME) as PluginExtension
        if (rootExt) {
            println "$TAG root ext not null"
            def outputExt = rootExt.getProperty(RmPack.EXTENSION_OUTPUT) as OutputLibExtension
            if(outputExt) {
                println "$TAG output ext not null"
                def outputAarExt = outputExt.getProperty(RmPack.EXTENSION_OUTPUT_AAR) as OutputLibAarExtension
                if(outputAarExt) {
                    println "$TAG outputAar ext not null"
                    if(outputAarExt.fromDir) {
                        fromDir = outputAarExt.fromDir
                    }

                    if(outputAarExt.fromName) {
                        fromName = outputAarExt.fromName
                    }

                    if(outputAarExt.targetDir) {
                        targetDir = outputAarExt.targetDir
                    }

                    if(outputAarExt.targetName) {
                        targetName = outputAarExt.targetName
                    }
                }
            }
        }
        def aarTargetFile = "${targetDir}/${targetName}"
        def aarFromFile = fromDir + fromName
        println "$TAG fromDir:$fromDir fromName:$fromName targetDir:$targetDir targetName:$targetName"

        //删除存在的
        project.delete(aarTargetFile)
        //设置拷贝的文件
        from(aarFromFile)
        into(targetDir)
//        project.copySpec().exclude(it.name.startsWith('R$'))
        //打进jar包后的文件目录
//            exclude { it.name.startsWith('R$'); }
        /*include {AAR_FROM_NAME}
        //重命名
        */rename(fromName,targetName)
        println 'aar task finished'
    }

    @Override
    protected CopyActionExecuter createCopyActionExecuter() {
        return super.createCopyActionExecuter()
    }
}
