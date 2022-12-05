package com.example.loginapi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPassword extends AppCompatActivity {

    Button submit;
    EditText userId,newPass,confirmPass;
    String tokenValue="";

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_forgot_password );

        submit=findViewById ( R.id.submitBtn );
        userId=findViewById ( R.id.Email );
        newPass=findViewById ( R.id.newPass );
        confirmPass=findViewById ( R.id.ConfirmPass );

       //progress dialog
        progressDialog = new ProgressDialog (ForgotPassword.this);
        progressDialog.setTitle("Loading....");
       //progress dialog


        RetroFitInstance.getInstance().fillcontext(getApplicationContext());
        getTokenFromSharedPreference();

        submit.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                if (RetroFitInstance.getInstance().checkNetwork()) {
                    progressDialog.show();
                    passwordUpdateApiParams();
                } else {
                    Toast.makeText(ForgotPassword.this, "No internet connection.", Toast.LENGTH_LONG).show();
                }
            }
        } );
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(RetroFitInstance.MessageEvent messageEvent) {
        progressDialog.dismiss();
        Log.e("response", "call" + messageEvent.body);
        if (messageEvent.body != null && messageEvent.message.equals("updatePswApi")) {
            try {
                JSONObject jsonObj = new JSONObject(messageEvent.body);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            Gson gson = new Gson();
            ResponseModel loginResponseModel = gson.fromJson(messageEvent.body, ResponseModel.class);
            Log.e("loginResponse", "call" + loginResponseModel.getResponse());
            if (loginResponseModel.getResponse().equals ("3")) {
                finish();
                Toast.makeText(getApplicationContext(), loginResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (loginResponseModel.getResponse().equals ("0")) {
                Toast.makeText(getApplicationContext(), loginResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), loginResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void passwordUpdateApiParams() {
        JsonObject CheckUserObj = new JsonObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userID", userId.getText().toString());
            jsonObject.put("Password", confirmPass.getText().toString());
            JsonParser jsonParser = new JsonParser();
            CheckUserObj = (JsonObject) jsonParser.parse(jsonObject.toString());
            Log.e("loadUpdateApiParams:", " " + CheckUserObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetroFitInstance.getInstance().ApiPutMethods(ForgotPassword.this,tokenValue
                ,"seller/SellerUpdatePassword", CheckUserObj,"updatePswApi");
    }

    private void getTokenFromSharedPreference(){
        SharedPreferences sharedPref =  getSharedPreferences("tokenPrefs", MODE_PRIVATE);
        tokenValue= sharedPref.getString("token",null);
    }

}