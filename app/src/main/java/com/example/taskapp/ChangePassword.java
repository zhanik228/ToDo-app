package com.example.taskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePassword extends AppCompatActivity {

    EditText edit_password, editConfirmPassword;
    Button changePasswordBtn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        firebaseAuth = FirebaseAuth.getInstance();

        edit_password = findViewById(R.id.edit_password);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String edit_passwordTxt = edit_password.getText().toString();
                String editConfimePasswordTxt = editConfirmPassword.getText().toString();

                if(edit_passwordTxt.isEmpty()) {
                    edit_password.setError("fill field");
                }
                else if(editConfimePasswordTxt.isEmpty()) {
                    editConfirmPassword.setError("fill field");
                }
                else if(!edit_passwordTxt.equals(editConfimePasswordTxt)) {
                    Toast.makeText(getApplicationContext(), "passwords do not match", Toast.LENGTH_LONG).show();
                }
                else {
                    firebaseAuth.getCurrentUser().updatePassword(edit_passwordTxt).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "succesfully changed", Toast.LENGTH_LONG).show();
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
    }
}