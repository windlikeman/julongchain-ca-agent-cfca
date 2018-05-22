package com.cfca.ra.command.utils;

import javax.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/17
 * @Description 证书工具类, 用于操作证书等
 * @CodeReviewer
 * @since v3.0.0
 */
public class CertUtils {
    public static List<X509Certificate> buildCertsFromP7b(byte[] chain) {
        final ArrayList<X509Certificate> result = new ArrayList<>();
        return result;
    }

    public static boolean isCA(X509Certificate cert) {
        return false;
    }

}
