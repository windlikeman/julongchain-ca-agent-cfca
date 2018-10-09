package org.bica.julongchain.cfca.ra.command.internal;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description ClientIdentity 用于标识客户端
 * @CodeReviewer
 * @since v3.0.0
 */
public class ClientIdentity {
    private String name;
    private String type;
    private String affiliation;
    private int maxenrollments;
    private List<ClientAttribute> attributes;

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public void setMaxenrollments(int maxenrollments) {
        this.maxenrollments = maxenrollments;
    }

    public void setAttributes(List<ClientAttribute> attributes) {
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public int getMaxenrollments() {
        return maxenrollments;
    }

    public List<ClientAttribute> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "ClientIdentity{" + "name='" + name + '\'' + '}';
    }
}
