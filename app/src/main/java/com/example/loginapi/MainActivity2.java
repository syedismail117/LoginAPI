package com.example.loginapi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity2 extends AppCompatActivity {

    EditText name,userID,PhoneNumber,BankName,AccountNumber,IFSCCode,AccountName,Password;
    Button signupBTn,Cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main2 );

        getSupportActionBar ().hide ();

        name=findViewById ( R.id.Name );
        userID=findViewById ( R.id.userId );
        PhoneNumber=findViewById ( R.id.PhoneNumber );
        BankName=findViewById ( R.id.BankName );
        AccountNumber=findViewById ( R.id.AccountNumber );
        IFSCCode=findViewById ( R.id.IFSCCode );
        AccountName=findViewById ( R.id.AccountName );
        Password=findViewById ( R.id.Password);
        signupBTn=findViewById ( R.id.button );
        Cancel=findViewById ( R.id.cancelButton );


        signupBTn.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().isEmpty() || userID.getText().toString().isEmpty() ||
                        PhoneNumber.getText().toString().isEmpty() || BankName.getText().toString().isEmpty() ||
                        AccountNumber.getText().toString().isEmpty() || IFSCCode.getText().toString().isEmpty() ||
                        AccountName.getText().toString().isEmpty() || Password.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity2.this, "Please Enter Required Fields", Toast.LENGTH_SHORT).show();

                } else {
                    if (RetroFitInstance.getInstance().checkNetwork()) {

                        LoadRegisterApiParams();
                    } else {
                        Toast.makeText(MainActivity2.this, "Please Check Your Internet Connection.", Toast.LENGTH_LONG).show();
                    }

                }
            }
        } );

        Cancel.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                startActivity ( new Intent (MainActivity2.this,MainActivity.class) );
            }
        } );

    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(MainActivity2.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(MainActivity2.this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(RetroFitInstance.MessageEvent messageEvent) {

        Log.e("response", "call" + messageEvent.body);
        if (messageEvent.body != null) {
            try {
                JSONObject jObj = new JSONObject(messageEvent.body);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            Gson gson = new Gson();
            ResponseModel registerResponseModel = gson.fromJson(messageEvent.body, ResponseModel.class);
            if (registerResponseModel.getResponseRegister ().equals ("3")) {
                Log.e("register Response", "call" + registerResponseModel.getResponseRegister ());

                Toast.makeText(getApplicationContext(), registerResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
                action();
            } else {
                Toast.makeText(getApplicationContext(), registerResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void action() {
        Intent i = new Intent(this, MainActivity3.class);
        startActivity(i);
        finish();
    }





    public void LoadRegisterApiParams() {
        JsonObject CheckUserObj = new JsonObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", name.getText().toString());
            jsonObject.put("userID", userID.getText().toString());
            jsonObject.put("PhoneNumber", PhoneNumber.getText().toString());
            jsonObject.put("BankName", BankName.getText().toString());
            jsonObject.put("AccountNumber", AccountNumber.getText().toString());
            jsonObject.put("IFSCCode", IFSCCode.getText().toString());
            jsonObject.put("AccountName", AccountName.getText().toString());
            jsonObject.put("Password", Password.getText().toString());
            JsonParser jsonParser = new JsonParser();
            CheckUserObj = (JsonObject) jsonParser.parse(jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetroFitInstance.getInstance ().ApiCallbacksRegisterPosts ( MainActivity2.this,"seller/SellerRegister","seller",CheckUserObj);

    }
}