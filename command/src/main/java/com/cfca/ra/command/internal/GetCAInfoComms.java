package com.cfca.ra.command.internal;

import com.cfca.ra.command.CommandException;
import com.cfca.ra.command.utils.MyStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/17
 * @Description 获取证书命令客户端与服务器之间的网络交互, 发送和接受网络消息
 * @CodeReviewer
 * @since v3.0.0
 */
class GetCAInfoComms {
    private static final Logger logger = LoggerFactory.getLogger(GetCAInfoComms.class);
    private final ClientConfig clientConfig;

    GetCAInfoComms(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    private interface GetCAInfoService {
        @POST("cainfo")
        Call<GetCAInfoResponseNet> postWithCaName(@Body GetCAInfoRequestNet requestNet);
    }

    GetCAInfoResponseNet request(GetCAInfoRequestNet requestNet) throws CommandException {
        final String baseUrl = clientConfig.getUrl();
        if (MyStringUtils.isBlank(baseUrl)) {
            throw new CommandException(CommandException.REASON_CODE_GETCAINFO_COMMAND_COMMS_FAILED, "baseurl is empty");
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpUtils.getUnsafeOkHttpsClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetCAInfoComms.GetCAInfoService service = retrofit.create(GetCAInfoComms.GetCAInfoService.class);

        final Call<GetCAInfoResponseNet> call = service.postWithCaName(requestNet);
        call.request();
        return syncHandle(call);
    }

    private GetCAInfoResponseNet syncHandle(Call<GetCAInfoResponseNet> call) throws CommandException {
        try {
            Response<GetCAInfoResponseNet> response = call.execute();
            GetCAInfoResponseNet decodedResponse = response.body();
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
                throw new CommandException(CommandException.REASON_CODE_GETCAINFO_COMMAND_COMMS_FAILED, "server internal error : " + errorMessage);
            }
            return decodedResponse;
        } catch (IOException e) {
            throw new CommandException(CommandException.REASON_CODE_GETCAINFO_COMMAND_COMMS_FAILED, "failed to communicate with server, reaseon : " + e.getMessage(), e);
        }
    }
}
