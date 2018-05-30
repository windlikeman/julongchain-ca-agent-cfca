package com.cfca.ra.repository;

import com.cfca.ra.RAServerException;
import com.cfca.ra.register.DefaultUser;
import com.cfca.ra.register.IUser;
import com.cfca.ra.register.UserInfo;
import com.cfca.ra.utils.MyFileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description CA 对应注册命令的注册信息库
 * @CodeReviewer
 * @since v3.0.0
 */
public enum RegistryStore implements IRegistryStore {
    /**
     * CA 对应注册命令的注册信息库的默认文件实现方式
     */
    CFCA("CFCA") {

        private Map<String, IUser> registerStore;

        private String getHomeDir() {
            String homeDir = String.join(File.separator, System.getProperty("user.dir"), caName);
            return MyFileUtils.getAbsFilePath(homeDir);
        }

        @Override
        public IUser getUser(String id, String[] attrs) throws RAServerException {
            if (registerStore == null) {
                registerStore = loadRegisterStoreFile();
            }
            if (attrs == null) {
                return registerStore.getOrDefault(id, null);
            }
            return registerStore.getOrDefault(id, null);
        }

        @Override
        public void insertUser(UserInfo user) throws RAServerException {
            try {
                final String name = user.getName();
                final String s = name + ":" + user.getPass();
                final String s1 = Base64.toBase64String(s.getBytes("UTF-8"));
                UserInfo newUserInfo = new UserInfo(user, s1);
                registerStore.put(name, new DefaultUser(newUserInfo));
                updateRegistrationFile();
            } catch (UnsupportedEncodingException e) {
                throw new RAServerException(RAServerException.REASON_CODE_REGISTER_SERVICE_INSERT_USER,e);
            }
        }

        @Override
        public boolean containsUser(String id, String[] attrs) throws RAServerException {
            if (registerStore == null) {
                registerStore = loadRegisterStoreFile();
            }
            return registerStore.containsKey(id);
        }

        private void updateRegistrationFile() throws RAServerException {
            if (registerStore == null) {
                return;
            }
            try {
                Gson gson = new GsonBuilder()
                        .disableHtmlEscaping()
                        .enableComplexMapKeySerialization()
                        .setPrettyPrinting()
                        .create();
                final String json = gson.toJson(registerStore);
                logger.info("updateRegistrationFile<<<<<< json : \n" + json);
                final String registerFilePath = String.join(File.separator, getHomeDir(), "register.dat");
                final File registerFile = new File(registerFilePath);
                FileUtils.writeStringToFile(registerFile, json);
            } catch (Exception e) {
                throw new RAServerException(RAServerException.REASON_CODE_REGISTER_SERVICE_UPDATE_REGISTER_STORE, e);
            }
        }

        private Map<String, IUser> loadRegisterStoreFile() throws RAServerException {

            try {
                Map<String, IUser> registerStore = new HashMap<String, IUser>();
                String admin = "admin";
                String pass = "YWRtaW46MTIzNA==";

                registerStore.put(admin, new DefaultUser(new UserInfo(admin, pass, admin, "", null, -1, 1)));
                final String s;
                final String registerFilePath = String.join(File.separator, getHomeDir(), "register.dat");
                final File registerFile = new File(registerFilePath);
                if (registerFile.exists()) {
                    s = FileUtils.readFileToString(registerFile);
                    if (StringUtils.isBlank(s) || FILE_NULL_CONTENT.equalsIgnoreCase(s.trim())) {
                        registerFile.delete();
                        return registerStore;
                    }
                    Type elemType = new TypeToken<Map<String, DefaultUser>>() {
                    }.getType();
                    final Map<String, DefaultUser> map = new Gson().fromJson(s, elemType);
                    Iterator<Map.Entry<String, DefaultUser>> it = map.entrySet().iterator();
                    String key;
                    DefaultUser value;
                    while (it.hasNext()) {
                        Map.Entry<String, DefaultUser> entry = it.next();
                        key = entry.getKey();
                        value = entry.getValue();
                        registerStore.put(key, value);
                    }
                }
                return registerStore;
            } catch (IOException e) {
                throw new RAServerException(RAServerException.REASON_CODE_REGISTER_SERVICE_LOAD_REGISTER_STORE, e);
            }
        }
    };
    private static final String FILE_NULL_CONTENT = "null";
    protected static final Logger logger = LoggerFactory.getLogger(RegistryStore.class);
    protected final String caName;

    RegistryStore(String caName) {
        this.caName = caName;
    }
}
