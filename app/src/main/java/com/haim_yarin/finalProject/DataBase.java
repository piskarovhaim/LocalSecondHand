package com.haim_yarin.finalProject;

import android.os.AsyncTask;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

// fetch the items from the data base with asyncTask
public class DataBase {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ItemsList itemsList;
    private ArrayList<Item> tempItemList;


    public  void UpdateItems(ItemsList itemsList){
        this.itemsList = itemsList;
        new readItems().execute();
    }


    private class readItems extends AsyncTask<String, Void, ArrayList<Item>>
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
            }

            protected ArrayList<Item> doInBackground(String... urls)
            {
                tempItemList = new ArrayList<Item>();
                Task<QuerySnapshot> items = db.collection("items").get();
                try {
                    QuerySnapshot  querySnapshot = Tasks.await(items);
                    for (DocumentSnapshot snapshot : querySnapshot) {
                        JSONObject item = new JSONObject(snapshot.getData());
                        try {
                            tempItemList.add(new Item(item));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (ExecutionException e) {

                } catch (InterruptedException e) {

                }

                return tempItemList;
            }


            protected void onPostExecute(ArrayList<Item> result)
            {
                itemsList.setItemsList(tempItemList);
            }
        }

}
