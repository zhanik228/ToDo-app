package com.example.taskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main2 extends AppCompatActivity {

    TextInputLayout emailSet, usernameSet;
    Button logoutBtn, changePasswordBtn, saveChanges;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        logoutBtn = findViewById(R.id.logoutBtn);
        changePasswordBtn = findViewById(R.id.changePassBtn);
        saveChanges = findViewById(R.id.saveChanges);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        emailSet = findViewById(R.id.emailSet);
        usernameSet = findViewById(R.id.userNameSet);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient gsc = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            Toast.makeText(getApplicationContext(),"google account",
                    Toast.LENGTH_LONG).show();
            emailSet.getEditText().setText(account.getDisplayName());
            usernameSet.getEditText().setText(account.getEmail());
        }
        if(firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(getApplicationContext(), "Something went wrong, user details are not available",Toast.LENGTH_LONG).show();

        }else if(firebaseAuth.getCurrentUser() != null){
            showUserProfile(firebaseUser);
        }

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameSetTxt = usernameSet.getEditText().getText().toString();
                String emailSetTxt = emailSet.getEditText().getText().toString();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered users");

                ReadAndWriteUser readAndWriteUser = new ReadAndWriteUser(emailSet.getEditText().getText().toString(), usernameSet.getEditText().getText().toString());


                databaseReference.child(firebaseUser.getUid()).setValue(readAndWriteUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(usernameSetTxt.trim())
                                .build();
                        if (task.isSuccessful())
                            firebaseUser.updateProfile(userProfileChangeRequest);
                            firebaseUser.updateEmail(emailSetTxt);

                        Toast.makeText(getApplicationContext(), "Profile updated", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ChangePassword.class));
                finish();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.today:
                        startActivity(new Intent(getApplicationContext(), Today.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.allTasks:
                        startActivity(new Intent(getApplicationContext(), All_tasks.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.completed:
                        startActivity(new Intent(getApplicationContext(), Completed.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), Main2.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

    }

    //get data from database
    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered users");
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadAndWriteUser readAndWriteUser = snapshot.getValue(ReadAndWriteUser.class);
                if(readAndWriteUser != null) {
                    String emailSetTxt = emailSet.getEditText().getText().toString().trim();
                    String userData = readAndWriteUser.username;
                    usernameSet.getEditText().setText(readAndWriteUser.username);
                    emailSet.getEditText().setText(firebaseUser.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });
    }
}