package org.bica.julongchain.cfca.ra.command.internal.enroll;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.bica.julongchain.cfca.ra.command.CommandException;
import org.bica.julongchain.cfca.ra.command.internal.ClientConfig;
import org.bica.julongchain.cfca.ra.command.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description 注册者证书 与用户的ID 映射信息
 * @CodeReviewer
 * @since v3.0.0
 */
public enum EnrollIdStore implements IEnrollIdStore {
    /**
     * 默认记录文件的实现
     */
    CFCA("CFCA") {

        private final String ENROLL_ID_DAT = "enroll-id.dat";

        private Map<String, String> enrollIdStore;

        @Override
        public void updateEnrollIdStore(String enrollmentID, String id) throws CommandException {
            if (enrollIdStore == null) {
                enrollIdStore = loadEnrollIdFile();
            }
            enrollIdStore.put(enrollmentID, id);
            updateEnrollIdFile();
        }

        @Override
        public String getUserName(String enrollmentID) throws CommandException {
            if (enrollIdStore == null) {
                enrollIdStore = loadEnrollIdFile();
            }
            final String userName = enrollIdStore.getOrDefault(enrollmentID, ADMIN);
            logger.info("EnrollIdStore@getUserName {} : {}", enrollmentID, userName);
            return userName;
        }

        private Map<String, String> loadEnrollIdFile() throws CommandException {
            String contentFile = null;
            logger.info("EnrollIdStore@loadEnrollIdFile running...");
            final String homeDir = ClientConfig.INSTANCE.getMspDir();
            File file = new File(String.join(File.separator, homeDir, ENROLL_ID_DAT));

            try {
                Map<String, String> enrollIdStore = new HashMap<>();
                enrollIdStore.put("admin", "admin");

                if (file.exists()) {
                    contentFile = FileUtils.readFileToString(file);

                    boolean invalidFile = StringUtils.isBlank(contentFile)
                            || (contentFile.length() < 100 && contentFile.trim().equalsIgnoreCase(FILE_NULL_CONTENT));
                    if (invalidFile) {
                        logger.error("EnrollIdStore@loadEnrollIdFile failed: invalidFile");
                        file.delete();
                        return enrollIdStore;
                    }

                    Gson gson = new GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().create();
                    Type type = new TypeToken<Map<String, String>>() {
                    }.getType();
                    Map<String, String> map = gson.fromJson(contentFile, type);
                    Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
                    Map.Entry<String, String> entry;
                    while (it.hasNext()) {
                        entry = it.next();
                        enrollIdStore.put(entry.getKey(), entry.getValue());
                    }
                    logger.info("EnrollIdStore@loadEnrollIdFile okay");
                } else {
                    logger.info("EnrollIdStore@loadEnrollIdFile failed: not exists-->{}", file.getAbsolutePath());
                    final File parentFile = file.getParentFile();
                    final boolean mkdirs = parentFile.mkdirs();
                    if (!mkdirs) {
                        logger.warn("EnrollCommand@loadEnrollIdFilecreateNewFile<<<<<<failed to mkdirs at {} ",
                                parentFile.getAbsolutePath());
                    }

                    final boolean newFile = file.createNewFile();
                    if (!newFile) {
                        logger.warn("EnrollIdStore@loadEnrollIdFile : failed to createNewFile at {} ",
                                file.getAbsolutePath());
                    }
                }
                return enrollIdStore;
            } catch (Exception e) {
                logger.error("EnrollIdStore@loadEnrollIdFile failed: {}", file, e);
                throw new CommandException("EnrollIdStore@loadEnrollIdFile failed");
            }
        }

        private void updateEnrollIdFile() throws CommandException {
            if (enrollIdStore == null) {
                return;
            }

            logger.info("EnrollIdStore@updateEnrollIdFile running...");
            final String homeDir = ClientConfig.INSTANCE.getMspDir();
            File file = new File(String.join(File.separator, homeDir, "enroll-id.dat"));

            try {

                if (file.exists()) {
                    Gson gson = new GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().setPrettyPrinting().create();
                    final String contentFile = gson.toJson(enrollIdStore);
                    FileUtils.writeStringToFile(file, contentFile);
                    logger.info("EnrollIdStore@updateEnrollIdFile okay");
                } else {
                    logger.error("EnrollIdStore@updateEnrollIdFile failed: not exists-->{}",
                            file.getAbsolutePath());
                }

            } catch (IOException e) {
                logger.error("EnrollIdStore@updateEnrollIdFile failed: {}", file, e);
                throw new CommandException("EnrollIdStore@updateEnrollIdFile failed",e);
            }
        }
    };
    public static final String FILE_NULL_CONTENT = "null";
    public static final String ADMIN = "admin";
    protected static final Logger logger = LoggerFactory.getLogger(EnrollIdStore.class);
    protected final String caName;

    EnrollIdStore(String caName) {
        this.caName = caName;
    }

}
