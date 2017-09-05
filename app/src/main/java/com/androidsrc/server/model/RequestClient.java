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
    @SerializedName("Message")
    private String message;
    @SerializedName("Destination")
    private String destination;
    @SerializedName("nickname")
    private String nickname;

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
}
