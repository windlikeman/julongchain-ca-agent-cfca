package com.cfca.ra.command.internal;

/**
 * @author zhangchong
 * @create 2018/5/13
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
class Userinfo {
    private final String username;
    private final String password;

    public Userinfo(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    @Override
    public String toString() {
        return "Userinfo{" +
                "username='" + username + '\'' +
                '}';
    }
}
