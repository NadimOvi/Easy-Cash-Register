package com.gtr.easycashregister.API;

import com.google.gson.JsonObject;
import com.gtr.easycashregister.Model.OurDataSet;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Api {

    @POST("CreateItem")
    Call<String> createItem(@Body JsonObject ussdObject);


    //viewItem
    @GET("All/{phoneNumber}")
    Call<List<OurDataSet>> getDataSet(@Path("phoneNumber") String phoneNumber);

}
