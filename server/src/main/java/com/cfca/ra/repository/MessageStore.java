package com.cfca.ra.repository;

import com.cfca.ra.RAServerException;
import com.cfca.ra.beans.BaseRequest;
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
    };

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
            final String registerFilePath = String.join(File.separator, getHomeDir(), fileName);
            final File registerFile = new File(registerFilePath);
            FileUtils.writeStringToFile(registerFile, json);
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_MESSAGE_STORE_LOAD, e);
        }
    }

    protected Map<Integer, BaseRequest> loadMessageFile(String fileName) throws RAServerException {
        try {
            Map<Integer, BaseRequest> store = new HashMap<>();
            final String homeDir = getHomeDir();
            final String filePath = String.join(File.separator, homeDir, fileName);
            final File storefile = new File(filePath);
            if (storefile.exists()) {
                final String s = FileUtils.readFileToString(storefile);
                if (StringUtils.isBlank(s) || s.trim().equalsIgnoreCase("null")) {
                    storefile.delete();
                    return store;
                }

                Type elemType = new TypeToken<Map<Long, BaseRequest>>() {
                }.getType();
                final Map<Integer, BaseRequest> map = new Gson().fromJson(s, elemType);
                Iterator<Map.Entry<Integer, BaseRequest>> it = map.entrySet().iterator();
                Integer key;
                BaseRequest value;
                while (it.hasNext()) {
                    Map.Entry<Integer, BaseRequest> entry = it.next();
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

    protected String getHomeDir() {
        final String homeDir = System.getProperty("user.dir");
        return MyFileUtils.getAbsFilePath(homeDir);
    }
}
