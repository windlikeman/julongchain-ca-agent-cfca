package com.cfca.ra.register;

import com.cfca.ra.RAServerException;

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
     * Returns the enrollment ID of the user
     */
    String getName();

    /**
     * Returns the enrollment ID of the user
     *
     * @return
     */
    String getType();

    /**
     * Return the max enrollments of the user
     * @return
     */
    int getMaxEnrollments();

    /**
     * Return the max enrollments of the user
     * @param password
     * @param caMaxEnrollment
     * @throws RAServerException
     */
    void login(String password, int caMaxEnrollment) throws RAServerException;

    /**
     * Get the complete path for the user's affiliation.
     * @return
     */
    List<String> getAffiliationPath();

    /**
     * GetAttribute returns the value for an attribute name
     * @param name
     * @return
     * @throws RAServerException
     */
    UserAttrs getAttribute(String name) throws RAServerException;

    /**
     * GetAttributes returns the requested attributes
     * @param attrNames
     * @return
     * @throws RAServerException
     */
    List<UserAttrs> getAttributes(String... attrNames) throws RAServerException;

    /**
     * ModifyAttributes adds, removes, or deletes attribute
     * @param attrs
     * @throws RAServerException
     */
    void modifyAttributes(List<UserAttrs> attrs) throws RAServerException;

    /**
     * Revoke will revoke the user, setting the state of the user to be -1
     * @throws RAServerException
     */
    void revoke() throws RAServerException;
}
