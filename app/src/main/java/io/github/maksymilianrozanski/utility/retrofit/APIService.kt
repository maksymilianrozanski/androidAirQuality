package io.github.maksymilianrozanski.utility.retrofit

import retrofit2.Call
import retrofit2.http.GET

interface APIService {

    @GET("/pjp-api/rest/station/findAll")
    fun getAllStations(): Call<List<StationsResponse>>

}