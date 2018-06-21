package com.cfca.ra.gettcert;

import org.bouncycastle.asn1.x509.Extension;

import java.util.List;
import java.util.Map;

/**
 * @author zhangchong
 * @create 2018/5/25
 * @Description
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class ExtensionsAndks {

    private final List<Extension> extensions;
    private final Map<String, byte[]> ks;

    public ExtensionsAndks(List<Extension> extensions, Map<String, byte[]> ks) {
        this.extensions = extensions;
        this.ks = ks;
    }

    public List<Extension> getExtensions() {
        return extensions;
    }

    public Map<String, byte[]> getKs() {
        return ks;
    }
}
