package com.cfca.ra.command.internal;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description 解析raw之后的对象
 * @CodeReviewer
 * @since v3.0.0
 */
public class ParsedUrl {
    private final String scheme;
    private final String host;
    private final String username;
    private final String password;

    public ParsedUrl(String scheme, String host, String username, String password) {
        this.scheme = scheme;
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public String getScheme() {
        return scheme;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "ParsedUrl{" +
                "scheme='" + scheme + '\'' +
                ", host='" + host + '\'' +
                ", username='" + username + '\'' +
                ", password='hide'"+
                '}';
    }
}
