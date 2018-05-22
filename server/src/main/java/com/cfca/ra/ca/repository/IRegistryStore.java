package com.cfca.ra.ca.repository;

import com.cfca.ra.RAServerException;
import com.cfca.ra.ca.register.IUser;
import com.cfca.ra.ca.register.UserInfo;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description
 * @CodeReviewer
 * @since
 */
public interface IRegistryStore {
    IUser getUser(String id, String[] attrs) throws RAServerException;

    void insertUser(UserInfo user) throws RAServerException;
}
