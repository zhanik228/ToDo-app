package com.example.taskapp.LogAndReg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskapp.Main2;
import com.example.taskapp.R;
import com.example.taskapp.ReadAndWriteUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    TextInputLayout userName, email, password, confPassword;
    TextView textViewSignin;
    FirebaseAuth firebaseAuth;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle("Register");

        textViewSignin = findViewById(R.id.textViewSignin);

        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confPassword = findViewById(R.id.confPassword);

        firebaseAuth = FirebaseAuth.getInstance();
        registerBtn = findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userNameTxt = userName.getEditText().getText().toString();
                String emailTxt = email.getEditText().getText().toString();
                String passwordTxt = password.getEditText().getText().toString();
                String confPasswordTxt = confPassword.getEditText().getText().toString();

                if(userNameTxt.isEmpty()) {
                    userName.setError("Fill field");
                    userName.requestFocus();
                }
                else if(emailTxt.isEmpty()) {
                    email.setError("fill field");
                    email.requestFocus();
                }
                else if(passwordTxt.isEmpty()) {
                    password.setError("fill field");
                    password.requestFocus();
                }
                else if(confPasswordTxt.isEmpty()) {
                    confPassword.setError("fill field");
                    confPassword.requestFocus();
                }
                else if(!passwordTxt.equals(confPasswordTxt)) {
                    Toast.makeText(getApplicationContext(), "Password not matched", Toast.LENGTH_LONG).show();
                }
                else {
                    firebaseAuth.createUserWithEmailAndPassword(emailTxt, passwordTxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                ReadAndWriteUser readAndWriteUser = new ReadAndWriteUser(emailTxt, userNameTxt);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered users");

                                databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(readAndWriteUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(userNameTxt).build();
                                        if (task.isSuccessful()) {
                                            firebaseAuth.getCurrentUser().sendEmailVerification();
                                            firebaseAuth.getCurrentUser().updateProfile(userProfileChangeRequest);
                                            Toast.makeText(getApplicationContext(), "Succesfully created", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(getApplicationContext(), Main2.class));
                                            finish();
                                        }

                                    }
                                });

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

        textViewSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }
}