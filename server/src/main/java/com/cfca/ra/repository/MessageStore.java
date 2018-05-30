package com.cfca.ra.repository;

import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.BaseRequest;
import com.cfca.ra.enroll.EnrollmentRequest;
import com.cfca.ra.gettcert.GettCertRequest;
import com.cfca.ra.reenroll.ReenrollmentRequest;
import com.cfca.ra.revoke.RevokeRequest;
import com.cfca.ra.utils.MyFileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description 消息记录存储实现
 * @CodeReviewer
 * @since v3.0.0
 */
public enum MessageStore implements IMessageStore {
    /**
     * enroll消息的存储记录
     */
    ENROLL_DEFAULT {
        @Override
        public boolean containsMessage(int messageId) throws RAServerException {
            if (store == null) {
                store = loadMessageFile("enroll-message.dat");
            }
            return store.containsKey(messageId);
        }

        @Override
        public void updateMessage(int messageId, BaseRequest s) throws RAServerException {
            if (store != null) {
                store.put(messageId, s);
                updateMessageFile("enroll-message.dat");
            }
        }

        private Map<Integer, BaseRequest> loadMessageFile(String fileName) throws RAServerException {
            try {
                Map<Integer, BaseRequest> store = new HashMap<>();
                final String filePath = String.join(File.separator, getServerHomeDir(), fileName);
                final File storefile = new File(filePath);
                if (storefile.exists()) {
                    final String s = FileUtils.readFileToString(storefile);
                    if (StringUtils.isBlank(s) || FILE_NULL_CONTENT.equalsIgnoreCase(s.trim())) {
                        storefile.delete();
                        return store;
                    }

                    Type elemType = new TypeToken<Map<Integer, EnrollmentRequest>>() {
                    }.getType();
                    final Map<Integer, EnrollmentRequest> map = new Gson().fromJson(s, elemType);
                    Iterator<Map.Entry<Integer, EnrollmentRequest>> it = map.entrySet().iterator();
                    Integer key;
                    EnrollmentRequest value;
                    while (it.hasNext()) {
                        Map.Entry<Integer, EnrollmentRequest> entry = it.next();
                        key = entry.getKey();
                        value = entry.getValue();
                        store.put(key, value);
                    }
                }
                return store;
            } catch (Exception e) {
                throw new RAServerException(RAServerException.REASON_CODE_MESSAGE_STORE_LOAD, e);
            }
        }

    },

    /**
     * reenroll消息的存储记录
     */
    REENROLL_DEFAULT {
        @Override
        public boolean containsMessage(int messageId) throws RAServerException {
            if (store == null) {
                store = loadMessageFile("reenroll-message.dat");
            }
            return store.containsKey(messageId);
        }

        @Override
        public void updateMessage(int messageId, BaseRequest s) throws RAServerException {
            if (store != null) {
                store.put(messageId, s);
                updateMessageFile("reenroll-message.dat");
            }
        }

        private Map<Integer, BaseRequest> loadMessageFile(String fileName) throws RAServerException {
            try {
                Map<Integer, BaseRequest> store = new HashMap<>();
                final String filePath = String.join(File.separator, getServerHomeDir(), fileName);
                final File storefile = new File(filePath);
                if (storefile.exists()) {
                    final String s = FileUtils.readFileToString(storefile);
                    if (StringUtils.isBlank(s) || FILE_NULL_CONTENT.equalsIgnoreCase(s.trim())) {
                        storefile.delete();
                        return store;
                    }

                    Type elemType = new TypeToken<Map<Integer, ReenrollmentRequest>>() {
                    }.getType();
                    final Map<Integer, ReenrollmentRequest> map = new Gson().fromJson(s, elemType);
                    Iterator<Map.Entry<Integer, ReenrollmentRequest>> it = map.entrySet().iterator();
                    Integer key;
                    ReenrollmentRequest value;
                    while (it.hasNext()) {
                        Map.Entry<Integer, ReenrollmentRequest> entry = it.next();
                        key = entry.getKey();
                        value = entry.getValue();
                        store.put(key, value);
                    }
                }
                return store;
            } catch (Exception e) {
                throw new RAServerException(RAServerException.REASON_CODE_MESSAGE_STORE_LOAD, e);
            }
        }
    },

    /**
     * revoke 消息的存储记录
     */
    REVOKE_DEFAULT {
        @Override
        public boolean containsMessage(int messageId) throws RAServerException {
            if (store == null) {
                store = loadMessageFile("revoke-message.dat");
            }
            return store.containsKey(messageId);
        }

        @Override
        public void updateMessage(int messageId, BaseRequest s) throws RAServerException {
            if (store != null) {
                store.put(messageId, s);
                updateMessageFile("revoke-message.dat");
            }
        }

        private Map<Integer, BaseRequest> loadMessageFile(String fileName) throws RAServerException {
            try {
                Map<Integer, BaseRequest> store = new HashMap<>();
                final String filePath = String.join(File.separator, getServerHomeDir(), fileName);
                final File storefile = new File(filePath);
                if (storefile.exists()) {
                    final String s = FileUtils.readFileToString(storefile);
                    if (StringUtils.isBlank(s) || FILE_NULL_CONTENT.equalsIgnoreCase(s.trim())) {
                        storefile.delete();
                        return store;
                    }

                    Type elemType = new TypeToken<Map<Integer, RevokeRequest>>() {
                    }.getType();
                    final Map<Integer, RevokeRequest> map = new Gson().fromJson(s, elemType);
                    Iterator<Map.Entry<Integer, RevokeRequest>> it = map.entrySet().iterator();
                    Integer key;
                    RevokeRequest value;
                    while (it.hasNext()) {
                        Map.Entry<Integer, RevokeRequest> entry = it.next();
                        key = entry.getKey();
                        value = entry.getValue();
                        store.put(key, value);
                    }
                }
                return store;
            } catch (Exception e) {
                throw new RAServerException(RAServerException.REASON_CODE_MESSAGE_STORE_LOAD, e);
            }
        }
    },

    /**
     * gettcert 消息的存储记录
     */
    GETTCERT_DEFAULT {
        @Override
        public boolean containsMessage(int messageId) throws RAServerException {
            if (store == null) {
                store = loadMessageFile("gettcert-message.dat");
            }
            return store.containsKey(messageId);
        }

        @Override
        public void updateMessage(int messageId, BaseRequest s) throws RAServerException {
            if (store != null) {
                store.put(messageId, s);
                updateMessageFile("gettcert-message.dat");
            }
        }

        private Map<Integer, BaseRequest> loadMessageFile(String fileName) throws RAServerException {
            try {
                Map<Integer, BaseRequest> store = new HashMap<>();
                final String filePath = String.join(File.separator, getServerHomeDir(), fileName);
                final File storefile = new File(filePath);
                if (storefile.exists()) {
                    final String s = FileUtils.readFileToString(storefile);
                    if (StringUtils.isBlank(s) || FILE_NULL_CONTENT.equalsIgnoreCase(s.trim())) {
                        storefile.delete();
                        return store;
                    }

                    Type elemType = new TypeToken<Map<Integer, GettCertRequest>>() {
                    }.getType();
                    final Map<Integer, GettCertRequest> map = new Gson().fromJson(s, elemType);
                    Iterator<Map.Entry<Integer, GettCertRequest>> it = map.entrySet().iterator();
                    Integer key;
                    GettCertRequest value;
                    while (it.hasNext()) {
                        Map.Entry<Integer, GettCertRequest> entry = it.next();
                        key = entry.getKey();
                        value = entry.getValue();
                        store.put(key, value);
                    }
                }
                return store;
            } catch (Exception e) {
                throw new RAServerException(RAServerException.REASON_CODE_MESSAGE_STORE_LOAD, e);
            }
        }
    };

    private static final String FILE_NULL_CONTENT = "null";
    protected Map<Integer, BaseRequest> store;

    protected void updateMessageFile(String fileName) throws RAServerException {
        if (store == null) {
            return;
        }
        try {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .enableComplexMapKeySerialization()
                    .create();
            final String json = gson.toJson(store);
            final String registerFilePath = String.join(File.separator, getServerHomeDir(), fileName);
            final File registerFile = new File(registerFilePath);
            FileUtils.writeStringToFile(registerFile, json);
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_MESSAGE_STORE_LOAD, e);
        }
    }

    protected String serverHomeDir;

    protected String getServerHomeDir() {
        return MyFileUtils.getAbsFilePath(serverHomeDir);
    }

    public void setServerHomeDir(String serverHomeDir) {
        this.serverHomeDir = serverHomeDir;
    }
}
