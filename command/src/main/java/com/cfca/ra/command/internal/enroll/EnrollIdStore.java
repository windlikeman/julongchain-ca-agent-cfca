package com.cfca.ra.command.internal.enroll;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.internal.ClientConfig;
import com.cfca.ra.command.utils.MyStringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
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
    CFCA("CFCA") {
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
            return enrollIdStore.getOrDefault(enrollmentID, ADMIN);
        }

        private Map<String, String> loadEnrollIdFile() throws CommandException {
            try {
                Map<String, String> enrollIdStore = new HashMap<>();
                enrollIdStore.put("admin", "admin");
                final String homeDir = ClientConfig.INSTANCE.getMspDir();
                File file = new File(String.join(File.separator, homeDir, "enroll-id.dat"));
                if (file.exists()) {
                    final String s = FileUtils.readFileToString(file);
                    if (MyStringUtils.isBlank(s) || s.trim().equalsIgnoreCase(FILE_NULL_CONTENT)) {
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
                throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_LOAD_ENROLLID_FILE, e);
            }
        }

        private void updateEnrollIdFile() throws CommandException {
            if (enrollIdStore == null) {
                return;
            }
            try {
                final String homeDir = ClientConfig.INSTANCE.getMspDir();
                File file = new File(String.join(File.separator, homeDir, "enroll-id.dat"));

                Gson gson = new GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().create();
                final String s = gson.toJson(enrollIdStore);
                if (logger.isInfoEnabled()) {
                    logger.info("updateEnrollIdFile<<<<<<" + s);
                }

                FileUtils.writeStringToFile(file, s);
            } catch (IOException e) {
                throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_UPDATE_ENROLLID_FILE, e);
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
