package org.bica.julongchain.cfca.ra.register;

import org.bica.julongchain.cfca.ra.RAServerException;
import org.bica.julongchain.cfca.ra.ca.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/21
 * @Description 默认的IUser接口实现
 * @CodeReviewer helonglong
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
    public Attribute getAttribute(String name) throws RAServerException {
        final List<Attribute> attributes = userInfo.getAttributes();
        Attribute result = null;
        for (Attribute attr : attributes) {
            if (name.equals(attr.getName())) {
                result = attr;
                break;
            }
        }
        return result;
    }

    @Override
    public List<Attribute> getAttributes(List<String> attrNames) throws RAServerException {
        final List<Attribute> attributes = userInfo.getAttributes();
        List<Attribute> result = new ArrayList<>();
        for (Attribute attr : attributes) {
            for (String attrName : attrNames) {
                if (attrName.equals(attr.getName())) {
                    result.add(attr);
                }
            }
        }
        return result;
    }

    @Override
    public void modifyAttributes(List<Attribute> attrs) throws RAServerException {
        final List<Attribute> attributes = userInfo.getAttributes();
        for (Attribute attr : attributes) {
            for (Attribute modified : attrs) {
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
