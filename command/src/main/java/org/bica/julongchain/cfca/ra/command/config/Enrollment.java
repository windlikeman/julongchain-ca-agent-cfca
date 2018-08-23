package org.bica.julongchain.cfca.ra.command.config;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description Enrollment section used to enroll an identity with fabric-ca
 *              server
 * @CodeReviewer
 * @since v3.0.0
 */
public class Enrollment {

    /**
     * 保证用户唯一性的用户名随机ID
     */
    private String profile;
    /**
     * Label to use in HSM operations
     */
    private String label;

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getProfile() {
        return profile;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "Enrollment{" + "profile='" + profile + '\'' + ", label='" + label + '\'' + '}';
    }
}
