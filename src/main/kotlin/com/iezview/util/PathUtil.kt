package com.iezview.util

import java.nio.file.Files
import java.nio.file.Path

/**
 * Created by shishifanbuxie on 2017/4/21.
 * 工具类
 */
object PathUtil {
    /**
     * 创建目录
     */
     fun resolvePath(savepath: Path): Path {
        if (!Files.exists(savepath))  Files.createDirectories(savepath)
        return savepath
    }
}