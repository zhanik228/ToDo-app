package com.example.taskapp.LogAndReg;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskapp.Main2;
import com.example.taskapp.MainActivity;
import com.example.taskapp.R;
import com.example.taskapp.ReadAndWriteUser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    TextView textViewRegisterBtn;
    EditText email, password;
    FirebaseAuth aUth;
    FirebaseUser aUser;
    Button loginBtn;
    SignInButton googleBtn;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn= findViewById(R.id.loginBtn);

        googleBtn = findViewById(R.id.googleBtn);

        aUth = FirebaseAuth.getInstance();


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailTxt = email.getText().toString();
                String passwordTxt = password.getText().toString();

                if(emailTxt.isEmpty()) {
                    email.setError("fill field");
                    return;
                }
                else if(passwordTxt.isEmpty()){
                    password.setError("fill field");
                    return;
                }
                else {
                    aUth.signInWithEmailAndPassword(emailTxt, passwordTxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                if(aUth.getCurrentUser().isEmailVerified()) {
                                    Toast.makeText(getApplicationContext(), "You are logged in", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), Main2.class));
                                    finish();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Your email is not verified, see your email", Toast.LENGTH_LONG).show();
                                    aUth.getCurrentUser().sendEmailVerification();
                                    aUth.signOut();
                                    showAlertDialog();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });

        //google sign in
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);



        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = gsc.getSignInIntent();
                startActivityForResult(intent, 100);


            }
        });

        ///////////////////////////

//        TOregister
        textViewRegisterBtn = findViewById(R.id.textViewRegisterBtn);

        textViewRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(Login.this)
                .setTitle("You are not verified")
                .setMessage("check your email to verify your account")

                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                })
                .create();
        alertDialog.show();
    }

    // start main
    @Override
    protected void onStart() {
        super.onStart();
        if(aUth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), Main2.class));
            finish();
        }
    }

    //Google sign in
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);

                String emailTxt = task.getResult().getDisplayName();
                String userNameTxt = task.getResult().getEmail();

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                ReadAndWriteUser readAndWriteUser = new ReadAndWriteUser(emailTxt, userNameTxt);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered users");

                databaseReference.child(task.getResult().getId()).setValue(readAndWriteUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userNameTxt).build();
                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), "Succesfully created", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), Main2.class));
                            finish();
                        }

                    }
                });
                startActivity(new Intent(getApplicationContext(), Main2.class));
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}