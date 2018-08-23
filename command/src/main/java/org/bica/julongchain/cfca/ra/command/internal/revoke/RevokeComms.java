package org.bica.julongchain.cfca.ra.command.internal.revoke;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bica.julongchain.cfca.ra.command.CommandException;
import org.bica.julongchain.cfca.ra.command.internal.ClientConfig;
import org.bica.julongchain.cfca.ra.command.internal.OkHttpUtils;
import org.bica.julongchain.cfca.ra.command.internal.ServerResponseError;
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

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 处理 Enrollment 命令与服务器之间的网络交互, 发送和接受网络消息
 * @CodeReviewer
 * @since v3.0.0
 */
public class RevokeComms {

    private static final Logger logger = LoggerFactory.getLogger(RevokeComms.class);
    private final ClientConfig clientConfig;

    public RevokeComms(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    private interface RegisterService {
        /**
         * 使用 post 请求提交 enroll数据
         *
         * @param request
         * @param auth
         * @return EnrollmentResponseNet
         */
        @POST("revoke")
        Call<RevokeResponseNet> postWithRevokeRequestNet(@Body RevokeRequestNet request, @Header("Authorization") String auth);
    }

    /**
     * @param registrationRequestNet
     *            申请证书的数据
     * @return EnrollmentResponseNet
     * @throws IOException
     *             网络请求失败
     */
    public RevokeResponseNet request(RevokeRequestNet registrationRequestNet, String auth) throws CommandException {
        final String baseurl = clientConfig.getUrl();
        if (StringUtils.isBlank(baseurl)) {
            throw new CommandException("baseurl is empty");
        }
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseurl).client(OkHttpUtils.getUnsafeOkHttpsClient())
                .addConverterFactory(GsonConverterFactory.create()).build();

        RegisterService service = retrofit.create(RegisterService.class);

        final Call<RevokeResponseNet> call = service.postWithRevokeRequestNet(registrationRequestNet, auth);

        call.request();
        return syncHandle(call);
    }

    private RevokeResponseNet syncHandle(Call<RevokeResponseNet> call) throws CommandException {
        try {
            Response<RevokeResponseNet> response = call.execute();
            RevokeResponseNet decodedResponse = response.body();
            if (!response.isSuccessful()) {
                logger.error("RevokeComms@syncHandle : response is not successful : " + response.message());
                throw new CommandException("RevokeComms@syncHandle : response is not successful : " + response.message());
            }

            if (decodedResponse == null) {
                logger.error("RevokeComms@syncHandle : response body is null");
                throw new CommandException("RevokeComms@syncHandle : response body is null due to unknown reason");
            }

            if (!decodedResponse.isSuccess()) {
                final List<ServerResponseError> errors = decodedResponse.getErrors();
                final List<String> errorMessages = errors.stream().map(error -> error.getMessage()).collect(Collectors.toList());
                final String errorMessage = Arrays.toString(errorMessages.toArray(new String[errorMessages.size()]));

                logger.error("RevokeComms@syncHandle : " + errorMessage);
                throw new CommandException("RevokeComms@syncHandle : " + errorMessage);
            }
            return decodedResponse;
        } catch (IOException e) {
            logger.error("RevokeComms@syncHandle : failed to communicate with server", e);
            throw new CommandException("RevokeComms@syncHandle : failed to communicate with server", e);
        }
    }

}
