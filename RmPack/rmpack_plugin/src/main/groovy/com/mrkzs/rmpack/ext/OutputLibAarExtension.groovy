package com.mrkzs.rmpack.ext

import org.gradle.api.Project

/**
 * Created by KINCAI
 * <p>
 * Desc sdk公共资源 资源结构跟android studio项目module结构保持一致
 * <p>
 * Date 2020-03-16 15:25
 */
class OutputLibAarExtension {
    def fromDir
    //带后缀
    def fromName
    def targetDir
    //带后缀
    def targetName
    OutputLibAarExtension(Project project) {

    }
}
