package com.cfca.ra.utils;

import com.cfca.ra.RAServerException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangchong
 * @create 2018/6/8
 * @Description 用于处理鉴权信息的工具类
 * @CodeReviewer
 * @since
 */
public class AuthUtils {
    private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);
    public static byte[] marshal(Object request) throws RAServerException {
        try {
            final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            final String s = gson.toJson(request);
            logger.info("marshal<<<<<<json    : " + s);
            final byte[] encode = Base64.encode(s.getBytes("UTF-8"));
            logger.info("marshal<<<<<<encode  : " + Hex.toHexString(encode));
            return encode;
        } catch (Exception e) {
            throw new RAServerException(RAServerException.REASON_CODE_SERVER_EXCEPTION, "marshal failed", e);
        }
    }
}
