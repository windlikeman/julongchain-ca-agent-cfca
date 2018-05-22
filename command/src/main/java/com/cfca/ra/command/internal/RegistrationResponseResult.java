package com.cfca.ra.command.internal;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
class RegistrationResponseResult {
    private final String redentials;

    RegistrationResponseResult(String redentials) {
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
