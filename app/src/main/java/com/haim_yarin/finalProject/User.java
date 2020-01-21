package com.haim_yarin.finalProject;


// user object
public class User {
    private String name;
    private String uid;
    private String email;
    private String phone;

    public User(String name, String uid, String email, String phone){
        this.name = name;
        this.uid = uid;
        this.email = email;
        this.phone = phone;

    }

    public String getName(){
        return this.name;
    }
    public String getUid(){
        return this.uid;
    }
    public String getEmail(){
        return this.email;
    }
    public String getPhone(){
        return this.phone;
    }
}
