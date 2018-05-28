package com.cfca.ra.register;

import com.cfca.ra.RAServerException;
import com.cfca.ra.ca.Attribute;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/14
 * @Description 用户操作接口
 * @CodeReviewer
 * @since v3.0.0
 */
public interface IUser {

    /**
     * 返回用户的身份ID
     *
     * @return 用户的身份ID
     */
    String getName();
    /**
     * 返回用户的密码
     * @return 用户的密码
     */
    String getPassWord();

    /**
     * 返回用户的类型
     *
     * @return 用户的类型
     */
    String getType();

    /**
     * 返回用户的最大注册次数
     * @return 用户的最大注册次数
     */
    int getMaxEnrollments();

    /**
     * 获取用户所属机构的完整路径
     * @return 路径上的所有节点
     */
    List<String> getAffiliationPath();

    /**
     * GetAttribute returns the value for an attribute name
     * @param name
     * @return
     * @throws RAServerException
     */
    Attribute getAttribute(String name) throws RAServerException;

    /**
     * GetAttributes returns the requested attributes
     * @param attrNames
     * @return
     * @throws RAServerException
     */
    List<Attribute> getAttributes(List<String> attrNames) throws RAServerException;

    /**
     * ModifyAttributes adds, removes, or deletes attribute
     * @param attrs
     * @throws RAServerException
     */
    void modifyAttributes(List<Attribute> attrs) throws RAServerException;

    /**
     * Revoke will revoke the user, setting the state of the user to be -1
     * @throws RAServerException
     */
    void revoke() throws RAServerException;

}
