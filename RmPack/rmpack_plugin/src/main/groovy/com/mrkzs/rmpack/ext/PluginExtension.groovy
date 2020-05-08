package com.mrkzs.rmpack.ext

import org.gradle.api.Project

/**
 * Created by KINCAI
 * <p>
 * Desc 本插件扩展参数根扩展
 * <p>
 * Date 2020-03-16 15:25
 */
class PluginExtension {
    def libPack
    //依赖包文件
    def packFile = ""
    //需要删除的包
    def packName = ""
    PluginExtension(Project project) {

    }
}
