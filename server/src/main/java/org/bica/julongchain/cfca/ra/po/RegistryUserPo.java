package org.bica.julongchain.cfca.ra.po;

import org.bica.julongchain.cfca.ra.ca.Attribute;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


/**
 * @author zhangchong
 * @Create 2018/7/27 17:18
 * @CodeReviewer
 * @Description
 * @since
 */
@Document(collection = "t_registryuser")
public class RegistryUserPo {
    private final String caName;
    private final String name;
    private final String pass;
    private final String type;
    private final String affiliation;
    private final List<Attribute> attributes;
    private final int maxEnrollments;
    private final int state;

    public RegistryUserPo(String caName, String name, String pass, String type, String affiliation, List<Attribute> attributes, int maxEnrollments, int state) {
        super();
        this.caName = caName;
        this.name = name;
        this.pass = pass;
        this.type = type;
        this.affiliation = affiliation;
        this.attributes = attributes;
        this.maxEnrollments = maxEnrollments;
        this.state = state;
    }

    public String getCaName() {
        return caName;
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

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public int getMaxEnrollments() {
        return maxEnrollments;
    }

    public int getState() {
        return state;
    }

    @Override
    public String toString() {
        return "RegistryUserPo{" +
                "caName='" + caName + '\'' +
                ", name='" + name + '\'' +
                ", pass='" + pass + '\'' +
                ", type='" + type + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", attributes=" + attributes +
                ", maxEnrollments=" + maxEnrollments +
                ", state=" + state +
                '}';
    }
}
