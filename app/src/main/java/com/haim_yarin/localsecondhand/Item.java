package com.haim_yarin.localsecondhand;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Item {
    private String title;
    private String discription;
    private String imageUrl;
    private String price;
    private User user;

    public Item (String title , String discription,String imageUrl,String price,String name, String pid, String email, String phone){

        this.title = title;
        this.discription = discription;
        this.imageUrl = imageUrl;
        this.price = price;
        this.user = new User(name,pid,email,phone);
    }

    public Item (JSONObject JSON_item) throws JSONException {
        this.title = JSON_item.getString("Title");
        this.discription = JSON_item.getString("Disctiption");
        this.imageUrl = JSON_item.getString("ImageUrl");
        this.price = JSON_item.getString("Price");
        JSONObject user = JSON_item.getJSONObject("user");
        String name = user.getString("Name");
        String uid=user.getString("Uid");
        String email=user.getString("Email");
        String phone=user.getString("Phone");
        this.user = new User(name,uid,email,phone);

    }

    public String getTitle(){
        return this.title;
    }
    public String getDiscription(){
        return this.discription;
    }
    public String getImageUrl(){
        return this.imageUrl;
    }
    public String getPrice(){return  this.price;}
    public User getUser(){
        return user;
    }
}
