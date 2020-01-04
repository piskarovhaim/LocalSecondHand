package com.haim_yarin.localsecondhand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,ListView.OnItemClickListener {

    private Intent addItemForm;
    private Intent ItemActivity;
    private Intent AuthActivity;
    private ItemsList itemsList;
    private Authentication auth;
    private MenuItem menuAuth;
    private MyNotification notification;
    private WifiReceiver wifiReceiver;



    private ImageButton btnAddItem;
    private ImageButton btnSearch;
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


        ItemActivity = new Intent(this,ItemActivity.class);

        itemsList = new ItemsList(this);
        ListView listView = findViewById(R.id.itemList);
        listView.setAdapter(itemsList);
        listView.setOnItemClickListener(this);

        auth = new Authentication();

        addItemForm = new Intent(this,AddItemFormActivity.class);

        btnAddItem = (ImageButton) findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(this);
        btnSearch = (ImageButton)findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        edSearchTitle = (EditText)findViewById(R.id.edSearchTitle);
        edSearchRange = (EditText)findViewById(R.id.edRange);



        notification = new MyNotification((NotificationManager) getSystemService(NOTIFICATION_SERVICE),this,itemsList,auth);
        notification.ListenToChange();


        gps = new Gps(this);
        wifiReceiver = new WifiReceiver(gps);
        IntentFilter intentFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        // register the receiver with the filter
        registerReceiver(wifiReceiver, intentFilter);

        AuthActivity = new Intent(this,Authentication.class);
    }

    @Override
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
            case R.id.btnAddItem:
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
            case R.id.btnSearch:
                if(!gps.isPermissionToReadGPSLocationOK())
                    break;
                String title = edSearchTitle.getText().toString();
                float range;
                try {
                    range = (float)Integer.parseInt(edSearchRange.getText().toString());
                }catch (Exception e){
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                Item item = new Item(title,"dfg","dfg","dfg","sdfg","dfg","dsfg","dfg",gps.getLatitude(),gps.getLongitude());
                itemsList.Search(item,range);
            default:
                break;

        }
    }

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
        MenuItem menuAbout = menu.add("About");
        MenuItem menuExit = menu.add("Exit");

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
        alertDialog.setTitle("About the game");
        //alertDialog.setMessage(getString(R.string.about));
        alertDialog.setMessage("aboutttttttttttttt");
        alertDialog.show();
        //Toast.makeText(this, "ABOUT", Toast.LENGTH_SHORT).show();
    }

    private void showExitDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(R.mipmap.ic_exit);
        alertDialog.setTitle("Exit game");
        alertDialog.setMessage("Do you really want to exit?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();  // destroy this activity
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        alertDialog.show();
    }
}
