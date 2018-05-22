package com.cfca.ra.command.utils;

import com.cfca.ra.command.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author zhangchong
 * @create 2018/5/13
 * @Description 文件操作工具类
 * @CodeReviewer
 * @since v3.0.0
 */
public class MyFileUtils {
    private static final Logger logger = LoggerFactory.getLogger(MyFileUtils.class);

    private MyFileUtils() {

    }

    /**
     * 创建新文件
     *
     * @param filePath
     * @throws Exception
     */
    public static void createNewFile(String filePath) throws CommandException {
        if (MyStringUtils.isEmpty(filePath)) {
            throw new CommandException(CommandException.REASON_CODE_FILE_CREATE_NEW_FILE, "failed to create file because filePath is empty");
        }
        try {
            final File file = new File(filePath);
            if (!file.exists()) {

                final File parentFile = file.getParentFile();
                final boolean mkdirs = parentFile.mkdirs();
                if (!mkdirs) {
                    logger.warn("failed to mkdirs at {} ", parentFile.getAbsolutePath());
                }

                final boolean newFile = file.createNewFile();
                if (!newFile) {
                    logger.warn("a default configuration file at {} already exists", filePath);
                }
            }
            logger.info("Created a default configuration file at {}", filePath);
        } catch (IOException e) {
            throw new CommandException(CommandException.REASON_CODE_FILE_CREATE_NEW_FILE, "failed to create file:" + filePath);
        }
    }

    public static String getDir(String filePath) throws CommandException {
        if (MyStringUtils.isEmpty(filePath)) {
            throw new CommandException(CommandException.REASON_CODE_FILE_GET_DIR_PATH, "failed to get file dir because filePath is empty");
        }
        try {
            File file = new File(filePath);
            return file.getParent();
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_FILE_GET_DIR_PATH);
        }
    }

    /**
     * 文件是否存在
     *
     * @param filename
     * @return
     * @throws CommandException
     */
    public static boolean fileExists(String filename) throws CommandException {
        if (MyStringUtils.isEmpty(filename)) {
            throw new CommandException(CommandException.REASON_CODE_FILE_EXISTS, "failed to check file exists because filename is empty");
        }
        try {
            final File file = new File(filename);
            return file.exists();
        } catch (Exception e) {
            throw new CommandException(CommandException.REASON_CODE_FILE_EXISTS);
        }
    }

    public static String makeFileAbs(String filename, String dir) {
        if (MyStringUtils.isEmpty(filename)) {
            return "";
        }

        final File file = new File(filename);
        if (file.isAbsolute()) {
            return file.getAbsolutePath();
        }

        return String.join(File.separator, dir, filename);
    }

    public static String makeFileAbs(String dir) {
        if (MyStringUtils.isEmpty(dir)) {
            return "";
        }

        final File file = new File(dir);
        if (file.isAbsolute()) {
            return file.getAbsolutePath();
        }

        final String workDir = System.getProperty("user.dir");
        return String.join(File.separator, workDir, dir);
    }
}
