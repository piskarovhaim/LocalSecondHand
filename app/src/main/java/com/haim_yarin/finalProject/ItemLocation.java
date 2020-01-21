package com.haim_yarin.finalProject;


// item location object, have the latitude and longitude of the item
public class ItemLocation {

        private double latitude, longitude ;

        public ItemLocation(double latitude,double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude(){
            return latitude;
        }
        public double getLongitude(){
            return longitude;
        }
}
