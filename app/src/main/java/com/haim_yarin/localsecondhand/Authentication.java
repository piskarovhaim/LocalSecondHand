package com.haim_yarin.localsecondhand;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;

public  class Authentication {

    private FirebaseAuth Auth;
    private GoogleSignInClient mGoogleSignInClient;
    private Context context;
    private Activity activity;
    private FirebaseUser user;

    public Authentication(Context context,Activity activity){

        this.context = context;
        this.activity = activity;
        Auth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        if (Auth.getCurrentUser() != null) {
            user = Auth.getCurrentUser();
        }
    }

    public boolean isLogin(){
        if (Auth.getCurrentUser() != null)
            return true;
        return false;
    }
    public FirebaseUser getUser(){
        return user;
    }

    public void Logout() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                user = null;
            }
        });
    }

    public void SignInGoogle(int codeReq) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, codeReq);
    }

    public FirebaseAuth getAuth(){
        return this.Auth;
    }

    public void setUser(){
        user = Auth.getCurrentUser();
    }




}
