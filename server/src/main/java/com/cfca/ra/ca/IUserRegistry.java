package com.cfca.ra.ca;

import com.cfca.ra.RAServerException;
import com.cfca.ra.ca.register.IUser;
import com.cfca.ra.ca.register.UserInfo;

import java.util.Map;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description IUserRegistry is the API for retreiving users and groups
 * @CodeReviewer
 * @since v3.0.0
 */
public interface IUserRegistry {

    IUser getUser(String id, String[] attrs) throws RAServerException;

    void insertUser(UserInfo user) throws RAServerException;

    void updateUser(UserInfo userInfo) throws RAServerException;

    UserInfo deleteUser(String id) throws RAServerException;

    Affiliation getAffiliation(String name) throws RAServerException;

    Affiliation[] getAllAffiliations(String name) throws RAServerException;

    void insertAffiliation(String name, String prekey, int level) throws RAServerException;

    /**
     * GetProperties returns the properties by name from the database
     * @param names
     * @return
     * @throws RAServerException
     */
    Map<String, String> getProperties(String... names) throws RAServerException;

    UserInfo[] getUserLessThanLevel(int version) throws RAServerException;

    UserInfo[] getFilteredUsers(String affiliation, String types) throws RAServerException;

    void deleteAffiliation(String name, boolean force, boolean identityRemoval, boolean isRegistrar) throws RAServerException;

    void modifyAffiliation(String oldAffiliation, String newAffiliation , boolean force, boolean isRegistrar) throws RAServerException;

}
