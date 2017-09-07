package com.androidsrc.server.model;

/**
 * Created by ADMIN on 04-Sep-17.
 *
 */

public class ResponseToClient {
    private boolean success;
    private String sender;
    private String message;
    private String status;
    private String idMessage;

    public ResponseToClient(boolean success, String sender, String message) {
        this.success = success;
        this.sender = sender;
        this.message = message;
    }

    public ResponseToClient(boolean success, String sender, String message, String status) {
        this.success = success;
        this.sender = sender;
        this.message = message;
        this.status = status;
    }

    public ResponseToClient(boolean success, String sender, String message, String status, String idMessage) {
        this.success = success;
        this.sender = sender;
        this.message = message;
        this.status = status;
        this.idMessage = idMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(String idMessage) {
        this.idMessage = idMessage;
    }
}
