package org.bica.julongchain.cfca.ra.ca;

import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description CA 信息,它包括发放登记证书(ECerts)和交易证书(TCerts)时使用的密钥和证书文件
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class CAInfo {

    /**
     * Certificate Authority name
     */
    private String name;

    /**
     * PEM-encoded CA chain file
     */
    private String chainfile;

    public CAInfo() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChainfile(String chainfile) {
        this.chainfile = chainfile;
    }

    public String getName() {
        return name;
    }

    public String getChainfile() {
        return chainfile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CAInfo caInfo = (CAInfo) o;
        return Objects.equals(name, caInfo.name) &&
                Objects.equals(chainfile, caInfo.chainfile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, chainfile);
    }

    @Override
    public String toString() {
        return "CAInfo{" +
                "name='" + name + '\'' +
                ", chainfile='" + chainfile + '\'' +
                '}';
    }
}
