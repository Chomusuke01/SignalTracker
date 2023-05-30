package com.example.test;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    String BASE_URL = "https://api.mylnikov.org/geolocation/";

    @GET("cell")
    Call<Data> getAntennaInformtion(@Query("v") double v, @Query("data") String data, @Query("mcc") int mcc, @Query("mnc") int mnc, @Query("lac") int lac, @Query("cellid") int cellID);

}
