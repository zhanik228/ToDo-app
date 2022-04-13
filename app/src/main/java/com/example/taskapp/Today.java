package com.example.taskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Today extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setSelectedItemId(R.id.today);

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
}