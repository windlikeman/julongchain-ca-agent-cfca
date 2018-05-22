package com.cfca.ra.ca.register;

import com.cfca.ra.beans.RegistrationRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
public class UserInfo {
    private final String name;
    private final String pass;
    private final String type;
    private final String affiliation;
    private final List<UserAttrs> attributes;
    private final int maxEnrollments;
    private final int state;

    public UserInfo(String name, String pass, String type, String affiliation, List<UserAttrs> attributes, int maxEnrollments, int state) {
        this.name = name;
        this.pass = pass;
        this.type = type;
        this.affiliation = affiliation;
        this.attributes = attributes;
        this.maxEnrollments = maxEnrollments;
        this.state = state;
    }

    public UserInfo(RegistrationRequest req, int state) {
        this.name = req.getName();
        this.pass = req.getSecret();
        this.type = req.getType();
        this.affiliation = req.getAffiliation();
        this.attributes = req.getAttribute();
        this.maxEnrollments = req.getMaxEnrollments();
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public String getPass() {
        return pass;
    }

    public String getType() {
        return type;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public List<UserAttrs> getAttributes() {
        return attributes;
    }

    public int getMaxEnrollments() {
        return maxEnrollments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return maxEnrollments == userInfo.maxEnrollments &&
                state == userInfo.state &&
                Objects.equals(name, userInfo.name) &&
                Objects.equals(pass, userInfo.pass) &&
                Objects.equals(type, userInfo.type) &&
                Objects.equals(affiliation, userInfo.affiliation) &&
                Objects.equals(attributes, userInfo.attributes);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, pass, type, affiliation, attributes, maxEnrollments, state);
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", pass='" + pass + '\'' +
                ", type='" + type + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", attributes=" + attributes +
                ", maxEnrollments=" + maxEnrollments +
                ", state=" + state +
                '}';
    }
}
