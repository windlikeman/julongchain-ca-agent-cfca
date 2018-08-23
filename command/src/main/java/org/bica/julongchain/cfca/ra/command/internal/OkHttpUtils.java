package org.bica.julongchain.cfca.ra.command.internal;

import static org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangchong
 * @create 2018/5/22
 * @Description OkHttp 网路工具类,用于封装网络通信功能
 * @CodeReviewer
 * @since v3.0.0
 */
public class OkHttpUtils {

    private static Logger logger = LoggerFactory.getLogger(OkHttpUtils.class);

    @SuppressWarnings("deprecation")
    public static OkHttpClient getUnsafeOkHttpsClient() {
        try {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                //打印retrofit日志
                logger.info("OkHttpUtils@HttpLoggingInterceptor<<<<<<retrofitBack:\n{}", message);
            });

            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

            final Interceptor headerInterceptor = chain -> {
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("Content-Type", "application/json;charset=utf-8")
                        .addHeader("Accept", "application/json")
                        .addHeader("ca-protocol-version", "0.0.1")
                        .build();
                return chain.proceed(request);
            };

            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            } };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();

            okHttpClient = okHttpClient.newBuilder().sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(ALLOW_ALL_HOSTNAME_VERIFIER)
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(headerInterceptor)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS).build();

            return okHttpClient;
        } catch (Exception e) {
            logger.error("OkHttpUtils@getUnsafeOkHttpsClient : failed", e);
            throw new RuntimeException(e);
        }

    }
}
