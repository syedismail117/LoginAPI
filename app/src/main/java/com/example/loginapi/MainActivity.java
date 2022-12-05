package com.example.loginapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
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

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText userid, password;
    TextView signup,forgotPass;
    Button btnLogin;
    ProgressDialog progressDialog;

    private long backPressed;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );
        getSupportActionBar ().hide ();



        progressDialog = new ProgressDialog ( MainActivity.this );
        progressDialog.setTitle("Loading....");
        actions();
        RetroFitInstance.getInstance().fillcontext(getApplicationContext ());
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(MainActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(MainActivity.this);
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(RetroFitInstance.MessageEvent messageEvent){
        progressDialog.dismiss();
        Log.e("response","call"+messageEvent.body);
        if(messageEvent.message.contains("loginApi")) {
            if (messageEvent.body != null) {

                try {
                    JSONObject jasonObj = new JSONObject(messageEvent.body);
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }
                Gson gson = new Gson();
                ResponseModel loginResponseModel = gson.fromJson(messageEvent.body, ResponseModel.class);
                Log.e("loginResponse", "call" + loginResponseModel.getResponse());
                if(loginResponseModel.getResponse().equals("3")){

                    //token
                    if( storeAccessToken (loginResponseModel.getJsontoken())){
                        startActivity ( new Intent (this,MainActivity3.class) );
                        Toast.makeText(getApplicationContext(),loginResponseModel.getMessage(),Toast.LENGTH_SHORT).show();
                    }//token

                }else{
                    Toast.makeText(getApplicationContext(),loginResponseModel.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public boolean storeAccessToken(String token){
        SharedPreferences sharedPref =  getSharedPreferences("tokenPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token",token);
        Log.e("token", "call" + token);

        if(editor.commit()){
            return true;
        }else{
            return false;
        }
    }


    private void actions() {

        userid = findViewById(R.id.Email);
        password = findViewById(R.id.pass);
        btnLogin = findViewById(R.id.loginBtn);
        forgotPass=findViewById ( R.id.forgotBtn );


        forgotPass.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                startActivity ( new Intent (MainActivity.this,ForgotPassword.class) );
            }
        } );


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userid.getText().toString().isEmpty()|| password.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Username or Password Required", Toast.LENGTH_LONG).show();
                } else {
                    if(RetroFitInstance.getInstance().checkNetwork()){
                        progressDialog.show();
                             LoginApiParams();
                    }else {
                        Toast.makeText(MainActivity.this, "No internet connection.", Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

        signup=findViewById ( R.id.signupBtn );
        signup.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                startActivity ( new Intent (MainActivity.this,MainActivity2.class) );
            }
        } );
    }


    public void LoginApiParams(){
        JsonObject CheckUserObj = new JsonObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userID", userid.getText().toString());
            jsonObject.put("Password", password.getText().toString());
            JsonParser jsonParser = new JsonParser();
            CheckUserObj = (JsonObject) jsonParser.parse(jsonObject.toString());
            Log.e("checkBuyProject:", " " + CheckUserObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetroFitInstance.getInstance().ApiCallbacksForAllPosts(getApplicationContext (),"loginApi"
                ,"seller/Sellerlogin",CheckUserObj);
    }


    @Override
    public void onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            finishAffinity ();
            return;
        }else {
            backToast=Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressed= System.currentTimeMillis();
    }
}