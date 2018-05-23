package com.cfca.ra.command.internal.gettcert;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.internal.ClientConfig;
import com.cfca.ra.command.internal.OkHttpUtils;
import com.cfca.ra.command.internal.ServerResponseError;
import com.cfca.ra.command.utils.MyStringUtils;
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

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 处理 Enrollment 命令与服务器之间的网络交互, 发送和接受网络消息
 * @CodeReviewer
 * @since v3.0.0
 */
public class GettCertComms {

    private static final Logger logger = LoggerFactory.getLogger(GettCertComms.class);
    private final ClientConfig clientConfig;

    public GettCertComms(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    private interface GettCertService {
        /**
         * 使用 post 请求提交 enroll数据
         *
         * @param request
         * @param auth
         * @return EnrollmentResponseNet
         */
        @POST("tcert")
        Call<GettcertResponseNet> postWithGettCertRequestNet(
                @Body GettCertRequestNet request,
                @Header("Authorization") String auth
        );
    }

    /**
     * @param registrationRequestNet 申请证书的数据
     * @return EnrollmentResponseNet
     * @throws IOException 网络请求失败
     */
    public GettcertResponseNet request(GettCertRequestNet registrationRequestNet, String auth) throws CommandException {
        final String baseurl = clientConfig.getUrl();
        if (MyStringUtils.isBlank(baseurl)) {
            throw new CommandException(CommandException.REASON_CODE_GETTCERT_COMMAND_COMMS_FAILED, "baseurl is empty");
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseurl)
                .client(OkHttpUtils.getUnsafeOkHttpsClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GettCertService service = retrofit.create(GettCertService.class);

        final Call<GettcertResponseNet> call = service.postWithGettCertRequestNet(registrationRequestNet, auth);

        call.request();
        return syncHandle(call);
    }

    private GettcertResponseNet syncHandle(Call<GettcertResponseNet> call) throws CommandException {
        try {
            Response<GettcertResponseNet> response = call.execute();
            GettcertResponseNet decodedResponse = response.body();
            if (!response.isSuccessful()) {
                logger.error("response is not successful : " + response.message());
                return null;
            }

            if (decodedResponse == null) {
                logger.error("response body is null");
                return null;
            }

            if (!decodedResponse.isSuccess()) {
                final List<ServerResponseError> errors = decodedResponse.getErrors();
                final String errorMessage = Arrays.toString(errors.toArray(new ServerResponseError[errors.size()]));
                throw new CommandException(CommandException.REASON_CODE_GETTCERT_COMMAND_COMMS_FAILED, "server internal error : " + errorMessage);
            }
            return decodedResponse;
        } catch (IOException e) {
            throw new CommandException(CommandException.REASON_CODE_GETTCERT_COMMAND_COMMS_FAILED, "failed to communicate with server, reaseon : " + e.getMessage(), e);
        }
    }

}
