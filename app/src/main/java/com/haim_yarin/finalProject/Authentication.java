package com.haim_yarin.finalProject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

public  class Authentication extends AppCompatActivity {

    private FirebaseAuth Auth;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    public void onStart(){
        super.onStart();

        boolean logIn = false;
        boolean logOut = false;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("logout")) {
                logOut = true;
            }
            if (extras.containsKey("login")) {
                logIn = true;
            }
        }

        if(logOut)
            Logout();
        else if(logIn){
            if (Auth.getCurrentUser() != null) {
                setResult(RESULT_OK);
                finish();
            }
            else {
                SignInGoogle();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public Authentication(){
        Auth = FirebaseAuth.getInstance();
    }

    public boolean isLogin() {
        if (Auth.getCurrentUser() != null)
            return true;
        return false;
    }


    public void Logout() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    public void SignInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    public FirebaseAuth getAuth(){
        return this.Auth;
    }

    public FirebaseUser getUser(){
        return Auth.getCurrentUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account,requestCode);
            }

        } catch (ApiException e) {
            Log.w("error", "Google sign in failed", e);
        }
    }


    public void firebaseAuthWithGoogle(GoogleSignInAccount acct, final int requestCode) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        Auth.signInWithCredential(credential)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete (@NonNull Task <AuthResult> task) {
                        if (task.isSuccessful ()) {
                            Log.d("TAsignInWithCredentialG", "signInWithCredential:success");
                            setResult(RESULT_OK);
                            finish();
                        }
                        else {
                            Log.w ("TAsignInWithCredentialG", "signInWithCredential: failure", task.getException ());
                            finish();
                            if (task.getException () instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });


    }




}
