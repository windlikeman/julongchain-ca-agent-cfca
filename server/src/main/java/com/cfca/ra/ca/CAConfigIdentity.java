package com.cfca.ra.ca;

import java.util.Map;
import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description 用于标识 CA 中注册信息
 * @CodeReviewer
 * @since v3.0.0
 */
public class CAConfigIdentity {
    /**
     * username
     */
   private final String name;
    /**
     * password
     */
    private final  String pass;
    /**
     *
     */
    private final String type;
    private final String affiliation;
    private final int maxEnrollments;
    private final Map<String, String> attrs;

    public CAConfigIdentity(String name, String pass, String type, String affiliation, int maxEnrollments, Map<String, String> attrs) {
        this.name = name;
        this.pass = pass;
        this.type = type;
        this.affiliation = affiliation;
        this.maxEnrollments = maxEnrollments;
        this.attrs = attrs;
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

    public int getMaxEnrollments() {
        return maxEnrollments;
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    @Override
    public String toString() {
        return "CAConfigIdentity{" +
                "name='" + name + '\'' +
                ", pass='" + pass + '\'' +
                ", type='" + type + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", maxEnrollments=" + maxEnrollments +
                ", attrs=" + attrs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        final CAConfigIdentity that = (CAConfigIdentity) o;
        return maxEnrollments == that.maxEnrollments &&
                Objects.equals(name, that.name) &&
                Objects.equals(pass, that.pass) &&
                Objects.equals(type, that.type) &&
                Objects.equals(affiliation, that.affiliation) &&
                Objects.equals(attrs, that.attrs);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, pass, type, affiliation, maxEnrollments, attrs);
    }
}
