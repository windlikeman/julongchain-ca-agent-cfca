package org.bica.julongchain.cfca.ra.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * @Author zhangchong
 * @Description 系统运行环境信息
 * @create 2018/7/11
 * @CodeReviewer zhangqingan
 * @since v3.0.0.2
 */
public final class Environments {

    private static final Logger LOG = LoggerFactory.getLogger(Environments.class);

    private static final String COMPANY_NAME = "China Financial Certification Authority";
    private static final String PRODUCT_NAME = "bica julong blockchain";
    private static final String VERSION = "v3.0.0.4";

    private volatile static Environments environments;

    public static Environments environments() {
        if (environments == null) {
            synchronized (Environments.class) {
                if (environments == null) {
                    Environments env = new Environments();
                    LOG.info(env.buildEnvironments());
                    environments = env;
                }
            }
        }
        return environments;
    }

    final StringBuilder appendOS(final StringBuilder builder) {
        builder.append("\n");
        builder.append("\n ===================OSx info===================");
        builder.append("\n 操作系统名称: " + System.getProperty("os.name"));
        builder.append("\n 操作系统架构: " + System.getProperty("os.arch"));
        builder.append("\n 操作系统版本: " + System.getProperty("os.version"));
        builder.append("\n 操作系统用户名: " + System.getProperty("user.name"));
        builder.append("\n user.home: " + System.getProperty("user.home"));
        builder.append("\n user.dir: " + System.getProperty("user.dir"));
        builder.append("\n user.timezone: ").append(System.getProperty("user.timezone"));
        builder.append("\n user.language: ").append(System.getProperty("user.language"));
        return builder;
    }

    final StringBuilder appendJDK(final StringBuilder builder) {
        builder.append("\n");
        builder.append("\n ===================JDK info===================");
        builder.append("\n JDK主版本: " + System.getProperty("java.version"));
        builder.append("\n JDK供应商: " + System.getProperty("java.vendor"));
        builder.append("\n JDK主目录: " + System.getProperty("java.home"));
        builder.append("\n JDK类路径: " + System.getProperty("java.class.path"));
        builder.append("\n JDK动态库: " + System.getProperty("java.library.path"));
        return builder;
    }

    final StringBuilder appendJVM(final StringBuilder builder) {
        builder.append("\n");
        builder.append("\n ===================JVM info===================");
        builder.append("\n java.vm.vendor=").append(System.getProperty("java.vm.vendor"));
        builder.append("\n java.vm.name=").append(System.getProperty("java.vm.name"));
        builder.append("\n java.vm.version=").append(System.getProperty("java.vm.version"));
        builder.append("\n java.home=").append(System.getProperty("java.home"));
        builder.append("\n java.class.path=").append(System.getProperty("java.class.path"));
        builder.append("\n java.class.version=").append(System.getProperty("java.class.version"));
        builder.append("\n java.runtime.name=").append(System.getProperty("java.runtime.name"));
        builder.append("\n java.runtime.version=").append(System.getProperty("java.runtime.version"));
        builder.append("\n java.library.path=").append(System.getProperty("java.library.path"));
        builder.append("\n java.version=").append(System.getProperty("java.version"));
        builder.append("\n sun.arch.data.model=").append(System.getProperty("sun.arch.data.model"));
        builder.append("\n sun.os.patch.level=").append(System.getProperty("sun.os.patch.level"));
        builder.append("\n ");

        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        builder.append("\n JVM版本: " + System.getProperty("java.vm.version"));
        builder.append("\n JVM名称: " + System.getProperty("java.vm.name"));
        builder.append("\n JVM参数: " + inputArguments);
        return builder;

    }

    final StringBuilder appendME(final StringBuilder builder) {
        builder.append("\n");
        builder.append("\n ===================Server info===================");
        builder.append("\n 应用厂商: " + COMPANY_NAME);
        builder.append("\n 应用名称: " + PRODUCT_NAME);
        builder.append("\n 应用版本: " + VERSION);
        return builder;
    }

    public final String buildEnvironments() {
        final StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append("\n");
        appendOS(builder);
        appendJDK(builder);
        appendJVM(builder);
        appendME(builder);
        builder.append("\n");
        builder.append("\n");
        return builder.toString();

    }

}
