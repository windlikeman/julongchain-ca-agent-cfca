package com.cfca.ra.utils;

import java.io.File;
/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description 文件工具类
 * @CodeReviewer
 * @since v3.0.0
 */
public class MyFileUtils {
    public static String getAbsFilePath(String dir) {
        final File file = new File(dir);
        return file.getAbsolutePath();
    }
}
