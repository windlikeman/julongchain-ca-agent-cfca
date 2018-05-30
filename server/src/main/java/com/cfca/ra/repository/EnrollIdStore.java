package com.cfca.ra.repository;

import com.cfca.ra.RAServerException;
import com.cfca.ra.utils.MyFileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description CA的注册者证书 ID 映射信息
 * @CodeReviewer
 * @since v3.0.0
 */
public enum EnrollIdStore implements IEnrollIdStore {
    /**
     * CA的注册者证书 与用户映射信息的默认文件实现方式
     */
    CFCA("CFCA") {
        private Map<String, String> enrollIdStore;

        private String getHomeDir() {
            String homeDir = String.join(File.separator, System.getProperty("user.dir"), caName);
            return MyFileUtils.getAbsFilePath(homeDir);
        }

        @Override
        public void updateEnrollIdStore(String enrollmentID, String id) throws RAServerException {
            if (enrollIdStore == null) {
                enrollIdStore = loadEnrollIdFile();
            }
            enrollIdStore.put(id, enrollmentID);
            updateEnrollIdFile();
        }

        @Override
        public String getEnrollmentId(String id) throws RAServerException {
            if (enrollIdStore == null) {
                enrollIdStore = loadEnrollIdFile();
            }
            return enrollIdStore.getOrDefault(id, "admin");
        }

        private Map<String, String> loadEnrollIdFile() throws RAServerException {
            try {
                Map<String, String> enrollIdStore = new HashMap<>();
                enrollIdStore.put("admin", "admin");
                final String homeDir = getHomeDir();
                File file = new File(String.join(File.separator, homeDir, "enroll-id.dat"));
                if (file.exists()) {
                    final String s = FileUtils.readFileToString(file);
                    if (StringUtils.isBlank(s) || FILE_NULL_CONTENT.equalsIgnoreCase(s.trim())) {
                        file.delete();
                        return enrollIdStore;
                    }
                    if (logger.isInfoEnabled()) {
                        logger.info("loadEnrollIdFile<<<<<< s:" + s);
                    }

                    Gson gson = new GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().create();
                    Type type = new TypeToken<Map<String, String>>() {
                    }.getType();
                    Map<String, String> map = gson.fromJson(s, type);
                    Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
                    Map.Entry<String, String> entry;
                    while (it.hasNext()) {
                        entry = it.next();
                        enrollIdStore.put(entry.getKey(), entry.getValue());
                    }
                }
                return enrollIdStore;
            } catch (Exception e) {
                throw new RAServerException(RAServerException.REASON_CODE_ENROLLIDSTORE_LOAD_ENROLLID_FILE, e);
            }
        }

        private void updateEnrollIdFile() throws RAServerException {
            if (enrollIdStore == null) {
                return;
            }
            try {
                final String homeDir = getHomeDir();
                File file = new File(String.join(File.separator, homeDir, "enroll-id.dat"));

                Gson gson = new GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().create();
                final String s = gson.toJson(enrollIdStore);
                if (logger.isInfoEnabled()) {
                    logger.info("updateEnrollIdFile<<<<<<" + s);
                }

                FileUtils.writeStringToFile(file, s);
            } catch (IOException e) {
                throw new RAServerException(RAServerException.REASON_CODE_ENROLLIDSTORE_UPDATE_ENROLLID_FILE, e);
            }
        }
    };
    private static final String FILE_NULL_CONTENT = "null";
    protected static final Logger logger = LoggerFactory.getLogger(EnrollIdStore.class);
    protected final String caName;

    EnrollIdStore(String caName) {
        this.caName = caName;
    }


}
