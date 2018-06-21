package com.cfca.ra.ca;

import com.cfca.ra.RAServerException;
import com.cfca.ra.register.IUser;
import com.cfca.ra.register.UserInfo;
import com.cfca.ra.repository.RegistryStore;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description DefaultUserRegistry 是用于检索用户和组的接口的默认实现
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class DefaultUserRegistry implements IUserRegistry {
    private RegistryStore registerStore;

    public DefaultUserRegistry() throws RAServerException {
        this.registerStore = RegistryStore.CFCA;
    }

    @Override
    public RegistryStore getRegistryStore() {
        return registerStore;
    }

    @Override
    public boolean containsUser(String id, String[] attrs) throws RAServerException {
        return registerStore.containsUser(id,attrs);
    }

    @Override
    public IUser getUser(String id, String[] attrs) throws RAServerException {
        return registerStore.getUser(id, null);
    }

    @Override
    public void insertUser(UserInfo user) throws RAServerException {
        registerStore.insertUser(user);

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
        return new DefaultAffiliation(0, name);
    }

    @Override
    public Affiliation[] getAllAffiliations(String name) throws RAServerException {
        return new Affiliation[]{new DefaultAffiliation(0, name)};
    }

    @Override
    public void insertAffiliation(String name, int level) throws RAServerException {
        final DefaultAffiliation defaultAffiliation = new DefaultAffiliation(level, name);
    }

    @Override
    public Map<String, String> getProperties(String... names) throws RAServerException {
        return new HashMap<>();
    }
}
