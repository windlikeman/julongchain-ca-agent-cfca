package com.cfca.ra.repository;

import com.cfca.ra.RAServerException;
import com.cfca.ra.register.IUser;
import com.cfca.ra.register.UserInfo;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description 注册用户信息数据库接口,用于对接用户注册数据库
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public interface IRegistryStore {
    IUser getUser(String id, String[] attrs) throws RAServerException;

    void insertUser(UserInfo user) throws RAServerException;

    boolean containsUser(String id, String[] attrs) throws RAServerException;
}
