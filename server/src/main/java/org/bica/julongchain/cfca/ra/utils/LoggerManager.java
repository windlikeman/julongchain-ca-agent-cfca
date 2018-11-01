package org.bica.julongchain.cfca.ra.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author qazhang
 * @Description 工具包日志管理类
 * @CodeReviewer v4500 wangmeng/zhangchong/qianxiaobo/jianzhidong/wanghuishu
 *
 */
public final class LoggerManager {
    public final static Logger debugLogger = LoggerFactory.getLogger("debugLogger");;
    public final static Logger systemLogger = LoggerFactory.getLogger("systemLogger");
    public final static Logger exceptionLogger = LoggerFactory.getLogger("exceptionLogger");

    static {

        LoggerManager.debugLogger.debug("debugLogger: test-debug");
        LoggerManager.debugLogger.info("debugLogger: test-info");
        LoggerManager.debugLogger.warn("debugLogger: test-warn");
        LoggerManager.debugLogger.error("debugLogger: test-error");

        LoggerManager.systemLogger.debug("systemLogger: test-debug");
        LoggerManager.systemLogger.info("systemLogger:  test-info");
        LoggerManager.systemLogger.warn("systemLogger:  test-warn");
        LoggerManager.systemLogger.error("systemLogger:  test-error");

        LoggerManager.exceptionLogger.debug("exceptionLogger: test-debug");
        LoggerManager.exceptionLogger.info("exceptionLogger: test-info");
        LoggerManager.exceptionLogger.warn("exceptionLogger: test-warn");
        LoggerManager.exceptionLogger.error("exceptionLogger: test-error");
    }

}
