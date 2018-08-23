package org.bica.julongchain.cfca.ra.command.utils;

import java.io.File;
import java.io.IOException;

import org.bica.julongchain.cfca.ra.command.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangchong
 * @create 2018/5/13
 * @Description 文件操作工具类
 * @CodeReviewer
 * @since v3.0.0
 */
public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {

    }

    /**
     * 创建新文件
     *
     * @param filePath
     * @throws Exception
     */
    public static void createNewFile(String filePath) throws CommandException {
        if (StringUtils.isEmpty(filePath)) {
            throw new CommandException("failed to create file because filePath is empty");
        }
        try {
            final File file = new File(filePath);
            if (!file.exists()) {

                final File parentFile = file.getParentFile();
                final boolean mkdirs = parentFile.mkdirs();
                if (!mkdirs) {
                    logger.warn("createNewFile<<<<<<failed to mkdirs at {} ", parentFile.getAbsolutePath());
                }

                final boolean newFile = file.createNewFile();
                if (!newFile) {
                    logger.warn("createNewFile<<<<<<a default configuration file at {} already exists", filePath);
                }
            }
            logger.info("createNewFile<<<<<<Created a default configuration file at {}", filePath);
        } catch (IOException e) {
            throw new CommandException("failed to create file:" + filePath);
        }
    }

    public static String getDir(String filePath) throws CommandException {
        if (StringUtils.isEmpty(filePath)) {
            throw new CommandException("failed to get file dir because filePath is empty");
        }
        try {
            File file = new File(filePath);
            return file.getParent();
        } catch (Exception e) {
            throw new CommandException("fail to get file dir");
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
        if (StringUtils.isEmpty(filename)) {
            throw new CommandException("failed to check file exists because filename is empty");
        }
        try {
            final File file = new File(filename);
            return file.exists();
        } catch (Exception e) {
            throw new CommandException("fail to check file exists");
        }
    }

    public static String makeFileAbs(String filename, String dir) {
        if (StringUtils.isEmpty(filename)) {
            return "";
        }

        final File file = new File(filename);
        if (file.isAbsolute()) {
            return file.getAbsolutePath();
        }

        return String.join(File.separator, dir, filename);
    }

    public static String makeFileAbs(String dir) {
        if (StringUtils.isEmpty(dir)) {
            return "";
        }

        final File file = new File(dir);
        if (file.isAbsolute()) {
            return file.getAbsolutePath();
        }

        final String workDir = System.getProperty("user.dir");
        return String.join(File.separator, workDir, dir);
    }

    public static String readFileToString(File file, String string) throws IOException {
        return org.apache.commons.io.FileUtils.readFileToString(file, string);
    }

    public static void writeStringToFile(File file, String defaultCfgTemplate) throws IOException {
        org.apache.commons.io.FileUtils.writeStringToFile(file, defaultCfgTemplate);
    }

    public static String readFileToString(File file) throws IOException {
        return org.apache.commons.io.FileUtils.readFileToString(file);
    }
}
