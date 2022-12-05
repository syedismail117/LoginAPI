package com.example.loginapi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;

public class RetroFitInstance {

    private Context context;

    private static RetroFitInstance retrofitCallbacks;
    private static Retrofit retrofit = null;

    private static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(InterFaceAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static RetroFitInstance getInstance() {
        if (retrofitCallbacks == null) {
            retrofitCallbacks = new RetroFitInstance();
        }
        return retrofitCallbacks;
    }

    public void fillcontext(Context context) {
        this.context = context;
    }



    public boolean checkNetwork() {
        boolean wifiAvailable = false;
        boolean mobileAvailable = false;
        ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = conManager.getAllNetworkInfo();
        for (NetworkInfo netInfo : networkInfo) {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                if (netInfo.isConnected())
                    wifiAvailable = true;
            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if (netInfo.isConnected())
                    mobileAvailable = true;
        }
        return wifiAvailable || mobileAvailable;
    }




    public void ApiCallbacksForAllPosts(Context context, String NotifyMsg, String EndUrl, JsonObject jsonobj) {
        this.context = context;
        InterFaceAPI apiCollection = getClient().create(InterFaceAPI.class);
        Call<ResponseBody> call = apiCollection.apiCalling(jsonobj, EndUrl);
        call.enqueue(new Callback<ResponseBody> () {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                String bodyString = null;
                Log.e("NotifyMsg", " " + response.body());
                if (response.body() != null) {
                    try {
                        bodyString = new String(response.body().bytes());
                        Log.e("NotifyMsg", " " + bodyString);
                        EventBus.getDefault ().post ( new MessageEvent  (bodyString,NotifyMsg) );

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    EventBus.getDefault().post(new MessageEvent( bodyString,NotifyMsg));
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                EventBus.getDefault().post(new MessageEvent("onFailure", null));
                Toast.makeText(context, ":" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }

//for register api call back
    public void ApiCallbacksRegisterPosts(Context context, String EndUrl,String NotifyMsg, JsonObject jsonobj) {
        this.context = context;
        InterFaceAPI ApiSignup= getClient ().create ( InterFaceAPI.class );

        Call<ResponseBody> call = ApiSignup.apiInsertSignup(jsonobj, EndUrl);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String Body = null;
                if (response.body() != null) {
                    try {
                        Body = new String(response.body().bytes());


                        EventBus.getDefault().post(new MessageEvent(Body,NotifyMsg));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    EventBus.getDefault().post(new MessageEvent( Body,NotifyMsg));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                EventBus.getDefault().post(new MessageEvent("onFailure", null));


                Toast.makeText(context, ":" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void ApiPutMethods(Context context, String Token,String EndUrl, JsonObject jsonobj,String eventBusMsg) {
        this.context = context;
        InterFaceAPI api = getClient().create(InterFaceAPI.class);
        Call<ResponseBody> call = api.apiPutMethode(Token,jsonobj, EndUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String Body = null;
                if (response.body() != null) {
                    try {
                        Body = new String(response.body().bytes());
                        EventBus.getDefault().post(new MessageEvent(Body,eventBusMsg));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    EventBus.getDefault().post(new MessageEvent(Body,eventBusMsg));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                EventBus.getDefault().post(new MessageEvent(null,"onFailure"));
                Toast.makeText(context, ":" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }




    public static class MessageEvent {
        public String message, body;

        public MessageEvent(String body,String msg) {
            this.body = body;
            this.message=msg;
        }
    }


}
