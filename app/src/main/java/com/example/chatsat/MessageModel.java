package com.example.chatsat;

public class MessageModel {



    String message;
    String senderId;
    long timestamp;
    String currentime;

    public MessageModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCurrentime() {
        return currentime;
    }

    public void setCurrentime(String currentime) {
        this.currentime = currentime;
    }

    public MessageModel(String message, String senderId, long timestamp, String currentime) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.currentime = currentime;
    }
}
