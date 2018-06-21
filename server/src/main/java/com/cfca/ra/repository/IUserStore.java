package com.cfca.ra.repository;

import com.cfca.ra.RAServerException;
import com.cfca.ra.register.IUser;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description 用户信息数据库接口, 用于检索用户, 更新用户信息
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public interface IUserStore {
    void updateUserStore(IUser user, String secret) throws RAServerException;

    boolean containsUser(String user) throws RAServerException;

    String getUser(String user) throws RAServerException;
}
