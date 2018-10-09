package org.bica.julongchain.cfca.ra.command.internal.enroll;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bica.julongchain.cfca.ra.command.CommandException;
import org.bica.julongchain.cfca.ra.command.internal.ClientConfig;
import org.bica.julongchain.cfca.ra.command.internal.OkHttpUtils;
import org.bica.julongchain.cfca.ra.command.internal.ServerResponseError;
import org.bica.julongchain.cfca.ra.command.utils.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

import com.google.gson.Gson;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 处理 Enrollment 命令与服务器之间的网络交互, 发送和接受网络消息
 * @CodeReviewer
 * @since v3.0.0
 */
public class EnrollmentComms {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentComms.class);
    private final ClientConfig clientConfig;

    public EnrollmentComms(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    private interface EnrollService {
        /**
         * 使用 post 请求提交 enroll数据
         *
         * @param request
         * @param auth
         * @return EnrollmentResponseNet
         */
        @POST("enroll")
        Call<EnrollmentResponseNet> postWithEnrollData(@Body EnrollmentRequestNet request, @Header("Authorization") String auth);

        /**
         * 使用 post 请求提交 enroll数据
         *
         * @param request
         * @return EnrollmentResponseNet
         */
        @POST("enroll")
        Call<EnrollmentResponseNet> postWithEnrollData(@Body EnrollmentRequestNet request);
    }

    /**
     * @param enrollmentRequest
     *            申请证书的数据
     * @return EnrollmentResponseNet
     * @throws IOException
     *             网络请求失败
     */
    public EnrollmentResponseNet request(EnrollmentRequestNet enrollmentRequest, String auth) throws CommandException {
        final String s = new Gson().toJson(enrollmentRequest);
        logger.info("EnrollmentComms@request : {}", Base64.toBase64String(s.getBytes()));
        final String baseurl = clientConfig.getUrl();
        if (StringUtils.isBlank(baseurl)) {
            throw new CommandException("baseurl is empty");
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseurl)
                .client(OkHttpUtils.getUnsafeOkHttpsClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EnrollService service = retrofit.create(EnrollService.class);

        final Call<EnrollmentResponseNet> call;
        if (StringUtils.isEmpty(auth)) {
            call = service.postWithEnrollData(enrollmentRequest);
        } else {
            call = service.postWithEnrollData(enrollmentRequest, auth);
        }

        call.request();
        return syncHandle(call);
    }

    private EnrollmentResponseNet syncHandle(Call<EnrollmentResponseNet> call) throws CommandException {
        try {
            Response<EnrollmentResponseNet> response = call.execute();
            EnrollmentResponseNet decodedResponse = response.body();
            if (!response.isSuccessful()) {
                logger.error("EnrollmentComms@syncHandle : response is not successful : " + response.message());
                throw new CommandException("response is not successful : " + response.message());
            }

            if (decodedResponse == null) {
                logger.error("EnrollmentComms@syncHandle : response body is null");
                throw new CommandException("EnrollmentComms@syncHandle : response body is null due to unknown reason");
            }

            if (!decodedResponse.isSuccess()) {
                final List<ServerResponseError> errors = decodedResponse.getErrors();
                final List<String> errorMessages = errors.stream().map(error -> error.getMessage()).collect(Collectors.toList());
                final String errorMessage = Arrays.toString(errorMessages.toArray(new String[errorMessages.size()]));

                logger.error("EnrollmentComms@syncHandle : server internal error : " + errorMessage);
                throw new CommandException("EnrollmentComms@syncHandle : server internal error : " + errorMessage);
            }
            return decodedResponse;
        } catch (IOException e) {
            throw new CommandException("EnrollmentComms@syncHandle : failed to communicate with server", e);
        }
    }

}
