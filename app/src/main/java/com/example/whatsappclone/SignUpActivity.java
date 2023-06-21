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
import android.view.Window;
import android.widget.Toast;

import com.example.whatsappclone.Models.Users;
import com.example.whatsappclone.databinding.ActivitySignUpBinding;
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

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;

    private FirebaseAuth auth;

    FirebaseDatabase firebaseDatabase;

    ProgressDialog progressDialog;

    GoogleSignInClient mGoogleSignInClient ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(SignUpActivity.this);
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
                Toast.makeText(SignUpActivity.this, "Feature not found!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvSigninPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignUpActivity.this, "Feature not found!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(binding.signupUsername.getText().toString().equals("")){
                    binding.signupUsername.setError("Write your username here!");
                    return;
                }

                if(binding.signupEmail.getText().toString().equals("")){
                    binding.signupEmail.setError("Write your email here!");
                    return;
                }

                if(binding.signupPassword.getText().toString().equals("")){
                    binding.signupPassword.setError("Write your password here!");
                    return;
                }



                progressDialog.show();
                auth.createUserWithEmailAndPassword(binding.signupEmail.getText().toString(),
                        binding.signupPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful()) {

                                    Users users = new Users(binding.signupUsername.getText().toString(), binding.signupEmail.getText().toString(),binding.signupPassword.getText().toString());

                                    String id = task.getResult().getUser().getUid();

                                    firebaseDatabase.getReference().child("Users").child(id).setValue(users);


                                    Toast.makeText(SignUpActivity.this, "Signed Up Successfully", Toast.LENGTH_SHORT).show();
                                }
                                else Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                finish();
            }
        });

        binding.signupTvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(i);
                finish();
            }
        });

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
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
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