package com.haim_yarin.localsecondhand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

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

public class MainActivity extends AppCompatActivity {

    private Intent addItemForm;
    private Intent ItemActivity;
    private Intent Login;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ItemsList itemsList;
    private Authentication auth;
    private MenuItem menuAuth;


    private ImageButton btnAddItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ItemActivity = new Intent(this,ItemActivity.class);
        itemsList = new ItemsList(this);
        ListView listView = findViewById(R.id.itemList);
        listView.setAdapter(itemsList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
        });

        Login = new Intent(this,LoginActivity.class);
        //startActivity(Login);

        auth = new Authentication(this,MainActivity.this);
        //auth.Logout();


        addItemForm = new Intent(this,AddItemFormActivity.class);
        btnAddItem = (ImageButton) findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!auth.isLogin())
                    auth.SignInGoogle(getResources().getInteger(R.integer.sign_in_add_item));
                else
                    goToAddItemForm();

            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        itemsList.UpdateItemsList();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account,requestCode);
                    menuAuth.setTitle("התנתק");
                }

            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
            }

    }

    private void goToAddItemForm() {
        FirebaseUser fUser = auth.getUser();
        addItemForm.putExtra("name",fUser.getDisplayName());
        addItemForm.putExtra("uid",fUser.getUid());
        addItemForm.putExtra("email",fUser.getEmail());
        addItemForm.putExtra("phone",fUser.getPhoneNumber());
        startActivity(addItemForm);
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct, final int requestCode) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.getAuth().signInWithCredential(credential)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete (@NonNull Task <AuthResult> task) {
                        if (task.isSuccessful ()) {
                            if (task.isSuccessful()) {
                                Log.d("TAsignInWithCredentialG", "signInWithCredential:success");
                                auth.setUser();
                                if (requestCode == getResources().getInteger(R.integer.sign_in_add_item)) {
                                    goToAddItemForm();
                                }
                            } else {

                                Log.w("TAG", "signInWithCredential:failure", task.getException());
                            }
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w ("TAG", "signInWithCredential: failure", task.getException ());
                            if (task.getException () instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });


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
                    auth.Logout();
                    menuAuth.setTitle("התחבר");
                }
                else {
                    auth.SignInGoogle(getResources().getInteger(R.integer.sign_in_from_menu));
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
