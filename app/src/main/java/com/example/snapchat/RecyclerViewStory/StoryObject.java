package com.example.snapchat.RecyclerViewStory;


public class StoryObject {
    private String email;
    private String uid;

    public StoryObject(String email, String uid){
        this.email = email;
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid!=null ? uid : "";
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object obj) {
        boolean same = false;
        if(obj != null && obj instanceof StoryObject){
            same = this.uid == ((StoryObject) obj ).uid;
        }
        return same;
    }

    //This will change the hashcode and check for the users that have same Uid
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.uid == null ? 0 : this.uid.hashCode());
        return result;
    }
}
