package com.example.loginapi;

import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

public interface InterFaceAPI {

    public static final String BASE_URL = "http://liveapi-vmart.softexer.com/api/";
     String token="vmart";

    @POST
    Call<ResponseBody> apiCalling(@Body JsonObject jsonObject, @Url String url);

    @POST
    Call<ResponseBody> apiInsertSignup(@Body JsonObject jsonObject, @Url String url);

    @PUT
    Call<ResponseBody> apiPutMethode(@Header(token) String Token ,@Body JsonObject jsonObject, @Url String url);

}
