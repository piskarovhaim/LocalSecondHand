package com.haim_yarin.finalProject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,ListView.OnItemClickListener {

    private Intent addItemForm;
    private Intent ItemActivity;
    private Intent AuthActivity;
    private ItemsList itemsList;
    private Authentication auth;
    private MenuItem menuAuth;
    private MyNotification notification;
    private WifiReceiver wifiReceiver;
    private ImageView btnAddItem;
    private ImageView btnSearch;
    private EditText edSearchTitle;
    private EditText edSearchRange;
    private Gps gps;

    final private static int CONNECT = 10;
    final private static int LOGOUT = 20;
    final private static int ADD_ITEM = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // intent for item details
        ItemActivity = new Intent(this,ItemActivity.class);

        // list of the all items
        itemsList = new ItemsList(this);
        ListView listView = findViewById(R.id.itemList);
        listView.setAdapter(itemsList);
        listView.setOnItemClickListener(this);

        // login abject
        auth = new Authentication();

        // intent for the add new item Form
        addItemForm = new Intent(this,AddItemFormActivity.class);

        btnAddItem = (ImageView) findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(this);
        btnSearch = (ImageView)findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        edSearchTitle = (EditText)findViewById(R.id.edSearchTitle);
        edSearchRange = (EditText)findViewById(R.id.edRange);


        //notification listen
        notification = new MyNotification((NotificationManager) getSystemService(NOTIFICATION_SERVICE),this,itemsList,auth);
        notification.ListenToChange();


        // GPS object
        gps = new Gps(this);
        wifiReceiver = new WifiReceiver(gps);
        IntentFilter intentFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        // register the receiver with the filter
        registerReceiver(wifiReceiver, intentFilter);

        AuthActivity = new Intent(this,Authentication.class);
    }

    @Override // go to item details activity
    public void onItemClick(AdapterView<?> parent, View view, int i, long id)
    {
        Item item = itemsList.getItem(i);
        ItemActivity.putExtra("price",item.getPrice());
        ItemActivity.putExtra("title",item.getTitle());
        ItemActivity.putExtra("discription",item.getDiscription());
        ItemActivity.putExtra("name",item.getUser().getName());
        ItemActivity.putExtra("email",item.getUser().getEmail());
        ItemActivity.putExtra("phone",item.getUser().getPhone());
        ItemActivity.putExtra("image",item.getImageUrl());
        startActivity(ItemActivity);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAddItem: // check gps permission and the if conected go to add item form else to log in screen
                if(!gps.isPermissionToReadGPSLocationOK())
                    break;
                if(!auth.isLogin()) {
                    AuthActivity = new Intent(MainActivity.this,Authentication.class);
                    AuthActivity.putExtra("login",true);
                    startActivityForResult(AuthActivity,ADD_ITEM);
                }
                else
                    goToAddItemForm();
                break;
            case R.id.btnSearch: // check gps permission ( for the range) and make the search
                if(!gps.isPermissionToReadGPSLocationOK())
                    break;
                String title = edSearchTitle.getText().toString();
                float range;
                try {
                    range = (float)Integer.parseInt(edSearchRange.getText().toString());
                    range *= 1000;
                }catch (Exception e){
                    range = -1;
                }

                Item item = new Item(title,"dfg","dfg","dfg","sdfg","dfg","dsfg","dfg",gps.getLatitude(),gps.getLongitude());
                itemsList.Search(item,range);
            default:
                break;

        }
    }

    // activity navigation after login/logout and set the action bar
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_ITEM && resultCode == RESULT_OK){
            menuAuth.setTitle("התנתק");
            goToAddItemForm();
        }
        if (requestCode == CONNECT && resultCode == RESULT_OK){
            menuAuth.setTitle("התנתק");
        }
        if (requestCode == LOGOUT && resultCode == RESULT_OK){
            menuAuth.setTitle("התחבר");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        itemsList.UpdateItemsList();
        if(gps.isPermissionToReadGPSLocationOK())
            gps.trackLocation(wifiReceiver.getProvider());
    }

    @Override
    public void onPause() {
        super.onPause();
        gps.RemoveUpdates();
    }


    private void goToAddItemForm() {
        FirebaseUser fUser = auth.getUser();
        addItemForm.putExtra("name",fUser.getDisplayName());
        addItemForm.putExtra("uid",fUser.getUid());
        addItemForm.putExtra("email",fUser.getEmail());
        addItemForm.putExtra("phone",fUser.getPhoneNumber());
        addItemForm.putExtra("latitude",gps.getLatitude());
        addItemForm.putExtra("longitude",gps.getLongitude());
        startActivity(addItemForm);
    }

    // Action Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        String login_logout = "התחבר";
        if(auth.isLogin())
            login_logout = "התנתק";
        menuAuth = menu.add(login_logout);
        MenuItem menuAbout = menu.add("אודות");
        MenuItem menuExit = menu.add("יציאה");

        menuAuth.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                if(auth.isLogin()) {
                    AuthActivity = new Intent(MainActivity.this,Authentication.class);
                    AuthActivity.putExtra("logout",true);
                    startActivityForResult(AuthActivity,LOGOUT);
                }
                else {
                    AuthActivity = new Intent(MainActivity.this,Authentication.class);
                    AuthActivity.putExtra("login",true);
                    startActivityForResult(AuthActivity,CONNECT);

                }
                return true;
            }
        });

        menuAbout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                showAboutDialog();
                return true;
            }
        });

        menuExit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                showExitDialog();
                return true;
            }
        });
        return true;
    }


    private void showAboutDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("אודות");
        //alertDialog.setMessage(getString(R.string.about));
        alertDialog.setMessage("זירת מהסחר הגדולה למוצרי יד שנייה");
        alertDialog.show();
        //Toast.makeText(this, "ABOUT", Toast.LENGTH_SHORT).show();
    }

    private void showExitDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(R.mipmap.ic_exit);
        alertDialog.setTitle("יציאה");
        alertDialog.setMessage("אתה בטוח שאתה רוצה לצאת מהאפליקציה");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("כן", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();  // destroy this activity
            }
        });
        alertDialog.setNegativeButton("לא", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        alertDialog.show();
    }
}
