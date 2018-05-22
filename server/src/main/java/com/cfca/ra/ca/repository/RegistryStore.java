package com.cfca.ra.ca.repository;

import com.cfca.ra.RAServerException;
import com.cfca.ra.ca.DefaultUserRegistry;
import com.cfca.ra.ca.register.DefaultUser;
import com.cfca.ra.ca.register.IUser;
import com.cfca.ra.ca.register.UserInfo;
import com.cfca.ra.utils.MyFileUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public enum RegistryStore implements IRegistryStore {
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
            registerStore.put(user.getName(), new DefaultUser(user));
            updateRegistrationFile();

        }

        private void updateRegistrationFile() throws RAServerException {
            if (registerStore == null) {
                return;
            }
            try {
                final String json = new Gson().toJson(registerStore);
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
                final String s;
                final String registerFilePath = String.join(File.separator, getHomeDir(), "register.dat");
                final File registerFile = new File(registerFilePath);
                if (registerFile.exists()) {
                    s = FileUtils.readFileToString(registerFile);
                    if (StringUtils.isBlank(s) || s.trim().equalsIgnoreCase("null")) {
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
    protected static final Logger logger = LoggerFactory.getLogger(RegistryStore.class);
    protected final String caName;

    RegistryStore(String caName) {
        this.caName = caName;
    }
}
