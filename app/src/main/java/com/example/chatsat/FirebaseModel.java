package com.example.chatsat;

public class FirebaseModel {
    ///////for fetching the data from firebase firestore  we use the
   // recyleradapter of firebase
    // to fetch the details we need to create a model class

    String username , imageurl , userId,status;

    public FirebaseModel() {
    }

    public FirebaseModel(String username, String imageurl, String userId, String status) {
        this.username = username;
        this.imageurl = imageurl;
        this.userId = userId;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
