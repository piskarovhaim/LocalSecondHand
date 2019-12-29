package com.haim_yarin.localsecondhand;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class ItemsList extends ArrayAdapter<Item> {

    private ArrayList<Item> itemsList;
    private DataBase db;
    private Context context;
    private int w;
    private  int h;


    public ItemsList(@NonNull Context context) {
        super(context, 0);
        itemsList = new ArrayList<Item>();
        db = new DataBase();
        this.context = context;
    }

    public void UpdateItemsList(){
        db.UpdateItems(this);
    }


    public void setItemsList(ArrayList<Item> itemsList){
        this.itemsList = itemsList;
    }

    @Override
    public int getCount() { return itemsList.size(); }
    @Override
    public Item getItem(int i) { return itemsList.get(i); }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View listItemView = convertView;
        if(listItemView == null)
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);

        Item item = getItem(position);

        TextView titleTextView = listItemView.findViewById(R.id.txtTitle);
        titleTextView.setText(item.getTitle());

        TextView priceTextView = listItemView.findViewById(R.id.txtPrice);
        priceTextView.setText(item.getPrice());

        ImageView itemImage = listItemView.findViewById(R.id.imgItem);
        Picasso.with(context).load(item.getImageUrl())
                .fit()
                .into(itemImage);



        return listItemView;
    }

}
