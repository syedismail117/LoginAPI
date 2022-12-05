package com.example.loginapi;

public class ResponseModel {

    private String response;
    private String message;
    private String jsontoken;
    private model Info;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
    public String getResponseRegister(){
        return response;
    }
    public void setResponseRegister(){
        this.response=response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJsontoken() {
        return jsontoken;
    }

    public void setJsontoken(String jsontoken) {
        this.jsontoken = jsontoken;
    }

    public model getInfo() {
        return Info;
    }

    public void setInfo(model info) {
        Info = info;
    }
}
