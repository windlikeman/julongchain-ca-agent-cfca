package demo;

import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;

import com.google.gson.JsonObject;

public final class ChainCode {

    static final Provider provider = new BouncyCastleProvider();

    public static String newRequest() throws Exception {

        final AlgorithmParameterSpec sm2p256v1 = new ECNamedCurveGenParameterSpec("sm2p256v1");

        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", provider);
        generator.initialize(sm2p256v1);
        KeyPair keypair = generator.generateKeyPair();

        PKCS10CertificationRequestBuilder pkcs10Builder = new JcaPKCS10CertificationRequestBuilder(
                new X500Name("CN=vic,C=CN"), keypair.getPublic());

        ContentSigner contentSigner = new JcaContentSignerBuilder("SM3WITHSM2").setProvider(provider)
                .build(keypair.getPrivate());
        PKCS10CertificationRequest csr = pkcs10Builder.build(contentSigner);
        final byte[] base64Encoded = Base64.encode(csr.getEncoded());
        final String requestText = new String(base64Encoded);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("request", requestText);
//		jsonObject.addProperty("profile", "H093580286");
        jsonObject.addProperty("label", "");
        jsonObject.addProperty("caname", "OCA1");

        return jsonObject.toString();

    }

    public static void call(boolean console) throws Exception {
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        try {
            String p10Request = newRequest();

            if (console) {
                System.err.println(p10Request);
            }

            byte[] bodyData = p10Request.getBytes();

            final URL url = new URL("http://192.168.123.24:8001/enroll");

            Builder builder = RequestConfig.custom();
            builder.setSocketTimeout(15000);
            builder.setConnectTimeout(15000);
            builder.setConnectionRequestTimeout(15000);

            final HttpClientBuilder httpBuilder = HttpClients.custom().setDefaultRequestConfig(builder.build());

            final CloseableHttpClient httpclient = httpBuilder.build();
            httpPost = new HttpPost(url.toURI());
//		httpPost.addHeader("Authorization", "Basic YWRtaW46MTIzNA==");
            httpPost.addHeader("Authorization", "Basic YWRtaW46MTIzNA==:e1oiQeVrSa");
            httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");
            httpPost.addHeader("Accept", "application/json");
            httpPost.setEntity(new ByteArrayEntity(bodyData));
            response = httpclient.execute(httpPost);
            if (console) {
                System.err.println(response.getStatusLine());
            }

            byte[] data = EntityUtils.toByteArray(response.getEntity());
            if (console) {
                System.err.println(new String(data));
                System.err.println("responseLength: " + data.length);
            }
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
                httpPost = null;
            }
            if (response != null) {
                response.close();
                response = null;
            }

        }
    }


    public static void callGetCainfo(boolean console) throws Exception {
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        try {

//            final URL url = new URL("http://192.168.123.24:8001/cainfo");
            final URL url = new URL("http://localhost:8089/cainfo");
            Builder builder = RequestConfig.custom();
            builder.setSocketTimeout(5000);
            builder.setConnectTimeout(5000);
            builder.setConnectionRequestTimeout(5000);

            final HttpClientBuilder httpBuilder = HttpClients.custom().setDefaultRequestConfig(builder.build());

            final CloseableHttpClient httpclient = httpBuilder.build();
            httpPost = new HttpPost(url.toURI());
//		httpPost.addHeader("Authorization", "Basic YWRtaW46MTIzNA==");
//            httpPost.addHeader("Authorization", "Basic YWRtaW46MTIzNA==:e1oiQeVrSr");
            httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");
            httpPost.addHeader("Accept", "application/json");
            byte[] bodyData = "{\"caname\":\"CFCA\"}".getBytes("UTF-8");
            httpPost.setEntity(new ByteArrayEntity(bodyData));

            response = httpclient.execute(httpPost);
            if (console) {
                System.err.println(response.getStatusLine());
            }

            byte[] data = EntityUtils.toByteArray(response.getEntity());
            if (console) {
                System.err.println(new String(data));
                System.err.println("responseLength: " + data.length);
            }
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
                httpPost = null;
            }
            if (response != null) {
                response.close();
                response = null;
            }

        }
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 100; i++) {
            call(true);
            System.out.println("############" + i);
        }

    }

}
