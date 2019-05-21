package io.github.maksymilianrozanski.utility;

import java.io.IOException;

import io.github.maksymilianrozanski.dataholders.StationList;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ServiceGenerator {
    private static OkHttpClient.Builder okHttpClient;
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(StationList.STATIONS_BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder();
        }
        OkHttpClient client = okHttpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }

    public static String getResponseBody(retrofit2.Call<ResponseBody> call) throws IOException {
        try {
            Response<ResponseBody> response = call.execute();
            if (response.code() == 200) {
                return response.body().string();
            } else {
                throw new IOException("server response code: " + response.code());
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
