package com.mrkzs.rmpack

import com.mrkzs.rmpack.ext.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class RmPack implements Plugin<Project> {

    private final String TAG = "RmPack "
    static final def EXTENSION_NAME = "rmPackArg"
    static final def EXTENSION_RM_LIB_PACK = "libPack"
    static final def EXTENSION_COMM_RES = "commRes"
    static final def EXTENSION_DEX_SPLIT = "dex"
    static final def EXTENSION_OUTPUT = "output"
    static final def EXTENSION_OUTPUT_AAR = "aar"
    static final def EXTENSION_OUTPUT_JAR = "jar"
    //任务组
    static final def TASK_GROUP_RMPACK = 'rmpack'
    static final def TASK_RM_PACK = 'rmPack'
    static final def TASK_COPY_COMM_RES = 'copyLibRes'
    static final def TASK_DEX_SPLIT_PACK = 'dexSplitPack'

    @Override
    void apply(Project project) {
        println(TAG + "plugin start")
        println(TAG + "plugin root dir " + project.getRootDir())

        //添加扩展参数
        def baseExt = project.extensions.create(EXTENSION_NAME, PluginExtension, project)
        def extCommRes = baseExt.extensions.create(EXTENSION_COMM_RES, CommResExtension, project)
        def extDex = baseExt.extensions.create(EXTENSION_DEX_SPLIT, DexTempSplitPackExtension, project)
        def extOutput = baseExt.extensions.create(EXTENSION_OUTPUT, OutputLibExtension, project)
        def extOutputAar = extOutput.extensions.create(EXTENSION_OUTPUT_AAR, OutputLibAarExtension, project)
        def extOutputJar = extOutput.extensions.create(EXTENSION_OUTPUT_JAR, OutputLibJarExtension, project)

        def rmTask = project.task(TASK_RM_PACK, group: TASK_GROUP_RMPACK, type: RmTask)
        def copyResTask = project.task(TASK_COPY_COMM_RES, group: TASK_GROUP_RMPACK, type: CopyResTask)

        //根据 module name 生成任务名
        def projectName = project.name
        def projectNameSuffix = projectName
        if (projectNameSuffix != null && projectNameSuffix.length() > 0) {
            if (projectNameSuffix.contains('_')) {
                projectNameSuffix = projectNameSuffix.substring(projectNameSuffix.lastIndexOf('_') + 1)
            }
        }
        println "$TAG plugin projectNameSuffix ${projectNameSuffix}"
        //打包aar
        def aarTask = project.task("makeAar${projectNameSuffix}", group: TASK_GROUP_RMPACK, type: AARTask)
        def build = project.tasks.findByName("build")
        aarTask.dependsOn(build)
        aarTask.mustRunAfter(build)

        //打包所有aar
        def aarAllTask = project.task("makeAarAll", group: TASK_GROUP_RMPACK, type: AARTask)
        aarAllTask.dependsOn(build)
        aarAllTask.mustRunAfter(build)
        //打包jar，
        def jarTask = project.task("makeJar${projectNameSuffix}", group: TASK_GROUP_RMPACK, type: JARTask)
        jarTask.dependsOn(build)
        jarTask.mustRunAfter(build)
        //打包所有jar
        def jarAllTask = project.task("makeJarAll", group: TASK_GROUP_RMPACK, type: JARTask)
        jarAllTask.dependsOn(build)
        jarAllTask.mustRunAfter(build)

        //dex临时分包方案任务
        project.task(TASK_DEX_SPLIT_PACK, group: TASK_GROUP_RMPACK, type: DexTempSplitPackTask)


        boolean isTask
        project.tasks.whenTaskAdded { Task theTask ->
            println "plugin task " + theTask.name
            if (!isTask && (theTask.name == 'preDebugBuild' || theTask.name == "preBuild")) {
                println "plugin task nnn " + theTask.name
                isTask = true

                // TODO 暂定在编译前执行任务，针对zsdk打包aar，后续会考虑根据需求手动执行以下任务
                // TODO 因为配置了以下任务的必要参数，就会执行任务，当项目没有这个必要的时候，就有点多余
                theTask.dependsOn(copyResTask)
                theTask.mustRunAfter(copyResTask)

                theTask.dependsOn(rmTask)
                theTask.mustRunAfter(rmTask)
            }
        }

    }
}

