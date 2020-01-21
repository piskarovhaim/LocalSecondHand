package com.haim_yarin.finalProject;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

public class Item{
    private String title;
    private String discription;
    private String imageUrl;
    private String price;
    private User user;
    private ItemLocation location;


    // Item object
    public Item(String title, String discription, String imageUrl, String price, String name, String pid, String email, String phone, double latitude, double longitude){

        this.title = title;
        this.discription = discription;
        this.imageUrl = imageUrl;
        this.price = price;
        this.user = new User(name,pid,email,phone);
        this.location = new ItemLocation(latitude,longitude);
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
        JSONObject location = JSON_item.getJSONObject("location");
        double latitude = location.getDouble("Latitude");
        double longitude = location.getDouble("Longitude");
        this.location = new ItemLocation(latitude,longitude);

    }

    String getTitle(){
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
    public ItemLocation getLocation(){
        return this.location;
    }

    public int compareTo(Item compItem,float distance) {

        float[] results = new float[1];
        Location.distanceBetween(this.location.getLatitude(),this.location.getLongitude(),compItem.getLocation().getLatitude(),compItem.getLocation().getLongitude(),results);
        if(this.title.indexOf(compItem.getTitle()) > -1 && ((distance > 0 && results[0] < distance) || distance == -1))
            return 1;
        return -1;
    }
}
