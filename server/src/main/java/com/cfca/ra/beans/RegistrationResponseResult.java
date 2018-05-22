package com.cfca.ra.beans;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description
 * @CodeReviewer
 * @since
 */
public class RegistrationResponseResult {
    private final String redentials;

    public RegistrationResponseResult(String redentials) {
        this.redentials = redentials;
    }

    public String getRedentials() {
        return redentials;
    }

    @Override
    public String toString() {
        return "RegistrationResponseResult{" +
                "redentials='" + redentials + '\'' +
                '}';
    }
}
