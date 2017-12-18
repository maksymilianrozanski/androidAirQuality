package com.example.android.airquality.utility;

import com.example.android.airquality.dataholders.StationList;

import okhttp3.OkHttpClient;
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
}
