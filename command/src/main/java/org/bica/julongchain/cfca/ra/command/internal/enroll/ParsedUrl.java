package org.bica.julongchain.cfca.ra.command.internal.enroll;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description 解析raw之后的对象
 * @CodeReviewer
 * @since v3.0.0
 */
class ParsedUrl {
    private final String scheme;
    private final String host;
    private final String username;
    private final String password;

    ParsedUrl(String scheme, String host, String username, String password) {
        this.scheme = scheme;
        this.host = host;
        this.username = username;
        this.password = password;
    }

    String getScheme() {
        return scheme;
    }

    String getHost() {
        return host;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ParsedUrl [scheme=");
        builder.append(scheme);
        builder.append(", host=");
        builder.append(host);
        builder.append(", username=");
        builder.append(username);
        builder.append(", password=");
        builder.append(password);
        builder.append("]");
        return builder.toString();
    }

   
    
}
