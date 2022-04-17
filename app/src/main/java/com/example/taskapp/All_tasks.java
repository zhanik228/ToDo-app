package com.example.taskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class All_tasks extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private EditText task, description;
    private EditText dateToDo;
    private Button saveBtn, cancelBtn;
    private LinearLayoutManager linearLayoutManager;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference reference;
    private String onlineUserID;

    private String key = "";
    private String taskk;
    private String descriptionn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        loader = new ProgressDialog(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ToDo App");

        recyclerView = findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Tasks").child(onlineUserID);

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });







        //bottom nav
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setSelectedItemId(R.id.allTasks);

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

    private void addTask() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_file, null);
        AlertDialog dialog = builder.create();
        dialog.setView(myView);
        dialog.show();

        cancelBtn = myView.findViewById(R.id.cancelBtn);
        saveBtn = myView.findViewById(R.id.saveBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dateToDo = myView.findViewById(R.id.dateToDo);


        dateToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog pickerDialog = new DatePickerDialog(All_tasks.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        int plusMonth = i1+1;
                        dateToDo.setText(i2 + "/" + plusMonth + "/" + i);
                    }
                },year,month,day);
                pickerDialog.show();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task = myView.findViewById(R.id.task);
                description = myView.findViewById(R.id.description);

                String mTask = task.getText().toString().trim();
                String mDescription = description.getText().toString().trim();
                String id = reference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());
                String mdateToDo = dateToDo.getText().toString().trim();

                if(TextUtils.isEmpty(mTask)) {
                    task.setError("Task required");
                    task.requestFocus();
                }
                if(TextUtils.isEmpty(mDescription)) {
                    description.setError("Description required");
                    description.requestFocus();
                }else {
                    loader.setMessage("Adding your data");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    Model model = new Model(mTask, mDescription, id, date,mdateToDo);
                    reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Added succesfully", Toast.LENGTH_LONG).show();
                                loader.dismiss();
                            }else {
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                loader.dismiss();
                            }
                        }
                    });
                }
                dialog.dismiss();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(reference, Model.class)
                .build();

        FirebaseRecyclerAdapter<Model, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Model, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Model model) {
                holder.setDate(model.getDate());
                holder.setTask(model.getTask());
                holder.setDesc(model.getDescription());
                holder.setPlanToDo(model.getPlanToDate());

                holder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        key = getRef(position).getKey();
                        taskk = model.getTask();
                        descriptionn = model.getDescription();
                        updateTask();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieved_layout, parent, false);
                return new MyViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View myView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myView = itemView;
        }

        public void setTask(String task) {
            TextView taskTextView = myView.findViewById(R.id.taskTv);
            taskTextView.setText(task);
        }

        public void setDesc(String desc) {
            TextView descTextView = myView.findViewById(R.id.descriptionTv);
            descTextView.setText(desc);
        }

        public void setDate(String date) {
            TextView dateTextView = myView.findViewById(R.id.dateTv);
            dateTextView.setText(date);
        }

        public void setPlanToDo(String planToDo) {
            TextView planTextView = myView.findViewById(R.id.datetoDoTv);
            planTextView.setText(planToDo);
        }
    }

    private void updateTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(All_tasks.this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_data, null);
        myDialog.setView(view);
        AlertDialog dialog = myDialog.create();

        EditText mTask = view.findViewById(R.id.mEditTextTask);
        EditText mDescription = view.findViewById(R.id.mEditTextDescription);
        EditText mDateToDo = view.findViewById(R.id.mEditTextDateToDo);


        mTask.setText(taskk);
        mTask.setSelection(taskk.length());
        mDescription.setText(descriptionn);
        mDescription.setSelection(descriptionn.length());

        mDateToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                DatePickerDialog pickerDialog = new DatePickerDialog(All_tasks.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        mDateToDo.setText(i2 + "/" + i1 + "/" + i);
                    }
                },year,month,day);
                pickerDialog.show();
            }
        });

        Button delButton = view.findViewById(R.id.btnDelete);
        Button updButton = view.findViewById(R.id.btnUpdate);

        updButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskk = mTask.getText().toString().trim();
                descriptionn = mDescription.getText().toString().trim();
                String dateToDo = mDateToDo.getText().toString().trim();

                String date = DateFormat.getDateInstance().format(new Date());

                Model model = new Model(taskk,descriptionn, key, date, dateToDo);
                reference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Data updated successfully"
                            ,Toast.LENGTH_LONG).show();
                        }else {
                            String error = task.getException().toString();
                            Toast.makeText(getApplicationContext(), "update failed" + error, Toast.LENGTH_LONG).show();
                        }
                    }
                });

                dialog.dismiss();
            }
        });

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Task deleted successfully"
                            , Toast.LENGTH_LONG).show();
                        }else {
                            String error = task.getException().toString();
                            Toast.makeText(getApplicationContext(), "Failed to delete" + error,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

                dialog.dismiss();

            }
        });

        dialog.show();
    }

}