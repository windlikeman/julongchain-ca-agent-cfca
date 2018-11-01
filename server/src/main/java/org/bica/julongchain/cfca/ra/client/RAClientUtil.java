package org.bica.julongchain.cfca.ra.client;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfca.ra.toolkit.RAClient;

/**
 * @author qazhang
 * @Description RATK客户端工具包
 * @CodeReviewer zhangchong
 *
 */
public final class RAClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(RAClientUtil.class);

    private static final File RATK_FILE = new File("config/ratk.properties");

    private static final String HTTPS = "https";

    private static volatile RAClientUtil INSTANCE = null;

    public static RAClientUtil getInstance() throws Exception {
        if (INSTANCE == null) {
            synchronized (RAClientUtil.class) {
                if (INSTANCE == null) {
                    RAClientUtil instance = new RAClientUtil();
                    INSTANCE = instance;
                }
            }
        }
        return INSTANCE;
    }

    RAClient client;

    RAClientBean clientBean;

    /**
     * 日志告警时间阈值,单位毫秒
     */
    public static int warningTime = 5000;

    private RAClientUtil() throws Exception {
        super();
        buildRAClient(RATK_FILE);
    }

    public RAClient getClient() {
        return client;
    }

    private final RAClient buildRAClient(final File file) throws Exception {
        logger.info("ratkLoadPropertiesFile={}", file.getAbsoluteFile());

        if (!file.exists()) {
            throw new Exception("not found: ratkLoadPropertiesFile=" + file.getAbsoluteFile());
        }
        if (!file.isFile()) {
            throw new Exception("not file: ratkLoadPropertiesFile=" + file.getAbsoluteFile());
        }

        final Properties properties = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            properties.load(fis);
        } catch (Exception e) {
            logger.error("ratkLoadPropertiesFile load failed, filePath=" + file.getAbsolutePath(), e);
            throw new Exception("ratkLoadPropertiesFile load failed", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    logger.error("ratkLoadPropertiesFile closed failed, filePath=" + file.getAbsolutePath(), e);
                }
            }
        }

        RAClientBean bean;
        try {
            bean = new RAClientBean(properties);
        } catch (Exception e) {
            logger.error("properties={}", properties);
            logger.error("ratkLoadPropertiesFile buildRAClientBean failed, filePath=" + file.getAbsolutePath(), e);
            throw new Exception("ratkLoadPropertiesFile buildRAClientBean failed", e);
        }
        logger.info("bean={}", bean);

        URL url;
        RAClient client;
        try {
            url = new URL(bean.url);
            client = new RAClient(bean.url, bean.connectTimeout, bean.readTimeout);
        } catch (Exception e) {
            throw new Exception("RAClient new failed: " + bean, e);
        }
        if (HTTPS.equals(url.getProtocol())) {
            try {
                client.initSSL(bean.keyStorePath, bean.keyStorePassword, bean.trustStorePath, bean.trustStorePassword);
            } catch (Exception e) {
                throw new Exception("RAClient initSSL failed: " + bean, e);
            }
        }

        warningTime = bean.warningTime;
        this.clientBean = bean;
        this.client = client;
        return client;
    }

}
