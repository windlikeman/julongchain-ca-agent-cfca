package com.cfca.ra.ca;

import com.cfca.ra.RAServerException;
import com.cfca.ra.register.IUser;
import com.cfca.ra.register.UserInfo;
import com.cfca.ra.repository.RegistryStore;

import java.util.Map;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description IUserRegistry是用于检索用户和组的API
 * @CodeReviewer
 * @since v3.0.0
 */
public interface IUserRegistry {

    RegistryStore getRegistryStore();

    boolean containsUser(String id, String[] attrs) throws RAServerException;

    IUser getUser(String id, String[] attrs) throws RAServerException;

    void insertUser(UserInfo user) throws RAServerException;

    void updateUser(UserInfo userInfo) throws RAServerException;

    UserInfo deleteUser(String id) throws RAServerException;

    Affiliation getAffiliation(String name) throws RAServerException;

    Affiliation[] getAllAffiliations(String name) throws RAServerException;

    void insertAffiliation(String name, int level) throws RAServerException;

    /**
     * GetProperties returns the properties by name from the database
     * @param names
     * @return
     * @throws RAServerException
     */
    Map<String, String> getProperties(String... names) throws RAServerException;
}
