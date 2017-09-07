package com.androidsrc.server.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ADMIN on 04-Sep-17.
 *
 */

public class RequestClient {
    @SerializedName("request")
    private String requestKey;
    @SerializedName("ipAddress")
    private String ipAddress;
    @SerializedName("message")
    private String message;
    @SerializedName("destination")
    private String destination;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("idRequest")
    private String idRequest;

    public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(String idRequest) {
        this.idRequest = idRequest;
    }
}
