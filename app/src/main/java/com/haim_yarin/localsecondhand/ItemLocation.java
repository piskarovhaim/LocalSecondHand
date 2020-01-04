package com.haim_yarin.localsecondhand;

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
