package org.bica.julongchain.cfca.ra.command.internal.reenroll;

import org.bica.julongchain.cfca.ra.command.CommandException;
import org.bica.julongchain.cfca.ra.command.internal.ClientConfig;
import org.bica.julongchain.cfca.ra.command.internal.OkHttpUtils;
import org.bica.julongchain.cfca.ra.command.internal.ServerResponseError;
import org.bica.julongchain.cfca.ra.command.internal.enroll.EnrollmentResponseNet;
import org.bica.julongchain.cfca.ra.command.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 处理 Enrollment 命令与服务器之间的网络交互, 发送和接受网络消息
 * @CodeReviewer
 * @since v3.0.0
 */
public class ReenrollmentComms {

    private static final Logger logger = LoggerFactory.getLogger(ReenrollmentComms.class);
    private final ClientConfig clientConfig;

    public ReenrollmentComms(ClientConfig clientConfig) {
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
        @POST("reenroll")
        Call<EnrollmentResponseNet> postWithEnrollData(@Body ReenrollmentRequestNet request, @Header("Authorization") String auth);

    }

    /**
     * @param enrollmentRequest 申请证书的数据
     * @return EnrollmentResponseNet
     * @throws IOException 网络请求失败
     */
    public EnrollmentResponseNet request(ReenrollmentRequestNet enrollmentRequest, String auth) throws CommandException {
        final String baseUrl = clientConfig.getUrl();
        if (StringUtils.isBlank(baseUrl)) {
            throw new CommandException("baseurl is empty");
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpUtils.getUnsafeOkHttpsClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EnrollService service = retrofit.create(EnrollService.class);

        final Call<EnrollmentResponseNet> call;
        if (StringUtils.isEmpty(auth)) {
            throw new CommandException("the reenroll command fail to parse CLI parameters");
        }

        call = service.postWithEnrollData(enrollmentRequest, auth);
        call.request();
        return syncHandle(call);
    }

    private EnrollmentResponseNet syncHandle(Call<EnrollmentResponseNet> call) throws CommandException {
        try {
            Response<EnrollmentResponseNet> response = call.execute();
            if (!response.isSuccessful()) {
                logger.error("ReenrollmentComms@syncHandle : response is not successful : " + response.message());
                throw new CommandException("ReenrollmentComms@syncHandle : response is not successful : " + response.message());
            }

            EnrollmentResponseNet decodedResponse = response.body();
            if (decodedResponse == null) {
                logger.error("response body is null");
                throw new CommandException("ReenrollmentComms@syncHandle : " +
                        "response body is null due to unknown reason");
            }

            if (!decodedResponse.isSuccess()) {
                final List<ServerResponseError> errors = decodedResponse.getErrors();
                final List<String> errorMessages = errors.stream().map(error -> error.getMessage()).collect(Collectors.toList());
                final String errorMessage = Arrays.toString(errorMessages.toArray(new String[errorMessages.size()]));
                
                logger.error("ReenrollmentComms@syncHandle : server internal error : " + errorMessage);
                throw new CommandException("ReenrollmentComms@syncHandle : server internal error : " + errorMessage);
            }
            return decodedResponse;
        } catch (IOException e) {
            
            logger.error("ReenrollmentComms@syncHandle : failed to communicate with server", e);
            
            throw new CommandException("ReenrollmentComms@syncHandle : failed to communicate with server", e);
        }
    }

}
