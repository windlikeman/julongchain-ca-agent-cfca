package com.cfca.ra.ca;

import java.util.List;
import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
public class CAConfigRegistry {
    private final int maxEnrollments;
    private final List<CAConfigIdentity> identities;

    public CAConfigRegistry(int maxEnrollments, List<CAConfigIdentity> identities) {
        this.maxEnrollments = maxEnrollments;
        this.identities = identities;
    }

    public int getMaxEnrollments() {
        return maxEnrollments;
    }

    public List<CAConfigIdentity>getIdentities() {
        return identities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CAConfigRegistry that = (CAConfigRegistry) o;
        return maxEnrollments == that.maxEnrollments &&
                Objects.equals(identities, that.identities);
    }

    @Override
    public int hashCode() {

        return Objects.hash(maxEnrollments, identities);
    }

    @Override
    public String toString() {
        return "CAConfigRegistry{" +
                "maxEnrollments=" + maxEnrollments +
                ", identities=" + identities +
                '}';
    }
}
