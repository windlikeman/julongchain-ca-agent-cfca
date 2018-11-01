package org.bica.julongchain.cfca.ra.ca;

import org.bica.julongchain.cfca.ra.RAServerException;
import org.bica.julongchain.cfca.ra.register.IUser;
import org.bica.julongchain.cfca.ra.register.UserInfo;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description IUserRegistry是用于检索用户和组的API
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public interface IUserRegistry {

    boolean containsUser(String id, String[] attrs) throws RAServerException;

    IUser getUser(String id, String[] attrs) throws RAServerException;

    void insertUser(UserInfo user) throws RAServerException;
}
