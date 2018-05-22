package com.cfca.ra.ca;

import com.cfca.ra.RAServerException;
import com.cfca.ra.ca.register.DefaultUser;
import com.cfca.ra.ca.register.IUser;
import com.cfca.ra.ca.register.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
public class DefaultUserRegistry implements IUserRegistry {
    private static final Logger logger = LoggerFactory.getLogger(DefaultUserRegistry.class);
    private final ConcurrentHashMap<String, IUser> registerStore;
    private final String dir;

    public DefaultUserRegistry(String dir) throws RAServerException {
        this.dir = dir;
        this.registerStore = loadRegisterStoreFile();
    }

    @Override
    public IUser getUser(String id, String[] attrs) throws RAServerException {
        if (attrs == null) {
            return registerStore.getOrDefault(id, null);
        }

        return null;
    }

    @Override
    public void insertUser(UserInfo user) throws RAServerException {
        registerStore.put(user.getName(), new DefaultUser(user));
        updateRegistrationFile();

    }

    @Override
    public void updateUser(UserInfo userInfo) throws RAServerException {

    }

    @Override
    public UserInfo deleteUser(String id) throws RAServerException {
        return null;
    }

    @Override
    public Affiliation getAffiliation(String name) throws RAServerException {
        return null;
    }

    @Override
    public Affiliation[] getAllAffiliations(String name) throws RAServerException {
        return new Affiliation[0];
    }

    @Override
    public void insertAffiliation(String name, String prekey, int level) throws RAServerException {

    }

    @Override
    public Map<String, String> getProperties(String... names) throws RAServerException {
        return null;
    }

    @Override
    public UserInfo[] getUserLessThanLevel(int version) throws RAServerException {
        return new UserInfo[0];
    }

    @Override
    public UserInfo[] getFilteredUsers(String affiliation, String types) throws RAServerException {
        return new UserInfo[0];
    }

    @Override
    public void deleteAffiliation(String name, boolean force, boolean identityRemoval, boolean isRegistrar) throws RAServerException {

    }

    @Override
    public void ModifyAffiliation(String oldAffiliation, String newAffiliation, boolean force, boolean isRegistrar) throws RAServerException {

    }

    private void updateRegistrationFile() throws RAServerException {

        try {
            final String json = new Gson().toJson(registerStore);
            logger.info("updateRegistrationFile<<<<<< json : \n" + json);
            final String registerFilePath = String.join(File.separator, dir, "register.dat");
            final File registerFile = new File(registerFilePath);
            FileUtils.writeStringToFile(registerFile, json);
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_REGISTER_SERVICE_UPDATE_REGISTER_STORE, e);
        }
    }

    private ConcurrentHashMap<String, IUser> loadRegisterStoreFile() throws RAServerException {

        try {
            ConcurrentHashMap<String, IUser> registerStore = new ConcurrentHashMap<String, IUser>();
            final String s;
            final String registerFilePath = String.join(File.separator, dir, "register.dat");
            final File registerFile = new File(registerFilePath);
            if (registerFile.exists()) {
                s = FileUtils.readFileToString(registerFile);
                Type elemType = new TypeToken<Map<String, DefaultUser>>() {}.getType();
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


}
