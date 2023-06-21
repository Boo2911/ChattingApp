package com.example.whatsappclone;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.whatsappclone.Models.Users;
import com.example.whatsappclone.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInCredential;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.security.AuthProvider;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;

    private FirebaseAuth auth;

    FirebaseDatabase firebaseDatabase;
    ProgressDialog progressDialog;
    GoogleSignInClient mGoogleSignInClient ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setTitle("Signing in..");
        progressDialog.setMessage("we're logging you in..");


        GoogleSignInOptions gso =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.btnGoogleSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                signIn();

            }
        });

        binding.btnFbSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignInActivity.this, "Feature not found", Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvSigninPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignInActivity.this, "Feature not found", Toast.LENGTH_SHORT).show();
            }
        });




        binding.btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.signinEmail.getText().toString().equals("")){
                    binding.signinEmail.setError("Write Email here..");
                    return ;
                }

                if(binding.signinPassword.getText().toString().equals("")){
                    binding.signinPassword.setError("Write Password here..");
                    return ;
                }

                progressDialog.show();

                auth.signInWithEmailAndPassword(binding.signinEmail.getText().toString(), binding.signinPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful()){
                                    Intent i = new Intent(SignInActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }else
                                    Toast.makeText(SignInActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        binding.signinTvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                finish();
            }
        });

        if(auth.getCurrentUser()!=null){
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }
    }



    private static final int REQ_ONE_TAP = 2;
    private void signIn(){

        Intent i = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(i, REQ_ONE_TAP);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                   GoogleSignInAccount account = task.getResult(ApiException.class);

                        Log.d("TAG", "Got ID token "+account.getId());
                        firebaseAuthWithGoogle(account.getIdToken());

                } catch (ApiException e) {
                    // ...
                    progressDialog.dismiss();
                }
                break;
        }
    }

    private void firebaseAuthWithGoogle(String idToken){
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");

                                FirebaseUser user = auth.getCurrentUser();
                                Users users = new Users();
                                users.setUserId(user.getUid());
                                users.setUsername(user.getDisplayName());
                                users.setProfilePic(user.getPhotoUrl().toString());

                                firebaseDatabase.getReference().child("Users").child(user.getUid()).setValue(users);
//                                updateUI(user);
                                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
//                                updateUI(null);
                            }
                        }
                    });
        }
    }