package com.example.android.airquality.utility;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface StationsRestService {

    @GET("pjp-api/rest/station/findAll/")
    Call<ResponseBody> getAllStations();
}
