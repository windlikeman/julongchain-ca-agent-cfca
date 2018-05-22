package com.cfca.ra.register;

import com.cfca.ra.RAServerException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/21
 * @Description 默认的IUser接口实现
 * @CodeReviewer
 * @since v3.0.0
 */
public class DefaultUser implements IUser {
    private final UserInfo userInfo;

    public DefaultUser(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String getName() {
        return userInfo.getName();
    }

    @Override
    public String getPassWord() {
        return userInfo.getPass();
    }

    @Override
    public String getType() {
        return userInfo.getType();
    }

    @Override
    public int getMaxEnrollments() {
        return userInfo.getMaxEnrollments();
    }

    @Override
    public List<String> getAffiliationPath() {
        return new ArrayList<String>();
    }

    @Override
    public UserAttrs getAttribute(String name) throws RAServerException {
        final List<UserAttrs> attributes = userInfo.getAttributes();
        UserAttrs result = null;
        for (UserAttrs attr : attributes) {
            if (name.equals(attr.getName())) {
                result = attr;
                break;
            }
        }
        return result;
    }

    @Override
    public List<UserAttrs> getAttributes(String[] attrNames) throws RAServerException {
        final List<UserAttrs> attributes = userInfo.getAttributes();
        List<UserAttrs> result = new ArrayList<>();
        for (UserAttrs attr : attributes) {
            for (String attrName : attrNames) {
                if (attrName.equals(attr.getName())) {
                    result.add(attr);
                }
            }
        }
        return result;
    }

    @Override
    public void modifyAttributes(List<UserAttrs> attrs) throws RAServerException {
        final List<UserAttrs> attributes = userInfo.getAttributes();
        for (UserAttrs attr : attributes) {
            for (UserAttrs modified : attrs) {
                if (attr.getName().equals(modified.getName())) {
                    attributes.remove(attr);
                    attributes.add(modified);
                }
            }
        }
    }

    @Override
    public void revoke() throws RAServerException {

    }
}
