package com.asimpson.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btn_getById, btn_getByTitle, btn_getAll;
    EditText dataInput;
    ListView listTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_getById = findViewById(R.id.btn_getTaskId);
        btn_getByTitle = findViewById(R.id.btn_getTaskTitle);
        btn_getAll = findViewById(R.id.btn_getbyAll);
        dataInput = findViewById(R.id.dataInput);
        listTasks = findViewById(R.id.listview);

        final TaskService taskService = new TaskService(MainActivity.this);

        //Button Click Listeners
        btn_getById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                taskService.getTaskbyId(dataInput.getText().toString(), new TaskService.Volleyresponserlisterner() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, "Error Occurred", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(List<TaskModel> taskModel) {
                        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, taskModel);
                        listTasks.setAdapter(arrayAdapter);
                    }
                });


            }
        });

        btn_getByTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Functionlity doesn't work as yet " , Toast.LENGTH_LONG).show();
            }
        });

        btn_getAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Functionality doesn't work as yet", Toast.LENGTH_LONG).show();
            }
        });


    }
}