package com.example.android.airquality.utility;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface StationsRestService {

    @GET("pjp-api/rest/station/findAll/")
    Call<ResponseBody> getAllStations();

    @GET("pjp-api/rest/station/sensors/{stationId}/")
    Call<ResponseBody> getListOfSensors(@Path("stationId") int stationId);

    @GET("pjp-api/rest/data/getData/{sensorId}/")
    Call<ResponseBody> getSensorValues(@Path("sensorId") int sensorId);
}
