package com.asimpson.tasks2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String URL = "http://127.0.0.1:5000/todo/api/v1.0/tasks";
    private ListView listTasks;
    private ArrayAdapter adapter;

    private List<TaskModel> allTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listTasks = findViewById(R.id.listview);

        allTasks = new ArrayList<>();

        loadListViewData();


    }

    private void loadListViewData() {
        ProgressDialog processDialog = new ProgressDialog(this);
        processDialog.setMessage("Loading data...");
        processDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                processDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("tasks");

                    for(int i = 0; i<array.length(); i++){
                        JSONObject o = array.getJSONObject(i);
                        TaskModel task = new TaskModel(
                                o.getInt("id"),
                                o.getString("title"),
                                o.getString("description"),
                                o.getBoolean("done")
                        );
                        allTasks.add(task);
                    }

                    adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, allTasks);
                    listTasks.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}