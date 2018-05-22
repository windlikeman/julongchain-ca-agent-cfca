package com.cfca.ra.ca;

import com.cfca.ra.RAServerException;
import com.cfca.ra.ca.register.IUser;
import com.cfca.ra.ca.register.UserInfo;
import com.cfca.ra.ca.repository.RegistryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
public class DefaultUserRegistry implements IUserRegistry {
    private RegistryStore registerStore;


    public DefaultUserRegistry() throws RAServerException {
        this.registerStore = RegistryStore.CFCA;
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
    public void modifyAffiliation(String oldAffiliation, String newAffiliation, boolean force, boolean isRegistrar) throws RAServerException {

    }
}
