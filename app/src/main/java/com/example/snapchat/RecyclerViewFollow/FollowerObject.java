package com.example.snapchat.RecyclerViewFollow;

public class FollowerObject {
    private String userName;
    private String uid;

    public FollowerObject(String userName, String uid){
        this.userName = userName;
        this.uid = uid;
    }

    public String getuserName() {
        return userName;
    }

    public void setEmail(String userName) {
        this.userName = userName;
    }

    public String getUid() {
        return uid!=null ? uid : "";
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
