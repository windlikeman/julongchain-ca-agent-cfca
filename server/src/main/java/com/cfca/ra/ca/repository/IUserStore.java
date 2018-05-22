package com.cfca.ra.ca.repository;

import com.cfca.ra.RAServerException;
import com.cfca.ra.ca.register.IUser;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description
 * @CodeReviewer
 * @since
 */
public interface IUserStore {
    void updateUserStore(IUser user, String secret) throws RAServerException;

    boolean containsUser(String user) throws RAServerException;

    String getUser(String user) throws RAServerException;
}
