package com.haim_yarin.finalProject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;


//Manege all the items
public class ItemsList extends ArrayAdapter<Item> {

    private ArrayList<Item> itemsList;
    private ArrayList<Item> filteredItemsList;
    private DataBase db;
    private Context context;
    private int w;
    private  int h;


    public ItemsList(@NonNull Context context) {
        super(context, 0);
        itemsList = new ArrayList<Item>();
        filteredItemsList = new ArrayList<Item>();
        db = new DataBase();
        this.context = context;
    }

    public void addItem(Item item){
        itemsList.add(item);
    }


    public void UpdateItemsList(){
        db.UpdateItems(this);
    }

    public void Search(Item item,float distance){
        this.filteredItemsList = new ArrayList<>();
        for(int i = 0 ; i < itemsList.size() ; i++) {
            if (itemsList.get(i).compareTo(item,distance) > 0)
                filteredItemsList.add(itemsList.get(i));
        }
        this.notifyDataSetChanged();
    }


    public void setItemsList(ArrayList<Item> itemsList){
        this.itemsList = itemsList;
        this.filteredItemsList = itemsList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() { return filteredItemsList.size(); }
    @Override
    public Item getItem(int i) { return filteredItemsList.get(i); }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View listItemView = convertView;
        if(listItemView == null)
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);

        Item item = getItem(position);

        TextView titleTextView = listItemView.findViewById(R.id.txtTitle);
        titleTextView.setText(item.getTitle()+' ');

        TextView priceTextView = listItemView.findViewById(R.id.txtPrice);
        priceTextView.setText(item.getPrice() + "\u200F₪");

        ImageView itemImage = listItemView.findViewById(R.id.imgItem);
        Picasso.with(context).load(item.getImageUrl())
                .fit()
                .into(itemImage);

        return listItemView;
    }


}
