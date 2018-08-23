package org.bica.julongchain.cfca.ra.command.internal.getcainfo;

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
import retrofit2.http.POST;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangchong
 * @create 2018/5/17
 * @Description 获取证书命令客户端与服务器之间的网络交互, 发送和接受网络消息
 * @CodeReviewer
 * @since v3.0.0
 */
public class GetCAInfoComms {
    private static final Logger logger = LoggerFactory.getLogger(GetCAInfoComms.class);
    private final ClientConfig clientConfig;

    public GetCAInfoComms(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    private interface GetCAInfoService {
        /**
         * 发送GetCaInfo 的post请求给服务器
         *
         * @param requestNet
         * @return 服务器xiangying
         */
        @POST("cainfo")
        Call<GetCAInfoResponseNet> postWithCaName(@Body GetCAInfoRequestNet requestNet);
    }

    public GetCAInfoResponseNet request(GetCAInfoRequestNet requestNet) throws CommandException {
        final String baseUrl = clientConfig.getUrl();
        if (StringUtils.isBlank(baseUrl)) {
            throw new CommandException("baseurl is empty");
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
            if (!response.isSuccessful()) {
                logger.error("GetCAInfoComms@syncHandle : response is not successful : " + response.message());
                throw new CommandException("GetCAInfoComms@syncHandle : response is not successful : " + response.message());
            }

            GetCAInfoResponseNet decodedResponse = response.body();
            if (decodedResponse == null) {
                logger.error("GetCAInfoComms@syncHandle : response body is null");
                throw new CommandException("GetCAInfoComms@syncHandle : response body is null due to unknown reason");
            }

            if (!decodedResponse.isSuccess()) {
                final List<ServerResponseError> errors = decodedResponse.getErrors();
                final List<String> errorMessages = errors.stream().map(error -> error.getMessage()).collect(Collectors.toList());
                final String errorMessage = Arrays.toString(errorMessages.toArray(new String[errorMessages.size()]));
                
                logger.error("GetCAInfoComms@syncHandle : server internal error : " + errorMessage);
                throw new CommandException("GetCAInfoComms@syncHandle : server internal error : " + errorMessage);
            }
            return decodedResponse;
        } catch (IOException e) {
            logger.error("GetCAInfoComms@syncHandle : failed to communicate with server", e);
            throw new CommandException("GetCAInfoComms@syncHandle : failed to communicate with server", e);
        }
    }
}
