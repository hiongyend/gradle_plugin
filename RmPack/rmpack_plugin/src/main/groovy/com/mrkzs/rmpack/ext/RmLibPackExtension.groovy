package com.mrkzs.rmpack.ext

import org.gradle.api.Project

/**
 * Created by KINCAI
 * <p>
 * Desc 去除依赖包中的指定包
 * <p>
 * Date 2020-03-16 15:25
 */
class RmLibPackExtension {
    //依赖包文件
    def packFile = ""
    //需要删除的包
    def packName = ""

   RmLibPackExtension(Project project) {

    }
}
