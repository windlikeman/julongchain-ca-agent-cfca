package org.bica.julongchain.cfca.ra.command.internal.heartbeat;

import org.bica.julongchain.cfca.ra.command.CommandException;
import org.bica.julongchain.cfca.ra.command.internal.ClientConfig;
import org.bica.julongchain.cfca.ra.command.internal.OkHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

import java.io.IOException;

/**
 * @author zhangchong
 * @Create 2018/7/15 9:34
 * @CodeReviewer
 * @Description
 * @since
 */
public class HeartBeatComms {
   

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatComms.class);
    private final ClientConfig clientConfig;

    public HeartBeatComms(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
    
    private interface HeartBeatService {
        /**
         * 使用 GET 请求提交 HeartBeat 数据
         *
         * @return HeartBeatResponseNet
         */
        @GET("heartbeat")
        Call<HeartBeatResponseNet> getHeartBeat();
    }

    /**
     * @return EnrollmentResponseNet
     * @throws IOException 网络请求失败
     */
    public HeartBeatResponseNet request() throws CommandException {
        final String baseurl = clientConfig.getUrl();
        if (org.bica.julongchain.cfca.ra.command.utils.StringUtils.isBlank(baseurl)) {
            throw new CommandException("baseurl is empty");
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseurl)
                .client(OkHttpUtils.getUnsafeOkHttpsClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HeartBeatService service = retrofit.create(HeartBeatService.class);

        final Call<HeartBeatResponseNet> call = service.getHeartBeat();

        call.request();
        return syncHandle(call);
    }

    private HeartBeatResponseNet syncHandle(Call<HeartBeatResponseNet> call) throws CommandException {
        try {
            Response<HeartBeatResponseNet> response = call.execute();
            if (!response.isSuccessful()) {
                logger.error("HeartBeatComms@syncHandle : response is not successful : " + response.message());
                throw new CommandException("response is not successful : " + response.message());
            }

            HeartBeatResponseNet decodedResponse = response.body();
            if (decodedResponse == null) {
                logger.error("HeartBeatComms@syncHandle : response body is null");
                throw new CommandException("HeartBeatComms@syncHandle : response body is null due to unknown reason");
            }

            return decodedResponse;
        } catch (IOException e) {
            logger.error("HeartBeatComms@syncHandle : failed to communicate with server", e);
            throw new CommandException("HeartBeatComms@syncHandle : failed to communicate with server", e);
        }
    }
}
