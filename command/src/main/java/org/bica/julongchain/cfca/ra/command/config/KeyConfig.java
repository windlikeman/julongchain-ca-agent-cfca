package org.bica.julongchain.cfca.ra.command.config;

import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description 生成 CSR 带的签名算法和密钥长度
 * @CodeReviewer
 * @since v3.0.0
 */
public class KeyConfig {
    private String algo;
    private int size;

    public void setAlgo(String algo) {
        this.algo = algo;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getAlgo() {
        return algo;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "KeyConfig{" + "algo='" + algo + '\'' + ", size=" + size + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KeyConfig keyConfig = (KeyConfig) o;
        return size == keyConfig.size && Objects.equals(algo, keyConfig.algo);
    }

    @Override
    public int hashCode() {

        return Objects.hash(algo, size);
    }
}
